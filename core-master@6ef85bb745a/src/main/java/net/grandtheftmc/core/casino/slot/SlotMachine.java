package net.grandtheftmc.core.casino.slot;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.google.common.collect.Lists;
import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.Lang;
import net.grandtheftmc.core.casino.Casino;
import net.grandtheftmc.core.casino.game.CasinoGameAttribute;
import net.grandtheftmc.core.casino.game.CasinoRunnable;
import net.grandtheftmc.core.casino.game.CoreCasinoGame;
import net.grandtheftmc.core.casino.game.bet.CasinoBet;
import net.grandtheftmc.core.casino.game.bet.CasinoBetType;
import net.grandtheftmc.core.casino.game.bet.SlotMachineBet;
import net.grandtheftmc.core.casino.game.event.CasinoGameEndEvent;
import net.grandtheftmc.core.casino.game.event.CasinoGameStartEvent;
import net.grandtheftmc.core.casino.slot.menu.SlotMachineBetMenu;
import net.grandtheftmc.core.users.User;
import net.grandtheftmc.core.util.AngleUtil;
import net.grandtheftmc.core.util.C;
import net.grandtheftmc.core.util.ServerUtil;
import net.grandtheftmc.core.util.Utils;
import net.grandtheftmc.core.util.factory.FireworkFactory;
import net.grandtheftmc.core.util.factory.ItemFactory;
import net.grandtheftmc.core.util.title.NMSTitle;
import net.grandtheftmc.core.wrapper.packet.out.WrapperPlayServerEntityDestroy;
import org.bukkit.*;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.List;
import java.util.Random;

@CasinoGameAttribute(id = 1, name = "Slot Machine", version = "1.0.0-BETA")
public class SlotMachine extends CoreCasinoGame implements CasinoRunnable, CasinoBet {

    private static final BlockFace[] AXIS = { BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST };
    private final Random random;

    private Hologram hologram;
    private final Location originLocation;
    private CasinoSpinData[] spinData;
    private SlotReward[] rewards;
    private boolean running;
    private BukkitTask task;
    private boolean registered = true;

    public SlotMachine(Casino casino, Location location) {
        super(casino);

        this.task = null;
        this.random = new Random();
        this.rewards = new SlotReward[3];
        this.running = false;
        this.originLocation = location;
    }

    public Hologram getHologram() {
        return this.hologram;
    }

    public boolean isRunning() {
        return this.running;
    }

    /**
     * This method will run when the Game is added<br>
     * To the list of valid games in Casino.
     */
    @Override
    public void enable() {
        if(!originLocation.getChunk().isLoaded())
            originLocation.getChunk().load();

        this.spinData = new CasinoSpinData[] {
                new CasinoSpinData(this, this.getNextOffset(this.originLocation.clone(), 0.475, true), 1, SlotWheelType.ONE, getRandomNumber()),
                new CasinoSpinData(this, this.originLocation.clone(), 2, SlotWheelType.TWO, getRandomNumber()),
                new CasinoSpinData(this, this.getNextOffset(this.originLocation.clone(), -0.475, true), 3, SlotWheelType.THREE, getRandomNumber())
        };

        this.hologram = HologramsAPI.createHologram(Core.getInstance(), this.originLocation.clone().add(0, 3.5, 0));
        this.hologram.appendTextLine(Utils.f("&e&lClick to play!"));
        this.hologram.getVisibilityManager().setVisibleByDefault(true);
        this.hologram.getVisibilityManager().resetVisibilityAll();
    }

    /**
     * This method will run when the Game is removed<br>
     * From the list of valid games in Casino.
     */
    @Override
    public void disable() {
        this.registered = false;
        if(this.hologram != null) this.hologram.delete();
        if(this.spinData != null) {
            for (CasinoSpinData aSpinData : this.spinData)
                aSpinData.getArmorStand().remove();
        }

        resetStands(true);
    }

