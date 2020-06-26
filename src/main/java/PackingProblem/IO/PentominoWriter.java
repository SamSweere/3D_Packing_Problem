package PackingProblem.IO;

import PackingProblem.Model.*;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Locale;

public class PentominoWriter extends AProblemWriter {
    public PentominoWriter(String filename) {
        super("PentominoeProblem", filename);
    }

    public void writeToFile(PentominoProblem pentominoProblem){
        Container container = pentominoProblem.getContainer();
        double multiplicationConstant = (double) pentominoProblem.getMultiplicationConstant();

        // Convert the container values to the real values by dividing it by the multiplication constant
        double container_x = ((double) container.getX_size())/multiplicationConstant;
        double container_y = ((double) container.getY_size())/multiplicationConstant;
        double container_z = ((double) container.getZ_size())/multiplicationConstant;

        ArrayList<StartPentomino> subset = pentominoProblem.getPentominoSubset();
        StringBuilder subsetString = new StringBuilder();
        // Use a decimal format to remove trailing zeros, i.e. 1.500000 becomes 1.5 and 2.0000 becomes 2
        DecimalFormatSymbols formatSymbols = new DecimalFormatSymbols(Locale.US);
        DecimalFormat d_format = new DecimalFormat("0.#", formatSymbols);

        for (int i = 0; i < subset.size(); i++){
            subsetString.append(String.format("(%s, %s, %d)", subset.get(i).getShape(), d_format.format(subset.get(i).getValue()), subset.get(i).getAmount()));
            if(i != subset.size()-1){
                subsetString.append(",");
            }
        }

        ArrayList<Pentomino> pentominoes = pentominoProblem.getPentominos();
        ArrayList<String> pentominoesStrings = new ArrayList<>();

        for(int i = 0; i < pentominoes.size(); i++){
            // Convert all the locations and size back to the originals
            Coordinate[] coordinates = pentominoes.get(i).getCoordinates();
            Double[][] coordinatesDouble = new Double[coordinates.length][];

            for(int j = 0; j < coordinates.length; j++){
                Double coords_doub[] = {((double) coordinates[j].x)/multiplicationConstant,
                        ((double) coordinates[j].y)/multiplicationConstant,
                        ((double) coordinates[j].z)/multiplicationConstant};
                coordinatesDouble[j] = coords_doub;
            }

            StringBuilder s = new StringBuilder();

            for(int j = 0; j < coordinates.length; j++){
                s.append(String.format("(%s,%s,%s)", d_format.format(coordinatesDouble[j][0]),
                        d_format.format(coordinatesDouble[j][1]),
                        d_format.format(coordinatesDouble[j][2])));
                if(j != coordinates.length-1){
                    s.append(",");
                }
            }

            pentominoesStrings.add(s.toString());
        }

        super.writeToFile(container_x, container_y, container_z, pentominoProblem.getTotalValue(), subsetString.toString(), pentominoesStrings);
    }
}
