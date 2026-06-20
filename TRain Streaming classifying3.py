import os
import numpy as np
import pandas as pd
from scipy.signal import butter, filtfilt
from sklearn.model_selection import train_test_split
from sklearn.ensemble import RandomForestClassifier
from sklearn.metrics import classification_report, confusion_matrix
import matplotlib.pyplot as plt

# Configuration
folders = {
    "A": r"C:\Users\melka\Desktop\measurement zip\cyber\cyber\PythonProject1\Youtube",
    "B": r"C:\Users\melka\Desktop\measurement zip\cyber\cyber\PythonProject1\dailymotion",
    "C": r"C:\Users\melka\Desktop\measurement zip\cyber\cyber\PythonProject1\both streams"
}

signal_col = "Channel A"
sampling_rate = 10000  # Hz
cutoff_frequency = 40  # Hz
filter_order = 5
samples_per_signal = 2000

def butter_lowpass_filter(data, cutoff, fs, order=5):
    nyq = 0.5 * fs
    normal_cutoff = cutoff / nyq
    b, a = butter(order, normal_cutoff, btype='low', analog=False)
    if len(data) < max(len(a), len(b)) * 3:
        return None
    return filtfilt(b, a, data)

def extract_time_domain_features(signal):
    rms = np.sqrt(np.mean(signal ** 2))
    mean_val = np.mean(signal)
    peak = np.max(np.abs(signal))
    std_dev = np.std(signal)

    return [rms, mean_val, peak, std_dev]

# Load and extract features
X, y = [], []

for label, folder_path in folders.items():
    for filename in os.listdir(folder_path):
        if not filename.endswith(".csv"):
            continue
        path = os.path.join(folder_path, filename)
        try:
            df = pd.read_csv(path)
            if signal_col not in df.columns:
                continue
            signal = pd.to_numeric(df[signal_col], errors="coerce").dropna().values
            signal = butter_lowpass_filter(signal, cutoff_frequency, sampling_rate, filter_order)
            if signal is None or len(signal) < samples_per_signal:
                continue
            segment = signal[:samples_per_signal]
            features = extract_time_domain_features(segment)
            X.append(features)
            y.append(label)
        except Exception as e:
            print(f"Error in {filename}: {e}")

# Convert to numpy arrays
X = np.array(X)
y = np.array(y)

# Train/Test split
X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.2, random_state=42)

# Train classifier
clf = RandomForestClassifier(n_estimators=100, random_state=42)
clf.fit(X_train, y_train)

# Predict
y_pred = clf.predict(X_test)

# Evaluation
print("Classification Report:")
print(classification_report(y_test, y_pred))
print("Confusion Matrix:")
print(confusion_matrix(y_test, y_pred))

# Feature importance plot
plt.bar(range(len(clf.feature_importances_)), clf.feature_importances_)
plt.xticks(
    range(len(clf.feature_importances_)),
    ['RMS', 'Mean', 'Peak', 'StdDev'],
    rotation=45
)
plt.title("Time-Domain Feature Importances")
plt.tight_layout()
plt.show()