    /**
     * This is used so the Handler knows,
     * which slot machine was interacted with.
     *
     * @param entity
     * @return
     */
    @Override
    public boolean isClicked(Entity entity) {
        if(this.spinData != null) {
            for (CasinoSpinData aSpinData : this.spinData) {
                if (aSpinData == null || aSpinData.getArmorStand() == null) continue;
                if (entity.getUniqueId().equals(aSpinData.getArmorStand().getUniqueId()))
                    return true;
            }
        }

        return false;
    }

    @Override
    public boolean isInProgress() {
        return this.running;
    }

    @Override
    public boolean registered() {
        return this.registered;
    }

    @Override
    public Location getOriginLocation() {
        return this.originLocation.clone();
    }

    @Override
    public void start(Player player) {
        this.start(player, SlotMachineBet.TINY);
    }

    public void resetStands(boolean shutdown) {
        if (shutdown) {
            this.hologram.delete();
            for (CasinoSpinData data : spinData) {
                data.getArmorStand().remove();
            }
        }
        else {
            this.hologram.getVisibilityManager().setVisibleByDefault(true);
            this.hologram.getVisibilityManager().resetVisibilityAll();
        }
    }

    @Override
    public CasinoBetType getBetType() {
        return CasinoBetType.TOKENS;
    }

    public void pre(Player player) {
        if (this.running) return;

        this.running = true;
        CasinoGameStartEvent startEvent = new CasinoGameStartEvent(this, player);
        Bukkit.getPluginManager().callEvent(startEvent);
        this.originLocation.getWorld().spawnParticle(Particle.VILLAGER_HAPPY, this.originLocation, 100, 1.2, 1, 1.2);
        this.hologram.clearLines();

        CasinoSpinData[] old = this.spinData.clone();

        this.spinData = new CasinoSpinData[] {
                new CasinoSpinData(this, this.getNextOffset(this.originLocation.clone(), 0.475, true), 1, SlotWheelType.ONE, getRandomNumber()),
                new CasinoSpinData(this, this.originLocation.clone(), 2, SlotWheelType.TWO, getRandomNumber()),
                new CasinoSpinData(this, this.getNextOffset(this.originLocation.clone(), -0.475, true), 3, SlotWheelType.THREE, getRandomNumber())
        };

        if(old != null) {
            for(CasinoSpinData data : old) {
                WrapperPlayServerEntityDestroy wrappedPacket = new WrapperPlayServerEntityDestroy();
                wrappedPacket.setEntityIds(new int[] {data.getArmorStand().getEntityId()});
                wrappedPacket.sendPacket(player);
                data.getArmorStand().remove();
            }
            old[0] = null;
            old[1] = null;
            old[2] = null;
            old = null;
        }

        this.spinData[1].getArmorStand().getEquipment().setItemInOffHand(this.getMachine(false));
    }

