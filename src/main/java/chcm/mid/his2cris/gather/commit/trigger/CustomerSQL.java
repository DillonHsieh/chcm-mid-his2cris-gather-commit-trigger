package chcm.mid.his2cris.gather.commit.trigger;

public class CustomerSQL {

	public String getCustomerUpdate2_1(String today, String caseNo) {
		return "SELECT DISTINCT *\n"
				+ " FROM \n"
				+ "(\n"
				+ "SELECT distinct \n"
				+ "       A.ORGNO,A.CASENO, A.CHARTNO, E.SERIALNO RFID,B.NAME CHARTNAME,B.REMARK PERREMARK,A.REMARK REVRemark,\n"
				+ "       CASE WHEN (B.GENDER IS NULL OR Trim(B.GENDER) = '') THEN ' ' ELSE B.GENDER END GENDER,\n"
				+ "       B.BIRTHDAY, A.AGE,\n"
				+ "       CASE WHEN (G.VIPFLAG = 'Y') OR (C.VIPFLAG = 'Y') THEN 'Y' ELSE 'N' END VIPFLAG, \n"
				+ "       CASE WHEN (H.ISPREGNANT IS NULL OR Trim(H.ISPREGNANT) = '') THEN 'N' ELSE H.ISPREGNANT END ISPREGNANT,\n"
				+ "       CASE WHEN (G.DMFLAG IS NULL OR Trim(G.DMFLAG) = '') THEN 'N' ELSE G.DMFLAG END DMFLAG,\n"
				+ "       CASE WHEN (IFNULL(UpgradeFlag,'') = '') THEN C.DAYTYPE ELSE '2' END DAYTYPE,\n"
				+ "       (A.CHECKINDATE || ' ' || Substr(A.CHECKINTIME,1,6)) CHECKINDT,\n"
				+ "       (A.BOOKCHECKINDATE || ' ' || A.BOOKCHECKINTIME || '00') BOOKCHECKINDT,\n"
				+ "       A.BOOKDATE,'N' IsPOSTPONE,'' ISGYNINTRUSIVE,\n"
				+ "       A.CATENO,C.CATENAME,\n"
				+ "		  CASE WHEN (F.DESCRIPTION = '') OR (F.DESCRIPTION is NULL) THEN A.PACKAGENO ELSE F.DESCRIPTION END PACKAGENAME,\n"
				+ "       CASE WHEN (H.ISSELECTGYNFEMALEDR IS NULL OR Trim(H.ISSELECTGYNFEMALEDR) = '') THEN 'N' ELSE H.ISSELECTGYNFEMALEDR END ISSELECTGYNFEMALEDR,\n"
				+ "       D.SHORTNAME COMPANYNAME, A.BookPeriod, CAST(A.SELFAMT AS SIGNED) SELFAMT, B.Address1, B.EMAIL, B.CELL\n"
				+ " FROM DB2INST1.REVTBOOKDATATBL A JOIN DB2INST1.REVBPERSONALTBL B ON A.CHARTNO = B.CHARTNO\n"
				+ "                                JOIN DB2INST1.SCHBCATEGORYTBL C ON A.ORGNO = C.ORGNO AND A.CATENO = C.UNO\n"
				+ "                                JOIN DB2INST1.CMNBCOMPANYTBL  D ON A.orgno = D.orgno AND A.COMPANYNO = D.UNO\n"
				+ "                           LEFT JOIN DB2INST1.CMNBRFIDTBL E ON A.ORGNO = E.ORGNO AND A.CASENO = E.CASENO\n"
				+ "                           LEFT JOIN DB2INST1.CMNBPACKAGECOMPANYTBL F ON A.ORGNO = F.ORGNO AND A.PACKAGENO = F.PACKAGENO\n"
				+ "                           LEFT JOIN DB2INST1.REVBPERSONALSUBTBL G ON B.CHARTNO = G.CHARTNO                           \n"
				+ "                           LEFT JOIN DB2INST1.REVTBOOKDATA2TBL H ON A.CASENO = H.CASENO\n"
				+ " WHERE A.BOOKDATE = '"+today+"'\n"
				+ " AND A.CASENO = '"+caseNo+"'\n"
				+ " UNION\n"
				+ " SELECT distinct \n"
				+ "       A.ORGNO,A.CASENO, A.CHARTNO, E.SERIALNO RFID,B.NAME CHARTNAME,B.REMARK PERREMARK,A.REMARK REVRemark,\n"
				+ "       CASE WHEN (B.GENDER IS NULL OR Trim(B.GENDER) = '') THEN ' ' ELSE B.GENDER END GENDER,\n"
				+ "       B.BIRTHDAY, A.AGE,\n"
				+ "       CASE WHEN (G.VIPFLAG = 'Y') OR (C.VIPFLAG = 'Y') THEN 'Y' ELSE 'N' END VIPFLAG, \n"
				+ "       CASE WHEN (H.ISPREGNANT IS NULL OR Trim(H.ISPREGNANT) = '') THEN 'N' ELSE H.ISPREGNANT END ISPREGNANT,\n"
				+ "       CASE WHEN (G.DMFLAG IS NULL OR Trim(G.DMFLAG) = '') THEN 'N' ELSE G.DMFLAG END DMFLAG,\n"
				+ "       CASE WHEN (IFNULL(UpgradeFlag,'') = '') THEN C.DAYTYPE ELSE '2' END DAYTYPE,\n"
				+ "       (A.CHECKINDATE || ' ' || Substr(A.CHECKINTIME,1,6)) CHECKINDT,\n"
				+ "       (A.BOOKCHECKINDATE || ' ' || A.BOOKCHECKINTIME || '00') BOOKCHECKINDT,\n"
				+ "       A.BOOKDATE,'Y' IsPOSTPONE,'' ISGYNINTRUSIVE,\n"
				+ "       A.CATENO,C.CATENAME, F.DESCRIPTION PACKAGENAME,\n"
				+ "       CASE WHEN (H.ISSELECTGYNFEMALEDR IS NULL OR Trim(H.ISSELECTGYNFEMALEDR) = '') THEN 'N' ELSE H.ISSELECTGYNFEMALEDR END ISSELECTGYNFEMALEDR,\n"
				+ "       D.SHORTNAME COMPANYNAME, A.BookPeriod, CAST(A.SELFAMT AS SIGNED) SELFAMT, B.Address1, B.EMAIL, B.CELL\n"
				+ " FROM (SELECT CASENO, BOOKDATE, '000000' BookTime\n"
				+ "      FROM \n"
				+ "      (\n"
				+ "        SELECT S.CASENO , S.BookDate \n"
				+ "        FROM DB2INST1.SCHTSCHEDULETBL S JOIN DB2INST1.REVTBOOKDATATBL A ON S.CASENO = A.CASENO\n"
				+ "        WHERE S.BOOKDATE = '"+today+"' \n"
				+ "         AND (S.BOOKDATE <> A.BOOKDATE)\n"
				+ "         AND A.CASENO = '"+caseNo+"'\n"
				+ "        union\n"
				+ "        SELECT P.CASENO , P.BookDate \n"
				+ "        FROM DB2INST1.REVTPOSTPONETBL P JOIN DB2INST1.REVTBOOKDATATBL A ON P.CASENO = A.CASENO\n"
				+ "        WHERE P.BOOKDATE = '"+today+"' \n"
				+ "         AND (P.BOOKDATE <> A.BOOKDATE)\n"
				+ "         AND A.CASENO = '"+caseNo+"'\n"
				+ "      ) ZZ\n"
				+ "     ) P JOIN DB2INST1.REVTBOOKDATATBL A ON P.CASENO = A.CASENO\n"
				+ "         JOIN DB2INST1.REVBPERSONALTBL B ON A.CHARTNO = B.CHARTNO\n"
				+ "         JOIN DB2INST1.SCHBCATEGORYTBL C ON A.ORGNO = C.ORGNO AND A.CATENO = C.UNO\n"
				+ "         JOIN DB2INST1.CMNBCOMPANYTBL  D ON A.orgno = D.orgno AND A.COMPANYNO = D.UNO\n"
				+ "    LEFT JOIN DB2INST1.CMNBRFIDTBL E ON A.ORGNO = E.ORGNO AND A.CASENO = E.CASENO\n"
				+ "    LEFT JOIN DB2INST1.CMNBPACKAGECOMPANYTBL F ON A.ORGNO = F.ORGNO AND A.PACKAGENO = F.PACKAGENO\n"
				+ "    LEFT JOIN DB2INST1.REVBPERSONALSUBTBL G ON B.CHARTNO = G.CHARTNO                           \n"
				+ "    LEFT JOIN DB2INST1.REVTBOOKDATA2TBL H ON A.CASENO = H.CASENO\n"
				+ ") Z  \n";
	}
	
