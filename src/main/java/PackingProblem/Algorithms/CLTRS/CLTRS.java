package PackingProblem.Algorithms.CLTRS;

import PackingProblem.Algorithms.IParcelSolver;
import PackingProblem.Model.*;

import java.util.ArrayList;
import java.util.Collections;

public class CLTRS implements IParcelSolver {
    private int max_bl = 10000; //Maximum amount of blocks to be generated
    private final int min_ns = 100; // min_ns indicates the desired minimal number of successors of a partial solution// (or node) throughout the search.
    private final int max_iterations = 9999; // Used for debugging

    final long maxSecondsToRun; // Time limit in seconds per phase

    long latestEndTime;

    private Container container;
    private Parcel[] parcel_types;
    int[] init_bres;
    private ArrayList<Block> bll = new ArrayList<>();
    //    private Packing best_packing = new Packing();
    // Note that these states are global for the class
    private ArrayList<State> stmp_all = new ArrayList<>();
    private State stmp_best; // Best phase state
    private double ptmpbest; // Best completed temporary solution
    private State pbest; // Best completed global solution

    private TT tt; // The transpsition table

    private ParcelProblem parcelProblem;
    private double upper_limit_value_parc = -1;
    private double upper_limit_value_pent = -1;

//    private ArrayList<Parcel> final_packing = new ArrayList<>();


    public CLTRS(long maxSecondsToRun) {
        this.maxSecondsToRun = maxSecondsToRun;
    }

