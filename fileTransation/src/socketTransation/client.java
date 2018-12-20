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
      
		
		static int count=0;//发送文件计数
		
        private static String sendFilePath = "D:/test.sql"; //目标传输文件全路径  
        private static String fileName = "test.sql";//目标传输文件名
        
        static int port_a=6667;//文件传输端口号
        static int port_f=6668;//finish传输端口号
		static int portP=6663;//心跳检测端口号
		
        static int portA=6665;//心跳检测本地绑定端口号
		 
		int file_size=204800;//文件切分块大小
		
		static String server_ip="120.78.188.54";
		
       // private static ArrayList<String> fileList = new ArrayList<String>();  
		private static String[] names;  //切分后所有文件名的集合
		
		private static HashMap<Integer,String> ips=new HashMap<>(); //所有ip的集合
		
		static final Object object=new Object();//sychronized同步锁定object

        /** 
         * 不带参数的构造器。使用默认的传送文件的文件夹 
         */  
        public client(){  
            getFilePath(sendFilePath);  
        }  
          
        /*
         * 开启多线程传输
         */
        public void service(){  
            ExecutorService executorService = Executors.newCachedThreadPool();  
           // ExecutorService executorService1 = Executors.newCachedThreadPool();  
//            Vector<Integer> vector = getRandom(fileList.size());  
//            for(Integer integer : vector){  
            
        	long startTime = System.currentTimeMillis(); //程序开始记录时间
        	
    		for(Map.Entry<Integer, String> w:ips.entrySet()) { 
                executorService.execute(sendFile(w.getValue())); 
            }  
    		executorService.shutdown();//不再接受新的线程
            while(true){  
               if(executorService.isTerminated()){  
            	   
                   long endTime   = System.currentTimeMillis(); //程序结束记录时间
                   long TotalTime = endTime - startTime;       //总消耗时间
                   System.out.println("用时:  " + ((double)TotalTime)/1000+"s" );
                   
                   finish();
                   System.out.println("所有的子线程都结束了！");  
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
       * 获取所有要传输的文件名
       */
        private void getFilePath(String fileName){  
        	File file1 =  new File(fileName);
            try {
//            	HashMap<Integer, Float> size=Network.net();
            	names = file.divideFile(file1.getAbsolutePath(),file_size);
//            	ips=Network.IP(size);
            	//手动输入vpn绑定ip
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
                   * 连接并发送文件
                   */          
        private static Runnable sendFile(String localIp){  
            return new Runnable(){                    
                private Socket socket = null;    
                private int port = 6666; //本地绑定端口号 
                private String filePath;
                public void run() {   
		            if(createConnection()){
		            	try {  
		                	DataOutputStream dos = new DataOutputStream(socket.getOutputStream());  
			                while(count<names.length) {
			                    synchronized (object){
			                    	filePath=names[count++];
			                    }
			                    //long startTime = System.currentTimeMillis(); //程序开始记录时间
			                    System.out.println("开始发送文件:" + filePath);  
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
		                        long length = file1.length();    //获得要发送文件的长度  
		                        while ((read = fis.read(buf)) != -1) {  
		                        	passedlen += read;  
		                            dos.write(buf, 0, read);  
		                        }  
		                        System.out.println("文件 " + filePath + "传输完成!"); 
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
            * socket设置和连接
            */
                private boolean createConnection() {  
                    try {  
                    	socket = new java.net.Socket();
                    	socket.bind(new InetSocketAddress(localIp, port_a));
                    	socket.connect(new InetSocketAddress(server_ip, port));
                        System.out.println("连接服务器成功！");  
                        return true;  
                    } catch (Exception e) {  
                        System.out.println("连接服务器失败！");  
                        return false;  
                    }   
                }  
                  
            };  
        }  
         
        /*
         * 发送finish信号
         */
        public static void finish() {
        	try {
				Socket socket1 = new Socket(server_ip, port_f);
				//2、获取输出流，向服务器端发送信息 
                DataOutputStream dos = new DataOutputStream(socket1.getOutputStream());  
                dos.writeUTF(fileName);  
                dos.writeUTF(file.getCRC32(sendFilePath));
                dos.flush();  
                System.out.println(file.getCRC32(sendFilePath));
                dos.close();
				socket1.close();
            } catch (Exception e) {  
                System.out.println("连接服务器失败！"); 
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
						//2、获取输出流，向服务器端发送信息 
		                DataOutputStream dos = new DataOutputStream(socket1.getOutputStream()); 
		        		//3、获取输入流，并读取客户端信息
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