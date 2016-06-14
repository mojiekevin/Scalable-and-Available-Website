package RPC;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import javax.servlet.http.Cookie;

import org.json.simple.parser.ParseException;

import com.sessionmanagement.DataBrickManager;
import com.sessionmanagement.ServerID;
import com.sessionmanagement.Session;

public class RPCClient  {
	
	
	public void write(Session session) throws IOException, ClassNotFoundException, ParseException {
		String callID = UUID.randomUUID().toString();
		String message = callID + "_"  + new Integer(RpcParameter.WRITE).toString() + "_" 
						+ session.generateSessionKey() + "_"
						+ session.getMessage() + "_"
						+ session.getExpirationTime();
		System.out.println("client message to send" + message);
		DatagramSocket rpcSocket = new DatagramSocket();
		rpcSocket.setSoTimeout(30000);
		
		byte[] encodeInfo = RpcParameter.convertToBytes(message);
		
		//final int[] numOfwrite = new Random().ints(0,DataBrickManager.getServerNum()).distinct().limit(RpcParameter.W).toArray();
		int[] numOfwrite = {0, 1, 2};
		List<ServerID> serverList = DataBrickManager.getServerID();
		List<ServerID> repliedBricks = new ArrayList<ServerID>();
		for(int index : numOfwrite){
			ServerID s = serverList.get(index);
			System.out.println("target ip address " + s.toString());
	        DatagramPacket sendPkt = new DatagramPacket(encodeInfo, encodeInfo.length, s.getIP(), s.getPort());
	        rpcSocket.send(sendPkt);
	        
	        
		}
		session.clearLocation();
		byte[] inBuf = new byte[RpcParameter.sessionLength];
		DatagramPacket recvPkt = new DatagramPacket(inBuf, inBuf.length);
		System.out.println("received item number outside" + repliedBricks.size());
		while(repliedBricks.size() < RpcParameter.WQ){
			System.out.println("received item number " + repliedBricks.size());
			recvPkt.setLength(inBuf.length);
			//rpcSocket.setSoTimeout(30000);
			rpcSocket.receive(recvPkt);
			
			String response = (String) RpcParameter.convertFromBytes(inBuf);
			//System.out.println("1111111111111++++++++++++++++++");
			System.out.println("recieved brick in client write " + response);
			//System.out.println("22222222222222++++++++++++++++++");
		    String[] decodeInfo = response.split("_");
		    String returnID = decodeInfo[0];
		    if(returnID.equals(callID)) {
		    	
		    	
		    	
		    	ServerID locationInfo = new ServerID(decodeInfo[1]);
		    	repliedBricks.add(locationInfo);
		    }
			
		}
		session.updateLocationByOnce(repliedBricks);
		rpcSocket.close();
		 
	}
	
	
	/* UDP message format
	 * callerID, operation code, operation code argument
	 * 
	 * */
	

	public boolean read(Session session) throws IOException, ClassNotFoundException, EmptyBodyException, CorruptedCookieInfoException, NullPointerException{
		boolean readSuccess = false;
		String callID = UUID.randomUUID().toString();
		
		String queryMessage = "";
		queryMessage += callID;
		queryMessage += "_";
		queryMessage += new Integer(RpcParameter.READ).toString();
		queryMessage += "_";
		queryMessage += session.generateSessionKey();
		
		DatagramSocket rpcSocket = new DatagramSocket();
		rpcSocket.setSoTimeout(30000);
		byte[] encodeInfo = RpcParameter.convertToBytes(queryMessage);
		List<ServerID> locations = session.getLocation();
		/* Send message format
		 *  callID _ READ _ sessionKey
		 * */
		if(locations != null){
			for(ServerID server : locations) {
				DatagramPacket sendPacket = new DatagramPacket(encodeInfo, encodeInfo.length, server.getIP(), server.getPort());
				rpcSocket.send(sendPacket);
			}
		}
		
		byte[] inBuf = new byte[RpcParameter.sessionLength];
		DatagramPacket recvPkt = new DatagramPacket(inBuf, inBuf.length);
		String[] decodeInfo = null;
		do{
			recvPkt.setLength(inBuf.length);
			rpcSocket.setSoTimeout(300000);
			rpcSocket.receive(recvPkt);
			
			String response = (String) RpcParameter.convertFromBytes(inBuf);
			decodeInfo = response.split("_");
			
		} while (!decodeInfo[0].equals(callID));
		if(decodeInfo.length == 2) {
		// contain valid caller id and message
			readSuccess = true;
			session.ResetMessage(decodeInfo[1]);
		}
		//else {
		//	throw new CorruptedCookieInfoException("return read does not contain message");
		//}
		
		rpcSocket.close();
		return readSuccess;
		
	
	}
	
	public Session read(boolean[] flag, String[] tokens) {
		 //  svrID_rebootNum_sessNum_version_S1_S2 ... Swq
		try {
			System.out.println("begin read");
			ServerID serverID = new ServerID(tokens[0]);
			int rebootNum = Integer.parseInt(tokens[1]);
			int sessionNum = Integer.parseInt(tokens[2]);
			int version = Integer.parseInt(tokens[3]);
			List<ServerID> answeredServerID = new ArrayList<>();
			if(tokens.length > 4){
				for(int i = 4; i < 4 + RpcParameter.WQ; i++) {
					answeredServerID.add(new ServerID(tokens[i]));
				}
			}
			Session sessionToBeRead = new Session(serverID, rebootNum, sessionNum, version, answeredServerID);
			
			flag[0] = this.read(sessionToBeRead);
			return sessionToBeRead;
		
		} catch (CorruptedCookieInfoException e) {
		
			flag[0] = false;
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			flag[0] = false;
			e.printStackTrace();
		} catch (IOException e) {
			flag[0] = false;
			e.printStackTrace();
		} catch (EmptyBodyException e) {
			flag[0] = false;
			e.printStackTrace();
		} catch (NullPointerException e) {
			flag[0] = false;
			e.printStackTrace();
		}
		return null;	
	}
	

	public void writeTo(Session session)  {
		try {
			System.out.println("begin to write");
			this.write(session);
			System.out.println("end to write");
			
		} catch (CorruptedCookieInfoException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public class CorruptedCookieInfoException extends NullPointerException {
		public CorruptedCookieInfoException(String error) {
			super(error);
		}
		
	}
	
	public class EmptyBodyException extends Exception {
		public EmptyBodyException(String error) {
			super(error);
		}
	}

	
}
