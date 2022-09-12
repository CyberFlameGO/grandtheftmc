package net.grandtheftmc.core.voting.crates;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.users.User;
import net.grandtheftmc.core.util.Component;
import net.grandtheftmc.core.util.Utils;
import net.grandtheftmc.core.util.WeightedRandomCollection;
import net.grandtheftmc.core.voting.RewardPack;
import net.grandtheftmc.core.voting.crates.events.CrateNearbyPlayerEvent;


/**
 * Created by Liam on 25/04/2017.
 */
public class CrateManager implements Component<CrateManager, Core> {

    private final Set<Crate> crates = new HashSet<>();
    private final Map<CrateStars, List<CrateReward>> rewards = new HashMap<>();
    private final Map<CrateStars, Double> totalWeights = new HashMap<>();

    private int taskId = -1;

    // TODO crate locations, etc etc

    //    public CrateManager() {
    //        this.load();
    //    }

    public void load() {
        this.loadRewards();
        this.loadCrates();
        this.startSchedule();
    }

    /**
     * This will run when the plugin (T) is enabled.
     *
     * @param plugin The JavaPlugin
     */
    @Override
    public CrateManager onEnable(Core plugin) {
        World world = Bukkit.getWorld("spawn");
        if (world == null) return this;
        Core.log("Removing ArmorStand Crate Entities");

        world.getEntitiesByClass(ArmorStand.class).stream().filter(ent -> ent.getHelmet() != null && ent.getHelmet().getType() == Material.FLINT_AND_STEEL && ent.hasMetadata("CRATE")).forEach(entity -> {
            entity.getNearbyEntities(2, 2, 2).stream().filter(nearby -> nearby instanceof ArmorStand).forEach(Entity::remove);
            entity.remove();
        });

        load();
        return this;
    }

    /**
     * This will run when the plugin (T) is disabling.
     *
     * @param plugin The JavaPlugin
     */
    @Override
    public CrateManager onDisable(Core plugin) {
        World world = Bukkit.getWorld("spawn");
        if (world == null) return this;

        Core.log("Removing ArmorStand Crate Entities.");
        if (!this.crates.isEmpty()) {
            this.crates.forEach(crate -> {

                if (crate.isBeingOpened()) {

                    switch (crate.getCrateStars().getStars()){
                        case 1:
                            crate.getLocation().getWorld().getNearbyEntities(crate.getLocation(), 1, 3, 1).stream().filter(ent -> ent instanceof Item).forEach(item -> crate.resetAnimationFirst((Item) item));
                            break;
                        case 2:
                            crate.getLocation().getWorld().getNearbyEntities(crate.getLocation(), 1, 3, 1).stream().filter(ent -> ent instanceof Item).forEach(item -> crate.resetAnimationSecond((Item) item));
                            break;
                        case 3:
                            Bukkit.getOnlinePlayers().forEach(crate::resetCrateThird);
                            break;
                        case 4:
                            Bukkit.getOnlinePlayers().forEach(crate::resetCrateFourth);
                            break;
                        case 5:
                            Bukkit.getOnlinePlayers().forEach(crate::resetCrateFifth);
                            break;
                        case 6:
                            Bukkit.getOnlinePlayers().forEach(crate::resetCrateThird);
                            break;
                    }
                }

                crate.destroy();
            });

            this.crates.clear();
        }

        if (!this.rewards.isEmpty()) this.rewards.clear();
        if (!this.totalWeights.isEmpty()) this.totalWeights.clear();
        return this;
    }

    public void loadCrates() {
        Core.log("Loading crates...");
        YamlConfiguration c = Core.getSettings().getCratesConfig();
        for (Crate crate : this.crates) crate.destroy();
        this.crates.clear();

        for (String s : c.getKeys(false)) {
            try {
                Location loc = Utils.teleportLocationFromString(c.getString(s + ".loc"));
                for(Entity en : loc.getWorld().getEntities()) {
                    if(en instanceof ArmorStand && en.getLocation().distance(loc) < 2)
                        en.remove();
                }

                int stars = c.getInt(s + ".stars");
                this.crates.add(new Crate(loc, stars));
            } catch (Exception e) {
                Core.error("There was an error while loading Crate with id " + s);
                e.printStackTrace();
            }
        }
        Core.log("Finished loading crates!");
    }

