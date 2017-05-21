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
	public static void main(String args[]) throws IOException {
		Set<String> dictionary = new HashSet<String>();
		InputStream in = Lemmatizer.class.getResourceAsStream("/root-words.txt");
		BufferedReader br = new BufferedReader(new InputStreamReader(in));

		String line;
		while ((line = br.readLine()) != null) {
			dictionary.add(line);
		}

		Lemmatizer lemmatizer = new DefaultLemmatizer(dictionary);
		String kalimat = "";
		StringTokenizer tokens = new StringTokenizer(kalimat);
		ArrayList<String> isi = new ArrayList<>();
		ArrayList<String> nama = new ArrayList<>();
		int iii = -1;
		while (tokens.hasMoreTokens()) {
			String cek = tokens.nextToken();
			String namaSubjek = "";
			boolean stillNamaSubjek = false;
			if (cek.charAt(cek.length() - 1) == '[') {
				nama.add(cek.substring(0,cek.length()-2));
				isi.add("");
				iii++;
			
			} else {
				if (cek.equals("]") || cek.equals("],")) {
						
				} else {
					isi.set(iii, isi.get(iii) + " " + cek);
				}

			}

		}

		
		for(int i =0; i<isi.size(); i++){
			tokens = new StringTokenizer(isi.get(i));
			String contoh1 = "";
			String space = " ";
			while(tokens.hasMoreTokens()){
				if(tokens.countTokens()==1){
					
					contoh1 = contoh1 + lemmatizer.lemmatize(tokens.nextToken());
					//System.out.println(i);

				} else{
					contoh1 = contoh1 + lemmatizer.lemmatize(tokens.nextToken()) + space;
				}	
			}
			isi.set(i, contoh1);
		}
		
		
		for(int i = 0; i<isi.size(); i++){
			System.out.println(nama.get(i));
			String arguments1[] = {"-l", "id", "-t", isi.get(i)};
			new SentimentAnalysis().run(arguments1);
		}
		
	}

}
