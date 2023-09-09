package ru.alexander;


public class App {
    public static void main(String[] args) {

        SourceImpl.signalNoiseFactor = 5e-11;
//        Source.signalSpeed = 330;
        SourceImpl[] sources = {
                new SourceImpl(0, 0, 7),
                new SourceImpl(30, 0, 8),
                new SourceImpl(0, 30, 9),
                new SourceImpl(30, 30, 10)
        };
        TrackerImpl[] trackers = { new TrackerImpl() };
        trackers[0].x = 15;
        trackers[0].y = 15;
        trackers[0].z = 5;
        Window window = new Window(sources, trackers);
    }
}
