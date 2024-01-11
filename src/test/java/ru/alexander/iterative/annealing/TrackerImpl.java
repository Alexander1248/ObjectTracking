package ru.alexander.iterative.annealing;


import ru.alexander.Source;
import ru.alexander.iterative.AnnealingTracker;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class TrackerImpl extends AnnealingTracker {
    public double x;
    public double y;
    public double z;

    @Override
    public void trackSignals(Source[] sources) {
        data = new double[sources.length][];
        Arrays.fill(data, new double[] { Double.MAX_VALUE, 0, 0, 0 });

        ByteBuffer buffer = ByteBuffer.allocate(24);
        buffer.putDouble(x);
        buffer.putDouble(y);
        buffer.putDouble(z);

        for (int i = 0; i < sources.length; i++) {
            double dst = sources[i].sendSignal(ByteBuffer.wrap(buffer.array())) * SourceImpl.signalSpeed;
            data[i] = new double[]{ dst, sources[i].x, sources[i].y, sources[i].z };
        }
    }
}
