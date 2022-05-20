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
        System.out.println(logger+"[END]");
        HandlerGatherCommitTrigger commitTrigger = new HandlerGatherCommitTrigger();
		commitTrigger.handlerRequest(logger);
//		String testLogger = "H4sIAAAAAAAAAO1Uy27aUBD9FeRNNjzu+4GUhbGd1BLYFBv1lSh24IpaIhDZl6RRxL93DKqctBDUqst4dWfOzJmZ49E8O3emqvKFSZ/ujdN3fDd1b0ZBkriXgdN21o8rU4IbCaSYYFgSIsG9XC8uy/XmHpBe/lj1ynnVK1aVzVcz03s0y+UKSMFcGEAQns/tzPYWBrjy5T49saXJ7yD/SDREVZvbalYW97ZYry6KpTVl5fS/Od+LioC76tiyWEBKp9wsTWeTW+d6xxw8mJWtI5+dYg4FqFBcK4kVZZozxjFHmMI0knPEBOZcIkaR5lJqiTAjACCuiYIObAHi2PwO5sSCE8WpwpIT3P4lGtATREgH8Q5WKRJ9RPtUdiEEan69shhjAZVaHzemfLqySZC23Gkae/FoFKbnCEqYH7bMZ9bMLwqznEPbz/+Le7ttH1BAChiAMMGRREhgIiWXGB5ME4pAHQo2p0wJyvhxBchpBQjV8s8up2NYsKCV+QMSRkmKs27mjaJBMIjdiT+MPTcN4ygdDLPWqa+eN/OGYRCloZ+d39wWq7x8OvN8tPvEWbuVBQ2OwYzcUdBEUoTrmHDcuDDqEtSVqIt36SP38zAEMffZSeqm02T/9oPEm4TjutnsPJoOh+CLJ5dR3HAhVlN4kwDmnSbB5CXmjsObl3itSYPWeiJMRIOn4egVziSRuwZjP7z48ju7wFpR3eAH2BlRDf6aHTPKYPyT+rc+fQgmwdu/oOVG/iFh/m3v3zfqfaOObtSBa8cl0VpqhTUiFA69VppAK1TAlUeYY8Y52LBVShEpyJFrpzihb187BbtJCTtwk/e3+O+3/STjdnu9/QkH1VfkugcAAA==";
//		commitTrigger.handlerRequest(testLogger);
		
		
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
