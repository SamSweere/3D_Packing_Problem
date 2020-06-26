package PackingProblem.Model;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class Parcel{
    private Coordinate location;
    private final int x_size;
    private final int y_size;
    private final int z_size;
    private final double value;
    private final double value_density;
    private final int filled_volume;
    private int id; // Used count how many are in a block

//    private boolean consists_out_of_pentominoes = false;
    // The p_packing is saved relative to the parcel, the location is not transfered into this array
    private ArrayList<Pentomino> p_packing = null;


    public Parcel(int x_size, int y_size, int z_size, double value) {
        this.x_size = x_size;
        this.y_size = y_size;
        this.z_size = z_size;
        this.value = value;
        this.filled_volume = x_size*y_size*z_size;
        this.value_density = value/(double) (x_size*y_size*z_size);
    }

    public Parcel(int x_size, int y_size, int z_size, double value, int filled_vol, ArrayList<Pentomino> p_packing) {
        this.x_size = x_size;
        this.y_size = y_size;
        this.z_size = z_size;
        this.value = value;
        this.p_packing = p_packing;
        this.filled_volume = filled_vol;

        this.value_density = value/(double) (x_size*y_size*z_size);
    }

    public Parcel(int x_size, int y_size, int z_size, double value, int filled_vol, int id, ArrayList<Pentomino> p_packing) {
        this.x_size = x_size;
        this.y_size = y_size;
        this.z_size = z_size;
        this.value = value;
        this.id = id;
        this.p_packing = p_packing;
        this.filled_volume = filled_vol;
        this.value_density = value/(double) (x_size*y_size*z_size);
    }

    public Parcel(int x_size, int y_size, int z_size, ArrayList<Pentomino> p_packing) {
        this.x_size = x_size;
        this.y_size = y_size;
        this.z_size = z_size;
        double value = 0;
        int f_vol = 0;
        for(Pentomino p:p_packing){
             value += p.getValue();
             f_vol += 5; // Pentominoes consist out of 5 blocks
        }
        this.value = value;
        this.filled_volume = f_vol;
        this.value_density = value/(double) (x_size*y_size*z_size);
        this.p_packing = p_packing;
    }

    public Parcel(Coordinate location, int x_size, int y_size, int z_size, double value, int filled_vol) {
        this.location = location;
        this.x_size = x_size;
        this.y_size = y_size;
        this.z_size = z_size;
        this.value = value;
        this.filled_volume = filled_vol;
        this.value_density = value/(double) (x_size*y_size*z_size);
    }

    public Parcel(Coordinate location, int x_size, int y_size, int z_size, double value, int filled_vol, ArrayList<Pentomino> p_packing) {
        this.location = location;
        this.x_size = x_size;
        this.y_size = y_size;
        this.z_size = z_size;
        this.value = value;
        this.p_packing = p_packing;
        this.filled_volume = filled_vol;
        this.value_density = value/(double) (x_size*y_size*z_size);
    }

//    public Parcel(ArrayList<Pentomino> p_packing){
//        this.p_packing = p_packing;
//        //TODO: value etc
//        this.x_size = 1;
//        this.y_size = 1;
//        this.z_size = 1;
//        this.value = 1;
//        this.filled_volume = 1;
//    }




    public Parcel(Parcel parcel, Coordinate location){
        this.x_size = parcel.getX_size();
        this.y_size = parcel.getY_size();
        this.z_size = parcel.getZ_size();
        this.value = parcel.getValue();
        this.location = location;
        this.id = parcel.getId();
        this.p_packing = parcel.getP_packing();
        this.filled_volume = parcel.getFilled_volume();
        this.value_density = parcel.getValue_density();
    }

    public ArrayList<Pentomino> getP_packing() {
        return p_packing;
    }

    public ArrayList<Parcel> get_rotations(){
        // Returns an arraylist with all the possible rotations
        ArrayList<Parcel> parcel_rot = new ArrayList<>();
        // Add the original parcel to the arraylist
        parcel_rot.add(this.clone());

        // Note, since we are rotating a rectangular box we do not have to check a lot of rotations since they
        // will have the same dimensions. I.e. rotating around the z-axis we can swap x and y once (if we would do all of them they would swap twice)
        // The letters in the rotations indicate a 90 deg rotation along that axes, if there are two letters it means
        // first to 90 deg rotation on the first axes then a 90 deg on the second axes
        // These combinations should cover every possible unique dimension rotation for boxes
        String[] rotations;
        if(this.x_size == this.y_size && this.x_size == this.z_size && this.y_size == this.z_size){
            //This is a cube rotations do not make sense
            return parcel_rot;
        }
        else if(this.x_size == this.y_size || this.x_size == this.z_size || this.y_size == this.z_size){
            // Two sides are the same, we only need to do the basic rotations
            rotations  = new String[]{"x", "y", "z"};
        }
        else {
            // All sides have different dimensions, do all the possible rotations that make sense
            rotations  = new String[]{"x", "y", "z", "xy", "zy"};
        }

        for(String rots:rotations){
            ArrayList<Pentomino> new_p_packing = null;
            if(this.p_packing != null){
                new_p_packing = (ArrayList<Pentomino>) p_packing.clone();
            }
            int new_x = this.x_size;
            int new_y = this.y_size;
            int new_z = this.z_size;

            for(int i=0; i<rots.length();i++){
                char rot = rots.charAt(i);

                if(rot == 'x'){
                    //swap y and z
                    int buf_y = new_y;
                    new_y = new_z;
                    new_z = buf_y;
                    if(new_p_packing != null) {
                        // Rotate p_packing
                        new_p_packing = rotate_p_packing(new_p_packing, rot);
                    }
                }
                else if(rot == 'y'){
                    //swap x and z
                    int buf_x = new_x;
                    new_x = new_z;
                    new_z = buf_x;
                    if(new_p_packing != null) {
                        // Rotate p_packing
                        new_p_packing = rotate_p_packing(new_p_packing, rot);
                    }
                }
                else if(rot == 'z'){
                    //swap x and y
                    int buf_x = new_x;
                    new_x = new_y;
                    new_y = buf_x;
                    if(new_p_packing != null) {
                        // Rotate p_packing
                        new_p_packing = rotate_p_packing(new_p_packing, rot);
                    }
                }
                else{
                    System.out.println("Invalid rotation character: " + rot);
                }
            }

            // Add the rotated parcel
            parcel_rot.add(new Parcel(new_x, new_y, new_z, this.value, this.filled_volume, this.id, new_p_packing));
        }

        return parcel_rot;
    }

    private ArrayList<Pentomino> rotate_p_packing(ArrayList<Pentomino> p_packing, char rot){
        ArrayList<Pentomino> new_p_packing = new ArrayList<>();
        for(Pentomino pent:p_packing){
            Coordinate[] coords = pent.getCoordinates();
            Coordinate[] new_coords = new Coordinate[coords.length];

            double rot_angle = 1.0/2.0*Math.PI;

            for(int i = 0; i < coords.length; i++){
                Coordinate coord = coords[i];
                int new_x;
                int new_y;
                int new_z;
                if(rot == 'x'){
                    new_x = coord.x;
                    new_y = (int) Math.round(coord.y*Math.cos(rot_angle) - coord.z*Math.sin(rot_angle));
                    new_z = (int) Math.round(coord.y*Math.sin(rot_angle) + coord.z*Math.cos(rot_angle));

                    // Mirror the y values back into positive
                    new_y = -1*new_y;
                }
                else if(rot == 'y'){
                    new_x = (int) Math.round(coord.x*Math.cos(rot_angle) + coord.z*Math.sin(rot_angle));
                    new_y = coord.y;
                    new_z = (int) Math.round(coord.z*Math.cos(rot_angle) - coord.x*Math.sin(rot_angle));

                    // Mirror the z values back into positive
                    new_z = -1*new_z;
                }
                else if(rot == 'z'){
                    new_x = (int) Math.round(coord.x*Math.cos(rot_angle) - coord.y*Math.sin(rot_angle));
                    new_y = (int) Math.round(coord.x*Math.sin(rot_angle) + coord.z*Math.cos(rot_angle));
                    new_z = coord.z;

                    // Mirror the x values back into positive
                    new_x = -1*new_x;
                }
                else{
                    System.out.println("ERROR invalid rotation");
                    new_x = -1;
                    new_y = -1;
                    new_z = -1;
                }

                new_coords[i] = new Coordinate(new_x, new_y, new_z);
            }

            // Make a new pentomino with the new coordinates
            new_p_packing.add(new Pentomino(pent.getShape(), pent.getValue(), new_coords));
        }
        return new_p_packing;
    }
//
//    public int getVolume(){
//        int volume = x_size*y_size*z_size;
//        return volume;
//    }

    public int getX_size() {
        return this.x_size;
    }

    public int getY_size() {
        return this.y_size;
    }

    public int getZ_size() {
        return this.z_size;
    }

    public double getValue() {
        return this.value;
    }

    public double getValue_density() {
        return value_density;
    }

    public int getFilled_volume() {
        return filled_volume;
    }

    public Coordinate getLocation() {
        return this.location;
    }

    public void setLocation(Coordinate location) {
        this.location = location;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public ArrayList<Coordinate> calculateCoordinates(){
        ArrayList<Coordinate> coordinates = new ArrayList<>();
        for(int x=0; x<x_size;x++){
            for(int y=0; y<y_size;y++){
                for(int z=0;z<z_size;z++){
                    coordinates.add(new Coordinate(this.location.x+x, this.location.y+y, this.location.z+z));
                }
            }
        }
        return coordinates;
    }

    public Parcel clone() {

        if(location != null){
            if(p_packing != null){
                return new Parcel(this.location.clone(), this.x_size, this.y_size, this.z_size, this.value, this.filled_volume, (ArrayList<Pentomino>) this.p_packing.clone());
            }
            else{
                return new Parcel(this.location.clone(), this.x_size, this.y_size, this.z_size, this.value, this.filled_volume);
            }
        }
        else{
            if(p_packing != null){
                return new Parcel(this.x_size, this.y_size, this.z_size, this.value, this.filled_volume, (ArrayList<Pentomino>) this.p_packing.clone());
            }
            else{
                return new Parcel(this.x_size, this.y_size, this.z_size, this.value);
            }

        }

    }

    public String toString(){
        if(location == null){
            return String.format("Parcel with no location with dimensions: %d x %d x %d", x_size, y_size, z_size);
        }else{
            return String.format("Parcel: (%d, %d, %d) %d x %d x %d", this.location.x, this.location.y, this.location.z, this.x_size, this.y_size, this.z_size);
        }
    }
}
