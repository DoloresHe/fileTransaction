package netInformation;

import java.net.*;
import java.util.Enumeration;

public class networkTest
{
	 public static void main(String[] args) throws Exception
	    {
	        Enumeration<NetworkInterface> nis = NetworkInterface.getNetworkInterfaces();
	        while (nis.hasMoreElements())
	            System.out.println(nis.nextElement());
	    }
}