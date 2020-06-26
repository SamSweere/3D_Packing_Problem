package TournamentSuite.Code.StudentsCodeExample;

import java.io.*;
import java.util.ArrayList;

public class PentominoInput {

    public static PentominoProblem loadFromFile(String filename) {
        // Get the data from the file as a ArrayList of strings
        ArrayList<String> data = new ArrayList<>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(filename));
            String st;

            // Read the file to string
            while ((st = br.readLine()) != null) {
                data.add(st);
            }
            System.out.println(String.format("Successfully read from file: %s", filename));
        } catch (FileNotFoundException e) {
            System.err.println(String.format("The input file: %s does not exist.", filename));
        } catch (IOException e) {
            e.printStackTrace();
        }

        String[] containerString = data.get(0).split(",");

        // All the container values are correct
        int container_x = Integer.valueOf(containerString[0]);
        int container_y = Integer.valueOf(containerString[1]);
        int container_z = Integer.valueOf(containerString[2]);

        PentominoProblem pentominoProblem = new PentominoProblem();
        pentominoProblem.setContainer(container_x, container_y, container_z);

        for (int index = 1; index < data.size(); index++) {
            String subsetString = data.get(index);
            String[] subsetValues = subsetString.split(",");
            Shape shape = Shape.valueOf(subsetValues[0].strip());
            int value = Integer.valueOf(subsetValues[1].strip());
            pentominoProblem.addPentominoToSubset(shape, value);
        }
        return pentominoProblem;
    }
}
