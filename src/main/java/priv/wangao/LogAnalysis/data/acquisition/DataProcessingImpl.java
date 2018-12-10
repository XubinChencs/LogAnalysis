package priv.wangao.LogAnalysis.data.acquisition;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
		LogDAO logGen = LogGenFactory.build("nic-multi-logs", "10.1.1.201:9300");
		
		System.err.println(String.join(",", logGen.getIndices()));
	
		
		
		String[] target = logGen.getIndices(new String[] { 
			"niclog-4th-2018\\.11\\..*",
		});
		
		ExecutorService executorService = Executors.newFixedThreadPool(7);
		
		for (String index : target) {
			executorService.execute(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					
					logGen.containTermEntry(new String[] { index }, "source-IP", new ArrayList<String>() {
						// serialVersionUID :
						private static final long serialVersionUID = 1L;
						{
							add("219.224.171.2");
							add("219.224.171.3");
							add("219.224.171.4");
							add("219.224.171.5");
							add("219.224.171.6");
							add("219.224.171.7");
							add("219.224.171.8");
							add("219.224.171.9");
							add("219.224.171.10");
							add("219.224.171.11");
							add("219.224.171.12");
							add("219.224.171.13");
							add("219.224.171.14");
							add("219.224.171.15");
							add("219.224.171.16");
							add("219.224.171.17");
							add("219.224.171.18");
							add("219.224.171.19");
							add("219.224.171.20");
							add("219.224.171.21");
							add("219.224.171.22");
							add("219.224.171.23");
							add("219.224.171.24");
							add("219.224.171.25");
							add("219.224.171.26");
							add("219.224.171.27");
							add("219.224.171.28");
							add("219.224.171.29");
							add("219.224.171.30");
							add("219.224.171.31");
							add("219.224.171.32");
							add("219.224.171.33");
							add("219.224.171.34");
							add("219.224.171.35");
							add("219.224.171.36");
							add("219.224.171.37");
							add("219.224.171.38");
							add("219.224.171.39");
							add("219.224.171.40");
							add("219.224.171.41");
							add("219.224.171.42");
							add("219.224.171.43");
							add("219.224.171.44");
							add("219.224.171.45");
							add("219.224.171.46");
							add("219.224.171.47");
							add("219.224.171.48");
							add("219.224.171.49");
							add("219.224.171.50");
							add("219.224.171.51");
							add("219.224.171.52");
							add("219.224.171.53");
							add("219.224.171.54");
							add("219.224.171.55");
							add("219.224.171.56");
							add("219.224.171.57");
							add("219.224.171.58");
							add("219.224.171.59");
							add("219.224.171.60");
							add("219.224.171.61");
							add("219.224.171.62");
							add("219.224.171.63");
							add("219.224.171.64");
						}
					}, new HashMap<String, String>() {

						// serialVersionUID :
						private static final long serialVersionUID = 1L;
						{
							put("@timestamp", "asc");
						}
						
					}, new String[] {
						"time", "host", "operation", 
						"source-mac", "source-IP", "source-port",
						"server-IP", "server-port", "content"
					}, null, "nic\\" + index + ".txt", 0);
					
//					logGen.filterTermsEntry(new String[] { index }, null 
//							, new HashMap<String, String>() {
//
//						// serialVersionUID :
//						private static final long serialVersionUID = 1L;
//						{
//							put("@timestamp", "asc");
//						}
//						
//					}, null, null, "nat\\" + index + ".txt", 0);
				}
				
			});
			
		}
		
		executorService.shutdown();
		
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
