package net.grandtheftmc.core.voting.crates;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.Lang;
import net.grandtheftmc.core.servers.ServerType;
import net.grandtheftmc.core.util.Utils;
import net.grandtheftmc.core.listeners.Move;
import net.grandtheftmc.core.menus.MenuManager;
import net.grandtheftmc.core.users.User;
import net.grandtheftmc.core.util.factory.ItemFactory;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import us.myles.ViaVersion.api.Via;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by Timothy Lampen on 2017-04-24.
 */
public class Crate {

    private final Location loc;
    private final CrateStars rank;
    private final Location selected;
    private Hologram hologram;
    private UUID openingCrate;
    private ArmorStand gravityArmorStand, hologramArmorStand, stand;
    private CrateReward determinedCrateReward;
    private boolean finished;
    private Item item;

    //@param loc should be a BLOCK location.
    public Crate(Location loc, int stars) {
        this.loc = loc;
        this.selected = loc.clone().add(.5, 0, .5);
        this.rank = CrateStars.getCrateStars(stars);
        this.generate();
    }

    public CrateStars getCrateStars() {
        return this.rank;
    }

    public Location getLocation() {
        return this.loc;
    }

    private void generate() {
        this.loc.getBlock().setType(Material.AIR);
        this.hologram = HologramsAPI.createHologram(Core.getInstance(), this.loc.clone().add(0, this.rank.getHeight(), 0));
        this.hologram.appendTextLine(this.rank.getDisplayName());
        this.hologram.appendTextLine(this.rank.getStarsString());
        this.hologram.appendTextLine(Utils.f("&9&l" + this.rank.getCrowbars() + " Crowbar" + (this.rank.getCrowbars() == 1 ? "" : "s")));
        this.hologram.appendTextLine(Utils.f("&7Right click to see rewards!"));

        this.stand = (ArmorStand) this.loc.getWorld().spawnEntity(this.loc, EntityType.ARMOR_STAND);
        this.stand.setVisible(false);
        this.stand.setGravity(false);
//        this.stand.setHelmet(new ItemStack(Material.DIAMOND_SWORD, 1, this.rank.getClosedHead()));
        this.stand.setHelmet(new ItemFactory(Core.getSettings().getType() != ServerType.VICE ? Material.DIAMOND_SWORD : Material.DIAMOND_SWORD, (short) this.rank.getClosedHead()).setUnbreakable(true).build());
        this.stand.setRemoveWhenFarAway(false);
        this.stand.setMetadata("CRATE", new FixedMetadataValue(Core.getInstance(), true));

        //In the future we can use this to check how many armour stands are spawned in the world when the server starts up.
        System.out.println("Crate count now spawned in world: " + loc.getWorld().getEntitiesByClass(ArmorStand.class).stream().filter(ent -> ent.hasMetadata("CRATE")).count());
    }

    public void startAnimation(Player player, User user) {
        this.openingCrate = player.getUniqueId();

        //Store as a crate being opened
        if (rank.getStars() > 2) {
            Move.setOpening(player.getUniqueId(), this.loc);
        }

        // While the animation is playing, any players except 'player' should be bounced away from the crate
        for (Entity e : this.loc.getWorld().getNearbyEntities(this.loc, 4, 4, 4)) {
            if (e instanceof Player && !Objects.equals(e.getUniqueId(), player.getUniqueId())) {
                e.setVelocity(e.getLocation().getDirection().setY(2).multiply(-2));
            }
        }

        this.determinedCrateReward = Core.getCrateManager().determineCrateReward(player, user, this.rank);
        ItemStack is = determinedCrateReward.getItem();
        ItemMeta im = is.getItemMeta();
        im.setDisplayName(determinedCrateReward.getDisplayName());
        is.setItemMeta(im);

        switch (rank.getStars()) {
            case 1:
                startAnimationFirst(player, stand.getLocation(), is);
                break;
            case 2:
                startAnimationSecond(player, stand.getLocation(), is);
                break;
            case 3:
                startAnimationThird(player, stand.getLocation(), is);
                break;
            case 4:
                startAnimationFourth(player, stand.getLocation(), is);
                break;
            case 5:
                startAnimationFifth(player, stand.getLocation(), is);
                break;
            case 6:
                startAnimationThird(player, stand.getLocation(), is);
                break;
        }

        this.hologram.getVisibilityManager().setVisibleByDefault(false);
        this.hologram.getVisibilityManager().resetVisibilityAll();
        //change to how long the animation is, or include with the ending of the animation
    }

