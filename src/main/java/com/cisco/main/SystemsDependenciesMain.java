package com.cisco.main;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SystemsDependenciesMain {
	private static final String FILENAME = "C:/Users/hmarella/Desktop/CISCO/EB-Dev/CA-technologies/userInput.txt";
	//Also attached the file reference in the same project.You can change the location of the filke as and when required for flexibility

	public static Map<String, List<String>> dependencyList = new LinkedHashMap<String, List<String>>();;
	static List<String> commandLineInput = new ArrayList<String>();
	final static String DEPEND = "DEPEND";
	final static String INSTALL = "INSTALL";
	final static String REMOVE = "REMOVE";
	final static Logger log = LoggerFactory.getLogger(SystemsDependenciesMain.class);

	public static void main(String[] args) throws Exception {
		Date date = new Date();
		log.info("Start Time :" + date);
		log.info("Started INPUT :: Accepting the system dependencies and its components : ");

		 loadUserInputFromFile();  //accepts input from FILE
		loadUserInputFromCommandLine(); //only accepting input from commandline is completed.Half Completed
		printOutputFromFile(); //prints outputs from FILE
		
		log.info("Completed printing the Output ::: ");

	}

	private static void loadUserInputFromCommandLine() {
		

		Scanner scanner = new Scanner(new InputStreamReader(System.in));
		log.debug("Reading input from console using Scanner in Java ");
		log.debug("Please enter your input: ");

		while (scanner.hasNext()) {
			String input = scanner.nextLine();
			log.debug(input);
			if (input.length() > 80) {
				System.out.println("Cannot Accept more than 80 characters as input for each line ");
				return;
			}
			String[] items = input.split(" ");
			for (String item : items) {
				if (item.length() > 10 || (!item.equals(item.toUpperCase()))) {
					System.out.println(
							"Cannot Accept more than 10 characters as input item name (OR) inout must be UPPERCASE");
					return;
				}
			}

			commandLineInput.add(input);

		}
	}

		

	private static void printOutputFromFile() {
		boolean presentInKey = false;
		boolean presentInValue = false;

		printLinkedHashmap();

		for (Entry<String, List<String>> e : dependencyList.entrySet()) {
			Iterator<String> keyIter = dependencyList.keySet().iterator();
			loop: if (e.getKey().contains("DEPEND")) {
				System.out.println(e.getKey() + " " + e.getValue());
			} else if (e.getKey().contains("INSTALL")) {
				System.out.println(e.getKey());

				if (dependencyList
						.containsKey("DEPEND" + e.getKey().substring(e.getKey().indexOf(" "), e.getKey().length()))) {
					for (String str : dependencyList
							.get("DEPEND" + e.getKey().substring(e.getKey().indexOf(" "), e.getKey().length()))) {
						System.out.println("      Installing " + str);
					}

				} else {
					System.out.println(
							"    Installing" + e.getKey().substring(e.getKey().indexOf(" "), e.getKey().length()));
				}
			} else if (e.getKey().contains("REMOVE")) {
				String str1 = "";

				while (keyIter.hasNext()) {
					str1 = (String) keyIter.next();
					if (str1.contains("DEPEND" + e.getKey().substring(e.getKey().indexOf(" "), e.getKey().length()))) {
						System.out.println(
								"     Removing " + e.getKey().substring(e.getKey().indexOf(" "), e.getKey().length()));
						dependencyList.put(str1, null);
						presentInKey = true;
						break loop;
					}
				}

				outerloop: for (List<String> str : dependencyList.values()) {
					// log.debug(e.getKey().substring(e.getKey().indexOf(" "),
					// e.getKey().length()) + " " + str);
					Iterator<String> iter = str.iterator();

					while (iter.hasNext()) {

						if (((String) iter.next()).indexOf(
								e.getKey().substring(e.getKey().indexOf(" "), e.getKey().length()).trim()) != -1) {
							System.out.println(
									"      " + e.getKey().substring(e.getKey().indexOf(" "), e.getKey().length())
											+ " is still needed");
							presentInValue = true;
							break outerloop;

						}
					}

				}

				if (!presentInKey && !presentInValue) {
					System.out.println(str1 + "  is not installed");
				}

			} else if (e.getKey().contains("LIST")) {
				System.out.println(e.getKey());
				for (List<String> str : dependencyList.values()) {
					if (str != null) {
						for (String s : str)
							System.out.println(s);
					}
				}

			}
		}

	}

	private static void printLinkedHashmap() {
		// TODO Auto-generated method stub

		for (String s : dependencyList.keySet())
			log.debug(s);
	}

	private static void loadUserInputFromFile() {

		BufferedReader br = null;
		StringBuilder sb = new StringBuilder();
		File file = new File(FILENAME);
		try {
			System.out.println(file.getCanonicalPath());
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		try {

			FileInputStream ft = new FileInputStream(file);

			DataInputStream in = new DataInputStream(ft);
			br = new BufferedReader(new InputStreamReader(in));
			String strline;
			String[] strArr = new String[5];

			while ((strline = br.readLine()) != null) {

				if (strline != "" && strline != "[]" && strline.length() > 0) {
					strArr = strline.split(" ");
				}

				StringBuilder key = new StringBuilder();
				List<String> strList = new ArrayList<String>();
				for (int i = 0; strArr.length > 0 && i < strArr.length; i++) {
					if (i == 0 || i == 1) {
						key.append(strArr[i] + " ");
					} else {
						strList.add(strArr[i]);
					}
				}
				if (!dependencyList.containsKey(key)) {
					dependencyList.put(key.toString(), strList);
				} else {
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
