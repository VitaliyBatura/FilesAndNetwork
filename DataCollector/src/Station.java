
public class Station {
    private String stationName;

    private String stationLineNumber;

    private String stationLineName;

    private String stationDate;

    private double stationDepth;

    private boolean hasConnection;

    public Station(String stationName, String stationLineNumber) {
        this.stationName = stationName;
        this.stationLineNumber = stationLineNumber;
    }

    public String getStationName() {
        return stationName;
    }

    public void setStationName(String stationName) {
        this.stationName = stationName;
    }

    public String getStationLineNumber() {
        return stationLineNumber;
    }

    public void setStationLineNumber(String stationLineNumber) {
        this.stationLineNumber = stationLineNumber;
    }

    public String getStationLineName() {
        return stationLineName;
    }

    public void setStationLineName(String stationLineName) {
        this.stationLineName = stationLineName;
    }

    public String getStationDate() {
        return stationDate;
    }

    public void setStationDate(String stationDate) {
        this.stationDate = stationDate;
    }

    public double getStationDepth() {
        return stationDepth;
    }

    public void setStationDepth(double stationDepth) {
        this.stationDepth = stationDepth;
    }

    public boolean isHasConnection() {
        return hasConnection;
    }

    public void setHasConnection(boolean hasConnection) {
        this.hasConnection = hasConnection;
    }
}