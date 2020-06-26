package PackingProblem.Algorithms.CLTRS;

import PackingProblem.Model.Container;
import PackingProblem.Model.Coordinate;
import PackingProblem.Model.Parcel;
import com.sun.javafx.geom.Path2D;

import java.util.ArrayList;
import java.util.Stack;

public class State{
    private static Parcel[] parcel_types;
    private Packing packing; // The current packing
    private int vc; // Volume left unpacked
    private int[] bres; // The amount of free boxes left of parcel_type
    private Stack<ResidualSpace> rs_stack; // Stack of residual spaces
    private ArrayList<Integer> block_placements = new ArrayList<>(); // Arraylist to track the block placements
    private long hashKey;
    private double total_value;

    // Init constructor
    public State(Container container, Parcel[] parcel_types, int[] bres){
        this.packing = new Packing();
        // The first residual space is the container
        ResidualSpace res = new ResidualSpace(new Coordinate(0,0,0),
                container.getX_size(), container.getY_size(), container.getZ_size(), 0);
        this.rs_stack = new Stack<>();
        this.rs_stack.push(res);
        // Add the box types
        this.parcel_types = parcel_types;
        // Add the free boxes
        this.bres = bres.clone();
        // Set the hashkey to zero
        this.hashKey = 0;
        this.total_value = 0;
        // Calculate the free volume
        vc = container.getVolume();
    }

    // Copy constructor
    public State(State state){
        // Made sure everything is cloned
        this.packing = state.getPacking().clone();
        this.vc = state.getVc();
        this.parcel_types = state.getParcel_types();
        this.bres = state.getBres().clone();
        this.rs_stack = (Stack<ResidualSpace>) state.getRs_stack().clone();
        this.block_placements = (ArrayList<Integer>) state.getBlock_placements().clone();
        this.hashKey = state.getHashKey();
        this.total_value = state.getTotal_value();
    }

    // This function will place the block in the top residual space
    public void placeBlockInTopResSpace(Block block, TT tt){
        ResidualSpace res = rs_stack.pop();

        // Place the block, this also updates the locations of the parcels inside it
        block.setLocation(res.getLocation().clone());

        block_placements.add(block.getId()); // Add the id of the placed block

        for(Parcel p:block.getPacking()){
            packing.addParcel(p);
        }

        this.total_value = packing.getTotalValue();

        // Update the hashkey
//        hashKey = tt.updateHash(hashKey, block_placements.size(), block.getId());

        // Remove parcels from the availability
        removeFromBres(block);

        // Calculate the new residual spaces
        generateDaughterResSpaces(block, res);
    }

    public State clone() {
        return new State(this);
    }

    public void greedySolve(ArrayList<Block> bll, TT tt){
//        // Check if the hash state is already calculated, if not do so
//        if(block_placements.size() > 0 && hashKey == 0){
//            hashKey = tt.getHash(this);
//        }

        // TODO: cheaty solution for now
//        hashKey = tt.getHash(this);

//        ArrayList<Long> add_to_tt = new ArrayList<>();

        while(rs_stack.size() > 0) {
            // Check if this state is present in tt
//            TTElement ttElem = tt.checkTT(hashKey);
//            if(ttElem != null){
//                //State is present in TT, set this total value to that value
//                this.total_value = ttElem.value;
////                System.out.println("tt hit!");
//                return;
//            }
//            else{
//                // Add current state to be added to the queue
//                add_to_tt.add(hashKey);
//            }

            // Remove the top residual space
            ResidualSpace res = rs_stack.pop();

            // Get the best fitting block
            ArrayList<Block> best_blocks = generateRsBlocklist(res, bll, 1);

            if(best_blocks.size() == 0){
                // Nothing fits
                continue;
            }

            Block best_block = best_blocks.get(0);

            // Place the block, this also updates the locations of the parcels inside it
            best_block.setLocation(res.getLocation().clone());

            block_placements.add(best_block.getId()); // Add the id of the placed block

            for(Parcel p:best_block.getPacking()){
                packing.addParcel(p);
            }

            this.total_value = packing.getTotalValue();

            // Update the hashkey
//            hashKey = tt.updateHash(hashKey, block_placements.size(), best_block.getId());

            // Remove parcels from the availability
            removeFromBres(best_block);

            // Calculate the new residual spaces
            generateDaughterResSpaces(best_block, res);
        }

//        // Solution complete, add the saved tt hashes to the tt
//        for(long hash:add_to_tt){
//            tt.storeTT(hash, this.total_value);
//        }

    }