    public void loadRewards() {
        YamlConfiguration c = Core.getSettings().getCrateRewardsConfig();
        this.rewards.clear();
        for (String s : c.getKeys(false)) {
            CrateStars stars;
            double totalWeight = 0;
            try {
                stars = CrateStars.getCrateStars(Integer.parseInt(s));
            } catch (NumberFormatException e) {
                Core.error("There was an error while loading Crate Rewards for crate with " + s + " star(s)");
                e.printStackTrace();
                continue;
            }
            List<CrateReward> packs = new ArrayList<>();
            for (String name : c.getConfigurationSection(s).getKeys(false)) {
                try {
                    String item = c.getString(s + '.' + name + ".item");
                    double weight = c.get(s + '.' + name + ".weight") == null ? 1 : c.getDouble(s + '.' + name + ".weight");
                    boolean announce = c.get(s + '.' + name + ".announce") != null && c.getBoolean(s + '.' + name + ".announce");
                    RewardPack pack = Core.getVoteManager().getRewardPack(c, name, s + '.' + name);
                    if (pack == null) Core.error("Error while loading RewardPack for crate reward: " + name);
                    else {
                        packs.add(new CrateReward(pack, item, weight, announce));
                        totalWeight += weight;
                    }
                } catch (Exception e) {
                    Core.error("There was an error while loading Crate Reward " + name + " for crate with " + s + " star(s)");
                    e.printStackTrace();
                }
            }
            this.totalWeights.put(stars, totalWeight);
            this.rewards.put(stars, packs);
        }
    }

    public void save(boolean shutdown) {
        YamlConfiguration c = Core.getSettings().getCratesConfig();
        for (String s : c.getKeys(false)) c.set(s, null);
        int i = 0;
        for (Crate crate : this.crates) {
            c.set(i + ".loc", Utils.teleportLocationToString(crate.getLocation()));
            c.set(i + ".stars", crate.getCrateStars().getStars());
            i++;
        }
        Utils.saveConfig(c, "crates");

        if (shutdown) {
            crates.forEach(Crate::destroy);
        }
    }

    public CrateReward determineCrateReward(Player player, User user, CrateStars rank) {
        WeightedRandomCollection<CrateReward> rewards = new WeightedRandomCollection<>();
        this.getRewards(rank).stream().filter(r -> !r.getRewardPack().hasAllRewards(player, user)).collect(Collectors.toList()).forEach(r -> rewards.add(r.getWeight(), r));
        
        return rewards.next();
    }

    public Optional<Crate> getCrate(LivingEntity entity) {
        return entity == null ? null : this.crates.stream().filter(crate -> Objects.equals(crate.getLocation(), entity.getLocation())).findFirst();
    }

    public void addCrate(Crate crate) {
        this.crates.add(crate);
    }

    public boolean removeCrate(Crate crate) {
        if (this.crates.contains(crate)) {
            crate.destroy();
            this.crates.remove(crate);
            return true;
        }
        return false;
    }

    public void startSchedule() {
        if (this.taskId != -1) {
            Bukkit.getScheduler().cancelTask(this.taskId);
        }

        this.taskId = new BukkitRunnable() {
            @Override
            public void run() {

                getCrates().forEach(create -> {

                    for (Entity nearby : create.getStand().getNearbyEntities(0.4, 1, 0.4)) {

                        if (nearby instanceof Player) {
                            Player nearbyPlayer = (Player) nearby;

                            CrateNearbyPlayerEvent event = new CrateNearbyPlayerEvent(nearbyPlayer, create);

                            if (event.isCancelled()) {
                                return;
                            }

                            Bukkit.getPluginManager().callEvent(event);
                        }
                    }
                });
            }
        }.runTaskTimerAsynchronously(Core.getInstance(), 0L, 5).getTaskId();
    }

    /**
     * @return the next id of the list of crates( used to remove a crate)
     */
    public int getNextAvaliableCrateID() {
        return this.crates.size();
    }

    public List<CrateReward> getRewards(CrateStars rank) {
        return this.rewards.containsKey(rank) ? this.rewards.get(rank) : new ArrayList<>();
    }

    public Set<Crate> getCrates() {
        return this.crates;
    }

    public double getTotalWeight(CrateStars stars) {
        return this.totalWeights.get(stars);
    }
}
