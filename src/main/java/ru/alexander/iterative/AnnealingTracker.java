package ru.alexander.iterative;

import ru.alexander.Source;

import java.util.ArrayList;
import java.util.List;

public abstract class AnnealingTracker {
    private double cx;
    private double cy;
    private double cz;

    protected double[][] data;

    private final List<double[]> path = new ArrayList<>();

    public abstract void trackSignals(Source[] sources);
    public boolean locate(double startT, double maxIterations) {
        double err = 0;
        for (int i = 0; i < data.length; i++) {
            double dx = cx - data[i][1];
            double dy = cy - data[i][2];
            double dz = cz - data[i][3];
            double d = dx * dx + dy * dy + dz * dz;
            d -= data[i][0] * data[i][0];
            err += Math.abs(d);
        }
        err = Math.sqrt(err / data.length);
        int it = 1;

        path.add(new double[] { cx, cy, cz });
        do {
            double v = err * 0.1;
            double pe = err;
            double px = cx, py = cy, pz = cz;
            cx += (Math.random() - 0.5) * v;
            cy += (Math.random() - 0.5) * v;
            cz += (Math.random() - 0.5) * v;
            err = 0;
            for (int i = 0; i < data.length; i++) {
                double dx = cx - data[i][1];
                double dy = cy - data[i][2];
                double dz = cz - data[i][3];
                double d = dx * dx + dy * dy + dz * dz;
                d -= data[i][0] * data[i][0];
                err += Math.abs(d);
            }
            err = Math.sqrt(err / data.length);
            if (err > pe) {
                cx = px;
                cy = py;
                cz = pz;
                err = pe;
            }
            it++;

            path.add(new double[] { cx, cy, cz });
        } while (it < maxIterations);

        while (path.size() > 100000) path.remove(0);
        System.out.printf("Error: %1.3f\n", err);
        return true;
    }

    public double getCX() {
        return cx;
    }

    public double getCY() {
        return cy;
    }

    public double getCZ() {
        return cz;
    }

    public List<double[]> getPath() {
        return path;
    }
}
