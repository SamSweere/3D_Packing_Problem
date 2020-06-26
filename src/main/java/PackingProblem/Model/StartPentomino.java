package PackingProblem.Model;

public class StartPentomino implements Comparable<StartPentomino> {
    private final Pentomino pentomino;
    private final double value;
    private int amount;

    public StartPentomino(Pentomino pentomino, double value, int amount) {
        this.pentomino = pentomino;
        this.value = value;
        this.amount = amount;
    }

    public Pentomino getPentomino() {
        return pentomino.clone();
    }

    public Pentomino usePentomino() {
        // Remove one pentomino from the count
        this.amount -= 1;
        return pentomino.clone();
    }

    public Shape getShape() {
        return pentomino.getShape();
    }

    public double getValue() {
        return value;
    }

    public int getAmount() {
        return amount;
    }

    public String toString() {
        return String.format("StartPentomino: %s, value: %f, amount: %d", pentomino.getShape(), value, amount);
    }

    @Override
    public int compareTo(StartPentomino startPentomino) {
        return Double.compare(value, startPentomino.value);
    }
}
