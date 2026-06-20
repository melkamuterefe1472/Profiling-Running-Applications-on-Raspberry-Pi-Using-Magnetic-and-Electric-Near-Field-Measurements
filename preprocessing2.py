import numpy as np
import pandas as pd
from scipy.signal import butter, filtfilt, welch
import chardet

# === Configuration ===
sampling_rate = 10000  # Hz
cutoff_frequency = 40  # Hz
filter_order = 7
signal_col = "Channel A"


def butter_lowpass_filter(data, cutoff=cutoff_frequency, fs=sampling_rate, order=filter_order):
    """
    Apply a Butterworth low-pass filter to the input signal.

    Parameters:
        data (np.array): Input signal array.
        cutoff (float): Cutoff frequency in Hz.
        fs (float): Sampling frequency in Hz.
        order (int): Filter order.

    Returns:
        np.array: Filtered signal.

    Raises:
        ValueError: If the input signal is too short for filtering.
    """
    nyquist = 0.5 * fs
    normal_cutoff = cutoff / nyquist
    b, a = butter(order, normal_cutoff, btype='low', analog=False)
    if len(data) <= max(len(a), len(b)) * 3:
        raise ValueError("Input signal too short for filtering.")
    return filtfilt(b, a, data)


def extract_features(signal, fs=sampling_rate):
    """
    Extract spectral features from the signal using Welch's method.

    Parameters:
        signal (np.array): Input filtered signal.
        fs (float): Sampling frequency in Hz.

    Returns:
        list: [avg_power, peak_freq, total_power, centroid]
    """
    freqs, psd = welch(signal, fs=fs)
    total_power = np.sum(psd)
    avg_power = np.mean(psd)
    peak_freq = freqs[np.argmax(psd)]
    centroid = np.sum(freqs * psd) / total_power
    return [avg_power, peak_freq, total_power, centroid]


def preprocess_csv(file_path):
    """
    Load CSV, detect encoding, validate, and extract features from the signal column.

    Parameters:
        file_path (str): Path to the CSV file.

    Returns:
        list: Extracted features.

    Raises:
        FileNotFoundError: If CSV file not found.
        ValueError: If signal column missing or signal too short.
    """
    # Detect file encoding automatically
    with open(file_path, 'rb') as f:
        raw_data = f.read()
        detected_encoding = chardet.detect(raw_data)['encoding']

    # Load CSV with detected encoding
    df = pd.read_csv(file_path, low_memory=False, encoding=detected_encoding)

    if signal_col not in df.columns:
        raise ValueError(f"Missing '{signal_col}' in {file_path}")

    # Convert to numeric and drop NaNs
    signal = pd.to_numeric(df[signal_col], errors='coerce').dropna().values

    if len(signal) < 100:
        raise ValueError("Signal too short.")

    filtered = butter_lowpass_filter(signal)
    features = extract_features(filtered)
    return features


# Example test when running this file directly
if __name__ == "__main__":
    test_file = r"C:\Users\melka\Desktop\measurement zip\cyber\cyber\PythonProject1\Tesfile\1-000A2.csv"
    print("Loading file from:", test_file)
    try:
        feats = preprocess_csv(test_file)
        print("Extracted features:", feats)
    except Exception as e:
        print(f"Error: {e}")

