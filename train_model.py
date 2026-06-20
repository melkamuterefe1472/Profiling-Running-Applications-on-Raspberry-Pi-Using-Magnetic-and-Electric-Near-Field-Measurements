import os
import numpy as np
import joblib
from sklearn.ensemble import RandomForestClassifier
from sklearn.model_selection import train_test_split
from sklearn.metrics import classification_report, confusion_matrix
import matplotlib.pyplot as plt
import seaborn as sns

from preprocessing2 import preprocess_csv

# === Folder Labels ===
data_folders = {
    "A": r"C:\Users\melka\Desktop\measurement zip\cyber\cyber\PythonProject1\Youtube",
    "B": r"C:\Users\melka\Desktop\measurement zip\cyber\cyber\PythonProject1\dailymotion",
    "C": r"C:\Users\melka\Desktop\measurement zip\cyber\cyber\PythonProject1\both streams"
}

X = []
y = []

for label, folder_path in data_folders.items():
    for file in os.listdir(folder_path):
        if not file.endswith(".csv"):
            continue
        try:
            filepath = os.path.join(folder_path, file)
            features = preprocess_csv(filepath)
            X.append(features)
            y.append(label)
        except Exception as e:
            print(f"Error: {file} - {e}")

if not X:
    print("No data found!")
    exit()

# === Train and Save Model ===
X = np.array(X)
y = np.array(y)

X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.2, random_state=42)
clf = RandomForestClassifier(n_estimators=100, random_state=42)
clf.fit(X_train, y_train)

print("\n=== Classification Report ===")
print(classification_report(y_test, clf.predict(X_test)))

# Confusion Matrix
cm = confusion_matrix(y_test, clf.predict(X_test), labels=["A", "B", "C"])
sns.heatmap(cm, annot=True, fmt="d", cmap="Blues", xticklabels=["A", "B", "C"], yticklabels=["A", "B", "C"])
plt.title("Confusion Matrix")
plt.xlabel("Predicted")
plt.ylabel("True")
plt.tight_layout()
plt.show()

# Ensure model directory exists
os.makedirs("model", exist_ok=True)

# Save model inside the model folder
model_path = "model/stream_classifier.pkl"
joblib.dump(clf, model_path)
print(f"Model saved as {model_path}")
