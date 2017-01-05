package com.cisco.main;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cisco.Interface.MDFInterface;
import com.cisco.Interface.MDFObjectSBPInterfaceImpl;

public class MDFWhiteListOneTimeDataMigration {

	final static Logger log = LoggerFactory.getLogger(MDFWhiteListOneTimeDataMigration.class);
	
	public static void main(String[] args) throws Exception {
		
		Date date = new Date();
		log.info("Start Time :" + date);
		
		MDFInterface mdfObjectInterface = new MDFObjectSBPInterfaceImpl();
		mdfObjectInterface.runQuery();
		
		log.info("Started one-time data dump MDFWhiteListOneTimeDataMigration : ");
	}	
}