    @Override
    public void solve(ParcelProblem parcelProblem) {
        long startTime = System.nanoTime();
        latestEndTime = startTime + (maxSecondsToRun - 1) * 1000000000L;

        if (max_bl < parcelProblem.getParcelSubset().size()) {
            System.out.println("WARNING: max_bl was smaller than the parcel subset size. " +
                    "Changed max_bl to parcel_subset size");
            max_bl = parcelProblem.getParcelSubset().size();
        }

        // Get the required information
        this.parcelProblem = parcelProblem;
        this.container = parcelProblem.getContainer();


        ArrayList<StartParcel> parcelSubset = parcelProblem.getParcelSubset();

        // Sort the parcelSubset on volume, biggest first
        parcelSubset.sort(Collections.reverseOrder());

        // Tracks how many are left of every parcel type. The use of arrays is more efficient
        init_bres = new int[parcelSubset.size()];
        parcel_types = new Parcel[parcelSubset.size()];

        // Convert the parcelSubset to parcel_types and bres. Note they are already sorted
        for (int i = 0; i < parcelSubset.size(); i++) {
            parcel_types[i] = parcelSubset.get(i).getParcel();
            parcel_types[i].setId(i); // Set the id to the bres index
            init_bres[i] = parcelSubset.get(i).getAmount();
        }

        // Get the maximum value density
        double max_value_dens_parc = 0;
        double max_value_dens_pent = 0;
        for(StartParcel startParcel:parcelProblem.getParcelSubset()) {
            double value = startParcel.getValue();
            double volume = startParcel.getParcel().getFilled_volume();
            ArrayList<Pentomino> ppacking = startParcel.getParcel().getP_packing();
            if(ppacking != null){
                for(Pentomino p:ppacking){
                    double p_dens = p.getValue()/5.0;
                    if(p_dens > max_value_dens_pent){
                        max_value_dens_pent = p_dens;
                    }
                }
            }

            double dens = value/volume;
            if(dens > max_value_dens_parc){
                max_value_dens_parc = dens;
            }
        }

        this.upper_limit_value_parc = container.getVolume()*max_value_dens_parc;
        this.upper_limit_value_pent = container.getVolume()*max_value_dens_pent;

        if(max_value_dens_pent != 0){
            System.out.println("Total value upper limit given pentominoes: " + this.upper_limit_value_pent);
        }
        System.out.println("Total value upper limit cltrs given parcels: " + this.upper_limit_value_parc);


        // First generate the simple blocks
        bll = generateSimpleBlocks(init_bres, parcel_types);
        // Sort the bll on value density (value/volume), biggest first. If the value density is the same take the bigger volume
        bll.sort(Collections.reverseOrder());

        // Create the transpostion table
        int max_depth = container.getVolume(); //TODO: This is a guessing estimation
        this.tt = null; //new TT(max_depth, bll.size());




//        //TODO: for debugging
//        for(Block block:bll){
//            if(block.getValue_density() != 1.0){
//                System.out.println("Not density 1!!: " + block);
//            }
//        }


        // Start the search loop
        int search_effort = 1; // internal parameter of the search efforts
        int iteration = 0;
        while (System.nanoTime() < latestEndTime && iteration < max_iterations) {


            System.out.println("Iteration: " + iteration);
            System.out.println("Search effort: " + search_effort);

            // Create the initial state
            State state = new State(container, parcel_types, init_bres);

            // Calculate the todal depth td
            int td = calculate_td(search_effort);
            System.out.println("td: " + td);

            ArrayList<ArrayList<Integer>> partitions = calculate_partitions(td);

//            //TODO: this can be used to only get normal search trees
//            ArrayList<Integer> backup = partitions.get(0);
//            partitions = new ArrayList<>();
//            partitions.add(backup);

            // Start the loop to chain all the basic searches
            outer:
            while(!state.getRs_stack().empty() && System.nanoTime() < latestEndTime) {

                // Reset the variables to track the best search state outcome
                stmp_best = null;
                ptmpbest = 0;

                for (ArrayList<Integer> partition : partitions) {
                    if(pbest != null) {
                        if (pbest.getPacking().getTotalValue() == upper_limit_value_parc) {
                            break outer; //best possible value reached
                        }
                    }

                    int l = partition.get(0);
//                    System.out.println(partition);
                    // Create the local stmp and ptmp
                    // Create the local stmp and ptmp
                    State[] stmp_best_loc = new State[l];
                    double[] ptmpbest_loc = new double[l];

                    for (int l_iter = 0; l_iter < l; l_iter++) {
                        int d = partition.get(l_iter+1); // Note 0 index is l itself
                        // Start the tree search
                        basicSearch(state, d, search_effort, l_iter, stmp_best_loc, ptmpbest_loc);

                        if (stmp_best == null) {
                            // No exists yet, take the first
                            stmp_best = stmp_best_loc[0];
                            ptmpbest = ptmpbest_loc[0];
                        } else {
                            for(int i = 0; i < l_iter; i++){
                                // Go through every found stage in every basic search phase. Pick the best stmp state
                                // as the state that has the highest completed value
                                if (ptmpbest_loc[i] > ptmpbest) {
                                    // New best solution
                                    ptmpbest = ptmpbest_loc[i];
                                    stmp_best = stmp_best_loc[i];
                                }
                            }

                        }

                        // Update the state to the next best found stmp_loc
                        state = stmp_best_loc[l_iter].clone();
                    }
                }

                // Update the state to the next best
                state = stmp_best.clone();
            }
            // Double the search effort for the next iteration
            search_effort = search_effort * 2;
            iteration++;
        }

        System.out.println("Best found packing has packed volume: " + pbest.getPacking().getFilled_volume());
        System.out.println("Best found packing has value: " + pbest.getPacking().getTotalValue());


        if(max_value_dens_pent != 0){
            System.out.println("Total value upper limit given pentominoes: " + this.upper_limit_value_pent);
        }
        System.out.println("Upper limit value cltrs given input parcels (not taking count into account): " + upper_limit_value_parc);

        //This solver assumed that coordinate 0, 0, 0 was bottom top left but this is not the 0
        this.parcelProblem.setParcels(pbest.getPacking().getPacking());
        this.parcelProblem.setTotalValue(pbest.getPacking().getTotalValue());
    }

    private int calculate_td(int search_effort) {
        // Calculate the search depth : td
        int d = (int) (Math.log(search_effort) / Math.log(min_ns));
        d = Math.max(0, d);
        int td = Math.max(1, d);
        return td;
    }

    private ArrayList<ArrayList<Integer>> calculate_partitions(int td) {
        ArrayList<ArrayList<Integer>> partitions = new ArrayList<>();

        for (int l = 1; l <= td; l++) {
            ArrayList<Integer> prev = new ArrayList<>();
            prev.add(l);
            calc_sub_parts_given_l(partitions, (ArrayList<Integer>) prev.clone(), l, td);
        }
        return partitions;
    }

