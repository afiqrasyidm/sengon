import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.process.CoreLabelTokenFactory;
import edu.stanford.nlp.process.PTBTokenizer;

public class Politik {

	public static void main(String[] args) throws IOException {

		HashMap<String, Integer> map = new HashMap<>();
		PTBTokenizer<CoreLabel> ptbt = new PTBTokenizer<>(
				new FileReader("C:\\Users\\Maruli\\workspace\\MPPI\\src\\tes.txt"), new CoreLabelTokenFactory(), "");
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
		int count = 0;
		for (Map.Entry entri1 : map.entrySet()) {
			String kata = (String) entri1.getKey();
			for (Map.Entry entri2 : kata_politik.entrySet()) {
				String kp = (String) entri2.getKey();
				if (kata.equalsIgnoreCase(kp)) {
					count += (int) entri1.getValue();
					System.out.println(entri1.getKey() + ", " + entri1.getValue());
				}

			}
		}
		System.out.println("Jumlah Kata Politik : "+ count);

	}

	public static HashMap<String, Integer> getKataPolitik() {
		HashMap<String, Integer> map = new HashMap<>();
		try {
			BufferedReader in = new BufferedReader(
					new FileReader("C:\\Users\\Maruli\\workspace\\MPPI\\src\\kata_politik.txt"));
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