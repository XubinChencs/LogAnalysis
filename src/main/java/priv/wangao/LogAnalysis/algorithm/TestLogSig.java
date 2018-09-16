package priv.wangao.LogAnalysis.algorithm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import priv.wangao.LogAnalysis.util.IOHelper;

public class TestLogSig {

	public int getLCS(String s1, String s2) {
		int len1 = s1.length(), len2 = s2.length();
		int[][] dp = new int[len1 + 1][len2 + 1];
		for (int i = 0; i < len1; i++) {
			dp[i + 1][0] = 0;
		}
		for (int i = 0; i < len2; i++) {
			dp[0][i + 1] = 0;
		}
		int ans = 0;
		for (int i = 0; i < len1; i++) {
			for (int j = 0; j < len2; j++) {
				if (s1.charAt(i) == s2.charAt(j)) {
					dp[i + 1][j + 1] = dp[i][j] + 1;
				} else {
					dp[i + 1][j + 1] = Math.max(dp[i + 1][j], dp[i][j + 1]);
				}
				ans = Math.max(ans, dp[i + 1][j + 1]);
			}
		}
		return ans;
	}

	/**
	 * @Title: randomInit
	 * @Description: 局部搜搜获取初始解
	 * @param messages
	 * @param K
	 * @param groupIndex
	 * @return
	 * @return: List<List<String>>
	 */
	public List<List<String>> randomInit(List<String> messages, int K, List<Integer> groupIndex) {
		List<List<String>> result = new ArrayList<List<String>>();
		Random rand = new Random(25);
		int len = messages.size();
		for (int i = 0; i < K; i++) {
			result.add(new ArrayList<String>());
		}
		for (int i = 0; i < len; i++) {
			int group = rand.nextInt(K);
			groupIndex.set(i, group);
			result.get(group).add(messages.get(i));
		}
		return result;
	}

	public List<String> genTerms(String message) {
		String[] split = message.split("\\s+");
		List<String> result = new ArrayList<String>();
		int len = split.length;
		for (int i = 0; i < len; i++) {
			for (int j = i + 1; j < len; j++) {
				result.add(split[i] + " " + split[j]);
			}
		}
		return result;
	}

	public void updateGroupTermsUnionSet(List<String> terms, Map<String, Integer> union) {
		// System.out.println("terms size: " + terms.size());
		for (String term : terms) {
			if (union.containsKey(term) == true) {
				union.put(term, union.get(term) + 1);
			} else {
				union.put(term, 1);
			}
			// System.out.println(term);
		}
		// System.out.println("union size: " + union.size());
	}

	private List<Integer> getNext(String pattern) {
		int len = pattern.length();
		List<Integer> res = new ArrayList<Integer>();
		for (int i = 0; i < len + 1; i++) {
			res.add(0);
		}
		int k = 0;
		for (int q = 1; q < len; q++) {
			while (k > 0 && pattern.charAt(k) != pattern.charAt(q))
				k = res.get(k);
			if (pattern.charAt(k) == pattern.charAt(q)) {
				k++;
			}
			res.set(q + 1, k);
		}
		return res;
	}

	private int KMP(String text, String pattern, int from) {
		List<Integer> next = this.getNext(pattern);
		int k = 0, lt = text.length(), lp = pattern.length();
		for (int q = from; q < lt; q++) {
			while (k > 0 && pattern.charAt(k) != text.charAt(q))
				k = next.get(k);
			if (pattern.charAt(k) == text.charAt(q)) {
				k++;
			}
			if (k == lp) {
				return q - k + 1;
			}
		}
		return -1;
	}

	private boolean contains(String term, String message) {
		String[] split = term.split(" ");
		int first = this.KMP(message, split[0], 0), second = -1;
		if (first != -1) {
			second = this.KMP(message, split[1], first + split[0].length());
		}
		// int first = message.indexOf(split[0]), second = -1;
		// if (first != -1) {
		// second = message.indexOf(split[1], first+split[0].length());
		// }
		return second != -1;
	}

	private int getNrC(String term, Map<String, Integer> union) {
		if (union.containsKey(term) == false)	return 0;
		return union.get(term);
	}

	public double getDelta(List<List<String>> groups, List<Map<String, Integer>> unions, int i, int j, int k) {
		double ans = 0.0D;
		List<String> terms = this.genTerms(groups.get(i).get(k));
		for (String term : terms) {
			int NrCi = this.getNrC(term, unions.get(i));
			int NrCj = this.getNrC(term, unions.get(j));
			double prCi = (double) NrCi / groups.get(i).size();
			double prCj = (double) NrCj / groups.get(j).size();
			ans += prCj * prCj - prCi * prCi;
		}
		return ans * 3.0D;
	}

	private double getGroupPhi(List<String> group, Map<String, Integer> union) {
		for (String message : group) {
			this.updateGroupTermsUnionSet(this.genTerms(message), union);
		}
		double ans = 0.0D;
		System.out.println("Union size: " + union.size());
		long start = System.currentTimeMillis();
		int i = 0;
		for (Map.Entry<String, Integer> entry : union.entrySet()) {
			int NrC = entry.getValue();
			// System.out.println("NrC: " + NrC + " " + union.size());
			// if (NrC > 1) System.out.println(i++ + "NrC: " + NrC);
			ans += (double) NrC * NrC * NrC / group.size() / group.size();
		}
		long end = System.currentTimeMillis();
		System.out.println("getGroupPhi: " + ans + " " + union.size());
		System.out.println("Timeout: " + (end - start) + "ms");
		return ans;
	}

	public double getPhi(List<List<String>> groups, List<Map<String, Integer>> unions) {
		double ans = 0.0D;
		int K = groups.size();
		for (int i = 0; i < K; i++) {
			ans += this.getGroupPhi(groups.get(i), unions.get(i));
		}
		return ans;
	}

	public void logSig(List<String> messages, int K) {

	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		TestLogSig logSig = new TestLogSig();
		// String s1 = "<invld>Jan 30 09:48:11 10.254.4.50 PNB10>dnsquery3 1517276949
		// 74-25-8a-c2-37-00 202.112.137.78 54103 10.8.10.24 53 wup.browser.qq.com";
		// String s2 = "<invld>Jan 30 10:14:33 10.254.4.50 PNB10>dnsquery3 1517278531
		// 00-24-ac-02-76-1c 219.224.142.130 54749 10.5.0.4 53 weibo.com";
		// System.out.println(logSig.getLCS(s1, s2));
		// System.out.println(s1.length());
		// System.out.println(s2.length());
		List<String> messages = IOHelper.getInstance().readFromFile("target.txt");
		int len = messages.size();
		List<Integer> groupIndex = Arrays.asList(new Integer[len]);
		List<List<String>> init = logSig.randomInit(messages, 10, groupIndex);
		List<Map<String, Integer>> unions = new ArrayList<Map<String, Integer>>();
		for (int i = 0; i < 10; i++) {
			unions.add(new HashMap<String, Integer>());
		}
		System.out.println(logSig.getPhi(init, unions));
		System.out.println("Union size: " + unions.size());
		System.out.println("DeltaL: " + logSig.getDelta(init, unions, 2, 5, 3));
	}

}
