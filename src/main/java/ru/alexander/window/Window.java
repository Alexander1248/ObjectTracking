package ru.alexander.window;

import ru.alexander.Source;
import ru.alexander.Tracker;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseWheelEvent;
import java.awt.image.BufferStrategy;

public class Window extends JFrame {
    private double x = 0;
    private double y = 0;
    private double scale = 30;


    private Source[] sources;
    private Tracker[] trackers;
    private int sx, sy;
    private boolean drag;
    private Object selected;

    private long textTime;
    private String msg;
    private int mx, my;

    private double gridScale = 0;
    private double precision = 5;


    private final BufferStrategy bufferStrategy;

    public Window(Source[] sources, Tracker[] trackers) throws HeadlessException {
        super("Tracker");
        this.sources = sources;
        this.trackers = trackers;
        setSize(1280, 720);
        setResizable(false);
        setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);


        setIgnoreRepaint(true);
        createBufferStrategy(2);
        bufferStrategy = getBufferStrategy();
        repaint();

        WindowMenu windowMenu = new WindowMenu(Window.this);

        addMouseWheelListener(new MouseAdapter() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                if (selected == null) {
                    scale -= e.getPreciseWheelRotation() * 0.5;
                    if (scale < 10) {
                        scale = 10;
                        return;
                    }
                    if (scale > 100) {
                        scale = 100;
                        return;
                    }
                    msg = String.format("Scale: %1.1f", scale);
                    mx = 20;
                    my = 50;
                }
                else {
                    if (selected.getClass() == Source.class) {
                        Source source = (Source) selected;
                        source.z -= e.getPreciseWheelRotation() * 0.1;
                        msg = String.format("Z: %1.1f", ((Source) selected).z);
                    }
                    else {
                        Tracker tracker = (Tracker) selected;
                        tracker.z -= e.getPreciseWheelRotation() * 0.1;
                        msg = String.format("Z: %1.1f", ((Tracker) selected).z);
                    }
                    mx = sx;
                    my = (int) (sy + scale);
                }
                textTime = System.currentTimeMillis() + 1000;
                repaint();
            }
        });

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                sx = e.getX();
                sy = e.getY();
                for (int i = 0; i < Window.this.trackers.length; i++) {
                    if (Math.sqrt(Math.pow(Window.this.trackers[i].x * scale + x - sx, 2)
                            + Math.pow(Window.this.trackers[i].y * scale + y - sy, 2)) < 0.7 * scale) {
                        selected = Window.this.trackers[i];
                        break;
                    }
                }
                if (selected == null) {
                    for (int i = 0; i < Window.this.sources.length; i++) {
                        if (Math.sqrt(Math.pow(Window.this.sources[i].x * scale + x - sx, 2)
                                + Math.pow(Window.this.sources[i].y * scale + y - sy, 2)) < 0.7 * scale) {
                            selected = Window.this.sources[i];
                            break;
                        }
                    }
                }

                if (e.getButton() == MouseEvent.BUTTON1) {
                    drag = true;
                }
                else {
                    if (e.getButton() == MouseEvent.BUTTON3) {
                        if (selected == null) {
                            windowMenu.show(e.getComponent(), sx, sy);
                            windowMenu.x = sx;
                            windowMenu.y = sy;
                        } else {
                            ObjectMenu menu = new ObjectMenu(Window.this, selected);
                            menu.show(e.getComponent(), sx, sy);
                        }
                    }
                    selected = null;
                    drag = false;
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                selected = null;
                drag = false;
            }
        });
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (drag) {
                    if (selected == null) {
                        x += scale * (e.getX() - sx) * 0.02;
                        y += scale * (e.getY() - sy) * 0.02;
                    } else {
                        if (selected.getClass() == Source.class) {
                            Source source = (Source) selected;
                            if (gridScale == 0) {
                                source.x = (sx - x) / scale;
                                source.y = (sy - y) / scale;
                            }
                            else {
                                source.x = Math.round((sx - x) / (scale * gridScale)) * gridScale;
                                source.y = Math.round((sy - y) / (scale * gridScale)) * gridScale;
                            }
                        } else {
                            Tracker tracker = (Tracker) selected;
                            if (gridScale == 0) {
                                tracker.x = (sx - x) / scale;
                                tracker.y = (sy - y) / scale;
                            }
                            else {
                                tracker.x = Math.round((sx - x) / (scale * gridScale)) * gridScale;
                                tracker.y = Math.round((sy - y) / (scale * gridScale)) * gridScale;
                            }
                        }
                    }
                    sx = e.getX();
                    sy = e.getY();
                    repaint();
                }
            }
        });
    }

    @Override
    public void paint(Graphics graphics) {
        Graphics g = bufferStrategy.getDrawGraphics();
        super.paint(g);

        if (System.currentTimeMillis() < textTime) g.drawString(msg, mx, my);


        g.setColor(Color.BLUE);
        for (int i = 0; i < sources.length; i++) {
            g.fillOval((int) ((sources[i].x - 0.35) * scale + x),
                    (int) ((sources[i].y - 0.35) * scale + y),
                    (int) (0.7 * scale), (int) (0.7 * scale));
        }

        for (int i = 0; i < trackers.length; i++) {
            g.setColor(Color.ORANGE);
            g.fillOval((int) ((trackers[i].x - 0.35) * scale + x),
                    (int) ((trackers[i].y - 0.35) * scale + y),
                    (int) (0.7 * scale), (int) (0.7 * scale));

            trackers[i].trackSignals(sources);
            trackers[i].locate();

            int rc = 0, gc = 0, bc = 0;
            if (Math.abs(trackers[i].getCZ() - trackers[i].z) > precision) rc = 255;
            if (Math.abs(trackers[i].getCY() - trackers[i].y) > precision) gc = 255;
            if (Math.abs(trackers[i].getCX() - trackers[i].x) > precision) bc = 255;
            g.setColor(new Color(rc, gc, bc));
            g.fillOval((int) ((trackers[i].getCX() - 0.15) * scale + x),
                    (int) ((trackers[i].getCY() - 0.15) * scale + y),
                    (int) (0.3 * scale), (int) (0.3 * scale));
        }
        g.dispose();
        bufferStrategy.show();
    }

    private static class ObjectMenu extends JPopupMenu {
        public ObjectMenu(Window window, Object object) {
            JMenuItem delete = new JMenuItem("Delete");
            delete.addActionListener(e -> {
                if (object.getClass() == Source.class) {
                    Source source = (Source) object;
                    Source[] sources = window.sources;
                    Source[] buff = new Source[sources.length - 1];
                    for (int i = 0; i < sources.length - 1; i++) {
                        if (sources[i] != source) buff[i] = sources[i];
                        else {
                            System.arraycopy(sources, i + 1, buff, i, sources.length - 1 - i);
                            break;
                        }
                    }
                    window.sources = buff;

                } else {
                    Tracker tracker = (Tracker) object;
                    Tracker[] trackers = window.trackers;
                    Tracker[] buff = new Tracker[trackers.length - 1];
                    for (int i = 0; i < trackers.length - 1; i++) {
                        if (trackers[i] != tracker) buff[i] = trackers[i];
                        else {
                            System.arraycopy(trackers, i + 1, buff, i, trackers.length - 1 - i);
                            break;
                        }
                    }
                    window.trackers = buff;
                }
                window.repaint();
            });
            add(delete);
        }
    }
    private static class WindowMenu extends JPopupMenu {
        public int x, y;
        public WindowMenu(Window window) {

            JMenuItem setSpeed = new JMenuItem("Set Speed");
            setSpeed.addActionListener(e -> {
                String s = JOptionPane.showInputDialog("Set signal speed:", Source.signalSpeed);
                if (s != null) Source.signalSpeed = Double.parseDouble(s);
                getComponent().repaint();
            });
            add(setSpeed);

            JMenuItem setNoise = new JMenuItem("Set Noisiness");
            setNoise.addActionListener(e -> {
                String s = JOptionPane.showInputDialog("Set noisiness:", Source.signalNoiseFactor);
                if (s != null) Source.signalNoiseFactor = Double.parseDouble(s);
                getComponent().repaint();
            });
            add(setNoise);
            JMenuItem setPrecision = new JMenuItem("Set Precision");
            setPrecision.addActionListener(e -> {
                String s = JOptionPane.showInputDialog("Set precision:", window.precision);
                if (s != null) window.precision = Double.parseDouble(s);
                getComponent().repaint();
            });
            add(setPrecision);

            JMenuItem addSource = new JMenuItem("Add Source");
            addSource.addActionListener(e -> {
                int length = window.sources.length;
                Source[] sources = new Source[length + 1];
                System.arraycopy(window.sources, 0, sources, 0, length);
                sources[length] = new Source(
                        (x - window.x) / window.scale,
                        (y - window.y) / window.scale, 0);
                window.sources = sources;
                window.repaint();
            });
            add(addSource);

            JMenuItem addTracker = new JMenuItem("Add Tracker");
            addTracker.addActionListener(e -> {
                int length = window.trackers.length;
                Tracker[] trackers = new Tracker[length + 1];
                System.arraycopy(window.trackers, 0, trackers, 0, length);
                trackers[length] = new Tracker();
                trackers[length].x = (x - window.x) / window.scale;
                trackers[length].y = (y - window.y) / window.scale;
                window.trackers = trackers;
                window.repaint();
            });
            add(addTracker);

            JCheckBoxMenuItem autoUpdate = new JCheckBoxMenuItem("Auto Update");
            autoUpdate.addActionListener(e -> new Thread(() -> {
                while (autoUpdate.getState()) window.repaint();
            }).start());
            add(autoUpdate);

            JCheckBoxMenuItem stg = new JCheckBoxMenuItem("Snap To Grid");
            stg.addActionListener(e -> {
                if (stg.getState()) {
                    String gridSize = JOptionPane.showInputDialog("Set Grid Size", 1);
                    if (gridSize == null) gridSize = "1.0";
                    window.gridScale = Double.parseDouble(gridSize);
                }
                else window.gridScale = 0;
            });
            add(stg);
        }

    }
}
