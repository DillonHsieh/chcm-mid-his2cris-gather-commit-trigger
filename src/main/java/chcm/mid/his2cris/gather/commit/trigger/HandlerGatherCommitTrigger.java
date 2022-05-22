package chcm.mid.his2cris.gather.commit.trigger;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Base64.Decoder;
import java.util.Map.Entry;
import java.util.zip.GZIPInputStream;

import org.json.JSONArray;
import org.json.JSONObject;

import com.jayway.jsonpath.JsonPath;

import chcm.mid.his2cris.gather.commit.trigger.dto.CHCM;
import lombok.extern.slf4j.Slf4j;

//@Slf4j
public class HandlerGatherCommitTrigger{	
	CheckroomSQL roomSQL = new CheckroomSQL();
	CustomerSQL custSQL = new CustomerSQL();
	CustExamSQL examSQL = new CustExamSQL();
	
	Connection conn = null;
	Statement stmt = null;
	ResultSet resultSet = null;
	private static final String ReportTable = "RPTTREPORTTBL";
	//private static final String HIS_IP = "http://10.62.3.9";
	private static final String HIS_IP = "http://54.254.120.84";
	private static final String[] syncCenter = new String[] {"01", "04"};
	private static List<String> syncCenterList = Arrays.asList(syncCenter);
	//private static Map<String, List<CHCM>> commitMap = new HashMap<>();
	
	public String handlerRequest(String logger){
		Decoder decoder = Base64.getDecoder();
		byte[] decodedEvent = decoder.decode(logger);
		StringBuilder output = new StringBuilder();
		try {
			GZIPInputStream inputStream = new GZIPInputStream(new ByteArrayInputStream(decodedEvent));
	        InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
	        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
	        bufferedReader.lines().forEach( line -> {
	        	System.out.println(line);
	        	output.append(line);
	        });
	        List<Long> timestampList = JsonPath.read(output.toString(), "$.logEvents[*].timestamp");
	        List<String> messageList = JsonPath.read(output.toString(), "$.logEvents[*].message");
	        
	        try {
		        for (int i = 0; i < messageList.size(); i++) {
		        	String commit = getCommitKeyword(messageList.get(i));
		        	//log.info(messageList.get(i));
		        	String[] splitQuery = new String[2];
		        	String id = "";
		        	switch(commit) {
		        		case "0":
		        			splitQuery = messageList.get(i).split("Query");
			        		id = splitQuery[0].split("Z")[1].trim();
			        		GetAwslogsServlet.commitsMap.put(id, new ArrayList<CHCM>());
		        			break;
		        		case "1":
		        			splitQuery = messageList.get(i).split("Query");
			        		id = splitQuery[0].split("Z")[1].trim();
			        		if(GetAwslogsServlet.commitsMap.containsKey(id)) {
			        			//執行AUTOCOMMIT=0到COMMIT之間的資料
			        			syncUpdateDBTable(GetAwslogsServlet.commitsMap);
			        		}
		        			break;
		        		default:
		        			splitQuery = messageList.get(i).split("Query");
			        		id = splitQuery[0].split("Z")[1].trim();
			        		if(GetAwslogsServlet.commitsMap.containsKey(id)) {
			        			CHCM chcm = new CHCM();
			        			List<CHCM> chcmList = GetAwslogsServlet.commitsMap.get(id);
			        			String CRUD = getCRUDKeyword(messageList.get(i));
			        			chcm.setQueryID(id);
			        			chcm.setAction(CRUD);
			        			chcm.setTimestamp(timestampList.get(i));
			        			
			        			switch(CRUD) {
				        		  case "C":
				        			  chcm =saveCHCM4C(messageList.get(i));
					        		  chcmList = GetAwslogsServlet.commitsMap.get(id);
					        		  chcmList.add(chcm);
					        		  GetAwslogsServlet.commitsMap.put(id, chcmList);
				        			  break;
				        		  case "D":
				        			  chcm =saveCHCM4D(messageList.get(i));
				        			  if(chcm.getTable().equals(ReportTable)) {
				        				  chcmList = GetAwslogsServlet.commitsMap.get(id);
						        		  chcmList.add(chcm);
				        			  }
				        			  break;
			        			  default:
			        				  chcm =saveCHCM4U(messageList.get(i));
					        		  chcmList = GetAwslogsServlet.commitsMap.get(id);
					        		  chcmList.add(chcm);
					        		  GetAwslogsServlet.commitsMap.put(id, chcmList);
			        			}		        			
			        			
			        		}  				  
		        	}		        	
		        }
	        }catch(Exception e) {
	        	System.out.println(e.getMessage());
	        }finally {
	        	  close(conn, resultSet, stmt);
	        	  System.out.println("關閉Connection...");
	        }	        
		} catch(IOException e) {
        	System.out.println("ERROR: " + e.toString());
        }
		return "";
	}
	
