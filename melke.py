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
    if len(data) <= max(len(a), len(b)) * 3:
        raise ValueError("Input signal too short for filtering.")
    return filtfilt(b, a, data)

# === STORE AVERAGE FILTERED SIGNALS ===
averages = {}

# === PROCESS EACH FOLDER ===
for label, data_folder in folders.items():
    all_signals = []
    csv_files = [f for f in os.listdir(data_folder) if f.endswith(".csv")]

    print(f"\nProcessing folder: {label} ({len(csv_files)} files)")

    for file_name in csv_files:
        file_path = os.path.join(data_folder, file_name)

        try:
            df = pd.read_csv(file_path, low_memory=False)

            if signal_col not in df.columns:
                print(f"'{signal_col}' not in {file_name}")
                continue

            df[signal_col] = pd.to_numeric(df[signal_col], errors="coerce")
            df[signal_col] = (df[signal_col] / adc_max) * v_ref

            signal = df[signal_col].dropna().values
            if len(signal) < plot_samples:
                continue

            raw = signal[:plot_samples]
            filtered = butter_lowpass_filter(raw, cutoff_frequency, sampling_rate, filter_order)

            all_signals.append(filtered)

        except Exception as e:
            print(f"Error reading {file_name}: {e}")

    if all_signals:
        combined = np.vstack(all_signals)
        avg_signal = np.mean(combined, axis=0)
        averages[label] = avg_signal

        # === INDIVIDUAL PLOT FOR THIS SCENARIO ===
        time = np.arange(plot_samples) / sampling_rate
        plt.figure(figsize=(10, 4))
        plt.plot(time, avg_signal, label=f"{label} (Avg Filtered)", color='blue')
        plt.title(f"Average Filtered Signal - {label}")
        plt.xlabel("Time (s)")
        plt.ylabel("Voltage (V)")
        plt.grid(True)
        plt.legend()
        plt.tight_layout()
        plt.show()

    else:
        print(f"No valid signals found in {label}")

# === FINAL COMBINED PLOT ===
if averages:
    time = np.arange(plot_samples) / sampling_rate
    plt.figure(figsize=(12, 6))
    colors = ['red', 'green', 'blue']

    for (label, avg_signal), color in zip(averages.items(), colors):
        plt.plot(time, avg_signal, label=f"{label} (Avg Filtered)", color=color)

    plt.xlabel("Time (s)")
    plt.ylabel("Voltage (V)")
    plt.title("Combined Average Filtered Signals")
    plt.legend()
    plt.grid(True)
    plt.tight_layout()
    plt.show()
else:
    print("No average signals available to plot.")
