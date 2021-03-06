package chcm.mid.his2cris.gather.commit.trigger;

public class CustExamSQL {

	public String getExamine(String today, String caseNo, String examItem) {
		return "SELECT DISTINCT *\n"
				+ " FROM\n"
				+ "(\n"
				+ "SELECT A.ORGNO,A.CASENO, B.EXAMITEM, B.EXAMSTATUS,\n"
				+ " (B.CHECKINDATE || ' ' || Substr(B.CHECKINTIME,1,6)) CheckinDT, \n"
				+ " (B.FINISHDATE || ' ' || Substr(B.FINISHTIME,1,6)) CheckoutDT,\n"
				+ " B.LOCNO, '' CLIENTID\n"
				+ " FROM (SELECT distinct X.CASENO, X.ORGNO, X.BOOKDATE\n"
				+ "      FROM DB2INST1.REVTBOOKDATATBL X JOIN DB2INST1.REVBPERSONALTBL Y ON X.CHARTNO = Y.CHARTNO\n"
				+ "                                      JOIN DB2INST1.SCHBCATEGORYTBL Z ON X.ORGNO = Z.ORGNO AND X.CATENO = Z.UNO\n"
				+ "                                      JOIN DB2INST1.CMNBCOMPANYTBL  P ON X.orgno = P.orgno AND X.COMPANYNO = P.UNO\n"
				+ "      WHERE X.BOOKDATE = '"+today+"') A JOIN DB2INST1.RPTTREPORTTBL B ON B.CASENO = A.CASENO\n"
				+ "                              JOIN DB2INST1.CMNBEXAMITEMDATATBL C ON A.ORGNO = C.ORGNO AND B.EXAMITEM = C.EXAMITEM \n"
				+ "                         LEFT JOIN (SELECT * FROM DB2INST1.SCHTSCHEDULETBL WHERE BOOKDATE = '"+today+"') D ON A.CASENO = D.CASENO AND A.BOOKDATE = D.BOOKDATE AND B.EXAMITEM = D.EXAMITEM\n"
				+ " WHERE B.RESULTSEQ = '1'\n"
				+ "  AND C.ISSyncAutoSchedule = 'Y'\n"
				+ "  AND A.BOOKDATE = '"+today+"'  \n"
				+ "  AND B.CASENO = '"+caseNo+"'\n"
				+ "  AND B.EXAMITEM = '"+examItem+"'\n"
				+ " UNION\n"
				+ " SELECT A.ORGNO,A.CASENO, B.EXAMITEM, B.EXAMSTATUS,\n"
				+ " (B.CHECKINDATE || ' ' || Substr(B.CHECKINTIME,1,6)) CheckinDT, \n"
				+ " (B.FINISHDATE || ' ' || Substr(B.FINISHTIME,1,6)) CheckoutDT, \n"
				+ " B.LOCNO, '' CLIENTID\n"
				+ " FROM(\n"
				+ "     SELECT DISTINCT M.CASENO, M.ORGNO,M.BOOKDATE, M.EXAMITEM\n"
				+ "     FROM \n"
				+ "     (SELECT Z.ORGNO,S.CASENO , S.BookDate ,S.EXAMITEM, Z.BOOKDATE ORGBOOKDATE\n"
				+ "      FROM DB2INST1.SCHTSCHEDULETBL S JOIN DB2INST1.REVTBOOKDATATBL Z ON S.CASENO = Z.CASENO\n"
				+ "      WHERE S.BOOKDATE = '"+today+"' \n"
				+ "        AND (S.BOOKDATE <> Z.BOOKDATE)\n"
				+ "      union\n"
				+ "      SELECT Z.ORGNO,P.CASENO , P.BOOKDATE ,P.EXAMITEM, Z.BOOKDATE ORGBOOKDATE\n"
				+ "      FROM DB2INST1.REVTPOSTPONETBL P JOIN DB2INST1.REVTBOOKDATATBL Z ON P.CASENO = Z.CASENO\n"
				+ "      WHERE P.BOOKDATE = '"+today+"' \n"
				+ "       AND (P.BOOKDATE <> Z.BOOKDATE)\n"
				+ "     ) M JOIN DB2INST1.REVTBOOKDATATBL X ON M.CASENO = X.CASENO\n"
				+ "         JOIN DB2INST1.REVBPERSONALTBL Y ON X.CHARTNO = Y.CHARTNO\n"
				+ "         JOIN DB2INST1.SCHBCATEGORYTBL Z ON X.ORGNO = Z.ORGNO AND X.CATENO = Z.UNO\n"
				+ "         JOIN DB2INST1.CMNBCOMPANYTBL  P ON X.orgno = P.orgno AND X.COMPANYNO = P.UNO\n"
				+ "    ) A JOIN DB2INST1.RPTTREPORTTBL B ON A.CASENO = B.CASENO AND A.EXAMITEM = B.EXAMITEM\n"
				+ " WHERE B.RESULTSEQ = '1'\n"
				+ "  AND B.CASENO = '"+caseNo+"'\n"
				+ "  AND B.EXAMITEM = '"+examItem+"'\n"
				+ ")Z";
	}
	