	private String getCommitKeyword(String message) {
		if(message.contains("AUTOCOMMIT=0"))
			return "0";
		if(message.contains("COMMIT"))
			return "1";
		return "data";
	}
	
	private String getCRUDKeyword(String message) {
		if(message.contains("INSERT INTO"))
			return "C";
		if(message.contains("DELETE"))
			return "D";
		return "U";
	}
	
	enum SyncAction {
    	CREATE, UPDATE, SYNC, DELETE, PERSON_UPDATE
    }
	
	/*
     *拆解AWS_logEvents_message中的Data 
     * */
	private CHCM saveCHCM4C(String message) {
		CHCM chcm = new CHCM();
    	String[] splitFristBracket = message.split("\\("); //insert有兩個(
    	chcm.setTable(splitFristBracket[0].split("`DB2INST1`.")[1].replaceAll("`", "").trim());
    	String[] values = splitFristBracket[2].split(",_binary");
    	   	
    	switch(chcm.getTable()) {
    	case "REVTBOOKDATATBL": //五、客戶資料新增同步
    		chcm.setCaseNo(values[0].trim());
			break;
		case "RPTTREPORTTBL": //六、客戶檢查項目新增同步
			values = splitFristBracket[2].split(",");
			chcm.setCaseNo(values[0].trim());
			chcm.setExamItem(values[1].replaceAll("_binary", "").replaceAll("'", "").trim());
    	}
		return chcm;
    }
	
	private CHCM saveCHCM4D(String message) {
		CHCM chcm = new CHCM();
		String[] splitWhere = message.split("WHERE");
		chcm.setTable(splitWhere[0].split("`DB2INST1`.")[1].replaceAll("`", "").trim());
    	if(chcm.getTable().equals(ReportTable)) {//只刪除六、客戶檢查項目
	    	String[] values = splitWhere[1].split("AND");
	    	chcm.setCaseNo(values[0].split("=")[1].replaceAll("'", "").trim());
	    	chcm.setExamItem(values[1].split("=")[1].replaceAll("_binary", "").replaceAll("'", "").trim());
		}
		return chcm;
    }
	
	private CHCM saveCHCM4U(String message) {
		CHCM chcm = new CHCM();

    	String[] splitWhere = message.split("WHERE");
	  	String[] splitSet = splitWhere[0].split("SET");
	  	chcm.setTable(splitSet[0].split("`DB2INST1`.")[1].replaceAll("`", "").trim());
    	switch(chcm.getTable()) {
    		case "CMNBEBOARDLOCATIONTBL":
    			String[] splitAnd = splitWhere[1].split("AND");
    			String[] splitMAXLIMIT = splitWhere[0].split(", `MAXLIMIT`");
	        	String[] splitIP= splitMAXLIMIT[0].split("`IP`");
	        	String[] splitSTATUS = splitMAXLIMIT[1].split("`STATUS`=");
	        	chcm.setClientId(splitAnd[0].split("_binary")[1].replaceAll("'", "").trim());
	        	chcm.setOrgNo(splitAnd[1].split("_binary")[1].replaceAll("'", "").trim());
	        	chcm.setIP(splitIP[1].split("_binary")[1].replaceAll("'", "").trim());
	        	chcm.setStatus(splitSTATUS[1].split(", `DESCRIPTION`")[0]);
    			break;
    		case "CMNBLOCATIONTBL":
    			String[] str2_1 = splitWhere[1].split("AND");
    			chcm.setUno(str2_1[0].split("_binary")[1].replaceAll("'", "").trim());
    			chcm.setOrgNo(str2_1[1].split("_binary")[1].replaceAll("'", "").trim());
    			break;
    		case "CMNBDOCTORTBL":
    			String[] str3_1 = splitWhere[1].split("AND");
    			chcm.setUID(str3_1[0].split("_binary")[1].replaceAll("'", "").trim());
    			chcm.setOrgNo(str3_1[1].split("_binary")[1].replaceAll("'", "").trim());
    			break;
    		case "REVTBOOKDATATBL":
    			String[] bookCaseNo = splitWhere[1].split("`CASENO`=");
    			chcm.setCaseNo(bookCaseNo[1]);
    			break;
    		case "REVBPERSONALTBL":
    			String[] personChartNo = splitWhere[1].split("`CHARTNO`=");
    			chcm.setChartNo(personChartNo[1].split("_binary")[1].replaceAll("'", "").trim());
    			break;
    		case "REVTBOOKDATA2TBL":
    			String[] book2CaseNo = splitWhere[1].split("`CASENO`=");
    			chcm.setCaseNo(book2CaseNo[1]);
    			break;
    		case "REVBPERSONALSUBTBL":
    			String[] personSubChartNo = splitWhere[1].split("`CHARTNO`=");
    			chcm.setChartNo(personSubChartNo[1].split("_binary")[1].replaceAll("'", "").trim());
    			break;
    		case "RPTTREPORTTBL":
				String[] splitCompleteFlag = splitWhere[0].split(", `COMPLETEFLAG`");
    			String[] spliteCancelFlag = splitCompleteFlag[0].split("`CANCELFLAG`=_binary");
    			chcm.setCancelVal(spliteCancelFlag[1].replaceAll("'", ""));
    			String[] andGroup = splitWhere[1].split("AND");
    			chcm.setCaseNo(andGroup[0].split("`CASENO`=")[1].trim());
    			chcm.setExamItem(andGroup[1].split("=_binary")[1].replaceAll("'", "").trim());
    			break;
    	}
    	
		return chcm;
    }
	
