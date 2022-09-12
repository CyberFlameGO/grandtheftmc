package net.grandtheftmc.gtm.users.npcs;

import net.citizensnpcs.api.event.NPCCollisionEvent;
import net.citizensnpcs.api.event.NPCLeftClickEvent;
import net.citizensnpcs.api.event.NPCPushEvent;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.menus.MenuManager;
import net.grandtheftmc.core.npc.CoreNPC;
import net.grandtheftmc.core.npc.interfaces.ClickableNPC;
import net.grandtheftmc.core.npc.interfaces.CollideableNPC;
import net.grandtheftmc.gtm.GTM;
import net.grandtheftmc.gtm.users.GTMRank;
import net.grandtheftmc.gtm.users.GTMUser;
import net.grandtheftmc.gtm.users.TaxiTarget;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Created by Timothy Lampen on 1/14/2018.
 */
public class TaxiNPC extends CoreNPC implements ClickableNPC, CollideableNPC {

    public TaxiNPC(Location loc) {
        super(loc, EntityType.PLAYER,  "&e&lGerald Hackney", "&7&oGo to the game world!");
    }

    @Override
    protected void generateNewNPC() {
        setSkin("eyJ0aW1lc3RhbXAiOjE1MTU5NzAzNjYzNDUsInByb2ZpbGVJZCI6ImFkMWM2Yjk1YTA5ODRmNTE4MWJhOTgyMzY0OTllM2JkIiwicHJvZmlsZU5hbWUiOiJGdXJrYW5iejAwIiwic2lnbmF0dXJlUmVxdWlyZWQiOnRydWUsInRleHR1cmVzIjp7IlNLSU4iOnsidXJsIjoiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS9jYzUzZWQyMzJlYWQyNmU4Y2I0NzJiMGFmOGIyMDQyYjdhZjljZDMyOGRlM2M0YTZlNGQzNjNiNDNiZDlmNDUifX19", "hLKOkCuejv8K7bwWS/SKY6jrxJXNojg4eiv1/TPkKJ/1Hc+njUE99IPLCuexppJASlUEMe2815FfDwS4PTzMzgxBeLPdcB/xJR8BZw4FuvodIXfLubmVkxme245u0hRHfVlPLk31l4dyPFEwRMhbSmHacVroB8pebEov6+164p3fUnctqlM48bf6lNfpsbhY46nvqPVRVtv9ljTL6FwBPDvnZL97zTSsUqjKjLUJMTtuHIaAj26Q9+M9y4rP1VMInDWrgfXpEuwz32xy/2HiHHQrGMrNxU9MmshDX1BTJ4UAxmipmj+pJENRSon3GrIgLE7t/yP0Z1ZCcfHFqZtzLHKPXzt+u4jW5hl9bFUU9d9HSUEV0qt+nD68a1yNiWPE2rB0l549v+AZ5D8bktSpvdLy574/uBLLXbM8JJk8g1iFgyeEpQS8TJuHfnyV62KU6lML5+MTt7/zBXlRh2+Vz89Ti0fbZs1g6NdcJWQisCTtVPMBiws9yoitmSsqKk+8/8WYQX7EYXLGilL7gavoBZhlIyP0P8ltTc4oHfcwoOtoZvPivauUv8lHZu18tZpOE1kq28lNuBdytLTWTuJckDzeRwbB8pHCQKB628nRLt2Xp1N57CNnc9XW/3sWY+rKTRHRNw7BvVqoaCAXP4tNo+c7frELxv+CgWMxRcE2g5E=");
        setLookClose(true);
        setCollideable(true);
        setGlowing(ChatColor.GREEN);

        new BukkitRunnable() {
            @Override
            public void run() {
                if (!getNPC().isSpawned()) {
                    this.cancel();
                } else {
                    getStartingLoc().getWorld().spawnParticle(Particle.VILLAGER_HAPPY, getStartingLoc(), 25, 1.2, 1, 1.2);

//                    LivingEntity entity = (LivingEntity) TaxiNPC.this.getNPC().getEntity();
//                    if (entity.hasPotionEffect(PotionEffectType.GLOWING))
//                        entity.removePotionEffect(PotionEffectType.GLOWING);
//
//                    entity.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 20, 0, false, false, colors[i[0]]));
                }
            }
        }.runTaskTimer(GTM.getInstance(), 20, 20);
    }

    @Override
    public void onRightClick(NPCRightClickEvent event) {
        Player player = event.getClicker();

        GTMUser user = GTM.getUserManager().getLoadedUser(player.getUniqueId());
        if (player.isSneaking() || user.getRank() == GTMRank.HOBO) {
            GTM.getWarpManager().warp(
                    player,
                    Core.getUserManager().getLoadedUser(player.getUniqueId()),
                    user,
                    new TaxiTarget(GTM.getWarpManager().getRandomWarp()),
                    0,
                    Core.getUserManager().getLoadedUser(player.getUniqueId()).isPremium() ? 1 : 10
            );

            return;
        }

        MenuManager.openMenu(player, "taxi");
    }

    @Override
    public void onLeftClick(NPCLeftClickEvent event) {
        Player player = event.getClicker();

        GTMUser user = GTM.getUserManager().getLoadedUser(player.getUniqueId());
        if (player.isSneaking() || user.getRank() == GTMRank.HOBO) {
            GTM.getWarpManager().warp(
                    player,
                    Core.getUserManager().getLoadedUser(player.getUniqueId()),
                    user,
                    new TaxiTarget(GTM.getWarpManager().getRandomWarp()),
                    0,
                    Core.getUserManager().getLoadedUser(player.getUniqueId()).isPremium() ? 1 : 10
            );

            return;
        }

        MenuManager.openMenu(player, "taxi");
    }

    @Override
    public void onCollide(NPCCollisionEvent event) {
        
    }

    @Override
    public void onPush(NPCPushEvent event) {
        
    }
}
