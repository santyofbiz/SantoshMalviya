package com.cs.eventLogger;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Stream;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cs.eventLogger.model.EventLog;

public class EventLoggerApplication 
{
	private static Logger logger = LoggerFactory.getLogger(EventLoggerApplication.class);
	public static void main( String[] args )     {
    	String filePath;
		try {
			filePath = getFilePath();
			List<EventLog> eventLogList = parseFile(filePath);
			List<String> longEventList = calculateTimeTaken(eventLogList);
			logger.info("Events which take more than 4ms is/are  : " + longEventList);
		} catch (Exception e) {
			logger.error("Exception occurred : " + e);
		}
    } 
    private static String getFilePath() throws IOException {
		Scanner in = new Scanner(System.in);
		System.out.print("Please provide path of the file :");
		String filePath = in.nextLine();
		if(filePath.isEmpty()) {
			logger.error("Please provide valid file path");
		}
		return filePath;
	}
	
	private static List<EventLog> parseFile(String filePath) {
		JSONParser parser = new JSONParser();
    	Path inputFilePath = Paths.get(filePath);
    	List<EventLog> eventLogList = new ArrayList<>();
    	try (BufferedReader br = Files.newBufferedReader(inputFilePath)) {
    		Stream<String> lines = br.lines();
    		lines.forEach(line -> {
    			 try {
					JSONObject  a = (JSONObject ) parser.parse(line);
					EventLog el = new EventLog();
			    	   if(a.get("id") != null) {
			    		   el.setId((String)a.get("id"));
			    	   }
			    	   if(a.get("state") != null) {
			    		   el.setState((String)a.get("state"));
			    	   }
			    	   if(a.get("type") != null) {
			    		   el.setType((String)a.get("type"));
			    	   }
			    	   if(a.get("host") != null) {
			    		   el.setHost((String)a.get("host"));
			    	   }
			    	   if(a.get("timestamp") != null) {
			    		   el.setTimestamp((Long)a.get("timestamp"));
			    	   }
			    	   eventLogList.add(el);
				} catch (Exception e) {
					logger.error("Exception occurred during parsing : " + e);
				}
    		});
    	} catch (Exception e1) {
    		logger.error("Exception occurred during parsing : " + e1);
		}
    	return eventLogList;
	}
	
	private static List<String> calculateTimeTaken(List<EventLog> eventLogList) {
		   Map<String, EventLog> eventMap = new HashMap<>();
		   List<String> longEventList = new ArrayList<>();
	       long timeTaken = 0;
	       for(EventLog event : eventLogList) {
	    	   if(eventMap.containsKey(event.getId())) {
	    		   EventLog evt = eventMap.get(event.getId());
	    		   if(evt != null) {
	    			   String existingState = evt.getState();
	    			   long timestamp = evt.getTimestamp();
	    			   if(existingState != null && existingState.equalsIgnoreCase("STARTED")) {
	    				   timeTaken  = event.getTimestamp() - timestamp;
	    			   } if(existingState != null && existingState.equalsIgnoreCase("FINISHED")) {
	    				    timeTaken =  timestamp - event.getTimestamp();
	    			   }
	    			   if(timeTaken > 4) {
	    				   longEventList.add(evt.getId());
	    			   }
	    			   logger.info("Time taken by event " + evt.getId()+ " is :"+timeTaken);
	    		   }
	    	   } else {
	    		   eventMap.put(event.getId(), event);
	    	   }
	       }
			return longEventList;
		}
}
