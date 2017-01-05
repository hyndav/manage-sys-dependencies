/**
 * 
 */
package com.cisco.Interface;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.ne;
import org.bson.Document;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cisco.env.MongoDBDataSource;
import com.google.gson.Gson;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;

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

		List<Integer> pidMappingsCursor = mdfPIDMappings.distinct("mdfLeafNodeId", Integer.class)
				.filter(new Document("itemName", new Document("$in", sbpPIDs))).into(new ArrayList<Integer>());

		log.debug("pidMappingsCursor size::::: " + pidMappingsCursor.size());

		MongoCollection<Document> mdfMetaDatas = mdfDatabase.getMdfMetaDatas();

		BasicDBObject metaDataquery = new BasicDBObject();
		metaDataquery.put("mdfLeafNodeId", new BasicDBObject("$in", pidMappingsCursor));
		metaDataquery.put("lifecycle", new BasicDBObject("$ne", "obsolete"));

		BasicDBObject projectFields = new BasicDBObject("mdfNodeObjects", true).append("_id", false)
				.append("mdfLeafNodeId", true);

		List<Document> mdfMetaDataIDsCursor = mdfMetaDatas.find(metaDataquery).projection(projectFields)
				.into(new ArrayList<>());

		log.debug("mdfMetaDatas Id(s) Array :: " + mdfMetaDataIDsCursor.get(0).get("mdfNodeObjects"));
		log.debug("mdfMetaDatas Id(s) ID :: " + mdfMetaDataIDsCursor.get(0).get("mdfLeafNodeId"));

		for (Document mdfMetaDataIDsCursorElem : mdfMetaDataIDsCursor) {
			log.debug("mdfMetaDatas Id(s) For :: " + mdfMetaDataIDsCursorElem.get("mdfNodeObjects"));
			metaDataIds.add((Integer)mdfMetaDataIDsCursorElem.get("mdfLeafNodeId"));
			if (mdfMetaDataIDsCursorElem.get("mdfNodeObjects") != null) {
				metaDataIds.addAll((List<Integer>) mdfMetaDataIDsCursorElem.get("mdfNodeObjects"));
			}

		}
		log.debug("Final LeafNodes  ::: " + metaDataIds);

		try {
			URL url = new URL("http://127.0.0.1:10020/v0.1/mdf_nodes");
			Gson gson = new Gson();
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/json");//?
			conn.setDoInput(true);
			conn.setDoOutput(true);
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

}
