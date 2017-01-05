package com.cisco.service;

import static org.junit.Assert.assertEquals;

import org.bson.Document;
import org.junit.Before;
import org.junit.Test;

import com.cisco.Interface.EntObjectSBPInterfaceImpl;
import com.cisco.env.EnvUtil;
import com.mongodb.client.MongoCursor;

public class EntitlementChangeDataDlServiceTest {

	EntObjectSBPInterfaceImpl entSvcImpl;
	String sourceDB = "subscription_db";
	String className = "Right-to-technical-support";
	String entName = "Right to Technical Support";
	String implClass = "subscriptions";
	Query query;
	EntitlementDataDlService entDLSvc;

	@Before
	public void setUp() throws Exception {
		entSvcImpl = new EntObjectSBPInterfaceImpl(sourceDB);
		entDLSvc = new EntitlementDataDlService();
	}

	@SuppressWarnings("static-access")
	@Test
	public void testEntMetaData() throws Exception {
		query = entSvcImpl.getQuery(className, sourceDB);
		System.out.println("queryFields::" + query.queryFields);
		assertEquals(EnvUtil.get("TEST_ENTITLEMENT_CLASS"), query.getEntName());
	}

	@SuppressWarnings("static-access")
	@Test
	public void findSBPData() throws Exception {
		entSvcImpl.findChangedData(sourceDB, entName, query.getQueryString(),  query.getMethodList(), Query.getKeyList(),
				query.getQueryFields(), implClass);
		if (entSvcImpl.cur.iterator().hasNext()) {
			MongoCursor<Document> cursor = entSvcImpl.cur.iterator();
			int sbpRecordCount = cursor.next().size();
			System.out.println("sbpRecordCount :" +  sbpRecordCount);
			System.out.println("entRecordCount : " + entDLSvc.entRecordCount);
			if(entDLSvc.entRecordCount == sbpRecordCount){
				System.out.println("Test Passed");
			}
		}		
	}
}
