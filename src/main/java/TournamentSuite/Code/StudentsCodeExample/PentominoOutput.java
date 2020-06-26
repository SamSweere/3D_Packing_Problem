package TournamentSuite.Code.StudentsCodeExample;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class PentominoOutput {
    public static void writeToFile(PentominoProblem pentominoProblem, String filename){
        ArrayList<Pentomino> pentominoes = pentominoProblem.getPentominoes();
        ArrayList<String> pentominoesStrings = new ArrayList<>();

        for(int i = 0; i < pentominoes.size(); i++){
            // Convert all the locations and size back to the originals
            Coordinate[] coordinates = pentominoes.get(i).getCoordinates();

            StringBuilder s = new StringBuilder();

            for(int j = 0; j < coordinates.length; j++){
                s.append(String.format("(%s,%s,%s)", coordinates[j].x, coordinates[j].y, coordinates[j].z));
                if(j != coordinates.length-1){
                    s.append(";");
                }
            }

            pentominoesStrings.add(s.toString());
        }

        File file = new File(filename);
        try {
            FileWriter fw = new FileWriter(file);
            // initialize our BufferedWriter
            BufferedWriter bw = new BufferedWriter(fw);
            //Write all the location strings
            for(int i = 0; i < pentominoesStrings.size(); i++){
                bw.write(pentominoesStrings.get(i));
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
