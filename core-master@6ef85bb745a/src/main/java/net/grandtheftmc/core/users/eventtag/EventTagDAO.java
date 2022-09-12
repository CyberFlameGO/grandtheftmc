package net.grandtheftmc.core.users.eventtag;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.Lang;
import net.grandtheftmc.core.database.BaseDatabase;
import net.grandtheftmc.core.nametags.NametagManager;
import net.grandtheftmc.core.users.User;
import net.grandtheftmc.core.users.UserManager;
import net.grandtheftmc.core.util.ServerUtil;

/**
 * Created by Timothy Lampen on 1/7/2018.
 */
public class EventTagDAO {

    private static final HashMap<EventTag, TagVisibility> VISIBILITY = new HashMap<>();

    /*
     * Everything is static to maintain the pattern of DAO classes.
     * Caches the results from the mysql for 15min, then updates.
     */
    static {
        new BukkitRunnable() {
            @Override
            public void run() {
                refreshTagVisiblity();
            }
        }.runTaskTimerAsynchronously(Core.getInstance(), 0, 20 * 60 * 15);
    }

    /**
     * @param tag - the tag that is being checked for visiblity
     * @return the visibility of the tag according to the cached results.
     */
    public static TagVisibility getTagVisibility(EventTag tag) {
        if (VISIBILITY.containsKey(tag))
            return VISIBILITY.get(tag);

        //the tag was added to the plugin, but not to the actual database, so update the database with an entry.
        ServerUtil.runTaskAsync(() -> {
            addTagToDatabase(tag);//finally add it.
        });
        return TagVisibility.EVERYONE;
    }

    public static void setVisibility(EventTag tag, TagVisibility visibility) {
        String query = "UPDATE tag_visibility set visibility = ? WHERE tag = ?";
        try (Connection c = BaseDatabase.getInstance().getConnection()) {
            try (PreparedStatement statement = c.prepareStatement(query)) {
                statement.setInt(1, visibility.getID());
                statement.setString(2, tag.toString());
                statement.execute();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param tag - the tag that is not currently in the database but must be added.
     */
    private static void addTagToDatabase(EventTag tag) {
        String query = "INSERT IGNORE INTO tag_visibility(tag,visibility) VALUES (?,?);";
        try (Connection c = BaseDatabase.getInstance().getConnection()) {
            try (PreparedStatement statement = c.prepareStatement(query)) {
                statement.setString(1, tag.toString());
                statement.setInt(2, TagVisibility.NO_ONE.getID());
                statement.execute();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * @apiNote refreshes the cache of tag visibility.
     */
    public static void refreshTagVisiblity() {
        String query = "SELECT * FROM tag_visibility;";
        try (Connection c = BaseDatabase.getInstance().getConnection()) {
            try (PreparedStatement statement = c.prepareStatement(query)) {
                try (ResultSet set = statement.executeQuery()) {
                    HashMap<EventTag, TagVisibility> copy = new HashMap<>();
                    while (set.next()) {
                        EventTag tag = EventTag.valueOf(set.getString("tag"));
                        TagVisibility v = TagVisibility.fromID(set.getInt("visibility"));
                        copy.put(tag, v);
                    }
                    
                    // get back on sync
                    ServerUtil.runTask(() -> {
                        VISIBILITY.clear();
                        VISIBILITY.putAll(copy);
                        
                        // for all players
                        for (Player player : Bukkit.getOnlinePlayers()) {
                        	
                            User user = UserManager.getInstance().getUser(player.getUniqueId()).orElse(null);
                            if (user != null){
                                if (user.getEquipedTag() == null)
                                    continue;
                                if (getTagVisibility(user.getEquipedTag()) == TagVisibility.NO_ONE) {
                                    user.setEquipedTag(null);
                                    user.updateDisplayName(player);
                                    NametagManager.updateNametagsTo(player, user);
                                    NametagManager.updateNametag(player);
                                    player.sendMessage(Lang.REWARDS.f("&cYour current event tag was dequiped because it was disabled."));
                                }
                            }
                        }
                    });
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
