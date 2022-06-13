package chcm.mid.his2cris.gather.commit.trigger;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import chcm.mid.his2cris.gather.commit.trigger.dto.CHCM;
import chcm.mid.his2cris.gather.commit.trigger.dto.COMMIT;
import chcm.mid.his2cris.gather.commit.trigger.dto.TESTCLASS;


public class HandlerGatherCommitTrigger2{	

	
	public void handlerRequest(String test){
		List<CHCM> chcms = new ArrayList<>();
		List<COMMIT> commitsList1 = new ArrayList<>();
		List<COMMIT> commitsList2 = new ArrayList<>();
		List<TESTCLASS> testClassList1 = new ArrayList<>();
		List<TESTCLASS> testClassList2 = new ArrayList<>();
		List<TESTCLASS> testClassList3 = new ArrayList<>();
		List<TESTCLASS> testClassList4 = new ArrayList<>();
		List<TESTCLASS> testClassList5 = new ArrayList<>();
		
		TESTCLASS testC1 = new TESTCLASS();
		TESTCLASS testC2 = new TESTCLASS();
		TESTCLASS testC3 = new TESTCLASS();
		TESTCLASS testC4 = new TESTCLASS();
		TESTCLASS testC5 = new TESTCLASS();
		TESTCLASS testC6 = new TESTCLASS();
		TESTCLASS testC7 = new TESTCLASS();
		TESTCLASS testC8 = new TESTCLASS();
		TESTCLASS testC9 = new TESTCLASS();
		TESTCLASS testC10 = new TESTCLASS();
		testC1.setTestId("C1");
		testC1.setTestStatus("C1 status");
		testC2.setTestId("C2");
		testC2.setTestStatus("C2 status");
		testC3.setTestId("C3");
		testC3.setTestStatus("C3 status");
		testC4.setTestId("C4");
		testC4.setTestStatus("C4 status");		
		testC5.setTestId("C5");
		testC5.setTestStatus("C5 status");
		testC6.setTestId("C6");
		testC6.setTestStatus("C6 status");
		testC7.setTestId("C7");
		testC7.setTestStatus("C7 status");
		testC8.setTestId("C8");
		testC8.setTestStatus("C8 status");
		testC9.setTestId("C9");
		testC9.setTestStatus("C9 status");
		testC10.setTestId("C10");
		testC10.setTestStatus("C10 status");
		testClassList1.add(testC1);		
		testClassList1.add(testC2);
		testClassList2.add(testC3);
		testClassList2.add(testC4);
		testClassList3.add(testC5);		
		testClassList3.add(testC6);
		testClassList4.add(testC7);
		testClassList4.add(testC8);
		testClassList4.add(testC9);
		testClassList5.add(testC10);
		
		COMMIT commit1 = new COMMIT();
		COMMIT commit2 = new COMMIT();
		COMMIT commit3 = new COMMIT();
		COMMIT commit4 = new COMMIT();
		COMMIT commit5 = new COMMIT();
		commit1.setTestClass(testClassList1);
		commit2.setTestClass(testClassList2);
		commit3.setTestClass(testClassList3);
		commit4.setTestClass(testClassList4);
		commit5.setTestClass(testClassList5);
		commitsList1.add(commit1);
		commitsList1.add(commit2);
		commitsList2.add(commit3);
		commitsList2.add(commit4);
		commitsList2.add(commit5);
		
		CHCM chcm1 = new CHCM();
		CHCM chcm2 = new CHCM();
		chcm1.setOrgNo("1");
		chcm1.setCommit(commitsList1);
		chcm2.setOrgNo("2");
		chcm2.setCommit(commitsList2);		
		chcms.add(chcm1);
		chcms.add(chcm2);
		
		System.out.println("chcms = "+chcms.toString());
		
		List<TESTCLASS> results = chcms.stream().map(x ->x.getCommit()).collect(Collectors.toList())
				.stream().flatMap(List::stream).collect(Collectors.toList())
				.stream().map(y ->y.getTestClass()).collect(Collectors.toList())
				.stream().flatMap(List::stream).collect(Collectors.toList());
		System.out.println("result = "+results.toString());
	}
}
