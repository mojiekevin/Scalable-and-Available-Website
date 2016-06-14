package test;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import com.sessionmanagement.DataBrickManager;

import RPC.RpcParameter;

public class Test {
	public static void main(String[] args) throws IOException, org.json.simple.parser.ParseException {
		Date curr = new Date();
		System.out.println(curr.toString());
		//curr.setTime(curr.getTime() + 5000);
		//System.out.println(curr.toString());
		
		DateFormat dateFormatter = new SimpleDateFormat ( "E MMM dd HH:mm:ss Z yyyy" );
		String d = curr.toString();
		try {
			
			System.out.println(dateFormatter.parse(d).toString());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		String hello = "hello";
		byte[] byteInfo = RpcParameter.convertToBytes(hello);
		
		try {
			String back = (String) RpcParameter.convertFromBytes(byteInfo);
			System.out.println(back);
		} catch (ClassNotFoundException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//System.out.println("serverID list is " + DataBrickManager.getServerID().toString());
		
		
		InetAddress address = InetAddress.getByName("10.128.136.51");
		int port = 5300;
		DatagramSocket socket = new DatagramSocket(5300);
		DatagramPacket packet = new DatagramPacket(byteInfo, byteInfo.length, 
                address, 5300);
		try {
			socket.send(packet);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