	private String syncUpdateDBTable(Map<String, List<CHCM>> commitMap) {
		Set<String> ids = commitMap.keySet();
		for(String id : ids) {
			List<CHCM> chcmList = commitMap.get(id);
			for(CHCM chcm:chcmList) {
				switch(chcm.getTable()) {
	    			case "CMNBEBOARDLOCATIONTBL":
			        	if(syncCenterList.contains(chcm.getOrgNo())) {
			        		//符合同步中心的才同步
			        		System.out.println("三、檢查室資料監測資料表-1 GET_TABLE, CLIENTID, STATUS, ORGNO: " 
			        	        	+ chcm.getTable() + ", " + chcm.getClientId() + ", " + chcm.getStatus() + ", " + chcm.getOrgNo());
				        	if(syncCheckroomData3_1(chcm.getOrgNo(), chcm.getClientId(), 1, chcm.getStatus()) != null) {
				        		//開診時才檢查室項目同步
				        		if(chcm.getStatus().equals("1")){
				        			System.out.println("四、檢查室項目同步監測資料表 GET_TABLE, STATUS, ORGNO, IP: " 
					        	        	+ chcm.getTable() + ", " + chcm.getStatus() + ", " + chcm.getOrgNo() + ", " + chcm.getIP());
				        			syncCheckroomItem4(chcm.getOrgNo(), chcm.getIP());
				        		}
				        	}
			        	}
	    				break;
	        		case "CMNBLOCATIONTBL":
	    	        	if(syncCenterList.contains(chcm.getOrgNo())) {
	    	        		//符合同步中心的才同步
	    	        		System.out.println("三、檢查室資料監測資料表-2 GET_TABLE, ORGNO, UNo: "
	    		        	+ chcm.getTable() + ", " + chcm.getOrgNo() + ", " + chcm.getUno());
	    	    			syncCheckroomData3_2(chcm.getOrgNo(), chcm.getUno());
	    	        	}
	        			break;
	        		case "CMNBDOCTORTBL":
	    	        	if(syncCenterList.contains(chcm.getOrgNo())) {
	    	        		//符合同步中心的才同步
	    	        		System.out.println("三、檢查室資料監測資料表-3 GET_TABLE, ORGNO, UID: "
	    		        	+ chcm.getTable() + ", " + chcm.getOrgNo() + ", " + chcm.getUID());
	    	    			syncCheckroomData3_3(chcm.getOrgNo(), chcm.getUID());
	    	        	}
	        			break;
	        		case "REVTBOOKDATATBL":
	        			if(chcm.getAction().equals("C")) {
	        				System.out.println("五、客戶資料新增同步 GET_TABLE, CASENO: " + chcm.getTable() + ", " + chcm.getCaseNo());
	        				syncCustomerDataChanged("C", chcm.getCaseNo());
	        			}else if(chcm.getAction().equals("U")) {
	        				System.out.println("五、客戶資料異動同步 GET_TABLE, CASENO: " + chcm.getTable() + ", " + chcm.getCaseNo());
	        				syncCustomerDataChanged("U", chcm.getCaseNo());
	        			}
	        			break;
	        		case "REVBPERSONALTBL":
	        			System.out.println("五、客戶資料異動同步 GET_TABLE, CHARTNO: " + chcm.getTable() + ", " + chcm.getChartNo());
	        			syncCustomerDataChanged("PU", chcm.getChartNo());
	        			break;
	        		case "REVTBOOKDATA2TBL":
	        			System.out.println("五、客戶資料異動同步 GET_TABLE, CASENO: " + chcm.getTable() + ", " + chcm.getCaseNo());
	        			syncCustomerDataChanged("U", chcm.getCaseNo());
	        			break;
	        		case "REVBPERSONALSUBTBL":
	        			System.out.println("五、客戶資料異動同步 GET_TABLE, CHARTNO: " + chcm.getTable() + ", " + chcm.getChartNo());
	        			syncCustomerDataChanged("PU", chcm.getChartNo());
	        		case "RPTTREPORTTBL":
	        			if(chcm.getCancelVal().equals("Y")) {
	        				//cancelFlag=Y，刪除
	        				triggerDeleteDBTable("RPTTREPORTTBL", chcm.getCaseNo(), chcm.getExamItem());
	        				break;
	        			}
	        			System.out.println("六、客戶檢查項目異動同步 GET_TABLE, CASENO, EXAMITEM: " + chcm.getTable() + ", " + chcm.getCaseNo() + ", " + chcm.getExamItem());
	        			syncCustomerExamineItems("U", chcm.getCaseNo(), chcm.getExamItem());
	        			break;
      		  		default:
  				}
			}
		}
		
		return "";
	}
	
