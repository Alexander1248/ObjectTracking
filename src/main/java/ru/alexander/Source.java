package ru.alexander;

import java.nio.ByteBuffer;

public abstract class Source {
    public double x;
    public double y;
    public double z;

    public Source(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }


    public abstract double sendSignal(ByteBuffer data);
}
