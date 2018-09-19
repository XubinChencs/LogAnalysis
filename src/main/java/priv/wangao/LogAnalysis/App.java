package priv.wangao.LogAnalysis;

import net.sf.json.JSONObject;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        System.out.println( "Hello World!" );
        String json = "{\"@message\":\"Service_Control_Manager[740]: Windows Time 服务处于 停止 状态。\"}";
        System.out.println(JSONObject.fromObject(json).get("@message"));
    }
}
