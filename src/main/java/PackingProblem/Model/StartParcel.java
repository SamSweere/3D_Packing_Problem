package PackingProblem.Model;

import PackingProblem.Algorithms.CLTRS.Block;

import java.util.Arrays;

public class StartParcel implements Comparable<StartParcel>{
    private final Parcel parcel;
    private final double value;
    private int amount;

    public StartParcel(Parcel parcel, double value, int amount){
        this.parcel = parcel;
        this.value = value;
        this.amount = amount;
    }

    public StartParcel(Parcel parcel, int amount){
        this.parcel = parcel;
        this.value = parcel.getValue();
        this.amount = amount;
    }

    public Parcel getParcel() {
        return parcel.clone();
    }

    public Parcel useParcel(){
        // Remove one parcel from the count
        this.amount -= 1;
        return parcel.clone();
    }

    public double getValue() {
        return value;
    }

    public int getAmount() {
        return amount;
    }

    public String toString(){
        return String.format("StartParcel: (%d, %d, %d) volume: %d, value: %f, amount: %d", parcel.getX_size(),
                parcel.getY_size(), parcel.getZ_size(), parcel.getFilled_volume(), value, amount);
    }

    public boolean equals(Parcel parcel){
        if (parcel == null){
            return false;
        }
        int[] startParcelDimensions = {this.parcel.getX_size(), this.parcel.getY_size(), this.parcel.getZ_size()};
        int[] parcelDimensions = {parcel.getX_size(), parcel.getY_size(), parcel.getZ_size()};
        Arrays.sort(startParcelDimensions);
        Arrays.sort(parcelDimensions);
        return Arrays.equals(startParcelDimensions, parcelDimensions);
    }


    //Used for sorting based on value density, then volume
    @Override
    public int compareTo(StartParcel startParcel) {
        if(parcel.getValue_density() > startParcel.getParcel().getValue_density()){
            return 1;
        }
        else if(parcel.getValue_density() == startParcel.getParcel().getValue_density()){
            if(parcel.getFilled_volume() > startParcel.getParcel().getFilled_volume()){
                return 1;
            }
            else if(parcel.getFilled_volume() == startParcel.getParcel().getFilled_volume()){
                return 0;
            }
            else{
                return -1;
            }
        }
        else{
            return -1;
        }
    }

//    //Used for sorting based on volume
//    @Override
//    public int compareTo(StartParcel startParcel) {
//        if(parcel.getFilled_volume() > startParcel.getParcel().getFilled_volume()){
//            return 1;
//        }
//        else if(parcel.getFilled_volume() == startParcel.getParcel().getFilled_volume()){
//            return 0;
//        }
//        else{
//            return -1;
//        }
//    }
    //Used for sorting based on height (y), width(x) and length(z)
//    @Override
//    public int compareTo(StartParcel startParcel) {
//        if(parcel.getY_size() > startParcel.getParcel().getY_size()){
//            return 1;
//        }
//        else if(parcel.getY_size() < startParcel.getParcel().getY_size()){
//            return -1;
//        }
//        // Height is the same at this point
//        else if(parcel.getX_size() > startParcel.getParcel().getX_size()){
//            return 1;
//        }
//        else if(parcel.getX_size() < startParcel.getParcel().getX_size()){
//            return -1;
//        }
//        // Height and width are the same at this point
//        else if(parcel.getZ_size() > startParcel.getParcel().getZ_size()){
//            return 1;
//        }
//        else if(parcel.getZ_size() < startParcel.getParcel().getZ_size()){
//            return -1;
//        }
//
//        // Everything is the same
//        return 0;
//    }
}
