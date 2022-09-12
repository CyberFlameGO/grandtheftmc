package net.grandtheftmc.vice.listeners;

import net.grandtheftmc.core.util.Component;
import net.grandtheftmc.vice.Vice;

/**
 * Created by Luke Bingham on 03/08/2017.
 */
public class NametagComponent implements Component<NametagComponent, Vice> {

//    private final TagManager<Vice> tagManager;
//
//    public NametagComponent(Vice plugin, TagManager<Vice> tagManager) {
//        this.tagManager = tagManager;
//        Bukkit.getPluginManager().registerEvents(this, plugin);
//    }
//
//    @EventHandler(priority = EventPriority.MONITOR)
//    protected final void onPlayerJoin(PlayerJoinEvent event) {
//        Player player = event.getPlayer();
//        UserRank userRank = Core.getUserManager().getLoadedUser(player.getUniqueId()).getUserRank();
//        ViceRank viceRank = Vice.getUserManager().getLoadedUser(player.getUniqueId()).getRank();
//
//        StringBuilder name = new StringBuilder();
//        name.append(userRank.isHigherThan(UserRank.DEFAULT) ? userRank.getColoredNameBold() + "&r " : "&r");
//        name.append(player.getDisplayName() == null ? player.getName() : player.getDisplayName()).append("&r ");
//        name.append(viceRank.getColoredNameBold() + "&r");
//
//        Tag tag1 = this.tagManager.createTag(NMSVersion.MC_1_12, player, 1, "&c❤&r &c" + player.getHealth(), null);
//        Tag tag2 = this.tagManager.createTag(NMSVersion.MC_1_12, player, 2, name.toString(), tag1);
//
//        FPlayer fPlayer = FPlayers.getInstance().getByPlayer(player);
//        if(fPlayer != null && fPlayer.hasFaction())
//            this.tagManager.createTag(NMSVersion.MC_1_12, player, 3, fPlayer.getFaction().getTag(), tag2);
//
//        this.tagManager.refreshAll(player);
//    }
}
