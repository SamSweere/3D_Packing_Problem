package PackingProblem.Model;

import PackingProblem.Algorithms.IPentominoSolver;
import PackingProblem.IO.PentominoReader;
import PackingProblem.IO.PentominoWriter;

import java.util.ArrayList;
import java.util.HashMap;

public class PentominoProblem{
    private static final int MULTIPLICATION_CONSTANT = 2;
    private Container container;
    private ArrayList<StartPentomino> pentominoSubset;
    private double totalValue;
    private Pentomino[][][] positioning;
    private ArrayList<Pentomino> pentominos;

    public PentominoProblem() {
        this.totalValue = 0;
        this.pentominos = new ArrayList<>();
        this.pentominoSubset = new ArrayList<>();
    }

    public int getMultiplicationConstant() {
        return this.MULTIPLICATION_CONSTANT;
    }

    public Container getContainer() {
        return this.container;
    }

    public void setContainer(double x_size, double y_size, double z_size) {
        this.container = new Container((int) (x_size * this.MULTIPLICATION_CONSTANT), (int) (y_size * this.MULTIPLICATION_CONSTANT),
                (int) (z_size * this.MULTIPLICATION_CONSTANT));
        this.positioning = new Pentomino[this.container.getX_size()][this.container.getY_size()][this.container.getZ_size()];
    }

    public void setContainer(Container container) {
        this.container = container;
    }

    public void addStartPentomino(Shape shape, double value, int amount){
        Pentomino p = new Pentomino(shape, value);
        StartPentomino ps = new StartPentomino(p, value, amount);
        this.pentominoSubset.add(ps);
    }

    public ArrayList<StartPentomino> getPentominoSubset() {
        return this.pentominoSubset;
    }

    public void solve(IPentominoSolver solver) {
        solver.solve(this);
    }

    public void setPentomino(Pentomino pentomino){
        this.pentominos.add(pentomino);
        for(Coordinate coordinate: pentomino.getCoordinates()){
            this.positioning[coordinate.x][coordinate.y][coordinate.z] = pentomino;
        }
        this.totalValue += pentomino.getValue();
    }

    public double getTotalValue() {
        return this.totalValue;
    }

    public void setTotalValue(double totalValue) {
        this.totalValue = totalValue;
    }

    public ArrayList<Pentomino> getPentominos() {
        return this.pentominos;
    }

    public void setPentominos(ArrayList<Pentomino> pentominos) {
        this.pentominos = pentominos;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("PentominoeProblem containing:  (note that these are the internal values, they are not" +
                " converted using the multiplication constant)\n");
        sb.append(this.container.toString() + "\n");
        for (int i = 0; i < this.pentominos.size(); i++) {
            sb.append(this.pentominos.get(i).toString() + "\n");
        }
        return sb.toString();
    }

    public void saveToFile(String filename) {
        PentominoWriter pw = new PentominoWriter(filename);
        pw.writeToFile(this);
    }

    public void loadFromFile(String filename) {
        PentominoReader pr = new PentominoReader(filename);
        pr.loadFromFile(this);
    }

    public boolean isPentominoSetable(Pentomino pentomino) {
        for (Coordinate coordinate : pentomino.getCoordinates()) {
            if (coordinate.x >= this.container.getX_size() || coordinate.y >= this.container.getY_size() || coordinate.z >= this.container.getZ_size() ||
                    coordinate.x < 0 || coordinate.y < 0 || coordinate.z < 0){
                System.out.println("ERROR pentominoe outside of container");
                return false;
            }
            else if(this.positioning[coordinate.x][coordinate.y][coordinate.z] != null) {
                System.out.println("ERROR pentominoe inside another pentominoe");
                return false;
            }
        }
        return true;
    }

    public boolean checkCorrectness() {
        this.positioning = new Pentomino[this.container.getX_size()][this.container.getY_size()][this.container.getZ_size()];
        HashMap<Shape, Integer> shapeCounters = new HashMap<>();
        double valueSum = 0;

        //Initialize counters
        for (int i = 0; i < pentominoSubset.size(); i++) {
            shapeCounters.put(pentominoSubset.get(i).getShape(), 0);
        }

        for (int i = 0; i < pentominos.size(); i++) {
            Pentomino pentomino = this.pentominos.get(i);
            Shape shape = pentomino.getShape();
            StartPentomino subsetElement = null;

            //Check if current pentomino shape is in the subset
            for (int j = 0; j < pentominoSubset.size(); j++) {
                if (pentominoSubset.get(j).getShape() == shape) {
                    subsetElement = pentominoSubset.get(j);
                }
            }

            //If current pentomino shape is in the subset
            if (subsetElement != null) {
                //Increment the counter and the sum of the pentomino values
                int counter = shapeCounters.get(shape) + 1;
                shapeCounters.put(shape, counter);
                valueSum += subsetElement.getValue();

                if (this.isPentominoSetable(pentomino) && counter <= subsetElement.getAmount()) {
                    for (Coordinate coordinate : pentomino.getCoordinates()) {
                        this.positioning[coordinate.x][coordinate.y][coordinate.z] = pentomino;
                    }
                } else {
                    if(counter <= subsetElement.getAmount()){
                        System.out.println("ERROR trying to place too many pentominoes of type " + subsetElement.getShape());
                    }
                    return false;
                }
            } else {
                System.out.println("ERROR pentominoe not in subset");
                return false;
            }
        }
        //Check if the sum of the pentomino values is equal to the stored total value
        if(valueSum == this.totalValue){
            return true;
        }else{
            System.out.println("ERROR pentominoe placement correct but invalid value");
            return false;
        }
    }
}
