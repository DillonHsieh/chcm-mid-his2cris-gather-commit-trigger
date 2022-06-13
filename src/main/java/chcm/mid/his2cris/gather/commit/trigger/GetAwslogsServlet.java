package chcm.mid.his2cris.gather.commit.trigger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import chcm.mid.his2cris.gather.commit.trigger.dto.CHCM;

@WebServlet("/GetAwslogsServlet")
public class GetAwslogsServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	public static Map<String, List<CHCM>> commitsMap = new HashMap<>();
    public GetAwslogsServlet() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		System.out.println("GetAwslog!");
		
		BufferedReader reader = request.getReader();
        char[] buf = new char[512];
        int len = 0;
        StringBuffer contentBuffer = new StringBuffer();
        while ((len = reader.read(buf)) != -1) {
            contentBuffer.append(buf, 0, len);
        }
        
        String logger = contentBuffer.toString();        
        if(logger == null){
        	logger = "";
        }        
        System.out.println(logger);
        HandlerGatherCommitTrigger commitTrigger = new HandlerGatherCommitTrigger();
//		commitTrigger.handlerRequest(logger);
//      String testLogger = "H4sIAAAAAAAAAO1Uy27aUBD9FeRNNjzu+4GUhbGd1BLYFBv1lSh24IpaIhDZl6RRxL93DKqctBDUqst4dWfOzJmZ49E8O3emqvKFSZ/ujdN3fDd1b0ZBkriXgdN21o8rU4IbCaSYYFgSIsG9XC8uy/XmHpBe/lj1ynnVK1aVzVcz03s0y+UKSMFcGEAQns/tzPYWBrjy5T49saXJ7yD/SDREVZvbalYW97ZYry6KpTVl5fS/Od+LioC76tiyWEBKp9wsTWeTW+d6xxw8mJWtI5+dYg4FqFBcK4kVZZozxjFHmMI0knPEBOZcIkaR5lJqiTAjACCuiYIObAHi2PwO5sSCE8WpwpIT3P4lGtATREgH8Q5WKRJ9RPtUdiEEan69shhjAZVaHzemfLqySZC23Gkae/FoFKbnCEqYH7bMZ9bMLwqznEPbz/+Le7ttH1BAChiAMMGRREhgIiWXGB5ME4pAHQo2p0wJyvhxBchpBQjV8s8up2NYsKCV+QMSRkmKs27mjaJBMIjdiT+MPTcN4ygdDLPWqa+eN/OGYRCloZ+d39wWq7x8OvN8tPvEWbuVBQ2OwYzcUdBEUoTrmHDcuDDqEtSVqIt36SP38zAEMffZSeqm02T/9oPEm4TjutnsPJoOh+CLJ5dR3HAhVlN4kwDmnSbB5CXmjsObl3itSYPWeiJMRIOn4egVziSRuwZjP7z48ju7wFpR3eAH2BlRDf6aHTPKYPyT+rc+fQgmwdu/oOVG/iFh/m3v3zfqfaOObtSBa8cl0VpqhTUiFA69VppAK1TAlUeYY8Y52LBVShEpyJFrpzihb187BbtJCTtwk/e3+O+3/STjdnu9/QkH1VfkugcAAA==";
		String testLogger = "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";
