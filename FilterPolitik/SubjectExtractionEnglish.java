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

import edu.stanford.nlp.ling.CoreAnnotations.NamedEntityTagAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;

public class SubjectExtractionEnglish {

	public static void main(String[] args) throws IOException {

		String articlePoliticPath = "D:\\SourceCode\\python\\scrapping\\articles_politik\\";
		String API_KEY = "trnsl.1.1.20170514T113308Z.dc0360943fce4c7a.beca61b6d2d33fb4f71805c46661196557e6014e";
		String processedArticlePath = "D:\\SourceCode\\python\\scrapping\\processed_en_articles_politik\\";

		Properties props = new Properties();
		props.setProperty("annotators", "tokenize, ssplit, pos, lemma, ner");
		props.setProperty("tokenize.language", "en");

		StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

		BufferedReader br;
		BufferedWriter bw;

		HttpClient httpclient = HttpClients.createDefault();
		HttpPost httppost = new HttpPost("https://translate.yandex.net/api/v1.5/tr.json/translate");
		List<NameValuePair> params = new ArrayList<NameValuePair>(3);
		params.add(new BasicNameValuePair("key", API_KEY));
		params.add(new BasicNameValuePair("lang", "id-en"));
		HttpResponse response;
		HttpEntity entity;

        File newdir = new File(processedArticlePath);
        if (!newdir.exists()) newdir.mkdir();
        
        File dir = new File(articlePoliticPath);            
		File[] filesList = dir.listFiles();
		for (File file : filesList) {
			if (file.isFile()) {

				System.out.println(file.getName());
				br = new BufferedReader(new FileReader(articlePoliticPath + file.getName()));
				bw = new BufferedWriter(new FileWriter(processedArticlePath + "processed_en_" + file.getName()));
				String raw = br.readLine();

				JSONObject dataJSON = new JSONObject(raw);
				String title = dataJSON.getString("title");
				String url = dataJSON.getString("url");
				Iterator<Object> iter = dataJSON.getJSONArray("paragraphs").iterator();

				Map<String, List<String>> contents = new HashMap();

				br.close();
				while (iter.hasNext()) {
					String p = iter.next().toString();

					// System.out.println(p);
					try {
						params.remove(2);
					} catch (Exception e) {
					}

					params.add(new BasicNameValuePair("text", p));
					httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
					response = httpclient.execute(httppost);
					entity = response.getEntity();

					StringBuilder content = new StringBuilder();
					if (entity != null) {
					try {
						br = new BufferedReader(new InputStreamReader(entity.getContent()));

						String temp = "";

						while ((temp = br.readLine()) != null)
							content.append(temp);

						entity.getContent().close();

						Iterator<Object> text = new JSONObject(content.toString()).getJSONArray("text").iterator();

						content = new StringBuilder();

						while (text.hasNext())
							content.append(((String) text.next()).replace("?", " ? ").replace("-", " - ")
									.replace(".", " . ").replace(",", " , ").replace("!", " ! ")
									.replace("(", " ( ").replace(")", " ) ").replace("\"", " \" ")
									.replace("'", " ' "));

						Annotation document = new Annotation(content.toString());
						pipeline.annotate(document);

						List<CoreMap> sentences = document.get(SentencesAnnotation.class);
						String lastSubject = "";

						for (CoreMap sentence : sentences) {
							List<CoreLabel> labels = sentence.get(TokensAnnotation.class);

							boolean isPassiveVoice = false;
							String lastPos = "";

							for (CoreLabel token : labels) {
								String pos = token.get(PartOfSpeechAnnotation.class);
								if (lastPos.contains("VB") && (pos.contains("VB") || pos.contains("RB")))
									isPassiveVoice = true;
								lastPos = pos;
							}

							String subjectPerson = "";
							String objectPerson = "";
							boolean afterVerb = false;
							String lastNE = "";

							for (CoreLabel token : labels) {
								String pos = token.get(PartOfSpeechAnnotation.class);
								String ne = token.get(NamedEntityTagAnnotation.class);
								String word = token.get(TextAnnotation.class);

								if (!afterVerb && pos.contains("VB"))
									afterVerb = true;

								if (pos.equals("PRP")) {
									if (afterVerb)
										objectPerson = lastSubject;
									else
										subjectPerson = lastSubject;
								}

								if (pos.contains("NNP") || !ne.equalsIgnoreCase("O")) {
									if (afterVerb && (ne.equals(lastNE) || objectPerson.isEmpty())) {
										objectPerson += " " + word;
									} else if (!afterVerb && (ne.equals(lastNE) || subjectPerson.isEmpty())) {
										subjectPerson += " " + word;
									}
								}

								lastNE = ne;
								lastPos = pos;
							}

							objectPerson = objectPerson.trim();
							subjectPerson = subjectPerson.trim();
							
							if (!isPassiveVoice) {
								if (!objectPerson.isEmpty()) {
									if (contents.get(objectPerson) == null)
										contents.put(objectPerson, new ArrayList<String>());
									contents.get(objectPerson).add(sentence.toString());
									lastSubject = objectPerson;
								}
							} else {
								if (!subjectPerson.isEmpty()) {
									if (contents.get(subjectPerson) == null)
										contents.put(subjectPerson, new ArrayList<String>());
									contents.get(subjectPerson).add(sentence.toString());
									lastSubject = subjectPerson;
								}
							}
						}

						br.close();

						entity.getContent().close();
					}
					catch (Exception e){
					
					}
					
					}
					

				}

				JSONArray contentJSON = new JSONArray();
				for (String key : contents.keySet()){
					JSONObject content = new JSONObject();
					content.put("name", key);
					content.put("sentences", new JSONArray( contents.get(key) ) );
					contentJSON.put(content);
				}
				
				JSONObject result = new JSONObject();
				result.put("title", title);
				result.put("url", url);
				result.put("contents", contentJSON);

				bw.write(result.toString());
				bw.flush();
				bw.close();

			}
		}

	}

}
