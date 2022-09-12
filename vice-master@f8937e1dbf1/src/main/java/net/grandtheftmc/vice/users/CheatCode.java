package net.grandtheftmc.vice.users;

import net.grandtheftmc.core.Lang;
import net.grandtheftmc.core.users.User;
import net.grandtheftmc.core.users.UserRank;
import net.grandtheftmc.core.util.C;
import net.grandtheftmc.core.util.State;
import net.grandtheftmc.core.util.Utils;
import net.grandtheftmc.vice.Vice;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

/**
 * Created by Timothy Lampen on 8/21/2017.
 */
public enum CheatCode {

    KATANA(UserRank.ADMIN, Utils.createItem(Material.DIAMOND_SWORD, 421, "KATANA" , "&7Chop off heads with this masterful weapon!", "&7Delay: &e&l3 days."), true, false, "katana_cc", null),
    JELLYLEGS(UserRank.ADMIN, Utils.createItem(Material.DIAMOND_BOOTS, "JELLYLEGS", "&7Recieve no more fall damage."), true, true, null, null),
    SILKSPAWNERS(UserRank.SUPREME, Utils.createItem(Material.DIAMOND_PICKAXE, "SILKSPAWNERS", "&7Recieve the ability to silk touch spawners."), true, true, null, "&7To unlock this cheatcode, purchase the &c&lSUPREME&7 rank or the &2&lSILKSPAWNERS&7 cheatcode at &a&lstore.grandtheftmc.net&7!"),
    NIGHTVIS(UserRank.SUPREME, Utils.createItem(Material.CHAINMAIL_HELMET, "NIGHTVIS", "&7Be able to toggle permanent night vision."), false, true, null, "&7To unlock this cheatcode, purchase the &c&lSUPREME&7 rank or the &2&lNIGHTVIS&7 cheatcode at &a&lstore.grandtheftmc.net&7!"),
    NOFUEL(UserRank.ADMIN, Utils.createItem(Material.DIAMOND_SWORD, 1001, "NOFUEL", "&7Don't use any jetpack fuel in non-enemy territory while enemies aren't around."), true, true, null, null),
    SNEAKY(UserRank.ADMIN, Utils.createItem(Material.GLASS_BOTTLE, "SNEAKY", "&7Gain invisibility for 5 seconds when you go to spawn ", "&7and exit the safezone to more easily sell your drugs"), true, true, null, null),
    FEED(UserRank.SPONSOR, Utils.createItem(Material.COOKED_BEEF, "FEED", "&7Have access to the feed command."), true, false, null, null),
    FUCKME(UserRank.ADMIN, Utils.createItem(Material.DIAMOND_SWORD, 401, "FUCKME", "&7Gives a dildo so you can go fuck yourself.", "&7Delay: &e&l7 days."), true, false, "fuckme_cc", null),
    SANIC(UserRank.ADMIN, Utils.createItem(Material.LEATHER_BOOTS, "SANIC", "&7Gives speed II for 5 minutes.", "&7Delay: &e&l1 hour."), true, false, "sanic_cc", null),
    YOUCANTSEEME(UserRank.ADMIN, Utils.createItem(Material.GLASS, "YOUCANTSEEME", "&7Gain invisibility for 3 minutes.", "&7Delay: &e&l1 hour."), true, false, "youcantseeme_cc", null),
    WINGSUIT(UserRank.ADMIN, Utils.createItem(Material.ELYTRA, "WINGSUIT", "&7Recieve a single wingsuit", "&7Delay: &e&l3 days."), true, false, "wingsuit_cc", null),
    FIXHAND(UserRank.VIP, Utils.createItem(Material.STICK, "FIXHAND", "&7Gain access to the /fix hand command."), true, false, null, null),
    FIXALL(UserRank.SUPREME, Utils.createItem(Material.CHEST, "FIXALL", "&7Have the ability to do /fix all."), true, false, null, null),
    STACK(UserRank.ADMIN, Utils.createItem(Material.GOLD_INGOT, "STACK", "&7Be able to do the /stack command."), true, false, null, null),
    VILLAGERJOB(UserRank.ADMIN, Utils.createItem(Material.MONSTER_EGG,120, "Villager Job", "&7Shift right clicking a villager will give you the ability to change their profession.", "&7Delay: &e1 hour."), true, false, null, null),
    QUICKSELL(UserRank.ELITE, Utils.createItem(Material.DISPENSER, "QUICK SELL", "&7Have the ability to sell items", "&7without having to travel back", "&7to spawn!"), true, false, null, "&7To unlock this cheatcode, purchase the &c&lELITE&7 rank or the &2&lQUICKSELL&7 cheatcode at &a&lstore.grandtheftmc.net&7!"),
    ;

