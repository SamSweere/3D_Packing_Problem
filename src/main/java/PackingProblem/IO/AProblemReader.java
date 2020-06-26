package PackingProblem.IO;

import java.io.*;
import java.util.ArrayList;

public abstract class AProblemReader {
    private final String filename;
    private final File file;

    public AProblemReader(String path, String filename) {
        this.filename = filename;

        //TODO: throw error when file does not exist
        this.file = new File("Solutions" + File.separatorChar + path + File.separatorChar + filename);
    }

    public ArrayList<String> readFromFile(){
        try {
            BufferedReader br = new BufferedReader(new FileReader(this.file));
            String st;
            ArrayList<String> data = new ArrayList<>();
            // Read the file to string
            while ((st = br.readLine()) != null){
                data.add(st);
            }
            System.out.println(String.format("Successfully read from file: %s", this.file.toString()));
            return data;
        } catch (FileNotFoundException e) {
            System.err.println(String.format("The input file: %s does not exist.", this.file.toString()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
