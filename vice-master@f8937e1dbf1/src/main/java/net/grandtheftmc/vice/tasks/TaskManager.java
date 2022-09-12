package net.grandtheftmc.vice.tasks;

import net.grandtheftmc.vice.Vice;

public class TaskManager {
    private PlayerTask playerTask;
    private GlassesTask glassesTask;

    public TaskManager() {
        this.startTasks();
    }

    private void startTasks() {
        this.playerTask = new PlayerTask();
        this.playerTask.runTaskTimer(Vice.getInstance(), 20, 20);
        this.glassesTask = new GlassesTask();
        this.glassesTask.runTaskTimer(Vice.getInstance(), 100, 100);
    }

    public PlayerTask getPlayerTask() {
        return this.playerTask;
    }

}
