package dip;

import javafx.scene.chart.XYChart;

import java.util.ArrayList;
import java.util.Arrays;

public class ImageProcessing {

    public static MyPair<Integer[][], Zone[]> scan(Integer[][] pixels, Integer clustersAmount) {
        Integer[][] res = findZones(pixels);
        Integer zonesAmount = getAmountOfZones(res);
        Zone[] zones = new Zone[zonesAmount];

        calculateParameters(res, zones);
        clusterAnalyze(zones, clustersAmount);

            for (int i = 0; i < res.length; i++) {
                for (int j = 0; j < res[i].length; j++) {
                    for (Zone zone: zones) {
                      if(isZoneContainsPixel(res[i][j], zone.id)) {
                          res[i][j] = (zone.cluster + 1);
                          break;
                      }
                }
            }
        }

        return new MyPair<>(res, zones);
    }

    private static void clusterAnalyze(Zone[] zones, Integer clusterAmount) {
        if(clusterAmount >= zones.length)
            return ;
        Integer[] supposedClusters = new Integer[clusterAmount];
        Arrays.fill(supposedClusters, -1);
        for(int i = 0; i < clusterAmount; i++) {
            supposedClusters[i] = (int)(Math.random() * zones.length);
            for(int j = 0; j < i; j++)
                if(supposedClusters[i].equals(supposedClusters[j]))
                    i--;
        }
        Coordinates<Double, Double, Integer, Integer>[] clusterMeans = new Coordinates[clusterAmount];
        for (int i = 0; i < clusterMeans.length; i++) {
            clusterMeans[i] = new Coordinates<>(zones[supposedClusters[i]].compact, zones[supposedClusters[i]].elongation,
                    zones[supposedClusters[i]].square, zones[supposedClusters[i]].perimeter);
        }

        while(true) {
            boolean isZoneChanged = false;
            for (Zone zone: zones) {
                Coordinates<Integer, Double, Integer, Integer> minRange = new Coordinates<>(-1, Double.MAX_VALUE,
                        Integer.MAX_VALUE, Integer.MAX_VALUE);
                for(int i = 0; i < clusterMeans.length; i++)
                    if(minRange.getSecond() > zone.getRange(clusterMeans[i])) {
                        minRange.setFirst(i);
                        minRange.setSecond(zone.getRange(clusterMeans[i]));
                }
                if(!zone.cluster.equals(minRange.getFirst()))
                    isZoneChanged = true;
                zone.cluster = minRange.getFirst();
            }
            for (int i = 0; i < clusterMeans.length; i++) {
                clusterMeans[i].setFirst(0.0);
                clusterMeans[i].setSecond(0.0);
                clusterMeans[i].setThird(0);
                clusterMeans[i].setFourth(0);

                Integer count = 0;
                for (Zone zone : zones) {
                    if(zone.cluster == i) {
                        clusterMeans[i].setFirst(clusterMeans[i].getFirst() + zone.compact);
                        clusterMeans[i].setSecond(clusterMeans[i].getSecond() + zone.elongation);
                        clusterMeans[i].setThird(clusterMeans[i].getThird() + zone.square);
                        clusterMeans[i].setFourth(clusterMeans[i].getFourth() + zone.perimeter);
                        count++;
                    }
                }
                clusterMeans[i].setFirst(clusterMeans[i].getFirst() / count);
                clusterMeans[i].setSecond(clusterMeans[i].getSecond() / count);
                clusterMeans[i].setThird(clusterMeans[i].getThird() / count);
                clusterMeans[i].setFourth(clusterMeans[i].getFourth() / count);
            }
            if(!isZoneChanged)
                break;
        }
    }

    private static void calculateParameters(Integer[][] pixels, Zone[] zones) {
        ArrayList<Integer> zonesIDs = getZonesIDs(pixels);
        for (int i = 0; i < getAmountOfZones(pixels); i++) {
            zones[i] = new Zone();
            zones[i].id = zonesIDs.get(i);
            zones[i].square = getZoneSquare(pixels, zones[i].id);
            zones[i].perimeter = getZonePerimeter(pixels, zones[i].id);
            zones[i].compact = (double) zones[i].perimeter * zones[i].perimeter / zones[i].square;
            getMassCenter(pixels, zones[i]);
            zones[i].elongation = getElongation(pixels, zones[i]);
        }
    }

    private static Double getElongation(Integer[][] pixels, Zone zone) {
        return (getCentralMoment(pixels, zone, 2, 0) + getCentralMoment(pixels, zone, 0, 2) +
                Math.sqrt(Math.pow(getCentralMoment(pixels, zone, 2, 0) - getCentralMoment(pixels, zone, 0, 2), 2) +
                        4 * Math.pow(getCentralMoment(pixels, zone, 1, 1), 2))) /
                (getCentralMoment(pixels, zone, 2, 0) + getCentralMoment(pixels, zone, 0, 2) -
                        Math.sqrt(Math.pow(getCentralMoment(pixels, zone, 2, 0) - getCentralMoment(pixels, zone, 0, 2), 2) +
                                4 * Math.pow(getCentralMoment(pixels, zone, 1, 1), 2)));
    }

