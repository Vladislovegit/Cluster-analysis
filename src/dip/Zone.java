package dip;

public class Zone {
    public Integer id;
    public Integer square;
    public Integer averageX;
    public Integer averageY;
    public Integer perimeter;
    public Double compact;
    public Double elongation;
    public Integer cluster;

    public Zone() {
        id = 0;
        square = 0;
        averageX = 0;
        averageY = 0;
        perimeter = 0;
        compact = 0.0;
        elongation = 0.0;
        cluster = 0;
    }

    public Double getRange(Coordinates<Double, Double, Integer, Integer> clusterMeans) {
        return Math.sqrt(Math.pow(0.3 *(clusterMeans.getFirst() - compact), 2) +
                Math.pow(1 * (clusterMeans.getSecond() - elongation), 2) +
                Math.pow(0.5 * (clusterMeans.getThird() - square), 2) +
                Math.pow(1 * (clusterMeans.getFourth() - perimeter), 2));
    }
}
