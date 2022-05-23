package chcm.mid.his2cris.gather.commit.trigger;

public class CheckroomSQL {

	public String get3_1CheckroomData(String OrgNo, String ClientID) {
		return  "SELECT A.ORGNO, E.UNO LOCNO,A.ROOMNO,E.LOCname LocNameonEBoard,A.FLOOR,A.AREA,\n"
				+ "		  CASE WHEN E.MAXLIMIT is null THEN 1 ELSE E.MAXLIMIT END MAXLIMIT,\n"
				+ "       CASE WHEN (Trim(E.LocType) = '' or E.LocType IS NULL) THEN '1' ELSE E.LocType END LocType,\n"
				+ "       B.CLIENTID, E.LastScheduleTime,               \n"
				+ "       CASE WHEN (Trim(F.GENDER) = '' or F.GENDER IS NULL) THEN ' ' ELSE F.GENDER END DRGender,        \n"
				+ "       D.EXAMDRNO, D.REPORTDRNO, \n"
				+ "       CASE WHEN (Trim(E.GENDER) = '' or E.GENDER IS NULL) THEN '' ELSE E.GENDER END LocGender,\n"
				+ "       CASE WHEN (Trim(F.ASSIGNRULE) = '' or F.ASSIGNRULE IS NULL) THEN '2' ELSE F.ASSIGNRULE END ASSIGNRULE,\n"
				+ "       C.STATUS ROOMSTATUS,\n"
				+ "       CASE WHEN (F.ExamAvgTime IS NULL) THEN E.ExamAvgTime ELSE F.ExamAvgTime END ExamAvgTime,\n"
				+ "       CASE WHEN (F.ExamIntervalTime IS NULL) THEN E.ExamIntervalTime ELSE F.ExamIntervalTime END ExamIntervalTime\n"
				+ " FROM  (SELECT X.*\n"
				+ "	      FROM DB2INST1.CMNBEBOARDDEVICETBL X JOIN DB2INST1.CMNBEBOARDLAYOUTTBL Y on X.ORGNO = Y.ORGNO AND X.LayoutId = Y.LayoutId\n" 
				+"	      WHERE Y.TYPE ='2') A LEFT JOIN DB2INST1.CMNBEBOARDLOCATIONMAPTBL B ON A.ORGNO = B.ORGNO AND A.DEVICEID = B.DEVICEID\n"
				+"	                           LEFT JOIN DB2INST1.CMNBEBOARDLOCATIONTBL C ON B.ORGNO = C.ORGNO AND B.CLIENTID = C.CLIENTID\n"
				+"	                           LEFT JOIN DB2INST1.CMNBLOCATIONIPTBL D ON C.ORGNO = D.ORGNO AND C.IP = D.IP\n"
				+"	                           LEFT JOIN DB2INST1.CMNBLOCATIONTBL E ON D.ORGNO = E.ORGNO AND D.LOCNO = E.UNO\n"
				+"	                           LEFT JOIN DB2INST1.CMNBDOCTORTBL F ON D.ORGNO = F.ORGNO AND D.EXAMDRNO = F.UID\n"
				+ " WHERE C.ORGNO = '" + OrgNo + "'\n"
				+ "  AND C.CLIENTID = '" + ClientID + "'\n";
	}
	
	public String get3_2CheckroomData(String OrgNo, String Uno) {
		return "SELECT A.ORGNO, E.UNO LOCNO,A.ROOMNO,E.LOCname LocNameonEBoard,A.FLOOR,A.AREA,\n"
				+ "		  CASE WHEN E.MAXLIMIT is null THEN 1 ELSE E.MAXLIMIT END MAXLIMIT,\n"
				+ "       CASE WHEN (Trim(E.LocType) = '' or E.LocType IS NULL) THEN '1' ELSE E.LocType END LocType,\n"
				+ "       B.CLIENTID, E.LastScheduleTime,               \n"
				+ "       CASE WHEN (Trim(F.GENDER) = '' or F.GENDER IS NULL) THEN ' ' ELSE F.GENDER END DRGender,        \n"
				+ "       D.EXAMDRNO, D.REPORTDRNO, \n"
				+ "       CASE WHEN (Trim(E.GENDER) = '' or E.GENDER IS NULL) THEN '' ELSE E.GENDER END LocGender,\n"
				+ "       CASE WHEN (Trim(F.ASSIGNRULE) = '' or F.ASSIGNRULE IS NULL) THEN '2' ELSE F.ASSIGNRULE END ASSIGNRULE,\n"
				+ "       C.STATUS ROOMSTATUS,\n"
				+ "       CASE WHEN (F.ExamAvgTime IS NULL) THEN E.ExamAvgTime ELSE F.ExamAvgTime END ExamAvgTime,\n"
				+ "       CASE WHEN (F.ExamIntervalTime IS NULL) THEN E.ExamIntervalTime ELSE F.ExamIntervalTime END ExamIntervalTime\n"
				+ " FROM  (SELECT X.*\n"
				+ "      FROM DB2INST1.CMNBEBOARDDEVICETBL X JOIN DB2INST1.CMNBEBOARDLAYOUTTBL Y on X.ORGNO = Y.ORGNO AND X.LayoutId = Y.LayoutId\n"
				+ "      WHERE Y.TYPE ='2') A LEFT JOIN DB2INST1.CMNBEBOARDLOCATIONMAPTBL B ON A.ORGNO = B.ORGNO AND A.DEVICEID = B.DEVICEID\n"
				+ "                           LEFT JOIN DB2INST1.CMNBEBOARDLOCATIONTBL C ON B.ORGNO = C.ORGNO AND B.CLIENTID = C.CLIENTID\n"
				+ "                           LEFT JOIN DB2INST1.CMNBLOCATIONIPTBL D ON C.ORGNO = D.ORGNO AND C.IP = D.IP\n"
				+ "                           LEFT JOIN DB2INST1.CMNBLOCATIONTBL E ON D.ORGNO = E.ORGNO AND D.LOCNO = E.UNO\n"
				+ "                           LEFT JOIN DB2INST1.CMNBDOCTORTBL F ON D.ORGNO = F.ORGNO AND D.EXAMDRNO = F.UID\n"
				+ " WHERE (C.ORGNO, C.IP) in\n"
				+ "   (SELECT ORGNO,IP\n"
				+ "    FROM CMNBLOCATIONIPTBL\n"
				+ "    WHERE ORGNO = '" + OrgNo + "'\n"
				+ "      and LOCNO = '" + Uno + "')\n";
	}
	