	public String getCustomerUpdate2_2(String today, String chartNo) {
		return "SELECT DISTINCT *\n"
				+ " FROM \n"
				+ "(\n"
				+ "SELECT distinct \n"
				+ "       A.ORGNO,A.CASENO, A.CHARTNO, E.SERIALNO RFID,B.NAME CHARTNAME,B.REMARK PERREMARK,A.REMARK REVRemark,\n"
				+ "       CASE WHEN (B.GENDER IS NULL OR Trim(B.GENDER) = '') THEN ' ' ELSE B.GENDER END GENDER,\n"
				+ "       B.BIRTHDAY, A.AGE,\n"
				+ "       CASE WHEN (G.VIPFLAG = 'Y') OR (C.VIPFLAG = 'Y') THEN 'Y' ELSE 'N' END VIPFLAG, \n"
				+ "       CASE WHEN (H.ISPREGNANT IS NULL OR Trim(H.ISPREGNANT) = '') THEN 'N' ELSE H.ISPREGNANT END ISPREGNANT,\n"
				+ "       CASE WHEN (G.DMFLAG IS NULL OR Trim(G.DMFLAG) = '') THEN 'N' ELSE G.DMFLAG END DMFLAG,\n"
				+ "       CASE WHEN (IFNULL(UpgradeFlag,'') = '') THEN C.DAYTYPE ELSE '2' END DAYTYPE,\n"
				+ "       (A.CHECKINDATE || ' ' || Substr(A.CHECKINTIME,1,6)) CHECKINDT,\n"
				+ "       (A.BOOKCHECKINDATE || ' ' || A.BOOKCHECKINTIME || '00') BOOKCHECKINDT,\n"
				+ "       A.BOOKDATE,'N' IsPOSTPONE,'' ISGYNINTRUSIVE,\n"
				+ "       A.CATENO,C.CATENAME,\n"
				+ "		  CASE WHEN (F.DESCRIPTION = '') OR (F.DESCRIPTION is NULL) THEN A.PACKAGENO ELSE F.DESCRIPTION END PACKAGENAME,\n"
				+ "       CASE WHEN (H.ISSELECTGYNFEMALEDR IS NULL OR Trim(H.ISSELECTGYNFEMALEDR) = '') THEN 'N' ELSE H.ISSELECTGYNFEMALEDR END ISSELECTGYNFEMALEDR,\n"
				+ "       D.SHORTNAME COMPANYNAME, A.BookPeriod, CAST(A.SELFAMT AS SIGNED) SELFAMT, B.Address1, B.EMAIL, B.CELL\n"
				+ " FROM DB2INST1.REVTBOOKDATATBL A JOIN DB2INST1.REVBPERSONALTBL B ON A.CHARTNO = B.CHARTNO\n"
				+ "                                JOIN DB2INST1.SCHBCATEGORYTBL C ON A.ORGNO = C.ORGNO AND A.CATENO = C.UNO\n"
				+ "                                JOIN DB2INST1.CMNBCOMPANYTBL  D ON A.orgno = D.orgno AND A.COMPANYNO = D.UNO\n"
				+ "                           LEFT JOIN DB2INST1.CMNBRFIDTBL E ON A.ORGNO = E.ORGNO AND A.CASENO = E.CASENO\n"
				+ "                           LEFT JOIN DB2INST1.CMNBPACKAGECOMPANYTBL F ON A.ORGNO = F.ORGNO AND A.PACKAGENO = F.PACKAGENO\n"
				+ "                           LEFT JOIN DB2INST1.REVBPERSONALSUBTBL G ON B.CHARTNO = G.CHARTNO                           \n"
				+ "                           LEFT JOIN DB2INST1.REVTBOOKDATA2TBL H ON A.CASENO = H.CASENO\n"
				+ " WHERE A.BOOKDATE = '"+today+"'\n"
				+ "  AND A.CHARTNO = '"+chartNo+"'\n"
				+ " UNION\n"
				+ " SELECT distinct \n"
				+ "       A.ORGNO,A.CASENO, A.CHARTNO, E.SERIALNO RFID,B.NAME CHARTNAME,B.REMARK PERREMARK,A.REMARK REVRemark,\n"
				+ "       CASE WHEN (B.GENDER IS NULL OR Trim(B.GENDER) = '') THEN ' ' ELSE B.GENDER END GENDER,\n"
				+ "       B.BIRTHDAY, A.AGE,\n"
				+ "       CASE WHEN (G.VIPFLAG = 'Y') OR (C.VIPFLAG = 'Y') THEN 'Y' ELSE 'N' END VIPFLAG, \n"
				+ "       CASE WHEN (H.ISPREGNANT IS NULL OR Trim(H.ISPREGNANT) = '') THEN 'N' ELSE H.ISPREGNANT END ISPREGNANT,\n"
				+ "       CASE WHEN (G.DMFLAG IS NULL OR Trim(G.DMFLAG) = '') THEN 'N' ELSE G.DMFLAG END DMFLAG,\n"
				+ "       CASE WHEN (IFNULL(UpgradeFlag,'') = '') THEN C.DAYTYPE ELSE '2' END DAYTYPE,\n"
				+ "       (A.CHECKINDATE || ' ' || Substr(A.CHECKINTIME,1,6)) CHECKINDT,\n"
				+ "       (A.BOOKCHECKINDATE || ' ' || A.BOOKCHECKINTIME || '00') BOOKCHECKINDT,\n"
				+ "       A.BOOKDATE,'Y' IsPOSTPONE,'' ISGYNINTRUSIVE,\n"
				+ "       A.CATENO,C.CATENAME, F.DESCRIPTION PACKAGENAME,\n"
				+ "       CASE WHEN (H.ISSELECTGYNFEMALEDR IS NULL OR Trim(H.ISSELECTGYNFEMALEDR) = '') THEN 'N' ELSE H.ISSELECTGYNFEMALEDR END ISSELECTGYNFEMALEDR,\n"
				+ "       D.SHORTNAME COMPANYNAME, A.BookPeriod, CAST(A.SELFAMT AS SIGNED) SELFAMT, B.Address1, B.EMAIL, B.CELL\n"
				+ " FROM (SELECT CASENO, BOOKDATE, '000000' BookTime\n"
				+ "      FROM \n"
				+ "      (\n"
				+ "        SELECT S.CASENO , S.BookDate \n"
				+ "        FROM DB2INST1.SCHTSCHEDULETBL S JOIN DB2INST1.REVTBOOKDATATBL A ON S.CASENO = A.CASENO\n"
				+ "        WHERE S.BOOKDATE = '"+today+"' \n"
				+ "         AND (S.BOOKDATE <> A.BOOKDATE)\n"
				+ "         AND A.CHARTNO = '"+chartNo+"'\n"
				+ "        union\n"
				+ "        SELECT P.CASENO , P.BookDate \n"
				+ "        FROM DB2INST1.REVTPOSTPONETBL P JOIN DB2INST1.REVTBOOKDATATBL A ON P.CASENO = A.CASENO\n"
				+ "        WHERE P.BOOKDATE = '"+today+"' \n"
				+ "         AND (P.BOOKDATE <> A.BOOKDATE)\n"
				+ "         AND A.CHARTNO = '"+chartNo+"'\n"
				+ "      ) ZZ\n"
				+ "     ) P JOIN DB2INST1.REVTBOOKDATATBL A ON P.CASENO = A.CASENO\n"
				+ "         JOIN DB2INST1.REVBPERSONALTBL B ON A.CHARTNO = B.CHARTNO\n"
				+ "         JOIN DB2INST1.SCHBCATEGORYTBL C ON A.ORGNO = C.ORGNO AND A.CATENO = C.UNO\n"
				+ "         JOIN DB2INST1.CMNBCOMPANYTBL  D ON A.orgno = D.orgno AND A.COMPANYNO = D.UNO\n"
				+ "    LEFT JOIN DB2INST1.CMNBRFIDTBL E ON A.ORGNO = E.ORGNO AND A.CASENO = E.CASENO\n"
				+ "    LEFT JOIN DB2INST1.CMNBPACKAGECOMPANYTBL F ON A.ORGNO = F.ORGNO AND A.PACKAGENO = F.PACKAGENO\n"
				+ "    LEFT JOIN DB2INST1.REVBPERSONALSUBTBL G ON B.CHARTNO = G.CHARTNO                           \n"
				+ "    LEFT JOIN DB2INST1.REVTBOOKDATA2TBL H ON A.CASENO = H.CASENO\n"
				+ ") Z";
	}
	
