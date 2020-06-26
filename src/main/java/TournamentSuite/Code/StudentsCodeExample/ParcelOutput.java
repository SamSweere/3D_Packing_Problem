package TournamentSuite.Code.StudentsCodeExample;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class ParcelOutput {
    public static void writeToFile(ParcelProblem parcelProblem, String filename){
        ArrayList<Parcel> parcels = parcelProblem.getParcels();
        ArrayList<String> parcelsStrings = new ArrayList<>();

        for(int i = 0; i < parcels.size(); i++){
            // Convert all the locations and size back to the originals
            Parcel currentParcel = parcels.get(i);

            StringBuilder s = new StringBuilder();
            s.append(String.format("%s;(%s,%s,%s)", currentParcel.getLocation().toString(),  currentParcel.getX_size(), currentParcel.getY_size(), currentParcel.getZ_size()));
            parcelsStrings.add(s.toString());
        }

        File file = new File(filename);
        try {
            FileWriter fw = new FileWriter(file);
            // initialize our BufferedWriter
            BufferedWriter bw = new BufferedWriter(fw);
            //Write all the location strings
            for(int i = 0; i < parcelsStrings.size(); i++){
                bw.write(parcelsStrings.get(i));
                bw.newLine();
            }
            bw.close();
            fw.close();
            System.out.println(String.format("Successfully wrote to file: %s", file.toString()));
        } catch (IOException e) {
            System.out.println(String.format("An error occurred in writing file: %s", file.toString()));
            e.printStackTrace();
        }
    }
}
