package PackingProblem.Algorithms.CLTRS;

import PackingProblem.Model.Coordinate;

public class ResidualSpace implements Cloneable{
    // Assumes cuboid space
    // Bottom up left coordinate
    private final Coordinate location;
    private final int x_size;
    private final int y_size;
    private final int z_size;
    private final int type; // Min = 0, med = 1, max = 2

    public ResidualSpace(Coordinate location, int x, int y, int z, int type){
        this.location = location.clone();
        this.x_size = x;
        this.y_size = y;
        this.z_size = z;
        this.type = type;
    }

    public Coordinate getLocation() {
        return location;
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

    public int getType() {
        return type;
    }

    @Override
    public String toString() {
        return "ResidualSpace{" +
                "location=" + location +
                ", type=" + type +
                ", x_size=" + x_size +
                ", y_size=" + y_size +
                ", z_size=" + z_size +
                '}';
    }

    public ResidualSpace clone(){
        return new ResidualSpace(this.location, this.x_size, this.y_size, this.z_size, this.type);
    }
}