    private final ItemStack displayItem;
    private final boolean toggleable, defaultToggle;
    private final UserRank rank;
    private String cooldownID, lockedLore;

    CheatCode(UserRank rank, ItemStack displayItem, boolean defaultToggle, boolean toggleable, String cooldownID, String lockedLore) {
        ItemMeta im = displayItem.getItemMeta();
        im.setUnbreakable(true);
        im.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE);
        displayItem.setItemMeta(im);
        this.defaultToggle = defaultToggle;
        this.lockedLore = Utils.f(lockedLore==null ? "&7You can purchase this cheatcode at &a&lstore.grandtheftmc.net&7!" : lockedLore);
        this.displayItem = displayItem;
        this.toggleable = toggleable;
        this.rank = rank;
        this.cooldownID = cooldownID;
    }

    public UserRank getMinmumRank() {
        return rank;
    }

    public State getDefaultState(){
        return this.defaultToggle ? State.ON : State.OFF;
    }

    public String getLockedLore() {
        return this.lockedLore;
    }

    public boolean isToggleable() {
        return toggleable;
    }

    public ItemStack getDisplayItem(User user, State state) {
        ItemStack is = displayItem.clone();
        ItemMeta im = is.getItemMeta();

        if(state==State.LOCKED) {
            ArrayList<String> lore = new ArrayList<>(im.getLore());
            lore.add(" ");
            lore.add(Utils.f("&4&lLOCKED"));
            if(this.rank.isHigherThan(UserRank.DEFAULT) && !this.rank.isHigherThan(UserRank.YOUTUBER)) {
                lore.add(Utils.f("&4&lUnlocked with rank: " + rank.getColoredNameBold()));
            }
            im.setDisplayName(Utils.f(getStateColor(state) + im.getDisplayName()));
            im.setLore(lore);
            is.setItemMeta(im);
            return is;
        }

        if(user!=null && this.cooldownID!=null) {
            boolean stillOnCooldown = user.isOnCooldown(this.cooldownID);
            state = stillOnCooldown ? State.OFF : State.ON;
            List<String> lore = new ArrayList<>(im.getLore());
            if(stillOnCooldown)
                lore.set(1, Utils.f("&7Delay: &e&l" + Utils.timeInSecondsToText(user.getCooldownTimeLeft(this.cooldownID), C.RED, C.RED, C.GRAY) + "&7."));
        }
        im.setDisplayName(Utils.f(getStateColor(state) + im.getDisplayName()));
        is.setItemMeta(im);
        return is;
    }

    public static String seralizeCheatCodes(HashMap<CheatCode, CheatCodeState> codes) {
        StringBuilder sb = new StringBuilder();
        codes.forEach((codea, statea) -> sb.append(codea.toString() + "#" + statea.getState() + "#" + statea.isPurchased() + "-"));
        sb.deleteCharAt(sb.length()-1);
        return sb.toString();
    }
    
    public void activate(User coreUser, ViceUser user, Player player, CheatCodeState cState) {
        if(user.isArrested()) {
            player.sendMessage(Lang.CHEAT_CODES.f("&7You cannot use this cheat code while you are arrested!"));
            return;
        }
        if (cState.getState() == State.LOCKED) {
            player.sendMessage(Lang.CHEAT_CODES.f("&7You haven't unlocked this cheat code yet!"));
            return;
        }
        if(!this.isToggleable()){//the non-toggleables
            if(coreUser.isOnCooldown(this.cooldownID)) {
//                player.sendMessage(Lang.CHEAT_CODES.f("&7You must wait &a" + coreUser.getFormattedCooldown(this.cooldownID, true) + " &7before using this cheatcode again!"));
                player.sendMessage(Lang.CHEAT_CODES.f("&7You must wait &a" + Utils.timeInSecondsToText(coreUser.getCooldownTimeLeft(this.cooldownID), C.RED, C.RED, C.GRAY) + " &7before using this cheatcode again!"));
                return;
            }
            switch (this) {
                case KATANA:
                    if(Utils.giveItems(player, Vice.getItemManager().getItem("katana").getItem())) {
                        player.sendMessage(Utils.f(Lang.CHEAT_CODES + "&cYour inventory was full so some items were dropped on the ground!"));
                    }
                    coreUser.addCooldown(this.cooldownID, 60*60*24*3, true, true);
                    player.sendMessage(Lang.CHEAT_CODES.f("&7You have used your katana cheat code!"));
                    break;
                case FUCKME:
                    if(Utils.giveItems(player, Vice.getItemManager().getItem("dildo").getItem())) {
                        player.sendMessage(Utils.f(Lang.CHEAT_CODES + "&cYour inventory was full so some items were dropped on the ground!"));
                    }
                    coreUser.addCooldown(this.cooldownID, 60*60*24*7, true, true);
                    player.sendMessage(Lang.CHEAT_CODES.f("&7You have used your fuck me cheat code!"));
                    break;
                case WINGSUIT:
                    if(Utils.giveItems(player, Vice.getItemManager().getItem("wingsuit").getItem())) {
                        player.sendMessage(Utils.f(Lang.CHEAT_CODES + "&cYour inventory was full so some items were dropped on the ground!"));
                    }
                    coreUser.addCooldown(this.cooldownID, 60*60*24*3, true, true);
                    player.sendMessage(Lang.CHEAT_CODES.f("&7You have used your wingsuit cheat code!"));
                    break;
                case SANIC:
                    coreUser.addCooldown(this.cooldownID, 60*60, false, true);
                    player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20*60*5, 1));
                    player.sendMessage(Lang.CHEAT_CODES.f("&7You have used your sanic cheat code!"));
                    break;
                case YOUCANTSEEME:
                    coreUser.addCooldown(this.cooldownID, 60*60, false, true);
                    player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 20*60*3, 0));
                    player.sendMessage(Lang.CHEAT_CODES.f("&7You have used your you can't see me cheat code!"));
                    break;
                case STACK:
                    player.chat("/stack");
                    break;
                case FIXALL:
                    player.chat("/fix all");
                    break;
                case FIXHAND:
                    player.chat("/fix hand");
                    break;
                case FEED:
                    player.chat("/feed");
                    break;
                case QUICKSELL:
                    player.chat("/qsell");
                    break;
            }
        }
        else {
            player.sendMessage(Lang.CHEAT_CODES.f("&7You turrned " + (cState.getState() ==State.OFF ? "&a&lon &7" : "&c&loff &7") + (this.toString().toLowerCase().replace("_", " "))));
            switch (this) {//only the toggleable ones that need effects NOW
                case NIGHTVIS:
                    if (cState.getState() == State.OFF)
                        player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 0));
                    else if (cState.getState() == State.ON && player.hasPotionEffect(PotionEffectType.NIGHT_VISION))
                        player.removePotionEffect(PotionEffectType.NIGHT_VISION);
                    break;
                case JELLYLEGS:
                    break;
            }
        }
        user.setCheatCodeState(this, new CheatCodeState(cState.getState()==State.ON && this.isToggleable() ? State.OFF : State.ON, cState.isPurchased()));
    }

    private String getStateColor(State state){
        switch (state) {
            case ON:
                return "&a&l";
            case OFF:
                return "&c&l";
            case LOCKED:
                return "&4&l";
        }
        return null;
    }

    public static Optional<CheatCode> getCheatCodeFromItemStack(ItemStack stack){
        if(stack==null)
            return Optional.empty();
        return Arrays.stream(CheatCode.values()).filter(code -> code.getDisplayItem(null, State.ON).getType()==stack.getType() && code.getDisplayItem(null, State.ON).getDurability()==stack.getDurability()).findFirst();
    }

}
