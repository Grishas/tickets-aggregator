package com.ctnu.gzip;

import java.io.ByteArrayInputStream;
import java.util.zip.GZIPInputStream;

public class GZIPUtils {

	
	public static String decompress(String compressedData) throws Exception {
		
		 final int BUFFER_SIZE = 32;
		 
		 ByteArrayInputStream is = new ByteArrayInputStream(compressedData.getBytes());
		 
		 GZIPInputStream gis = new GZIPInputStream(is, BUFFER_SIZE);
		 
		 StringBuilder string = new StringBuilder();
		   
		 byte[] data = new byte[BUFFER_SIZE];
		    
		 int bytesRead;
		    
		 while ((bytesRead = gis.read(data)) != -1) 
		 {
			 string.append(new String(data, 0, bytesRead));
		 }
		 
		 gis.close();
		 
		 is.close();
		 
		 return string.toString();
   }
	
	
	public static void main(String[] args) 
	{
		
	
	}

	
	
	
//	public static void decompressGzipFile(String gzipFile, String newFile) {
//		 byte[] buffer = new byte[1024];
//		 
//	     try{
//	 
//	    	 GZIPInputStream gzis = 
//	    		new GZIPInputStream(new FileInputStream(INPUT_GZIP_FILE));
//	 
//	    	 FileOutputStream out = 
//	            new FileOutputStream(OUTPUT_FILE);
//	 
//	        int len;
//	        while ((len = gzis.read(buffer)) > 0) {
//	        	out.write(buffer, 0, len);
//	        }
//	 
//	        gzis.close();
//	    	out.close();
//	 
//	    	System.out.println("Done");
//	 
//	    }catch(IOException ex){
//	       ex.printStackTrace();   
//	    }
//         
//    }
// 
//    public static void compressGzipFile(String file, String gzipFile) {
//    	 byte[] buffer = new byte[1024];
//    	 
//         try{
//     
//        	GZIPOutputStream gzos = 
//        		new GZIPOutputStream(new FileOutputStream(OUTPUT_GZIP_FILE));
//     
//            FileInputStream in = 
//                new FileInputStream(SOURCE_FILE);
//     
//            int len;
//            while ((len = in.read(buffer)) > 0) {
//            	gzos.write(buffer, 0, len);
//            }
//     
//            in.close();
//     
//        	gzos.finish();
//        	gzos.close();
//     
//        	System.out.println("Done");
//     
//        }catch(IOException ex){
//           ex.printStackTrace();   
//        }
//      
//    }
	
}