    private void triggerDeleteDBTable(String table, String caseNo, String examItem) {
    	System.out.println("六、客戶檢查項目刪除同步 GET_TABLE, CASENO, EXAMITEM: " + table + ", " + caseNo + ", " + examItem);
		syncCustomerExamineItems("D", caseNo, examItem);
    }
	
    /*
     *三、	檢查室資料同步(1~3)
     * */
    public String syncCheckroomData3_1(String orgNo, String ClientID
    		, int index, String status) {
    	String strSql = roomSQL.get3_1CheckroomData(orgNo, ClientID);
    	System.out.println("get3_1CheckroomDataSQL: " + strSql );
    	JSONObject data2Cris = getCheckroomJSON(strSql, orgNo, "監測資料表-1", index, status);
    	if(data2Cris == null)
    		return null;
    	URL url = getURL("syncMonitorData");
    	return postData2CrisAPI(url, data2Cris);
    }
    
    public void syncCheckroomData3_2(String orgNo, String Uno) {
    	String strSql = roomSQL.get3_2CheckroomData(orgNo, Uno);
    	System.out.println("get3_2CheckroomDataSQL: " + strSql );
    	JSONObject data2Cris = getCheckroomJSON(strSql, orgNo, "監測資料表-2", 2, "");
    	if(data2Cris != null) {
	    	URL url = getURL("syncMonitorData");
	    	postData2CrisAPI(url, data2Cris);
    	}
    }
    public void syncCheckroomData3_3(String orgNo, String UID) {
    	String strSql = roomSQL.get3_3CheckroomData(orgNo, UID);
    	System.out.println("get3_3CheckroomDataSQL: " + strSql );
    	JSONObject data2Cris = getCheckroomJSON(strSql, orgNo, "監測資料表-3", 3, "");
    	if(data2Cris != null) {
	    	URL url = getURL("syncMonitorData");
	    	postData2CrisAPI(url, data2Cris);
    	}
    }
    
