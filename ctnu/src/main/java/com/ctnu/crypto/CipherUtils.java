package com.ctnu.crypto;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

import com.ctnu.UtilsException;

public class CipherUtils {

	private final String encryptionKey;

	public CipherUtils(String encryptionKey)
	{
		this.encryptionKey = encryptionKey;
	}

	public String encrypt(String plainText) throws UtilsException 
	{
		Cipher cipher;
		try {
			cipher = getCipher(Cipher.ENCRYPT_MODE);
		} catch (Exception e) {
			throw new UtilsException(e); 
		}
		
	    byte[] encryptedBytes;
		try {
			encryptedBytes = cipher.doFinal(plainText.getBytes());
		} catch (IllegalBlockSizeException | BadPaddingException e) {
			throw new UtilsException(e); 
		}

	    return Base64.encodeBase64String(encryptedBytes);
	}
	
	public String decrypt(String encrypted) throws UtilsException 
	{
		Cipher cipher;
		try {
			cipher = getCipher(Cipher.DECRYPT_MODE);
		} catch (Exception e) {
			throw new UtilsException(e); 
		}
		
	    byte[] plainBytes;
		try {
			plainBytes = cipher.doFinal(Base64.decodeBase64(encrypted));
		} catch (IllegalBlockSizeException | BadPaddingException e) {
			throw new UtilsException(e); 
		}

	    return new String(plainBytes);
	 }

	private Cipher getCipher(int cipherMode)throws Exception{
		
		String encryptionAlgorithm = "AES";
	        
		SecretKeySpec keySpecification = 
				new SecretKeySpec(encryptionKey.getBytes("UTF-8"), encryptionAlgorithm);
	    
		Cipher cipher = Cipher.getInstance(encryptionAlgorithm);
	    
		cipher.init(cipherMode, keySpecification);

		return cipher;
	}
	
	public static void main(String[] arguments) throws Exception
	{
		
		
		
		
//	    System.out.println(cipherUtils.decrypt("fW7bKcBbbTnRLWgSb69Y2Tc5wKd4YXyiM327Hymzb2DuEvmtvxET637Ygrnur2yS"));
		
		if(arguments.length==0||arguments[0]==null||arguments[1]==null){
	    	System.out.println("CipherUtils usage > key text");
	    	return;
	    }
		
	    String encryptionKey = arguments[0];
	    String plainText = arguments[1];
	    
	    CipherUtils cipherUtils = new CipherUtils(encryptionKey);
	    
	    String cipherText = cipherUtils.encrypt(plainText);
	    
	    String decryptedCipherText = cipherUtils.decrypt(cipherText);

	    System.out.println("Plain text : "+plainText);
	    System.out.println("Cipher text : "+cipherText);
	    System.out.println("Decrypted cipher text : "+decryptedCipherText);
	    System.out.println("Key : "+arguments[0]);
	    
	}
}