    public void activateReward() {
        Player player = this.getOpeningCrate();
        CrateReward crateReward = this.determinedCrateReward;
        User user = Core.getUserManager().getLoadedUser(player.getUniqueId());
        user.setSelectedCrate(null);
        if (crateReward.getRewardPack().hasAllRewards(player, user)) {
            user.addCrowbars(this.rank.getCrowbars());
            player.sendMessage(Lang.CRATES.f("&7Sorry, it seems that you already won this package. Your crowbars have been returned. Feel free to try again."));
            return;
        }

        if (crateReward.getRewardPack().hasAnyReward(player, user)) {
            player.sendMessage(Lang.CRATES.f("&7You seem to have &a" + crateReward.getRewardPack().hasAnyRewardSize(player, user) + "&7/&a" + crateReward.getRewardPack().get().size() + "&7 rewards in this package already. You can chose between accepting the remaining rewards or having your crowbars returned."));
            user.setConfirmingCrateReward(crateReward);
            MenuManager.openMenu(player, "confirmcratereward");
            return;
        }

        player.sendMessage(Utils.f("&7You won the following reward" + (crateReward.getRewardPack().get().size() == 1 ? "" : "s") + " by opening this " + this.rank.getDisplayName() + "&7:"));
        crateReward.give(player, user, this.rank);
        user.insertLog(player, "CrateReward", "CRATE-" + this.getCrateStars().getStars(), crateReward.getName(), 1, this.getCrateStars().getCrowbars());
    }

    public void destroy() {
        this.hologram.delete();
        this.stand.remove();

        if (this.gravityArmorStand != null)
            this.gravityArmorStand.remove();

        if (this.hologramArmorStand != null)
            this.hologramArmorStand.remove();

        if (this.item != null)
            this.item.remove();
    }

    public UUID getOpeningCrateUUID() {
        return this.openingCrate;
    }

    public Player getOpeningCrate() {
        Player player = this.openingCrate == null ? null : Bukkit.getPlayer(this.openingCrate);
        if (this.openingCrate != null && player == null) this.openingCrate = null;
        return player;
    }

    public boolean isBeingOpened() {
        return this.getOpeningCrate() != null;
    }

    public ArmorStand getStand() {
        return this.stand;
    }

    /*
    *
    *
    *
    * Crate Effects
    *
    *
    * */

