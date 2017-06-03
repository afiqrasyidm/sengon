import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import com.masasdani.sengon.classifier.HolisticLexiconClassifierEn;
import com.masasdani.sengon.classifier.HolisticLexiconClassifierId;
import com.masasdani.sengon.classifier.SentimentClassifier;
import com.masasdani.sengon.model.Document;
import com.masasdani.sengon.model.Sentiment;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.CoreAnnotations.NamedEntityTagAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;

public class TwoPartyEnglishSentiment {

	public static void main(String[] args) throws IOException {

		String articlePoliticPath = "D:\\SourceCode\\python\\scrapping\\translate_en_articles_politik\\";
//		String API_KEY = "trnsl.1.1.20170514T113308Z.dc0360943fce4c7a.beca61b6d2d33fb4f71805c46661196557e6014e";
		String sentimentArticlePath = "D:\\SourceCode\\python\\scrapping\\sentiment_en_articles_politik\\";

		BufferedReader br;
		BufferedWriter bw;
//
//		HttpClient httpclient = HttpClients.createDefault();
//		HttpPost httppost = new HttpPost("https://translate.yandex.net/api/v1.5/tr.json/translate");
//		List<NameValuePair> params = new ArrayList<NameValuePair>(3);
//		params.add(new BasicNameValuePair("key", API_KEY));
//		params.add(new BasicNameValuePair("lang", "id-en"));
//		HttpResponse response;
//		HttpEntity entity;

		SentimentClassifier classifier = new HolisticLexiconClassifierEn();

		File newdir = new File(sentimentArticlePath);
		if (!newdir.exists())
			newdir.mkdir();
		
		for (File file : newdir.listFiles()){
			if (file.isFile()) file.delete();
		}

		File dir = new File(articlePoliticPath);
		File[] filesList = dir.listFiles();
		for (File file : filesList) {
			if (file.isFile()) {

				System.out.println(file.getName());
				br = new BufferedReader(new FileReader(articlePoliticPath + file.getName()));
				bw = new BufferedWriter(new FileWriter(sentimentArticlePath + "sentiment_en_" + file.getName()));
				String raw = br.readLine();

				JSONObject json = new JSONObject(raw);
				Iterator<Object> iter = json.getJSONArray("paragraphs").iterator();

				br.close();

				StringBuilder content = new StringBuilder();

				while (iter.hasNext()) {
					String p = iter.next().toString();
					content.append(p);
					// System.out.println(p);
//					try {
//						params.remove(2);
//					} catch (Exception e) {
//					}
//
//					params.add(new BasicNameValuePair("text", p));
//					httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
//					response = httpclient.execute(httppost);
//					entity = response.getEntity();
//
//					if (entity != null) {
//						try {
//							br = new BufferedReader(new InputStreamReader(entity.getContent()));
//
//							String temp = "";
//
//							while ((temp = br.readLine()) != null)
//								content.append(temp);
//
//							entity.getContent().close();
//
//							Iterator<Object> text = new JSONObject(content.toString()).getJSONArray("text").iterator();
//
//							while (text.hasNext())
//								content.append(((String) text.next()).replace("?", " ? ").replace("-", " - ")
//										.replace(".", " . ").replace(",", " , ").replace("!", " ! ").replace("(", " ( ")
//										.replace(")", " ) ").replace("\"", " \" ").replace("'", " ' "));
//							br.close();
//
//							entity.getContent().close();
//						} catch (Exception e) {
//
//						}
//					}

				}

				Document doc = classifier.getDocumentOrientation(content.toString());
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
