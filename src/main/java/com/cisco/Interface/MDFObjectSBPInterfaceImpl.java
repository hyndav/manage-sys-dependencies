/**
 * 
 */
package com.cisco.Interface;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cisco.env.MongoDBDataSource;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.stream.JsonWriter;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import org.json.simple.*;

/**
 * @author hmarella
 *
 */

public class MDFObjectSBPInterfaceImpl implements MDFInterface {

	final static Logger log = LoggerFactory.getLogger(MDFObjectSBPInterfaceImpl.class);

	MongoDBDataSource mdfDatabase = null;
	DBCollection db = null;
	MongoDBDataSource sbpDatabase;
	public static FindIterable<Document> cur;

	public MDFObjectSBPInterfaceImpl() {
		mdfDatabase = new MongoDBDataSource(MongoDBDataSource.mdfmap_db);
		sbpDatabase = new MongoDBDataSource(MongoDBDataSource.subscription_db);
	}

	public void runQuery() {
		List<Integer> metaDataIds = new ArrayList<Integer>();
		MongoCollection<Document> subscriptionLines = sbpDatabase.getSubscriptionLines();
		List<String> sbpPIDs = subscriptionLines.distinct("productName", String.class)
				.filter(new Document("productName", new Document("$ne", null))).into(new ArrayList<String>());

		log.debug("SBP PIds :: " + sbpPIDs);
		log.debug("SBP PIds size:: " + sbpPIDs.size());

		MongoCollection<Document> mdfPIDMappings = mdfDatabase.getMdfPIDMappings();

		List<Integer> pidMappingsList = mdfPIDMappings.distinct("mdfLeafNodeId", Integer.class)
				.filter(new Document("itemName", new Document("$in", sbpPIDs))).into(new ArrayList<Integer>());

		log.debug("pidMappingsCursor ::::: " + pidMappingsList);
		log.debug("pidMappingsCursor size::::: " + pidMappingsList.size());

		MongoCollection<Document> mdfMetaDatas = mdfDatabase.getMdfMetaDatas();

		BasicDBObject metaDataquery = new BasicDBObject();
		metaDataquery.put("mdfLeafNodeId", new BasicDBObject("$in", pidMappingsList));
		metaDataquery.put("lifecycle", new BasicDBObject("$ne", "obsolete"));

		BasicDBObject projectFields = new BasicDBObject("mdfNodeObjects", true).append("_id", false)
				.append("mdfLeafNodeId", true);

		List<Document> mdfMetaDataIDsCursor = mdfMetaDatas.find(metaDataquery).projection(projectFields)
				.into(new ArrayList<>());

		log.debug("mdfMetaDatas Id(s) Array :: " + mdfMetaDataIDsCursor.get(0).get("mdfNodeObjects"));
		log.debug("mdfMetaDatas Id(s) ID :: " + mdfMetaDataIDsCursor.get(0).get("mdfLeafNodeId"));

		for (Document mdfMetaDataIDsCursorElem : mdfMetaDataIDsCursor) {
			log.debug("mdfMetaDatas Id(s) For :: " + mdfMetaDataIDsCursorElem.get("mdfNodeObjects"));
			metaDataIds.add((Integer) mdfMetaDataIDsCursorElem.get("mdfLeafNodeId"));
			if (mdfMetaDataIDsCursorElem.get("mdfNodeObjects") != null) {
				metaDataIds.addAll((List<Integer>) mdfMetaDataIDsCursorElem.get("mdfNodeObjects"));
			}

		}
	
		log.debug("Final LeafNodes size ::: " + metaDataIds.size());
		log.debug("Final LeafNodes ::: " + metaDataIds);
		
		// 3003
		// 7
		int totalNoOfIterations = (metaDataIds.size() % 500 == 0) ? metaDataIds.size() / 500 : (metaDataIds.size() / 500) + 1; // 1000

		int MAXSIZE = 500;

		List<Integer> intermediateList = new ArrayList<Integer>();
		int iterationNo = 1;

		while (iterationNo <= totalNoOfIterations) {
			if (iterationNo == 1) {
				if (totalNoOfIterations != 1) {
					intermediateList.addAll(metaDataIds.subList(0, MAXSIZE - 1));
					postDataToSWCAPI(intermediateList);
				} else {
					intermediateList.addAll(metaDataIds.subList(0, metaDataIds.size() - 1));
					postDataToSWCAPI(intermediateList);
				}
			} else {
				if (totalNoOfIterations != iterationNo) {
					intermediateList.addAll(metaDataIds.subList(500 * (iterationNo - 1), (500 * iterationNo) - 1));
					postDataToSWCAPI(intermediateList);
				} else {
					intermediateList.addAll(metaDataIds.subList(500 * (iterationNo - 1), metaDataIds.size() - 1));
					postDataToSWCAPI(intermediateList);
				}
			}
			iterationNo++;
		}

	}

	private void postDataToSWCAPI(List<Integer> metaDataIds) {
		// TODO Auto-generated method stub
		try {
			URL url = new URL("http://127.0.0.1:10020/v0.1/mdf_nodes");
			Gson gson = new Gson();
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("POST");
			conn.setDoOutput(true);
			conn.setDoInput(true);

			conn.setRequestProperty("Content-Type", "application/json");

			DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
			String str = gson.toJson(metaDataIds);

			log.debug("metaDataIds::" + metaDataIds);
			log.debug("str:::" + str);
			wr.writeBytes(str);
			wr.flush();
			wr.close();

			InputStream is = conn.getInputStream();
			BufferedReader rd = new BufferedReader(new InputStreamReader(is));
			String line;
			StringBuffer response = new StringBuffer();
			while ((line = rd.readLine()) != null) {
				response.append(line);
				response.append('\r');
			}
			rd.close();
			log.debug("post response ::" + response.toString());

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static void main(String[] args) {
		ArrayList<Integer> arr = new ArrayList<Integer>();
		arr.add(1);
		arr.add(3);
		arr.add(7);
		test(arr);
	}

	public static void test(ArrayList<Integer> metaDataIds) {
		JSONArray jsonArr = new JSONArray();
		jsonArr.addAll(metaDataIds);
		log.debug("jsonarr:::" + jsonArr);

	}

	public static String toJavascriptArray(String[] arr) {
		StringBuffer sb = new StringBuffer();
		sb.append("[");
		for (int i = 0; i < arr.length; i++) {
			sb.append("\"").append(arr[i]).append("\"");
			if (i + 1 < arr.length) {
				sb.append(",");
			}
		}
		sb.append("]");
		return sb.toString();
	}

}
