import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.json.JSONArray;
import org.json.JSONObject;

import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.neural.rnn.RNNCoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations.SentimentAnnotatedTree;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.util.CoreMap;

public class EnglishSentiment {

	public static void main(String[] args) throws Exception{
		
		String processedArticlePath = "D:\\SourceCode\\python\\scrapping\\processed_en_articles_politik\\";
		String sentimentArticlePath = "D:\\SourceCode\\python\\scrapping\\sentiment_en_articles_politik\\";

        Properties props = new Properties();
        props.setProperty("annotators", "tokenize, ssplit, pos, parse, sentiment");
        props.setProperty("tokenize.language", "en");
        
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
        
        Annotation annotation;
        BufferedReader br;
        BufferedWriter bw;

        File newdir = new File(sentimentArticlePath);
        if (!newdir.exists()) newdir.mkdir();
        
        File dir = new File(processedArticlePath);
		File[] filesList = dir.listFiles();
		for (File file : filesList) {
			if (file.isFile()) {
				System.out.println(file.getName());
				br = new BufferedReader(new FileReader(processedArticlePath + file.getName()));
				bw = new BufferedWriter(new FileWriter(sentimentArticlePath + "sentiment_" + file.getName()));
				String raw = br.readLine();

		        List<Subject> subjects = new ArrayList<>();
				JSONObject dataJSON = new JSONObject(raw);
				Iterator<Object> iter = dataJSON.getJSONArray("contents").iterator();
				
				while (iter.hasNext()){
					JSONObject content = new JSONObject(iter.next().toString());
					String name = content.getString("name");
					Iterator<Object> sentences = content.getJSONArray("sentences").iterator();
					int point = 0;
					
					StringBuilder paragraph = new StringBuilder();
					
					while (sentences.hasNext()){
						paragraph.append(" ").append(sentences.next().toString());
					}
					
					annotation = new Annotation(paragraph.toString());
			        pipeline.annotate(annotation);
			        
			        List<CoreMap> coreMap = annotation.get(SentencesAnnotation.class);
			        
			        for (CoreMap sentence : coreMap){
			        	Tree tree = sentence.get(SentimentAnnotatedTree.class);
				        int score = RNNCoreAnnotations.getPredictedClass(tree) - 2;
				        score = Math.min( Math.max(score, -1), 1);
				        point += score;
			        }
			        
			        subjects.add( new Subject(name, point) );
				}
				
				Collections.sort(subjects);
				
				int currentRank = 1;
				int currentPos = 1;
				int lastPoint = Integer.MIN_VALUE;
				
				JSONArray result = new JSONArray();
				
				for (Subject s : subjects){
					if (lastPoint != s.point){
						lastPoint = s.point;
						currentRank = currentPos;
					}
					result.put(new JSONObject( "{"
							+ " \"name\" : \"" + s.name + "\" , "
							+ "\"rank\" : \"" + currentRank + "\", "
							+ "\"point\" : \"" + s.point + "\" "
							+ "}" ) );
					currentPos++;
				}
				
				bw.write(result.toString());
				bw.flush();
				bw.close();
				
			}	
		}
	}

}

