package com.ctnu.io;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import com.ctnu.UtilsException;

public class UtilsIO {

	public static String readAllBytes(String path,String encoding) throws UtilsException
	{
		byte[] encoded;
		try {
			encoded = Files.readAllBytes(Paths.get(path));
		} catch (IOException e) {
			throw new UtilsException(e);
		}
	  
		return new String(encoded);
	}
	
	public static void writeToFile(String path,String data) throws UtilsException
	{
		try {
			Files.write(Paths.get(path),data.getBytes());
		} catch (IOException e) {
			throw new UtilsException(e);
		}
	}
	
	
	
	
	
	public static Path getPath(String directory){
		return FileSystems.getDefault().getPath(directory);
	}
	
	public static String getSeparator(){
		return FileSystems.getDefault().getSeparator();
	}
}
