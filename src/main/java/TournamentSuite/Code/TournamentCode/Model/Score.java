package TournamentSuite.Code.TournamentCode.Model;

public class Score {
    private String groupname;
    private String filename;
    private double scoreValue;
    private int packingValue;

    public Score(String groupname, String filename,int packingValue,  double scoreValue){
        this.groupname = groupname;
        this.filename = filename;
        this.scoreValue = scoreValue;
        this.packingValue =packingValue;
    }

    public String getGroupname() {
        return groupname;
    }

    public void setGroupname(String groupname) {
        this.groupname = groupname;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public double getScoreValue() {
        return scoreValue;
    }

    public void setScoreValue(double scoreValue) {
        this.scoreValue = scoreValue;
    }

    public int getPackingValue() {
        return packingValue;
    }

    public void setPackingValue(int packingValue) {
        this.packingValue = packingValue;
    }
}
