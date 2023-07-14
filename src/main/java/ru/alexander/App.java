package ru.alexander;


import ru.alexander.window.Window;

public class App {
    public static void main(String[] args) {

        Source.signalNoiseFactor = 5e-11;
//        Source.signalSpeed = 330;
        Source[] sources = {
                new Source(0, 0, 7),
                new Source(30, 0, 8),
                new Source(0, 30, 9),
                new Source(30, 30, 10)
        };
        Tracker[] trackers = { new Tracker() };
        trackers[0].x = 15;
        trackers[0].y = 15;
        trackers[0].z = 5;
        Window window = new Window(sources, trackers);
    }
}
