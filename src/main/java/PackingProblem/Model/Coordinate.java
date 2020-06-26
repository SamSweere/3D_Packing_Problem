package PackingProblem.Model;

public class Coordinate implements Comparable {
    public int x;
    public int y;
    public int z;

    public Coordinate(){
    }

    public Coordinate(int x, int y, int z){
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Coordinate(Coordinate coordinate){
        this.x = coordinate.x;
        this.y = coordinate.y;
        this.z = coordinate.z;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    public Coordinate clone(){
        return new Coordinate(x,y,z);
    }

    public String toString(){
        return String.format("(%d, %d, %d)", this.x, this.y, this.z);
    }

    @Override
    public int compareTo(Object object) {
        Coordinate compareCoordinate = (Coordinate) object;
        int value1 = this.x - compareCoordinate.x;
        if (value1 == 0) {
            int value2 = this.y - compareCoordinate.y;
            if (value2 == 0) {
                return this.z - compareCoordinate.z;
            } else {
                return value2;
            }
        }
        return value1;
    }
}
