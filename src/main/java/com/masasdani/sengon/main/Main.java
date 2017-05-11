package com.masasdani.sengon.main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

import com.masasdani.sengon.SentimentAnalysis;

import jsastrawi.morphology.DefaultLemmatizer;
import jsastrawi.morphology.Lemmatizer;

public class Main {
	public static void main(String args[]) throws IOException{
		
		Set<String> dictionary = new HashSet<String>();
		InputStream in = Lemmatizer.class.getResourceAsStream("/root-words.txt");
		BufferedReader br = new BufferedReader(new InputStreamReader(in));

		String line;
		while ((line = br.readLine()) != null) {
			dictionary.add(line);
		}

		Lemmatizer lemmatizer = new DefaultLemmatizer(dictionary);
		// Selesai setup JSastrawi
		// lemmatizer bisa digunakan berkali-kali
		ArrayList<String> kalimat = new ArrayList<String>();
		kalimat.add("Dia Benci Kamu");
		kalimat.add("Aku Menyumbang");
		StringTokenizer tokens;
		for(int i=0; i<kalimat.size(); i++){
			tokens = new StringTokenizer(kalimat.get(i));
			String contoh1 = "";
			String space = " ";
			while(tokens.hasMoreTokens()){
				if(tokens.countTokens()==1){	
					contoh1 = contoh1 + lemmatizer.lemmatize(tokens.nextToken());
				} else{
					contoh1 = contoh1 + lemmatizer.lemmatize(tokens.nextToken()) + space;
				}	
			}
			kalimat.set(i, contoh1);
		}
		
		
		for(int i = 0; i<kalimat.size(); i++){
//			System.out.println(kalimat.get(i));
			String arguments1[] = {"-l", "id", "-t", kalimat.get(i)};
			new SentimentAnalysis().run(arguments1);
		}
//		System.out.println(lemmatizer.lemmatize("memakan"));
//		System.out.println(lemmatizer.lemmatize("meminum"));
//		System.out.println(lemmatizer.lemmatize("bernyanyi"));


		// Always wrap FileReader in BufferedReader.
		// Always close files.
		
//		String arguments1[] = {"-l", "id", "-t", "kejahatan"};
//		new SentimentAnalysis().run(arguments1);

	}
	
	
}
