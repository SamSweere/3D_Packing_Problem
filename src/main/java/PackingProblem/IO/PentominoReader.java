package PackingProblem.IO;

import PackingProblem.Model.*;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Scanner;

public class PentominoReader extends AProblemReader {
    public PentominoReader(String filename) {
        super("PentominoeProblem", filename);
    }

    public void loadFromFile(PentominoProblem pentominoProblem){
        // Get the data from the file as a ArrayList of strings
        ArrayList<String> data = super.readFromFile();

        double multiplicationConstant = (double) pentominoProblem.getMultiplicationConstant();

        // Get the container string and remove a possible newline and whitespaces
        String containerString = data.get(0).replace("\n", "").replace(" ", "");

        // Create a regular expression to test and split the string
        String regexDecimalNumber = "\\d*\\.?\\d*";
        String regexCoordinate = "\\(" + regexDecimalNumber + "," + regexDecimalNumber + "," + regexDecimalNumber + "\\)";
        String regexPentomino = "(" + regexCoordinate + ",){4}" + regexCoordinate + ",?";

        // Check if the string has the correct format, if not throw error
        if (!containerString.matches(regexCoordinate)) {
            try {
                throw new Exception("Container line: \n" + containerString + "\n" + "has an incorrect format.");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        //Replace the '(',')' and ',' with spaces such that we can use a scanner
        containerString = containerString.replace(","," ").replace("(", " ").replace(")", " ");

        //Get the values from the coordinates
        Scanner scanner = new Scanner(containerString);
        scanner.useLocale(Locale.US);

        double temp_container_x = scanner.nextDouble()*multiplicationConstant;
        double temp_container_y = scanner.nextDouble()*multiplicationConstant;
        double temp_container_z = scanner.nextDouble()*multiplicationConstant;

        //Check if the pentominoe values are correct
        if((temp_container_x % 1) != 0 || (temp_container_y % 1) != 0 || (temp_container_z % 1) != 0){
            try {
                throw new Exception("One of the container values (" + containerString + ") does not round to " +
                        "an integer using the multiplication factor: " + Double.toString(multiplicationConstant));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // All the container values are correct
        int container_x = (int) Math.round(temp_container_x);
        int container_y = (int) Math.round(temp_container_y);
        int container_z = (int) Math.round(temp_container_z);

        // Make a new container from the data
        Container container = new Container(container_x, container_y, container_z);

        String subsetString = data.get(1);
        String[] subsetValues = subsetString.split(",\\(");

        for(String subsetValue : subsetValues){
            String tempSubsetValue = subsetValue.replace("(", "").replace(")", "");
            String[] subsetValueProperties = tempSubsetValue.split(",");
            Shape shape = Shape.valueOf(subsetValueProperties[0].strip());
            double value = Double.valueOf(subsetValueProperties[1].strip());
            int amount = Integer.valueOf(subsetValueProperties[2].strip());
            pentominoProblem.addStartPentomino(shape, value, amount);
        }

        double totalValue = Double.valueOf(data.get(2));
        pentominoProblem.setTotalValue(totalValue);

        ArrayList<Pentomino> pentominoes = new ArrayList<>();

        // Add all the pentominoes, remember the first three values are for the container
        for(int i = 3; i < data.size(); i++) {
            // Get the string and remove a possible newline and whitespaces
            String pentominoString = data.get(i).replace("\n", "").replace(" ", "");

            // Check if the string has the correct format, if not throw error
            if (!pentominoString.matches(regexPentomino)) {
                try {
                    throw new Exception("Pentominoe line: \n" + pentominoString + "\n" + "has an incorrect format.");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            Coordinate[] coordinates = new Coordinate[5];

            // Split the string line with all the coordinates on the ',(' such that we can loop through it
            String [] pentominoBoxes = pentominoString.split(",\\(");
            for(int j = 0; j < coordinates.length; j++){
                //Replace the '(',')' and ',' with spaces such that we can use a scanner
                String pentominoBox = pentominoBoxes[j].replace(","," ").replace("(", " ").replace(")", " ");

                //Get the values from the coordinates
                scanner = new Scanner(pentominoBox);
                scanner.useLocale(Locale.US);

                double xCoordinate = scanner.nextDouble()*multiplicationConstant;
                double yCoordinate = scanner.nextDouble()*multiplicationConstant;
                double zCoordinate = scanner.nextDouble()*multiplicationConstant;

                //Check if the pentominoe values are correct
                if((xCoordinate % 1) != 0 || (yCoordinate % 1) != 0 || (zCoordinate % 1) != 0){
                    try {
                        throw new Exception("One of the pentomino box values (" + pentominoBox + ") does not round to " +
                                "an integer using the multiplication factor: " + Double.toString(multiplicationConstant));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                //Everything is correct, now convert to ints and coordinates
                Coordinate box_coord = new Coordinate((int) Math.round(xCoordinate), (int) Math.round(yCoordinate),
                        (int) Math.round(zCoordinate));
                coordinates[j] = box_coord;
            }

            //Create a new Pentominoe and add it to the pentominoes
            Pentomino pentominoe = new Pentomino(coordinates);
            pentominoes.add(pentominoe);
        }

        // Set the container and pentominoes in the root PentominoeProblem
        pentominoProblem.setContainer(container);
        pentominoProblem.setPentominos(pentominoes);
    }
}
