package ru.alexander;

public abstract class IterativeTracker {
    private double cx;
    private double cy;
    private double cz;

    protected double[][] data;

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
            if (err > pe && Math.random() > Math.exp((pe - err) * it / startT)) {
                cx = px;
                cy = py;
                cz = pz;
                err = pe;
            }
            it++;
        } while (it < maxIterations);
        System.out.printf("Error: %1.3f\n", err);
        return true;
    }

    private double[] sphereSphereIntersection(double[] s1, double[] s2) {
        double dx = s2[1] - s1[1];
        double dy = s2[2] - s1[2];
        double dz = s2[3] - s1[3];

        double l = Math.sqrt(dx * dx + dy * dy + dz * dz);
        double r1sqr = s1[0] * s1[0];

        double a = (l * l + r1sqr - s2[0] * s2[0]) / (2 * l);
        double t = a / l;
        double r0 = Math.sqrt(r1sqr - a * a);

        double x0 = s1[1] + dx * t;
        double y0 = s1[2] + dy * t;
        double z0 = s1[3] + dz * t;

        dx /= l;
        dy /= l;
        dz /= l;
        double v1x = 0, v1y = 0, v1z = 0;

        if (dx != 0 || dy != 0) {
            v1x = dy;
            v1y = -dx;
            v1z = 0;
        } else if (dz != 0) {
            v1x = dz;
            v1y = 0;
            v1z = -dx;
        }
        double v2x = dy * v1z - dz * v1y;
        double v2y = dz * v1x - dx * v1z;
        double v2z = dx * v1y - dy * v1x;
        return new double[] {
                r0, x0, y0, z0,
                v1x, v1y, v1z,
                v2x, v2y, v2z
        };
    }
    private double[][] circleSphereIntersection(double[] c, double[] s) {
        double nx = c[5] * c[9] - c[6] * c[8];
        double ny = c[6] * c[7] - c[4] * c[9];
        double nz = c[4] * c[8] - c[5] * c[7];

        double lsqr = nx * nx + ny * ny + nz * nz;
        double t = (nx * (c[1] - s[1]) + ny * (c[2] - s[2]) + nz * (c[3] - s[3])) / lsqr;

        double dx = t * nx;
        double dy = t * ny;
        double dz = t * nz;

        double l = Math.sqrt(lsqr);
        nx /= l;
        ny /= l;
        nz /= l;

        double r2 = Math.sqrt(s[0] * s[0] - (dx * dx + dy * dy + dz * dz));

        double x2 = s[1] + dx;
        double y2 = s[2] + dy;
        double z2 = s[3] + dz;

        dx = x2 - c[1];
        dy = y2 - c[2];
        dz = z2 - c[3];

        l = Math.sqrt(dx * dx + dy * dy + dz * dz);
        double a = (l * l + c[0] * c[0] - r2 * r2) / (2 * l);

        dx /= l;
        dy /= l;
        dz /= l;

        double ux = dy * nz - dz * ny;
        double uy = dz * nx - dx * nz;
        double uz = dx * ny - dy * nx;

        l = Math.sqrt(ux * ux + uy * uy + uz * uz);
        ux /= l;
        uy /= l;
        uz /= l;

        double cos = a / c[0];
        double sin = Math.sqrt(1 - cos * cos);

        double x0 = c[1] + c[0] * (dx * cos + ux * sin);
        double y0 = c[2] + c[0] * (dy * cos + uy * sin);
        double z0 = c[3] + c[0] * (dz * cos + uz * sin);

        double x1 = c[1] + c[0] * (dx * cos - ux * sin);
        double y1 = c[2] + c[0] * (dy * cos - uy * sin);
        double z1 = c[3] + c[0] * (dz * cos - uz * sin);

        return new double[][] {
                { x0, y0, z0 },
                { x1, y1, z1 }};
    }
    private int nearestToSphere(double[] p1, double[] p2, double[] s) {
        double rsqr = s[0] * s[0];
        double dst1 = Math.pow(s[1] - p1[0], 2) + Math.pow(s[2] - p1[1], 2) + Math.pow(s[3] - p1[2], 2);
        double err1 = Math.abs(dst1 - rsqr);

        double dst2 = Math.pow(s[1] - p2[0], 2) + Math.pow(s[2] - p2[1], 2) + Math.pow(s[3] - p2[2], 2);
        double err2 = Math.abs(dst2 - rsqr);

        if (err1 < err2) return 0;
        else return 1;
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
}
