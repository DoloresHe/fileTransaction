package socketTransation;

import java.io.BufferedInputStream;  
import java.io.BufferedOutputStream;  
import java.io.DataInputStream;  
import java.io.DataOutputStream;  
import java.io.FileOutputStream;  
import java.net.ServerSocket;  
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;  
import java.util.concurrent.Executors;  
      

import fileOperation.file;    

public class server {  
      
        private int defaultBindPort = 10000;    //Ĭ�ϼ����˿ں�Ϊ10000  
        private int tryBindTimes = 0;           //��ʼ�İ󶨶˿ڵĴ����趨Ϊ0  
          
        private ServerSocket serverSocket;      //�����׽��ֵȴ��Է������Ӻ��ļ�����  
          
        private ExecutorService executorService;    //�̳߳�  
        private final int POOL_SIZE = 4;            //����CPU���̳߳ش�С   
          
        
        public ArrayList<String> filenames=new ArrayList<String>();
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
            while (true) {  
                try {  
                    socket = serverSocket.accept();  
                    executorService.execute(new Handler(socket));  
                } catch (Exception e) {  
                    e.printStackTrace();  
                }  
            }  
        }  
          
      
        class Handler implements Runnable{  
            private Socket socket;  
              
            public Handler(Socket socket){  
                this.socket = socket;  
            }  
      
            public void run() {  
                  
                System.out.println("New connection accepted " + socket.getInetAddress() + ":" + socket.getPort());  
                  
                DataInputStream dis = null;  
                DataOutputStream dos = null;  
      
                int bufferSize = 8192;  
                byte[] buf = new byte[bufferSize];  
                  
                try {  
                    dis = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
                    String a=dis.readUTF();
                    System.out.println(a);
                    if(a.equals("finish")){
                    	String[] ww=(String[]) filenames.toArray();
                    	//file.uniteFile(ww, "D://test.sql");
                    }
                    	
                    String savePath =  "D:/"+ a;  
                    filenames.add(savePath);
                    long length = dis.readLong();  
                    dos = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(savePath)));  
                      
                    int read = 0;  
                    long passedlen = 0;  
                    while ((read = dis.read(buf)) != -1) {  
                        passedlen += read;  
                        dos.write(buf, 0, read);  
                        System.out.println("�ļ�[" + savePath + "]�Ѿ�����: " + passedlen * 100L/ length + "%");  
                    }  
                    System.out.println("�ļ�: " + savePath + "�������!");  
                      
                } catch (Exception e) {  
                    e.printStackTrace();  
                    System.out.println("�����ļ�ʧ��!");  
                }finally{  
                    try {  
                        if(dos != null){  
                            dos.close();  
                        }  
                        if(dis != null){  
                            dis.close();  
                        }  
                        if(socket != null){  
                            socket.close();  
                        }  
                    } catch (Exception e) {  
                        e.printStackTrace();  
                    }  
                }  
            }  
        }  
          
        public static void main(String[] args) throws Exception{  
            new server().service();  
            
        }  
    }  

