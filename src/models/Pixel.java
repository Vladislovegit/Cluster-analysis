package models;

public class Pixel {
    private int red = 0;
    private int green = 0;
    private int blue = 0;

    public Pixel(int r, int g, int b) {
        red = r;
        green = g;
        blue = b;
    }

    public Pixel(int argb) {
        fromInt(argb);
    }

    private void fromInt(int argb) {
        red = (argb >> 16) & 0xFF;
        green = (argb >> 8) & 0xFF;
        blue = argb & 0xFF;
    }

    public int toInteger() {
        return red * 256 * 256 + green * 256 + blue;
    }

    public int getBrightness() {
        return (int) (0.3 * red + 0.59 * green + 0.11 * blue);
    }

    public int getBlue() {
        return blue;
    }

    public int getGreen() {
        return green;
    }

    public int getRed() {
        return red;
    }

    public void setBlue(int blue) {
        if (blue > 255) this.blue = 255;
        else if (blue < 0) this.blue = 0;
        else this.blue = blue;
    }

    public void setGreen(int green) {
        if (green > 255) this.green = 255;
        else if (green < 0) this.green = 0;
        else this.green = green;
    }

    public void setRed(int red) {
        if (red > 255) this.red = 255;
        else if (red < 0) this.red = 0;
        else this.red = red;
    }
}
