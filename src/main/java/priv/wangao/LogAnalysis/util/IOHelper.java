package priv.wangao.LogAnalysis.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class IOHelper {

	private static IOHelper instance = new IOHelper();

	public static IOHelper getInstance() {
		return instance;
	}

	public void writeToFile(String context, String filePath, boolean append) {
		try (FileOutputStream fos = new FileOutputStream(new File(filePath), append);
				OutputStreamWriter osw = new OutputStreamWriter(fos);
				BufferedWriter bw = new BufferedWriter(osw);) {
			bw.write(context);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void writeToFile(List<String> context, String filePath, boolean append) {
		try (FileOutputStream fos = new FileOutputStream(new File(filePath), append);
				OutputStreamWriter osw = new OutputStreamWriter(fos, Charset.forName("UTF-8"));
				BufferedWriter bw = new BufferedWriter(osw);) {
			for (String line : context) {
				bw.write(line + "\r\n");
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void writeToFile(Map<String, Integer> context, String filePath, boolean append) {
		try (FileOutputStream fos = new FileOutputStream(new File(filePath), append);
				OutputStreamWriter osw = new OutputStreamWriter(fos, Charset.forName("UTF-8"));
				BufferedWriter bw = new BufferedWriter(osw);) {
			for (Map.Entry<String, Integer> entry : context.entrySet()) {
				bw.write(entry.toString() + "\r\n");
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public List<String> readFromFile(String filePath) {
		List<String> result = new ArrayList<String>();
		try (FileInputStream fis = new FileInputStream(new File(filePath));
				InputStreamReader isr = new InputStreamReader(fis, Charset.forName("UTF-8"));
				BufferedReader br = new BufferedReader(isr);) {
			String curLine = null;
			int count = 0;
			while ((curLine = br.readLine()) != null) {
				result.add(curLine);
				count++;
				if (count == 2000)
					break;
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

}
