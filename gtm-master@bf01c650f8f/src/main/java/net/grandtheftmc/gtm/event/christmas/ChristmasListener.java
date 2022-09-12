package net.grandtheftmc.gtm.event.christmas;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.Lang;
import net.grandtheftmc.core.Utils;
import net.grandtheftmc.core.menus.MenuManager;
import net.grandtheftmc.core.users.User;
import net.grandtheftmc.core.users.UserRank;
import net.grandtheftmc.core.users.eventtag.EventTag;
import net.grandtheftmc.core.util.ServerUtil;
import net.grandtheftmc.gtm.GTM;
import net.grandtheftmc.gtm.items.GameItem;
import net.grandtheftmc.gtm.users.ChatAction;
import net.grandtheftmc.gtm.users.GTMUser;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by Timothy Lampen on 1/2/2018.
 */
public class ChristmasListener implements Listener {

    private Set<UUID> naughtyPlayers = new HashSet<>();


    public ChristmasListener(){
        new BukkitRunnable(){
            @Override
            public void run() {
                for(UUID uuid : naughtyPlayers){
                    Player player = Bukkit.getPlayer(uuid);
                    if(player==null || !player.getWorld().getName().equals("minesantos"))
                        continue;
                    Item item = player.getWorld().dropItem(player.getLocation(), new ItemStack(Material.BLAZE_POWDER));

                    try {
                        Field itemField = item.getClass().getDeclaredField("item");
                        Field ageField;
                        Object entityItem;

                        itemField.setAccessible(true);
                        entityItem = itemField.get(item);

                        ageField = entityItem.getClass().getDeclaredField("age");
                        ageField.setAccessible(true);
                        ageField.set(entityItem, 5700);//last for 15 sec
                    }
                    catch (NoSuchFieldException | IllegalAccessException e) {
                        e.printStackTrace();
                    }

                    item.setPickupDelay(Integer.MAX_VALUE);
                }
            }
        }.runTaskTimer(GTM.getInstance(), 0, 20*5);
    }


