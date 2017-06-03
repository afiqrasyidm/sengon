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

public class TranslateDocument {

	public static void main(String[] args) throws IOException {
		
		String id_to_langs[] = {"en"};
		String API_KEY = "trnsl.1.1.20170514T113308Z.dc0360943fce4c7a.beca61b6d2d33fb4f71805c46661196557e6014e";
		String articlePoliticPath = "D:\\SourceCode\\python\\scrapping\\articles_politik\\";
		
		BufferedReader br;
		BufferedWriter bw;
		
		Map<String, String> paths = new HashMap<>();
		paths.put("en", "D:\\SourceCode\\python\\scrapping\\translate_en_articles_politik\\");
		
		HttpClient httpclient = HttpClients.createDefault();
		HttpPost httppost = new HttpPost("https://translate.yandex.net/api/v1.5/tr.json/translate");
		HttpResponse response;
		HttpEntity entity;

		
		for (String lang : id_to_langs) {
			List<NameValuePair> params = new ArrayList<NameValuePair>(3);
			params.add(new BasicNameValuePair("key", API_KEY));
			params.add(new BasicNameValuePair("lang", "id-" + lang));
			
			String path = paths.get(lang);
			File newdir = new File(path);
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
					bw = new BufferedWriter(new FileWriter(path + file.getName()));
					String raw = br.readLine();

					JSONObject json = new JSONObject(raw);
					Iterator<Object> iter = json.getJSONArray("paragraphs").iterator();

					br.close();

					List<String> paragraphs = new ArrayList<>();

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
								
								while (text.hasNext()){
									temp = (String) text.next();
									content.append((temp).replace("?", " ? ").replace("-", " - ")
											.replace(".", " . ").replace(",", " , ").replace("!", " ! ").replace("(", " ( ")
											.replace(")", " ) ").replace("\"", " \" ").replace("'", " ' "));
								}
								br.close();

								entity.getContent().close();
							} catch (Exception e) {

							}
							
							paragraphs.add(content.toString());
						}
						
					}
					
					json.remove("paragraphs");
					json.put("paragraphs", (new JSONArray( paragraphs )) );
					bw.write(json.toString());
					bw.flush();
					bw.close();

				}
			}
		}	
	}
}