    private void calc_sub_parts_given_l(ArrayList<ArrayList<Integer>> partitions, ArrayList<Integer> prev, int l, int sum) {
        if (sum == 0) {
            partitions.add(prev);
            return;
        } else if (sum < 0) {
            try {
                throw new Exception("sub part calculation, negative sum");
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
        }

        int biggest_pos_num = sum - l + 1;
        if (l == 1) {
            // Last part, only add the biggest possible to complete the sum
            prev.add(biggest_pos_num);
            calc_sub_parts_given_l(partitions, prev, l - 1, sum - biggest_pos_num);
        } else {
            // Do every possible number
            for (int i = biggest_pos_num; i > 0; i--) {
                //Clone the prev
                ArrayList<Integer> new_prev = (ArrayList<Integer>) prev.clone();

                new_prev.add(i);
                calc_sub_parts_given_l(partitions, new_prev, l - 1, sum - i);
            }
        }
    }

    private void basicSearch(State state, int d, int search_effort, int l_iter, State[] stmp_best_loc, double[] ptmpbest_loc) {
//        System.out.println("Depth: " + d);
//
//        if (System.nanoTime() > latestEndTime) return;

        // Calculate the number of successors per state: ns
        // We need to do precision rounding since the Math.pow method is not exact
        int precision = 10;
        double scale = Math.pow(10, precision);
        double sqrt_d_sd = Math.round(Math.pow(search_effort, 1 / ((double) d)) * scale) / scale;
        int q = Math.max(0, (int) sqrt_d_sd);
        int ns = Math.max(min_ns, q);

        // Set the initial stmp_best to itself
        stmp_best_loc[l_iter] = new State(state);
        // Reset the completed solution by putting itself in it
        ptmpbest_loc[l_iter] = state.getPacking().getTotalValue();

        // Recursively do the search steps
        recurisveSearchStep(state, d, ns, l_iter, stmp_best_loc, ptmpbest_loc);
    }

    private void checkIfPackingIsBetter(State state, State stmp, int l_iter, State[] stmp_best_loc, double[] ptmpbest_loc) {
        // If the packing is better update it
        double s_val = state.getPacking().getTotalValue();
        double pbest_val;
        if (pbest == null) {
            // If no best solution is found yet make it empty
            pbest_val = 0;
        } else {
            pbest_val = pbest.getPacking().getTotalValue();
        }

        double ptmpbest_val = ptmpbest_loc[l_iter];

        if (s_val > ptmpbest_val) {
            // Solution better than previous temp solution update them
            stmp_best_loc[l_iter] = stmp.clone();
            ptmpbest_loc[l_iter] = s_val;
        }

        if (s_val > pbest_val) {
            // The packing is the best we have encountered globally thus far, update the pbest
            pbest = new State(state);
            System.out.println("New best packing, total value: " + pbest.getPacking().getTotalValue());
            System.out.println("New best packing, total filled volume: " + pbest.getPacking().getFilled_volume());
        }
        return;
    }

    private void recurisveSearchStep(State state, int depth_rem, int ns, int l_iter, State[] stmp_best_loc, double[] ptmpbest_loc) {
        if (depth_rem == 0 || ns == 1 || state.getRs_stack().size() == 0) {
            // Make a backup of the state for stmp
            State stmp = new State(state);
            // This is a greedy solve, call that one instead because it is more efficient
            state.greedySolve(bll, tt);
            checkIfPackingIsBetter(state, stmp, l_iter, stmp_best_loc, ptmpbest_loc);
//            return new State[] {state, stmp};
            return;
        }

        // Get the top residual space
        ResidualSpace res = state.peekRs_stack();

        // Get blocks that fit, the function will return the top ns results
        ArrayList<Block> bll_rs = state.generateRsBlocklist(res, bll, ns);

        if (bll_rs.size() == 0) {
            // No blocks fit, pop the res state
            State next_state = new State(state);
            next_state.popRs_stack();

            // Recursively call the next search
            recurisveSearchStep(next_state, depth_rem - 1, ns, l_iter, stmp_best_loc, ptmpbest_loc);
//            basicSearch(next_state, d+1, search_effort);
        } else {
            // Create leaf nodes by placing the blocks
            for (int l = 0; l < bll_rs.size(); l++) {
                State next_state = new State(state);
                next_state.placeBlockInTopResSpace(bll_rs.get(l), tt);

                // Recursively call the next search depth
                recurisveSearchStep(next_state, depth_rem - 1, ns, l_iter, stmp_best_loc, ptmpbest_loc);
//                basicSearch(next_state, d+1, search_effort);
            }
        }

//        return new State[] {null, null};
    }

    private ArrayList<Block> generateSimpleBlocks(int[] bres, Parcel[] parcel_types) {
        ArrayList<Block> bll = new ArrayList<>();

        ArrayList<Parcel> parcel_t_w_r = new ArrayList<>();
        // Rotate the parcel_types
        for (Parcel parcel : parcel_types) {
            // Get all the rotations
            ArrayList<Parcel> rotations = parcel.get_rotations();
            for (Parcel p_rot : rotations) {
                // Add it to the total arraylist, since we add them in order the new arraylist will also be sorted on volume
                parcel_t_w_r.add(p_rot);
            }
        }

        // First get a limit amount of permutations for the x, y and z number of blocks
        //TODO: Limit is set to max_bl*10, this is not an upper boundary, could create problems
        ArrayList<int[]> permutations = generatePermutations(max_bl * 10);

        int id_counter = 0;

        // Loop over all permutations until max br is reached, the first permutation is only the block
        for (int[] perm : permutations) {
            // Loop over all possible parcel types
            for (int i = 0; i < parcel_t_w_r.size(); i++) {
                Parcel parcel = parcel_t_w_r.get(i);
                // Check if there are enough of this parcel type
                int numNeeded = perm[0] * perm[1] * perm[2];
                if (bres[parcel_t_w_r.get(i).getId()] < numNeeded) {
                    // Not enough parcels of this type, go to next
                    continue;
                }

                // Check if the permutation for this box fits in the container
                if (perm[0] * parcel.getX_size() > container.getX_size() ||
                        perm[1] * parcel.getY_size() > container.getY_size() ||
                        perm[2] * parcel.getZ_size() > container.getZ_size()) {
                    // Does not fit, go to next
                    continue;
                }

                // Make the block packing
                ArrayList<Parcel> blockPacking = new ArrayList<>();
                for (int x = 0; x < perm[0]; x++) {
                    for (int y = 0; y < perm[1]; y++) {
                        for (int z = 0; z < perm[2]; z++) {
                            blockPacking.add(new Parcel(parcel,
                                    new Coordinate(x * parcel.getX_size(),
                                            y * parcel.getY_size(),
                                            z * parcel.getZ_size())));
                        }
                    }
                }

                // Create the new Block
                bll.add(new Block(blockPacking, id_counter));
                id_counter++;

                // Check if we hit the limit
                if (bll.size() >= max_bl) {
                    return bll;
                }
            }
        }
        return bll;
    }

    private ArrayList<int[]> generatePermutations(int limit) {
        int[] start = {1, 1, 1};
        ArrayList<int[]> permutations = new ArrayList<>();
        permutations.add(start);

//        for(int i = 2; i <= limit; i++){
        int i = 2;
        while (permutations.size() < limit) {
//            arr = new int[]{1, 1, i};
//            permutations.add(arr);
            for (int j = 1; j <= i; j++) {
                for (int k = 1; k <= j; k++) {
                    int[] arr = new int[]{k, j, i};

                    // Do all the heap permutations without repetitions
                    ArrayList<int[]> newPerm = heapPermutation(arr, 3);
                    for (int index = 0; index < newPerm.size(); index++) {
                        permutations.add(newPerm.get(index));
                        if (permutations.size() >= limit) {
                            return permutations;
                        }
                    }
                }
            }
            i += 1;
        }
        return permutations;
    }

    private void swap(int[] input, int a, int b) {
        int tmp = input[a];
        input[a] = input[b];
        input[b] = tmp;
    }

    private boolean arrContains(ArrayList<int[]> arrList, int[] arr) {
        boolean contains = false;
        for (int i = 0; i < arrList.size(); i++) {
            int[] prevArr = arrList.get(i);
            if (prevArr[0] == arr[0] && prevArr[1] == arr[1] && prevArr[2] == arr[2]) {
                contains = true;
            }
        }

        return contains;
    }

    //Generating permutation using Heap Algorithm
    private ArrayList<int[]> heapPermutation(int a[], int n) {
        ArrayList<int[]> permutations = new ArrayList<>();

        int[] indexes = new int[n];
        for (int i = 0; i < n; i++) {
            indexes[i] = 0;
        }

        int[] arr = new int[]{a[0], a[1], a[2]};

        // Check for repetitions
        if (!arrContains(permutations, arr)) {
            permutations.add(arr);
//            System.out.println(String.format("%d %d %d",arr[0],arr[1],arr[2]));
        }

        int i = 0;
        while (i < n) {
            if (indexes[i] < i) {
                swap(a, i % 2 == 0 ? 0 : indexes[i], i);
                int[] arr2 = new int[]{a[0], a[1], a[2]};
                // Check for repetitions
                if (!arrContains(permutations, arr2)) {
                    permutations.add(arr2);
//                    System.out.println(String.format("%d %d %d",arr2[0],arr2[1],arr2[2]));
                }
                indexes[i]++;
                i = 0;
            } else {
                indexes[i] = 0;
                i++;
            }
        }

        return permutations;
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
