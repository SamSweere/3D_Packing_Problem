package PackingProblem.IO;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;   // Import the FileWriter class
import java.io.IOException;  // Import the IOException class to handle errors
import java.util.ArrayList;

public class AProblemWriter {
    private final String filename;
    private final File file;

    /**
     * @param path the relative path within the solutions folder
     * @param filename filename
     */
    public AProblemWriter(String path, String filename){
        this.filename = filename;

        // Check if needed folders exists, if not create it
        this.file = fileWithDirectoryAssurance("Solutions" + File.separatorChar + path, filename);
    }

    /** Creates parent directories if necessary. Then returns file */
    private static File fileWithDirectoryAssurance(String directory, String filename) {
        File dir = new File(directory);
        if (!dir.exists()) dir.mkdirs();
        return new File(directory + "/" + filename);
    }

    /**
     * @param x x size of container
     * @param y y size of container
     * @param z z size of container
     * @param locationStrings an ArrayList of strings that define the locations of the parcels/pentominoes
     */
    //, ArrayList coordinates
    public void writeToFile(double x, double y, double z, double totalValue, String subsetString, ArrayList<String> locationStrings){
        try {
            FileWriter fw = new FileWriter(this.file);
            // initialize our BufferedWriter
            BufferedWriter bw = new BufferedWriter(fw);
            // First write the x, y and z dimensions of the container
            bw.write(String.format("(%s,%s,%s)", Double.toString(x), Double.toString(y), Double.toString(z)));
            bw.newLine();

            //Write subset
            bw.write(subsetString);
            bw.newLine();

            //Write totalValue
            bw.write(String.format("%s", Double.toString(totalValue)));
            bw.newLine();

            //Write all the location strings
            for(int i = 0; i < locationStrings.size(); i++){
                bw.write(locationStrings.get(i));
                bw.newLine();
            }
            bw.close();
            fw.close();
            System.out.println(String.format("Successfully wrote to file: %s", this.file.toString()));
        } catch (IOException e) {
            System.out.println(String.format("An error occurred in writing file: %s", this.file.toString()));
            e.printStackTrace();
        }

    }
}
