package net.grandtheftmc.gtm.bounties;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.Lang;
import net.grandtheftmc.core.util.Utils;
import net.grandtheftmc.gtm.GTM;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.stream.Collectors;

public class BountyManager {
    private List<Bounty> bounties = new ArrayList<>();

    public BountyManager() {
        this.loadBounties();
        this.startSchedule();
    }

    private void startSchedule() {
        new BukkitRunnable() {
            @Override
            public void run() {
                Collection<? extends Player> players = Bukkit.getOnlinePlayers();
                if (GTM.getBountyManager().getBounties().size() >= 5 || players.isEmpty()) return;
                Random rand = Utils.getRandom();
                int amount = 10000 + (rand.nextInt(91) * 1000);
                Player player = (Player) players.toArray()[rand.nextInt(players.size())];
                Bounty bounty = BountyManager.this.getBounty(player.getUniqueId());
                if (bounty == null) {
                    GTM.getBountyManager().addBounty(new Bounty(player.getUniqueId(), player.getName(), new BountyPlacer(true, amount)));
                    Utils.broadcast(Lang.BOUNTIES.f("&7An anonymous player put a bounty of &a$&l"
                            + amount + "&7 on &a" + player.getName() + "&7."));
                }
            }

        }.runTaskTimer(GTM.getInstance(), 36000, 36000);


    }


    public List<Bounty> getBounties() {
        this.bounties = this.bounties.stream().filter(b -> !b.hasExpired()).collect(Collectors.toList());
        return this.bounties;
    }

    public Set<Bounty> getBountiesByAmount() {
        Map<Bounty, Double> unsortMap = new HashMap<>();
        for (Bounty b : this.getBounties())
            unsortMap.put(b, b.getAmount());
        return this.sort(unsortMap).keySet();
    }

    public Map<Bounty, Double> sort(Map<Bounty, Double> unsortMap) {
        List<Map.Entry<Bounty, Double>> list = new LinkedList<>(unsortMap.entrySet());
        list.sort(Comparator.comparing(Map.Entry::getValue));

        Map<Bounty, Double> sortedMap = new LinkedHashMap<>();
        for (Map.Entry<Bounty, Double> entry : list) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }
        return sortedMap;
    }

    public Bounty getBounty(UUID uuid) {
        return this.bounties.stream().filter(b -> Objects.equals(b.getUuid(), uuid)).findFirst().orElse(null);
    }

    public void loadBounties() {
        YamlConfiguration c = GTM.getSettings().getBountiesConfig();
        this.bounties.clear();
        for (String s : c.getKeys(false)) {
            try {
                UUID uuid = UUID.fromString(s);
                if (uuid == null)
                    continue;
                String name = c.getString(s + ".name");
                long lastUpdate = c.getLong(s + ".lastUpdate");
                List<BountyPlacer> placers = new ArrayList<>();
                for (String p : c.getConfigurationSection(s + ".placers").getKeys(false)) {
                    if ("console".equalsIgnoreCase(p)) {
                        placers.add(new BountyPlacer(true, c.getInt(s + ".placers." + p + ".amount")));
                        continue;
                    }
                    UUID uuidp = UUID.fromString(p);
                    if (uuidp == null)
                        continue;
                    String namep = c.getString(s + ".placers." + p + ".name");
                    int amount = c.getInt(s + ".placers." + p + ".amount");
                    boolean anonymous = c.getBoolean(s + ".placers." + p + ".anonymous");
                    placers.add(new BountyPlacer(uuidp, namep, amount, anonymous));
                }
                Bounty b = new Bounty(uuid, name, placers, lastUpdate);
                if (!b.hasExpired())
                    this.bounties.add(b);
            } catch (Exception e) {
                Core.error("Error occured while loading bounty: " + s);
                e.printStackTrace();
            }
        }
    }

    public void saveBounties() {
        YamlConfiguration c = GTM.getSettings().getBountiesConfig();
        for (String s : c.getKeys(false))
            c.set(s, null);
        for (Bounty b : this.bounties) {
            try {
                if (b.hasExpired())
                    continue;
                c.set(b.getUuid() + ".name", b.getName());
                c.set(b.getUuid() + ".lastUpdate", b.getLastUpdate());
                for (BountyPlacer p : b.getPlacers()) {
                    String path = b.getUuid() + ".placers." + (p.isConsole() ? "CONSOLE" : p.getUUID());
                    c.set(path + ".name", p.getName());
                    c.set(path + ".amount", p.getAmount());
                    c.set(path + ".anonymous", p.isAnonymous());
                }
            } catch (Exception e) {
                Core.error("Error occured while saving bounty: " + b.getName());
                e.printStackTrace();
            }
        }
        Utils.saveConfig(c, "bounties");
    }

    public void removeBounty(Bounty bounty) {
        this.bounties.remove(bounty);
    }

    private void addBounty(Bounty bounty) {
        this.bounties.add(bounty);
    }

    public boolean placeBounty(Player target, int amnt, Player placer, boolean anonymous) {
        Bounty bounty = this.getBounty(target.getUniqueId());
        if (bounty == null) {
            this.bounties.add(new Bounty(target.getUniqueId(), target.getName(), new BountyPlacer(placer.getUniqueId(), placer.getName(), amnt, anonymous)));
            return false;
        } else {
            BountyPlacer bPlacer = bounty.getPlacer(placer.getUniqueId());
            if (bPlacer == null)
                bounty.addPlacer(new BountyPlacer(placer.getUniqueId(), placer.getName(), amnt, anonymous));
            else {
                bPlacer.setAmount(bPlacer.getAmount() + amnt);
                bPlacer.setAnonymous(anonymous);
            }
            bounty.setLastUpdate();
            return true;
        }

    }

}
