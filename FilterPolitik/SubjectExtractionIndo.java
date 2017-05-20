import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.json.JSONArray;
import org.json.JSONObject;

import com.masasdani.sengon.partofspeech.PartOfSpeech;
import com.masasdani.sengon.partofspeech.PartOfSpeechId;

import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;

public class SubjectExtractionIndo {
	
	public static void main(String[] args) throws IOException{
		String articlePoliticPath =  "D:\\SourceCode\\python\\scrapping\\articles_politik\\";
		String processedArticlePath = "D:\\SourceCode\\python\\scrapping\\processed_id_articles_politik\\";
		
		Properties props = new Properties();
        props.setProperty("annotators", "tokenize, ssplit");
        props.setProperty("tokenize.language", "en");
        
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
        
        BufferedReader br;
        BufferedWriter bw;
        
        File newdir = new File(processedArticlePath);
        if (!newdir.exists()) newdir.mkdir();
        
        File dir = new File(articlePoliticPath);
		File[] filesList = dir.listFiles();
		for (File file : filesList) {
			if (file.isFile()) {
				System.out.println(file.getName());
				br = new BufferedReader(new FileReader(articlePoliticPath + file.getName()));
				bw = new BufferedWriter(new FileWriter(processedArticlePath + "processed_id_" + file.getName()));
				String raw = br.readLine();

				JSONObject dataJSON = new JSONObject(raw);
				String title = dataJSON.getString("title");
				String url = dataJSON.getString("url");
				Iterator<Object> iter = dataJSON.getJSONArray("paragraphs").iterator();
				
				Map<String, List<String>> contents = new HashMap();

				br.close();
				while (iter.hasNext()) {
					
					String text = iter.next().toString().replace("?", " ? ").replace("-", " - ")
							.replace(".", " . ").replace(",", " , ").replace("!", " ! ")
							.replace("(", " ( ").replace(")", " ) ").replace("\"", " \" ")
							.replace("'", " ' ");
					
					Annotation document = new Annotation(text);
					
			        pipeline.annotate(document);
			        
			        List<CoreMap> sentences = document.get(SentencesAnnotation.class);
			        String lastSubject = "";
			        
			        for(CoreMap sentence: sentences) {
			        	List<CoreLabel> labels = sentence.get(TokensAnnotation.class);
			        	List<String> listWord = new ArrayList<>();
			        	
			        	for (CoreLabel l : labels)
			        		listWord.add(l.word());
			        	
			        	PartOfSpeech postagger = new PartOfSpeechId();
			        	
			        	String words[] = new String[listWord.size()];
			        	String listPos[] = postagger.test(listWord.toArray(words));
			        	
			        	boolean isPassiveVoice = false;
						String lastPos = "";

						for (String pos : listPos) {
							if (lastPos.contains("VB") && (pos.contains("VB") || pos.contains("RB")))
								isPassiveVoice = true;
							lastPos = pos;
						}

						String subjectPerson = "";
						String objectPerson = "";
						boolean afterVerb = false;
						
						for (int i = 0; i < listPos.length; i++) {
							String pos = listPos[i];
							String word = words[i];

							if (!afterVerb && pos.contains("VB"))
								afterVerb = true;

							if (pos.equals("PRP")) {
								if (afterVerb)
									objectPerson = lastSubject;
								else
									subjectPerson = lastSubject;
							}

							if (pos.contains("NNP") ) {
								if (afterVerb && (lastPos.contains("NNP") || objectPerson.isEmpty())) {
									objectPerson += " " + word;
								} else if (!afterVerb && (lastPos.contains("NNP") || subjectPerson.isEmpty())) {
									subjectPerson += " " + word;
								}
							}

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