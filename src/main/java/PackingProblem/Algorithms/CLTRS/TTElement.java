package PackingProblem.Algorithms.CLTRS;

public class TTElement {
    public final long primaryHash;
    public final double value;

    public TTElement(long primaryHash, double value){
        this.primaryHash = primaryHash;
        this.value = value;
    }
}
