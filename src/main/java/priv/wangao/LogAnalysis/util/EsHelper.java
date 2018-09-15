package priv.wangao.LogAnalysis.util;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.sort.SortOrder;

public class EsHelper {

	private TransportClient client = null;
	private String cluster = null;
	private List<String> addrs = new ArrayList<String>();

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

	public void close() {
		this.client.close();
	}

	private QueryBuilder getMatchAllQuery() {
		return QueryBuilders.matchAllQuery();
	}

	private void scrollRequest(String index, QueryBuilder qb, int batchSize, int ttlSeconds) {
		SearchResponse scrollResp = this.client.prepareSearch(index)
				// .addSort(FieldSortBuilder.DOC_FIELD_NAME, SortOrder.ASC)
				.addSort("_doc", SortOrder.ASC).setScroll(TimeValue.timeValueSeconds(ttlSeconds)).setQuery(qb)
				.setSize(batchSize).get();
		
		int count = 0;

		do {
			for (SearchHit hit : scrollResp.getHits().getHits()) {
				// Handle the hit...
				System.out.println(hit.getSource().get("message"));
				count++;
			}

			scrollResp = client.prepareSearchScroll(scrollResp.getScrollId())
					.setScroll(TimeValue.timeValueSeconds(ttlSeconds)).execute().actionGet();
		} while (scrollResp.getHits().getHits().length != 0 && count < 100000);
	}

	public void matchAllQuery(String index) {
		this.scrollRequest(index, this.getMatchAllQuery(), 1000, 60);
	}

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		EsHelper esHelper = new EsHelper("nic-multi-logs", "10.1.1.201:9300");
		esHelper.matchAllQuery("niclog-4th-2018.01.30");
	}

}
