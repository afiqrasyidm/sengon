import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import com.masasdani.sengon.classifier.HolisticLexiconClassifierId;
import com.masasdani.sengon.classifier.SentimentClassifier;
import com.masasdani.sengon.model.Document;
import com.masasdani.sengon.model.Sentiment;

public class TwoPartyIndonesiaSentiment {
	public static void main(String[] args) throws Exception {

		String politicArticlePath = "D:\\SourceCode\\python\\scrapping\\articles_politik\\";
		String sentimentArticlePath = "D:\\SourceCode\\python\\scrapping\\sentiment_id_articles_politik\\";
		
		BufferedReader br;
		BufferedWriter bw;

		SentimentClassifier classifier = new HolisticLexiconClassifierId();

		File newdir = new File(sentimentArticlePath);
		if (!newdir.exists())
			newdir.mkdir();
		
		for (File file : newdir.listFiles()){
			if (file.isFile()) file.delete();
		}

		File dir = new File(politicArticlePath);
		File[] filesList = dir.listFiles();
		for (File file : filesList) {
			if (file.isFile()) {
				System.out.println(file.getName());
				br = new BufferedReader(new FileReader(politicArticlePath + file.getName()));
				bw = new BufferedWriter(new FileWriter(sentimentArticlePath + "sentiment_id_" + file.getName()));
				String raw = br.readLine();

				JSONObject json = new JSONObject(raw);

				Document doc = classifier
						.getDocumentOrientation(json.getJSONArray("paragraphs").join(" . ").replace("?", " ? ")
								.replace("-", " - ").replace(".", " . ").replace(",", " , ").replace("!", " ! ")
								.replace("(", " ( ").replace(")", " ) ").replace("\"", " \" ").replace("'", " ' "));
				Sentiment sentiment = doc.getSentiment();

				String query = json.getString("query");
				String party = "NEUTRAL";
				
				switch (query) {
				case "anies" :	
					party = "Anies - Sandi"; break;
				case "sandiaga" :
					party = "Anies - Sandi"; break;
				case "ahok" :
					party = "Ahok - Djarot"; break;
				case "djarot" :
					party = "Ahok - Djarot"; break;
				}
				
				if (sentiment == sentiment.NEGATIVE) {
					if (party.equals("Anies - Sandi")) party = "Ahok - Djarot";
					else if (party.equals("Ahok - Djarot")) party = "Anies - Sandi";
				}
				
				JSONObject result = new JSONObject();
				result.put("file", file.getName());
				result.put("title", json.getString("title"));
				result.put("query", query);
				result.put("sentiment", sentiment.toString());
				result.put("partySupport", party);

				bw.write(result.toString());
				bw.flush();
				bw.close();

			}
		}
	}

}
