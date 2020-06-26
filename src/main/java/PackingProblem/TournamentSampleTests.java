package PackingProblem;

import PackingProblem.Algorithms.CLTRS.CLTRS;
import PackingProblem.Algorithms.CLTRSDLX.CLTRSDLX;
import PackingProblem.Algorithms.DLX.DLX;
import PackingProblem.Algorithms.IParcelSolver;
import PackingProblem.Algorithms.IPentominoSolver;
import PackingProblem.Model.ParcelProblem;
import PackingProblem.Model.PentominoProblem;
import PackingProblem.Model.Shape;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class TournamentSampleTests {


    public static void main(String[] args) throws IOException {
//        runParcelCLTRS(60);
//        runParcelCLTRSDLX(60);

        runParcelCLTRS(120);
//        runParcelCLTRSDLX(120);
    }

    private static void runParcelCLTRS(long maxSecondsToRun) throws IOException {
        String timeStr = Long.toString(maxSecondsToRun);
        String test_dir = "tests/cltrs_" + timeStr;

        String test_dir_full = "Solutions/ParcelProblem/" + test_dir;
        new File(test_dir_full).mkdirs(); //Create the directory

        File file = new File(test_dir_full + "/" + "testParcelCLTRS_rt_" + timeStr + ".csv");
        FileWriter fw = new FileWriter(file);

        ArrayList<ParcelProblem> parcelProblems = new ArrayList<>();

        ParcelProblem parcelProblem = new ParcelProblem();

        parcelProblem.setContainer(33, 5, 8);
        parcelProblem.addStartParcel(2, 2, 4, 5, 10000);
        parcelProblem.addStartParcel(1, 1, 4, 1, 10000);

        parcelProblems.add(parcelProblem);
        parcelProblem = new ParcelProblem();

        parcelProblem.setContainer(33, 5, 8);
        parcelProblem.addStartParcel(2, 2, 2, 2, 10000);
        parcelProblem.addStartParcel(2, 3, 5, 10, 10000);

        parcelProblems.add(parcelProblem);
        parcelProblem = new ParcelProblem();

        parcelProblem.setContainer(33, 5, 8);
        parcelProblem.addStartParcel(2, 2, 4, 3, 10000);
        parcelProblem.addStartParcel(2, 3, 4, 4, 10000);
        parcelProblem.addStartParcel(3, 3, 3, 5, 10000);

        parcelProblems.add(parcelProblem);
        parcelProblem = new ParcelProblem();

        parcelProblem.setContainer(33, 5, 8);
        parcelProblem.addStartParcel(2, 2, 4, 16, 10000);
        parcelProblem.addStartParcel(2, 3, 2, 24, 10000);
        parcelProblem.addStartParcel(3, 3, 3, 27, 10000);

        parcelProblems.add(parcelProblem);
        parcelProblem = new ParcelProblem();

        parcelProblem.setContainer(99, 15, 24);
        parcelProblem.addStartParcel(2, 2, 4, 3, 10000);
        parcelProblem.addStartParcel(2, 3, 2, 4, 10000);
        parcelProblem.addStartParcel(3, 3, 3, 5, 10000);

        parcelProblems.add(parcelProblem);
        parcelProblem = new ParcelProblem();

        parcelProblem.setContainer(37, 23, 17);
        parcelProblem.addStartParcel(2, 3, 5, 7, 10000);
        parcelProblem.addStartParcel(3, 3, 7, 15, 10000);
        parcelProblem.addStartParcel(5, 7, 13, 115, 10000);

        parcelProblems.add(parcelProblem);
        parcelProblem = new ParcelProblem();

        parcelProblem.setContainer(17, 11, 27);
        parcelProblem.addStartParcel(4, 2, 2, 1, 10000);
        parcelProblem.addStartParcel(4, 3, 2, 2, 10000);
        parcelProblem.addStartParcel(3, 3, 3, 3, 10000);
        parcelProblem.addStartParcel(5, 5, 3, 4, 10000);

        parcelProblems.add(parcelProblem);
        parcelProblem = new ParcelProblem();

        parcelProblem.setContainer(17, 11, 27);
        parcelProblem.addStartParcel(4, 2, 2, 16, 10000);
        parcelProblem.addStartParcel(4, 3, 2, 20, 10000);
        parcelProblem.addStartParcel(3, 3, 3, 27, 10000);
        parcelProblem.addStartParcel(5, 5, 3, 75, 10000);

        parcelProblems.add(parcelProblem);
        parcelProblem = new ParcelProblem();

        parcelProblem.setContainer(97, 71, 89);
        parcelProblem.addStartParcel(7, 3, 2, 4, 10000);
        parcelProblem.addStartParcel(13, 7, 1, 9, 10000);
        parcelProblem.addStartParcel(7, 5, 3, 10, 10000);
        parcelProblem.addStartParcel(17, 11, 5, 95, 10000);

        parcelProblems.add(parcelProblem);

        BufferedWriter bw = new BufferedWriter(fw);
        bw.write("ProbNum;UpperLimit_parc;Value");
        bw.newLine();
        for (int i = 0; i < parcelProblems.size(); i++){
            System.out.println("---------------------------------");
            System.out.println("Running problem number: " + i);
            System.out.println("---------------------------------");
            IParcelSolver parcelSolver = new CLTRS(maxSecondsToRun);
            parcelProblems.get(i).solve(parcelSolver);
            double totalValue = parcelSolver.getTotalValue();

            if (parcelProblems.get(i).checkCorrectness()) {
                System.out.println("The packing is correct");
            } else {
                try {
                    throw new Exception("Incorrect packing");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            bw.write((i+1) + ";" + ((CLTRS) parcelSolver).getUpper_limit_value_parc() + ";"+ totalValue);
            bw.newLine();

            parcelProblems.get(i).saveToFile(test_dir + "/" + "prob_num_" + (i+1) + ".p");
        }
        bw.close();
        fw.close();
    }

    private static void runParcelCLTRSDLX(long maxSecondsToRun) throws IOException {
        // Multiplication constant must be set, because we always double the size of the Container to be able to work with 0.5 units
        // So the atomic cubes of a pentomino have a size of 0.5x0.5x0.5
        double multiplicationConstant = 0.5;
        String timeStr = Long.toString(maxSecondsToRun);
        String test_dir = "tests/cltrsdlx_" + timeStr;

        String test_dir_full = "Solutions/PentominoeProblem/" + test_dir;
        new File(test_dir_full).mkdirs(); //Create the directory

        File file = new File(test_dir_full + "/" + "testCLTRSDLX_rt_" + timeStr + ".csv");
        FileWriter fw = new FileWriter(file);

        ArrayList<PentominoProblem> pentominoProblems = new ArrayList<>();

        PentominoProblem pentominoProblem = new PentominoProblem();
        pentominoProblem.setContainer(33*multiplicationConstant, 5*multiplicationConstant, 8*multiplicationConstant);
        pentominoProblem.addStartPentomino(Shape.L, 1, 10000);
        pentominoProblems.add(pentominoProblem);

        pentominoProblem = new PentominoProblem();
        pentominoProblem.setContainer(33*multiplicationConstant, 5*multiplicationConstant, 8*multiplicationConstant);
        pentominoProblem.addStartPentomino(Shape.P, 1, 10000);
        pentominoProblems.add(pentominoProblem);

        pentominoProblem = new PentominoProblem();
        pentominoProblem.setContainer(33*multiplicationConstant, 5*multiplicationConstant, 8*multiplicationConstant);
        pentominoProblem.addStartPentomino(Shape.T, 1, 10000);
        pentominoProblems.add(pentominoProblem);

        pentominoProblem = new PentominoProblem();
        pentominoProblem.setContainer(33*multiplicationConstant, 5*multiplicationConstant, 8*multiplicationConstant);
        pentominoProblem.addStartPentomino(Shape.L, 1, 10000);
        pentominoProblem.addStartPentomino(Shape.P, 1, 10000);
        pentominoProblem.addStartPentomino(Shape.T, 1, 10000);
        pentominoProblems.add(pentominoProblem);

        pentominoProblem = new PentominoProblem();
        pentominoProblem.setContainer(33*multiplicationConstant, 5*multiplicationConstant, 8*multiplicationConstant);
        pentominoProblem.addStartPentomino(Shape.L, 3, 10000);
        pentominoProblem.addStartPentomino(Shape.P, 4, 10000);
        pentominoProblem.addStartPentomino(Shape.T, 5, 10000);
        pentominoProblems.add(pentominoProblem);

        pentominoProblem = new PentominoProblem();
        pentominoProblem.setContainer(33*multiplicationConstant, 5*multiplicationConstant, 8*multiplicationConstant);
        pentominoProblem.addStartPentomino(Shape.L, 1, 10000);
        pentominoProblem.addStartPentomino(Shape.P, 1, 10000);
        pentominoProblem.addStartPentomino(Shape.T, 10, 10000);
        pentominoProblems.add(pentominoProblem);

        pentominoProblem = new PentominoProblem();
        pentominoProblem.setContainer(99*multiplicationConstant, 15*multiplicationConstant, 24*multiplicationConstant);
        pentominoProblem.addStartPentomino(Shape.L, 1, 10000);
        pentominoProblem.addStartPentomino(Shape.P, 1, 10000);
        pentominoProblem.addStartPentomino(Shape.T, 1, 10000);
        pentominoProblems.add(pentominoProblem);

        pentominoProblem = new PentominoProblem();
        pentominoProblem.setContainer(33*multiplicationConstant, 5*multiplicationConstant, 8*multiplicationConstant);
        pentominoProblem.addStartPentomino(Shape.F, 1, 10000);
        pentominoProblem.addStartPentomino(Shape.W, 1, 10000);
        pentominoProblem.addStartPentomino(Shape.Z, 1, 10000);
        pentominoProblems.add(pentominoProblem);

        pentominoProblem = new PentominoProblem();
        pentominoProblem.setContainer(33*multiplicationConstant, 5*multiplicationConstant, 8*multiplicationConstant);
        for (Shape value : Shape.values()) {
            pentominoProblem.addStartPentomino(value, 1, 10000);
        }
        pentominoProblems.add(pentominoProblem);

        pentominoProblem = new PentominoProblem();
        pentominoProblem.setContainer(33*multiplicationConstant, 5*multiplicationConstant, 8*multiplicationConstant);
        pentominoProblem.addStartPentomino(Shape.F, 3, 10000);
        pentominoProblem.addStartPentomino(Shape.I, 1, 10000);
        pentominoProblem.addStartPentomino(Shape.L, 1, 10000);
        pentominoProblem.addStartPentomino(Shape.P, 1, 10000);
        pentominoProblem.addStartPentomino(Shape.N, 2, 10000);
        pentominoProblem.addStartPentomino(Shape.T, 2, 10000);
        pentominoProblem.addStartPentomino(Shape.U, 1, 10000);
        pentominoProblem.addStartPentomino(Shape.V, 2, 10000);
        pentominoProblem.addStartPentomino(Shape.W, 3, 10000);
        pentominoProblem.addStartPentomino(Shape.X, 2, 10000);
        pentominoProblem.addStartPentomino(Shape.Y, 3, 10000);
        pentominoProblem.addStartPentomino(Shape.Z, 3, 10000);
        pentominoProblems.add(pentominoProblem);

        pentominoProblem = new PentominoProblem();
        pentominoProblem.setContainer(99*multiplicationConstant, 15*multiplicationConstant, 24*multiplicationConstant);
        pentominoProblem.addStartPentomino(Shape.F, 3, 10000);
        pentominoProblem.addStartPentomino(Shape.I, 1, 10000);
        pentominoProblem.addStartPentomino(Shape.L, 1, 10000);
        pentominoProblem.addStartPentomino(Shape.P, 1, 10000);
        pentominoProblem.addStartPentomino(Shape.N, 2, 10000);
        pentominoProblem.addStartPentomino(Shape.T, 2, 10000);
        pentominoProblem.addStartPentomino(Shape.U, 1, 10000);
        pentominoProblem.addStartPentomino(Shape.V, 2, 10000);
        pentominoProblem.addStartPentomino(Shape.W, 3, 10000);
        pentominoProblem.addStartPentomino(Shape.X, 2, 10000);
        pentominoProblem.addStartPentomino(Shape.Y, 3, 10000);
        pentominoProblem.addStartPentomino(Shape.Z, 3, 10000);
        pentominoProblems.add(pentominoProblem);

        // Create the solver
        BufferedWriter bw = new BufferedWriter(fw);
        bw.write("ProbNum;UpperLimit_parc;UpperLimit_pent;Value");
        bw.newLine();
        for (int i = 0; i < pentominoProblems.size(); i++){
            System.out.println("---------------------------------");
            System.out.println("Running problem number: " + i);
            System.out.println("---------------------------------");
            IPentominoSolver pentominoSolver = new CLTRSDLX((int) maxSecondsToRun/2, (int) maxSecondsToRun/2);
            pentominoProblems.get(i).solve(pentominoSolver);
            double totalValue = pentominoProblems.get(i).getTotalValue();

            if (pentominoProblems.get(i).checkCorrectness()) {
                System.out.println("The packing is correct");
            } else {
                try {
                    throw new Exception("Incorrect packing");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            bw.write((i+1) + ";" + ((CLTRSDLX) pentominoSolver).getUpper_limit_value_parc() + ";"+
                    ((CLTRSDLX) pentominoSolver).getUpper_limit_value_pent() + ";" + totalValue);
            bw.newLine();

            pentominoProblems.get(i).saveToFile(test_dir + "/" + "prob_num_" + (i+1) + ".p");
        }
        bw.close();
        fw.close();
    }
}