    private void removeFromBres(Block block){
        ArrayList<Integer> needed_parcel_ids = block.getNeeded_parcel_ids();
        for(int j = 0; j < needed_parcel_ids.size(); j++){
            int id = needed_parcel_ids.get(j);
            bres[id] -= 1;
        }
    }

    // bll for the possible blocks, n the number of blocks to return
    public ArrayList<Block> generateRsBlocklist(ResidualSpace res, ArrayList<Block> bll, int n){
        ArrayList<Block> bll_rs = new ArrayList<>();

        if(res == null){
            // No more residual space left
            return bll_rs;
        }

        // Find all the boxes that fit
        for(int i = 0; i < bll.size(); i++){
            Block block = bll.get(i);
            if(block.getX_size() <= res.getX_size()
                    && block.getY_size() <= res.getY_size()
                    && block.getZ_size() <= res.getZ_size()) {
                // Block fits in the residual space, check if all the parcels are still available
                int[] bres_copy = bres.clone();

                boolean available = true;

                ArrayList<Integer> needed_parcel_ids = block.getNeeded_parcel_ids();
                for(int j = 0; j < needed_parcel_ids.size(); j++){
                    int id = needed_parcel_ids.get(j);
                    if(bres_copy[id] > 0){
                        bres_copy[id] -= 1;
                    }
                    else{
                        // Not enough left
                        available = false;
                        break;
                    }
                }

                if(available){
                    // save it to te bll_rs, make sure to clone it
                    bll_rs.add(block.clone());
                    if(bll_rs.size() >= n){
                        return bll_rs;
                    }
                }
            }
        }

        return bll_rs;
    }