//      String testLogger = "H4sIAAAAAAAAAO1YS4/bVBT+K6NsumAe9+HnSCNxY9/El/pV+3qqlFZNOmOFSGlSJR7KqJoNErtBXSCoWLFDLBCwQVT8HlL1Z3Cuk2lsJ+0IxIJFvEq+79zzuuccH/lF62k+nw+Gubx8lreOWy6T7HHA05R1eWu/NX0+yWcAIwNZmqFhkxAT4PF02J1NL54BczR4Pj+anc+PRpN5MZic5UfP8/F4AkovBgXgCJ+fF2fF0TAHTYPx8nBazPLBUzi9VRZk5hdP5mez0bNiNJ10RuMin81bx5+2PhvNCcDzg2I2Gg7z2cHsYpwfwOHWo1Iv/zyfFEryRWt0DuqpYVkmoohqCJzXsG5Z1ECaTjA1TFMziG0TgjRsI1unNtWobmKqazZ4UIwgMcXgKcSIDV1DJjY0UKXv3yQM1BNEyAEyDhCWyDom6FgzD0HERvjBwwLsUQvt3bvIZ5cPi5TLPZbJyImCQMgTBBbyL4rZ4KzIzzujfHwOXr/4j1RfXe1vid+gEIJBLAv+69i2EVAW1WxqGTY2NWpC3Bo2bGobxvvjN26P39CxueFkFkNl8b2+2yYiTCXuH/YTftqOeZJGIfNl2+/v3faoQPuOxxIZRv2Tx09Gk8Hs8g5CBBtY1wzzzv5eX7hVrkOIZhEb25riujx0eVJhFdgWifRc1lvD2LYQ0oit2JAFvKZOWVKEz8JuVjmjsIAlieDuGlWg5L63RohJbciavWKi7YzDfb8SIPhPKdIQLbkoC2UP140sQVIHfcFZQy7kouu1o8SLooabjthUKjZUMtdNYDZUBBcvf1tcv1q8/vLNtz8urr9bfP3q7R+//vXn9ZtffqeUvv3+B/Lmp28qRxsKH4i4ooxQfQU2xHjARCUjBVRmMB+5g2Lw8XA6PRxOx0rqVMT9EwQ/Ei5CVW51JWBePOA3El2xKcH8KFwJ3MuYLzq9Og+VW60uBUWOkzUxaMSYhb167WiImAd48dXPi5evK0IqLY1rY6n8JGo3VGZJsgGuJNu8uxnKioOK3+RWyraeW3Fbz/kpUzqhZ4UqnzvoEKHKmQ2cB/GmkjRIa91WlqUn3uUblV0nxenqIribOfA3CutHyrZdlv02QjRh6M27HZ9112ioYHC7eXsqkigVmyY7LBB+T4Rwc42YwtJBCKBqFeGbonN56DTT7HHnbsJZqoygcm7xUML5pjcJV5436yuUzJHL2bmd8spCriUgciG/9ftQ8xve7HjNS1GNDevERpQSuhbIUp5UfbQRPKUCj8MY9wIWwuaQpGsJmJimQZbTRyY8jhIZ369Mn3nw0eUouIzLBkvvZTzpeSJdCq6lemWz+a7D0oqDaN1Jspm5OIk6MHDKi4ELlVsTVZ+0inAFl26SSa8WRMkEimtUmw+TVPbiRrJ5vfdLg3Dbki/zH2a+/w5aprwG3SR5BaZZHKvC60SN6lX+QKGysOGpA6M/lN2EudXLXCbYW+V4a4KhWjlLHK9hJ4V3LlcDgTUIJ4KJGUYbPc5OuYzA0nvbJFL5bQzXFFqlCzJZwqtu3boW7N0HXfzDm8G/W7d2m8xuk9ltMrtNZrfJ7DaZ3Saz22T+F5vMlq87BrEtA0pTN4lOoRF0U9d1Q0eWZRPbwMTUQYRoxELQBBZ9z9cdC1Hzw193LNiJ4OVON3ai5aenf75k3abw6urR1d9DoneoohQAAA==";
//      commitTrigger.handlerRequest(testLogger);
        
        HandlerGatherCommitTrigger2 commitTrigger2 = new HandlerGatherCommitTrigger2();
        commitTrigger2.handlerRequest("");
		
		
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

	public static Connection JDBCUtil() {
		Connection connection = null;
		String driverClass = null;
		String url = null;
		String name = null;
		String password = null;

		// 讀取jdbc.properties
		try {
			// 1.建立一個屬性配置物件
			Properties properties = new Properties();

			// 1.對應檔案位於工程根目錄
			// InputStream is = new FileInputStream("jdbc.properties");

			// 2.使用類載入器,讀取drc下的資原始檔 對應檔案位於src目錄底下 建議使用
			InputStream is = HandlerGatherCommitTrigger.class.getClassLoader().getResourceAsStream("jdbc.properties");
			// 2.匯入輸入流,抓取異常
			properties.load(is);
			// 3.讀取屬性
			driverClass = properties.getProperty("driverClass");
			url = properties.getProperty("url");
			name = properties.getProperty("name");
			password = properties.getProperty("password");
			
			
			// 2. 建立連線 引數一： 協議 + 訪問的資料庫 ， 引數二： 使用者名稱 ， 引數三： 密碼。
			try {
				Class.forName(driverClass);
				connection = DriverManager.getConnection(url, name, password);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return connection;
	}


}
