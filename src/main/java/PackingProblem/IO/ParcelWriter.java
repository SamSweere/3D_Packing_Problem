package PackingProblem.IO;

import PackingProblem.Model.*;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Locale;

public class ParcelWriter extends AProblemWriter {

    public ParcelWriter(String filename) {
        super("ParcelProblem", filename);
    }

    public void writeToFile(ParcelProblem parcelProblem){
        Container container = parcelProblem.getContainer();
        double multiplicationConstant = (double) parcelProblem.getMultiplicationConstant();

        // Convert the container values to the real values by dividing it by the mutliplication constant
        double container_x = ((double) container.getX_size())/multiplicationConstant;
        double container_y = ((double) container.getY_size())/multiplicationConstant;
        double container_z = ((double) container.getZ_size())/multiplicationConstant;

        ArrayList<StartParcel> subset = parcelProblem.getParcelSubset();
        StringBuilder subsetString = new StringBuilder();
        // Use a decimal format to remove trailing zeros, i.e. 1.500000 becomes 1.5 and 2.0000 becomes 2
        DecimalFormatSymbols formatSymbols = new DecimalFormatSymbols(Locale.US);
        DecimalFormat d_format = new DecimalFormat("0.#", formatSymbols);

        for (int i = 0; i < subset.size(); i++){
            StartParcel subsetElement = subset.get(i);
            Parcel parcel = subsetElement.getParcel();
            double x_size = parcel.getX_size()/multiplicationConstant;
            double y_size = parcel.getY_size()/multiplicationConstant;
            double z_size = parcel.getZ_size()/multiplicationConstant;
            double value = subsetElement.getValue();
            int amount = subsetElement.getAmount();
            subsetString.append(String.format("(%s, %s, %s, %s, %d)", d_format.format(x_size), d_format.format(y_size), d_format.format(z_size), d_format.format(value), amount));
            if(i != subset.size()-1){
                subsetString.append(",");
            }
        }

        ArrayList<Parcel> parcels = parcelProblem.getParcels();
        ArrayList<String> parcelStrings = new ArrayList<>();

        for(int i = 0; i < parcels.size(); i++){
            Parcel parcel = parcels.get(i);
            // Convert all the locations and size back to the originals
            double x_loc = ((double) parcel.getLocation().x )/multiplicationConstant;
            double y_loc = ((double) parcel.getLocation().y)/multiplicationConstant;
            double z_loc = ((double) parcel.getLocation().z)/multiplicationConstant;
            double x_size = ((double) parcel.getX_size())/multiplicationConstant;
            double y_size = ((double) parcel.getY_size())/multiplicationConstant;
            double z_size = ((double) parcel.getZ_size())/multiplicationConstant;

            // Convert to the appropriate string
            String s = String.format("(%s,%s,%s),%s,%s,%s", d_format.format(x_loc), d_format.format(y_loc),
                    d_format.format(z_loc), d_format.format(x_size), d_format.format(y_size), d_format.format(z_size));
            parcelStrings.add(s);
        }

        super.writeToFile(container_x, container_y, container_z, parcelProblem.getTotalValue(), subsetString.toString(), parcelStrings);
    }

}