	public String getCreate(String today, String caseno) {
		return "SELECT DISTINCT *\n"
				+ " FROM \n"
				+ "(\n"
				+ "SELECT distinct \n"
				+ "       A.ORGNO,A.CASENO, A.CHARTNO, E.SERIALNO RFID,B.NAME CHARTNAME,B.REMARK PERREMARK,A.REMARK REVRemark,\n"
				+ "       CASE WHEN (B.GENDER IS NULL OR Trim(B.GENDER) = '') THEN ' ' ELSE B.GENDER END GENDER,\n"
				+ "       B.BIRTHDAY, A.AGE,\n"
				+ "       CASE WHEN (G.VIPFLAG = 'Y') OR (C.VIPFLAG = 'Y') THEN 'Y' ELSE 'N' END VIPFLAG, \n"
				+ "       CASE WHEN (H.ISPREGNANT IS NULL OR Trim(H.ISPREGNANT) = '') THEN 'N' ELSE H.ISPREGNANT END ISPREGNANT,\n"
				+ "       CASE WHEN (G.DMFLAG IS NULL OR Trim(G.DMFLAG) = '') THEN 'N' ELSE G.DMFLAG END DMFLAG,\n"
				+ "       CASE WHEN (IFNULL(UpgradeFlag,'') = '') THEN C.DAYTYPE ELSE '2' END DAYTYPE,\n"
				+ "       Trim(CONCAT(CHECKINDATE, ' ', Substr(CHECKINTIME,1,6))) CHECKINDT,\n"
				+ "       Trim(CONCAT(BOOKCHECKINDATE, ' ', BOOKCHECKINTIME, '00')) BOOKCHECKINDT,\n"
				+ "       A.BOOKDATE,'Y' IsPOSTPONE,\n"
				+ "       A.CATENO,C.CATENAME, F.DESCRIPTION PACKAGENAME,\n"
				+ "       CASE WHEN (H.ISSELECTGYNFEMALEDR IS NULL OR Trim(H.ISSELECTGYNFEMALEDR) = '') THEN 'N' ELSE H.ISSELECTGYNFEMALEDR END ISSELECTGYNFEMALEDR,\n"
				+ "       D.SHORTNAME COMPANYNAME, A.BookPeriod, CAST(A.SELFAMT AS SIGNED) SELFAMT, B.Address1, B.EMAIL, B.CELL\n"
				+ " FROM DB2INST1.REVTBOOKDATATBL A JOIN DB2INST1.REVBPERSONALTBL B ON A.CHARTNO = B.CHARTNO\n"
				+ "                                JOIN DB2INST1.SCHBCATEGORYTBL C ON A.ORGNO = C.ORGNO AND A.CATENO = C.UNO\n"
				+ "                                JOIN DB2INST1.CMNBCOMPANYTBL  D ON A.orgno = D.orgno AND A.COMPANYNO = D.UNO\n"
				+ "                           LEFT JOIN DB2INST1.CMNBRFIDTBL E ON A.ORGNO = E.ORGNO AND A.CASENO = E.CASENO\n"
				+ "                           LEFT JOIN DB2INST1.CMNBPACKAGECOMPANYTBL F ON A.ORGNO = F.ORGNO AND A.PACKAGENO = F.PACKAGENO\n"
				+ "                           LEFT JOIN DB2INST1.REVBPERSONALSUBTBL G ON B.CHARTNO = G.CHARTNO\n"
				+ "                           LEFT JOIN DB2INST1.REVTBOOKDATA2TBL H ON A.CASENO = H.CASENO\n"
				+ " WHERE A.BOOKDATE = '"+today+"'\n"
				+ "  AND A.CASENO = '"+caseno+"'\n"
				+ ")Z";
	}
}
