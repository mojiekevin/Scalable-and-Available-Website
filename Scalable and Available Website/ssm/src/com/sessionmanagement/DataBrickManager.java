package com.sessionmanagement;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.ServletContext;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;  
import java.io.InputStreamReader;  
import java.io.BufferedReader;  
import java.io.FileInputStream;  

public class DataBrickManager {
	
	public static boolean localTest = false;
	private static String  relative = "/usr/share/tomcat8";
	public static void setContext(String contextoutside) {
		relative = contextoutside;
	}
	public static int getLocalIndex(String fileRelativePath) {
		int index = -1;
		try {
            
			//String pathname = context.getRealPath("/");
			String pathname = relative + "../amiIndex.txt";
			//JSONParser parser = new JSONParser();
			//Object obj = parser.parse(new FileReader("/home/ec2-user/amiIndex.txt"));
			//String pathname = (String) obj;
			
			//File filename = new File(pathname);
			//InputStreamReader reader = new InputStreamReader (new FileInputStream(filename));
			@SuppressWarnings("resource")
			BufferedReader buffer = new BufferedReader(new FileReader(pathname));
			String str = "";
			str = buffer.readLine();
			index = Integer.parseInt(str);
			System.out.println("parse index is " + index);
			
		} catch (Exception e) {  
            e.printStackTrace();  
        } 		
		return index;
	}
	
	public static int getRebootNumber(String fileRelativePath) {
		int reboot = 0;
		try {
			//String pathname = context.getRealPath("/");
			String pathname = relative +  "../reboot.txt";
			//String pathname = "/home/ec2-user/reboot.txt";
//			JSONParser parser = new JSONParser();
//			Object obj = parser.parse(new FileReader("/home/ec2-user/reboot.txt"));
//			String pathname = (String) obj;
//			File filename = new File(pathname);
//			InputStreamReader reader = new InputStreamReader (new FileInputStream(filename));
//			@SuppressWarnings("resource")
			//BufferedReader buffer = new BufferedReader(reader);
			BufferedReader buffer = new BufferedReader(new FileReader(pathname));
			String str = "";
			str = buffer.readLine();
			reboot = Integer.parseInt(str);
			System.out.println("reboot number is " + reboot );
			
		} catch (IOException e) {  
            e.printStackTrace();  
        } 		
		return reboot;
	}
	
	public static List<ServerID> getServerID() throws IOException, org.json.simple.parser.ParseException{
		
		if(localTest) {
			List<ServerID> localServer = new ArrayList<ServerID>();
			localServer.add(new ServerID("127.0.0.1:5300"));
			localServer.add(new ServerID("10.128.128.186:5300"));
			return localServer;			
		}
	
		JSONParser parser = new JSONParser();
        List<ServerID> localServer = new ArrayList<ServerID>();
        HashMap<Integer,String> map = new HashMap<Integer,String>();
        try{
        	//Object obj = parser.parse(new FileReader("/Users/jd/Documents/workspace2/ssm/allServerInfo.txt"));
        	//String jsonfilePath = .getRealPath("/");
        	String pathname = relative +  "../allServerInfo.txt";
        	Object obj = parser.parse(new FileReader(pathname));
        	JSONObject jsonObject = (JSONObject) obj;
        	JSONArray items = (JSONArray) jsonObject.get("Items");
        	for(Object o : items){
        		JSONObject json = (JSONObject)o;
        		JSONArray attributes = (JSONArray) json.get("Attributes");
        		String serverid = "";
        		for(Object oo : attributes){
        			JSONObject j = (JSONObject)oo;
        			serverid += (j.get("Value"));
        			
        		}
        		System.out.println("server id " + serverid);
        		map.put(((int) serverid.charAt(0))-48, serverid.substring(1));
        	}   
        	for(int i=0; i<map.size(); i++){
        		ServerID id = new ServerID(map.get(i)+":5300");
        		localServer.add(id);
        	}
        			
        	return localServer;
	}
        catch (FileNotFoundException e) {
    		// TODO Auto-generated catch block
    		e.printStackTrace();
    	} 
        catch (IOException e) {
    		// TODO Auto-generated catch block
    		e.printStackTrace();
        } 
        catch (ParseException e) {
        	// TODO Auto-generated catch block
        	e.printStackTrace();
        }
		return null;
               
	}
        
        
	
	public static List<ServerID> getServerIdByNum(int num) {
		return null;
	}
	
	public static int getServerNum() {
		return 3;
	}
	
	
	
	@SuppressWarnings("finally")
	public static ServerID getLocalServerID() {
		ServerID ser = null;
		if(localTest) {
			
			return new ServerID("127.0.0.1:5300");
		} else {
			
			int n = getLocalIndex(relative);
			System.out.println("get index" + n);
			List<ServerID> list;
			
			try {
				list = getServerID();
				System.out.println(list.get(0).toString() + list.get(1).toString() );
				System.out.println("list is null" + list==null);
			    ser = list.get(n);		
			    return ser;
			} catch (IOException | org.json.simple.parser.ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
		}
		return null;
	}
	
}
