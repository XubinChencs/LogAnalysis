package priv.wangao.LogAnalysis.algorithm;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import priv.wangao.LogAnalysis.util.DataHelper;
import priv.wangao.LogAnalysis.util.IOHelper;

public class Spell {

	private class LCS {

		private List<String> seq;
		private List<String> logs;

		public LCS() {
			this.seq = new ArrayList<String>();
			this.logs = new ArrayList<String>();
		}

		public void append(String log) {
			this.logs.add(log);
		}

		public List<String> getSeq() {
			return seq;
		}

		public void setSeq(List<String> seq) {
			this.seq = seq;
		}

		public List<String> getLogs() {
			return logs;
		}

		public void setLogs(List<String> logs) {
			this.logs = logs;
		}

	}

	private List<LCS> LCSMap;

	public Spell() {
		this.LCSMap = new ArrayList<LCS>();
	}

	private List<String> tokenize(String log, String delimiters) {
		if (delimiters == null) {
			return Arrays.asList(log.split("\\s+"));
		} else {
			return Arrays.asList(log.split(delimiters));
		}
	}

	private void findLCS(String log) {
		boolean find = false;
		List<String> tokens = this.tokenize(log, null);
		List<String> lcsSeq = null;
		int maxSeqLen = 0;
		LCS targetLCS = null;
		for (LCS lcs : this.LCSMap) {
			lcsSeq = this.getLcsSeq(lcs.getSeq(), tokens);
			if (lcsSeq.size() > maxSeqLen) {
				maxSeqLen = lcsSeq.size();
				targetLCS = lcs;
			} else if (lcsSeq.size() == maxSeqLen && targetLCS != null
					&& targetLCS.getSeq().size() < lcs.getSeq().size()) {
				maxSeqLen = lcsSeq.size();
				targetLCS = lcs;
			}
		}

		if (maxSeqLen * 2 > tokens.size() && targetLCS != null) {
			targetLCS.setSeq(this.getLcsSeq(targetLCS.getSeq(), tokens));
			targetLCS.append(log);
		} else {
			LCS lcs = new LCS();
			lcs.setSeq(tokens);
			lcs.append(log);
			this.LCSMap.add(lcs);
		}
	}

	public void compute(List<String> logs) {
		for (String log : logs) {
			this.findLCS(log);
		}
	}

	private List<String> getLcsSeq(List<String> A, List<String> B) {
		List<String> result = new ArrayList<String>();
		int lenA = A.size(), lenB = B.size();
		int[][] dp = new int[lenA + 1][lenB + 1];
		String[][][] seq = new String[lenA + 1][lenB + 1][Math.max(lenA, lenB) + 1];
		for (int i = 0; i < lenA; i++) {
			for (int j = 0; j < lenB; j++) {
				if (A.get(i).equals(B.get(j)) == true) {
					dp[i + 1][j + 1] = dp[i][j] + 1;
					seq[i + 1][j + 1] = seq[i][j];
					seq[i + 1][j + 1][dp[i + 1][j + 1] - 1] = A.get(i);
				} else {
					if (dp[i + 1][j] > dp[i][j + 1]) {
						dp[i + 1][j + 1] = dp[i + 1][j];
						seq[i + 1][j + 1] = seq[i + 1][j];
					} else {
						dp[i + 1][j + 1] = dp[i][j + 1];
						seq[i + 1][j + 1] = seq[i][j + 1];
					}
				}
			}
		}
		for (int i = 0; i < dp[lenA][lenB]; i++) {
			result.add(seq[lenA][lenB][i]);
		}
		return result;
	}

	public void display() {
		File file = new File("Group");
		if (file.exists() == true) {
			file.delete();
		}
		int count = 0;
		for (LCS lcs : this.LCSMap) {
			System.out.println("Group " + count + ":-> " + lcs.getSeq());
			IOHelper.getInstance().writeToFile(lcs.getLogs(), "Group/" + count + ".txt", true);
			count++;
		}
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		List<String> logs = IOHelper.getInstance().readFromFile("RHZZ-syslog.txt");
		logs = DataHelper.getInstance().jsonsToList(logs, "@message", "timestamp");
		Spell spell = new Spell();
		spell.compute(logs);
		spell.display();
	}

}
