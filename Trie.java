import java.io.File;
import java.util.*;
public class Trie{

    private Node root;
    public Trie(){
        root = new Node();
    }

    public void insert(String words){

        if(words == null)
            return; //Not a real word. Just return

        words = words.toLowerCase().replaceAll("[^a-z]", "");
        if(words.length() == 0)  // makes sure that things that are just characters do not count as words
            return;
       
        root.passCount++; // this will be the # of 'words' added
        Node curr = root;

        for(char c: words.toCharArray()){
            Node child = curr.children.get(c); //Try to get the node assosciate with  char \
            if (child == null){
                child = new Node(); // there was no child so I need to make a new node
                curr.children.put(c,child); // put new child in map
            }
            curr.passCount++; // add to total passes
            curr = child; // go one level deeper in tree
        }
        curr.endCount++;
    }

    public boolean contains(String word){
        if(word == null)
            return false; //Not a real word. Just return

        word = word.toLowerCase().replaceAll("[^a-z]", "");
        if(word.length() == 0)
            return false;
        Node curr = root;

        for(char c: word.toCharArray()){
            Node child = curr.children.get(c);

            if(child == null ){
                return false;
            }
            curr = child;
        }
        return curr != null && curr.isEndOfWord();
    }

    public char mostLikelyNextChar(String prefix) {
        if (prefix == null) {
            return '_';
        }
        prefix = prefix.toLowerCase().replaceAll("[^a-z]", "");

        Node curr = root;

        // traverse down the prefix
        for (char c : prefix.toCharArray()) {
            Node child = curr.children.get(c); //navigate through prefix children
            if (child == null) {
                return '_'; //invalid prefix. no next word so return ''
            }
            curr = child;
        }

        if (curr.children.isEmpty()) {
            return '_';
        }

        char bestChar = '_';
        long bestCount = -1;

        for (Map.Entry<Character, Node> entry : curr.children.entrySet()) {
            char nextChar = entry.getKey();
            Node childNode = entry.getValue();

            if (childNode.passCount > bestCount) {
                bestCount = childNode.passCount;
                bestChar = nextChar;
            } else if (childNode.passCount == bestCount && nextChar < bestChar) {
                bestChar = nextChar;
            }
        }
         return bestChar;
    }

    public String mostLikelyNextWord(String prefix) {
           if(prefix == null) return "";

           prefix = prefix.toLowerCase().replaceAll("[^a-z]", "");

           if(prefix.length() == 0) return "";
           Node curr = root;
           for(char ch : prefix.toCharArray()){
              curr = curr.children.get(ch);
              if(curr == null)
                return "";
           }

        Map<String, Long> wordFreqMap = new HashMap<>(); //store all words under prefix and counts
        collectWordCounts(curr, new StringBuilder(prefix), wordFreqMap); //recursive

        String bestWord = "";
        long maxFreq = -1;

        for (String word : wordFreqMap.keySet()) {
            long freq = wordFreqMap.get(word);
            if (freq > maxFreq) {
                maxFreq = freq;
                bestWord = word;
            } else if (freq == maxFreq) {
                // tie
                if (word.length() < bestWord.length()) {
                    bestWord = word;
                } else if (word.length() == bestWord.length() && word.compareTo(bestWord) < 0) {
                    bestWord = word;
                }
            }

        }
        return bestWord;
    }


    private void collectWordCounts(Node curr, StringBuilder word, Map<String, Long> wordFreqMap){
          if(curr.endCount > 0){
              wordFreqMap.put(word.toString(), curr.endCount);
           }
           for(Map.Entry<Character, Node> entry : curr.children.entrySet()){
               word.append(entry.getKey());
               collectWordCounts(entry.getValue(), word, wordFreqMap);
               word.deleteCharAt(word.length() - 1);
           }
      }

    public void printWordFrequencies(){
        Map<String, Long> wordFreqMap = new HashMap<>();
        collectWordCounts(root, new StringBuilder(), wordFreqMap);

        for(String word : wordFreqMap.keySet()){
            System.out.println(word + ": " + wordFreqMap.get(word));
        }
    }


    /**
     * Returns a String containing the top N most likely next chars
     * after the given prefix, along with their percent likelihood.
     *
     * @param pre the prefix to search for
     * @param N the number of top characters to return
     * @return a formatted String of the top N characters and percent
     */
    public String topNLikelyCharsPercent(String pre, int N) {

        if(pre == null) return "";

        Node curr = root;
        for(char c : pre.toCharArray()){
            Node child = curr.children.get(c);
            if(child == null) return "";
            curr = child;
        }

        long total = 0;
        for(Node child : curr.children.values()){
            total += child.passCount;
        }

        if(total == 0) return "";

        String result = "";
        for(Map.Entry<Character, Node> entry : curr.children.entrySet()){
            char nextChar = entry.getKey();
            long percent = (entry.getValue().passCount * 100)/ total;
            if(!result.isEmpty()) result += ", ";
            result += nextChar + "(" + percent + "%)";
        }

        return result;
    }