    @EventHandler(priority = EventPriority.LOW)
    public void onDeath(PlayerDeathEvent event){
        Player player= event.getEntity();
        if(player.getKiller()==null)
            return;
        if(player.getKiller().equals(player))
            return;
        if(!this.naughtyPlayers.contains(player.getUniqueId()))
            return;
        player.getWorld().dropItem(player.getLocation(), GTM.getItemManager().getItem("candycane").getItem(2));
        this.naughtyPlayers.remove(player.getUniqueId());

        Player killer = player.getKiller();
        killer.sendMessage(Lang.CHRISTMAS.f("&aThank you &b" + killer.getName() + "&a! Christmas has been saved! Here are some candy canes for your trouble."));
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onChat(AsyncPlayerChatEvent e){
        Player player = e.getPlayer();
        String msg = e.getMessage();
        GTMUser user = GTM.getUserManager().getLoadedUser(player.getUniqueId());
        User coreUser = Core.getUserManager().getLoadedUser(player.getUniqueId());
        if(user.getCurrentChatAction()==null || user.getCurrentChatAction()!= ChatAction.SANTA_NAUGHTY_LIST)
            return;
        e.setCancelled(true);
        ServerUtil.runTask(() -> {
            switch (user.getCurrentChatAction()) {
                case SANTA_NAUGHTY_LIST: {
                    if(msg.equalsIgnoreCase("cancel")) {
                        user.clearCurrentChatAction();
                        player.sendMessage(Lang.CHRISTMAS.f("&7&oThe list is unimpressed with your choice."));
                        return;
                    }
                    Player target = Bukkit.getPlayer(msg);
                    if (target == null) {
                        player.sendMessage(Lang.CHRISTMAS.f("&7&oThe list can't find that player online, type 'cancel' to stop the selection."));
                        user.resetCurrentChatActionTimer(null);
                        return;
                    }
                    if(target.equals(player)) {
                        player.sendMessage(Lang.CHRISTMAS.f("&7&oSanta doesn't like masochists... Pick someone other than yourself!"));
                        user.resetCurrentChatActionTimer(null);
                        return;
                    }
                    if(!coreUser.getUnlockedTags().contains(EventTag.NAUGHTY)) {
                        coreUser.giveEventTag(EventTag.NAUGHTY);
                        player.sendMessage(Lang.CHRISTMAS.f("&aYou have been given the " + EventTag.NAUGHTY.getBoldName() + "&a tag. &7Select your active tag by going into Phone -> Account -> Unlocked Tags.\n&7Make sure that the '&6Show Game Rank&7' preference is toggled on."));
                    }
                    coreUser.addCooldown("naughty_list", 60 * 10, true, true);
                    Bukkit.broadcastMessage(Lang.CHRISTMAS.f("&cOh no! Santa just recieved a report that &b" + target.getName() + " &chas been a &4&lNAUGHTY &cperson! Santa will give anyone who kills him &a2 candy canes &cfor their troubles. The player will be marked with a blaze powder trail."));
                    this.naughtyPlayers.add(target.getUniqueId());
                    user.clearCurrentChatAction();
                    return;
                }
            }
        });
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e){
        Player player = e.getPlayer();
        ItemStack item = e.getItem();
        User user = Core.getUserManager().getLoadedUser(player.getUniqueId());
        GTMUser gtmUser = GTM.getUserManager().getLoadedUser(player.getUniqueId());
        if (e.getAction() != Action.RIGHT_CLICK_AIR && e.getAction() != Action.RIGHT_CLICK_BLOCK)
            return;



        //handle the custom items
        if (item != null && player.getInventory().getItemInMainHand().equals(item)) {
            if(item.getType()== Material.MAGMA_CREAM && item.hasItemMeta() && item.getItemMeta().getDisplayName().contains("Snowball")) {
                if(player.getWorld().getName().equalsIgnoreCase("spawn")) {
                    player.sendMessage(Lang.CHRISTMAS.f("&cYou cannot use this item at spawn!"));
                    return;
                }
                if(item.getAmount()==1)
                    player.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
                else
                    item.setAmount(item.getAmount()-1);
                player.updateInventory();

                Location loc = player.getEyeLocation().toVector().add(player.getLocation().getDirection().multiply(2)).toLocation(player.getWorld(), player.getLocation().getYaw(), player.getLocation().getPitch());
                Item cube = player.getWorld().dropItem(loc, new ItemStack(Material.MAGMA_CREAM));
                cube.setVelocity(player.getEyeLocation().getDirection().multiply(2));
                cube.setPickupDelay(Integer.MAX_VALUE);

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        if(cube.isOnGround() || !cube.isValid()) {
                            cube.remove();
                            cancel();
                            return;
                        }
                        for(Entity e : cube.getNearbyEntities(.3,1.2,.3)){
                            if(e.getType() == EntityType.PLAYER){
                                Player player = (Player)e;
                                player.getWorld().playEffect(cube.getLocation(), Effect.EXPLOSION_LARGE, 1, 1);
                                player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20*5, 1));
                                cube.remove();
                                cancel();
                                return;
                            }
                        }
                    }
                }.runTaskTimer(GTM.getInstance(), 3,1);
            }
            //game items
            GameItem gItem = GTM.getItemManager().getItem(item);
            if(gItem==null)
                return;
            switch (gItem.getName()) {
                case "candycane": {
                    e.setCancelled(true);
                    MenuManager.openMenu(player, "christmasshop");
                    return;
                }
                case "santanicelist": {
                    e.setCancelled(true);
                    if(player.getWorld().getName().equalsIgnoreCase("spawn")) {
                        player.sendMessage(Lang.CHRISTMAS.f("&cYou cannot use this item at spawn!"));
                        return;
                    }
                    if (player.getLocation().getY()<player.getWorld().getHighestBlockYAt(player.getLocation())) {
                        player.sendMessage(Lang.CHRISTMAS.f("&cYou must have a clear path to the sky to spawn the present drop!"));
                        return;
                    }
                    if(!user.getUnlockedTags().contains(EventTag.NICE)) {
                        player.sendMessage(Lang.CHRISTMAS.f("&aYou have been given the " + EventTag.NICE.getBoldName() + " tag. &7Select your active tag by going into Phone -> Account -> Unlocked Tags.\n&7Make sure that the '&6Show Game Rank&7' preference is toggled off."));
                        user.giveEventTag(EventTag.NICE);
                    }
                    if(item.getAmount()==1)
                        player.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
                    else
                        item.setAmount(item.getAmount()-1);
                    Bukkit.broadcastMessage(Lang.CHRISTMAS.f("&6Thank you &a" + player.getName() + " &6for spawning a santa drop for the server!"));
                    new SpawnSantaDropTask(player.getWorld().getHighestBlockAt(player.getLocation()).getLocation().clone().add(0,1,0));
                    player.updateInventory();
                    return;
                }
                case "santanaughtylist": {
                    e.setCancelled(true);
                    if(!user.getUserRank().isHigherThan(UserRank.ADMIN)) {
                        player.sendMessage(Lang.CHRISTMAS.f("&7Players cannot use this item. Have a cookie instead!"));
                        player.getInventory().setItemInMainHand(Utils.createItem(Material.COOKIE, "&4&lExtremely &cSalty Cookie"));
                        player.updateInventory();
                        return;
                    }
                    if (user.isOnCooldown("naughty_list")) {
                        player.sendMessage(Lang.CHRISTMAS.f("&cYou must wait " + user.getFormattedCooldown("naughty_list") + "&c to use this item."));
                        return;
                    }
                    player.sendMessage(Lang.CHRISTMAS.f("&7&oThe list in your hand asks you to enter the name of the player who is naughty into chat"));
                    gtmUser.setCurrentChatAction(ChatAction.SANTA_NAUGHTY_LIST, null);
                    return;
                }
                case "christmascake": {
                    e.setCancelled(true);

                    player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 30*20, 0), true);
                    player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 30*20, 0), true);

                    boolean destroy = ThreadLocalRandom.current().nextInt(0,10) == 0;
                    if(destroy) {
                        player.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
                        player.sendMessage(Lang.CHRISTMAS.f("&7Wow you're fat... You just ate the whole cake!"));
                        player.updateInventory();
                    }
                    return;
                }
            }
        }
    }

}
