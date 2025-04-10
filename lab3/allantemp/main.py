# Import necessary libraries
import pandas as pd
import numpy as np
import matplotlib.pyplot as plt
import seaborn as sns
from collections import Counter
import re
import nltk
from nltk.corpus import stopwords
from nltk.tokenize import word_tokenize
from sklearn.model_selection import train_test_split
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.linear_model import LogisticRegression
from sklearn.svm import SVC
from sklearn.metrics import accuracy_score, classification_report
import warnings
warnings.filterwarnings('ignore')

# Download required NLTK data
nltk.download('punkt')
nltk.download('punkt_tab')
nltk.download('stopwords')
nltk.download('wordnet')

# Read the dataset
df = pd.read_csv('./Dataset-SA.csv')

# Text preprocessing function
def preprocess_text(text):
    # Convert to lowercase
    text = str(text).lower()
    # Remove special characters and numbers
    text = re.sub(r'[^a-zA-Z\s]', '', text)
    # Tokenization
    tokens = word_tokenize(text)
    # Remove stopwords
    stop_words = set(stopwords.words('english'))
    tokens = [word for word in tokens if word not in stop_words]
    # Join tokens back to text
    return ' '.join(tokens)

# Preprocess the Review column
df['processed_review'] = df['Review'].apply(preprocess_text)

# Prepare data for ML models
X = df['processed_review']
y = df['Sentiment']

# Split the data
X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.2, random_state=42)

# TF-IDF Vectorization
tfidf = TfidfVectorizer(max_features=5000)
X_train_tfidf = tfidf.fit_transform(X_train)
X_test_tfidf = tfidf.transform(X_test)

# Train and evaluate Logistic Regression
lr_model = LogisticRegression(random_state=42, max_iter=1000)
lr_model.fit(X_train_tfidf, y_train)
lr_pred = lr_model.predict(X_test_tfidf)
lr_accuracy = accuracy_score(y_test, lr_pred)

# Train and evaluate SVM
svm_model = SVC(kernel='linear', random_state=42)
svm_model.fit(X_train_tfidf, y_train)
svm_pred = svm_model.predict(X_test_tfidf)
svm_accuracy = accuracy_score(y_test, svm_pred)

print("Logistic Regression Accuracy:", lr_accuracy)
print("SVM Accuracy:", svm_accuracy)
print("\nLogistic Regression Classification Report:")
print(classification_report(y_test, lr_pred))
print("\nSVM Classification Report:")
print(classification_report(y_test, svm_pred))

# Zipf's Law Analysis
def analyze_zipf_law(text_series):
    # Combine all text
    all_text = ' '.join(text_series)
    # Tokenize
    words = word_tokenize(all_text.lower())
    # Count word frequencies
    word_freq = Counter(words)
    # Sort by frequency
    sorted_freq = sorted(word_freq.items(), key=lambda x: x[1], reverse=True)
    
    # Create frequency distribution table
    ranks = range(1, len(sorted_freq) + 1)
    words, freqs = zip(*sorted_freq)
    
    # Calculate P x R (Product of frequency and rank)
    pr_values = [rank * freq for rank, freq in zip(ranks, freqs)]
    
    return ranks, freqs, pr_values, sorted_freq[:20]

# Perform Zipf's Law analysis
ranks, frequencies, pr_values, top_words = analyze_zipf_law(df['processed_review'])

# Plot Zipf's Law
plt.figure(figsize=(12, 6))
plt.loglog(ranks, frequencies, 'b.')
plt.xlabel('Rank (log scale)')
plt.ylabel('Frequency (log scale)')
plt.title("Zipf's Law: Word Frequency vs Rank")
plt.grid(True)
plt.show()

# Display top 20 words and their frequencies
print("\nTop 20 Words and Their Frequencies:")
print("Rank\tWord\tFrequency\tP x R")
for i, (word, freq) in enumerate(top_words, 1):
    print(f"{i}\t{word}\t{freq}\t\t{i*freq}")

# Calculate average P x R value
avg_pr = np.mean(pr_values)
print(f"\nAverage P x R value: {avg_pr:.2f}")

# Conclusion
print("\nConclusion:")
print("1. Model Performance:")
print(f"   - Logistic Regression Accuracy: {lr_accuracy:.4f}")
print(f"   - SVM Accuracy: {svm_accuracy:.4f}")
print("2. Zipf's Law Analysis:")
print(f"   - The average P x R value is {avg_pr:.2f}")
print("   - The word frequency distribution follows Zipf's law as shown in the log-log plot")