package net.grandtheftmc.gtm.tasks;

import net.grandtheftmc.gtm.GTM;

public class TaskManager {
    private PlayerTask playerTask;
    private GlassesTask glassesTask;

    public TaskManager() {
        this.startTasks();
    }

    private void startTasks() {
        this.playerTask = new PlayerTask();
        this.playerTask.runTaskTimer(GTM.getInstance(), 20, 20);
        this.glassesTask = new GlassesTask();
        this.glassesTask.runTaskTimer(GTM.getInstance(), 100, 100);
    }

    public PlayerTask getPlayerTask() {
        return this.playerTask;
    }

}
