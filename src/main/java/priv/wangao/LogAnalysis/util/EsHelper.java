package priv.wangao.LogAnalysis.util;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.sort.SortOrder;

/**
 * @author WangAo
 *
 */
public class EsHelper {

	// client : Es客户端
	private TransportClient client = null;
	// cluster : 集群名称
	private String cluster = null;
	// addrs : 集群地址数组
	private List<String> addrs = new ArrayList<String>();
	// DEFAULT_BATCHSIZE : 滚动查询批量默认大小
	private final int DEFAULT_BATCHSIZE = 1000;
	// DEFAULT_BATCHSIZE : 滚动查询默认超时时间
	private final int DEFAULT_TTLSECOND = 1200;
	// batchSize : 滚动查询批量大小
	private int batchSize;
	// ttlSecond : 滚动查询超时时间
	private int ttlSecond;

	/**
	 * @Title:EsHelper
	 * @Description: 指定集群名称和集群地址构建客户端的构造函数
	 * @param cluster
	 *            集群名称
	 * @param addrs
	 *            集群地址
	 * @throws Exception
	 *             集群地址无效时，抛出异常
	 */
	public EsHelper(String cluster, String addrs) throws Exception {
		String[] split = addrs.split(";");

		if (split == null || split.length == 0) {
			throw new Exception("无效的集群地址");
		}

		for (String addr : split) {
			if (RegExpHelper.getInstance().isAddr(addr) == false) {
				throw new Exception("无效的地址: " + addr);
			}
			this.addrs.add(addr);
		}

		this.cluster = cluster;
		this.client = this.getClientInstance();
		this.batchSize = DEFAULT_BATCHSIZE;
		this.ttlSecond = DEFAULT_TTLSECOND;
	}

	/**
	 * @Title: getClientInstance
	 * @Description: 获取ES客户端实例
	 * @return ES客户端实例
	 * @throws NumberFormatException
	 * @throws UnknownHostException
	 * @return: TransportClient
	 */
	private TransportClient getClientInstance() throws NumberFormatException, UnknownHostException {
		if (this.client == null) {
			Settings settings = Settings.builder().put("cluster.name", this.cluster).build();
			// this.client = new PreBuiltTransportClient(settings);
			this.client = TransportClient.builder().settings(settings).build();

			for (String add : addrs) {
				String[] split = add.split(":");
				// this.client.addTransportAddress(
				// new TransportAddress(InetAddress.getByName(split[0]),
				// Integer.parseInt(split[1])));
				this.client.addTransportAddress(
						new InetSocketTransportAddress(InetAddress.getByName(split[0]), Integer.parseInt(split[1])));

			}
		}
		return this.client;
	}

	/**
	 * @Title: getBatchSize
	 * @Description: 获取批量查询超时时间
	 * @return 批量查询超时时间
	 * @return: int
	 */
	public int getBatchSize() {
		return batchSize;
	}

	/**
	 * @Title: setBatchSize
	 * @Description: 设置批量查询超时时间
	 * @param batchSize
	 *            批量查询超时时间
	 * @return: void
	 */
	public void setBatchSize(int batchSize) {
		this.batchSize = batchSize;
	}

	/**
	 * @Title: getTtlSecond
	 * @Description: 获取滚动查询超时时间
	 * @return 滚动查询超时时间
	 * @return: int
	 */
	public int getTtlSecond() {
		return ttlSecond;
	}

	/**
	 * @Title: setTtlSecond
	 * @Description: 设置滚动查询超时时间
	 * @param ttlSecond
	 *            滚动查询超时时间
	 * @return: void
	 */
	public void setTtlSecond(int ttlSecond) {
		this.ttlSecond = ttlSecond;
	}

	public void executeMatchAllQuery(String[] indices, String[] includes, String[] excludes, String outputPath,
			int maxCnt) {
		SearchRequestBuilder searchQuery = indices == null ? this.client.prepareSearch()
				: this.client.prepareSearch(indices);
		searchQuery.setFetchSource(includes, excludes);
		searchQuery.addSort("_doc", SortOrder.ASC).setScroll(TimeValue.timeValueSeconds(this.ttlSecond))
				.setQuery(this.matchAllQuery());

		SearchResponse scrollResp = searchQuery.get();
		int count = 0;

		do {
			for (SearchHit hit : scrollResp.getHits().getHits()) {
				System.out.println(hit.getSourceAsString());
				if (outputPath != null) {
					IOHelper.getInstance().writeToFile(hit.getSourceAsString(), outputPath, true);
				}
				count++;
				if (maxCnt > 0 && count >= maxCnt) {
					break;
				}
			}
			if (maxCnt > 0 && count >= maxCnt) {
				break;
			}
			scrollResp = client.prepareSearchScroll(scrollResp.getScrollId())
					.setScroll(TimeValue.timeValueSeconds(this.ttlSecond)).execute().actionGet();
		} while (scrollResp.getHits().getHits().length != 0);
	}

	private QueryBuilder matchAllQuery() {
		return QueryBuilders.matchAllQuery();
	}

	private QueryBuilder termsQuery(Map<String, String> terms) {
		BoolQueryBuilder result = QueryBuilders.boolQuery();
		for (Map.Entry<String, String> entry : terms.entrySet()) {
			result.filter(QueryBuilders.termQuery(entry.getKey(), entry.getValue()));
		}
		return result;
	}

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		EsHelper esHelper = new EsHelper("nic-multi-logs", "10.1.1.201:9300");
		esHelper.executeMatchAllQuery(new String[] { "niclog-4th-2018.01.30" }, new String[] { "message" }, null,
				"target.txt", 100000);
	}

}