    private static Double getCentralMoment(Integer[][] pixels, Zone zone, Integer i, Integer j) {
        Double moment = 0.0;

        for (int k = 0; k < pixels.length; k++) {
            for (int l = 0; l < pixels[i].length; l++) {
                if (isZoneContainsPixel(pixels[k][l], zone.id)) {
                    moment += Math.pow(k - zone.averageX, i) * Math.pow(l - zone.averageY, j);
                }
            }
        }

        return moment;
    }

    private static ArrayList<Integer> getZonesIDs (Integer[][] pixels) {
        ArrayList<Integer> zonesIDs = new ArrayList<>();
        for (int i = 1; i < pixels.length; i++) {
            for (int j = 1; j < pixels[i].length; j++) {
                if(pixels[i][j] != 0) {
                    if(!zonesIDs.contains(pixels[i][j]))
                        zonesIDs.add(pixels[i][j]);
                }
            }
        }
        return zonesIDs;
    }

    private static Integer getAmountOfZones (Integer[][] pixels) {
        return getZonesIDs(pixels).size();
    }

    private static Integer getZoneSquare(Integer[][] pixels, Integer zoneId) {
        Integer square = 0;
        for (int i = 0; i < pixels.length; i++) {
            for (int j = 0; j < pixels[i].length; j++) {
                if(isZoneContainsPixel(pixels[i][j], zoneId))
                    square++;
            }
        }
        return square;
    }

    private static Integer getZonePerimeter(Integer[][] pixels, Integer zoneId) {
        Integer perimeter = 0;
        for (int i = 0; i < pixels.length; i++) {
            for (int j = 0; j < pixels[i].length; j++) {
                if(isZoneContainsPixel(pixels[i][j], zoneId))
                    if(isPixelOnZoneBorder(pixels, i, j))
                        perimeter++;
            }
        }
        return perimeter;
    }

    private static void getMassCenter(Integer[][] pixels, Zone zone) {
        Integer X = 0, Y = 0;
        for (int i = 0; i < pixels.length; i++) {
            for (int j = 0; j < pixels[i].length; j++) {
                if (isZoneContainsPixel(pixels[i][j], zone.id)) {
                    X += i;
                    Y += j;
                }
            }
        }
        zone.averageX = X / zone.square;
        zone.averageY = Y / zone.square;
    }

    private static Boolean isPixelOnZoneBorder(Integer[][] pixels, Integer x, Integer y) {
        for (int i = x - 1; i <= x + 1; i++) {
            for (int j = y - 1; j <= y + 1; j++) {
                if((i >= pixels.length) || (j >= pixels[0].length))
                    return true;
                else if(pixels[i][j] == 0)
                    return true;
            }
        }
        return false;
    }

    private static Boolean isZoneContainsPixel(Integer pixel, Integer zoneId) {
        return pixel.equals(zoneId);
    }

    private static Integer[][] findZones(Integer[][] pixels) {
        Integer[][] res = new Integer[pixels.length][pixels[0].length];
        for (int i = 0; i < res.length; i++)
            Arrays.fill(res[i], 0);
        ArrayList<ArrayList<Integer>> equalZones = new ArrayList<>();
        Integer N = 0;
        for (int i = 1; i < pixels.length; i++) {
            for (int j = 1; j < pixels[i].length; j++) {
                if(pixels[i][j] != 0) {
                    if((res[i - 1][j] == 0) && (res[i][j - 1] == 0)) {
                        N++;
                        res[i][j] = N;
                    }
                    else  if ((res[i - 1][j] != 0) && (res[i][j - 1] != 0) && res[i - 1][j].equals(res[i][j - 1])) {
                        res[i][j] = res[i - 1][j];
                    }
                    else if ((res[i - 1][j] != 0) && (res[i][j - 1] != 0) && !res[i - 1][j].equals(res[i][j - 1])) {
                        res[i][j] = res[i - 1][j];
                        setEqualZones(equalZones, res[i - 1][j], res[i][j - 1]);
                    } else {
                        if (res[i - 1][j] != 0)
                            res[i][j] = res[i - 1][j];
                        else
                            res[i][j] = res[i][j - 1];
                    }
                }
            }
        }

        for (int i = 1; i < pixels.length; i++) {
            for (int j = 1; j < pixels[i].length; j++) {
                if(res[i][j] != 0) {
                    res[i][j] = getUniqueZoneNumber(equalZones, res[i][j]);
                }
            }
        }
        return res;
    }

    private static Integer getUniqueZoneNumber(ArrayList<ArrayList<Integer>> equalZones, int oldZone) {
        for(int i = 0; i < equalZones.size(); i++) {
            if(equalZones.get(i).contains(oldZone))
                return i + 1;
        }
        return 0;
    }