    @Override
    public void start(Player player, SlotMachineBet bet) {
        if (this.task != null) return;

        this.pre(player);
        boolean[] b = {false, false, false};

        task = new BukkitRunnable() {
            @Override public void run() {

                for(int i = 0; i < spinData.length; i++) {
                    CasinoSpinData data = spinData[i];

                    if(data.getSpinState() == SpinState.END) {
                        if (data.getId() == 3) {

                            SlotReward rewardOne = rewards[0];
                            SlotReward rewardTwo = rewards[1];
                            SlotReward rewardThree = rewards[2];

                            if(rewardOne == null || rewardTwo == null || rewardThree == null)
                                continue;

                            int wonAmount = getReward(player, bet, rewardOne, rewardTwo, rewardThree);
                            if(wonAmount > 0) {
                                ServerUtil.runTask(() -> {
                                    if (player != null && player.isOnline()) {
                                        Core.getCoinManager().giveCasinoChips(player, wonAmount);
                                    }

                                    hologram.clearLines();
                                    hologram.appendTextLine(C.GREEN + C.BOLD + "$" + wonAmount);

                                    ServerUtil.runTaskAsync(() -> {
                                        if (player == null || !player.isOnline()) return;
                                        Utils.insertLog(player.getUniqueId(), "casino_slot", "CHIPS", bet.getCost().getAmount() + " Chips","" +  wonAmount, 0, bet.getCost().getAmount());
                                    });
                                });
                            }

                            new BukkitRunnable() {
                                @Override public void run() {
                                    resetStands(false);

                                    if (SlotMachine.this.task != null)
                                        task.cancel();

                                    task = null;
                                    rewards = new SlotReward[3];
                                    spinData[1].getArmorStand().getEquipment().setItemInOffHand(getMachine(true));

                                    if (player != null && player.isOnline()) {
                                        NMSTitle.sendTitle(player, " ", " ", 20, 20, 20);
                                        CasinoGameEndEvent endEvent = new CasinoGameEndEvent(SlotMachine.this, player);
                                        Bukkit.getPluginManager().callEvent(endEvent);
                                    }

                                    hologram.clearLines();
                                    hologram.appendTextLine(Utils.f("&e&lClick to play!"));

                                    for (CasinoSpinData aSpinData : spinData)
                                        aSpinData.reset();

                                    running = false;
                                }
                            }.runTaskLater(Core.getInstance(), 20*5);
                            this.cancel();
                            break;
                        }
                        continue;
                    }

                    data.setTicks(data.getTicks() + 1);

                    if((i == 0 && !b[i]) || (i == 1 && (b[i-1] && !b[i])) || (i == 2 && (b[i-1] && !b[i]))) {
                        if ((data.getTicks() % 5 == 0)) {
                            if (data.getSpeed() - 4.5 > 0) data.setSpeed(data.getSpeed() - 4.5);
                            else b[i] = true;

                            if(player != null && player.isOnline()) {
                                originLocation.getWorld().playSound(originLocation, Sound.BLOCK_WOOD_BUTTON_CLICK_ON, 0.3f, 1f);
                                data.playNote();
                            }
                        }
                    }

                    if(data.getSpinState() == SpinState.SPINNING) {
                        if(b[i] && (Math.abs(AngleUtil.getDegreesFromRadians(data.getArmorStand().getHeadPose().getX()) % 30) <= 10)) {
                            data.setSpinState(SpinState.STOPPING);
                        }
                        data.spin(true);
                        continue;
                    }

                    if(data.getSpinState() == SpinState.STOPPING) {
                        SlotReward reward = data.getWheelType().getRewardByAngle(AngleUtil.getDegreesFromRadians(data.getArmorStand().getHeadPose().getX()));
                        if (reward != null) {
                            rewards[i] = reward;
                            int finalI = i;
                            ServerUtil.runTask(() -> {
                                hologram.appendTextLine(Utils.f("&f&l" + (finalI +1) + ". &7" + reward.getRewardItem().getName()));
                                originLocation.getWorld().playSound(originLocation, Sound.BLOCK_NOTE_BASS, 0.75f, 1f);
                            });
                        }

                        data.setSpinState(SpinState.END);
                    }
                }
            }
        }.runTaskTimerAsynchronously(Core.getInstance(), 10, 1);
    }

    @Override
    public void openMenu(Player player) {
        new SlotMachineBetMenu(this).openInventory(player);
    }

    private Location getNextOffset(Location current, double offset, boolean b) {
        double yaw = Math.toRadians(current.getYaw()) + (Math.PI / 2);
        double x = current.getX() + offset * (b ? Math.sin(yaw) : Math.cos(yaw));
        double z = current.getZ() + offset * (b ? Math.cos(yaw) : Math.sin(yaw));
        return new Location(current.getWorld(), x, current.getY(), z, current.getYaw(), current.getPitch());
    }

