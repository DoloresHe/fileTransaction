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
      
        private int defaultBindPort = 6666;    //默认监听端口号为10000  
        private int tryBindTimes = 0;           //初始的绑定端口的次数设定为0  
          
        private ServerSocket serverSocket;      //服务套接字等待对方的连接和文件发送  
          
        private ExecutorService executorService;    //线程池  
        private final int POOL_SIZE = 4;            //单个CPU的线程池大小   
          

    	private static int portP=6663;
    	
		 static int count=0;
		 static final Object object=new Object();
		 
        public static ArrayList<String> filenames=new ArrayList<String>();
        /** 
         * 不带参数的构造器，选用默认的端口号 
         * @throws Exception 
         */  
        public server() throws Exception{  
            try {  
                this.bingToServerPort(defaultBindPort);  
                executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * POOL_SIZE);  
                System.out.println("开辟线程数 ： " + Runtime.getRuntime().availableProcessors() * POOL_SIZE);  
            } catch (Exception e) {  
                throw new Exception("绑定端口不成功!");  
            }  
        }  
          
        /** 
         * 带参数的构造器，选用用户指定的端口号 
         * @param port 
         * @throws Exception 
         */  
        public server(int port) throws Exception{  
            try {  
                this.bingToServerPort(port);  
                executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * POOL_SIZE);  
            } catch (Exception e) {  
                throw new Exception("绑定端口不成功!");  
            }  
        }  
          
        private void bingToServerPort(int port) throws Exception{  
            try {  
                serverSocket = new ServerSocket(port);  
                System.out.println(port);  
                System.out.println("服务启动!");  
            } catch (Exception e) {  
                this.tryBindTimes = this.tryBindTimes + 1;  
                port = port + this.tryBindTimes;  
                if(this.tryBindTimes >= 20){  
                    throw new Exception("您已经尝试很多次了，但是仍无法绑定到指定的端口!请重新选择绑定的默认端口号");  
                }  
                //递归绑定端口  
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
                    String a=dis.readUTF();//这里做一个跳出？？？
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
                      //  System.out.println("文件[" + savePath + "]已经接收: " + passedlen * 100L/ length + "%");  
                    }  
                    System.out.println("文件: " + savePath + "接收完成!");  
            		String crc_b=file.getCRC32(savePath);
//            		System.out.println(crc_a + "   " + savePath);
//            		System.out.println(crc_b + "   " + savePath);
//            		if(crc_a.equals(crc_b)) {
//            			System.out.println("CRC码为: "+crc_b+", 校验成功！");
//            		}
                    dos.close(); 
                    } System.out.println(dis.read());
                            dis.close();  
                } catch (Exception e) {  
                    e.printStackTrace();  
                    System.out.println("接收文件失败!");  
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
    		//1、创建一个服务器端Socket，即ServerSocket，指定绑定的端口，并监听此端口
    		ServerSocket serverSocket;
			try {
				serverSocket = new ServerSocket(port);//1024-65535的某个端口
        		//2、调用accept()方法开始监听，等待客户端的连接
        		Socket socket= serverSocket.accept();
        		//3、获取输入流，并读取客户端信息
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
//                    System.out.println(k);                      //输出结果
//                }
        		file.uniteFile(filenames,fileName);
        		String crc_a=is.readUTF();
        		System.out.println(fileName);
        		String crc_b=file.getCRC32(fileName);
        		System.out.println(crc_a);
        		System.out.println(crc_b);
        		if(crc_a.equals(crc_b)) {
        			System.out.println("CRC码为: "+crc_b+", 校验成功！");
        		}
        		socket.shutdownInput();//关闭输入流

        		//5、关闭资源
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
	    		//1、创建一个服务器端Socket，即ServerSocket，指定绑定的端口，并监听此端口
	    		ServerSocket serverSocket;
				try {
					serverSocket = new ServerSocket(port);//1024-65535的某个端口
	        		//2、调用accept()方法开始监听，等待客户端的连接
	        		Socket socket= serverSocket.accept();
					//2、获取输出流，向服务器端发送信息 
	                DataOutputStream dos = new DataOutputStream(socket.getOutputStream()); 
	        		//3、获取输入流，并读取客户端信息
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

