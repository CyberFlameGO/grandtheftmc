package net.grandtheftmc.core.leaderboards;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.gmail.filoghost.holographicdisplays.api.line.TextLine;
import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.util.Utils;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LeaderBoard {

    private String name;
    private String displayName;
    private Location location;
    private final int lines;
    private String table;
    private String column;

    private Hologram hologram;
    private final List<TextLine> textLines = new ArrayList<>();

    private final Map<String, Double> cache;

    public LeaderBoard(String name, String displayName, String table, String column, Location location, int lines) {
        this.name = name;
        this.displayName = displayName;
        this.location = location;
        this.lines = lines;
        this.cache = new HashMap<>();
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Location getLocation() {
        return this.location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public Hologram getHologram() {
        return this.hologram;
    }

    public void setHologram(Hologram hologram) {
        this.hologram = hologram;
    }

    public void create() {
        this.hologram = HologramsAPI.createHologram(Core.getInstance(), this.location);
        this.hologram.appendTextLine(Utils.f(this.displayName + " Leaderboard"));
        this.hologram.appendTextLine("");
        this.textLines.clear();
        for (int i = 0; i < this.lines; i++) {
            this.textLines.add(this.hologram.appendTextLine(""));
        }
    }

    protected void delete() {
        this.hologram.delete();
        this.textLines.clear();
    }

    public void update() {
        /*
        try {
            ResultSet rs = Core.getSQL().query(
                    "select name, " + this.column + " from " + this.table + " order by " + this.column + " desc limit " + this.lines + ';');
            this.cache.clear();
            while (rs.next())
                this.cache.put(rs.getString("name"), rs.getDouble(this.column));

        } catch (SQLException e) {
            e.printStackTrace();
        }
        new BukkitRunnable() {
            @Override
            public void run() {
                int i = 0;
                for (Map.Entry<String, Double> stringDoubleEntry : LeaderBoard.this.cache.entrySet()) {
                    LeaderBoard.this.textLines.get(0).setText("&6&l#" + (i + 1) + "&7: &a&l" + stringDoubleEntry.getKey() + "&7 with &a&l" + LeaderBoard.this.cache.get(stringDoubleEntry.getKey()));
                    i++;
                    if (i >= LeaderBoard.this.lines)
                        break;
                }
            }
        }.runTask(Core.getInstance());*/
    }

    public String getTable() {
        return this.table;
    }

    public String getColumn() {
        return this.column;
    }

}