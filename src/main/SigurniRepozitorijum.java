package main;

import java.io.BufferedWriter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509CRL;
import java.security.cert.X509CRLEntry;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.Scanner;
import java.util.*;
import java.nio.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class SigurniRepozitorijum {
 
	
	
	public static void preuzmi(String ime,String naziv)
	{
		
		
			
			if(ProcesorFajlova.getNaziv(ime,naziv))
			System.out.println("Uspjesno preuzimanje. ");
			else
				System.out.println("Imamo problema sa vasim potpisom, moguce da je fajl neovlasteno izmjenjen. ");
		}
	
	
	public static void prikaz(String ime)
	{
		 Scanner sc = new Scanner(System.in);
	System.out.println("SPISAK VASIH DOKUMENATA: ");
	ProcesorFajlova pr=new ProcesorFajlova();
	pr.ispisFajlova(pr.listaFajlova(ime));
	
	System.out.println("VASE OPCIJE:    PREUZIMANJE DOKUMENTA  [pre]   DODAVANJE DOKUMENTA  [add]  IZLAZ  [izl]");
	 String ulaz="";
	 ulaz=sc.nextLine();
	 
	 if("add".equals(ulaz)) 
	 {
		 System.out.println("Unesite putanju do vaseg dokumenta: ");
		 ulaz=sc.nextLine();
		 ProcesorFajlova.add(ime,ulaz);
		 System.out.println("Uspjesno dodavanje. ");
		 prikaz(ime);
	 }
	 else if("pre".equals(ulaz)) 
	 {
		 System.out.println("Unesite naziv fajla koji zelite da preuzmete: ");
		 ulaz=sc.nextLine();
		 preuzmi(ime,ulaz);
		 prikaz(ime);
	 }
	
	 File directory = new File("Korisnici");

     if (!directory.exists() || !directory.isDirectory()) {
    	 System.out.println("Direktori ne postoji. ");
     }

     File[] files = directory.listFiles();
     if (files != null) {
         for (File file : files) {
             if (file.isFile()) {
                 if (!file.delete()) {
                	 System.out.println("Greska prilikom brisanja fajla u direktorijumu /Korisnici. ");
                 }
             }
         }
	 
		
	}
	
	}

	public static void pocetak() {
		
		 Scanner sc = new Scanner(System.in);
	        System.out.println("##########   Sigurna aplikacija za skladistenje dokumenata ###########");
	        System.out.println("#################### OPCIJE #########################################");
	        String ulaz="";
	        while(!"kraj".equals(ulaz)) {
	        	
	        	 System.out.println("REGISTRACIJA   [reg]   PRIJAVLJIVANJE   [pri]    KRAJ    [kraj]   " );
	        	 ulaz=sc.nextLine();
	        	 if("reg".equals(ulaz)) 
	        	 {
	        		 registracija();
	        	 }
	        	 else if("pri".equals(ulaz)) 
	        	 {
	        		 prijava();
	        	 }
	        	 
	        }

	}
	

	
	private static String getIme(String dn) {
	    String[] dnComponents = dn.split(",");
	    for (String component : dnComponents) {
	    
	        if (component.startsWith("CN=")) {
	        	
	            return component.substring(3);
	        }
	    }
	    return null;  
	}
	
	
	
	
	
	private static void upisListaKorisnika(String tekst)
	{
		 try (BufferedWriter writer =new BufferedWriter(new FileWriter("./lista.txt",true))){
	            writer.write(tekst);
	            writer.newLine();
	        } catch (IOException e) {
	            System.out.println("Fajl se ne moze otvoriti: " + e.getMessage());
	        }
		
	}
	
	private static boolean provjeraKorisnika(String ime,String sifra)
	{
		
		 try {
			 Path putanja=Paths.get("./lista.txt");
	           List<String> lista=Files.readAllLines(putanja);
	         
	           for(String ss:lista)
	           {
	        	   if(ss.length()>0)
	        	   {
	        		   String[] podjela = ss.split("#");
	        		   if (podjela[0].equals(ime)) {
	                       if (podjela[1].equals(sifra)) {
	                    	   System.out.println("Kredencijali su ispravni. ");
	                           return true;
	                       }
	                   }
	        		   
	        	   }
	           }
	          
	        } catch (IOException e) {
	            System.out.println("Greska pri otvaranju fajla. ");
	        }
		 System.out.println("Kredencijali nisu ispravni. ");
		 return false;
		
	}
	
	private static boolean provjeraSertifikatKorisnik(String ime,String putanja)
	{
		boolean vrijednost=true;
		FileInputStream certificateFile;
		try {
			certificateFile = new FileInputStream(putanja);
			   CertificateFactory certificateFactory=CertificateFactory.getInstance("X.509");
		       
		        X509Certificate certificate=(X509Certificate)certificateFactory.generateCertificate(certificateFile);
		        
		        
				 String procitano=certificate.getSubjectX500Principal().getName();
				 String imee=getIme(procitano);
				
				 
				 if(!ime.equals(imee))
				 {
					 System.out.println("Koristenje tudjeg sertifikata nije dozvoljeno. :(");
					 System.out.println("Ime na sertifikatu: "+imee);
					 vrijednost=false;
					 return vrijednost;
				 }
		} 
		catch (Exception e) {
			
			e.printStackTrace();
		}
		
		
		 
     
        
        return vrijednost;
	}
	
	
	private static boolean  validnostSertifikata(String putanja)
	{
		boolean vrednost=true;
		 try {
	           
			  FileInputStream certificateFile=new FileInputStream(putanja);
			 
	            CertificateFactory certificateFactory=CertificateFactory.getInstance("X.509");
	           
	            X509Certificate certificate=(X509Certificate)certificateFactory.generateCertificate(certificateFile);
	          
	            X509CRL crl =(X509CRL)certificateFactory.generateCRL(
	            		new FileInputStream("/home/markom/eclipse-workspace/KriptografijaAplikacija/CAtijelo/crl/Lista.crl"));
	            
	            X509CRLEntry revokedCertificate=crl.getRevokedCertificate(certificate.getSerialNumber());
	            Date treVr = new Date();
	            
	            if(treVr.before(certificate.getNotBefore()) || treVr.after(certificate.getNotAfter()))
	            {
	            	System.out.println("Sertifikat je istekao.");
	            	vrednost=false;
	            	return vrednost;
	            	
	            }
			 if(revokedCertificate != null)
			 {
				 System.out.println("Sertifikat je povucen.");
				 vrednost=false;
				 return vrednost;
			 }
			 
			 String imeIzdavaca="EMAILADDRESS=marko@hotmail.com, CN=MarkoMaric, OU=ETF, O=Elektrotehnicki fakultet, L=Banja Luka, ST=RS, C=BA";
			
			 String procitano=certificate.getIssuerX500Principal().toString();
			 
			 
			 if(!imeIzdavaca.equals(procitano))
			 {
				 System.out.println("Ime izdavaca nije validno.");
				 System.out.println("Ime izdavaca: "+procitano);
				 vrednost=false;
				 return vrednost;
			 }
			 
			 
	        }
		 catch (FileNotFoundException e) {
	            System.out.println("Fajl ne postoji.");
	            vrednost=false;
	            return vrednost;
	        }
		 
		 catch (Exception e) {
	            System.out.println("Doslo je do greske prilikom validacije.");
	            vrednost=false;
	            return vrednost;
	        }
		 System.out.println("Certifikat je vazeci.");
		 return vrednost;
		
	}
	private static void registracija() {
		
		 Scanner sc = new Scanner(System.in);
	
	        String ime="";
	        String sifra="";
	       
	        	 System.out.println("Vase korisnicko ime:" );
	        	 ime=sc.nextLine();
	        	 System.out.println("Vasa sifra:" );
	        	 sifra=sc.nextLine();
	        	 String hSifre=HesiranjeSifre.hesiranjeSifre(sifra,"crypto", "SHA-512");
	        	
	        	 upisListaKorisnika(ime + "#" + hSifre + "\n");
	        	 ProcesorFajlova.upisKorisnika(ime);
	        	 
	        	 System.out.println("Sacekajte da vam CAtijelo izda sertifikat." );

	}
	
	private static void prijava() {
		
		 Scanner sc = new Scanner(System.in);
		 System.out.println("Unesite odgovarajucu putanju do digitalnog sertifikata:" );
	        
	       String putanja="";
	       putanja=sc.nextLine();
	       File file=new File(putanja);
	      
	       while(!file.exists() ) {
	    		   System.out.println("Sertifikat ne postoji na datoj putanji." );
	    	   System.out.println("Unesite ponovo odgovarajucu putanju do digitalnog sertifikata:" );
	    	   putanja=sc.nextLine();
	    	   file=new File(putanja);
	       }
	      if( validnostSertifikata(putanja))
	      {
	       
	       String ime="";
	        String sifra="";
	       
	        	 System.out.println("Vase korisnicko ime:" );
	        	 ime=sc.nextLine();
	        	 System.out.println("Vasa sifra:" );
	        	 sifra=sc.nextLine();
	        	 String hSifre=HesiranjeSifre.hesiranjeSifre(sifra,"crypto", "SHA-512");
	        	 int i=1;
	       while(!provjeraKorisnika(ime,hSifre)  && i<3)
	       {
	    	   System.out.println("Vase korisnicko ime:" );
	        	 ime=sc.nextLine();
	        	 System.out.println("Vasa sifra:" );
	        	 sifra=sc.nextLine();
	        	  hSifre=HesiranjeSifre.hesiranjeSifre(sifra,"crypto", "SHA-512");
	        	  i++;
	    	   
	       }
	       if(i==3)
	       {
	    	   String izlaz1="";
	    	   String izlaz2="";
	    	   System.out.println(izlaz1=ProcesorKomandi.revokeCertificate(putanja));
	    	   System.out.println(izlaz2=ProcesorKomandi.crlList());
	    	   if(izlaz1.length()<2 && izlaz2.length()<2 ) {
	    	   System.out.println("Vas sertifikat je suspendovan :(" );
	    	   String ulaz="";
	    	   System.out.println("Da li zelite da reaktivirate sertifikat[rea],ili da se ponovo registurjete [reg]" );
	    	   ulaz=sc.nextLine();
	    	   if("rea".equals(ulaz)) 
	        	 {
	    		   System.out.println("Vase korisnicko ime:" );
		        	 ime=sc.nextLine();
		        	 System.out.println("Vasa sifra:" );
		        	 sifra=sc.nextLine();
		        	  hSifre=HesiranjeSifre.hesiranjeSifre(sifra,"crypto", "SHA-512");
		        	 if( provjeraKorisnika(ime,hSifre) )
		        	 {
		        		 //reaktivacija
		        		 System.out.println("CA tijelo ce izvrsiti reaktivaciju vaseg sertifikata." );
		        		 return;
		        	 }
		        	 else
		        		 System.out.println("Reaktivacija nije izvrsena." );
	        	 }
	        	 else if("reg".equals(ulaz)) 
	        	 {
	        		 registracija();
	        	 }
	    	   }
	       }
	       else
	       {
	    	   if(provjeraSertifikatKorisnik(ime,putanja))
	    	   {
	    		   System.out.println("Uspjesno ste se ulogovali." );
	    		   prikaz(ime);
	    	   
	    	   }
      	 }
	      }
	        	 
	        

	}
	
	public static void main(String[] args) {
		
		
		pocetak();
		
	
		//                         ./CAtijelo/certs/certifikatmilos.crt
		//                          /home/markom/Desktop/korisnici.txt
		//                            /home/markom/Desktop/ss.txt
	
		//ProcesorKomandi.encryptKorisniciFajl();
	}
	

}
