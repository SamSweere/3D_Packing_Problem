package PackingProblem;

import PackingProblem.Algorithms.CLTRS.CLTRS;
import PackingProblem.Algorithms.CLTRSDLX.CLTRSDLX;
import PackingProblem.Algorithms.DLX.DLX;
import PackingProblem.Algorithms.IParcelSolver;
import PackingProblem.Algorithms.IPentominoSolver;
import PackingProblem.Model.*;
import PackingProblem.Visualization.PackingVisualizer;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
//        runParcelCLTRS(15);
//        runDLX();
        runParcelCLTRSDLX(15);

//        visualizeParcel();
//        visualizePentomino();

    }

    private static void visualizeParcel(){
        //Visualize the packing
        PackingVisualizer visualizer = new PackingVisualizer();
        visualizer.run("parcel", "cltrsdlxsub.p");
    }

    private static void visualizePentomino(){
        //Visualize the packing
        PackingVisualizer visualizer = new PackingVisualizer();
        visualizer.run("pentomino", "cltrsdlx.p");
    }


    private static void runParcelCLTRS(long maxSecondsToRun) {
        ParcelProblem parcelProblem = new ParcelProblem();

        // Simple problem:
//        parcelProblem.setContainer(33, 5, 8);
//        parcelProblem.addStartParcel(2, 2, 4, 5.0, 10000);
//        parcelProblem.addStartParcel(1, 1, 4, 1.0, 10000);

//        // Simple problem 2:
//        parcelProblem.setContainer(33, 5, 8);
//        parcelProblem.addStartParcel(2, 2, 2, 2, 10000);
//        parcelProblem.addStartParcel(2, 3, 6, 10, 10000);

//        // Original problem:
//        parcelProblem.setContainer(33, 5, 8);
//        parcelProblem.addStartParcel(2, 2, 4, 3.0, 10000);
//        parcelProblem.addStartParcel(2, 3, 4, 4.0, 10000);
//        parcelProblem.addStartParcel(3, 3, 3, 5.0, 10000);

        // Volume density the same
//        parcelProblem.setContainer(33, 5, 8);
//        parcelProblem.addStartParcel(2, 2, 4, 16, 10000);
//        parcelProblem.addStartParcel(2, 3, 4, 24, 10000);
//        parcelProblem.addStartParcel(3, 3, 3, 27, 10000);

        // Big container
//        parcelProblem.setContainer(99, 15, 24);
//        parcelProblem.addStartParcel(2, 2, 4, 3, 10000);
//        parcelProblem.addStartParcel(2, 3, 4, 4, 10000);
//        parcelProblem.addStartParcel(3, 3, 3, 5, 10000);

//        // Harder test set
//        parcelProblem.setContainer(17, 11, 17);
//        parcelProblem.addStartParcel(4, 2, 2, 1, 100000);
//        parcelProblem.addStartParcel(4, 3, 2, 2, 10000);
//        parcelProblem.addStartParcel(3, 3, 3, 3, 10000);
//        parcelProblem.addStartParcel(5, 5, 3, 4, 10000);

        // Harder test set same density
//        parcelProblem.setContainer(17, 11, 17);
//        parcelProblem.addStartParcel(4, 2, 2, 16, 100000);
//        parcelProblem.addStartParcel(4, 3, 2, 20, 10000);
//        parcelProblem.addStartParcel(3, 3, 3, 27, 10000);
//        parcelProblem.addStartParcel(5, 5, 3, 75, 10000);

        // Big container using only prime numbers
//        parcelProblem.setContainer(37, 23, 17);
//        parcelProblem.addStartParcel(2, 3, 5, 7, 100000);
//        parcelProblem.addStartParcel(3, 3, 7, 15, 100000);
//        parcelProblem.addStartParcel(5, 7, 13, 115, 100000);

//        // Huge container with anoying dimensions consisting out of prime numbers
        parcelProblem.setContainer(97, 71, 89);
        parcelProblem.addStartParcel(17, 11, 5, 95, 10000);
        parcelProblem.addStartParcel(7, 5, 3, 10, 100000);
        parcelProblem.addStartParcel(13, 7, 1, 9, 10000);
        parcelProblem.addStartParcel(7, 3, 2, 4, 10000);

        // Additional problems
//        parcelProblem.setContainer(33, 5, 8);
//        parcelProblem.addStartParcel(2, 2, 4, 16, 10000);
//        parcelProblem.addStartParcel(2, 3, 4, 24, 10000);
//        parcelProblem.addStartParcel(3, 3, 3, 27, 10000);

        // Test problems
//        parcelProblem.setContainer(16.5, 4.5, 6.0);
//
//        parcelProblem.addStartParcel(4, 4, 4, 5.0, 1);
//        parcelProblem.addStartParcel(1.0, 1.0, 1.0, 4.0, 25);
//        parcelProblem.addStartParcel(1.0, 1.5, 2, 3.0, 40);
//        parcelProblem.addStartParcel(1.0, 4.5, 3.0, 3.0, 8);
//        parcelProblem.addStartParcel(2.0, 1.5, 2.0, 3.0, 32);
//        parcelProblem.addStartParcel(15.0, 1.0, 1.0, 3.0, 16);

        // Harder test set
//        parcelProblem.setContainer(8.5, 5.5, 8.5);
//        parcelProblem.addStartParcel(2, 1, 1, 4.0, 100000);
//        parcelProblem.addStartParcel(1.0, 1.5, 2.0, 4.0, 10000);
//        parcelProblem.addStartParcel(1.5, 1.5, 1.5, 4.0, 10000);
//        parcelProblem.addStartParcel(2.5, 1.5, 2.5, 4.0, 10000);


//        System.out.println(parcelProblem);
        // Create the solver
        IParcelSolver parcelSolver = new CLTRS(maxSecondsToRun);
        parcelProblem.solve(parcelSolver);
//        System.out.println(parcelProblem);
        if (parcelProblem.checkCorrectness()) {
            System.out.println("The packing is correct");
        } else {
            try {
                throw new Exception("Incorrect packing");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        // Save to file
        parcelProblem.saveToFile("cltrs.p");
        //Visualize the packing
        PackingVisualizer visualizer = new PackingVisualizer();
        visualizer.run("parcel", "cltrs.p");
    }

    private static void runParcelCLTRSDLX(long maxSecondsToRun) {
        PentominoProblem pentominoProblem = new PentominoProblem();

        //TODO: in the CLTRSDLX every amount is set to infinite, the amount here is currently useless
        //Original problem
        pentominoProblem.setContainer(33, 5, 8);
        pentominoProblem.addStartPentomino(Shape.L, 3, 999999);
        pentominoProblem.addStartPentomino(Shape.P, 4, 999999);
        pentominoProblem.addStartPentomino(Shape.T, 5, 999999);

        // Create the solver
        IPentominoSolver pentominoSolver = new CLTRSDLX((int) maxSecondsToRun/2, (int) maxSecondsToRun/2);
        pentominoProblem.solve(pentominoSolver);

        if (pentominoProblem.checkCorrectness()) {
            System.out.println("The packing is correct");
        } else {
            try {
                throw new Exception("Incorrect packing");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // Save to file
        pentominoProblem.saveToFile("cltrsdlx.p");
        //Visualize the packing
        PackingVisualizer visualizer = new PackingVisualizer();
        visualizer.run("pentomino", "cltrsdlx.p");
    }

    private static void runDLX() {
        PentominoProblem pentominoProblem = new PentominoProblem();

        // Add all shapes to the subset
        for (Shape value : Shape.values()) {
            pentominoProblem.addStartPentomino(value, 1, 99999999);
        }

        pentominoProblem.setContainer(1.5, 2, 2.5);

        System.out.println("Starting DLX now.");
        long startTime = System.nanoTime();

        pentominoProblem.solve(new DLX(20, true));

        long endTime = System.nanoTime();

        // Get difference of two nanoTime values
        long timeElapsed = endTime - startTime;

        System.out.println("Execution time in nanoseconds  : " + timeElapsed);

        System.out.println("Execution time in milliseconds : " +
                timeElapsed / 1000000);

        System.out.println(pentominoProblem);
        System.out.println(pentominoProblem.getTotalValue());
        pentominoProblem.saveToFile("dlx.p");

        PackingVisualizer visualizer = new PackingVisualizer();
        visualizer.run("pentomino", "dlx.p");
    }
}
