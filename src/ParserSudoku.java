import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class ParserSudoku {
	public static void main(String[] args) {
		String file = "dump2.txt";
		BufferedReader br;
		String line;
		int[] lines = new int[729];
		char[][] chrs = new char[729][3];
		int[][] sol = new int[9][9];

		try {
			br = new BufferedReader(new FileReader(file));
			int i = 0;
			while((line = br.readLine()) != null) {
				lines[i] = (int) Double.parseDouble(line.split("\\s")[1]);
				chrs[i] = line.split("\\s")[0].substring(1, 4).toCharArray();
				i++;
			}
			br.close();
		}
		catch(FileNotFoundException e) {
		}
		catch(IOException e) {
		}

		for(int i = 0; i < lines.length; i++) {
			if(lines[i] == 1) {
				sol[Character.getNumericValue(chrs[i][0]) - 1][Character.getNumericValue(chrs[i][1]) - 1] = Character.getNumericValue(chrs[i][2]);
			}
		}

		String l = "";
		for(int i = 0; i < sol.length; i++) {
			for(int j = 0; j < sol.length; j++) {
				l += sol[i][j] + " ";
			}
			l += "\n";
		}
		System.out.println(l);
	}
}
