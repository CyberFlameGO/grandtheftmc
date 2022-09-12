package net.grandtheftmc.core.leaderboards;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.util.Utils;
import net.grandtheftmc.core.util.Component;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class LeaderBoardManager implements Component<LeaderBoardManager, Core> {

    private int taskId = -1;

    private final List<LeaderBoard> leaderBoards = new ArrayList<>();

    public LeaderBoardManager() {
        if (!Core.getSettings().useHolographicDisplays())
            return;
        //this.loadLeaderBoards();
        //this.startSchedule();
    }

    @Override
    public LeaderBoardManager onDisable(Core plugin) {
        if(this.leaderBoards==null || this.leaderBoards.size()==0)
            return this;
        this.leaderBoards.forEach(LeaderBoard::delete);
        this.leaderBoards.clear();
        return this;
    }

    private void startSchedule() {
        /*
        if (this.taskId > 0)
            Bukkit.getScheduler().cancelTask(this.taskId);
        this.taskId = new BukkitRunnable() {
            @Override
            public void run() {
                LeaderBoardManager.this.leaderBoards.forEach(LeaderBoard::update);
            }
        }.runTaskTimerAsynchronously(Core.getInstance(), 100, 100).getTaskId();*/
    }

    public void loadLeaderBoards() {
        YamlConfiguration c = Core.getSettings().getLeaderBoardsConfig();
        this.leaderBoards.addAll(c.getKeys(false).stream().map(name -> new LeaderBoard(name, c.getString(name + ".displayName"), c.getString(name + ".table"),
                c.getString(name + ".column"), Utils.teleportLocationFromString(c.getString(name + ".location")),
                c.getInt(name + ".lines"))).collect(Collectors.toList()));
    }

    public void saveLeaderBoards(boolean shutdown) {
        YamlConfiguration c = Core.getSettings().getLeaderBoardsConfig();
        for (String s : c.getKeys(false))
            c.set(s, null);
        for (LeaderBoard board : this.leaderBoards) {
            String name = board.getName();
            c.set(name + ".displayName", board.getDisplayName());
            c.set(name + ".table", board.getTable());
            c.set(name + ".column", board.getColumn());
            c.set(name + ".location", Utils.teleportLocationToString(board.getLocation()));
        }
        Utils.saveConfig(c, "leaderBoards");
    }

}
