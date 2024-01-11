package ru.alexander.iterative.propagation;


import ru.alexander.Source;

import java.nio.ByteBuffer;

public final class SourceImpl extends Source {
    public static double signalSpeed = 3e8;
    public static double signalNoiseFactor = 0;

    public SourceImpl(double x, double y, double z) {
        super(x, y, z);
    }

    public double sendSignal(ByteBuffer data) {
        return Math.sqrt(Math.pow(x - data.getDouble(), 2) + Math.pow(y - data.getDouble(), 2)
                + Math.pow(z - data.getDouble(), 2)) / signalSpeed + (Math.random() - 0.5) * signalNoiseFactor;
    }
}
