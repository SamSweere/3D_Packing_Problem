package PackingProblem.Model;

import PackingProblem.Algorithms.IParcelSolver;
import PackingProblem.IO.ParcelReader;
import PackingProblem.IO.ParcelWriter;

import java.util.ArrayList;
import java.util.HashMap;

public class ParcelProblem{
    private static final int MULTIPLICATION_CONSTANT = 2;
    private Container container;
    private ArrayList<StartParcel> parcelSubset;
    private double totalValue;
    private Parcel[][][] positioning;
    private ArrayList<Parcel> parcels;

    // This constructor is used when loading the problem
    public ParcelProblem(){
        this.totalValue = 0;
        this.parcelSubset = new ArrayList<>();
        this.parcels = new ArrayList<>();
    }

    public void addStartParcel(double x, double y, double z, double value, int amount){
        // Simple to int conversion went wrong 3.0 became 2. Therefore use Math.round()
        Parcel p = new Parcel((int) Math.round(x*MULTIPLICATION_CONSTANT), (int) Math.round(y*MULTIPLICATION_CONSTANT),
                (int) Math.round(z*MULTIPLICATION_CONSTANT), (double) value);
        StartParcel ps = new StartParcel(p, value, amount);
        this.parcelSubset.add(ps);
    }

    public int getMultiplicationConstant() {
        return this.MULTIPLICATION_CONSTANT;
    }

    public Container getContainer() {
        return this.container;
    }

    public void setContainer(double x_size, double y_size, double z_size) {
        // Simple to int conversion went wrong 3.0 became 2. Therefore use Math.round()
        this.container = new Container((int) Math.round(x_size*MULTIPLICATION_CONSTANT),
                (int) Math.round(y_size*MULTIPLICATION_CONSTANT),
                (int) Math.round(z_size*MULTIPLICATION_CONSTANT));
        this.positioning = new Parcel[this.container.getX_size()][this.container.getY_size()][this.container.getZ_size()];
    }

    public void setContainer(Container container) {
        this.container = container;
    }

    public ArrayList<StartParcel> getParcelSubset() {
        return (ArrayList<StartParcel>) this.parcelSubset.clone();
    }

    public void solve(IParcelSolver solver) {
        solver.solve(this);
    }

    public boolean isParcelSetable(Parcel parcel){
        for(Coordinate coordinate: parcel.calculateCoordinates()){
            if(coordinate.x >= this.container.getX_size() || coordinate.y >= this.container.getY_size() || coordinate.z >= this.container.getZ_size() ||
                    coordinate.x < 0 || coordinate.y < 0 || coordinate.z < 0 ||
                    this.positioning[coordinate.x][coordinate.y][coordinate.z] != null){
                return false;
            }
        }
        return true;
    }

    public double getTotalValue() {
        return this.totalValue;
    }

    public void setTotalValue(double totalValue) {
        this.totalValue = totalValue;
    }

    public ArrayList<Parcel> getParcels() {
        return (ArrayList<Parcel>) this.parcels.clone();
    }

    public void setParcels(ArrayList<Parcel> parcels) {
        this.parcels = parcels;
    }

    public void setParcelSubset(ArrayList<StartParcel> parcelSubset) {
        this.parcelSubset = parcelSubset;
    }

    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("ParcelProblem containing:  (note that these are the internal values, they are not" +
                " converted using the multiplication constant)\n");

        sb.append(container.toString() + "\n");
        sb.append("Having parcelSubset: \n");
        for(int i = 0; i < this.parcelSubset.size(); i++){
            sb.append(this.parcelSubset.get(i).toString() + "\n");
        }
        sb.append("Having packing: \n");

        for(int i = 0; i < this.parcels.size(); i++){
            sb.append(this.parcels.get(i).toString() + "\n");
        }
        return sb.toString();
    }

    public void saveToFile(String filename) {
        ParcelWriter pw = new ParcelWriter(filename);
        pw.writeToFile(this);
    }

    public void loadFromFile(String filename) {
        ParcelReader pr = new ParcelReader(filename);
        pr.loadFromFile(this);
        //TODO: calculate the total value
    }

    public boolean checkCorrectness(){
        this.positioning = new Parcel[this.container.getX_size()][this.container.getY_size()][this.container.getZ_size()];
        HashMap<StartParcel, Integer> startParcelCounters = new HashMap<>();
        double valueSum = 0;

        //Initialize counters
        for(int i=0; i<this.parcelSubset.size(); i++){
            startParcelCounters.put(this.parcelSubset.get(i), 0);
        }

        for(int i = 0; i<this.parcels.size(); i++){
            Parcel parcel = this.parcels.get(i);
            StartParcel subsetElement = null;

            //Check if current parcel is in the subset
            for(int j=0; j<this.parcelSubset.size(); j++){
                if(parcelSubset.get(j).equals(parcel)){
                    subsetElement = parcelSubset.get(j);
                }
            }

            //If current parcel is in the subset
            if(subsetElement != null){
                //Increment the counter and the sum of the parcel values
                int counter = startParcelCounters.get(subsetElement) + 1;
                startParcelCounters.put(subsetElement, counter);
                valueSum += subsetElement.getValue();

                if(this.isParcelSetable(parcel) && counter<=subsetElement.getAmount()){
                    ArrayList<Coordinate> coordinates = parcel.calculateCoordinates();
                    for (int j = 0; j < coordinates.size(); j++) {
                        Coordinate coordinate = coordinates.get(j);
                        this.positioning[coordinate.x][coordinate.y][coordinate.z] = parcel;
                    }
                }else{
                    return false;
                }
            }else{
                return false;
            }
        }
        //Check if the sum of the pentomino values is equal to the stored total value
        return valueSum == this.totalValue;
    }
}
