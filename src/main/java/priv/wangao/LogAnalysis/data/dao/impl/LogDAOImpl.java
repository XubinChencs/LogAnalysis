package priv.wangao.LogAnalysis.data.dao.impl;

import java.util.Map;

import priv.wangao.LogAnalysis.data.dao.LogDAO;
import priv.wangao.LogAnalysis.util.EsHelper;

public class LogDAOImpl implements LogDAO {
	
	private EsHelper esHelper;

	public LogDAOImpl() {
		// TODO Auto-generated constructor stub
		try {
			this.esHelper = new EsHelper("my-cluster", "192.168.1.78:9300");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/* (non-Javadoc)
	 * @see priv.wangao.LogAnalysis.data.dao.LogDAO#matchAllEntry(java.lang.String[], java.lang.String[], java.lang.String[], java.lang.String, int)
	 */
	@Override
	public void matchAllEntry(String[] indices, String[] includes, String[] excludes, String outputPath, int maxCnt) {
		// TODO Auto-generated method stub
		this.esHelper.executeMatchAllQuery(indices, includes, excludes, outputPath, maxCnt);
	}

	/* (non-Javadoc)
	 * @see priv.wangao.LogAnalysis.data.dao.LogDAO#filterTermsEntry(java.lang.String[], java.util.Map, java.util.Map, java.lang.String[], java.lang.String[], java.lang.String, java.lang.String)
	 */
	@Override
	public void filterTermsEntry(String[] indices, Map<String, String> terms, Map<String, String> sorts, String[] includes,
			String[] excludes, String outputPath, int maxCnt) {
		// TODO Auto-generated method stub
		this.esHelper.executeTermsFilter(indices, terms, sorts, includes, excludes, outputPath, maxCnt);
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
