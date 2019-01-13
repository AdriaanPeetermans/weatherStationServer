package game.words;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;

public class WordsParser {
	
	public WordsParser(String language) {
		this.language = language;
		URL url = getClass().getResource("Words".concat(this.language).concat(".txt"));
		this.source = url.getPath();
	}
	
	public final String language;
	
	private final String source;
	
	private ArrayList<String> getWords() {
		ArrayList<String> result = new ArrayList<String>(42);
		File file = new File(this.source);
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			String st; 
			while ((st = br.readLine()) != null) {
				result.add(st);
			}
			br.close();
  		} 
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	private void append(String word) {
		try {
			FileWriter fw = new FileWriter(this.source, true);
			BufferedWriter bw = new BufferedWriter(fw);
		    PrintWriter out = new PrintWriter(bw);
		    out.println(word);
		    out.close();
		}catch (IOException e) {
		    e.printStackTrace();
		}
	}
	
	public void addWord(String word) {
		String ord = word.toLowerCase();
		ord = ord.substring(0,1).toUpperCase().concat(ord.substring(1));
		ArrayList<String> words = this.getWords();
		if (words.contains(ord)) {
			return;
		}
		this.append(ord);
	}
	
	public ArrayList<String >getWords(int number) {
		ArrayList<String> words = this.getWords();
		ArrayList<String> result = new ArrayList<String>(number);
		Collections.shuffle(words);
		for (int i = 0; i < number; i++) {
			result.add(words.get(i));
		}
		return result;
	}
}
