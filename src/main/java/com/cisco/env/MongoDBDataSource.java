package com.cisco.env;

import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class MongoDBDataSource {

	final static Logger log = LoggerFactory.getLogger(MongoDBDataSource.class.getName());

	public static MongoCollection<Document> mdfPIDMappings;
	public  MongoCollection<Document> getMdfPIDMappings() {
		return mdfPIDMappings;
	}

	

	public  MongoCollection<Document> getMdfMetaDatas() {
		return mdfMetaDatas;
	}


	public static MongoCollection<Document> mdfMetaDatas;
	public static MongoCollection<Document> subscriptionLines;

	private static MongoClient mongoClient;

	public static final String subscription_db = "subsription";
	public static final String mdfmap_db = "mdfmap";

	public MongoDBDataSource(String mongoDB) {

		if (mongoDB.equalsIgnoreCase(mdfmap_db)) {
			String dbInstance = EnvUtil.get("MDFMAP_MONGODB_CONNECTION_STRING");
			String dbName = EnvUtil.get("MDFMAP_MONGODB_DBNAME");
			String userName = EnvUtil.get("MDFMAP_MONGODB_USERNAME");
			String password = EnvUtil.get("MDFMAP_MONGODB_PASSWORD");
			String replicaSet = EnvUtil.get("MDFMAP_MONGODB_REPLICA_SET");
			String collName = EnvUtil.get("MDFMAP_COLLECTION_NAME");
			String collName1 = EnvUtil.get("MDFMAP_COLLECTION_NAME_1");

			String dbURI = "mongodb://" + userName + ":" + password + "@" + dbInstance + "/" + dbName + "?"
					+ replicaSet;
			log.debug("DBUri:::"+dbURI);
			MongoClientURI uri = new MongoClientURI(dbURI);

			mongoClient = new MongoClient(uri);
			MongoDatabase db = mongoClient.getDatabase(dbName);

			mdfPIDMappings = db.getCollection(collName);
			mdfMetaDatas = db.getCollection(collName1);
		} else if (mongoDB.equalsIgnoreCase(subscription_db)) {
			String dbInstance = EnvUtil.get("SUBSCRIPTION_MONGODB_CONNECTION_STRING");
			String dbName = EnvUtil.get("SUBSCRIPTION_MONGODB_DBNAME");
			String userName = EnvUtil.get("SUBSCRIPTION_MONGODB_USERNAME");
			String password = EnvUtil.get("SUBSCRIPTION_MONGODB_PASSWORD");
			String replicaSet = EnvUtil.get("SUBSCRIPTION_MONGODB_REPLICA_SET");
			String collName = EnvUtil.get("SUBSCRIPTION_COLLECTION_NAME");

			String dbURI = "mongodb://" + userName + ":" + password + "@" + dbInstance + "/" + dbName + "?"
					+ replicaSet;

			MongoClientURI uri = new MongoClientURI(dbURI);

			mongoClient = new MongoClient(uri);
			MongoDatabase db = mongoClient.getDatabase(dbName);

			subscriptionLines = db.getCollection(collName);
		}
	}
	
	public void closeMongoDB(){
		mongoClient.close();
	}
	
	public MongoCollection<Document> getSubscriptionLines() {
		return subscriptionLines;
	}
}