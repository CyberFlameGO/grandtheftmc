package net.grandtheftmc.core.animation;

import net.grandtheftmc.core.animation.step.AStep;

public interface Animation {

    AStep[] getSteps();

    boolean isAsynchronous();

    void addStep(AStep step);

    void start();

    /**
     * This method will stop all animations.
     */
    void stop();
}
