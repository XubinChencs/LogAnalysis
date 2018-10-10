package priv.wangao.LogAnalysis;

import net.sf.json.JSONObject;
import priv.wangao.LogAnalysis.constant.EAction;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        EAction.success.getValue().display();
        EAction.fail.getValue().display();
        EAction.success.getValue().display();
    }
}
