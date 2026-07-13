SOUR Suggestions — Predictive Writing Assistant
A predictive text engine that analyzes Olivia Rodrigo's SOUR album lyrics using a Trie (prefix tree) data structure.

What It Does
Type a prefix and the system predicts the most likely next character and next word based on frequency analysis — similar to how autocomplete works on your phone. Also includes an interactive "Guess the Lyric" game.

Features
Next Character Prediction — Given a prefix, predicts the most likely next character based on frequency
Next Word Prediction — Predicts the most likely complete word from a prefix
Top N Predictions with Percentages — Shows multiple predictions ranked by likelihood with percentage breakdowns
Guess the Lyric Game — Interactive game where you try to predict what word comes next in the lyrics
Word Frequency Analysis — Prints all words and their occurrence counts from the dataset
Efficient Lookups — All operations run in O(L) time where L is the word length


Technologies
Java
Trie Data Structure
HashMap
File I/O
Frequency Analysis
Recursion


How to Run
bashjavac Trie.java
java Trie

Make sure SOUR.txt (the lyrics data file) is in the same directory, and update the file path in main() to match your system.

Author
Harshanna Prasanna