	public String get3_3CheckroomData(String OrgNo, String UID) {
		return "SELECT A.ORGNO, E.UNO LOCNO,A.ROOMNO,E.LOCname LocNameonEBoard,A.FLOOR,A.AREA,\n"
				+ "		  CASE WHEN E.MAXLIMIT is null THEN 1 ELSE E.MAXLIMIT END MAXLIMIT,\n"
				+ "       CASE WHEN (Trim(E.LocType) = '' or E.LocType IS NULL) THEN '1' ELSE E.LocType END LocType,\n"
				+ "       B.CLIENTID, E.LastScheduleTime,               \n"
				+ "       CASE WHEN (Trim(F.GENDER) = '' or F.GENDER IS NULL) THEN 'M' ELSE F.GENDER END DRGender,        \n"
				+ "       D.EXAMDRNO, D.REPORTDRNO, \n"
				+ "       CASE WHEN (Trim(E.GENDER) = '' or E.GENDER IS NULL) THEN '' ELSE E.GENDER END LocGender,\n"
				+ "       CASE WHEN (Trim(F.ASSIGNRULE) = '' or F.ASSIGNRULE IS NULL) THEN '2' ELSE F.ASSIGNRULE END ASSIGNRULE,\n"
				+ "       C.STATUS ROOMSTATUS,\n"
				+ "       CASE WHEN (F.ExamAvgTime IS NULL) THEN E.ExamAvgTime ELSE F.ExamAvgTime END ExamAvgTime,\n"
				+ "       CASE WHEN (F.ExamIntervalTime IS NULL) THEN E.ExamIntervalTime ELSE F.ExamIntervalTime END ExamIntervalTime \n"
				+ " FROM  (SELECT X.*\n"
				+ "      FROM DB2INST1.CMNBEBOARDDEVICETBL X JOIN DB2INST1.CMNBEBOARDLAYOUTTBL Y on X.ORGNO = Y.ORGNO AND X.LayoutId = Y.LayoutId \n"
				+ "      WHERE Y.TYPE ='2') A LEFT JOIN DB2INST1.CMNBEBOARDLOCATIONMAPTBL B ON A.ORGNO = B.ORGNO AND A.DEVICEID = B.DEVICEID\n"
				+ "                           LEFT JOIN DB2INST1.CMNBEBOARDLOCATIONTBL C ON B.ORGNO = C.ORGNO AND B.CLIENTID = C.CLIENTID\n"
				+ "                           LEFT JOIN DB2INST1.CMNBLOCATIONIPTBL D ON C.ORGNO = D.ORGNO AND C.IP = D.IP\n"
				+ "                           LEFT JOIN DB2INST1.CMNBLOCATIONTBL E ON D.ORGNO = E.ORGNO AND D.LOCNO = E.UNO\n"
				+ "                           LEFT JOIN DB2INST1.CMNBDOCTORTBL F ON D.ORGNO = F.ORGNO AND D.EXAMDRNO = F.UID\n"
				+ " WHERE (C.ORGNO, C.IP) in\n"
				+ "   (SELECT ORGNO,IP\n"
				+ "    FROM CMNBLOCATIONIPTBL\n"
				+ "    WHERE ORGNO = '" + OrgNo + "'\n"
				+ "      and EXAMDRNO = '" + UID + "')\n";
	}
	
	public String get4CheckroomItem(String OrgNo, String IP) {
		return "SELECT A.ORGNO,A.LOCNO,B.DAYTYPE, A.CATENO, A.EXAMITEM\n"
				+ " FROM DB2INST1.CMNBEXAMITEMLOCATIONTBL A LEFT JOIN \n"
				+ " DB2INST1.SCHBCATEGORYTBL B ON A.ORGNO = B.ORGNO AND A.CATENO = B.UNO\n"
				+ " JOIN (SELECT ORGNO, EXAMITEM FROM DB2INST1.CMNBEXAMITEMDATATBL \n"
				+ " WHERE ACTIVEFLAG = '0' AND IsSyncAutoSchedule = 'Y') C \n"
				+ " ON A.ORGNO = C.ORGNO AND A.EXAMITEM = C.EXAMITEM\n"
				+ " WHERE (A.ORGNO,A.LOCNO) in\n"
				+ "  (SELECT OrgNo,LocNo\n"
				+ "   FROM DB2INST1.CMNBLOCATIONIPTBL\n"
				+ "   WHERE ORGNO = '"+OrgNo+"' \n"
				+ "     AND IP = '"+IP+"')\n"
				+ " AND B.DAYTYPE > ''\n"
				+ " AND (A.CHKDISPLAY IS NULL  OR A.CHKDISPLAY = '' OR A.CHKDISPLAY != '1')";
	}
}
