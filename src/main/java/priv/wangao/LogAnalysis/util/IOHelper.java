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

public class IOHelper {
	
	private static IOHelper instance = new IOHelper();
	
	public static IOHelper getInstance() {
		return instance;
	}
	
	public void writeToFile(String context, String filePath, boolean append) {
		try(
			FileOutputStream fos = new FileOutputStream(new File(filePath), append);
			OutputStreamWriter osw = new OutputStreamWriter(fos);
			BufferedWriter bw = new BufferedWriter(osw);
		) {
			bw.write(context);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void writeToFile(List<String> context, String filePath, boolean append) {
		try(
			FileOutputStream fos = new FileOutputStream(new File(filePath), append);
			OutputStreamWriter osw = new OutputStreamWriter(fos, Charset.forName("UTF-8"));
			BufferedWriter bw = new BufferedWriter(osw);
		) {
			for (String line : context) {
				bw.write(line);
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public List<String> readFromFile(String filePath) {
		List<String> result = new ArrayList<String>();
		try(
			FileInputStream fis = new FileInputStream(new File(filePath));
			InputStreamReader isr = new InputStreamReader(fis, Charset.forName("UTF-8"));
			BufferedReader br = new BufferedReader(isr);
		) {
			String curLine = null;
			while ((curLine = br.readLine()) != null) {
				result.add(curLine);
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}


}
