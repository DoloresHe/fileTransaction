<<<<<<< HEAD
package netInformation;  

import java.util.*;
import java.util.Map.Entry;
import java.util.regex.*;
import org.hyperic.sigar.NetFlags;
import org.hyperic.sigar.NetInterfaceConfig;
import org.hyperic.sigar.NetInterfaceStat;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;


public class Network {
     
    	public static HashMap<Integer,Float> net() throws Exception {
            Sigar sigar = new Sigar();
            String ifNames[] = sigar.getNetInterfaceList();
            HashMap<Integer,Float> speeds=new HashMap<>();
            HashMap<Integer,Float> speeds1=new HashMap<>();//junfen
            
            for (int i = 0; i < ifNames.length; i++) {
                String name = ifNames[i];
                NetInterfaceConfig ifconfig = sigar.getNetInterfaceConfig(name);
                String ipaddress= ifconfig.getAddress();
                if ((ifconfig.getFlags() & 1L) <= 0L) {
                    //System.out.println("!IFF_UP...skipping getNetInterfaceStat");
                    continue;
                }
                HashSet<String> set=new HashSet<>();
                set.add("192.168.0.36");
                set.add("192.168.0.138");
                set.add("192.168.0.29");
                set.add("192.168.8.102");
//                String pattern="((25[0-5]|(2[0-4]\\d)|(1\\d{2})|([1-9]\\d)|(\\d))\\.){2}(42.)(25[0-5]|(2[0-4]\\d)|(1\\d{2})|([1-9]\\d)|(\\d))";
//                if( Pattern.matches(pattern,ipaddress)) {
                //if(ipaddress.equals("192.168.8.102")){
            	if(set.contains(ipaddress)){
                NetInterfaceStat ifstat = sigar.getNetInterfaceStat(name);
                speeds.put(i,(float)ifstat.getTxBytes());
                speeds1.put(i,(float) 1.0);
                }
            }
                try {
                	Thread.sleep(10000); //暂停，每一秒输出一次
                    }catch (InterruptedException e) {
                    	System.out.println(e);
                   }
                float sum=0.0f;      
                for(Map.Entry<Integer, Float> w:speeds.entrySet()) {
                    String name = ifNames[w.getKey()];
                    NetInterfaceStat ifstat = sigar.getNetInterfaceStat(name);
                   // System.out.println(name);
                    speeds.put(w.getKey(),ifstat.getTxBytes()-w.getValue());
                    sum+=w.getValue();
                }		  
                Iterator<Entry<Integer, Float>> it = speeds.entrySet().iterator();
                Iterator<Entry<Integer, Float>> it1 = speeds1.entrySet().iterator();
                while(it.hasNext()&&it1.hasNext()){
                    Entry<Integer, Float> w = it.next();
                    Entry<Integer, Float> w1 = it1.next();
                //for(Map.Entry<Integer, Float> w:speeds.entrySet()) {
                    if(w.getValue()==0) {
                    	sum-=w.getValue();
                    	it.remove();
                    	it1.remove();
                    }
                }       
                float sum1=speeds1.size();   
//                for (Map.Entry<Integer, Float> entry : speeds1.entrySet()) {
//           		 
//        		    System.out.println("Key = " + sigar.getNetInterfaceConfig(ifNames[entry.getKey()]).getAddress() + ", Value = " + entry.getValue());
//        		 
//        		}      
                for(Map.Entry<Integer, Float> w:speeds.entrySet()) {
                    speeds.put(w.getKey(),w.getValue()/sum);
                }       
 
		for (Map.Entry<Integer, Float> entry : speeds1.entrySet()) {
		 
                    speeds1.put(entry.getKey(),entry.getValue()/sum1);
		    System.out.println("Key = " + sigar.getNetInterfaceConfig(ifNames[entry.getKey()]).getAddress() + ", Value = " + entry.getValue());
		 
		}      
                return speeds1;
        }
    	public static HashMap<Integer,String> IP(HashMap<Integer,Float> speeds) throws Exception {
            Sigar sigar = new Sigar();
            String ifNames[] = sigar.getNetInterfaceList();
            HashMap<Integer,String> ips=new HashMap<>();
    		for(Map.Entry<Integer, Float> w:speeds.entrySet()) {
    			ips.put(w.getKey(), sigar.getNetInterfaceConfig(ifNames[w.getKey()]).getAddress());
    		}
    		return ips;
    	}
//    	public static void main(String[] args) {
//    		try{
//    			net();
//    		}	catch (Exception e1) {
//            e1.printStackTrace();
//        }
//    	}
    }  
