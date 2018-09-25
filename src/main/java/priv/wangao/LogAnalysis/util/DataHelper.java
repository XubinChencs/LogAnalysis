package priv.wangao.LogAnalysis.util;

import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONObject;

public class DataHelper {
	
	private static DataHelper instance = new DataHelper();

	public static DataHelper getInstance() {
		return instance;
	}
	
	private DataHelper() {
		
	}
	
	public List<String> jsonsToList(List<String> data, String... cols) {
		List<String> result = new ArrayList<String>();
		data.forEach(item -> {
			JSONObject json = JSONObject.fromObject(item);
			StringBuilder curLine = new StringBuilder();
			for (String col : cols) {
				curLine.append(json.getString(col));
			}
			result.add(curLine.toString());
		});
		return result;
	}

}
