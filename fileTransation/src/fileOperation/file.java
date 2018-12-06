package fileOperation;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;

import netInformation.Network;

public class file {
    
//    public static void main(String[] args) throws Exception {
//    	
//        File file =  new File("D:/test.sql");
//        Network network=new Network();
//        HashMap<Integer, Float> size=Network.net();
//        try {
//        	HashMap<Integer,String> names = divideFile(file.getAbsolutePath(),size);
//        	for(Map.Entry<Integer, String> w:names.entrySet()) {
//                System.out.println(w.getValue());
//            }
//        	String[] name= {"C:\\test\\test.sql1","C:\\test\\test.sql2"};
//            uniteFile(name,"C:/test/test.sql");
//        } catch (Exception e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//    }

    /**
     * �ָ��ļ�
     * @param fileName ���ָ���ļ���
     * @param size С�ļ��Ĵ�С�����ֽ�Ϊ��λ
     * @param suffix ���ɵ��ļ�����׺
     * @return  �ָ���С�ļ����ļ���
     * @throws Exception �ָ�����п����׳����쳣
     */
    public static HashMap<Integer,String> divideFile(String fileName, HashMap<Integer, Float> size) throws Exception {
        File inFile = new File(fileName);
        if (!inFile.exists() || inFile.isDirectory()) {
            throw new Exception("not found file.");
        }
       // File parentFile = inFile.getParentFile();//��ȡ��Ŀ¼
        long fileLength = inFile.length();
//        if (size.size() <= 0) {
//            size = fileLength / 2;
//        }
        int num = (int) size.size();
//        String[] outFileNames = new String[num];
        HashMap<Integer,String> fileNames=new HashMap<>();
        FileInputStream in = new FileInputStream(inFile);
        long inEndIndex = 0;
        int inBeginIndex = 0;
        int outFileIndex = 1; 
        for(Map.Entry<Integer, Float> w:size.entrySet()) {
            File outFile = new File("C:/test/", inFile.getName() + outFileIndex++);
            FileOutputStream out = new FileOutputStream(outFile);
            inEndIndex += w.getValue()*fileLength;
            inEndIndex = (inEndIndex > fileLength) ? fileLength : inEndIndex;
            for (; inBeginIndex < inEndIndex; inBeginIndex++) {
                out.write(in.read());
            }
            out.close();
            //outFileNames[outFileIndex] = outFile.getAbsolutePath();
            fileNames.put(w.getKey(), outFile.getAbsolutePath());
        }
        in.close();
        return fileNames;
    }

    /**
     * �ϲ��ļ�
     * @param fileNames ���ϲ����ļ�������һ������
     * @param targetFileName Ŀ���ļ���
     * @return  Ŀ���ļ���ȫ·��
     * @throws Exception �ϲ������п����׳����쳣
     */
    public static String uniteFile(String[] fileNames, String targetFileName) throws Exception {
        File inFile = null;
        File outFile = new File(targetFileName);
        FileOutputStream out = new FileOutputStream(outFile);
        for (int i = 0; i < fileNames.length; i++) {
            inFile = new File(fileNames[i]);
            FileInputStream in = new FileInputStream(inFile);
            int c;
            while ((c = in.read()) != -1) {
                out.write(c);
            }
            in.close();
        }
        out.close();
        return outFile.getAbsolutePath();
    }
}