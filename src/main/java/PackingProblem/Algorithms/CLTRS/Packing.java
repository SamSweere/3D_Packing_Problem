package PackingProblem.Algorithms.CLTRS;

import PackingProblem.Model.Parcel;

import java.util.ArrayList;

public class Packing{
    private ArrayList<Parcel> packing;
    private int filled_volume;
    private double totalValue;

    public Packing(){
        this.packing = new ArrayList<>();
        this.filled_volume = 0;
        this.totalValue = 0;
    }

    public Packing(Packing packing){
        this.packing = (ArrayList<Parcel>) packing.getPacking().clone();
        this.filled_volume = packing.getFilled_volume();
        this.totalValue = packing.getTotalValue();
    }

    public Packing(ArrayList<Parcel> packing){
        this.packing = (ArrayList<Parcel>) packing.clone();

        for(int i = 0; i < packing.size(); i++){
            filled_volume += packing.get(i).getFilled_volume();
        }
    }

    public void addParcel(Parcel parcel){
        packing.add(parcel);
        totalValue += parcel.getValue();
        filled_volume += parcel.getFilled_volume();
    }

    public ArrayList<Parcel> getPacking() {
        return packing;
    }

    public double getTotalValue() {
        return totalValue;
    }

    public Packing clone(){
        return new Packing(this);
    }

    public int getFilled_volume() {
        return filled_volume;
    }

    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("Packing containing:\n");
        sb.append(String.format("Filled volume: %d\n", filled_volume));
        for(int i = 0; i < packing.size(); i++){
            sb.append(packing.get(i).toString());
            sb.append("\n");
        }
        return sb.toString();
    }
}
