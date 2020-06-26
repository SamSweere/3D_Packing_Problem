package PackingProblem.Algorithms.CLTRSDLX;

import PackingProblem.Algorithms.CLTRS.CLTRS;
import PackingProblem.Algorithms.DLX.DLX;
import PackingProblem.Algorithms.IParcelSolver;
import PackingProblem.Algorithms.IPentominoSolver;
import PackingProblem.Model.*;
import PackingProblem.Visualization.PackingVisualizer;

import java.util.ArrayList;
import java.util.Collections;

public class CLTRSDLX implements IPentominoSolver {
    private ParcelProblem parcelProblem = new ParcelProblem();

    private int maxDimension = 5;
    int maxDLXSeconds;
    int maxCLTRSSeconds;

    private double upper_limit_value_parc = -1;
    private double upper_limit_value_pent = -1;

    public CLTRSDLX(int maxDLXSeconds, int maxCLTRSSeconds) {
        this.maxDLXSeconds = maxDLXSeconds;
        this.maxCLTRSSeconds = maxCLTRSSeconds;
    }

    @Override
    public void solve(PentominoProblem pentominoProblem) {
        long dlxEndTime = System.nanoTime() + (maxDLXSeconds - 1) * 1000000000L;
        long cltrsEndTime = dlxEndTime + (maxDLXSeconds - 1) * 1000000000L;

        this.parcelProblem.setContainer(pentominoProblem.getContainer());

        ArrayList<Parcel> possibleSubSolutions = new ArrayList<>();
        ArrayList<StartParcel> possSubSolStart = new ArrayList<>();

        double max_x = pentominoProblem.getContainer().getX_size();
        double max_y = pentominoProblem.getContainer().getY_size();
        double max_z = pentominoProblem.getContainer().getZ_size();

        long remainingDLXNanoSeconds = dlxEndTime - System.nanoTime();
        int current_min_dim = 1;
        int current_max_dim = 5; // To track the dimension, start at 5 since this will give the first good solutions

        outer:
        while(remainingDLXNanoSeconds > 0) {
            for (int i = 1; i <= Math.min(max_x, current_max_dim); i++) {
                for (int j = i; j <= Math.min(max_y, current_max_dim); j++) {
                    for (int k = Math.max(j, current_min_dim); k <= Math.min(max_z, current_max_dim); k++) {
                        remainingDLXNanoSeconds = dlxEndTime - System.nanoTime();
                        if (remainingDLXNanoSeconds <= 0) {
                            break outer;
                        }
//                    //TODO: this could be bad for some problems
//                    if(i*j*k % 5 != 0){
//                        // Not perfectly solvable
//                        continue;
//                    }

                        System.out.println("Subcontainer size: " + i + "," + j + "," + k);

                        ArrayList<StartPentomino> pentominoSubset = pentominoProblem.getPentominoSubset();
                        pentominoSubset.sort(Collections.reverseOrder());

                        ArrayList<StartPentomino> currentStartPentominoes = new ArrayList<>();

                        for (int sub_ind = 0; sub_ind < pentominoSubset.size(); sub_ind++) {
                            StartPentomino startPentomino = pentominoSubset.get(sub_ind);

                            remainingDLXNanoSeconds = dlxEndTime - System.nanoTime();
                            if (remainingDLXNanoSeconds <= 0) {
                                System.out.println("Timeout for DLX part!");
                                break outer;
                            }

                            currentStartPentominoes.add(startPentomino);

                            if (sub_ind != pentominoSubset.size() - 1) {
                                if (startPentomino.getValue() == pentominoSubset.get(sub_ind + 1).getValue()) {
                                    // The current value is the same as the next value, do not do a seperate search but instead continue
                                    continue;
                                }
                            }

                            PentominoProblem subProblem = new PentominoProblem();

                            // Devide by two since the pentominoproblem still expects the old format
                            subProblem.setContainer(((double) i) / 2.0, ((double) j) / 2.0, ((double) k) / 2.0);
                            System.out.print("Solving subproblem with shapes: ");
                            for (StartPentomino currentStartPentomino : currentStartPentominoes) {
                                subProblem.addStartPentomino(currentStartPentomino.getShape(), currentStartPentomino.getValue(), currentStartPentomino.getAmount());
                                System.out.print(currentStartPentomino.getShape() + ",");
                            }
                            System.out.println();

                            subProblem.solve(new DLX(remainingDLXNanoSeconds / 1000000000L, false));


                            // Convert the pentominoes to parcel
                            // Convert the found parcels to start parcels (the start parcel class might be obsolete, but this saves refractoring time)
                            ArrayList<Pentomino> solPent = subProblem.getPentominos();

                            //Only add them if there is a solution
                            if (solPent.size() > 0) {
                                Parcel subParc = new Parcel(i, j, k, subProblem.getPentominos());
                                // TODO: implement counter, now everything is max int
                                System.out.println("Added parcel with value density: " + subParc.getValue_density() + " , filled volume: " + subParc.getFilled_volume() + ", perc filled: " + ((double) subParc.getFilled_volume()) / ((double) subParc.getX_size() * subParc.getY_size() * subParc.getZ_size()));
                                possSubSolStart.add(new StartParcel(subParc, 2147483647));

                            }

                            System.out.println("Number of found sub_solutions: " + possSubSolStart.size());
                        }


                    }
                }
            }
            current_max_dim += 1; // Increase the searching dimension
            current_min_dim = current_max_dim; // In order to not have repeating patterns in the loop


            // Check the time for the while loop
            remainingDLXNanoSeconds = dlxEndTime - System.nanoTime();
        }

        maxCLTRSSeconds = (int) ((cltrsEndTime - System.nanoTime()) / 1000000000L);

        //transform them into a parcel problem

        ParcelProblem parcelProblem = new ParcelProblem();

        parcelProblem.setContainer(pentominoProblem.getContainer());
        parcelProblem.setParcelSubset(possSubSolStart);


        //run the parcel problem
        // Create the solver
        IParcelSolver parcelSolver = new CLTRS(maxCLTRSSeconds - 1);
        parcelProblem.solve(parcelSolver);

        ArrayList<Parcel> parcels = parcelProblem.getParcels();
        ArrayList<Pentomino> pentominos = new ArrayList<>();

        int value = 0;
        int volume = 0;

        // Get the pentominos from the parcels and add the coordinates also count the value
        for (Parcel parcel : parcels) {
            Coordinate loc = parcel.getLocation();
            value += parcel.getValue();
            volume += parcel.getFilled_volume();

            for (Pentomino pent : parcel.getP_packing()) {
                Coordinate[] coords = pent.getCoordinates();
                Coordinate[] new_coords = new Coordinate[coords.length];
                for (int i = 0; i < coords.length; i++) {
                    new_coords[i] = new Coordinate(coords[i].x + loc.x, coords[i].y + loc.y, coords[i].z + loc.z);
                }

                Pentomino corr_pent = new Pentomino(pent.getShape(), pent.getValue(), new_coords);
                // Add the corrected pentomino to the arraylist
                pentominos.add(corr_pent);

            }

            // Set the pentominos for the pentominoproblem
            pentominoProblem.setPentominos(pentominos);
            // Set the newly calculated total value
            pentominoProblem.setTotalValue(value);
        }
        //TODO: removethis
        // Save to file
//        parcelProblem.saveToFile("cltrsdlxsub.p");
//        //Visualize the packing
//        PackingVisualizer visualizer = new PackingVisualizer();
//        visualizer.run("parcel","cltrsdlxsub.p");
        this.upper_limit_value_parc = ((CLTRS) parcelSolver).getUpper_limit_value_parc();
        this.upper_limit_value_pent = ((CLTRS) parcelSolver).getUpper_limit_value_pent();
    }

    public double getUpper_limit_value_parc() {
        return upper_limit_value_parc;
    }

    public double getUpper_limit_value_pent() {
        return upper_limit_value_pent;
    }

    public double getTotalValue() {
        return this.parcelProblem.getTotalValue();
    }

}