    /**
     * Returns a String containing the top N most likely next word
     * after the given prefix, along with their percent likelihood.
     *
     * @param pre the prefix to search for
     * @param N the number of top words to return
     * @return a formatted String of the top N words and percent
     */
    public String topNLikelyWordPercent(String pre, int N) {
        if(pre == null || pre.length() == 0) return "";

        Node curr = root;
        for(char c : pre.toCharArray()){
            curr = curr.children.get(c);
            if(curr == null) return "";
        }

        Map<String, Long> wordFreqMap = new HashMap<>();
        collectWordCounts(curr, new StringBuilder(pre), wordFreqMap);

        long total = 0;
        for(long freq : wordFreqMap.values()){
            total += freq;
        }

        if(total == 0) return "";

        String result = "";
        for(String word : wordFreqMap.keySet()){
            long percent = (wordFreqMap.get(word) * 100) / total;
            if(!result.isEmpty()) result += ", ";
            result += word + "(" + percent + "%)";
        }

        return result;
    }

    //guess the lyric game
   
    public void playGuessNextWordGame(String prefix){
     
        if(prefix == null || prefix.isEmpty()){
            System.out.println("Prefix can't be empty!");
            return;
        }
     
        Scanner scanner = new Scanner(System.in);
        prefix = prefix.toLowerCase().replaceAll("[^a-z ]", "").trim();
     
        //starting to traverse
        Node curr = root;
        for(char c : prefix.toCharArray()){
            if(c == ' ') continue; //this will skip spaces
            curr = curr.children.get(c);
            if(curr == null){
                System.out.println("Yikes! Prefix not found in data set.");
                return;
            }
        }
     
        //get all freqs
        Map<String, Long> wordFreqMap = new HashMap<>();
        collectWordCounts(curr, new StringBuilder(prefix), wordFreqMap);
     
        String numberOneWord = "";
        long maxFreq = -1;
     
        for(String word : wordFreqMap.keySet()){
            if(word.equals(prefix)) continue; //skips prefix
            long freq = wordFreqMap.get(word);
            if(freq > maxFreq){
                maxFreq = freq;
                numberOneWord = word;
            }
        }
     
        if(numberOneWord.isEmpty()){
            System.out.println("No more for this prefix");
            return;
        }
     
        //guessing stuff
        System.out.println("Hi! Finish the lyric! What comes after: \"" + prefix + "\" ?");
        String playerGuess = scanner.nextLine().toLowerCase().replaceAll("[^a-z]", "");
     
        //comparing stuff
        String predictedNextWord = numberOneWord.replace(prefix, "").trim();
        if(playerGuess.equals(predictedNextWord)){
            System.out.println("Nice, you are correct! The next word is \"" + predictedNextWord + "\"");
        } else {
            System.out.println("Oh no, you are incorrect! The most likely next word was \"" + predictedNextWord + "\"");
        }
     
    }

    /**
    * Clears the data
    *
    */
    public void resetData() {
        root = new Node();
    }


    public static void main(String[] args) {
            Trie trie = new Trie();

            // Load lyrics data
            try {
    Scanner sc = new Scanner(new File("C:\\Users\\itsha\\OneDrive\\Desktop\\PWA\\SOUR.txt"));
    while(sc.hasNext()){
        trie.insert(sc.next());
    }
    sc.close();
    System.out.println("Lyrics loaded!");
} catch(Exception e){
    System.out.println("File not found!");
}

            // Original data
            String data = "apple banana apple apple and and and any any cat dog dog any any apple any banana any";
            System.out.println("Inserted words:");
            System.out.println(data + "\n");

            // Insert words
            for (String word : data.split(" ")) {
                trie.insert(word);
            }

            // Simple contains checks
            System.out.println("contains(\"cat\")  --> " + trie.contains("cat"));
            System.out.println("contains(\"cats\") --> " + trie.contains("cats"));
            System.out.println("contains(\"dog\")  --> " + trie.contains("dog"));
            System.out.println("contains(\"dogs\") --> " + trie.contains("dogs"));
            System.out.println("contains(\"door\") --> " + trie.contains("door"));

            System.out.println();

            // Partial / false cases
            System.out.println("contains(\"ca\")   --> " + trie.contains("ca"));
            System.out.println("contains(\"do\")   --> " + trie.contains("do"));
            System.out.println("contains(\"doo\")  --> " + trie.contains("doo"));
            System.out.println("contains(\"catz\") --> " + trie.contains("catz"));

            //doc tests
            System.out.println("---- contains ----");
            System.out.println("contains(\"apple\") --> " + trie.contains("apple"));
            System.out.println("contains(\"banana\") --> " + trie.contains("banana"));
            System.out.println("contains(\"ban\") --> " + trie.contains("ban"));
            System.out.println("contains(\"zebra\") --> " + trie.contains("zebra"));

            System.out.println("\n---- mostLikelyNextChar ----");
            System.out.println("mostLikelyNextChar(\"a\") --> " + trie.mostLikelyNextChar("a"));
            System.out.println("mostLikelyNextChar(\"ap\") --> " + trie.mostLikelyNextChar("ap"));
            System.out.println("mostLikelyNextChar(\"do\") --> " + trie.mostLikelyNextChar("do"));
            System.out.println("mostLikelyNextChar(\"x\") --> " + trie.mostLikelyNextChar("x"));
    }



    /* NODE -- Inner class */
    class Node{
        long passCount; //total # times node is traversed
        long endCount; // # of tims end of word (i.e count of words terminating here)
        Map<Character,Node> children;

        Node(){
            children = new HashMap<Character,Node>();
            passCount = endCount = 0;
        }

        boolean isEndOfWord(){
            return endCount > 0;
        }

        @Override
        public String toString(){
            return "(pass =" + passCount + ", end ="+endCount + ")";
        }
    }
}