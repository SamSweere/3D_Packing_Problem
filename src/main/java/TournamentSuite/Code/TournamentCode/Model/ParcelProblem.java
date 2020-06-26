package TournamentSuite.Code.TournamentCode.Model;

import java.io.*;
import java.util.ArrayList;

public class ParcelProblem {
    private Container container;
    private ArrayList<Parcel> parcelSubset;
    private Parcel[][][] positioning;
    private ArrayList<Parcel> parcels;

    // This constructor is used when loading the problem
    public ParcelProblem(){
        this.parcelSubset = new ArrayList<>();
        this.parcels = new ArrayList<>();
    }

    public Container getContainer() {
        return this.container;
    }

    public void setContainer(int x_size, int y_size, int z_size) {
        this.container = new Container(x_size, y_size, z_size);
    }

    public ArrayList<Parcel> getParcelSubset() {
        return this.parcelSubset;
    }

    public boolean isParcelSetable(Parcel parcel){
        for(Coordinate coordinate: parcel.calculateCoordinates()){
            if(coordinate.x >= this.container.getX_size() || coordinate.y >= this.container.getY_size() || coordinate.z >= this.container.getZ_size() ||
                    coordinate.x < 0 || coordinate.y < 0 || coordinate.z < 0){
//                System.out.println("ERROR: Parcel is outside of the container in coordinate" + coordinate);
                return false;
            }
            else if(this.positioning[coordinate.x][coordinate.y][coordinate.z] != null){
//                System.out.println("ERROR: Parcels overlap in coordinate " + coordinate);
                return false;
            }
        }
        return true;
    }

    public ArrayList<Parcel> getParcels() {
        return this.parcels;
    }

    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("ParcelProblem containing:  (note that these are the internal values, they are not" +
                " converted using the multiplication constant)\n");

        sb.append(container.toString() + "\n");
        sb.append("Having parcelSubset: \n");
        for(int i = 0; i < this.parcelSubset.size(); i++){
            sb.append(this.parcelSubset.get(i).toString() + "\n");
        }
        sb.append("Having packing: \n");

        for(int i = 0; i < this.parcels.size(); i++){
            sb.append(this.parcels.get(i).toString() + "\n");
        }
        return sb.toString();
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
        ArrayList<String> inputData = this.convertFileToStringArray(inputFilename);
        ArrayList<String> outputData = this.convertFileToStringArray(outputFilename);

        // Get the container string and remove a possible newline and whitespaces
        String containerString = inputData.get(0).replace("\n", "").replace(" ", "");

        // Create a regular expression for the container sting
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

        //Check if the container values are correct
        if( container_x < 0 || container_y < 0 || container_z < 0){
            try {
                throw new Exception("One of the container values (" + containerString + ") is 0 or negative");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // Make a new container from the data
        Container container = new Container(container_x, container_y, container_z);

        // Create a regular expression for the subset string
        String regexParcelSubset = "\\d+,\\d+,\\d+,\\d+";

        for(int index = 1; index<inputData.size(); index++){
            // Remove a possible newline and whitespaces
            String subsetString = inputData.get(index).replace("\n", "").replace(" ", "");;

            // Check if the subset values are correct
            if (!subsetString.matches(regexParcelSubset)) {
                try {
                    throw new Exception("Parcel subset line: \n" + subsetString + "\n" + "has an incorrect format.");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            // Add parcel to subset
            String[] subsetValueProperties = subsetString.split(",");
            int x_size = Integer.valueOf(subsetValueProperties[0].strip());
            int y_size = Integer.valueOf(subsetValueProperties[1].strip());
            int z_size = Integer.valueOf(subsetValueProperties[2].strip());
            int value = Integer.valueOf(subsetValueProperties[3].strip());
            parcelSubset.add(new Parcel(x_size, y_size, z_size, value));
        }

        ArrayList<Parcel> parcels = new ArrayList<>();

        // Create a regular expression for the parcels
        String regexParcel = "\\(" + regexCoordinate + "\\);" + "\\(" + regexCoordinate + "\\)";

        // Add all the pentominoes, remember the first three values are for the container
        for(int i = 0; i < outputData.size(); i++) {
            // Get the string and remove a possible newline and whitespaces
            String parcelString = outputData.get(i).replace("\n", "").replace(" ", "");

            // Check if the string has the correct format, if not throw error
            if (!parcelString.matches(regexParcel)) {
                try {
                    throw new Exception("Pentominoe line: \n" + parcelString + "\n" + "has an incorrect format.");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            Coordinate[] coordinates = new Coordinate[2];

            String [] parcelBoxes = parcelString.split(";");

            for(int j = 0; j < coordinates.length; j++) {
                String parcelBox = parcelBoxes[j].replace("(", "").replace(")", "");
                String[] stringCoordinates = parcelBox.split(",");

                int xCoordinate = Integer.valueOf(stringCoordinates[0]);
                int yCoordinate = Integer.valueOf(stringCoordinates[1]);
                int zCoordinate = Integer.valueOf(stringCoordinates[2]);

                // Check if the pentominoe values are correct
                if(xCoordinate < 0 || yCoordinate < 0 || zCoordinate < 0){
                    try {
                        throw new Exception("One of the pentomino box values (" + parcelBox + ") is negative.");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                // Everything is correct, now convert to ints and coordinates
                Coordinate box_coord = new Coordinate(xCoordinate, yCoordinate, zCoordinate);
                coordinates[j] = box_coord;
            }

            //Create a new Pentominoe and add it to the pentominoes
            Parcel parcel = new Parcel(coordinates[0], coordinates[1].x, coordinates[1].y, coordinates[1].z);
            parcels.add(parcel);
        }

        // Set the container and pentominoes in the root PentominoeProblem
        this.container = container;
        this.parcels = parcels;
    }

    public boolean checkCorrectness(){
        this.positioning = new Parcel[this.container.getX_size()][this.container.getY_size()][this.container.getZ_size()];

        for(int i = 0; i<this.parcels.size(); i++){
            Parcel parcel = this.parcels.get(i);
            Parcel subsetElement = null;

            //Check if current parcel is in the subset
            for(int j=0; j<this.parcelSubset.size(); j++){
                if(parcelSubset.get(j).equals(parcel)){
                    subsetElement = parcelSubset.get(j);
                }
            }

            //If current parcel is in the subset, check if it is setable
            if(subsetElement != null){
                if(this.isParcelSetable(parcel)){
                    ArrayList<Coordinate> coordinates = parcel.calculateCoordinates();
                    for (int j = 0; j < coordinates.size(); j++) {
                        Coordinate coordinate = coordinates.get(j);
                        this.positioning[coordinate.x][coordinate.y][coordinate.z] = parcel;
                    }
                }else{
                    return false;
                }
            }else{
                System.out.println("ERROR: Parcel of line index " + i + " is not in the subset");
                return false;
            }
        }
        System.out.println("Packing is valid");
        return true;
    }
}
