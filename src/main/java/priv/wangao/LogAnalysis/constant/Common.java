package priv.wangao.LogAnalysis.constant;

/**
 * @author WangAo 常量定义接口
 */
public interface Common extends IOConstants, CityConstans, HdfsConstants, MySQLConstants, MQConstants, ESConstants {

	// LOCAL_LINE_SEPARATOR : 本地文件行分隔符
	String LOCAL_LINE_SEPARATOR = System.getProperties().getProperty("line.separator");

}
