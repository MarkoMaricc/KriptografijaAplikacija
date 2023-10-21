package main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Random;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class ProcesorFajlova {
	
	public static void regenerisiFajl(List<String> lista,String naziv)
	{
		for(String ss:lista) {
		Path put=Paths.get(ss);
		byte[] niz;
		try {
			
			
			niz = Files.readAllBytes(put);
			Document.writeBytesToFile("Korisnici/"+naziv,niz);
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		
		}
		
		
		
		
	}
	
	public static boolean getNaziv(String ime,String imeFajla)
	{
		boolean valid=false;
		ProcesorKomandi.decryptKorisniciFajl();
		 List<String> lista;
		try {
			 Path putanja=Paths.get("korisnici.txt");
	           lista=Files.readAllLines(putanja);
	          boolean ima=false;
	          String linija="";
	          for(String ss:lista)
	          {
	          	
	          	if(ima)
	          	{
	          		linija+=ss;
	          		if(ss.contains("##"))
	          			ima=false;
	          	}
	          	if(ss.contains("#"+ime))
	          	{
	          		if(ss.contains("$$"))
	          			{
	          			System.out.println("Korisnik nema dokumenata. ");
	          			
	          			}
	          		
	          		linija+=ss;
	          		if(!ss.contains("##"))
	          		ima=true;
	          	}
	          	
	          }
	       
	          
	          if(linija.length()>1) {
	        	 // System.out.println("red: "+linija);
	        	  linija=linija.replaceAll("#","");
	             
	              String niz[] = linija.split("\\$");
	           
	              String niza[] = niz[2].split(",");
	              String nizaa[] = niz[1].split(",");
	           List<String> putanje= Arrays.asList(nizaa);
	           List<String> fajlovi= Arrays.asList(niza);
	           int brojFajla=0;
	           int brojPutanje=0;
	           boolean prviSlucaj=false;
	           for(String ss:fajlovi)
	           {
	        	   if(brojFajla==0 && ss.equals(imeFajla) )
	        			   prviSlucaj=true;
	        	   if(ss.equals(imeFajla))
	        		   break;
	        	   brojFajla++;
	        	  
	           }
	         
	           List<String> fajlovi1=new ArrayList<String>();
	           
	          
	           for(String ss:putanje)
	           { 
	           if(brojPutanje==(fajlovi.size()-brojFajla-1) || prviSlucaj)
	        	   {
	        	   fajlovi1.add(ss);
	        	 
	        	   }
	           if(ss.contains("segment_0.bin") )
        		   {
	        	  
	        	   brojPutanje++;
        		   }
	           }
	           Collections.reverse(fajlovi1);
	       
	           
	           regenerisiFajl(fajlovi1,imeFajla);
	           if(ProcesorKomandi.verifiedSignature(ime,imeFajla)) {
	           valid=true;
	        	ProcesorKomandi.decryptFajl("Korisnici/"+imeFajla);
	        	  
	           }
	          
	           brisanjeFajla("Korisnici/"+imeFajla);
	      
	          }
	        
	          
	          
	          else {
	        	  System.out.println("Ne postoji takav korisnik. ");
	        	  
	          }
	          
	          
	     
	       } catch (IOException e) {
	           System.out.println("Greska pri otvaranju fajla. ");
	        
	           
	       }
		
		
		
	
		 ProcesorKomandi.encryptKorisniciFajl();
         brisanjeFajla("korisnici.txt");
         return valid;
		
	}
	
	
	
	
	
	public static int readCounter() {
        try (BufferedReader reader = new BufferedReader(new FileReader("counter.txt"))) {
            String line = reader.readLine();
            if (line != null) {
                return Integer.parseInt(line);
            }
        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
        }

        return 0; 
    }

	public static void updateCounter(int newValue) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("counter.txt"))) {
            writer.write(String.valueOf(newValue));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
	
	public static void add(String ime,String putanja)
	{
		 Path put=Paths.get(putanja);
		ProcesorFajlova.upisNazivaFajla(ime,put.getFileName().toString());
		String putanjaDoKriptovanog=ProcesorKomandi.encryptFajl(putanja);
		ProcesorKomandi.digitalSignature(ime, putanjaDoKriptovanog);
		Document doc=new Document(putanjaDoKriptovanog);
		
		  Random generator = new Random();
		   int numberSegments = generator.nextInt(10);
	        if (numberSegments < 4) {
	            numberSegments = 4;
	        }
	        
	        List<byte[]> lista=Document.splitDocument(doc, numberSegments);
	        
	        	
	        
	        
	       Document.writeSegments(lista,"Segmenti/",ime);
	       brisanjeFajla(putanjaDoKriptovanog);
	        
		
	}
	
	public static void upisPutanjeFajla(String ime,String putanjaZaUpis)
	{
		ProcesorKomandi.decryptKorisniciFajl();
		 List<String> lista;
		try {
			 Path putanja=Paths.get("korisnici.txt");
	           lista=Files.readAllLines(putanja);
	          
	          for(int i=0;i< lista.size();i++) 
	          {
	        	
	          	if(lista.get(i).contains("#"+ime))
	          	{
	          	  lista.set(i,"#"+ime+"$"+putanjaZaUpis+","+lista.get(i).replaceAll("#"+ime+"\\$",""));
	          	}
	        	  
	          }
	          
	          
	     	 try (BufferedWriter writer =new BufferedWriter(new FileWriter("korisnici.txt",false))){
				 for(String ss:lista)
				 writer.write(ss+"\n");
		            //writer.newLine();
		            
		            writer.close();
		           
		        } catch (IOException e) {
		            System.out.println("Fajl se ne moze otvoriti: " + e.getMessage());
		            
		        }
	          
	     
	       } catch (IOException e) {
	           System.out.println("Greska pri otvaranju fajla. ");
	           
	       }
		
		
		
	
		 ProcesorKomandi.encryptKorisniciFajl();
         brisanjeFajla("korisnici.txt");
		
	}
	
	public static void upisNazivaFajla(String ime,String imeFajla)
	{
		ProcesorKomandi.decryptKorisniciFajl();
		 List<String> lista;
		try {
			 Path putanja=Paths.get("korisnici.txt");
	           lista=Files.readAllLines(putanja);
	          boolean ima=false;
	          for(int i=0;i< lista.size();i++) 
	          {
	        	  if(ima)
	          	{
	        		  if(lista.get(i).contains("##"))
	          			{
	        			  lista.set(i,lista.get(i).replaceAll("##","").concat(","+imeFajla+"##"));
	        			  ima=false;
	          			}
	          	}
	          	if(lista.get(i).contains("#"+ime))
	          	{
	          		if(lista.get(i).contains("##"))
	          			{
	          			//if(lista.contains("$$") && !lista.contains(","))
	          			lista.set(i,lista.get(i).replaceAll("##","").concat(","+imeFajla+"##"));
	          			//else
	          			//	lista.set(i,lista.get(i).replaceAll("##","").concat(imeFajla+"##"));
	          			}
	          		if(!lista.get(i).contains("##"))
	          		ima=true;
	          	}
	        	  
	          }
	      
	          
	     	 try (BufferedWriter writer =new BufferedWriter(new FileWriter("korisnici.txt",false))){
				 for(String ss:lista)
				 writer.write(ss+"\n");
		          
		            writer.close();
		           
		        } catch (IOException e) {
		            System.out.println("Fajl se ne moze otvoriti: " + e.getMessage());
		            
		        }
	          
	     
	       } catch (IOException e) {
	           System.out.println("Greska pri otvaranju fajla. ");
	           
	       }
		
		
		
	
		 ProcesorKomandi.encryptKorisniciFajl();
         brisanjeFajla("korisnici.txt");
		
	}
	
	
	
	
	public static void upisKorisnika(String ime)
	{
		ProcesorKomandi.decryptKorisniciFajl();
		 try (BufferedWriter writer =new BufferedWriter(new FileWriter("korisnici.txt",true))){
	            writer.write("#"+ime+"$$##\n");
	           
	            
	            writer.close();
	           
	        } catch (IOException e) {
	            System.out.println("Fajl se ne moze otvoriti: " + e.getMessage());
	            
	        }
		 ProcesorKomandi.encryptKorisniciFajl();
         brisanjeFajla("korisnici.txt");
		
	}
	
public static List<String> listaFajlova(String ime)
{
	ProcesorKomandi.decryptKorisniciFajl();
	
	try {
		 Path putanja=Paths.get("korisnici.txt");
          List<String> lista=Files.readAllLines(putanja);
          boolean ima=false;
          String linija="";
        for(String ss:lista)
        {
        	
        	if(ima)
        	{
        		linija+=ss;
        		if(ss.contains("##"))
        			ima=false;
        	}
        	if(ss.contains("#"+ime))
        	{
        		if(ss.contains("$$"))
        			{
        			System.out.println("Korisnik nema dokumenata. ");
        			  ProcesorKomandi.encryptKorisniciFajl();
        	          brisanjeFajla("korisnici.txt");
        			return null;
        			}
        		
        		linija+=ss;
        		if(!ss.contains("##"))
        		ima=true;
        	}
        	
        }
      if(linija.length()>1) {
          linija=linija.replaceAll("#","");
        
          String niz[] = linija.split("\\$");
        
          String niza[] = niz[2].split(",");
          ProcesorKomandi.encryptKorisniciFajl();
          brisanjeFajla("korisnici.txt");
          return Arrays.asList(niza);}
      else 
    	  {
    	  System.out.println("Ne postoji takav korisnik.");
    	  ProcesorKomandi.encryptKorisniciFajl();
          brisanjeFajla("korisnici.txt");
    	  return null;
    	  }
       } catch (IOException e) {
           System.out.println("Greska pri otvaranju fajla. ");
           ProcesorKomandi.encryptKorisniciFajl();
           brisanjeFajla("korisnici.txt");
           return null;
       }
	
	
	
	
	  
}

public static void ispisFajlova(List<String> lista)
{
	if(lista!=null)
	lista.stream().forEach(System.out::println);
}

public static void brisanjeFajla(String putanja) {
	
    File fajl = new File(putanja);

   
    if (fajl.exists()) {
        
        if (fajl.delete()) {
           // System.out.println("Fajl je uspješno obrisan.");
        } else {
            System.out.println("Nije moguće obrisati fajl.");
        }
    } else {
        System.out.println("Fajl ne postoji na datoj putanji.");
    }
}



}
