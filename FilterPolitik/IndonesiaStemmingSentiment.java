import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import org.json.JSONArray;
import org.json.JSONObject;

import com.masasdani.sengon.classifier.HolisticLexiconClassifierId;
import com.masasdani.sengon.classifier.SentimentClassifier;
import com.masasdani.sengon.model.Document;
import com.masasdani.sengon.model.Sentiment;

import jsastrawi.morphology.DefaultLemmatizer;
import jsastrawi.morphology.Lemmatizer;

public class IndonesiaStemmingSentiment {
	public static void main(String[] args) throws Exception {

		Set<String> dictionary = new HashSet<String>();
		InputStream in = Lemmatizer.class.getResourceAsStream("/root-words.txt");
		BufferedReader br = new BufferedReader(new InputStreamReader(in));

		String line;
		while ((line = br.readLine()) != null) {
			dictionary.add(line);
		}

		Lemmatizer lemmatizer = new DefaultLemmatizer(dictionary);

		String processedArticlePath = "D:\\SourceCode\\python\\scrapping\\processed_id_articles_politik\\";
		String sentimentArticlePath = "D:\\SourceCode\\python\\scrapping\\sentiment_stemming_id_articles_politik\\";

		BufferedReader br1;
		BufferedWriter bw;

		SentimentClassifier classifier = new HolisticLexiconClassifierId();

		File newdir = new File(sentimentArticlePath);
		if (!newdir.exists())
			newdir.mkdir();

		File dir = new File(processedArticlePath);
		File[] filesList = dir.listFiles();
		for (File file : filesList) {
			if (file.isFile()) {
				System.out.println(file.getName());
				br = new BufferedReader(new FileReader(processedArticlePath + file.getName()));
				bw = new BufferedWriter(new FileWriter(sentimentArticlePath + "sentiment_" + file.getName()));
				String raw = br.readLine();
				br.close();

				List<Subject> subjects = new ArrayList<>();
				JSONObject dataJSON = new JSONObject(raw);
				Iterator<Object> iter = dataJSON.getJSONArray("contents").iterator();

				while (iter.hasNext()) {
					JSONObject content = new JSONObject(iter.next().toString());
					String name = content.getString("name");
					Iterator<Object> sentences = content.getJSONArray("sentences").iterator();
					int point = 0;

					while (sentences.hasNext()) {
						String kalimat = sentences.next().toString();
						// kalimat = kalimat.replaceAll("[()]", "");
						String kalimatDasar = "";

						StringTokenizer tokens = new StringTokenizer(kalimat);
						while (tokens.hasMoreTokens()) {
							String tampungToken = tokens.nextToken();
							tampungToken = tampungToken.replace("?", " ? ").replace("-", " - ").replace(".", " . ")
									.replace(",", " , ").replace("!", " ! ").replace("(", " ( ").replace(")", " ) ")
									.replace("\"", " \" ").replace("'", " ' ").replace("\\", " \\ ");

							if (tokens.countTokens() == 1) {
								kalimatDasar = kalimatDasar + lemmatizer.lemmatize(tampungToken) + " ";
							} else {
								kalimatDasar = kalimatDasar + lemmatizer.lemmatize(tampungToken) + " ";
							}

						}

						Document doc = classifier.getDocumentOrientation(kalimat);
						Sentiment sentiment = doc.getSentiment();
						int score = 0;
						if (sentiment == Sentiment.POSITIVE)
							score = 1;
						else if (sentiment == Sentiment.NEGATIVE)
							score = -1;
						point += score;
					}
					
					subjects.add(new Subject(name, point));
				}

				Collections.sort(subjects);

				int currentRank = 1;
				int currentPos = 1;
				int lastPoint = Integer.MIN_VALUE;

				JSONArray result = new JSONArray();

				for (Subject s : subjects) {
					if (lastPoint != s.point) {
						lastPoint = s.point;
						currentRank = currentPos;
					}
					result.put(new JSONObject("{" + " \"name\" : \"" + s.name + "\" , " + "\"rank\" : \"" + currentRank
							+ "\", " + "\"point\" : \"" + s.point + "\" " + "}"));
					currentPos++;
				}

				bw.write(result.toString());
				bw.flush();
				bw.close();

			}
		}

	}
}