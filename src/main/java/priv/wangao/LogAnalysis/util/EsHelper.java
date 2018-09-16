package priv.wangao.LogAnalysis.util;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.unit.TimeValue;
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
		// TODO Auto-generated constructor stub
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
			Settings settings = Settings.builder().put("cluster.name", cluster).build();
			// this.client = new PreBuiltTransportClient(settings);
			this.client = TransportClient.builder().settings(settings).build();
			;

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
	 * @Title: getRequestBuilderv24
	 * @Description: 获取请求体对象
	 * @param index
	 *            目标索引
	 * @param includes
	 *            需要的field数组
	 * @param excludes
	 *            不要的field数组
	 * @param qb
	 *            请求体对象
	 * @param batchSize
	 *            批量查询尺寸
	 * @param ttlSeconds
	 *            连接过期时间
	 * @return
	 * @return: SearchRequestBuilder
	 */
	private SearchRequestBuilder getRequestBuilderv24(String index, String[] includes, String[] excludes,
			QueryBuilder qb, int batchSize, int ttlSeconds) {
		SearchRequestBuilder requestBuilder = this.client.prepareSearch(index);

		requestBuilder.setFetchSource(includes, excludes);

		requestBuilder.addSort("_doc", SortOrder.ASC).setScroll(TimeValue.timeValueSeconds(ttlSeconds)).setQuery(qb)
				.setSize(batchSize);

		return requestBuilder;
	}

	/**
	 * @Title: getMatchAllQuery
	 * @Description: 获取match_all查询体对象
	 * @return match_all查询体对象
	 * @return: QueryBuilder
	 */
	private QueryBuilder getMatchAllQuery() {
		return QueryBuilders.matchAllQuery();
	}

	/**
	 * @Title: scrollRequest
	 * @Description: 执行滚动查询
	 * @param index
	 *            目标索引名
	 * @param includes
	 *            需要的field数组
	 * @param excludes
	 *            不要的field数组
	 * @param qb
	 *            查询体对象
	 * @param batchSize
	 *            批量查询大小
	 * @param ttlSeconds
	 *            超时时间
	 * @param maxCount
	 *            设定最大获取数量，超过即停止获取
	 * @param targetPath
	 *            输出的目标路径
	 * @return: void
	 */
	private void scrollRequest(String index, String[] includes, String[] excludes, QueryBuilder qb, int batchSize,
			int ttlSeconds, int maxCount, String targetPath) {
		SearchResponse scrollResp = this.getRequestBuilderv24(index, includes, excludes, qb, batchSize, ttlSeconds)
				.get();

		int count = 0;

		do {
			for (SearchHit hit : scrollResp.getHits().getHits()) {
				System.out.println(hit.getSource().get(includes[0]));
				if (targetPath != null) {
					IOHelper.getInstance().writeToFile(hit.getSource().get(includes[0]) + "\r\n", targetPath, true);
				}
				count++;
				if (maxCount > 0 && count >= maxCount)
					break;
			}
			if (maxCount > 0 && count >= maxCount)
				break;
			scrollResp = client.prepareSearchScroll(scrollResp.getScrollId())
					.setScroll(TimeValue.timeValueSeconds(ttlSeconds)).execute().actionGet();
		} while (scrollResp.getHits().getHits().length != 0 && count < 100000);
	}

	/**
	 * @Title: matchAllQuery
	 * @Description: 对外暴露的match_all方法查询
	 * @param index
	 *            目标索引名
	 * @param targetPath
	 *            目标路径
	 * @return: void
	 */
	public void matchAllQuery(String index, String[] includes, String[] excludes, String targetPath) {
		this.scrollRequest(index, includes, excludes, this.getMatchAllQuery(), 1000, 60, 100000, targetPath);
	}

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		EsHelper esHelper = new EsHelper("nic-multi-logs", "10.1.1.201:9300");
		esHelper.matchAllQuery("niclog-4th-2018.01.30", new String[] { "message" }, null, "target.txt");
	}

}
