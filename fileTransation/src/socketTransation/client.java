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
		private static HashMap<Integer,String> names=new HashMap<>() ;  
		private static HashMap<Integer,String> ips=new HashMap<>() ; 
        private String sendFilePath = "D:/test.sql";   

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
//            Vector<Integer> vector = getRandom(fileList.size());  
//            for(Integer integer : vector){  
        	long startTime = System.currentTimeMillis(); //程序开始记录时间
    		for(Map.Entry<Integer, String> w:names.entrySet()) {
//                String filePath = fileList.get(integer.intValue());  
                executorService.execute(sendFile(w.getValue(),ips.get(w.getKey())));  
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
            	HashMap<Integer, Float> size=Network.net();
            	names = file.divideFile(file1.getAbsolutePath(),size);
            	ips=Network.IP(size);
            	for(Map.Entry<Integer, String> w:names.entrySet()) {
                    System.out.println(w.getValue());
                }
            	//String[] name= {"C:\\test\\test.sql1","C:\\test\\test.sql2"};
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }  
             
                  /*
                   * 连接并发送文件
                   */          
        private static Runnable sendFile(final String filePath,String localIp){  
            return new Runnable(){  
                  
                private Socket socket = null;  
                private String ip ="120.78.188.54";  
                private int port = 6666;  

                public void run() {  
                	long startTime = System.currentTimeMillis(); //程序开始记录时间
                    System.out.println("开始发送文件:" + filePath);  
                    File file = new File(filePath);  
                    if(createConnection()){  
                        int bufferSize = 8192;  
                        byte[] buf = new byte[bufferSize];  
                        try {  
                            DataInputStream fis = new DataInputStream(new BufferedInputStream(new FileInputStream(filePath)));  
                            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());  
                              
                            dos.writeUTF(file.getName());  
                            dos.flush();  
                            dos.writeLong(file.length());  
                            dos.flush();  
                              
                            int read = 0;  
                            int passedlen = 0;  
                            long length = file.length();    //获得要发送文件的长度  
                            while ((read = fis.read(buf)) != -1) {  
                                passedlen += read;  
                              //  System.out.println("已经完成文件 [" + file.getName() + "]百分比: " + passedlen * 100L/ length + "%");  
                                dos.write(buf, 0, read);  
                            }  
      
                           dos.flush();  
                           fis.close();  
                           dos.close();  
                           socket.close();  
                        long endTime   = System.currentTimeMillis(); //程序结束记录时间
                        long TotalTime = endTime - startTime;       //总消耗时间
                           System.out.println("文件 " + filePath + "传输完成!");  
                        System.out.println(localIp+"用时:  " + ((double)TotalTime)/1000+"s" ); 
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
                        //socket = new Socket(ip, port);
//                    	socket = new java.net.Socket();
//                    	socket.connect(new InetSocketAddress(ip, port));
                    	
//                    	NetworkInterface nif = NetworkInterface.getByName("eth26");
//                    	System.out.println((nif == null) ? "网络接口不存在!" : nif);
//                    	Enumeration<InetAddress> nifAddresses = nif.getInetAddresses();
                    	socket = new java.net.Socket();
                    	socket.bind(new InetSocketAddress(localIp, 6665));
                    	socket.connect(new InetSocketAddress(ip, port));
                        System.out.println("连接服务器成功！");  
                        return true;  
                    } catch (Exception e) {  
                        System.out.println("连接服务器失败！");  
                        return false;  
                    }   
                }  
                  
            };  
        }  
          
        public static void finish() {
        	try {
				Socket socket1 = new Socket("120.78.188.54", 6666);
				//2、获取输出流，向服务器端发送信息 
                DataOutputStream dos = new DataOutputStream(socket1.getOutputStream());  
                dos.writeUTF("finish");  
                dos.flush();  
                System.out.println("finish");
				socket1.shutdownOutput();
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        	
        }
        public static void main(String[] args){  
        	
            new client().service();  
            //finish();
        }  
    }