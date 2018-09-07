package priv.wangao.LogAnalysis.util;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

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
			TransportClient client = new PreBuiltTransportClient(settings);
			
			for (String add : addrs) {
				String[] split = add.split(":");
				client.addTransportAddress(new TransportAddress(InetAddress.getByName(split[0]), Integer.parseInt(split[1])));
			}
		}
		return client;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
