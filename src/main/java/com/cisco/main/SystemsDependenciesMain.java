package com.cisco.main;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Spliterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SystemsDependenciesMain {
	private static final String FILENAME = "C:/Users/hmarella/Desktop/CISCO/EB-Dev/CA-technologies/userInput.txt";
	public static Map<String, List<String>> dependencyList=   new HashMap<String, List<String>>();;

	final static Logger log = LoggerFactory.getLogger(SystemsDependenciesMain.class);

	public static void main(String[] args) throws Exception {
		Date date = new Date();
		log.info("Start Time :" + date);
		log.info("Started INPUT :: Accepting the system dependencies and its components : ");

		loadUserInput();
		loadOutput();
		log.info("Completed Output ::: ");

	}

	private static void loadOutput() {
		// TODO Auto-generated method stub
		for(Entry<String, List<String>> e:dependencyList.entrySet()){
			if(dependencyList.get(e.getKey()).contains("DEPEND")){
				
			}else if(dependencyList.get(e.getKey()).contains("INSTALL")){
				
			}else if(dependencyList.get(e.getKey()).contains("REMOVE")){
				
			}else{
				
			}
    	}
		
		
		
	}

	private static void loadUserInput(){
		 

				BufferedReader br = null;
				StringBuilder sb = new StringBuilder();
				File file = new File(FILENAME);
				try {
					System.out.println(file.getCanonicalPath());
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				String line;
				try {
//					File file1 = new File(".");
//					for(String fileNames : file1.list()) System.out.println(fileNames);
					
					FileInputStream ft = new FileInputStream(file);

		            DataInputStream in = new DataInputStream(ft);
		             br = new BufferedReader(new InputStreamReader(in));
		            String strline;
		            String[] strArr=new String[5];

		            while((strline = br.readLine()) != null){
		            	log.debug("Str line ::"+strline);
		                log.debug("SPlit length ::"+strline.split(" ").length);
		                if(strline!="" && strline!="[]" && strline.length()>0){
		                strArr = strline.split(" ");
		            }
		             
		                StringBuilder key = new StringBuilder();
		                List<String> strList=new ArrayList<String>();
		                	for(int i=0;strArr.length>0 && i<strArr.length;i++){
		                		if(i==0 || i==1){
		                	key = key.append(" ").append(strArr[i]);
		                		}
		                		else{
		                			strList.add(strArr[i]);
		                		}		                		
		                	}
		                	log.debug("Key::"+key+" ----> "+strList);
		                	if(!dependencyList.containsKey(key)){
		                		dependencyList.put(key.toString(), strList);
		                		}else{
		                			dependencyList.get(key).addAll(strList);
		                		}
		                	
		              
		                
		                
		                
		                
		                
		                
		            }

				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					if (br != null) {
						try {
							br.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
				
	 }

}
