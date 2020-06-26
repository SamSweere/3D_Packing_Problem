package CodeForStudents.Model;

import java.util.ArrayList;
import java.util.Arrays;

public class Parcel {
    private Coordinate location;
    private int x_size;
    private int y_size;
    private int z_size;
    private int value;

    public Parcel(int x_size, int y_size, int z_size, int value) {
        this.x_size = x_size;
        this.y_size = y_size;
        this.z_size = z_size;
        this.value = value;
    }

    public Parcel(Coordinate location, int x_size, int y_size, int z_size) {
        this.location = location;
        this.x_size = x_size;
        this.y_size = y_size;
        this.z_size = z_size;
    }


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

    public Coordinate getLocation() {
        return this.location;
    }

    public void setLocation(Coordinate location) {
        this.location = location;
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

    public String toString(){
        if(location == null){
            return String.format("Parcel with no location with dimensions: %d x %d x %d", x_size, y_size, z_size);
        }else{
            return String.format("Parcel: (%d, %d, %d) %d x %d x %d", this.location.x, this.location.y, this.location.z, this.x_size, this.y_size, this.z_size);
        }
    }

    public boolean equals(Parcel parcel){
        if (parcel == null){
            return false;
        }
        int[] startParcelDimensions = {this.x_size, this.y_size, this.z_size};
        int[] parcelDimensions = {parcel.getX_size(), parcel.getY_size(), parcel.getZ_size()};
        Arrays.sort(startParcelDimensions);
        Arrays.sort(parcelDimensions);
        return Arrays.equals(startParcelDimensions, parcelDimensions);
    }
}
