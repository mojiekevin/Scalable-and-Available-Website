package RPC;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

public class RpcParameter {
	private RpcParameter() {
		
	}
	public static final int portPROJ1BRPC = 5300;
	public static final int F = 1;
	public static final int R = 2;//F + 1;
	public static final int WQ = 2;//R;
	public static final int W = 3;//2 * F + 1;
	public static final int READ = 0;
	public static final int WRITE = 1;
	public static final int sessionLength = 512;
	public static  boolean localTest = true;
	
	/** helper function
	 * http://stackoverflow.com/a/30968827
	 * @return
	 */
	public static byte[] convertToBytes(Object object) {
		 try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
		         ObjectOutput out = new ObjectOutputStream(bos)) {
		        out.writeObject(object);
		        return bos.toByteArray();
		    }  catch  (IOException  e){
		    	System.err.print(e);
		    }
		 return null;
	}
	/** helper function
	http://stackoverflow.com/a/30968827
	*/
	public static Object convertFromBytes(byte[] bytes) throws IOException, ClassNotFoundException {
	    try (ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
	         ObjectInput in = new ObjectInputStream(bis)) {
	        return in.readObject();
	    } 
	}
	
	

}