    /*
     * 肆、 檢查室項目同步
     * */
    public void syncCheckroomItem4(String orgNo, String IP) {
    	String strSql = roomSQL.get4CheckroomItem(orgNo, IP);
    	System.out.println("get4CheckroomItemSQL: " + strSql );
    	JSONObject data2Cris = getRoomItemData(strSql, orgNo, "檢查室項目");
    	if(data2Cris != null) {
	    	URL url = getURL("syncMonitorItem");
	    	postData2CrisAPI(url, data2Cris);
    	}
    }
    
	
	public URL getURL(String purpose) {
		StringBuilder sb = new StringBuilder(HIS_IP);
		switch(purpose) {
			case "syncCustomerDataChanged":
				sb.append("/api/HCMAPI/customer");
				break;
			case "syncCustomerExamItem":
				sb.append("/api/HCMAPI/customerexam");
				break;
			case "syncMonitorData":
				sb.append("/api/HCMAPI/room");
				break;
			case "syncMonitorItem":
				sb.append("/api/HCMapi/roomexam");
				break;
			default:
		}
		
		try {
			return new URL(sb.toString());
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
    /*
     * 五、	客戶資料同步
     * */
    public void syncCustomerDataChanged(String action, String no) {
    	String strSql = null;
    	JSONObject data2Cris = null;
    	URL url = getURL("syncCustomerDataChanged");
    	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
    	String today = LocalDate.now( ZoneId.of( "Asia/Taipei" ) ).format(formatter);
    	
    	switch(action) {
    		case "C":
    			strSql = getSqlStr(SyncAction.CREATE, no);
    			System.out.println("syncCustomerDataChanged_C: " + strSql );
    			data2Cris = getCustData(SyncAction.CREATE, strSql);
    			break;
    		case "U":
    			strSql = custSQL.getCustomerUpdate2_1(today, no); //getSqlStr(SyncAction.UPDATE, no);
    			System.out.println("syncCustomerDataChanged_U: " + strSql );
    			data2Cris = getCustData(SyncAction.UPDATE, strSql);
    			break;
    		case "PU":
    			//personTable
    			strSql = custSQL.getCustomerUpdate2_2(today, no); //getSqlStr(SyncAction.PERSON_UPDATE, no);
    			System.out.println("syncCustomerDataChanged_Person_U: " + strSql );
    			data2Cris = getCustData(SyncAction.UPDATE, strSql);
    			break;
    	}
    	//JSONObject沒有資料就不執行API
    	if(data2Cris != null)
    		postData2CrisAPI(url, data2Cris);
    }
    
    /*
     * 六、	客戶檢查項目同步
     * */
    public void syncCustomerExamineItems(String action, String caseNo, String examItem) {
    	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
    	String today = LocalDate.now( ZoneId.of( "Asia/Taipei" ) ).format(formatter);
    	String strSql = examSQL.getExamine(today, caseNo, examItem); //getExamine(today, caseNo, examItem);
    	System.out.println("syncCustomerExamineItems_"+action+": " + strSql );
    	JSONObject data2Cris = null;
    	URL url = getURL("syncCustomerExamItem");
    	
    	switch(action) {
    		case "C":
    			data2Cris = getExamineItem(SyncAction.CREATE, strSql, caseNo);
    			break;
    		case "U":
    			data2Cris = getExamineItem(SyncAction.UPDATE, strSql, caseNo);
    			break;
    		default:
    			data2Cris = getExamineItem(SyncAction.DELETE, strSql, caseNo);
    	}
    	//JSONObject沒有資料就不執行API
    	if(data2Cris != null)
    		postData2CrisAPI(url, data2Cris);
    }
    	
	private JSONObject getCheckroomJSON(String strSql, String orgNo
			, String desc, int index, String status) {
    	try {
    		if(conn == null) {
    			conn = GetAwslogsServlet.JDBCUtil();
    			System.out.println("建立一個connection...");
    		}
    		stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
    		System.out.println("開始擷取檢查室資料同步-" + desc + " 相關資料......");
            resultSet = stmt.executeQuery(strSql);
            return getCheckroomDataFromHIS(resultSet, orgNo, index, status);
    	}catch (Exception e) {
            e.printStackTrace();
            System.out.println("Caught exception: " + e.getMessage());
            return null;
        } 
    	finally {
    		closeRS(resultSet);
    		closeSt(stmt);
        }
	}
	
	private JSONObject getRoomItemData(String strSql, String orgNo, String desc) {
    	try {
    		if(conn == null) {
    			conn = GetAwslogsServlet.JDBCUtil();
    			System.out.println("建立一個connection...");
    		}
    		stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
    		System.out.println("開始擷取檢查室資料同步-" + desc + " 相關資料......");
            resultSet = stmt.executeQuery(strSql);
            return getCheckroomItemFromHIS2(resultSet, orgNo);
    	}catch (Exception e) {
            e.printStackTrace();
            System.out.println("Caught exception: " + e.getMessage());
            return null;
        } finally {
        	closeRS(resultSet);
    		closeSt(stmt);
        }
	}
	
	private JSONObject getCheckroomDataFromHIS(ResultSet rs
			, String OrgNo, int index, String status) {
		JSONObject jsonObj = new JSONObject();
		try {
			jsonObj.put("CenterNo", OrgNo);
			JSONArray roomArr = new JSONArray();
			while(rs.next()) {
				JSONObject roomObj = new JSONObject();
				roomObj.put("RoomNo", rs.getString(2));
				roomObj.put("RealRoomNo", rs.getString(3));
				roomObj.put("RoomName", rs.getString(4));
				roomObj.put("FloorNo", rs.getString(5));
				roomObj.put("AreaNo", rs.getString(6));
				roomObj.put("CapacityCount", rs.getInt(7));
				roomObj.put("RoomType", rs.getInt(8));
				roomObj.put("ClientID", rs.getString(9));
				roomObj.put("EndDate", 
						(rs.getString(10)==null||rs.getString(10).length()==0)
						? "" : rs.getString(10)
						);
				roomObj.put("DoctorSex", rs.getString(11));
				roomObj.put("CurrentDoctorNo", rs.getString(12));
				roomObj.put("ReportDoctorNo", rs.getString(13));
				roomObj.put("SexConstraint", rs.getString(14));
				roomObj.put("Distribution", rs.getString(15));
				switch(index) {
					case 1:
						roomObj.put("Status", Integer.parseInt(status));
						break;
					default:
						roomObj.put("Status", rs.getInt(16));
				}
				roomObj.put("ExamTakeTime", rs.getInt(17));
				roomObj.put("PrearrangedTime", rs.getInt(18));
				roomArr.put(roomObj);
			}
			if(roomArr.isNull(0)) {
				System.out.println("檢查室資料同步(之"+index+")SQL查詢結果為空");
				return null;
			}
			jsonObj.put("Rooms", roomArr);
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Caught exception: " + e.getMessage());
			return null;
		}
		
		return jsonObj;
	}
	
    public String getSqlStr(SyncAction syncAction, String no) {
    	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
    	String today = LocalDate.now( ZoneId.of( "Asia/Taipei" ) ).format(formatter);
    	switch(syncAction) {
    		case CREATE:
    			return custSQL.getCreate(today, no);
    		case UPDATE:
    			return custSQL.getCustomerUpdate2_1(today, no);
    		case PERSON_UPDATE:
    			return custSQL.getCustomerUpdate2_2(today, no);
			default:
				return "";
    	}
    }
    
	private JSONObject getCustData(SyncAction syncAction, String strSql) {
    	try {
    		if(conn == null) {
    			conn = GetAwslogsServlet.JDBCUtil();
    			System.out.println("建立一個connection...");
    		}
    		stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
    		System.out.println("開始擷取客戶資料Action: "+syncAction.name()+" 相關資料......");
            resultSet = stmt.executeQuery(strSql);
            switch(syncAction) {
            	case CREATE:
            		return getCustDataFromHIS(resultSet, "C");
            	case UPDATE:
            		return getCustDataFromHIS(resultSet, "U");
        		default:
            		return null;
            }
            
    	}catch (Exception e) {
            e.printStackTrace();
            System.out.println("Caught exception: " + e.getMessage());
            return new JSONObject();
        } finally {
        	closeRS(resultSet);
    		closeSt(stmt);
        }
    }
	
	private JSONObject getCustDataFromHIS(ResultSet rs, String action) {
		JSONObject jsonObj = new JSONObject();
		try {
			jsonObj.put("Action", action);
			JSONArray jsonArr = new JSONArray();
			while(rs.next()) {
				String orgNo = rs.getString(1);
				//不符合要同步的中心就跳出
				if( !syncCenterList.contains(orgNo)) {
					System.out.println("五、客戶資料: 非同步的中心別：" + orgNo);
					break;
				}
				jsonObj.put("CenterNo", orgNo);
				JSONObject custObj = new JSONObject();
				custObj.put("CustomerNo", rs.getString(2));
				custObj.put("ChartNo", rs.getString(3));
				custObj.put("IdentificationNo", (rs.getString(4)==null)?"":rs.getString(4));
				custObj.put("CustomerName", rs.getString(5));
				custObj.put("CustomerNote", rs.getString(6));
				custObj.put("AppointmentNote", rs.getString(7));
				custObj.put("Sex", rs.getString(8));
				custObj.put("Birthday", rs.getString(9));
				custObj.put("Age", rs.getString(10));
				custObj.put("VIPFlag", rs.getString(11));
				custObj.put("IsPregnant", rs.getString(12));
				custObj.put("IsDiabetes", rs.getString(13));
				custObj.put("DayType", rs.getString(14));
				custObj.put("ArriveDate", rs.getString(15));
				custObj.put("BookingDate", rs.getString(16));
				custObj.put("OriginalDate", rs.getString(17));
				custObj.put("IsRedo", rs.getString(18));
				custObj.put("ExamTypeNo", rs.getString(19));
				custObj.put("ExamTypeName", rs.getString(20));
				custObj.put("ItemName", rs.getString(21));
				custObj.put("GynecologySex", rs.getString(22));
				custObj.put("CustomerCompany", rs.getString(23));
				custObj.put("TimePeriod", rs.getString(24));
				int expense = rs.getInt(25);
				custObj.put("Expense", String.valueOf(expense));
				custObj.put("Address", rs.getString(26));
				custObj.put("Email", rs.getString(27));
				custObj.put("TEL", rs.getString(28));
				jsonArr.put(custObj);
			}
			//沒裝資料就return null
			if(jsonArr.isNull(0)) {
				System.out.println("五、客戶資料SQL查詢結果為空");
				return null;
			}
			jsonObj.put("Customers", jsonArr);
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Caught exception: " + e.getMessage());
			System.out.println(e.getMessage());
		}
		
		return jsonObj;
	}
	
	private JSONObject getExamineItem(SyncAction syncAction, String strSql, String caseNo) {
    	try {
    		if(conn == null) {
    			conn = GetAwslogsServlet.JDBCUtil();
    			System.out.println("建立一個connection...");
    		}
    		stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
    		System.out.println("開始擷取客戶檢查項目Action: "+syncAction.name()+" 相關資料......");
            resultSet = stmt.executeQuery(strSql);
            System.out.println("完成地端客戶檢查項目取得");
            switch(syncAction) {
            	case CREATE:
            		return getCustExamItemFromAWS(resultSet, "C", caseNo);
            	case UPDATE:
            		return getCustExamItemFromAWS(resultSet, "U", caseNo);
            	case SYNC:
            		return getCustExamItemFromAWS(resultSet, "S", caseNo);
            	case DELETE:
            		return getCustExamItemFromAWS(resultSet, "D", caseNo);
        		default:
            		return null;
            }
            
    	}catch (Exception e) {
            e.printStackTrace();
            System.out.println("Caught exception: " + e.getMessage());
            return new JSONObject();
        } finally {
        	closeRS(resultSet);
    		closeSt(stmt);
        }
	}
	
	private JSONObject getCustExamItemFromAWS(ResultSet rs
			, String action, String caseNo) {
		JSONObject jsonObj = new JSONObject();
		try {
			jsonObj.put("Action", action);
			
			//先裝Customers陣列
			JSONArray custArr = new JSONArray();
			JSONObject custObj = new JSONObject();
			custObj.put("CustomerNo", caseNo);
			//檢查項目可能多筆
			JSONArray examArr = new JSONArray();
			while(rs.next()) {
				JSONObject examObj = new JSONObject();
				examObj.put("ExamNo", rs.getString(3));
				examObj.put("ExamSeq", 0);
				examObj.put("Status", rs.getString(4));
				examObj.put("CheckInDate", 
						(rs.getString(5)==null||rs.getString(5).length()==0)
						? JSONObject.NULL : rs.getString(5)
						);
				examObj.put("EndDate", 
						(rs.getString(6)==null||rs.getString(6).length()==0)
						? JSONObject.NULL : rs.getString(6)
						);
				examObj.put("RoomNo", 
						(rs.getString(7)==null||rs.getString(7).length()==0)
						? JSONObject.NULL : rs.getString(7)
						);
				examArr.put(examObj);
			}
			if(examArr.isNull(0)) {
				System.out.println("六、客戶檢查項目SQL查詢結果為空");
				return null;
			}
			custObj.put("Exams", examArr);
			custArr.put(custObj);
			jsonObj.put("Customers", custArr);
			//rs回到初始，裝入中心代號
			rs.beforeFirst();
			while(rs.next()) {
				String orgNo = rs.getString(1);
				//不符合要同步的中心就return null
				if(!syncCenterList.contains(orgNo)) {
					System.out.println("六、客戶檢查項目: 非同步的中心別：" + orgNo);
					return null;
				}
				jsonObj.put("CenterNo", orgNo);
				break;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Caught exception: " + e.getMessage());
			return null;
		}
		
		return jsonObj;
	}
	
	private String postData2CrisAPI(URL url, JSONObject data2Cris) {
        StringBuffer sb = new StringBuffer("");
        System.out.println("即將發出資料:" + data2Cris.toString());
        System.out.println("開始連線至快思API..." + url.toString());
        try {
            HttpURLConnection connection;
            connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setRequestMethod("POST");
            connection.setUseCaches(false);
            connection.setInstanceFollowRedirects(true);
            connection.setRequestProperty("Content-Type","application/json; charset=UTF-8");            
            connection.connect();
            System.out.println("連線至API成功......");
            DataOutputStream out = new DataOutputStream(connection.getOutputStream());
            out.write(data2Cris.toString().getBytes("UTF-8"));//這樣可以處理中文亂碼問題
            out.flush();
            out.close();
            
            
            int statusCode = connection.getResponseCode();

            InputStream is = null;

            if (statusCode >= 200 && statusCode < 400) {
               // Create an InputStream in order to extract the response object
               is = connection.getInputStream();
            }
            else {
               is = connection.getErrorStream();
            }
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader reader = new BufferedReader(isr);
            String lines;
            while ((lines = reader.readLine()) != null) { 
                lines = new String(lines.getBytes(), "utf-8");
                System.out.println("顯示呼叫結果:" + lines);
                sb.append(lines);
            }
            reader.close();
            // 斷開連線
            connection.disconnect();
            System.out.println("API連線已斷開......");
        } catch (MalformedURLException e) {
            e.printStackTrace();
            System.out.println("Caught exception: " + e.getMessage());
            return null;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            System.out.println("Caught exception: " + e.getMessage());
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Caught exception: " + e.getMessage());
            return null;
        }
        return sb.toString();
    }
	
	private JSONObject getCheckroomItemFromHIS2(ResultSet rs
			, String orgNo) {
		JSONObject jsonObj = new JSONObject();
		try {
			rs.last();
//			context.getLogger().log("四、檢查室項目總筆數"+resultSet.getRow());
		    rs.beforeFirst();
			Map<String, List<String>> rdeMap = new HashMap<>();
			Map<String, Map<String, List<String>>> roomMap = new HashMap<>();
			Map<String, Map<String, Map<String, List<String>>>> centerMap = new HashMap<>();
			List<String> examNoList = null;
			String roomNo = null;
			while(rs.next()) {
				//房號
				roomNo = rs.getString(2);
			    //全半日＋類別編號
			    String dayType = rs.getString(3);
			    String examTypeNo = rs.getString(4);
			    String examNo = rs.getString(5);
			    String key = roomNo+","+dayType+","+examTypeNo;
			    if(rdeMap.containsKey(key)) {
			    	examNoList = rdeMap.get(key);
			    	examNoList.add(examNo);
			    	rdeMap.put(key, examNoList);
			    }else {
			    	examNoList = new ArrayList<>();
			    	examNoList.add(examNo);
			    	rdeMap.put(key, examNoList);
			    }
			}
			roomMap.put(roomNo, rdeMap);
			centerMap.put(orgNo, roomMap);
//			context.getLogger().log("CenterMap: " + centerMap);
			Set<String> centers = centerMap.keySet();
			for(String centerNo : centers) {
				jsonObj.put("CenterNo", centerNo);
				roomMap = centerMap.get(centerNo);
				Set<String> roomNos = roomMap.keySet();
				JSONArray roomsArr = new JSONArray();
				for(String room : roomNos) {
					JSONObject roomObj = new JSONObject();
					roomObj.put("RoomNo", room);
					rdeMap = roomMap.get(room);
					JSONArray cateArr = new JSONArray();
					for(Entry<String, List<String>> entry : rdeMap.entrySet()) {
						JSONObject cateObj = new JSONObject();
						String[] strRDE = entry.getKey().split(",");
						String dayType = strRDE[1];
						String examType = strRDE[2];
						cateObj.put("DayType", dayType);
						cateObj.put("ExamTypeNo", examType);
						examNoList = rdeMap.get(roomNo+","+dayType+","+examType);//entry.getKey()
						JSONArray examArr = new JSONArray();
						for(String examNo : examNoList) {
							JSONObject examObj = new JSONObject();
							examObj.put("ExamNo", examNo);
							examArr.put(examObj);
						}
						cateObj.put("Exams", examArr);
						cateArr.put(cateObj);
					}
					roomObj.put("Cate", cateArr);
					roomsArr.put(roomObj);
				}
				if(roomsArr.isNull(0)) {
					System.out.println("四、檢查室項目SQL查詢結果為空");
					return null;
				}
				jsonObj.put("Rooms", roomsArr);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Caught exception: " + e.getMessage());
			return null;
		}
		
		return jsonObj;
	}
	

	
	/**
	 * 釋放資源
	 * <p>
	 * Title: close
	 * </p>
	 * <p>
	 * Description:
	 * </p>
	 * 
	 * @param connection
	 * @param resultSet
	 * @param statement
	 */
	private void close(Connection connection, ResultSet resultSet, Statement statement) {
		closeRS(resultSet);
		closeSt(statement);
		closeConn(connection);

	}
	
	private void close(Connection connection, Statement statement) {
		closeSt(statement);
		closeConn(connection);

	}
	
	private static void closeRS(ResultSet resultSet) {
		try {
			if (resultSet != null) {
				resultSet.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			resultSet = null;
		}
	}
	
	private static void closeSt(Statement statement) {
		try {
			if (statement != null) {
				statement.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			statement = null;
		}
	}

	private static void closeConn(Connection connection) {
		try {
			if (connection != null) {
				connection.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			connection = null;
		}
	}

}
