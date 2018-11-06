package priv.wangao.LogAnalysis.data.acquisition;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.log4j.BasicConfigurator;

import priv.wangao.LogAnalysis.constant.Common;
import priv.wangao.LogAnalysis.data.dao.LogDAO;
import priv.wangao.LogAnalysis.data.factory.LogGenFactory;
import priv.wangao.LogAnalysis.util.HdfsHelper;
import priv.wangao.LogAnalysis.util.IOHelper;
import priv.wangao.LogAnalysis.util.mapreduce.ActNatMR;
import priv.wangao.LogAnalysis.util.mapreduce.ActNatMR.ActNatMRMapper;
import priv.wangao.LogAnalysis.util.mapreduce.ActNatMR.ActNatMRPartitioner;
import priv.wangao.LogAnalysis.util.mapreduce.ActNatMR.ActNatMRReducer;
import priv.wangao.LogAnalysis.vo.IPVisited;

public class DataProcessingImpl implements IDataProcessing {

	private HdfsHelper hdfs = new HdfsHelper();

	public DataProcessingImpl() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void pullFromEs() {
		LogDAO logGen = LogGenFactory.build("act-multi-logs", "192.168.105.132:9300");
		String[] target = logGen.getIndices(new String[] { 
			"act-nat-2018.10.29", "act-nat-2018.10.30", "act-nat-2018.10.31",
			"act-nat-2018.11.01", "act-nat-2018.11.02", "act-nat-2018.11.03",
			"act-nat-2018.11.04"
		});
		
		for (String index : target) {
			logGen.filterTermsEntry(new String[] { index }, null, new HashMap<String, String>() {

				// serialVersionUID :
				private static final long serialVersionUID = 1L;
				{
					put("@timestamp", "asc");
				}
				
			}, new String[] { "message" }, null, "act\\" + index + ".txt", 0);
		}
		
		//(target, null, null, "act\\act-gpu-auth-2018.10.29-2018.11.04.txt", 0);
	}

	@Override
	public void putToHdfs() {
		hdfs.put("E:\\Coding\\Java\\eclipse-workspace\\LogAnalysis\\act\\windows-multi-2018.10.16.txt",
				"/wangao/LogAnalysis/act");
	}

	@Override
	public void runMR() throws IOException, ClassNotFoundException, InterruptedException {
		// TODO Auto-generated method stub
		Configuration conf = new Configuration();
		conf.set("fs.defaultFS", Common.HDFS_ADDR);
		FileSystem fs = FileSystem.get(conf);
		Job job = Job.getInstance();

		job.setJarByClass(ActNatMR.class);
		job.setMapperClass(ActNatMRMapper.class);
		job.setReducerClass(ActNatMRReducer.class);

		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(NullWritable.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(IntWritable.class);

		job.setPartitionerClass(ActNatMRPartitioner.class);
		job.setNumReduceTasks(4);

		Path inputPath = new Path("hdfs://wangao001.ics.com:8020/wangao/LogAnalysis/act-nat-2018.10.1__.txt");
		FileInputFormat.setInputPaths(job, inputPath);

		Path outputPath = new Path("hdfs://wangao001.ics.com:8020/wangao/LogAnalysis/MR");
		if (fs.exists(outputPath)) {
			fs.delete(outputPath, true);
		}
		FileOutputFormat.setOutputPath(job, outputPath);

		BasicConfigurator.configure();

		job.waitForCompletion(true);
	}

	@Override
	public List<String> getMRResult() {
		// TODO Auto-generated method stub
		return hdfs.readFromHdfs("/wangao/LogAnalysis/MR/part-r-00000");
	}

	@Override
	public void dealData(List<String> result) {
		// TODO Auto-generated method stub
		List<IPVisited> ips = new ArrayList<IPVisited>();
		for (String msg : result) {
			String[] split = msg.split("\t");
			ips.add(new IPVisited(split[0], Integer.parseInt(split[1])));
		}

		System.err.println("IPS' size: " + ips.size());

		Collections.sort(ips, new Comparator<IPVisited>() {

			@Override
			public int compare(IPVisited o1, IPVisited o2) {
				if (o1.getCount() > o2.getCount()) {
					return -1;
				} else if (o1.getCount() == o2.getCount()) {
					return 0;
				} else {
					return 1;
				}
			}

		});

		int malicious_count = 0;

		List<String> m_ips = IOHelper.getInstance().readFromCSV("malicious_ip.csv", "ip", ",");
		Set<String> m_set = new HashSet<String>(m_ips);

		for (IPVisited ip : ips) {
			IOHelper.getInstance().writeToFile(ip.toString() + Common.LOCAL_LINE_SEPARATOR, "IPVisited_act-nat-2018.10.1__.txt", true);
			if (m_set.contains(ip.getIP())) {
				System.err.println("恶意IP: " + ip.toString());
				IOHelper.getInstance().writeToFile(ip.toString() + Common.LOCAL_LINE_SEPARATOR, "malicious_ip_find_act-nat-2018.10.1__.txt",
						true);
				malicious_count += ip.getCount();
			}
			if (!ip.getCountry().equals("中国")) {
				IOHelper.getInstance().writeToFile(ip.toString() + Common.LOCAL_LINE_SEPARATOR,
						"Non Continental Address_act-nat-2018.10.1__.txt", true);
			}
		}

		System.err.println("恶意访问总数:" + malicious_count);
	}

	@Override
	public void saveDealResult(List<String> result) {
		// TODO Auto-generated method stub

	}

}
