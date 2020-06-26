package TournamentSuite.Code.TournamentCode.Model;

public class Container {
    private final int x_size;
    private final int y_size;
    private final int z_size;

    public Container(int x_size, int y_size, int z_size) {
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

    public String toString() {
        return String.format("Container: %d x %d x %d", this.x_size, this.y_size, this.z_size);
    }
}
