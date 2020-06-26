package PackingProblem.IO;

import PackingProblem.Model.*;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Scanner;

public class ParcelReader extends AProblemReader {
    public ParcelReader(String filename) {
        super("ParcelProblem", filename);
    }

    public void loadFromFile(ParcelProblem parcelProblem){
        // Get the data from the file as a ArrayList of strings
        ArrayList<String> data = super.readFromFile();

        double multiplicationConstant = (double) parcelProblem.getMultiplicationConstant();

        // Get the container string and remove a possible newline and whitespaces
        String containerString = data.get(0).replace("\n", "").replace(" ", "");

        // Create a regular expression to test and split the string
        String regexDecimalNumber = "\\d*\\.?\\d*";
        String regexCoordinate = "\\(" + regexDecimalNumber + "," + regexDecimalNumber + "," + regexDecimalNumber + "\\)";
        String regexParcel = "^\\(" + regexDecimalNumber + "," + regexDecimalNumber + "," + regexDecimalNumber + "\\)," +
                regexDecimalNumber + "," + regexDecimalNumber + "," + regexDecimalNumber + ",?";

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
        int container_z = (int) Math.round(temp_container_z);;

        // Make a new container from the data
        Container container = new Container(container_x, container_y, container_z);

        String subsetString = data.get(1);
        String[] subsetValues = subsetString.split(",\\(");

        for(String subsetValue : subsetValues){
            String tempSubsetValue = subsetValue.replace("(", "").replace(")", "");
            String[] subsetValueProperties = tempSubsetValue.split(",");
            double x_size = Double.valueOf(subsetValueProperties[0].strip());
            double y_size = Double.valueOf(subsetValueProperties[1].strip());
            double z_size = Double.valueOf(subsetValueProperties[2].strip());
            double value = Double.valueOf(subsetValueProperties[3].strip());
            int amount = Integer.valueOf(subsetValueProperties[4].strip());

            parcelProblem.addStartParcel(x_size, y_size, z_size, value, amount);
        }

        double totalValue = Double.valueOf(data.get(2));
        parcelProblem.setTotalValue(totalValue);

        ArrayList<Parcel> parcels = new ArrayList<>();

        // Add all the parcels, remember the first three values are for the container
        for(int i = 3; i < data.size(); i++){
            // Get the string and remove a possible newline and whitespaces
            String parcelString = data.get(i).replace("\n","").replace(" ", "");

            // Check if the string has the correct format, if not throw error
            if(!parcelString.matches(regexParcel)){
                try {
                    throw new Exception("Parcel line: \n" + parcelString + "\n" + "has an incorrect format.");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            //Replace the '(',')' and ',' with spaces such that we can use a scanner
            parcelString = parcelString.replace(","," ").replace("(", " ").replace(")", " ");

            //Check if the parcel values are correct
            scanner = new Scanner(parcelString);
            scanner.useLocale(Locale.US);
            for(int j = 0; j < 6; j++){
                double val = scanner.nextDouble()*multiplicationConstant;
                if((val % 1) != 0){
                    // This value does not become an int when multiplying it with the multiplication factor, throw an error
                    try {
                        throw new Exception("One of the parcel values (" + Double.toString(val) + ") does not round to " +
                                "an integer using the multiplication factor: " + Double.toString(multiplicationConstant));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            //Everything is correct, now convert to ints
            //We are doing this operation twice while it could be done in one sweep. However this part of the program
            //is not really dependent on speed

            scanner = new Scanner(parcelString);
            scanner.useLocale(Locale.US);
            int x_loc = (int) Math.round((scanner.nextDouble()*multiplicationConstant));  // Simple to int conversion went wrong 3.0 became 2. Therefore use Math.round()
            int y_loc = (int) Math.round((scanner.nextDouble()*multiplicationConstant));
            int z_loc = (int) Math.round((scanner.nextDouble()*multiplicationConstant));
            int x_size = (int) Math.round((scanner.nextDouble()*multiplicationConstant));
            int y_size = (int) Math.round((scanner.nextDouble()*multiplicationConstant));
            int z_size = (int) Math.round((scanner.nextDouble()*multiplicationConstant));

            // Add the read parcel
            // The value is set to zero, since we only use this for the correctness and the visualizer we do not have to know
            // the value
            // TODO: this might cause a problem in the tournament
            double value = 0;
            int filled_volume = x_size*y_size*z_size;
            parcels.add(new Parcel(new Coordinate(x_loc, y_loc, z_loc), x_size, y_size, z_size, value, filled_volume));
        }

        // Set the container and parcel in the root ParcelProblem
        parcelProblem.setContainer(container);
        parcelProblem.setParcels(parcels);
    }
}