    private void generateDaughterResSpaces(Block b, ResidualSpace res){
        // Generates the daughter res spaces as specified in the paper and places them in the residual stack
        // First get all the variables we need
        int blx = b.getX_size();
        int bly = b.getY_size();
        int blz = b.getZ_size();

        int rsx = res.getX_size();
        int rsy = res.getY_size();
        int rsz = res.getZ_size();

        int rmx = rsx - blx;
        int rmy = rsy - bly;
        int rmz = rsz - blz;

        // Check if the smallest available parcel has smaller dimensions than one of the potential res spaces
        // There are six possible cases here (see table 2)
        int case_num = 1;
        if(rmx >= rmy && rmy >= rmz){
            case_num = 1;
        }
        else if(rmy >= rmx && rmx >= rmz){
            case_num = 2;
        }
        else if(rmx >= rmz && rmz >= rmy){
            case_num = 3;
        }
        else if(rmz >= rmx && rmx >= rmy){
            case_num = 4;
        }
        else if(rmy >= rmz && rmz >= rmx){
            case_num = 5;
        }
        else if(rmz >= rmy && rmy >= rmx){
            case_num = 6;
        }
        else{
            try {
                throw new Exception("Something went wrong in generateDaughterResSpaces, " +
                        "else case should not be triggered");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        Coordinate drs_x_loc = new Coordinate(b.getLocation().getX() + blx,
                b.getLocation().getY(),
                b.getLocation().getZ());
        int drs_x_x = rmx;
        int drs_x_y = -1; //To be done
        int drs_x_z = -1; //To be done

        Coordinate drs_y_loc = new Coordinate(b.getLocation().getX(),
                b.getLocation().getY() + bly,
                b.getLocation().getZ());
        int drs_y_x = -1; //To be done
        int drs_y_y = rmy;
        int drs_y_z = -1; //To be done

        Coordinate drs_z_loc = new Coordinate(b.getLocation().getX(),
                b.getLocation().getY(),
                b.getLocation().getZ() + blz);
        int drs_z_x = -1; //To be done
        int drs_z_y = -1; //To be done
        int drs_z_z = rmz;

        // drs_x = 0, drs_y = 1, drs_z = 0
        int max_order = -1; //To be done
        int med_order = -1; //To be done
        int min_order = -1; //To be done


        // Apply the cases
        switch (case_num){
            case 1:
                max_order = 0;
                med_order = 1;
                min_order = 2;

                drs_x_y = rsy;
                drs_x_z = rsz;

                drs_y_x = blx;
                drs_y_z = rsz;

                drs_z_x = blx;
                drs_z_y = bly;
                break;
            case 2:
                max_order = 1;
                med_order = 0;
                min_order = 2;

                drs_x_y = bly;
                drs_x_z = rsz;

                drs_y_x = rsx;
                drs_y_z = rsz;

                drs_z_x = blx;
                drs_z_y = bly;
                break;
            case 3:
                max_order = 0;
                med_order = 2;
                min_order = 1;

                drs_x_y = rsy;
                drs_x_z = rsz;

                drs_y_x = blx;
                drs_y_z = blz;

                drs_z_x = blx;
                drs_z_y = rsy;
                break;
            case 4:
                max_order = 2;
                med_order = 0;
                min_order = 1;

                drs_x_y = rsy;
                drs_x_z = blz;

                drs_y_x = blx;
                drs_y_z = blz;

                drs_z_x = rsx;
                drs_z_y = rsy;
                break;
            case 5:
                max_order = 1;
                med_order = 2;
                min_order = 0;

                drs_x_y = bly;
                drs_x_z = blz;

                drs_y_x = rsx;
                drs_y_z = rsz;

                drs_z_x = rsx;
                drs_z_y = bly;
                break;
            case 6:
                max_order = 2;
                med_order = 1;
                min_order = 0;

                drs_x_y = bly;
                drs_x_z = blz;

                drs_y_x = rsx;
                drs_y_z = blz;

                drs_z_x = rsx;
                drs_z_y = rsy;
                break;
        }

        int drs_x_type = 0; // 0 = min, 1 = med, 2 = max
        int drs_y_type = 0;
        int drs_z_type = 0;

        if(min_order == 0){
            drs_x_type = 0;
        }
        else if(min_order == 1){
            drs_y_type = 0;
        }
        else{
            drs_z_type = 0;
        }

        if(med_order == 0){
            drs_x_type = 1;
        }
        else if(med_order == 1){
            drs_y_type = 1;
        }
        else{
            drs_z_type = 1;
        }

        if(max_order == 0){
            drs_x_type = 2;
        }
        else if(max_order == 1){
            drs_y_type = 2;
        }
        else{
            drs_z_type = 2;
        }

        // Create the residual spaces
        ResidualSpace drs_x = new ResidualSpace(drs_x_loc, drs_x_x, drs_x_y, drs_x_z, drs_x_type);
        ResidualSpace drs_y = new ResidualSpace(drs_y_loc, drs_y_x, drs_y_y, drs_y_z, drs_y_type);
        ResidualSpace drs_z = new ResidualSpace(drs_z_loc, drs_z_x, drs_z_y, drs_z_z, drs_z_type);

        boolean add_drs_x = aParcelFitsInRes(drs_x);
        boolean add_drs_y = aParcelFitsInRes(drs_y);
        boolean add_drs_z = aParcelFitsInRes(drs_z);

        // Check if something fits in the med and max res spaces, if not transfer all possible space to the min res space
        if(drs_x_type == 1 || drs_x_type == 2){
            if(!add_drs_x){
                // Nothing fits in x space but it is med or max transfer possible to min state
                if(drs_y_type == 0){
                    drs_y = transfer_space(drs_x, drs_y);
                }
                else{
                    drs_z = transfer_space(drs_x, drs_z);
                }
            }
        }

        if(drs_y_type == 1 || drs_y_type == 2){
            if(!add_drs_y){
                // Nothing fits in x space but it is med or max transfer possible to min state
                if(drs_x_type == 0){
                    drs_x = transfer_space(drs_y, drs_x);
                }
                else{
                    drs_z = transfer_space(drs_y, drs_z);
                }
            }
        }

        if(drs_z_type == 1 || drs_z_type == 2){
            if(!add_drs_z){
                // Nothing fits in x space but it is med or max transfer possible to min state
                if(drs_x_type == 0){
                    drs_x = transfer_space(drs_z, drs_x);
                }
                else{
                    drs_y = transfer_space(drs_z, drs_y);
                }
            }
        }

        // Add the boxes in "min - max - med" order
        // drs_x = 0, drs_y = 1, drs_z = 0
        if(min_order == 0){
            if(add_drs_x){
                rs_stack.add(drs_x);
            }
        }
        else if(min_order == 1){
            if(add_drs_y) {
                rs_stack.add(drs_y);
            }
        }
        else{
            if(add_drs_z) {
                rs_stack.add(drs_z);
            }
        }

        if(med_order == 0){
            if(add_drs_x) {
                rs_stack.add(drs_x);
            }
        }
        else if(med_order == 1){
            if(add_drs_y) {
                rs_stack.add(drs_y);
            }
        }
        else{
            if(add_drs_z) {
                rs_stack.add(drs_z);
            }
        }

        if(max_order == 0){
            if(add_drs_x) {
                rs_stack.add(drs_x);
            }
        }
        else if(max_order == 1){
            if(add_drs_y) {
                rs_stack.add(drs_y);
            }
        }
        else{
            if(add_drs_z) {
                rs_stack.add(drs_z);
            }
        }
    }

    private ResidualSpace transfer_space(ResidualSpace res, ResidualSpace res_min){
        Coordinate loc = res.getLocation();
        Coordinate loc_min = res_min.getLocation();
        // Always only one of the dimensions can be extended, x, y or z

        // Init the parameters with the current res_min to reduce later code
        int new_x = res_min.getX_size();
        int new_y = res_min.getY_size();
        int new_z = res_min.getZ_size();
        Coordinate new_loc = res_min.getLocation();


        if(loc_min.x == loc.x - res_min.getX_size()){
            new_x = res_min.getX_size() + res.getX_size();
            // Keep the current location
        }
        else if(loc_min.x == loc.x + res.getX_size()){
            new_x = res_min.getX_size() + res.getX_size();
            new_loc = new Coordinate(Math.min(loc.x, loc_min.x), loc_min.y, loc_min.z);
        }
        else if(loc_min.y == loc.y - res_min.getY_size()){
            new_y = res_min.getY_size() + res.getY_size();
            // Keep the current location
        }
        else if(loc_min.y == loc.y + res.getY_size()){
            new_y = res_min.getY_size() + res.getY_size();
            new_loc = new Coordinate(loc_min.x, Math.min(loc.y, loc_min.y), loc_min.z);
        }
        else if(loc_min.z == loc.z - res_min.getZ_size()){
            new_z = res_min.getZ_size() + res.getZ_size();
            // Keep the current location
        }
        else if(loc_min.z == loc.z + res.getZ_size()){
            new_z = res_min.getZ_size() + res.getZ_size();
            new_loc = new Coordinate(loc_min.x, loc_min.y, Math.min(loc.z, loc_min.z));
        }
        else{
            try {
                throw new Exception("Something went wrong in generateDaughterResSpaces, " +
                        "transfering med or max state to min failed");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return new ResidualSpace(new_loc, new_x, new_y, new_z, 0);
    }

    private boolean aParcelFitsInRes(ResidualSpace res){
        // Check if any parcel fits in the residual space, start at the smallest volume to be fast
        for(int i = bres.length - 1; i >= 0; i--){
            if(bres[i] > 0){
                if(parcel_types[i].getX_size() <= res.getX_size()
                        && parcel_types[i].getY_size() <= res.getY_size()
                        && parcel_types[i].getZ_size() <= res.getZ_size()) {
                    // Fits
                    return true;
                }
            }
        }
        // At this point nothing fits
        return false;
    }

    public ArrayList<Integer> getBlock_placements() {
        return block_placements;
    }

    public long getHashKey() {
        return hashKey;
    }

    public double getTotal_value() {
        return total_value;
    }

    public Packing getPacking() {
        return packing;
    }

    public int getVc() {
        return vc;
    }

    public Parcel[] getParcel_types() {
        return parcel_types;
    }

    public int[] getBres() {
        return bres;
    }

    public ResidualSpace peekRs_stack(){
        return rs_stack.peek();
    }

    public ResidualSpace popRs_stack(){
        return rs_stack.pop();
    }

    public Stack<ResidualSpace> getRs_stack() {
        // TODO: not sure if this makes a deep copy
        return (Stack<ResidualSpace>) rs_stack.clone();
    }
}
