package com.sessionmanagement;

import java.net.InetAddress;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class SessionManager {
	private SessionManager(){
		
	}
	private final static ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();
	protected final static Lock rLock = rwl.readLock();
	protected final static Lock wLock = rwl.writeLock();
	private final static String cookieName = "CS5300PROJ1SESSION";
	protected static ConcurrentHashMap<String, Session> sessionContainer = new ConcurrentHashMap<>();
	protected static int ONESECOND = 1 * 1000;
	protected static final String cookieDomain = ".dj327.bigdata.systems";
	/**
	 * parse request cookie, get the session from cookie
	 * @param request
	 * @return
	 */
	public static Session getSessionFromRequest(HttpServletRequest request) {
		
		
		Cookie[] cookies = request.getCookies();
	    Cookie matchingCookie = null;
		for( Cookie cookie : cookies ) {
			System.out.println("cookie name" + cookie.getName());
			if(cookieName.equals(cookie.getName())) {
				matchingCookie = cookie;
				break;
			}
		}
		
		if(matchingCookie == null) {
			return null;
		}
		
		
		Session sessionInRecord = getSessionFromCookie(matchingCookie);
		
		return sessionInRecord;
	}
	/**
	 * helper function to get the session from cookie
	 * @param cookie
	 * @return
	 */
	
	private static Session getSessionFromCookie(Cookie cookie) {
		String cookieContent = cookie.getValue(); // in project1b the id will be lengthy
		Session session = null;
		rLock.lock();
		try{
			if(sessionContainer.containsKey(cookieContent)){
				session = sessionContainer.get(cookieContent);
			}
		}
		finally{
			rLock.unlock();
		}
		return session;
	}
	
	
	public static String getSessionInfoFromCookie(HttpServletRequest request) {
		Cookie[] cookies = request.getCookies();
		Cookie matchingCookie = null;
		if(cookies == null) {
			return "";
		}
		for(Cookie cookie : cookies) {
			if(cookieName.equals(cookie.getName())) {
				matchingCookie = cookie;
				break;
			}
		}
		
		if(matchingCookie == null) {
			return "";
		} 
		System.out.println(matchingCookie.getValue());
		return matchingCookie.getValue();
	}
	
	/**
	 * generate new session
	 * @return session
	 */
	public static Session generateNewSession(ServerID localAddress, int rebootNum, int sessionNum) {
		// TODO Auto-generated method stub
		System.out.println("local address " + localAddress.toString());
		return new Session(localAddress, rebootNum,  sessionNum);
	}
	
	public static void addNewCookie(HttpServletResponse response, Session session) {
		Cookie cookie = new Cookie(cookieName, session.generateInfo());
		cookie.setDomain(cookieDomain);
		cookie.setPath("/");
		cookie.setMaxAge(60*9);
		response.addCookie(cookie);
	}
	
	public static void storeSession(Session session){
		wLock.lock();
		try{
			sessionContainer.put(session.getID(), session);
		} finally{
			wLock.unlock();
		}
	}
	
	
	public static void storeSession(String serverid, String rebootNum, String sessNum, String version,
			String clientMessage, String time, ServerID clientOrigin) {
		// TODO Auto-generated method stub
		String sessionKey = serverid + "_" + rebootNum + "_" + sessNum + "_" + version;
		ServerID localServerId = new ServerID(serverid);
		int reboot_num = Integer.parseInt(rebootNum);
		int sess_num = Integer.parseInt(sessNum);
		int version_num = Integer.parseInt(version);
		
		DateFormat dateFormatter = new SimpleDateFormat ( "E MMM dd HH:mm:ss Z yyyy" );
		Date discardTime = null;
		try {
			
			discardTime = dateFormatter.parse(time);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		Session sessionToStore = new Session(localServerId, reboot_num, sess_num, version_num);
		
		sessionToStore.setExpirationTime(discardTime);
		sessionToStore.ResetMessage(clientMessage);
		sessionToStore.addLocation(clientOrigin);
		System.out.println("session key to store " + sessionKey);
		wLock.lock();
		try{
			if(!sessionContainer.containsKey(sessionKey)) {
				System.out.println("new session in hashtable");
				sessionContainer.put(sessionKey, sessionToStore);
			} else {
				System.out.println("update exist session in hashtable");
				Session existSession = sessionContainer.get(sessionKey);
				existSession.addLocation(clientOrigin);
				existSession.ResetMessage(clientMessage);
				existSession.setExpirationTime(discardTime);
				sessionContainer.put(sessionKey, existSession);
			}
		} finally {
			wLock.unlock();
		}
	}
	
	/**
	 * timer thread to check session expiration time every one second
	 */
	public static void cleanExpiredSession(){
		Timer timer = new Timer();
		timer.schedule(new CleanTask(), ONESECOND, ONESECOND);
	}
	
	public static Session findSessionByID(String key) {
		//String[] tokens = ID.split("_");
		Session session = null;
		rLock.lock();
		try{
		  if(sessionContainer.containsKey(key)) {
			  System.out.println("locate session in hashtable");
			  session = sessionContainer.get(key);
		  }
		} finally{
			rLock.unlock();
		}
		return session;
		
	}
	static class CleanTask extends TimerTask{

		@Override
		public void run() {
			wLock.lock();
			try{
				Iterator<String> it = sessionContainer.keySet().iterator();
				while(it.hasNext()) {
					
					String sessionID = (String) it.next();
					Session session = sessionContainer.get(sessionID);
					if(session.getExpirationInSecond().before(new Date())) {
						sessionContainer.remove(sessionID);
					}
				}
			}finally{
				wLock.unlock();
			}
			
		}
		
	}
	

}
