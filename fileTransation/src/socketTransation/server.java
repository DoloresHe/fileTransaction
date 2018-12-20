package socketTransation;

import java.io.BufferedInputStream;  
import java.io.BufferedOutputStream;  
import java.io.DataInputStream;  
import java.io.DataOutputStream;  
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ServerSocket;  
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.ExecutorService;  
import java.util.concurrent.Executors;

import fileOperation.file;    

public class server {  
      
        private int defaultBindPort = 6666;    //Ĭ�ϼ����˿ں�Ϊ6666
        private int tryBindTimes = 0;           //��ʼ�İ󶨶˿ڵĴ����趨Ϊ0  
          
    	private static int portP=6663;	//�������˿ں�
    	private static int port_f=6668;	//finish�˿ں�
    	
        private ServerSocket serverSocket;      //�����׽��ֵȴ��ļ�����  
        private ServerSocket serverSocket1;      //�����׽��ֵȴ��������� 
        private ServerSocket serverSocket2;      //�����׽��ֵȴ�finish  
          
        private ExecutorService executorService;    //�̳߳�  
        private final int POOL_SIZE = 4;            //����CPU���̳߳ش�С   
          
    	
		static int count=0;//�����ļ�����
		static final Object object=new Object();//
		 
		int file_count=21; //�ļ�������
		int net_count=4;//������������
		
        public static ArrayList<String> filenames=new ArrayList<String>();//�����ļ����ļ���
        
