package fr.ariloxe.arena.tasks;

/**
 * @author Ariloxe
 */
public class ExecutorTask {

    private final String name;
    private final int duration;
    private int currentDuration = 0;
    private final Runnable runnable;
    private boolean repeatable = false;

    public ExecutorTask(String name, int duration, Runnable runnable) {
        this.name = name;
        this.duration = duration;
        this.runnable = runnable;
    }

    public String getName() {
        return name;
    }

    public int getDuration() {
        return duration;
    }

    public void setRepeatable() {
        this.repeatable = true;
    }

    public boolean isRepeatable() {
        return repeatable;
    }

    public Runnable getRunnable() {
        return runnable;
    }

    public int getCurrentDuration() {
        return currentDuration;
    }

    public void setCurrentDuration(int currentDuration) {
        this.currentDuration = currentDuration;
    }
}
