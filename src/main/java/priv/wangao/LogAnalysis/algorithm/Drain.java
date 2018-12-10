package priv.wangao.LogAnalysis.algorithm;

import java.io.BufferedReader;
import java.io.DataOutput;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import net.sf.json.JSONObject;
import priv.wangao.LogAnalysis.util.IOHelper;

public class Drain {
	
	private static final String DELIMITER = "\\s+";
	DrainTree<String> tree = new DrainTree<String>(null, 4, 10, 0.3);
	String fileName = "windows-multi-2018.10.16.txt";
	
	public Drain(){

	}
	
	public void process(String log, double ID) {
		DrainTree.Node<String> leafNode = traverse(log);
		if (leafNode == null) return;
		DrainLogGroup logGroup = leafNode.findGroup(log);
		if (logGroup == null) {
			logGroup = leafNode.addGroup(log, ID);
		} else {
			logGroup.update(log);
		}
		//System.out.println(logGroup.toString());
	}
	
	private DrainTree.Node<String> traverse(String log){
		DrainTree.Node<String> node;
		String[] seq = log.split(DELIMITER);
		String len = String.valueOf(seq.length);
		node = tree.root().findChild(len);
		Pattern pattern = Pattern.compile("[0-9]*");
		if (node == null) {
			node = tree.addNode(len, tree.root());
			if (node == null) {
				System.err.println("分支数达最大，无匹配日志长度");
				return null;
			}
		}
		DrainTree.Node<String> child_node;
		for (int i = 0; i < tree.getMaxDepth() - 2; i++) {
			child_node = node.findChild(pattern.matcher(seq[i]).matches()?"*":seq[i]);
			if (child_node == null) {
				if (pattern.matcher(seq[i]).matches()) {
					 child_node = tree.addNode("*", node);
				}else child_node = tree.addNode(seq[i], node);
				if (child_node == null) {
					System.err.println("分支数达最大，无匹配 token");
					return null;
				}
			}
			node = child_node;
		}
		
		return node;
	}
	
	

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Drain drain = new Drain();
		double cnt = 0;
		long startTime=System.nanoTime();   //获取开始时间 
		System.out.println("Running...");
		List<String> logs = IOHelper.getInstance().readFromFile("act\\act-shahe-hp-inspur-2018.11.15.txt");
		for (String log : logs) {
			drain.process(JSONObject.fromObject(log.split("\t")[2]).getString("message"), cnt);
			cnt++;
			System.err.println(cnt);
		}
		List<DrainTree.Node<String>> nodes = drain.tree.nodes;

		int groupNum = 0;
		for (int i = 0; i < nodes.size(); i++) {
			if (nodes.get(i).getDepth() == 4) {
				List<DrainLogGroup> groupList = nodes.get(i).groupList;
				for (int j = 0; j < groupList.size(); j++) {
					groupNum++;
					IOHelper.getInstance().writeToFile(groupList.get(j).toString() + "\r\n", "drain\\events.txt", true);
					IOHelper.getInstance().writeToFile(groupList.get(j).toString() + "\r\n", "drain\\group\\" + groupNum + ".txt", true);
					IOHelper.getInstance().writeToFile(new ArrayList<String>(groupList.get(j).getLogSet()), "drain\\group\\" + groupNum + ".txt", true);
				}
			}
		}
	}

}