    protected ItemStack getMachine(boolean idle) {
        ItemStack machineItem = new ItemFactory(Material.DIAMOND_SWORD).setDurability(idle ? (short)820 : (short)821).build();
        Utils.applyItemFlags(machineItem, ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE);
        return machineItem;
    }

    private int getRandomNumber() {
        int x = this.random.nextInt(73);
        return x*5;
    }

    private int getReward(Player player, SlotMachineBet bet, SlotReward one, SlotReward two, SlotReward three) {
        //announce(player, jackpotType, winAmount);
        int combinedAmount = 0;
        SlotItem I = one.getRewardItem(), II = two.getRewardItem(), III = three.getRewardItem();
        if(I == II && II == III) {
            if(I == SlotItem.SEVEN) {//JACKPOT
                combinedAmount += I.getReward()[2] * bet.getCost().getAmount();
                this.announce(player, 1, combinedAmount);
            }
            else if(I == SlotItem.CHERRY) {//DEMI JACKPOT
                combinedAmount += I.getReward()[2] * bet.getCost().getAmount();
                this.announce(player, 2, combinedAmount);
            }
            else {//3 IN A ROW
                combinedAmount += I.getReward()[2] * bet.getCost().getAmount();
                this.announce(player, 3, combinedAmount);
            }
            return combinedAmount;
        }

        if(I == II) {
            combinedAmount += I.getReward()[1] * bet.getCost().getAmount();
            if(III == SlotItem.CHERRY)
                combinedAmount += III.getReward()[0] * bet.getCost().getAmount();
            this.announce(player, 4, combinedAmount);
            return combinedAmount;
        }

        if(I == III) {
            combinedAmount += I.getReward()[1] * bet.getCost().getAmount();
            if(II == SlotItem.CHERRY)
                combinedAmount += II.getReward()[0] * bet.getCost().getAmount();
            this.announce(player, 4, combinedAmount);
            return combinedAmount;
        }

        if(II == III) {
            combinedAmount += I.getReward()[1] * bet.getCost().getAmount();
            if(I == SlotItem.CHERRY)
                combinedAmount += I.getReward()[0] * bet.getCost().getAmount();
            this.announce(player, 4, combinedAmount);
            return combinedAmount;
        }

        boolean cherry = false;
        if(I == SlotItem.CHERRY) {
            combinedAmount += I.getReward()[0] * bet.getCost().getAmount();
            cherry = true;
        }
        if(II == SlotItem.CHERRY) {
            combinedAmount += II.getReward()[0] * bet.getCost().getAmount();
            cherry = true;
        }
        if(III == SlotItem.CHERRY) {
            combinedAmount += III.getReward()[0] * bet.getCost().getAmount();
            cherry = true;
        }

        if(cherry) {
            this.announce(player, 5, combinedAmount);
            return combinedAmount;
        }

        this.announce(player, 0, combinedAmount);
        return combinedAmount;
    }