    public void startAnimationFirst(Player player, Location location, ItemStack reward) {
        this.gravityArmorStand = (ArmorStand) location.getWorld().spawnEntity(location.clone().subtract(0, 0.7, 0), EntityType.ARMOR_STAND);
        this.gravityArmorStand.setGravity(false);
        this.gravityArmorStand.setVisible(false);
        play(player, "VILLAGER_HAPPY", location, 0.5F, 0F, 0.5F, 0.1F, 40);

        new BukkitRunnable() {
            @Override
            public void run() {
                startAnimation(player, stand.getLocation().clone().add(0, 1, 0), new Callback<Location>() {
                    @Override
                    public void execute(Location response) {
//                        stand.setHelmet(new ItemStack(Material.DIAMOND_SWORD, 1, Crate.this.rank.getOpenHead()));
                        stand.setHelmet(new ItemFactory(Core.getSettings().getType() != ServerType.VICE ? Material.DIAMOND_SWORD : Material.DIAMOND_SWORD, (short) Crate.this.rank.getOpenHead()).setUnbreakable(true).build());
                        final Item b = response.clone().getWorld().dropItem(response.clone().add(0, 1, 0), reward);
                        b.setVelocity(new Vector(0, 0.2, 0));
                        b.setTicksLived(8000);
                        b.setPickupDelay(Integer.MAX_VALUE);

                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                gravityArmorStand.setPassenger(b);
                                hologramArmorStand = (ArmorStand) b.getLocation().getWorld().spawnEntity(b.getLocation().clone().add(0, -2.3, 0), EntityType.ARMOR_STAND);
                                hologramArmorStand.setMetadata("crateItemDisplay", new FixedMetadataValue(Core.getInstance(), true));
                                hologramArmorStand.setCustomName(Utils.f(reward.getItemMeta().getDisplayName()));
                                hologramArmorStand.setCustomNameVisible(true);
                                hologramArmorStand.setGravity(false);
                                hologramArmorStand.setVisible(false);
                                Crate.this.activateReward();

                                new BukkitRunnable() {
                                    @Override
                                    public void run() {
                                        resetAnimationFirst(b);
                                    }
                                }.runTaskLater(Core.getInstance(), 100);
                            }
                        }.runTaskLater(Core.getInstance(), 11);

                        player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_USE, 1, 1);
                        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
                    }
                });
            }
        }.runTaskLater(Core.getInstance(), 30);
    }

    public void resetAnimationFirst(Item item) {
        this.hologramArmorStand.remove();
        this.gravityArmorStand.remove();
        item.remove();
//        this.stand.setHelmet(new ItemStack(Material.DIAMOND_SWORD, 1, this.rank.getClosedHead()));
        this.stand.setHelmet(new ItemFactory(Core.getSettings().getType() != ServerType.VICE ? Material.DIAMOND_SWORD : Material.DIAMOND_SWORD, (short) this.rank.getClosedHead()).setUnbreakable(true).build());
        this.openingCrate = null;
        this.hologram.getVisibilityManager().setVisibleByDefault(true);
        this.hologram.getVisibilityManager().resetVisibilityAll();
    }

    public void startAnimationSecond(final Player player, final Location location, final ItemStack reward) {
        gravityArmorStand = (ArmorStand) location.getWorld().spawnEntity(location.clone().subtract(0, 0.6, 0), EntityType.ARMOR_STAND);
        gravityArmorStand.setGravity(false);
        gravityArmorStand.setVisible(false);
        play(player, "VILLAGER_HAPPY", location, 0.5F, 0F, 0.5F, 0.1F, 40);

        new BukkitRunnable() {
            int times = 0;

            @Override
            public void run() {
                if (times == 5) {
                    cancel();
                    new BukkitRunnable() {
                        @Override
                        public void run() {
//                            stand.setHelmet(new ItemStack(Material.DIAMOND_SWORD, 1, Crate.this.rank.getOpenHead()));
                            stand.setHelmet(new ItemFactory(Core.getSettings().getType() != ServerType.VICE ? Material.DIAMOND_SWORD : Material.DIAMOND_SWORD, (short) Crate.this.rank.getOpenHead()).setUnbreakable(true).build());
                            final Item b = location.clone().getWorld().dropItem(location.clone().add(0, 1, 0), reward);
                            b.setVelocity(new Vector(0, 0.2, 0));
                            b.setTicksLived(8000);
                            b.setPickupDelay(Integer.MAX_VALUE);

                            new BukkitRunnable() {
                                @Override
                                public void run() {
                                    gravityArmorStand.setPassenger(b);
                                    hologramArmorStand = (ArmorStand) b.getLocation().getWorld().spawnEntity(b.getLocation().clone().add(0, -2.2, 0), EntityType.ARMOR_STAND);
                                    hologramArmorStand.setMetadata("crateItemDisplay", new FixedMetadataValue(Core.getInstance(), true));
                                    hologramArmorStand.setCustomName(Utils.f(reward.getItemMeta().getDisplayName()));
                                    hologramArmorStand.setCustomNameVisible(true);
                                    hologramArmorStand.setGravity(false);
                                    hologramArmorStand.setVisible(false);
                                    Crate.this.activateReward();
                                    new BukkitRunnable() {
                                        @Override
                                        public void run() {
                                            resetAnimationSecond(b);
                                        }
                                    }.runTaskLater(Core.getInstance(), 100);
                                }
                            }.runTaskLater(Core.getInstance(), 11);

                            player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_USE, 1, 1);
                            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
                        }
                    }.runTask(Core.getInstance());
                }

                play(player, "SMOKE_LARGE", location, 0.3F, 0.1F, 0.3F, 0.1F, 50);
                player.playSound(player.getLocation(), Sound.BLOCK_GRAVEL_FALL, 1, 1);
                player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_BREAK, 1, 1);
                player.playSound(player.getLocation(), Sound.BLOCK_GRASS_BREAK, 1, 1);

                if (ThreadLocalRandom.current().nextInt(2) == 1) {
                    player.playSound(player.getLocation(), Sound.BLOCK_GRASS_BREAK, 1, 1);
                    player.playSound(player.getLocation(), Sound.BLOCK_GRAVEL_FALL, 1, 1);
                } else {
                    player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_BREAK, 1, 1);
                    player.playSound(player.getLocation(), Sound.BLOCK_GRASS_BREAK, 1, 1);
                }

                times++;
            }
        }.runTaskTimerAsynchronously(Core.getInstance(), 0, 10);
    }

    public void resetAnimationSecond(Item item) {
        this.hologramArmorStand.remove();
        this.gravityArmorStand.remove();
        item.remove();
//        this.stand.setHelmet(new ItemStack(Material.DIAMOND_SWORD, 1, this.rank.getClosedHead()));
        this.stand.setHelmet(new ItemFactory(Core.getSettings().getType() != ServerType.VICE ? Material.DIAMOND_SWORD : Material.DIAMOND_SWORD, (short) this.rank.getClosedHead()).setUnbreakable(true).build());
        this.openingCrate = null;
        this.hologram.getVisibilityManager().setVisibleByDefault(true);
        this.hologram.getVisibilityManager().resetVisibilityAll();
    }

    public void startAnimationThird(final Player player, final Location location, ItemStack reward) {
        gravityArmorStand = (ArmorStand) location.getWorld().spawnEntity(location.clone().subtract(0, 0.8, 0), EntityType.ARMOR_STAND);
        gravityArmorStand.setGravity(false);
        gravityArmorStand.setVisible(false);
        play(player, "VILLAGER_HAPPY", location, 0.5F, 0F, 0.5F, 0.1F, 40);
        new BukkitRunnable() {
            int times = 0;

            @Override
            public void run() {
                if (times == 4) {
                    cancel();
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            play(player, "EXPLOSION_NORMAL", location, 0.2F, 0F, 0.2F, 0.1F, 70);
//                            stand.setHelmet(new ItemStack(Material.DIAMOND_SWORD, 1, Crate.this.rank.getOpenHead()));
                            stand.setHelmet(new ItemFactory(Core.getSettings().getType() != ServerType.VICE ? Material.DIAMOND_SWORD : Material.DIAMOND_SWORD, (short) Crate.this.rank.getOpenHead()).setUnbreakable(true).build());
                            item = location.clone().getWorld().dropItem(location.clone().add(0, 0.2, 0), reward);
                            item.setVelocity(new Vector(0, 0, 0));
                            item.setTicksLived(8000);
                            item.setPickupDelay(Integer.MAX_VALUE);
                            gravityArmorStand.setPassenger(item);
                            Crate.this.activateReward();
                            new BukkitRunnable() {
                                @Override
                                public void run() {
                                    hologramArmorStand = (ArmorStand) item.getLocation().getWorld().spawnEntity(item.getLocation().clone().add(0, -1.3, 0), EntityType.ARMOR_STAND);
                                    hologramArmorStand.setMetadata("crateItemDisplay", new FixedMetadataValue(Core.getInstance(), true));
                                    hologramArmorStand.setCustomName(Utils.f(reward.getItemMeta().getDisplayName()));
                                    hologramArmorStand.setCustomNameVisible(true);
                                    hologramArmorStand.setGravity(false);
                                    hologramArmorStand.setVisible(false);
                                    new BukkitRunnable() {
                                        @Override
                                        public void run() {
                                            resetCrateThird(player);
                                        }
                                    }.runTaskLater(Core.getInstance(), 20 * 7);
                                }
                            }.runTaskLater(Core.getInstance(), 8);
                            player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_USE, 1, 1);
                            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
                        }
                    }.runTask(Core.getInstance());
                }

                play(player, "CLOUD", location, 0.2F, 0.1F, 0.2F, 0.1F, 2);
                play(player, "SMOKE_NORMAL", location, 0.2F, 0.1F, 0.2F, 0.1F, 20);
                if (ThreadLocalRandom.current().nextInt(2) == 1) {
                    player.playSound(player.getLocation(), Sound.BLOCK_GRASS_BREAK, 1, 1);
                    player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_BREAK, 1, 1);
                } else {
                    player.playSound(player.getLocation(), Sound.ENTITY_BLAZE_BURN, 1, 1);
                    player.playSound(player.getLocation(), Sound.ENTITY_BLAZE_HURT, 1, 1);
                }
                times++;
            }
        }.runTaskTimerAsynchronously(Core.getInstance(), 0, 10);
    }

    public void resetCrateThird(final Player player) {
        new BukkitRunnable() {
            int times = 0;

            @Override
            public void run() {
                if (times == 3) {
                    Move.stopOpening(Crate.this.openingCrate, Crate.this.loc);
                    Crate.this.openingCrate = null;
                    cancel();
                    item.remove();
                    Crate.this.hologram.getVisibilityManager().setVisibleByDefault(true);
                    Crate.this.hologram.getVisibilityManager().resetVisibilityAll();
                    gravityArmorStand.remove();
                    hologramArmorStand.remove();
//                    stand.setHelmet(new ItemStack(Material.DIAMOND_SWORD, 1, (short) 14));
                    stand.setHelmet(new ItemFactory(Core.getSettings().getType() != ServerType.VICE ? Material.DIAMOND_SWORD : Material.DIAMOND_SWORD, (short) Crate.this.getCrateStars().getClosedHead()).setUnbreakable(true).build());
                }

                if (ThreadLocalRandom.current().nextInt(2) == 1) {
                    play(player, "SLIME", item.getLocation().clone().add(0, 0.2, 0), 0.2F, 0F, 0.2F, 0.1F, 40);
                    play(player, "CLOUD", item.getLocation().add(0, -0.2, 0), 0.3F, 0F, 0.3F, 0.1F, 40);
                } else {
                    play(player, "CRIT", item.getLocation().clone().add(0, 0.2, 0), 0.2F, 0F, 0.2F, 0.1F, 40);
                    play(player, "SMOKE_NORMAL", item.getLocation().add(0, -0.2, 0), 0.3F, 0F, 0.3F, 0.1F, 40);
                }

                if (ThreadLocalRandom.current().nextInt(2) == 1) {
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_PLING, 1, 1);
                    player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1, 1);
                } else {
                    player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_BOTTLE_THROW, 1, 1);
                    player.playSound(player.getLocation(), Sound.BLOCK_SLIME_HIT, 1, 1);
                }

                times++;
            }
        }.runTaskTimerAsynchronously(Core.getInstance(), 0, 20);
    }

    public void startAnimationFourth(final Player player, final Location location, ItemStack reward) {
        gravityArmorStand = (ArmorStand) location.getWorld().spawnEntity(location.clone().subtract(0, 0.6, 0), EntityType.ARMOR_STAND);
        gravityArmorStand.setGravity(false);
        gravityArmorStand.setVisible(false);
        play(player, "VILLAGER_HAPPY", location, 0.5F, 0F, 0.5F, 0.1F, 40);
        if (selected != null) {
            player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_BREAK, 1, 1);
            player.playSound(player.getLocation(), Sound.ENTITY_BLAZE_HURT, 1, 1);
            new BukkitRunnable() {
                int times = 0;

                @Override
                public void run() {
                    if (times == 3) {
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                play(player, "EXPLOSION_HUGE", location, 0.2F, 0F, 0.2F, 0.1F, 1);
//                                stand.setHelmet(new ItemStack(Material.DIAMOND_SWORD, 1, Crate.this.rank.getOpenHead()));
                                stand.setHelmet(new ItemFactory(Core.getSettings().getType() != ServerType.VICE ? Material.DIAMOND_SWORD : Material.DIAMOND_SWORD, (short) Crate.this.rank.getOpenHead()).setUnbreakable(true).build());
                                item = location.clone().getWorld().dropItem(location.clone().add(0, 0.2, 0), reward);
                                item.setVelocity(new Vector(0, 0, 0));
                                item.setTicksLived(8000);
                                item.setPickupDelay(Integer.MAX_VALUE);
                                gravityArmorStand.setPassenger(item);
                                Crate.this.activateReward();
                                hologramArmorStand = (ArmorStand) item.getLocation().getWorld().spawnEntity(item.getLocation().clone().add(0, -0.4, 0), EntityType.ARMOR_STAND);
                                hologramArmorStand.setMetadata("crateItemDisplay", new FixedMetadataValue(Core.getInstance(), true));
                                hologramArmorStand.setCustomName(Utils.f(reward.getItemMeta().getDisplayName()));
                                hologramArmorStand.setCustomNameVisible(true);
                                hologramArmorStand.setGravity(false);
                                hologramArmorStand.setVisible(false);
                                new BukkitRunnable() {

                                    @Override
                                    public void run() {
                                        resetCrateFourth(player);
                                    }
                                }.runTaskLater(Core.getInstance(), 20 * 7);
                                player.playSound(player.getLocation(), Sound.ENTITY_BLAZE_HURT, 1, 1);
                                player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
                            }
                        }.runTaskLater(Core.getInstance(), 20);
                        cancel();
                    }

                    if (times <= 2) {
//                        stand.setHelmet(new ItemStack(Material.DIAMOND_SWORD, 1, (short) 17));
                        stand.setHelmet(new ItemFactory(Core.getSettings().getType() != ServerType.VICE ? Material.DIAMOND_SWORD : Material.DIAMOND_SWORD, Core.getSettings().getType() != ServerType.VICE ? (short) 17 : (short) 809).setUnbreakable(true).build());
                    } else if (times == 3) {
                        //   stand.setHelmet(new ItemStack(Material.DIAMOND_SWORD, 1, (short)18));//TODO: Change back to 18 once it comes in
                    }

                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_HAT, 1, 1);
                    times++;
                }
            }.runTaskTimerAsynchronously(Core.getInstance(), 0, 20);
        }
    }

    public void resetCrateFourth(final Player player) {
        new BukkitRunnable() {
            int times = 0;

            @Override
            public void run() {
                if (times == 3) {
                    Move.stopOpening(Crate.this.openingCrate, Crate.this.loc);
                    Crate.this.openingCrate = null;
                    cancel();
                    item.remove();
                    gravityArmorStand.remove();
                    hologramArmorStand.remove();
                    Crate.this.hologram.getVisibilityManager().setVisibleByDefault(true);
                    Crate.this.hologram.getVisibilityManager().resetVisibilityAll();
//                    stand.setHelmet(new ItemStack(Material.DIAMOND_SWORD, 1, Crate.this.rank.getClosedHead()));
                    stand.setHelmet(new ItemFactory(Core.getSettings().getType() != ServerType.VICE ? Material.DIAMOND_SWORD : Material.DIAMOND_SWORD, (short) Crate.this.rank.getClosedHead()).setUnbreakable(true).build());
                }
                if (ThreadLocalRandom.current().nextInt(2) == 1) {
                    play(player, "SLIME", item.getLocation().clone().add(0, 0.2, 0), 0.2F, 0F, 0.2F, 0.1F, 40);
                    play(player, "CLOUD", item.getLocation().add(0, -0.2, 0), 0.3F, 0F, 0.3F, 0.1F, 40);
                } else {
                    play(player, "CRIT", item.getLocation().clone().add(0, 0.2, 0), 0.2F, 0F, 0.2F, 0.1F, 40);
                    play(player, "SMOKE_NORMAL", item.getLocation().add(0, -0.2, 0), 0.3F, 0F, 0.3F, 0.1F, 40);
                }
                if (ThreadLocalRandom.current().nextInt(2) == 1) {
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_PLING, 1, 1);
                    player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1, 1);
                } else {
                    player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_BOTTLE_THROW, 1, 1);
                    player.playSound(player.getLocation(), Sound.BLOCK_SLIME_HIT, 1, 1);
                }
                times++;
            }
        }.runTaskTimerAsynchronously(Core.getInstance(), 0, 20);
    }

    public void startAnimationFifth(final Player player, final Location location, ItemStack reward) {
        gravityArmorStand = (ArmorStand) location.getWorld().spawnEntity(location.clone().subtract(0, 1.2, 0), EntityType.ARMOR_STAND);
        gravityArmorStand.setGravity(false);
        gravityArmorStand.setVisible(false);
        play(player, "VILLAGER_HAPPY", location, 0.5F, 0F, 0.5F, 0.1F, 40);
        if (selected != null) {
            new BukkitRunnable() {
                int times = 0;

                @Override
                public void run() {
                    if (finished) {
                        cancel();
                    }
                    play(player, "CLOUD", location, 0.2F, 0.1F, 0.2F, 0.1F, 2);
                }
            }.runTaskTimerAsynchronously(Core.getInstance(), 0, 10);
//            stand.setHelmet(new ItemStack(Material.DIAMOND_SWORD, 1, (short) 21));
            this.stand.setHelmet(new ItemFactory(Core.getSettings().getType() != ServerType.VICE ? Material.DIAMOND_SWORD : Material.DIAMOND_SWORD,  Core.getSettings().getType() != ServerType.VICE ? (short) 21 : (short) 813).setUnbreakable(true).build());
            launchBills(5, 20, location, 1);
            launchFireworks(location, 8, 8);
            new BukkitRunnable() {
                @Override
                public void run() {
                    launchBills(15, 20, location, 2);
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            launchBills(25, 20, location, 3);
                            new BukkitRunnable() {
                                int times = 0;
                                @Override
                                public void run() {
                                    if (times == 5) {
                                        stand.setHelmet(new ItemStack(Material.DIAMOND_SWORD, 1, Crate.this.rank.getOpenHead()));
                                        stand.setHelmet(new ItemFactory(Core.getSettings().getType() != ServerType.VICE ? Material.DIAMOND_SWORD : Material.DIAMOND_SWORD, (short) Crate.this.rank.getOpenHead()).setUnbreakable(true).build());
                                        cancel();
                                        finished = true;
                                        item = location.clone().getWorld().dropItem(location.clone().add(0, 0.2, 0), reward);
                                        item.setVelocity(new Vector(0, 0.2, 0));
                                        item.setTicksLived(8000);
                                        item.setPickupDelay(Integer.MAX_VALUE);
                                        gravityArmorStand.setPassenger(item);
                                        Crate.this.activateReward();
                                        new BukkitRunnable() {
                                            @Override
                                            public void run() {
                                                hologramArmorStand = (ArmorStand) item.getLocation().getWorld().spawnEntity(item.getLocation().clone().add(0, -1.6, 0), EntityType.ARMOR_STAND);
                                                hologramArmorStand.setMetadata("crateItemDisplay", new FixedMetadataValue(Core.getInstance(), true));
                                                hologramArmorStand.setCustomName(Utils.f(reward.getItemMeta().getDisplayName()));
                                                hologramArmorStand.setCustomNameVisible(true);
                                                hologramArmorStand.setGravity(false);
                                                hologramArmorStand.setVisible(false);
                                                new BukkitRunnable() {
                                                    @Override
                                                    public void run() {
                                                        resetCrateFifth(player);
                                                    }
                                                }.runTaskLater(Core.getInstance(), 20 * 5);
                                            }
                                        }.runTaskLater(Core.getInstance(), 8);
                                        player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_USE, 1, 1);
                                        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
                                    } else {
                                        launchBills(5, 20, location, 3);
                                    }
                                    if (times <= 2) {
//                                        stand.setHelmet(new ItemStack(Material.DIAMOND_SWORD, 1, (short) 22));
                                        stand.setHelmet(new ItemFactory(Core.getSettings().getType() != ServerType.VICE ? Material.DIAMOND_SWORD : Material.DIAMOND_SWORD, Core.getSettings().getType() != ServerType.VICE ? (short) 22 : (short) 814).setUnbreakable(true).build());

                                    } else if (times <= 4) {
//                                        stand.setHelmet(new ItemStack(Material.DIAMOND_SWORD, 1, (short) 23));
                                        stand.setHelmet(new ItemFactory(Core.getSettings().getType() != ServerType.VICE ? Material.DIAMOND_SWORD : Material.DIAMOND_SWORD, Core.getSettings().getType() != ServerType.VICE ? (short) 23 : (short) 815).setUnbreakable(true).build());
                                    }
                                    times++;
                                }
                            }.runTaskTimer(Core.getInstance(), 0, 20);

                            new BukkitRunnable() {
                                @Override
                                public void run() {
                                    play(player, "EXPLOSION_HUGE", location, 0.2F, 0.1F, 0.2F, 0.1F, 1);
                                    launchFireworks(location, 8, 8);
                                }
                            }.runTaskLater(Core.getInstance(), 60);
                        }
                    }.runTaskLater(Core.getInstance(), 10);
                }
            }.runTaskLater(Core.getInstance(), 10);
        }
    }

    public void resetCrateFifth(final Player player) {
        new BukkitRunnable() {
            int times = 0;

            @Override
            public void run() {
                if (times == 3) {
                    cancel();
                    Move.stopOpening(Crate.this.openingCrate, Crate.this.loc);
                    Crate.this.openingCrate = null;
                    item.remove();
                    gravityArmorStand.remove();
                    hologramArmorStand.remove();
                    Crate.this.hologram.getVisibilityManager().setVisibleByDefault(true);
                    Crate.this.hologram.getVisibilityManager().resetVisibilityAll();
//                    stand.setHelmet(new ItemStack(Material.DIAMOND_SWORD, 1, (short) 20));
                    stand.setHelmet(new ItemFactory(Core.getSettings().getType() != ServerType.VICE ? Material.DIAMOND_SWORD : Material.DIAMOND_SWORD, (short) Crate.this.getCrateStars().getClosedHead()).setUnbreakable(true).build());
                }
                if (ThreadLocalRandom.current().nextInt(2) == 1) {
                    play(player, "SLIME", item.getLocation().clone().add(0, 0.2, 0), 0.2F, 0F, 0.2F, 0.1F, 40);
                    play(player, "CLOUD", item.getLocation().add(0, -0.2, 0), 0.3F, 0F, 0.3F, 0.1F, 40);
                } else {
                    play(player, "CRIT", item.getLocation().clone().add(0, 0.2, 0), 0.2F, 0F, 0.2F, 0.1F, 40);
                    play(player, "SMOKE_NORMAL", item.getLocation().add(0, -0.2, 0), 0.3F, 0F, 0.3F, 0.1F, 40);
                }
                if (ThreadLocalRandom.current().nextInt(2) == 1) {
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_PLING, 1, 1);
                    player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1, 1);
                } else {
                    player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_BOTTLE_THROW, 1, 1);
                    player.playSound(player.getLocation(), Sound.BLOCK_SLIME_HIT, 1, 1);
                }
                times++;
            }
        }.runTaskTimerAsynchronously(Core.getInstance(), 0, 20);
    }

    public void startAnimationSixth(final Player player, final Location location, ItemStack reward) {
        gravityArmorStand = (ArmorStand) location.getWorld().spawnEntity(location.clone().subtract(0, 0.8, 0), EntityType.ARMOR_STAND);
        gravityArmorStand.setGravity(false);
        gravityArmorStand.setVisible(false);
        play(player, "VILLAGER_HAPPY", location, 0.5F, 0F, 0.5F, 0.1F, 40);
        new BukkitRunnable() {
            int times = 0;

            @Override
            public void run() {
                if (times == 4) {
                    cancel();
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            play(player, "EXPLOSION_NORMAL", location, 0.2F, 0F, 0.2F, 0.1F, 70);
//                            stand.setHelmet(new ItemStack(Material.DIAMOND_SWORD, 1, Crate.this.rank.getOpenHead()));
                            stand.setHelmet(new ItemFactory(Core.getSettings().getType() != ServerType.VICE ? Material.DIAMOND_SWORD : Material.DIAMOND_SWORD, (short) Crate.this.rank.getOpenHead()).setUnbreakable(true).build());
                            item = location.clone().getWorld().dropItem(location.clone().add(0, 0.2, 0), reward);
                            item.setVelocity(new Vector(0, 0, 0));
                            item.setTicksLived(8000);
                            item.setPickupDelay(Integer.MAX_VALUE);
                            gravityArmorStand.setPassenger(item);
                            Crate.this.activateReward();
                            new BukkitRunnable() {
                                @Override
                                public void run() {
                                    hologramArmorStand = (ArmorStand) item.getLocation().getWorld().spawnEntity(item.getLocation().clone().add(0, -1.3, 0), EntityType.ARMOR_STAND);
                                    hologramArmorStand.setMetadata("crateItemDisplay", new FixedMetadataValue(Core.getInstance(), true));
                                    hologramArmorStand.setCustomName(Utils.f(reward.getItemMeta().getDisplayName()));
                                    hologramArmorStand.setCustomNameVisible(true);
                                    hologramArmorStand.setGravity(false);
                                    hologramArmorStand.setVisible(false);
                                    new BukkitRunnable() {
                                        @Override
                                        public void run() {
                                            resetCrateThird(player);
                                        }
                                    }.runTaskLater(Core.getInstance(), 20 * 7);
                                }
                            }.runTaskLater(Core.getInstance(), 8);
                            player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_USE, 1, 1);
                            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
                        }
                    }.runTask(Core.getInstance());
                }

                play(player, "CLOUD", location, 0.2F, 0.1F, 0.2F, 0.1F, 2);
                play(player, "SMOKE_NORMAL", location, 0.2F, 0.1F, 0.2F, 0.1F, 20);
                if (ThreadLocalRandom.current().nextInt(2) == 1) {
                    player.playSound(player.getLocation(), Sound.BLOCK_GRASS_BREAK, 1, 1);
                    player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_BREAK, 1, 1);
                } else {
                    player.playSound(player.getLocation(), Sound.ENTITY_BLAZE_BURN, 1, 1);
                    player.playSound(player.getLocation(), Sound.ENTITY_BLAZE_HURT, 1, 1);
                }
                times++;
            }
        }.runTaskTimerAsynchronously(Core.getInstance(), 0, 10);
    }

    public void resetCrateSixth(final Player player) {
        new BukkitRunnable() {
            int times = 0;

            @Override
            public void run() {
                if (times == 3) {
                    Move.stopOpening(Crate.this.openingCrate, Crate.this.loc);
                    Crate.this.openingCrate = null;
                    cancel();
                    item.remove();
                    Crate.this.hologram.getVisibilityManager().setVisibleByDefault(true);
                    Crate.this.hologram.getVisibilityManager().resetVisibilityAll();
                    gravityArmorStand.remove();
                    hologramArmorStand.remove();
//                    stand.setHelmet(new ItemStack(Material.DIAMOND_SWORD, 1, (short) 14));
                    stand.setHelmet(new ItemFactory(Core.getSettings().getType() != ServerType.VICE ? Material.DIAMOND_SWORD : Material.DIAMOND_SWORD, (short) Crate.this.getCrateStars().getClosedHead()).setUnbreakable(true).build());
                }

                if (ThreadLocalRandom.current().nextInt(2) == 1) {
                    play(player, "SLIME", item.getLocation().clone().add(0, 0.2, 0), 0.2F, 0F, 0.2F, 0.1F, 40);
                    play(player, "CLOUD", item.getLocation().add(0, -0.2, 0), 0.3F, 0F, 0.3F, 0.1F, 40);
                } else {
                    play(player, "CRIT", item.getLocation().clone().add(0, 0.2, 0), 0.2F, 0F, 0.2F, 0.1F, 40);
                    play(player, "SMOKE_NORMAL", item.getLocation().add(0, -0.2, 0), 0.3F, 0F, 0.3F, 0.1F, 40);
                }

                if (ThreadLocalRandom.current().nextInt(2) == 1) {
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_PLING, 1, 1);
                    player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1, 1);
                } else {
                    player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_BOTTLE_THROW, 1, 1);
                    player.playSound(player.getLocation(), Sound.BLOCK_SLIME_HIT, 1, 1);
                }

                times++;
            }
        }.runTaskTimerAsynchronously(Core.getInstance(), 0, 20);
    }

    public void launchBills(int bills, final long despawnTime, Location location, final int maxLaunchRange) {
        final Location launch = location.clone().add(0, 0.1, 0);
        final ItemStack paper = new ItemStack(Material.PAPER);
        for (int i = 0; i < bills; i++) {
            new BukkitRunnable() {

                @Override
                public void run() {
                    final Item item = launch.getWorld().dropItemNaturally(launch.clone(), paper);
                    item.setPickupDelay(Integer.MAX_VALUE);
                    item.getVelocity().multiply(maxLaunchRange).setY(0.6);
                    launch.getWorld().playSound(launch.clone(), Sound.ENTITY_EGG_THROW, 1, 1);
                    new BukkitRunnable() {

                        @Override
                        public void run() {
                            item.remove();
                        }
                    }.runTaskLater(Core.getInstance(), despawnTime);
                }

            }.runTaskLater(Core.getInstance(), 10 * i);
        }
    }

    public void launchFireworks(Location center, int radius, int pointAmounts) {
        List<Location> pointsToLaunch = getCircle(center, radius, pointAmounts);
        for (Location launch : pointsToLaunch) {
            Firework fw = (Firework) center.getWorld().spawnEntity(launch, EntityType.FIREWORK);
            FireworkMeta fwm = fw.getFireworkMeta();
            int shape = ThreadLocalRandom.current().nextInt(4) + 1;
            FireworkEffect.Type type = FireworkEffect.Type.BALL;
            if (shape == 1) type = FireworkEffect.Type.BALL;
            if (shape == 2) type = FireworkEffect.Type.BALL_LARGE;
            if (shape == 3) type = FireworkEffect.Type.BURST;
            if (shape == 4) type = FireworkEffect.Type.CREEPER;
            if (shape == 5) type = FireworkEffect.Type.STAR;
            Color color = (ThreadLocalRandom.current().nextInt(2) == 1) ? Color.RED : Color.ORANGE;
            FireworkEffect effect = FireworkEffect.builder().flicker(ThreadLocalRandom.current().nextBoolean()).withColor(color).withFade(Color.AQUA).with(type).trail(ThreadLocalRandom.current().nextBoolean()).build();
            fwm.addEffect(effect);
            fwm.setPower(1);
            fw.setFireworkMeta(fwm);
        }
    }

    public void startAnimation(final Player player, final Location location, final Callback<Location> callback) {
        if (selected != null) {
            new BukkitRunnable() {
                int times = 0;

                @Override
                public void run() {
                    if (times == 4) {
                        cancel();
                        new BukkitRunnable() {

                            @Override
                            public void run() {
                                callback.execute(location);
                            }
                        }.runTask(Core.getInstance());
                    }
                    play(player, "SMOKE_NORMAL", location, 0.2F, 0.1F, 0.2F, 0.1F, 70);
                    if (ThreadLocalRandom.current().nextInt(2) == 1) {
                        player.playSound(player.getLocation(), Sound.ENTITY_CREEPER_HURT, 1, 1);
                        player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_BREAK, 1, 1);
                    } else {
                        player.playSound(player.getLocation(), Sound.ENTITY_BLAZE_BURN, 1, 1);
                        player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_BREAK, 1, 1);
                    }
                    times++;
                }
            }.runTaskTimerAsynchronously(Core.getInstance(), 0, 10);
        }
    }

    public ArrayList<Location> getCircle(Location center, double radius, int amount) {
        World world = center.getWorld();
        double increment = (2 * Math.PI) / amount;
        ArrayList<Location> locations = new ArrayList<Location>();
        for (int i = 0; i < amount; i++) {
            double angle = i * increment;
            double x = center.getX() + (radius * Math.cos(angle));
            double z = center.getZ() + (radius * Math.sin(angle));
            locations.add(new Location(world, x, center.getY(), z));
        }
        return locations;
    }

    public void play(Player p, String effect, Location loc, float xOffset, float yOffset, float zOffset, float speed, int amount) {
        p.getWorld().spawnParticle(Particle.valueOf(effect), loc, amount, xOffset, yOffset, zOffset, speed);
    }

    public interface Callback<T> {
        void execute(T response);
    }

}
