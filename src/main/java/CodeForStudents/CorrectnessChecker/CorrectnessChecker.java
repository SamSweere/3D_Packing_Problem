package CodeForStudents.CorrectnessChecker;

import CodeForStudents.Model.ParcelProblem;
import CodeForStudents.Model.PentominoProblem;

public class CorrectnessChecker {

    public static void main(String[] args) {
        // Get the problem type
        String[] path = args[0].split("\\\\");
        String problem = path[path.length-1].split("_")[0];

        if(problem.equals("pentomino")){
            PentominoProblem pentominoProblem = new PentominoProblem();
            pentominoProblem.loadFromFile(args[0], args[1]);
            pentominoProblem.checkCorrectness();
        }
        else if(problem.equals("parcel")){
            ParcelProblem parcelProblem = new ParcelProblem();
            parcelProblem.loadFromFile(args[0], args[1]);
            parcelProblem.checkCorrectness();
        }
        else{
            throw new IllegalArgumentException(String.format("No valid problem found. " +
                    "Problem has to be 'parcel' or 'pentomino'. Received: '%s'", problem));
        }
    }
}