    /**
     * This will be used when a casino game is won.
     *
     * @param type
     */
    @Override
    public void announce(Player player, int type, int reward) {
        ServerUtil.runTask(() -> {
            Location l = this.originLocation.clone();
            String r =  reward + " Chips";
            switch (type) {
                case 0:
                    if (reward > 0) {
                        NMSTitle.sendTitle(player, Utils.f("&9&lCASINO"), Utils.f("&fYou have won &a" + r + "&f!"), 3 * 20, 3 * 20, 1 * 20);
                        this.originLocation.getWorld().spawnParticle(Particle.VILLAGER_HAPPY, l.getX(), l.getY() + 1, l.getZ(), 100, 1.2, 1, 1.2);
                        this.originLocation.getWorld().playSound(l, Sound.ENTITY_PLAYER_LEVELUP, 0.5f, 1f);
                    } else {
                        this.originLocation.getWorld().spawnParticle(Particle.VILLAGER_ANGRY, l.getX(), l.getY() + 1, l.getZ(), 100, 1.2, 1, 1.2);
                        NMSTitle.sendTitle(player, Utils.f("&9&lCASINO"), Utils.f("&fUnlucky, You haven't won anything this time!"), 3 * 20, 3 * 20, 1 * 20);
                        this.originLocation.getWorld().playSound(l, Sound.ENTITY_PIG_DEATH, 0.5f, 1f);
                    }
                    break;

                case 1://JACKPOT
                    this.originLocation.getWorld().spawnParticle(Particle.VILLAGER_HAPPY, l.getX(), l.getY() + 1, l.getZ(), 200, 1.2, 1, 1.2);
                    this.originLocation.getWorld().playSound(l, Sound.ITEM_TOTEM_USE, 0.5f, 1f);
                    NMSTitle.sendTitle(player, Utils.f("&c&lJACKPOT"), Utils.f("&fYou have won &a" + r + "&f!"), 3 * 20, 3 * 20, 1 * 20);
                    Bukkit.broadcastMessage(Lang.CASINO.f(player.getName() + " has won the Slot Machine Jackpot of &a" + r + "&7!"));
                    new FireworkFactory(this.originLocation.clone().add(0, 1, 0)).setPower(0).setColor(Color.RED).setFadeColor(Color.WHITE).setFlicker(true).setTrail(true).setType(FireworkEffect.Type.BALL_LARGE).build();
                    new FireworkFactory(this.originLocation.clone().add(0, 2, 0)).setPower(0).setColor(Color.RED).setFadeColor(Color.WHITE).setFlicker(true).setTrail(true).setType(FireworkEffect.Type.BALL).build();

                    new BukkitRunnable() {
                        private List<Item> items = Lists.newArrayList();
                        private int ticks = 0;

                        @Override
                        public void run() {
                            if (ticks >= 20) {
                                this.cancel();
                                this.items.forEach(Entity::remove);
                                this.items.clear();
                                return;
                            }

                            if (player != null && player.isOnline()) {
                                Item item = getMoneyItem();
                                item.getLocation().getWorld().playSound(item.getLocation(), Sound.ENTITY_ITEM_PICKUP, 0.5f, 1.5f);
//                            item.setVelocity(new Vector());
//                            double xMod = random.nextBoolean() ? -(1 + random.nextDouble()) : (1 + random.nextDouble()), zMod = random.nextBoolean() ? -(1 + random.nextDouble()) : (1 + random.nextDouble());
//                            item.setVelocity(new Vector(item.getLocation().getX() + xMod, item.));
                                item.setVelocity(item.getLocation().toVector().subtract(player.getLocation().toVector()).normalize().multiply(1.4));
                                this.items.add(item);
                            }

                            ticks += 1;
                        }
                    }.runTaskTimer(Core.getInstance(), 0, 2);

                    break;

                case 2://DEMI JACKPOT
                    this.originLocation.getWorld().spawnParticle(Particle.VILLAGER_HAPPY, l.getX(), l.getY() + 1, l.getZ(), 200, 1.2, 1, 1.2);
                    this.originLocation.getWorld().playSound(l, Sound.ITEM_TOTEM_USE, 0.5f, 1f);
                    NMSTitle.sendTitle(player, Utils.f("&9&lCASINO"), Utils.f("&fYou have won &a" + r + "&f!"), 3 * 20, 3 * 20, 1 * 20);
                    new FireworkFactory(this.originLocation.clone().add(0, 1, 0)).setPower(0).setColor(Color.RED).setFadeColor(Color.WHITE).setFlicker(true).setTrail(true).setType(FireworkEffect.Type.BALL).build();

                    new BukkitRunnable() {
                        private List<Item> items = Lists.newArrayList();
                        private int ticks = 0;

                        @Override
                        public void run() {
                            if (ticks >= 30) {
                                this.cancel();
                                this.items.forEach(Entity::remove);
                                this.items.clear();
                                return;
                            }

                            if (player != null && player.isOnline()) {
                                Item item = getMoneyItem();
                                item.getLocation().getWorld().playSound(item.getLocation(), Sound.ENTITY_ITEM_PICKUP, 0.5f, 1.5f);
                                item.setVelocity(item.getLocation().toVector().subtract(player.getLocation().toVector()).normalize().multiply(1.4));
                                this.items.add(item);
                            }

                            ticks += 1;
                        }
                    }.runTaskTimer(Core.getInstance(), 0, 2);

                    break;

                case 3://3 IN A ROW
                    this.originLocation.getWorld().spawnParticle(Particle.VILLAGER_HAPPY, l.getX(), l.getY() + 1, l.getZ(), 200, 1.2, 1, 1.2);
                    this.originLocation.getWorld().playSound(l, Sound.ITEM_TOTEM_USE, 0.5f, 1f);
                    NMSTitle.sendTitle(player, Utils.f("&9&lCASINO"), Utils.f("&fYou have won &a" + r + "&f!"), 3 * 20, 3 * 20, 1 * 20);
                    new FireworkFactory(this.originLocation.clone().add(0, 1, 0)).setPower(0).setColor(Color.GREEN).setFadeColor(Color.WHITE).setFlicker(true).setTrail(true).setType(FireworkEffect.Type.BALL).build();

                    new BukkitRunnable() {
                        private List<Item> items = Lists.newArrayList();
                        private int ticks = 0;

                        @Override
                        public void run() {
                            if (ticks >= 15) {
                                this.cancel();
                                this.items.forEach(Entity::remove);
                                this.items.clear();
                                return;
                            }

                            if (player != null && player.isOnline()) {
                                Item item = getMoneyItem();
                                item.getLocation().getWorld().playSound(item.getLocation(), Sound.ENTITY_ITEM_PICKUP, 0.5f, 1.5f);
                                item.setVelocity(item.getLocation().toVector().subtract(player.getLocation().toVector()).normalize().multiply(1.4));
                                this.items.add(item);
                            }

                            ticks += 1;
                        }
                    }.runTaskTimer(Core.getInstance(), 0, 2);
                    break;

                case 4://WIN, 2 IN A ROW
                    this.originLocation.getWorld().spawnParticle(Particle.VILLAGER_HAPPY, l.getX(), l.getY() + 1, l.getZ(), 200, 1.2, 1, 1.2);
                    this.originLocation.getWorld().playSound(l, Sound.ITEM_TOTEM_USE, 0.5f, 1f);
                    NMSTitle.sendTitle(player, Utils.f("&9&lCASINO"), Utils.f("&fYou have won &a" + r + "&f!"), 3 * 20, 3 * 20, 1 * 20);
                    break;

                case 5://CHERRY
                    this.originLocation.getWorld().spawnParticle(Particle.VILLAGER_HAPPY, l.getX(), l.getY() + 1, l.getZ(), 200, 1.2, 1, 1.2);
                    this.originLocation.getWorld().playSound(l, Sound.ITEM_TOTEM_USE, 0.5f, 1f);
                    NMSTitle.sendTitle(player, Utils.f("&9&lCASINO"), Utils.f("&fYou have won &a" + r + "&f!"), 3 * 20, 3 * 20, 1 * 20);
                    break;
            }
        });
    }

    private BlockFace yawToDirection() {
        return AXIS[Math.round(this.originLocation.getYaw() / 90f) & 0x3];
    }

    private Item getMoneyItem() {
        ItemStack stack = new ItemFactory(Material.PAPER).setName(random.nextInt(999999) + "").build();
        Item item = this.originLocation.getWorld().dropItem(this.getNextOffset(this.originLocation.clone().add(0, 1.4, 0), 0.4, false), stack);
        item.setPickupDelay(Integer.MAX_VALUE);
        return item;
    }
}
