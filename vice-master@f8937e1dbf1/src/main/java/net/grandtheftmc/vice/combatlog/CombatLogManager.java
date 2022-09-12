package net.grandtheftmc.vice.combatlog;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.users.User;
import net.grandtheftmc.core.util.Utils;
import net.grandtheftmc.vice.Vice;
import net.grandtheftmc.vice.ViceUtils;
import net.grandtheftmc.vice.combatlog.task.DespawnTask;
import net.grandtheftmc.vice.users.ViceUser;
import net.grandtheftmc.vice.users.storage.BooleanStorageType;
import net.grandtheftmc.vice.world.ViceSelection;
import net.grandtheftmc.vice.world.ZoneFlag;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.NPC;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.*;

/**
 * Created by Timothy Lampen on 2017-08-14.
 */
public class CombatLogManager {
    private HashMap<NPC, CombatLogger> combatNPCs = new HashMap<>();
    private List<CombatLogger> destroyedNPCs = new ArrayList<>();


    public void spawnNPC(Player player, boolean fromSpawn) {
        spawnNPC(player, fromSpawn, false);
    }

    public void spawnNPC(Player player, boolean fromSpawn, boolean overrideKickCheck) {
        ViceUser viceUser = Vice.getUserManager().getLoadedUser(player.getUniqueId());
        User user  = Core.getUserManager().getLoadedUser(player.getUniqueId());
        if(fromSpawn) {
            List<ViceSelection> selections = Vice.getWorldManager().getZones(player.getLocation());
            if(selections.stream().anyMatch(selection -> selection.getFlags().contains(ZoneFlag.COP_CANT_ARREST)))
                return;
        }
        if(!overrideKickCheck && viceUser.getBooleanFromStorage(BooleanStorageType.KICKED)) {
            return;
        }
        List<ItemStack> contents = new ArrayList<>();
        for(ItemStack is : player.getInventory().getContents()) {
            if(is==null || is.getType()== Material.AIR || is.getType()==Material.WATCH)
                continue;
            if(ViceUtils.isDefaultPlayerItem(is))
                continue;
            if(fromSpawn/* && (Vice.getItemManager().getItem(is)==null || !Vice.getItemManager().getItem(is).isScheduled())*/)//don't spawn an npc if the player is at spawn.
                continue;
            contents.add(is);
        }
        if(contents.size()==0) {
            return;
        }
        NPC npc = (NPC) player.getWorld().spawnEntity(player.getLocation(), EntityType.VILLAGER);
        npc.setCustomName(Utils.f(user.getUserRank().getColor() + player.getName() + "&7's Combat NPC &e15&7)"));
        npc.setCustomNameVisible(true);
        npc.setAI(false);
        npc.setMetadata("loggedplayer", new FixedMetadataValue(Vice.getInstance(), player.getUniqueId().toString()));
        combatNPCs.put(npc, new CombatLogger(player.getUniqueId(), contents, fromSpawn));
        new DespawnTask(npc, Utils.f(user.getUserRank().getColor() + player.getName())).runTaskTimer(Vice.getInstance(), 0, 20);
    }

    public NPC getSpawnedNPCFromPlayer(UUID uuid){
        Optional<Map.Entry<NPC, CombatLogger>> log = this.combatNPCs.entrySet().stream().filter(entry -> entry.getValue().getUUID().equals(uuid)).findFirst();
        return log.map(Map.Entry::getKey).orElse(null);
    }

    public void removeNPC(NPC villager) {
        if(combatNPCs.containsKey(villager))
            combatNPCs.remove(villager);
    }

    public List<ItemStack> getPlayerInventory(NPC npc){
        if(this.combatNPCs.containsKey(npc))
            return this.combatNPCs.get(npc).getContents();
        return null;
    }

    public void addDestroyedNPC(NPC npc){
        this.destroyedNPCs.add(this.combatNPCs.get(npc));
    }

    public void clearRemovedItems(UUID uuid) {
        Optional<CombatLogger> log = this.destroyedNPCs.stream().filter(logger -> logger.getUUID().equals(uuid)).findFirst();
        log.ifPresent(combatLogger -> this.destroyedNPCs.remove(combatLogger));
    }

    public Optional<CombatLogger> getDestroyedCombatLogger(UUID uuid){
        return this.destroyedNPCs.stream().filter(log -> log.getUUID().equals(uuid)).findFirst();
    }

    public void load(){
        YamlConfiguration config = Vice.getSettings().getPlayerCacheConfig();
        if(config.getConfigurationSection("")==null)
            return;
        for(String sUUID : config.getConfigurationSection("").getKeys(false)) {
            if(config.contains(sUUID + ".destroyed-npcs")) {
                this.destroyedNPCs.add(new CombatLogger(UUID.fromString(sUUID), null, config.getBoolean(sUUID + ".destroyed-npcs")));
            }
        }
    }

    public void save(){
        combatNPCs.keySet().forEach(Entity::remove);

        YamlConfiguration config = Vice.getSettings().getPlayerCacheConfig();
        if(config.getConfigurationSection("")!=null) {
            for (String sUUID : config.getConfigurationSection("").getKeys(false)) {
                config.set(sUUID + ".destroyed-npcs", null);
            }
        }
        Utils.saveConfig(config, "playercache");
        for(CombatLogger log : destroyedNPCs){
            config.set(log.getUUID().toString() + ".destroyed-npcs", log.isFromSpawn());
        }
        Utils.saveConfig(config, "playercache");
    }
    
    public Set<NPC> getCombatLogNPCs() {
        return this.combatNPCs.keySet();
    }
}
