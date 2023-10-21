package main;


import java.security.*;
import java.nio.charset.*;

public class HesiranjeSifre {
	
	
	
	
	 public static String hesiranjeSifre(String sifra , String salt , String algoritam){
	 
		
		 try {
			 
			 MessageDigest digest = MessageDigest.getInstance(algoritam);
	           byte[] sifraBytes = sifra.getBytes(StandardCharsets.UTF_8);
	           byte[] saltBytes = salt.getBytes(StandardCharsets.UTF_8);
	           
	           byte[] saltSifra= new byte[sifraBytes.length+saltBytes.length];
	           System.arraycopy(sifraBytes, 0, saltSifra, 0, sifraBytes.length);
	           System.arraycopy(saltBytes, 0, saltSifra, sifraBytes.length,saltBytes.length);
	           
	           byte[] hashBytes=digest.digest(saltSifra);
	           StringBuilder stringBuilder = new StringBuilder();
	           for(byte b: hashBytes)
	           {
	        	   stringBuilder.append(String.format("%02x", b));
	           
	           }
	           
	           return stringBuilder.toString();
			 
		 }
		 catch(NoSuchAlgorithmException ex) {
			 
			 ex.printStackTrace();
			 
			 return null;
			 
			 
		 }
		
	 
	 
	 }

}
