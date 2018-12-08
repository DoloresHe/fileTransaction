package fileOperation;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.CRC32;
import java.util.zip.CheckedInputStream;

import netInformation.Network;

public class file {
    
    public static void main(String[] args) throws Exception {
    	
//        File file =  new File("D:/test.sql");
//        Network network=new Network();
//        //HashMap<Integer, Float> size=Network.net();
//        try {
//        	String[] names = divideFile(file.getAbsolutePath(),204800);
////        	for(Map.Entry<Integer, String> w:names.entrySet()) {
////                System.out.println(w.getValue());
////            }
//        	ArrayList<String> fileNames=new ArrayList<String>(Arrays.asList(names));
//            uniteFile(fileNames,"C:/test/test.sql");
//        } catch (Exception e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
    	System.out.println(getCRC32("C:/test/test.sql"));
    	System.out.println(getCRC32("D:/test.sql"));
    }

    /**
     * �ָ��ļ�
     * @param fileName ���ָ���ļ���
     * @param size С�ļ��Ĵ�С�����ֽ�Ϊ��λ
     * @param suffix ���ɵ��ļ�����׺
     * @return  �ָ���С�ļ����ļ���
     * @throws Exception �ָ�����п����׳����쳣
     */
    public static String[] divideFile(String fileName, long size) throws Exception {
        File inFile = new File(fileName);
        if (!inFile.exists() || inFile.isDirectory()) {
            throw new Exception("not found file.");
        }
        File parentFile = inFile.getParentFile();
        long fileLength = inFile.length();
        if (size <= 0) {
            size = fileLength / 2;
        }
        int num = (int) ((fileLength + size - 1) / size);
        String[] outFileNames = new String[num];
        FileInputStream in = new FileInputStream(inFile);
        long inEndIndex = 0;
        int inBeginIndex = 0;
        for (int outFileIndex = 0; outFileIndex < num; outFileIndex++) {
            File outFile = new File("C:/test/", inFile.getName() +"_"+ outFileIndex);
            FileOutputStream out = new FileOutputStream(outFile);
            inEndIndex += size;
            inEndIndex = (inEndIndex > fileLength) ? fileLength : inEndIndex;
            for (; inBeginIndex < inEndIndex; inBeginIndex++) {
                out.write(in.read());
            }
            out.close();
            outFileNames[outFileIndex] = outFile.getAbsolutePath();
        }
        in.close();
        return outFileNames;
    }

    /**
     * �ϲ��ļ�
     * @param fileNames ���ϲ����ļ�������һ������
     * @param targetFileName Ŀ���ļ���
     * @return  Ŀ���ļ���ȫ·��
     * @throws Exception �ϲ������п����׳����쳣
     */
    public static String uniteFile(ArrayList<String> fileNames, String targetFileName) throws Exception {
        File inFile = null;
        File outFile = new File(targetFileName);
        FileOutputStream out = new FileOutputStream(outFile);
        for (String i:fileNames) {
            inFile = new File(i);
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
    
    

	public static String getCRC32(String fileUri) {
		CRC32 crc32 = new CRC32();
		FileInputStream fileinputstream = null;
		CheckedInputStream checkedinputstream = null;
		String crc = null;
		try {
			fileinputstream = new FileInputStream(new File(fileUri));
			checkedinputstream = new CheckedInputStream(fileinputstream, crc32);
			while (checkedinputstream.read() != -1) {
			}
			crc = Long.toHexString(crc32.getValue()).toUpperCase();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (fileinputstream != null) {
				try {
					fileinputstream.close();
				} catch (IOException e2) {
					e2.printStackTrace();
				}
			}
			if (checkedinputstream != null) {
				try {
					checkedinputstream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return crc;
	}
}