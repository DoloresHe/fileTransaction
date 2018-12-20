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
      
		
		static int count=0;//�����ļ�����
		
        private static String sendFilePath = "D:/test.sql"; //Ŀ�괫���ļ�ȫ·��  
        private static String fileName = "test.sql";//Ŀ�괫���ļ���
        
        static int port_a=6667;//�ļ�����˿ں�
        static int port_f=6668;//finish����˿ں�
		static int portP=6663;//�������˿ں�
		
        static int portA=6665;//������Ȿ�ذ󶨶˿ں�
		 
		int file_size=204800;//�ļ��зֿ��С
		
		static String server_ip="120.78.188.54";
		
       // private static ArrayList<String> fileList = new ArrayList<String>();  
		private static String[] names;  //�зֺ������ļ����ļ���
		
		private static HashMap<Integer,String> ips=new HashMap<>(); //����ip�ļ���
		
		static final Object object=new Object();//sychronizedͬ������object

        /** 
         * ���������Ĺ�������ʹ��Ĭ�ϵĴ����ļ����ļ��� 
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
                executorService.execute(sendFile(w.getValue())); 
            }  
    		executorService.shutdown();//���ٽ����µ��߳�
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
//            	HashMap<Integer, Float> size=Network.net();
            	names = file.divideFile(file1.getAbsolutePath(),file_size);
//            	ips=Network.IP(size);
            	//�ֶ�����vpn��ip
            	ips.put(1,"192.168.30.10");
            	ips.put(2,"192.168.30.11");
            	ips.put(3,"192.168.30.12");
            	ips.put(4,"192.168.30.13");
            	for(Map.Entry<Integer, String> w:ips.entrySet()) {
                    System.out.println(w.getValue());
                    new Thread(ppp(w.getValue(),portP)).start();
                }
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
                private int port = 6666; //���ذ󶨶˿ں� 
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
		                     //   dos.writeUTF(file.getCRC32(filePath));
		                     //   dos.flush();  
		                        dos.writeLong(file1.length());  
		                        dos.flush();  
		                              
		                        int read = 0;  
		                        int passedlen = 0;  
		                        long length = file1.length();    //���Ҫ�����ļ��ĳ���  
		                        while ((read = fis.read(buf)) != -1) {  
		                        	passedlen += read;  
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
	            }  
            }
        
               
           /*
            * socket���ú�����
            */
                private boolean createConnection() {  
                    try {  
                    	socket = new java.net.Socket();
                    	socket.bind(new InetSocketAddress(localIp, port_a));
                    	socket.connect(new InetSocketAddress(server_ip, port));
                        System.out.println("���ӷ������ɹ���");  
                        return true;  
                    } catch (Exception e) {  
                        System.out.println("���ӷ�����ʧ�ܣ�");  
                        return false;  
                    }   
                }  
                  
            };  
        }  
         
        /*
         * ����finish�ź�
         */
        public static void finish() {
        	try {
				Socket socket1 = new Socket(server_ip, port_f);
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
        
        private static Runnable ppp(String localIp,int port) {
       	 return new Runnable(){ 
       		 public void run() {
	        		 try {
		            	Socket socket1 = new java.net.Socket();
		            	socket1.bind(new InetSocketAddress(localIp,portA++));
		            	socket1.connect(new InetSocketAddress(server_ip, port));
						//2����ȡ���������������˷�����Ϣ 
		                DataOutputStream dos = new DataOutputStream(socket1.getOutputStream()); 
		        		//3����ȡ������������ȡ�ͻ�����Ϣ
		        		DataInputStream dis = new DataInputStream(new BufferedInputStream(socket1.getInputStream()));
		        		while(true) { 
			                //dos.writeUTF("finish"); 
			                dos.writeUTF("ok");
			                dos.flush(); 
//			        		String a=dis.readUTF();
			        		//System.out.println(localIp+" "+a);
			                Thread.sleep(50000);
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
        }  
}