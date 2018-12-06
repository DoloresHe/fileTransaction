<<<<<<< HEAD
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
=======
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
>>>>>>> 20da882dbe34fa3dc2937dbee827e4134b65af36
}