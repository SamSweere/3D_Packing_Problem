package TournamentSuite.Code.StudentsCodeExample;

import java.util.ArrayList;

public class StudentsCodeExampleMain {
    public static void main(String[] args) throws InterruptedException {
        String[] path = args[0].split("\\\\");
        String problem = path[path.length-1].split("_")[0];

        if(problem.equals("pentomino")){
            PentominoProblem pentominoProblem = PentominoInput.loadFromFile(args[0]);
            Container container = pentominoProblem.getContainer();
            for(int x = 0; x< container.getX_size(); x++) {
                for (int y = 0; y < container.getY_size(); y++) {
                    for (int z = 0; z < container.getZ_size(); z++) {
                        for (Pentomino pentomino : pentominoProblem.getPentominoSubset()) {
                            ArrayList<Coordinate[]> possibleCoordinates = Pentomino.calculatePossibleCoordinates(new Coordinate(x, y, z), pentomino.getShape(), container);

                            for(int index = 0; index < possibleCoordinates.size(); index++){
                                Pentomino pentominoToSet = new Pentomino(pentomino.getShape(), pentomino.getValue());
                                pentominoToSet.setCoordinates(possibleCoordinates.get(index));
                                if (pentominoProblem.isPentominoSetable(pentominoToSet)) {
                                    pentominoProblem.setPentomino(pentominoToSet);
                                }
                            }
                        }
                    }
                }
            }
            PentominoOutput.writeToFile(pentominoProblem, args[1]);
        }
        else if(problem.equals("parcel")){
            ParcelProblem parcelProblem = ParcelInput.loadFromFile(args[0]);
            Container container = parcelProblem.getContainer();
            for(int x = 0; x< container.getX_size(); x++) {
                for (int y = 0; y < container.getY_size(); y++) {
                    for (int z = 0; z < container.getZ_size(); z++) {
                        for (Parcel parcel : parcelProblem.getParcelSubset()) {
                            Parcel parcelToSet = new Parcel(new Coordinate(x, y, z), parcel.getX_size(), parcel.getY_size(), parcel.getZ_size());
                            if (parcelProblem.isParcelSetable(parcelToSet)) {
                                parcelProblem.setParcel(parcelToSet);
                            }
                        }
                    }
                }
            }
            ParcelOutput.writeToFile(parcelProblem, args[1]);
        }
        else{
            throw new IllegalArgumentException(String.format("No valid problem found. " +
                    "Problem has to be 'parcel' or 'pentomino'. Received: '%s'", problem));
        }
    }
}
