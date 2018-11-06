package priv.wangao.LogAnalysis.util;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author WangAo
 * 正则表达式帮助类
 */
public class RegExpHelper {

	private static final RegExpHelper instance = new RegExpHelper();

	// IP_REG : IPv4正则表达式
	private static final String IP_REG = "(?<![0-9])(?:(?:25[0-5]|2[0-4][0-9]|[0-1]?[0-9]{1,2})[.](?:25[0-5]|2[0-4][0-9]|[0-1]?[0-9]{1,2})[.](?:25[0-5]|2[0-4][0-9]|[0-1]?[0-9]{1,2})[.](?:25[0-5]|2[0-4][0-9]|[0-1]?[0-9]{1,2}))(?![0-9])";
	// PORT_REG : 端口正则表达式
	private static final String PORT_REG = "(?:[1-9]\\d{1,5})";
	
	public static final String ACT_NAT = "(\\S+);((?:\\d{1,3}\\.){3}\\d{1,3});\\d+;((?:\\d{1,3}\\.){3}\\d{1,3});\\d+\\-\\-\\-\\>((?:\\d{1,3}\\.){3}\\d{1,3});\\d+;";
	
	private RegExpHelper() {
	}

	/** 
	* @Title: getInstance 
	* @Description: 饿汉单例模式 
	* @return 
	* @return: RegExpHelper
	*/ 
	public static RegExpHelper getInstance() {
		return instance;
	}

	/** 
	* @Title: isAddr 
	* @Description: 判断是否为网络地址[IP:PORT] 
	* @param addr 网络地址
	* @return 是否为网络地址，true 表示是，false 表示否
	* @return: boolean
	*/ 
	public boolean isAddr(String addr) {
		String[] split = addr.split(":");
		if (split.length != 2)
			return false;
		else
			return this.isIPv4(split[0]) && this.isPort(split[1]);
	}

	/** 
	* @Title: isIPv4 
	* @Description: 判断是否为 IPv4 地址 
	* @param ipv4 IPv4 地址 
	* @return 是否为 IPv4 地址 ，true 表示是，false 表示否
	* @return: boolean
	*/ 
	public boolean isIPv4(String ipv4) {
		return this.match(IP_REG, ipv4);
	}

	public boolean isPort(String port) {
		return this.match(PORT_REG, port);
	}

	/** 
	* @Title: match 
	* @Description: 跟定正则表达式和目标文本，判断是否匹配正则表达式 
	* @param regex 正则表达式
	* @param target 目标文本
	* @return 是否匹配正则表达式 ，true 表示是，false 表示否
	* @return: boolean
	*/ 
	private boolean match(String regex, String target) {
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(target);
		return matcher.matches();
	}
	
	/** 
	* @Title: isMatch 
	* @Description: 跟定正则表达式数组和目标文本，判读目标文本是否命中其中一个正则表达式 
	* @param patterns 正则表达式数组
	* @param target 目标文本
	* @return 目标文本是否命中其中一个正则表达式  ，true 表示是，false 表示否
	* @return: boolean
	*/ 
	public boolean isMatch(String[] patterns, String target) {
		for (String pattern : patterns) {
			if (this.match(pattern, target)) {
				return true;
			}
		}
		return false;
	}

	public static void main(String[] args) {

	}

}
