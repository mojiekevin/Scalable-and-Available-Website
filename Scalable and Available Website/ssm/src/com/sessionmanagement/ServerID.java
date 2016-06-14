package com.sessionmanagement;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class ServerID {
	private InetAddress ip;
	private int port;
	
	public ServerID(InetAddress ipaddress, int port) {
		this.ip = ipaddress;
		this.port = port;
	}
	
	public ServerID(String serverId) {
		String[] serverInfo = serverId.split(":");
		String ip = serverInfo[0];
		try {
			this.ip = InetAddress.getByName(serverInfo[0]);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.port = Integer.parseInt(serverInfo[1]);
		
	}
	
	
	public InetAddress getIP(){
		return ip;
	}
	
	public int getPort(){
		return port;
	}
	
	
	
	public String toString() {
		return ip.getHostAddress() + ":" + new Integer(port).toString();
	}

	
	
	
}
