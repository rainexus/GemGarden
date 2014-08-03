import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

public class GemGardenMerge {
	public static void main(String[ ] arg){
		File file = new File("./");
		ArrayList<File> csvList = new ArrayList<File>();  
        File[] files = file.listFiles();
        
        for (File file2 : files) {  
            if (file2.isDirectory()) {
            } else {
            	if (file2.getName().contains(".csv")) {
            		System.out.println(file2.getName());
            		csvList.add(file2);
            	}
            }  
        }
        
        String output = "";
        HashSet<String> hashSet = new HashSet<String>();
        for (int i=0; i<csvList.size(); ++i) {
        	String sCurrentLine;
        	BufferedReader br;
			try {
				br = new BufferedReader(new FileReader(csvList.get(i)));
				while ((sCurrentLine = br.readLine()) != null) {
					if(!hashSet.contains(sCurrentLine)) {
						hashSet.add(sCurrentLine);
						output += sCurrentLine + "\n";
					}
				}
				br.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
        
        File folder = new File("./Output/");
		if (!folder.exists()) {
		    boolean success = folder.mkdir();
		    if (!success) {
		    	throw new RuntimeException("folder.mkdir");
		    }
		}
		
		BufferedWriter bw;
		try {
			bw = new BufferedWriter(new FileWriter("./Output/output.csv"));
			bw.write(output);
			bw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        System.out.println(output);
	}
}
