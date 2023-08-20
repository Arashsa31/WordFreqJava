package Program5;

/*
 * An n-gram is a sequence of n items (numbers, characters, words) from some source.

For example, the phrase “scoo be do be do be” contains the word-based 2-grams “scoo be” (once), “be do” (twice), and “do be” (twice).

N-grams are used by computational linguists, biologists, and data compression experts in a multitude of ways. They can be especially useful in prediction models, where you have the start of a sequence of items and want to predict what is next—for example, when you text on your phone it might predict what word you want next as a shortcut.

Write a program that:

    Asks the user for a value of n and an associated minimum n-gram frequency, then tracks the frequency of n-grams from the input text, for that n.
    After reporting the results for the n-grams it allows the user repeatedly to enter a group of n words and it reports back the top three most likely words that follow that group, along with the percentage of time within the text that group of words is followed by the reported word.

 */

import java.io.*;
import java.net.*;
import java.text.DecimalFormat;
import java.util.*;

public class Program5 {
	public static void main(String[] args) throws IOException {
		Scanner keyboard = new Scanner(System.in);
		URL url = new URL("https://www.gutenberg.org/files/1661/1661-0.txt");
		Scanner words = new Scanner(url.openStream()).useDelimiter("[^a-zA-Z]+");

		int length = 0;
		int freq = 0;
		System.out.println("n-gram length> ");
		length = keyboard.nextInt();
		System.out.println("Minimum n-gram frequency> ");
		freq = keyboard.nextInt();
		System.out.println("The " + length + "-grams with frequency counts of " + freq + " and above:");

		// adds words to a list
		ArrayList<Object> wordList = new ArrayList<Object>();
		while (words.hasNext()) {
			wordList.add(words.next().toLowerCase());
		}
		// builds the tree
		BinarySearchTree myTree = treeBuilder(wordList, length);

		System.out.print("\nFreq\t" + length + "-gram");
		System.out.println("\n----\t" + "----------------");

		// prints the top n-gram words indicated by user
		treeIterator(myTree, freq);

		// while statement to loop through increasing length of words
		String userInput = "";
		keyboard.nextLine(); // help clear new line -- remove if loop is not reachable
		while (true) {

			System.out.println();
			System.out.println("Enter " + (length++) + " words (X to stop): ");
			userInput = keyboard.nextLine();
			if (userInput.equalsIgnoreCase("x")) {
				System.out.println("Exiting");
				break;
			}

			// prints the next top three predicted words
			myTree = treeBuilder(wordList, length);
			treePredictor(myTree, userInput);
		}
		
		//closes scanner
		words.close();		
	}

	// prints the next top three predicted words
	public static void treePredictor(BinarySearchTree myTree, String userInput) {

		Iterator iter = myTree.iterator();
		ArrayList<WordFreq> topThreeWords = new ArrayList<WordFreq>();
		int maxFreqPerWord = 0;
		Double totalFreq = 0.0;

		while (iter.hasNext()) {
			Object data = iter.next();
			WordFreq temp = (WordFreq) data;
			String[] tempWord = temp.getWordIs().split(" ");
			String[] counter = userInput.split(" ");
			String otherWord = "";
			for (int i = 0; i < tempWord.length; i++) {
				if (i < counter.length) {
					otherWord += tempWord[i] + " ";
				}
			}
			otherWord = otherWord.trim();
			if (otherWord.equalsIgnoreCase(userInput)) {
				topThreeWords.add(temp);
				if (temp.getFreq() > maxFreqPerWord) {
					maxFreqPerWord = temp.getFreq();
				}
			}
		}
		for (WordFreq word : topThreeWords) {
			totalFreq += word.getFreq();
		}
		topThreeWords(topThreeWords, maxFreqPerWord, totalFreq);
	}

	// treePredictor helper (sorts and prints the words and their percentage)
	public static void topThreeWords(ArrayList topThreeWords, int maxFreqPerWord, Double totalFreq) {
		DecimalFormat wordPercentObject = new DecimalFormat("#0.0#%");
		DecimalFormat wordFreqObject = new DecimalFormat("00000");
		int topThreeCounter = 3;
		Collections.sort(topThreeWords);
		for (int j = maxFreqPerWord; j > 0; j--) {
			for (int i = topThreeWords.size() - 1; i >= 0; i--) {
				if (topThreeCounter == 0)
					break;

				if (((WordFreq) topThreeWords.get(i)).getFreq() == j) {

					Integer wordFreq = ((WordFreq) topThreeWords.get(i)).getFreq();
					Double wordPercent = (wordFreq / totalFreq);
					String wordString = ((WordFreq) topThreeWords.get(i)).getWordIs();
					System.out.println(wordFreqObject.format(wordFreq) + "\t" + wordPercentObject.format(wordPercent)
							+ "\t" + wordString);

					topThreeWords.remove(i);
					i = topThreeWords.size();
					topThreeCounter--;
				}
			}
		}
	}

	// builds the tree
	public static BinarySearchTree treeBuilder(ArrayList wordList, int length) {
		BinarySearchTree myTree = new BinarySearchTree();

		String ngram = "";
		for (int i = 0; i < wordList.size() - (length - 1); i++) {
			for (int j = 0; j < length; j++) {// adds the next 'length' words
				ngram += (String) wordList.get(i + j) + " ";
			}

			WordFreq sentence = new WordFreq(ngram);

			// adds sentence into the tree or increment if already in the tree
			if (myTree.get(sentence) == null) {
				sentence.inc();
				myTree.add(sentence);
			} else {
				WordFreq tempSentence = (WordFreq) myTree.get(sentence);
				tempSentence.inc();
			}
			ngram = ""; // reset ngram
		}
		return myTree;
	}

	// prints the top n-gram words indicated by user
	public static void treeIterator(BinarySearchTree myTree, int freq) {
		Iterator iter = myTree.iterator();
		while (iter.hasNext()) {
			Object data = iter.next();
			WordFreq temp = (WordFreq) data;
			if (temp.getFreq() >= freq) {
				System.out.println(temp);
			}
		}
	}
}