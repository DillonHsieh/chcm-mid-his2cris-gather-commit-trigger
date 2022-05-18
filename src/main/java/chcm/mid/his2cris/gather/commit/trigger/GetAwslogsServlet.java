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
		//response.getWriter().append("GetAwslog!");
		System.out.println("GetAwslog!");		
		//getFileAction.main("");
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
		HandlerGatherCommitTrigger.handleRequest(content);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
