import os
import joblib
from preprocessing2 import preprocess_csv

SOURCE_MAP = {"A": "YouTube", "B": "Dailymotion", "C": "Both Streams"}

def main():
    file_path = (r"C:\Users\melka\Desktop\measurement zip\cyber\cyber\PythonProject1\Tesfile\1-000A.csv"
                 )
    model_path = r"C:\Users\melka\Desktop\measurement zip\cyber\cyber\PythonProject1\model\stream_classifier.pkl"

    # Check CSV file existence
    if not os.path.isfile(file_path):
        print(f"Error: File not found at path: {file_path}")
        return

    # Check model file existence
    if not os.path.isfile(model_path):
        print(f"Error: Model file not found at path: {model_path}")
        return

    try:
        features = preprocess_csv(file_path)
    except Exception as e:
        print(f"Error processing file: {e}")
        return

    try:
        clf = joblib.load(model_path)
    except Exception as e:
        print(f"Error loading model: {repr(e)}")  # Use repr() for detailed error
        return

    try:
        label = clf.predict([features])[0]
        predicted_source = SOURCE_MAP.get(label, "Unknown")
        print(f"\n✅ Predicted Source: {predicted_source}")
        print(f"📊 Feature Vector: {features}")
    except Exception as e:
        print(f"Error during prediction: {e}")

if __name__ == "__main__":
    main()