	public String getNewExamine(String today, String caseNo, String examItems) {
		return "SELECT DISTINCT *\n"
				+ " FROM\n"
				+ "(\n"
				+ "SELECT A.ORGNO,A.CASENO, B.EXAMITEM, B.EXAMSTATUS,\n"
				+ " (B.CHECKINDATE || ' ' || Substr(B.CHECKINTIME,1,6)) CheckinDT, \n"
				+ " (B.FINISHDATE || ' ' || Substr(B.FINISHTIME,1,6)) CheckoutDT,\n"
				+ " B.LOCNO, '' CLIENTID\n"
				+ " FROM (SELECT distinct X.CASENO, X.ORGNO, X.BOOKDATE\n"
				+ "      FROM DB2INST1.REVTBOOKDATATBL X JOIN DB2INST1.REVBPERSONALTBL Y ON X.CHARTNO = Y.CHARTNO\n"
				+ "                                      JOIN DB2INST1.SCHBCATEGORYTBL Z ON X.ORGNO = Z.ORGNO AND X.CATENO = Z.UNO\n"
				+ "                                      JOIN DB2INST1.CMNBCOMPANYTBL  P ON X.orgno = P.orgno AND X.COMPANYNO = P.UNO\n"
				+ "      WHERE X.BOOKDATE = '"+today+"') A JOIN DB2INST1.RPTTREPORTTBL B ON B.CASENO = A.CASENO\n"
				+ "                              JOIN DB2INST1.CMNBEXAMITEMDATATBL C ON A.ORGNO = C.ORGNO AND B.EXAMITEM = C.EXAMITEM \n"
				+ "                         LEFT JOIN (SELECT * FROM DB2INST1.SCHTSCHEDULETBL WHERE BOOKDATE = '"+today+"') D ON A.CASENO = D.CASENO AND A.BOOKDATE = D.BOOKDATE AND B.EXAMITEM = D.EXAMITEM\n"
				+ " WHERE B.RESULTSEQ = '1'\n"
				+ "  AND C.ISSyncAutoSchedule = 'Y'\n"
				+ "  AND A.BOOKDATE = '"+today+"'  \n"
				+ "  AND B.CASENO = '"+caseNo+"'\n"
				+ "  AND B.EXAMITEM IN ("+examItems+")\n"
				+ " UNION\n"
				+ " SELECT A.ORGNO,A.CASENO, B.EXAMITEM, B.EXAMSTATUS,\n"
				+ " (B.CHECKINDATE || ' ' || Substr(B.CHECKINTIME,1,6)) CheckinDT, \n"
				+ " (B.FINISHDATE || ' ' || Substr(B.FINISHTIME,1,6)) CheckoutDT, \n"
				+ " B.LOCNO, '' CLIENTID\n"
				+ " FROM(\n"
				+ "     SELECT DISTINCT M.CASENO, M.ORGNO,M.BOOKDATE, M.EXAMITEM\n"
				+ "     FROM \n"
				+ "     (SELECT Z.ORGNO,S.CASENO , S.BookDate ,S.EXAMITEM, Z.BOOKDATE ORGBOOKDATE\n"
				+ "      FROM DB2INST1.SCHTSCHEDULETBL S JOIN DB2INST1.REVTBOOKDATATBL Z ON S.CASENO = Z.CASENO\n"
				+ "      WHERE S.BOOKDATE = '"+today+"' \n"
				+ "        AND (S.BOOKDATE <> Z.BOOKDATE)\n"
				+ "      union\n"
				+ "      SELECT Z.ORGNO,P.CASENO , P.BOOKDATE ,P.EXAMITEM, Z.BOOKDATE ORGBOOKDATE\n"
				+ "      FROM DB2INST1.REVTPOSTPONETBL P JOIN DB2INST1.REVTBOOKDATATBL Z ON P.CASENO = Z.CASENO\n"
				+ "      WHERE P.BOOKDATE = '"+today+"' \n"
				+ "       AND (P.BOOKDATE <> Z.BOOKDATE)\n"
				+ "     ) M JOIN DB2INST1.REVTBOOKDATATBL X ON M.CASENO = X.CASENO\n"
				+ "         JOIN DB2INST1.REVBPERSONALTBL Y ON X.CHARTNO = Y.CHARTNO\n"
				+ "         JOIN DB2INST1.SCHBCATEGORYTBL Z ON X.ORGNO = Z.ORGNO AND X.CATENO = Z.UNO\n"
				+ "         JOIN DB2INST1.CMNBCOMPANYTBL  P ON X.orgno = P.orgno AND X.COMPANYNO = P.UNO\n"
				+ "    ) A JOIN DB2INST1.RPTTREPORTTBL B ON A.CASENO = B.CASENO AND A.EXAMITEM = B.EXAMITEM\n"
				+ " WHERE B.RESULTSEQ = '1'\n"
				+ "  AND B.CASENO = '"+caseNo+"'\n"
				+ "  AND B.EXAMITEM IN ("+examItems+")\n"
				+ ")Z";
	}

	
	public String checkExamItemInDBOntime_1(String caseNo, String examItem, String orderSeq, String resultSeq) {
		return "SELECT "
				+ "CASENO,EXAMITEM,ORDERSEQ,EXAMSTATUS,CHECKINDATE,CHECKINTIME,FINISHDATE,FINISHTIME"
				+ " FROM DB2INST1.RPTTREPORTTBL \n"
				+ "WHERE CASENO = '"+caseNo+"' "
				+ "AND EXAMITEM = '"+examItem+"' "
				+ "AND ORDERSEQ = '"+orderSeq+"' "
				+ "AND RESULTSEQ = '"+resultSeq+"'";
	}
	
	public String checkExamItemInDBOntime_2(String caseNo, String examItem) {
		return "SELECT "
				+ "CASENO,EXAMITEM,ORDERSEQ,EXAMSTATUS,CHECKINDATE,CHECKINTIME,FINISHDATE,FINISHTIME"
				+ " FROM DB2INST1.RPTTREPORTTBL \n"
				+ "WHERE CASENO = '"+caseNo+"' "
				+ "AND EXAMITEM = '"+examItem+"' "
				+ "AND RESULTSEQ = '1'";
	}
	
	public String checkExamItemInDBOntime_3(String caseNo, String examItems) {
		return "SELECT "
				+ "CASENO,EXAMITEM,ORDERSEQ,EXAMSTATUS,CHECKINDATE,CHECKINTIME,FINISHDATE,FINISHTIME"
				+ " FROM DB2INST1.RPTTREPORTTBL \n"
				+ "WHERE CASENO = '"+caseNo+"' "
				+ "AND EXAMITEM IN( "+examItems+" )"
				+ "AND RESULTSEQ = '1'";
	}
}
