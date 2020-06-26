package TournamentSuite.Code.TournamentCode.Model;

import java.io.*;
import java.util.ArrayList;

public class PentominoProblem {
    private Container container;
    private ArrayList<Pentomino> pentominoSubset;
    private ArrayList<Pentomino> pentominoes;
    private Pentomino[][][] positioning;

    public PentominoProblem() {
        this.pentominoes = new ArrayList<>();
        this.pentominoSubset = new ArrayList<>();
    }

    public Container getContainer() {
        return this.container;
    }

    public void setContainer(int x_size, int y_size, int z_size) {
        this.container = new Container(x_size, y_size, z_size);
    }

    public ArrayList<Pentomino> getPentominoSubset() {
        return this.pentominoSubset;
    }

    public ArrayList<Pentomino> getPentominoes() {
        return this.pentominoes;
    }

    private ArrayList<String> convertFileToStringArray(String filename){
        File inputFile = new File(filename);
        ArrayList<String> data = new ArrayList<>();

        try {
            BufferedReader br = new BufferedReader(new FileReader(inputFile));
            String st;

            // Read the file to string
            while ((st = br.readLine()) != null){
                data.add(st);
            }
            System.out.println(String.format("Successfully read from file: %s", inputFile.toString()));
        } catch (FileNotFoundException e) {
            System.err.println(String.format("The input file: %s does not exist.", inputFile.toString()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return data;
    }

    public void loadFromFile(String inputFilename, String outputFilename) {
        // Get data from files
        ArrayList<String> inputData = this.convertFileToStringArray(inputFilename);
        ArrayList<String> outputData = this.convertFileToStringArray(outputFilename);

        // Get the container string and remove a possible newline and whitespaces
        String containerString = inputData.get(0).replace("\n", "").replace(" ", "");

        // Create a regular expression for coordinates
        String regexCoordinate = "\\d+,\\d+,\\d+";

        // Check if the string has the correct format, if not throw error
        if (!containerString.matches(regexCoordinate)) {
            try {
                throw new Exception("Container line: \n" + containerString + "\n" + "has an incorrect format.");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        String[] containerSizes = containerString.split(",");

        int container_x = Integer.parseInt(containerSizes[0]);
        int container_y = Integer.parseInt(containerSizes[1]);
        int container_z = Integer.parseInt(containerSizes[2]);

        // Check if the container values are correct
        if( container_x <= 0 || container_y <= 0 || container_z <= 0){
            try {
                throw new Exception("One of the container values (" + containerString + ") is 0 or negative");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // Make a new container from the data
        this.container = new Container(container_x, container_y, container_z);

        // Create a regular expression for the subset string
        String regexPentominoSubset = "[F,I,L,N,P,T,U,V,W,X,Y,Z],\\d+";

        for(int index = 1; index<inputData.size(); index++){
            // Remove a possible newline and whitespaces
            String subsetString = inputData.get(index).replace("\n", "").replace(" ", "");

            // Check if the subset values are correct
            if (!subsetString.matches(regexPentominoSubset)) {
                try {
                    throw new Exception("Pentomino subset line: \n" + subsetString + "\n" + "has an incorrect format.");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            // Add pentomino to subset
            String[] subsetValueProperties = subsetString.split(",");
            Shape shape = Shape.valueOf(subsetValueProperties[0].strip());
            int value = Integer.valueOf(subsetValueProperties[1].strip());
            this.pentominoSubset.add(new Pentomino(shape, value));
        }

        // Create a regular expression for pentominoes
        String regexPentomino = "(\\(" + regexCoordinate + "\\);){4}" + "\\(" + regexCoordinate + "\\)";

        // Add all the pentominoes
        for(int i = 0; i < outputData.size(); i++) {
            // Get the string and remove a possible newline and whitespaces
            String pentominoString = outputData.get(i).replace("\n", "").replace(" ", "");

            // Check if the string has the correct format, if not throw error
            if (!pentominoString.matches(regexPentomino)) {
                try {
                    throw new Exception("Pentominoe line: \n" + pentominoString + "\n" + "has an incorrect format.");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            Coordinate[] coordinates = new Coordinate[5];

            String [] pentominoBoxes = pentominoString.split(";");
            for(int j = 0; j < coordinates.length; j++){
                String pentominoBox = pentominoBoxes[j].replace("(", "").replace(")", "");
                String[] stringCoordinates = pentominoBox.split(",");

                int xCoordinate = Integer.valueOf(stringCoordinates[0]);
                int yCoordinate = Integer.valueOf(stringCoordinates[1]);
                int zCoordinate = Integer.valueOf(stringCoordinates[2]);

                //Check if the pentomino values are correct
                if(xCoordinate < 0 || yCoordinate < 0 || zCoordinate < 0){
                    try {
                        throw new Exception("One of the pentomino box values (" + pentominoBox + ") is negative.");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                Coordinate box_coord = new Coordinate(xCoordinate, yCoordinate, zCoordinate);
                coordinates[j] = box_coord;
            }

            //Create a new pentomino and add it to the pentominoes
            Pentomino pentomino = new Pentomino(coordinates);
            this.pentominoes.add(pentomino);
        }
    }

    public boolean isPentominoSetable(Pentomino pentomino) {
        for (Coordinate coordinate : pentomino.getCoordinates()) {
            if (coordinate.x >= this.container.getX_size() || coordinate.y >= this.container.getY_size() || coordinate.z >= this.container.getZ_size() ||
                    coordinate.x < 0 || coordinate.y < 0 || coordinate.z < 0){
//                System.out.println("ERROR: Pentomino is outside of the container in coordinate" + coordinate);
                return false;
            }
            else if(this.positioning[coordinate.x][coordinate.y][coordinate.z] != null) {
//                System.out.println("ERROR: Pentominoes overlap in coordinate " + coordinate);
                return false;
            }
        }
        return true;
    }

    public boolean checkCorrectness() {
        this.positioning = new Pentomino[this.container.getX_size()][this.container.getY_size()][this.container.getZ_size()];

        for (int i = 0; i < pentominoes.size(); i++) {
            Pentomino pentomino = this.pentominoes.get(i);
            Shape shape = pentomino.getShape();
            Pentomino subsetElement = null;

            // Check if current pentomino shape is in the subset
            for (int subsetIndex = 0; subsetIndex < pentominoSubset.size(); subsetIndex++) {
                if (pentominoSubset.get(subsetIndex).getShape() == shape) {
                    subsetElement = pentominoSubset.get(subsetIndex);
                    break;
                }
            }

            // If current pentomino shape is in the subset, check if it is setable
            if (subsetElement != null) {
                if (this.isPentominoSetable(pentomino)) {
                    for (Coordinate coordinate : pentomino.getCoordinates()) {
                        positioning[coordinate.x][coordinate.y][coordinate.z] = pentomino;
                    }
                } else {
                    return false;
                }
            } else {
                System.out.println("ERROR: Pentomino of line index " + i + " has the shape " + shape.name() + " and is not in the subset");
                return false;
            }
        }
        System.out.println("Packing is valid");
        return true;
    }
}
