package ru.alexander;

import java.util.Objects;

public final class Source {
    public static double signalSpeed = 3e8;
    public static double signalNoiseFactor = 0;
    public double x;
    public double y;
    public double z;

    public Source(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }


    public double sendSignal(Tracker tracker) {
        return Math.sqrt(Math.pow(x - tracker.x, 2) + Math.pow(y - tracker.y, 2)
                + Math.pow(z - tracker.z, 2)) / signalSpeed + (Math.random() - 0.5) * signalNoiseFactor;
    }
}
