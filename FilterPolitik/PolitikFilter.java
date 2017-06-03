import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import com.google.common.io.Files;
import com.sun.jna.platform.FileUtils;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.process.CoreLabelTokenFactory;
import edu.stanford.nlp.process.PTBTokenizer;
import edu.stanford.nlp.process.PTBTokenizer.PTBTokenizerFactory;

public class PolitikFilter {

	public static void main(String[] args) throws IOException {

		String detikArticlesPath = "D:\\SourceCode\\python\\scrapping\\articles_detik\\";
		String articlePoliticPath =  "D:\\SourceCode\\python\\scrapping\\articles_politik\\";
		double MIN_POLITICAL_INTENSITY = 1.6E-4;
		int MIN_POLITICAL_WORDS = 43;
		
		File dir2 = new File(articlePoliticPath);
		if (!dir2.exists()) dir2.mkdir();
		File[] filesList2 = dir2.listFiles();
		
		for (File file : filesList2){
			if (file.isFile()) file.delete();
		}
		
		File dir = new File(detikArticlesPath);
		File[] filesList = dir.listFiles();
		for (File file : filesList) {
			if (file.isFile()) {
				System.out.println(file.getName());
				
				BufferedReader br = new BufferedReader(new FileReader(detikArticlesPath + file.getName()));
				JSONObject json = new JSONObject ( br.readLine() );
				
				HashMap<String, Integer> map = new HashMap<>();
				PTBTokenizer<CoreLabel> ptbt = new PTBTokenizer<>(
						new StringReader(json.getJSONArray("paragraphs").join(" ")), new CoreLabelTokenFactory(),
						"");
				while (ptbt.hasNext()) {
					CoreLabel label = ptbt.next();
					String kata = label.toString();
					if (map.containsKey(kata)) {
						map.put(kata, map.get(kata) + 1);
					} else {
						map.put(kata, 1);
					}
				}
				
				HashMap<String, Integer> kata_politik = getKataPolitik();
				double count = 0;
				int politicalCount = 0;
				for (Map.Entry entri1 : map.entrySet()) {
					String kata = (String) entri1.getKey();
					for (Map.Entry entri2 : kata_politik.entrySet()) {
						String kp = (String) entri2.getKey();
						if (kata.equalsIgnoreCase(kp)) {
							politicalCount += (int) entri1.getValue();
						}
						count += (int) entri1.getValue();
					}
				}
				
//				if (politicalCount / count >= MIN_POLITICAL_INTENSITY){
				if (politicalCount >= MIN_POLITICAL_WORDS){
					System.out.println("YES");
					File newFile = new File(articlePoliticPath + file.getName());
					if (newFile.exists()) 
						newFile.createNewFile();
					Files.copy(new File(detikArticlesPath + file.getName()), newFile);
				}
				else
					System.out.println("NO");
				
			}

		}
		
		System.out.println((new File(articlePoliticPath)).list().length);
	}

	public static HashMap<String, Integer> getKataPolitik() {
		HashMap<String, Integer> map = new HashMap<>();
		try {
			BufferedReader in = new BufferedReader(
					new FileReader("D:\\SourceCode\\python\\scrapping\\kata_politik.txt"));
			String kata;
			while ((kata = in.readLine()) != null) {
				if (!map.containsKey(kata)) {
					map.put(kata, 1);
				}
			}
			in.close();
		} catch (IOException ioe) {

		}
		return map;
	}
}