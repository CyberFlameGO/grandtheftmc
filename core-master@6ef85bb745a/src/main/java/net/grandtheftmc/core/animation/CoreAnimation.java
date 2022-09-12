package net.grandtheftmc.core.animation;

import com.avaje.ebeaninternal.server.el.ElSetValue;
import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.animation.event.AnimationFinishEvent;
import net.grandtheftmc.core.animation.step.AStep;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

public class CoreAnimation implements Animation {

    private final AStep[] aSteps;
    private final boolean async;

    private int size = 0, current = 0;
    private BukkitRunnable task = null;

    /**
     * Construct a new Animation.
     *
     * @param steps
     */
    public CoreAnimation(boolean async, AStep... steps) {
        this.async = async;
        this.aSteps = steps;
        this.size = steps.length;
    }

    /**
     * Construct a new Animation.
     */
    public CoreAnimation(boolean async, int steps) {
        this.async = async;
        this.aSteps = new AStep[steps];
    }

    @Override
    public AStep[] getSteps() {
        return this.aSteps;
    }

    @Override
    public boolean isAsynchronous() {
        return this.async;
    }

    @Override
    public void addStep(AStep step) {
        this.aSteps[this.size++] = step;
    }

    @Override
    public void start() {
        if(this.task != null) return;

        this.task = new BukkitRunnable() {
            private long ticks = 0;
            @Override public void run() {
                AStep step = aSteps[current];
                if(ticks >= step.getEndingTime()) {
                    if(current >= (aSteps.length - 1)) {
                        ticks = 0;
                        stop();

                        AnimationFinishEvent event = new AnimationFinishEvent(CoreAnimation.this);
                        Bukkit.getPluginManager().callEvent(event);
                        return;
                    }

                    step.getEnd().end();
                    aSteps[current++].getStart().start();
                }
            }
        };

        if(this.async) this.task.runTaskTimerAsynchronously(Core.getInstance(), 0L, 1L);
        else this.task.runTaskTimer(Core.getInstance(), 0L, 1L);
    }

    /**
     * This method will stop all animations.
     */
    @Override
    public void stop() {
        if(this.task == null) return;
        this.task.cancel();

        this.aSteps[this.current].getEnd().end();
        current = 0;

        this.task = null;
    }
}
