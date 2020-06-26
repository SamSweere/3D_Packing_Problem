package PackingProblem.Algorithms.CLTRS;

import PackingProblem.Model.Coordinate;
import PackingProblem.Model.Parcel;
import PackingProblem.Model.StartParcel;

import java.util.ArrayList;

public class Block implements Comparable<Block>{
    private final int x_size;
    private final int y_size;
    private final int z_size;
    private ArrayList<Parcel> packing = new ArrayList<>();
    private final int filled_volume;
    private final double totalValue;
    private final double value_density;
    private final int id;
    private ArrayList<Integer> needed_parcel_ids = new ArrayList<>();
    private Coordinate location = new Coordinate(0,0,0);


    public Block(ArrayList<Parcel> parcels, int id){
        // This should work for all packings of parcels
        this.packing = parcels;
        this.id = id;

        int max_x = 0;
        int max_y = 0;
        int max_z = 0;
        int total_filled_volume = 0;
        int summed_value = 0;

        // Get the necessary values
        for(Parcel parcel: parcels){
            int parcelMaxX = parcel.getLocation().getX() + parcel.getX_size();
            if(parcelMaxX > max_x){
                max_x = parcelMaxX;
            }
            int parcelMaxY = parcel.getLocation().getY() + parcel.getY_size();
            if(parcelMaxY > max_y){
                max_y = parcelMaxY;
            }
            int parcelMaxZ = parcel.getLocation().getZ() + parcel.getZ_size();
            if(parcelMaxZ > max_z){
                max_z = parcelMaxZ;
            }



            total_filled_volume += parcel.getFilled_volume();
            summed_value += parcel.getValue();
            // Add the parcel id to the needed_parcel_ids
            needed_parcel_ids.add(parcel.getId());
        }
        this.x_size = max_x;
        this.y_size = max_y;
        this.z_size = max_z;

        this.filled_volume = total_filled_volume;
        this.totalValue = summed_value;
        this.value_density = summed_value/((double) total_filled_volume);
    }

    public Block(int x_size, int y_size, int z_size, ArrayList<Parcel> packing, int filled_volume, double totalValue,
                 double value_density, ArrayList<Integer> needed_parcel_ids, Coordinate location, int id){
        this.x_size = x_size;
        this.y_size = y_size;
        this.z_size = z_size;
        this.packing = packing;
        this.filled_volume = filled_volume;
        this.totalValue = totalValue;
        this.value_density = value_density;
        this.needed_parcel_ids = needed_parcel_ids;
        this.location = location;
        this.id = id;
    }

    private ArrayList<Parcel> deepClonePacking(){
        ArrayList<Parcel> clone = new ArrayList<>();
        for(Parcel p:packing){
            clone.add(p.clone());
        }
        return clone;
    }

    public Block clone(){
        return new Block(x_size, y_size, z_size, deepClonePacking(), filled_volume, totalValue, value_density,
                (ArrayList<Integer>) needed_parcel_ids.clone(), location.clone(), id);
    }

    public void setLocation(Coordinate location) {
        this.location = location.clone();
        // Update the locations of the packing
        for(int i = 0; i < packing.size(); i++){
            Parcel p = packing.get(i);
            int loc_x = location.getX() + p.getLocation().getX();
            int loc_y = location.getY() + p.getLocation().getY();
            int loc_z = location.getZ() + p.getLocation().getZ();

            p.setLocation(new Coordinate(loc_x, loc_y, loc_z));

            packing.set(i, p);
        }
    }

    public Coordinate getLocation() {
        return location;
    }

    public int getId() {
        return id;
    }

    public int getX_size() {
        return x_size;
    }

    public int getY_size() {
        return y_size;
    }

    public int getZ_size() {
        return z_size;
    }

    public ArrayList<Parcel> getPacking() {
        return (ArrayList<Parcel>) packing.clone();
    }

    public ArrayList<Integer> getNeeded_parcel_ids() {
        return needed_parcel_ids;
    }

    public int getFilled_volume() {
        return filled_volume;
    }

    public double getTotalValue() {
        return totalValue;
    }

    public double getValue_density() {
        return value_density;
    }

// This is the old one based on volume
//    @Override
//    public int compareTo(Block block) {
//        if(filled_volume > block.getFilled_volume()){
//            return 1;
//        }
//        else if(filled_volume == block.getFilled_volume()){
//            return 0;
//        }
//        else {
//            return -1;
//        }
//    }

    //Used for sorting based on value density = value/volume
    //If the value density is the same take the bigger volume
    @Override
    public int compareTo(Block block) {
        if( value_density > block.getValue_density()){
            return 1;
        }
        else if(value_density == block.getValue_density()){
            if(filled_volume > block.getFilled_volume()){
                return 1;
            }
            else if(filled_volume == block.getFilled_volume()){
                return 0;
            }
            else {
                return -1;
            }
        }
        else{

            return -1;
        }
    }


    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("Block containing:\n");
        sb.append(String.format("Filled volume: %d\n", filled_volume));
        for(int i = 0; i < packing.size(); i++){
            sb.append(packing.get(i).toString());
            sb.append("\n");
        }
        return sb.toString();
    }


    //TODO:
}
