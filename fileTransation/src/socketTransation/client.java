package socketTransation;    
import java.io.BufferedInputStream;      
import java.io.DataInputStream;  
import java.io.DataOutputStream;  
import java.io.File;  
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;  
import java.util.Vector;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;  
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import netInformation.Network;      
import fileOperation.file;

public class client {  
      
       // private static ArrayList<String> fileList = new ArrayList<String>();  
		private static String[] names;  
		 static int count=0;
		 static final Object object=new Object();
		private static HashMap<Integer,String> ips=new HashMap<>() ; 
        private static String sendFilePath = "D:/test.sql";   
        private static String fileName = "test.sql";

		 static int portP=6663;
        /** 
         * ���������Ĺ�������ʹ��Ĭ�ϵĴ����ļ����ļ��У� 
         */  
        public client(){  
            getFilePath(sendFilePath);  
        }  
          
        /*
         * �������̴߳���
         */
        public void service(){  
            ExecutorService executorService = Executors.newCachedThreadPool();  
           // ExecutorService executorService1 = Executors.newCachedThreadPool();  
//            Vector<Integer> vector = getRandom(fileList.size());  
//            for(Integer integer : vector){  
        	long startTime = System.currentTimeMillis(); //����ʼ��¼ʱ��
    		for(Map.Entry<Integer, String> w:ips.entrySet()) {
//                String filePath = fileList.get(integer.intValue());  
                executorService.execute(sendFile(w.getValue()));  
               // executorService1.execute(ppp(w.getValue(),"120.78.188.54",6669));
                new Thread(ppp(w.getValue(),"120.78.188.54",portP++)).start();
            }  
//try {
//	executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
//	finish();
//} catch (InterruptedException e) {
//
//}
    		executorService.shutdown();
            while(true){  
               if(executorService.isTerminated()){  
                   long endTime   = System.currentTimeMillis(); //���������¼ʱ��
                   long TotalTime = endTime - startTime;       //������ʱ��
                   System.out.println("��ʱ:  " + ((double)TotalTime)/1000+"s" ); 
                    finish();
                    System.out.println("���е����̶߳������ˣ�");  
                    break;  
                }  
                try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}    
            }    		
        	
        }  
          
      /*
       * ��ȡ����Ҫ������ļ���
       */
        private void getFilePath(String fileName){  
        	File file1 =  new File(fileName);
            try {
            	HashMap<Integer, Float> size=Network.net();
            	names = file.divideFile(file1.getAbsolutePath(),204800);
            	ips=Network.IP(size);
            	for(Map.Entry<Integer, String> w:ips.entrySet()) {
                    System.out.println(w.getValue());
                }
            	//String[] name= {"C:\\test\\test.sql1","C:\\test\\test.sql2"};
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }  
             
                  /*
                   * ���Ӳ������ļ�
                   */          
        private static Runnable sendFile(String localIp){  
            return new Runnable(){  
                  
                private Socket socket = null;  
                private String ip ="120.78.188.54";  
                private int port = 6667;  
                private String filePath;
                public void run() {   
		            if(createConnection()){
		                        try {  
		                            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());  
		                while(count<names.length) {
		                    synchronized (object){
		                    	filePath=names[count++];
		                    }
		                	//long startTime = System.currentTimeMillis(); //����ʼ��¼ʱ��
		                    System.out.println("��ʼ�����ļ�:" + filePath);  
		                    File file1 = new File(filePath);   
		                        int bufferSize = 8192;  
		                        byte[] buf = new byte[bufferSize];  
		                            DataInputStream fis = new DataInputStream(new BufferedInputStream(new FileInputStream(filePath)));  
		                              
		                            dos.writeUTF(file1.getName());  
		                            dos.flush();  
		                            dos.writeUTF(file.getCRC32(filePath));
		                            dos.flush();  
		                            dos.writeLong(file1.length());  
		                            dos.flush();  
		                              
		                            int read = 0;  
		                            int passedlen = 0;  
		                            long length = file1.length();    //���Ҫ�����ļ��ĳ���  
		                            while ((read = fis.read(buf)) != -1) {  
		                                passedlen += read;  
		                              //  System.out.println("�Ѿ�����ļ� [" + file.getName() + "]�ٷֱ�: " + passedlen * 100L/ length + "%");  
		                                dos.write(buf, 0, read);  
		                            }  
		                           System.out.println("�ļ� " + filePath + "�������!"); 
		                           dos.flush();  
		                           fis.close();  
		                }
		                           dos.close();  
								socket.close();
		                        } catch (Exception e) {  
		                            e.printStackTrace();  
		                        }  
		                
		                           
		//                        long endTime   = System.currentTimeMillis(); //���������¼ʱ��
		//                        long TotalTime = endTime - startTime;       //������ʱ�� 
		                      //  System.out.println(localIp+"��ʱ:  " + ((double)TotalTime)/1000+"s" ); 
	                        
	                    }  
	                
                
            }
        
               
           /*
            * socket���ú�����
            */
                private boolean createConnection() {  
                    try {  
                        //socket = new Socket(ip, port);
//                    	socket = new java.net.Socket();
//                    	socket.connect(new InetSocketAddress(ip, port));
                    	
//                    	NetworkInterface nif = NetworkInterface.getByName("eth26");
//                    	System.out.println((nif == null) ? "����ӿڲ�����!" : nif);
//                    	Enumeration<InetAddress> nifAddresses = nif.getInetAddresses();
                    	socket = new java.net.Socket();
                    	socket.bind(new InetSocketAddress(localIp, 6665));
                    	socket.connect(new InetSocketAddress(ip, port));
                        System.out.println("���ӷ������ɹ���");  
                        return true;  
                    } catch (Exception e) {  
                        System.out.println("���ӷ�����ʧ�ܣ�");  
                        return false;  
                    }   
                }  
                  
            };  
        }  
          
        public static void finish() {
        	try {
				Socket socket1 = new Socket("120.78.188.54", 6668);
				//2����ȡ���������������˷�����Ϣ 
                DataOutputStream dos = new DataOutputStream(socket1.getOutputStream());  
                dos.writeUTF(fileName);  
                dos.writeUTF(file.getCRC32(sendFilePath));
                dos.flush();  
                System.out.println(file.getCRC32(sendFilePath));
                dos.close();
				socket1.close();
            } catch (Exception e) {  
                System.out.println("���ӷ�����ʧ�ܣ�"); 
				e.printStackTrace();
			}
        	
        }
        	static int portA=6666;
        private static Runnable ppp(String localIp,String ip,int port) {
       	 return new Runnable(){ 
       		 public void run() {
	        		 try {
		            	Socket socket1 = new java.net.Socket();
		            	socket1.bind(new InetSocketAddress(localIp,portA++));
		            	socket1.connect(new InetSocketAddress(ip, port));
						//2����ȡ���������������˷�����Ϣ 
		                DataOutputStream dos = new DataOutputStream(socket1.getOutputStream()); 
		        		//3����ȡ������������ȡ�ͻ�����Ϣ
		        		DataInputStream dis = new DataInputStream(new BufferedInputStream(socket1.getInputStream()));
		        		while(true) { 
			                //dos.writeUTF("finish"); 
		        			//System.out.println("okkkk");
			                dos.writeUTF("ok");
			                dos.flush(); 
			        		String a=dis.readUTF();
			        		System.out.println(localIp+" "+a);
			                Thread.sleep(10000);
		        		} 
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	
	        	 }
      	 };
      	
      }
        public static void main(String[] args){  
        	
            new client().service();  
            //finish();
        }  
}