    private static void setEqualZones( ArrayList<ArrayList<Integer>> equalZones, int zone1, int zone2) {
        for(ArrayList<Integer> zone : equalZones) {
            if(zone.contains(zone1) && zone.contains(zone2)){
                return;
            }
            else if(zone.contains(zone1) && !zone.contains(zone2)){
                zone.add(zone2);
                return;
            } else if(!zone.contains(zone1) && zone.contains(zone2)) {
                zone.add(zone1);
                return;
            }
        }
        ArrayList<Integer> newEqualZone =  new ArrayList<>();
        newEqualZone.add(zone1);
        newEqualZone.add(zone2);
        equalZones.add(newEqualZone);
    }

    public static Integer[][] dissection(Integer[][] pixels, int lowerLimit, int upperLimit) {
        Integer[][] res = new Integer[pixels.length][pixels[0].length];
        for (int i = 0; i < pixels.length; i++) {
            System.arraycopy(pixels[i], 0, res[i], 0, pixels[i].length);
        }

        for (int i = 0; i < pixels.length; i++) {
            for (int j = 0; j < pixels[i].length; j++) {
                res[i][j] = (res[i][j] > lowerLimit && res[i][j] < upperLimit) ? 255 : 0;
            }
        }
        return res;
    }

    public static Integer[][] dissection(Integer[][] pixels, int lowerLimit) {
        Integer[][] res = new Integer[pixels.length][pixels[0].length];
        for (int i = 0; i < pixels.length; i++) {
            System.arraycopy(pixels[i], 0, res[i], 0, pixels[i].length);
        }

        for (int i = 0; i < pixels.length; i++) {
            for (int j = 0; j < pixels[i].length; j++) {
                res[i][j] = (res[i][j] > lowerLimit) ? 255 : 0;
            }
        }
        return res;
    }

    public static XYChart.Series getHistogramSeries(Integer[][] pixels) {
        Integer[] brightneses = new Integer[256];
        Arrays.fill(brightneses, 0);

        XYChart.Series series = new XYChart.Series();
        series.setName("BarChart");




        for (int i = 0; i < pixels.length; i++) {
            for (int j = 0; j < pixels[i].length; j++) {
                brightneses[ pixels[i][j] ]++;
            }
        }

        for (int i = 0; i < brightneses.length ; i++) {
            series.getData().add(new XYChart.Data(Integer.toString(i), brightneses[i]));
        }


        return series;
    }

    public static Integer[][] SobelFilter(Integer[][] pixels) {
        Integer width = pixels.length;
        Integer height = pixels[0].length;
        Integer[][] h1 = {{1, 0, -1}, {2, 0, -2}, {1, 0, -1}};
        Integer[][] h2 = {{-1, -2, -1}, {0, 0, 0}, {1, 2, 1}};
        Integer[][] resh1 = convolution(pixels, h1);
        Integer[][] resh2 = convolution(pixels, h2);

        for (int i = 1; i < width - 1; i++) {
            for (int j = 1; j < height - 1; j++) {
                resh1[i][j] = (int)(Math.sqrt(resh1[i][j] * resh1[i][j] + resh2[i][j] * resh2[i][j]));
                if(resh1[i][j] > 255)
                    resh1[i][j] = 255;
                else if(resh1[i][j] < 0)
                    resh1[i][j] = 0;
            }
        }
        return resh1;
    }

    public static Integer[][] degradation(Integer[][] pixels) {
        Integer width = pixels.length;
        Integer height = pixels[0].length;
        Integer[][] h = {{1, 2, 1}, {2, 4, 2}, {1, 2, 1}};
        Integer[][] res = convolution(pixels, h);

        for (int i = 1; i < width - 1; i++) {
            for (int j = 1; j < height - 1; j++) {
                res[i][j] /= 16;
                if(res[i][j] > 255)
                    res[i][j] = 255;
                else if(res[i][j] < 0)
                    res[i][j] = 0;
            }
        }
        return res;
    }

    public static Integer[][] convolution(Integer[][] pixels, Integer[][] matrix) {
        Integer width = pixels.length;
        Integer height = pixels[0].length;

        Integer[][] resh1 = new Integer[pixels.length][pixels[0].length];
        for (int i = 0; i < pixels.length; i++) {
            System.arraycopy(pixels[i], 0, resh1[i], 0, pixels[i].length);
        }

        Integer pixel;
        for (int i = 1; i < width - 1; i++) {
            for (int j = 1; j < height - 1; j++) {
                pixel = 0;
                for (int k = 0; k < matrix.length; k++) {
                    for (int l = 0; l < matrix[k].length; l++) {
                        pixel += pixels[i - 1 + k][j - 1 + l] * matrix[l][k];
                    }
                }
                resh1[i][j] = pixel;
            }
        }
        return resh1;
    }

}