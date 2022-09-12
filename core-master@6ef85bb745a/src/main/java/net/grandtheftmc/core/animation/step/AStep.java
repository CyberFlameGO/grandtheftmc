package net.grandtheftmc.core.animation.step;

public class AStep {

    private final long endingTime;
    private final AStepStart start;
    private final AStepEnd end;

    /**
     * Construct a new Animation Step.
     *
     * @param start - Starting action
     * @param end   - Ending action
     */
    public AStep(long endingTime, AStepStart start, AStepEnd end) {
        this.endingTime = endingTime;
        this.start = start;
        this.end = end;
    }

    public long getEndingTime() {
        return this.endingTime;
    }

    public AStepStart getStart() {
        return this.start;
    }

    public AStepEnd getEnd() {
        return this.end;
    }
}
