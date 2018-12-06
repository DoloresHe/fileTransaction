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
//            Vector<Integer> vector = getRandom(fileList.size());  
//            for(Integer integer : vector){  
        	long startTime = System.currentTimeMillis(); //����ʼ��¼ʱ��
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
                   * ���Ӳ������ļ�
                   */          
        private static Runnable sendFile(final String filePath,String localIp){  
            return new Runnable(){  
                  
                private Socket socket = null;  
                private String ip ="120.78.188.54";  
                private int port = 6666;  

                public void run() {  
                	long startTime = System.currentTimeMillis(); //����ʼ��¼ʱ��
                    System.out.println("��ʼ�����ļ�:" + filePath);  
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
                            long length = file.length();    //���Ҫ�����ļ��ĳ���  
                            while ((read = fis.read(buf)) != -1) {  
                                passedlen += read;  
                              //  System.out.println("�Ѿ�����ļ� [" + file.getName() + "]�ٷֱ�: " + passedlen * 100L/ length + "%");  
                                dos.write(buf, 0, read);  
                            }  
      
                           dos.flush();  
                           fis.close();  
                           dos.close();  
                           socket.close();  
                        long endTime   = System.currentTimeMillis(); //���������¼ʱ��
                        long TotalTime = endTime - startTime;       //������ʱ��
                           System.out.println("�ļ� " + filePath + "�������!");  
                        System.out.println(localIp+"��ʱ:  " + ((double)TotalTime)/1000+"s" ); 
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
				Socket socket1 = new Socket("120.78.188.54", 6666);
				//2����ȡ���������������˷�����Ϣ 
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