=======
package netInformation;  

import java.util.*;  
import java.util.regex.*;
import org.hyperic.sigar.NetFlags;
import org.hyperic.sigar.NetInterfaceConfig;
import org.hyperic.sigar.NetInterfaceStat;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;


public class Network {
     
    	public static HashMap<Integer,Float> net() throws Exception {
            Sigar sigar = new Sigar();
            String ifNames[] = sigar.getNetInterfaceList();
            HashMap<Integer,Float> speeds=new HashMap<>();
            
            for (int i = 0; i < ifNames.length; i++) {
                String name = ifNames[i];
                NetInterfaceConfig ifconfig = sigar.getNetInterfaceConfig(name);
                String ipaddress= ifconfig.getAddress();
                if ((ifconfig.getFlags() & 1L) <= 0L) {
                    //System.out.println("!IFF_UP...skipping getNetInterfaceStat");
                    continue;
                }
                
                String pattern="((25[0-5]|(2[0-4]\\d)|(1\\d{2})|([1-9]\\d)|(\\d))\\.){2}(42.)(25[0-5]|(2[0-4]\\d)|(1\\d{2})|([1-9]\\d)|(\\d))";
                if( Pattern.matches(pattern,ipaddress)) {
                NetInterfaceStat ifstat = sigar.getNetInterfaceStat(name);
                speeds.put(i,(float)ifstat.getTxBytes());
                }
            }
                try {
                	Thread.sleep(10000); //暂停，每一秒输出一次
                    }catch (InterruptedException e) {
                    	System.out.println(e);
                   }
                float sum=0.0f;
                for(Map.Entry<Integer, Float> w:speeds.entrySet()) {
                    String name = ifNames[w.getKey()];
                    NetInterfaceStat ifstat = sigar.getNetInterfaceStat(name);
                   // System.out.println(name);
                    speeds.put(w.getKey(),ifstat.getTxBytes()-w.getValue());
                    sum+=w.getValue();
                }		for (Map.Entry<Integer, Float> entry : speeds.entrySet()) {
           		 
        		//    System.out.println("Key = " + sigar.getNetInterfaceConfig(ifNames[entry.getKey()]).getAddress() + ", Value = " + entry.getValue());
        		 
        		}      
                for(Map.Entry<Integer, Float> w:speeds.entrySet()) {
                    speeds.put(w.getKey(),w.getValue()/sum);
                }                
//		for (Map.Entry<Integer, Float> entry : speeds.entrySet()) {
//		 
//		    System.out.println("Key = " + sigar.getNetInterfaceConfig(ifNames[entry.getKey()]).getAddress() + ", Value = " + entry.getValue());
//		 
//		}      
                return speeds;
        }
    	public static HashMap<Integer,String> IP(HashMap<Integer,Float> speeds) throws Exception {
            Sigar sigar = new Sigar();
            String ifNames[] = sigar.getNetInterfaceList();
            HashMap<Integer,String> ips=new HashMap<>();
    		for(Map.Entry<Integer, Float> w:speeds.entrySet()) {
    			ips.put(w.getKey(), sigar.getNetInterfaceConfig(ifNames[w.getKey()]).getAddress());
    		}
    		return ips;
    	}
//    	public static void main(String[] args) {
//    		try{
//    			net();
//    		}	catch (Exception e1) {
//            e1.printStackTrace();
//        }
//    	}
    }  
>>>>>>> 20da882dbe34fa3dc2937dbee827e4134b65af36
