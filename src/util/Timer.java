package util;

public class Timer {
    private long nanos = 0L;
    private long tmp;
    boolean running = false;

    public long getNanos() {
        return nanos;
    }
    public long getMillis() {
        return nanos/1_000_000;
    }
    public long getSeconds() {
        return getMillis()/1_000;
    }

    public void start() {
        if(!running) {
            tmp = System.nanoTime();
            running = true;
        }
    }
    public void stop() {
        if(running) {
            nanos += System.nanoTime() - tmp;
            running = false;
        }
    }
}
