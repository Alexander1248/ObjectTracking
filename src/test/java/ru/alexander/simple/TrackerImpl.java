package ru.alexander.simple;


import ru.alexander.Source;
import ru.alexander.Tracker;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class TrackerImpl extends Tracker {
    public double x;
    public double y;
    public double z;

    @Override
    public void trackSignals(Source[] sources) {
        double[][] data = getData();
        Arrays.fill(data, new double[] { Double.MAX_VALUE, 0, 0, 0 });

        ByteBuffer buffer = ByteBuffer.allocate(24);
        buffer.putDouble(x);
        buffer.putDouble(y);
        buffer.putDouble(z);

        for (int i = 0; i < sources.length; i++) {
            double dst = sources[i].sendSignal(ByteBuffer.wrap(buffer.array())) * SourceImpl.signalSpeed;
            if (dst < data[3][0]) {
                data[3] = new double[]{ dst, sources[i].x, sources[i].y, sources[i].z };
                for (int j = 2; j >= 0 && dst < data[j][0]; j--) {
                    double[] buff = data[j];
                    data[j] = data[j + 1];
                    data[j + 1] = buff;
                }
            }
        }
    }
}