        /** 
         * ���������Ĺ�������ѡ��Ĭ�ϵĶ˿ں� 
         * @throws Exception 
         */  
        public server() throws Exception{  
            try {  
                this.bingToServerPort(defaultBindPort);  
                executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * POOL_SIZE);  
                new Thread(new runs()).start(); 
                System.out.println("�����߳��� �� " + Runtime.getRuntime().availableProcessors() * POOL_SIZE);  
            } catch (Exception e) {  
                throw new Exception("�󶨶˿ڲ��ɹ�!");  
            }  
        }  
          
        /** 
         * �������Ĺ�������ѡ���û�ָ���Ķ˿ں� 
         * @param port 
         * @throws Exception 
         */  
        public server(int port) throws Exception{  
            try {  
                this.bingToServerPort(port);  
                executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * POOL_SIZE);  
                new Thread(new runs()).start();
            } catch (Exception e) {  
                throw new Exception("�󶨶˿ڲ��ɹ�!");  
            }  
        }  
        class runs implements Runnable {
        	@Override
        	public void run() {
                while(true){
                    Socket socket=null;
                    try{
                        socket=serverSocket1.accept();                        //���̻߳�ȡ�ͻ�������
                        Thread workThread=new Thread(new PPP(socket));    //�����߳�
                        workThread.start();                                    //�����߳�
                    }catch(Exception e){
                        e.printStackTrace();
                    }
        	}
        	
        }
        }
        
        private void bingToServerPort(int port) throws Exception{  
            try {  
                serverSocket = new ServerSocket(port);  
                serverSocket1 = new ServerSocket(portP); 
                serverSocket2 = new ServerSocket(6668);   
                System.out.println(port);  
                System.out.println("��������!");  
            } catch (Exception e) {  
                this.tryBindTimes = this.tryBindTimes + 1;  
                port = port + this.tryBindTimes;  
                if(this.tryBindTimes >= 20){  
                    throw new Exception("���Ѿ����Ժܶ���ˣ��������޷��󶨵�ָ���Ķ˿�!������ѡ��󶨵�Ĭ�϶˿ں�");  
                }  
                //�ݹ�󶨶˿�  
                this.bingToServerPort(port);  
            }  
        }  
          
        public void service(){  
            Socket socket = null; 
	            int i=0;
	            while (i<net_count) {  
	                try {  
	                	i++;
	                    socket = serverSocket.accept();  
	                    //socket1 = serverSocket1.accept();  
	                    executorService.execute(new Handler(socket)); 
	                  //  new Thread(new PPP(socket1)).start();
	                } catch (Exception e) {  
	                    e.printStackTrace();  
	                }  
	            }
	            executorService.shutdown();
	            while(true){  
	               if(executorService.isTerminated()){  
	               	try {
	               		System.out.println("prepare finish");
	               		finish(port_f,"C:/",serverSocket2);
						//file.uniteFile(filenames, "C://test.sql");
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
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
          
      
        class Handler implements Runnable{  
            private Socket socket;  
              
            public Handler(Socket socket){  
                this.socket = socket;  
            }  
            
            public void run() {//count_max
                System.out.println("New connection accepted " + socket.getInetAddress() + ":" + socket.getPort()); 
      
                int bufferSize = 8192;  
                byte[] buf = new byte[bufferSize];  
                  
                try {
                	DataInputStream dis = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
				    while(true) {
	                    synchronized (object){
	                    	if(count<file_count)
	                    		count++;
	                    	else break;
	                    }
                    String a=dis.readUTF();//������һ������������
                  //  System.out.println(a);
                    if(a.equals("finish")){
                    	file.uniteFile(filenames, "C://test.sql");
                    	break;
                    }
                    String savePath =  "C:/"+ a;  
                    filenames.add(savePath);
                    String crc_a=dis.readUTF();
                    long length = dis.readLong();  
                    DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(savePath)));  
                    //System.out.println(length);
                    int read = 0;  
                    long passedlen = 0;  
                    while ((read = dis.read(buf)) != -1) {  
                        passedlen += read;  
                        dos.write(buf, 0, read); 
                        if(passedlen==length)
                        	break;
                    }  
                    System.out.println("�ļ�: " + savePath + "�������!");  
            		String crc_b=file.getCRC32(savePath);
//            		System.out.println(crc_a + "   " + savePath);
//            		System.out.println(crc_b + "   " + savePath);
//            		if(crc_a.equals(crc_b)) {
//            			System.out.println("CRC��Ϊ: "+crc_b+", У��ɹ���");
//            		}
                    dos.close(); 
                    } System.out.println(dis.read());
                            dis.close();  
                } catch (Exception e) {  
                    e.printStackTrace();  
                    System.out.println("�����ļ�ʧ��!");  
                }finally{  
                    try {  
//                        if(dos != null){  
//                            dos.close();  
//                        }  
//                        if(dis != null){  
//                        }  
                        if(socket != null){  
                        }  
                    } catch (Exception e) {  
                        e.printStackTrace();  
                    }  
                }  
            }  
        }       

		public static void finish(int port,String fileDir,ServerSocket serverSocket2) {//6668,"c:/"
    		//1������һ����������Socket����ServerSocket��ָ���󶨵Ķ˿ڣ��������˶˿�
    		//ServerSocket socket1;
			try {
			//	socket1 = new ServerSocket(port);//1024-65535��ĳ���˿�
        		//2������accept()������ʼ�������ȴ��ͻ��˵�����
        		Socket socket=  serverSocket2.accept();
        		//3����ȡ������������ȡ�ͻ�����Ϣ
        		DataInputStream is = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
        		String fileName=fileDir+is.readUTF();
        		Collections.sort( filenames,new Comparator<String>() {
        			@Override
        			public int compare(String o1, String o2) {
        				String[] x= o1.split("_");
        				String x1=x[x.length-1];
        				String[] y= o2.split("_");
        				String y1=y[y.length-1];
        				return Integer.valueOf(x1)-Integer.valueOf(y1);
        					
        			}
        		});
//        		for(String k:filenames){  
//                    System.out.println(k);                      //������
//                }
        		file.uniteFile(filenames,fileName);
        		String crc_a=is.readUTF();
        		System.out.println(fileName);
        		String crc_b=file.getCRC32(fileName);
        		System.out.println(crc_a);
        		System.out.println(crc_b);
        		if(crc_a.equals(crc_b)) {
        			System.out.println("CRC��Ϊ: "+crc_b+", У��ɹ���");
        		}
        		socket.shutdownInput();//�ر�������

        		//5���ر���Դ
        		is.close();
        		socket.close();
        		//socket1.close();
		
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	
    }
	
	        
        class PPP implements Runnable{
            private Socket socket;  
              
            public PPP(Socket socket){  
                this.socket = socket;  
            }  
			@Override
	        public void run() {
	    		//1������һ����������Socket����ServerSocket��ָ���󶨵Ķ˿ڣ��������˶˿�
	    		//ServerSocket serverSocket;
				try {
				//	serverSocket = new ServerSocket(port);//1024-65535��ĳ���˿�
	        		//2������accept()������ʼ�������ȴ��ͻ��˵�����
	        		//Socket socket= serverSocket1.accept();
					//2����ȡ���������������˷�����Ϣ 
	                //DataOutputStream dos = new DataOutputStream(socket.getOutputStream()); 
	        		//3����ȡ������������ȡ�ͻ�����Ϣ
	        		DataInputStream dis = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
	        		while(true) { 
		                //dos.writeUTF("finish");  
		        		String a=dis.readUTF();
		        		System.out.println(socket.getInetAddress()+" "+a);
//		                dos.writeUTF("ok");
//		                dos.flush(); 
		                Thread.sleep(50000);
	        		} 
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	        	
	        }
        }
        public static void main(String[] args) throws Exception{  
            new server().service();  
            
        }  
    }  

