package priv.wangao.LogAnalysis.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author WangAo
 *
 */
public class RegExpHelper {

	private static RegExpHelper instance = new RegExpHelper();

	// IP_REG : IPv4正则表达式
	private static final String IP_REG = "(?<![0-9])(?:(?:25[0-5]|2[0-4][0-9]|[0-1]?[0-9]{1,2})[.](?:25[0-5]|2[0-4][0-9]|[0-1]?[0-9]{1,2})[.](?:25[0-5]|2[0-4][0-9]|[0-1]?[0-9]{1,2})[.](?:25[0-5]|2[0-4][0-9]|[0-1]?[0-9]{1,2}))(?![0-9])";

	private static final String PORT_REG = "(?:[1-9]\\d{1,5})";

	private RegExpHelper() {
	}

	public static RegExpHelper getInstance() {
		return instance;
	}

	public boolean isAddr(String addr) {
		String[] split = addr.split(":");
		if (split.length != 2)
			return false;
		else
			return this.isIPv4(split[0]) && this.isPort(split[1]);
	}

	public boolean isIPv4(String ipv4) {
		return this.match(IP_REG, ipv4);
	}

	public boolean isPort(String port) {
		return this.match(PORT_REG, port);

	}

	private boolean match(String regex, String target) {

		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(target);

		return matcher.matches();

	}

	public static void main(String[] args) {

	}

}
