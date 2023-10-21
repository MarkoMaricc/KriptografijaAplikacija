package main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;







public class ProcesorKomandi {
	
	// private static final String algoritamKf = "idea-ecb";
	 private static final String algoritamKf = "aes-192-cbc ";
	 private static final String algoritam = "-sha256 ";
	 private static final String key = "sigurnost";
	
	public static String decryptKorisniciFajl() {

        String command = "openssl "+algoritamKf+" -d -in " + "KorisniciKripto.txt -out "
                + "korisnici.txt -k " + key;
        return executeSystemCommand(command);
        
    }

    public static String encryptKorisniciFajl() {

        String command = "openssl "+algoritamKf+" -in " + "korisnici.txt -out "
                + "KorisniciKripto.txt -k " + key;
        return   executeSystemCommand(command);
        
    }
	
    public static String encryptFajl(String putanja) {
    	 Path put=Paths.get(putanja);
    	
        String command = "openssl "+algoritamKf+" -in " +putanja+  " -out "
                + "Korisnici/"+"e_"+ put.getFileName().toString()+ " -k " + key;
        executeSystemCommand(command);
        return "Korisnici/"+"e_"+ put.getFileName().toString();
        
    }
    public static String decryptFajl(String putanja) {
    	Path put=Paths.get(putanja);
        String command = "openssl "+algoritamKf+" -d -in " + putanja+" -out "
                + "Korisnici/"+"d_" +put.getFileName().toString()+ " -k " + key;
        return executeSystemCommand(command);
        
    }
    
    public static String digitalSignature(String ime,String putanja) {
    	Path put=Paths.get(putanja);
        String command = "openssl dgst -sign " + "CAtijelo/private/" + "private"+ ime + ".key " +algoritam +"-out CAtijelo/signature/" + ime +
                "_potpis_" +put.getFileName().toString()+ ".sign " + putanja;
        return executeSystemCommand(command);
       
    }
    
    public static boolean verifiedSignature(String ime, String imeFajla) {
    	 boolean verifiedOk = false;
     
    	
    	  String command = "openssl dgst "+algoritam + "-verify CAtijelo/public/public" + ime + ".key -signature CAtijelo/signature/"+ ime + "_potpis_e_" + imeFajla+".sign "+" Korisnici/" + imeFajla;
    	  System.out.println(command);
    	  String rezultat=executeSystemCommand(command);
          if(rezultat.contains("Verified OK")){
              verifiedOk = true;
             
          }
          return verifiedOk;
         
    }
    
	
	public static String revokeCertificate(String putanja)
	{
		String revokeCertificate = "openssl ca -revoke " +putanja.replace("./","") + " -crl_reason certificateHold -config CAtijelo/openssl.cnf -key sigurnost";
		
		return executeSystemCommand(revokeCertificate);
       
	}

	public static String crlList()
	{
		String crlList = "openssl ca -gencrl -out CAtijelo/crl/Lista.crl -config CAtijelo/openssl.cnf -key sigurnost";
        return executeSystemCommand(crlList);
	}

	public static String executeSystemCommand(String command) {
		ProcessBuilder pb = new ProcessBuilder("/bin/bash", "-c", command);
        Process process = null;
        try {
            process = pb.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        StringBuilder output = new StringBuilder();
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        try {
            int ex = process.waitFor();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        return output.toString();
	}
}
