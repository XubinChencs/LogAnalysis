package priv.wangao.LogAnalysis.data.dao.impl;

import java.util.Map;

import priv.wangao.LogAnalysis.data.dao.LogDAO;
import priv.wangao.LogAnalysis.util.EsHelper;

public class LogDAOImpl implements LogDAO {

	private EsHelper esHelper;

	public LogDAOImpl(String clusterName, String addrs) {
		try {
			this.esHelper = new EsHelper(clusterName, addrs);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * priv.wangao.LogAnalysis.data.dao.LogDAO#matchAllEntry(java.lang.String[],
	 * java.lang.String[], java.lang.String[], java.lang.String, int)
	 */
	@Override
	public void matchAllEntry(String[] indices, String[] includes, String[] excludes, String outputPath, int maxCnt) {
		this.esHelper.executeMatchAllQuery(indices, includes, excludes, outputPath, maxCnt);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * priv.wangao.LogAnalysis.data.dao.LogDAO#filterTermsEntry(java.lang.String[],
	 * java.util.Map, java.util.Map, java.lang.String[], java.lang.String[],
	 * java.lang.String, java.lang.String)
	 */
	@Override
	public void filterTermsEntry(String[] indices, Map<String, String> terms, Map<String, String> sorts,
			String[] includes, String[] excludes, String outputPath, int maxCnt) {
		this.esHelper.executeTermsFilter(indices, terms, sorts, includes, excludes, outputPath, maxCnt);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see priv.wangao.LogAnalysis.data.dao.LogDAO#getIndices()
	 */
	@Override
	public String[] getIndices() {
		return this.esHelper.executeGetIndices(null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see priv.wangao.LogAnalysis.data.dao.LogDAO#getIndices(java.lang.String[])
	 */
	@Override
	public String[] getIndices(String[] patterns) {
		return this.esHelper.executeGetIndices(patterns);
	}

}
