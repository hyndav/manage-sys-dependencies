/**
 * 
 */
package com.cisco.Interface;

import java.util.List;

import com.mongodb.BasicDBObject;

/**
 * @author hmarella
 *
 */
public interface MDFInterface {

		
//	public Query getQuery(String className, String sourceDB) throws Exception;
//	
//	public void findChangedData(String sourceDB, String entName,BasicDBObject queryString, List<BasicDBObject> methodList, List<String> keyList, List<String> queryFields, String inputClassName) throws Exception;
	
	public void runQuery() throws Exception;
	
}
