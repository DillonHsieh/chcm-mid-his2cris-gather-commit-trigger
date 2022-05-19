package chcm.mid.his2cris.gather.commit.trigger;

import java.io.BufferedReader;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/GetAwslogsServlet")
public class GetAwslogsServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

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
        
        String content = contentBuffer.toString();        
        if(content == null){
            content = "";
        }        
        System.out.println(content+"[END]");
        HandlerGatherCommitTrigger commitTrigger = new HandlerGatherCommitTrigger();
		commitTrigger.handlerRequest(content);
//		String testContent = "H4sIAAAAAAAAAO1Uy27aUBD9FeRNNjzu+4GUhbGd1BLYFBv1lSh24IpaIhDZl6RRxL93DKqctBDUqst4dWfOzJmZ49E8O3emqvKFSZ/ujdN3fDd1b0ZBkriXgdN21o8rU4IbCaSYYFgSIsG9XC8uy/XmHpBe/lj1ynnVK1aVzVcz03s0y+UKSMFcGEAQns/tzPYWBrjy5T49saXJ7yD/SDREVZvbalYW97ZYry6KpTVl5fS/Od+LioC76tiyWEBKp9wsTWeTW+d6xxw8mJWtI5+dYg4FqFBcK4kVZZozxjFHmMI0knPEBOZcIkaR5lJqiTAjACCuiYIObAHi2PwO5sSCE8WpwpIT3P4lGtATREgH8Q5WKRJ9RPtUdiEEan69shhjAZVaHzemfLqySZC23Gkae/FoFKbnCEqYH7bMZ9bMLwqznEPbz/+Le7ttH1BAChiAMMGRREhgIiWXGB5ME4pAHQo2p0wJyvhxBchpBQjV8s8up2NYsKCV+QMSRkmKs27mjaJBMIjdiT+MPTcN4ygdDLPWqa+eN/OGYRCloZ+d39wWq7x8OvN8tPvEWbuVBQ2OwYzcUdBEUoTrmHDcuDDqEtSVqIt36SP38zAEMffZSeqm02T/9oPEm4TjutnsPJoOh+CLJ5dR3HAhVlN4kwDmnSbB5CXmjsObl3itSYPWeiJMRIOn4egVziSRuwZjP7z48ju7wFpR3eAH2BlRDf6aHTPKYPyT+rc+fQgmwdu/oOVG/iFh/m3v3zfqfaOObtSBa8cl0VpqhTUiFA69VppAK1TAlUeYY8Y52LBVShEpyJFrpzihb187BbtJCTtwk/e3+O+3/STjdnu9/QkH1VfkugcAAA==";
//		commitTrigger.handlerRequest(testContent);
		
		
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
