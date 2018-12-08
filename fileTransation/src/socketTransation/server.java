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
      
        private int defaultBindPort = 6666;    //Ĭ�ϼ����˿ں�Ϊ10000  
        private int tryBindTimes = 0;           //��ʼ�İ󶨶˿ڵĴ����趨Ϊ0  
          
        private ServerSocket serverSocket;      //�����׽��ֵȴ��Է������Ӻ��ļ�����  
          
        private ExecutorService executorService;    //�̳߳�  
        private final int POOL_SIZE = 4;            //����CPU���̳߳ش�С   
          

    	private static int portP=6663;
    	
		 static int count=0;
		 static final Object object=new Object();
		 
        public static ArrayList<String> filenames=new ArrayList<String>();
        /** 
         * ���������Ĺ�������ѡ��Ĭ�ϵĶ˿ں� 
         * @throws Exception 
         */  
        public server() throws Exception{  
            try {  
                this.bingToServerPort(defaultBindPort);  
                executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * POOL_SIZE);  
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
            } catch (Exception e) {  
                throw new Exception("�󶨶˿ڲ��ɹ�!");  
            }  
        }  
          
        private void bingToServerPort(int port) throws Exception{  
            try {  
                serverSocket = new ServerSocket(port);  
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
            while(true) {
	            int i=0;
	            while (i<4) {  
	                try {  
	                	i++;
	                    socket = serverSocket.accept();  
	                    executorService.execute(new Handler(socket)); 
	                    new Thread(new PPP(portP++)).start();
	                } catch (Exception e) {  
	                    e.printStackTrace();  
	                }  
	            }
	            executorService.shutdown();
	            while(true){  
	               if(executorService.isTerminated()){  
	               	try {
	               		finish(6668,"C:/");
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
	                    	if(count<21)
	                    		count++;
	                    	else break;
	                    }
                    String a=dis.readUTF();//������һ������������
                  //  System.out.println(a);
                    if(a.equals("finish")){
                    	//String[] ww=(String[]) filenames.toArray();
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
                      //  System.out.println("�ļ�[" + savePath + "]�Ѿ�����: " + passedlen * 100L/ length + "%");  
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

		public static void finish(int port,String fileDir) {//6668,"c:/"
    		//1������һ����������Socket����ServerSocket��ָ���󶨵Ķ˿ڣ��������˶˿�
    		ServerSocket serverSocket;
			try {
				serverSocket = new ServerSocket(port);//1024-65535��ĳ���˿�
        		//2������accept()������ʼ�������ȴ��ͻ��˵�����
        		Socket socket= serverSocket.accept();
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
        		serverSocket.close();
		
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	
    }
	
	        
        class PPP implements Runnable{
        	int port;
        	public PPP(int port) {
        		this.port=port;
        	}
			@Override
	        public void run() {
	    		//1������һ����������Socket����ServerSocket��ָ���󶨵Ķ˿ڣ��������˶˿�
	    		ServerSocket serverSocket;
				try {
					serverSocket = new ServerSocket(port);//1024-65535��ĳ���˿�
	        		//2������accept()������ʼ�������ȴ��ͻ��˵�����
	        		Socket socket= serverSocket.accept();
					//2����ȡ���������������˷�����Ϣ 
	                DataOutputStream dos = new DataOutputStream(socket.getOutputStream()); 
	        		//3����ȡ������������ȡ�ͻ�����Ϣ
	        		DataInputStream dis = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
	        		while(true) { 
		                //dos.writeUTF("finish");  
		        		String a=dis.readUTF();
		        		System.out.println(socket.getInetAddress()+" "+a);
		                dos.writeUTF("ok");
		                dos.flush(); 
		                Thread.sleep(10000);
	        		} 
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	        	
	        }
        }
        public static void main(String[] args) throws Exception{  
            new server(6667).service();  
            
        }  
    }  

