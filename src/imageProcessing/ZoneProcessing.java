package imageProcessing;

import models.Zone;

public class ZoneProcessing {

    public static Double getElongation(Integer[][] pixels, Zone zone) {
        return (getCentralMoment(pixels, zone, 2, 0) + getCentralMoment(pixels, zone, 0, 2) +
                Math.sqrt(Math.pow(getCentralMoment(pixels, zone, 2, 0) - getCentralMoment(pixels, zone, 0, 2), 2) +
                        4 * Math.pow(getCentralMoment(pixels, zone, 1, 1), 2))) /
                (getCentralMoment(pixels, zone, 2, 0) + getCentralMoment(pixels, zone, 0, 2) -
                        Math.sqrt(Math.pow(getCentralMoment(pixels, zone, 2, 0) - getCentralMoment(pixels, zone, 0, 2), 2) +
                                4 * Math.pow(getCentralMoment(pixels, zone, 1, 1), 2)));
    }

    public static Double getCentralMoment(Integer[][] pixels, Zone zone, Integer i, Integer j) {
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

    public static Integer getZoneSquare(Integer[][] pixels, Integer zoneId) {
        Integer square = 0;
        for (int i = 0; i < pixels.length; i++) {
            for (int j = 0; j < pixels[i].length; j++) {
                if (isZoneContainsPixel(pixels[i][j], zoneId))
                    square++;
            }
        }
        return square;
    }

    public static Integer getZonePerimeter(Integer[][] pixels, Integer zoneId) {
        Integer perimeter = 0;
        for (int i = 0; i < pixels.length; i++) {
            for (int j = 0; j < pixels[i].length; j++) {
                if (isZoneContainsPixel(pixels[i][j], zoneId))
                    if (isPixelOnZoneBorder(pixels, i, j))
                        perimeter++;
            }
        }
        return perimeter;
    }

    public static void getMassCenter(Integer[][] pixels, Zone zone) {
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
                if ((i >= pixels.length) || (j >= pixels[0].length))
                    return true;
                else if (pixels[i][j] == 0)
                    return true;
            }
        }
        return false;
    }

    public static Boolean isZoneContainsPixel(Integer pixel, Integer zoneId) {
        return pixel.equals(zoneId);
    }

}
