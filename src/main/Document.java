package main;


import java.io.*;
import java.util.ArrayList;
import java.util.List;



public class Document {
    private  byte[] content;

    public Document(String putanja) {
       
        
        try {
            DataInputStream dataInputStream = new DataInputStream(new FileInputStream(putanja));
            byte[] bytes = dataInputStream.readAllBytes();
            dataInputStream.close();
            this.content =bytes;
            }
catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public byte[] getContent() {
        return content;
    }
    
    public static List<byte[]> splitDocument(Document document, int numSegments) {
        List<byte[]> segments = new ArrayList<>();
        byte[] content = document.getContent();

        int segmentSize = content.length / numSegments;
        int startIndex = 0;
        int endIndex = segmentSize;

        for (int i = 0; i < numSegments - 1; i++) {
            byte[] segment = new byte[segmentSize];
            System.arraycopy(content, startIndex, segment, 0, segmentSize);
            segments.add(segment);
            startIndex = endIndex;
            endIndex += segmentSize;
        }

       
        byte[] lastSegment = new byte[content.length - startIndex];
        System.arraycopy(content, startIndex, lastSegment, 0, content.length - startIndex);
        segments.add(lastSegment);

        return segments;
    }
    public static void writeSegments(List<byte[]> segments, String directoryPath,String ime) {
        for (int i = 0; i < segments.size(); i++) {
        	int broj=ProcesorFajlova.readCounter();
            String filePath = directoryPath+broj+"segment/" + "segment_" + i + ".bin"; // Kreiranje putanje za fajl
            File directory = new File(directoryPath+broj+"segment");
            if (!directory.exists()) 
                 directory.mkdirs();
            ProcesorFajlova.upisPutanjeFajla(ime,filePath);
            ProcesorFajlova.updateCounter(++broj);
            byte[] segment = segments.get(i);
            writeBytesToFile(filePath, segment);
        }
    }

    public static void writeBytesToFile(String filePath, byte[] data) {
        try (FileOutputStream fos = new FileOutputStream(filePath,true)) {
            fos.write(data);
        } catch (IOException e) {
            System.err.println("Greška prilikom pisanja u fajl: " + filePath);
            e.printStackTrace();
        }
    }

   

}

 