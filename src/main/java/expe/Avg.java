package expe;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class Avg {

	public static void main(String[] args) throws IOException {

		BufferedReader r = new BufferedReader(new FileReader("C:\\Users\\adrie\\Desktop\\toavg.txt"));
		String line;

		boolean header = true;

		float sum1 = 0;
		float sum2 = 0;
		int n = 0;

		while ((line = r.readLine()) != null) {

			if (header) {
				header = false;
				System.out.print(line + " ");
			}

			else if (line.equals("")) {
				header = true;

				sum1 = sum1 / ((float) n);
				sum2 = sum2 / ((float) n);

				System.out.println(sum1 + " (" + sum2 + " n/s)");

				sum1 = 0;
				sum2 = 0;
				n = 0;
			}

			else {
				String[] sl = line.split(" ");
				sum1 += Float.parseFloat(sl[0]);
				sum2 += Float.parseFloat(sl[1]);
				n++;
			}
		}

		r.close();
	}
}
