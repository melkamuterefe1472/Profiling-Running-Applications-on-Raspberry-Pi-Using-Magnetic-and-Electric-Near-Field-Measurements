import os
import pandas as pd
import numpy as np
import matplotlib.pyplot as plt
from scipy.signal import butter, filtfilt

# === CONFIGURATION ===
folders = {
    "YouTube": r"C:\Users\melka\Desktop\measurement zip\cyber\cyber\PythonProject1\youtube",
    "Dailymotion": r"C:\Users\melka\Desktop\measurement zip\cyber\cyber\PythonProject1\dailymotion",
    "Both Streams": r"C:\Users\melka\Desktop\measurement zip\cyber\cyber\PythonProject1\both streams"
}

sampling_rate = 10000  # Hz
cutoff_frequency = 40  # Hz
filter_order = 7
signal_col = "Channel A"
plot_samples = 2000
adc_max = 4095
v_ref = 3.3

def butter_lowpass_filter(data, cutoff, fs, order=5):
    nyquist = 0.5 * fs
    normal_cutoff = cutoff / nyquist
    b, a = butter(order, normal_cutoff, btype='low', analog=False)
    return filtfilt(b, a, data)

averages = {}

for label, folder_path in folders.items():
    all_signals = []
    files = [f for f in os.listdir(folder_path) if f.endswith(".csv")]
    for file in files:
        path = os.path.join(folder_path, file)
        df = pd.read_csv(path, low_memory=False)
        if "Channel A" not in df.columns:
            continue
        df["Channel A"] = pd.to_numeric(df["Channel A"], errors="coerce")
        df["Channel A"] = (df["Channel A"] / adc_max) * v_ref
        signal = df["Channel A"].dropna().values
        if len(signal) < plot_samples:
            continue
        filtered = butter_lowpass_filter(signal[:plot_samples], cutoff_frequency, sampling_rate, filter_order)
        all_signals.append(filtered)

    if all_signals:
        combined = np.vstack(all_signals)
        avg_signal = np.mean(combined, axis=0)
        averages[label] = avg_signal

# Plot combined graph
if averages:
    time = np.arange(plot_samples) / sampling_rate
    plt.figure(figsize=(12, 6))
    for label, avg in averages.items():
        plt.plot(time, avg, label=f"{label} (Avg Filtered)")
    plt.xlabel("Time (s)")
    plt.ylabel("Voltage (V)")
    plt.title("Average Filtered Signals Across All Scenarios")
    plt.legend()
    plt.grid(True)
    plt.tight_layout()
    plt.show()
