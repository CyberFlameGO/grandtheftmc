package net.grandtheftmc.gtm.drugs;

import net.grandtheftmc.core.util.Utils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class DrugUtil {
    private static final Collection<Material> IGNORE_BLOCKS = Arrays.asList(Material.AIR, Material.SIGN, Material.SIGN_POST,
            Material.WALL_SIGN, Material.IRON_DOOR, Material.IRON_DOOR_BLOCK, Material.CHEST,
            Material.WOODEN_DOOR, Material.WOOD_DOOR, Material.WATER, Material.STATIONARY_WATER, Material.LAVA, Material.STATIONARY_LAVA);

    public static Sound getRandomParanoiaSound() {
        int roll = ThreadLocalRandom.current().nextInt(0, 5);
        switch (roll) {
            default:
            case 0:
                return Sound.ENTITY_GHAST_SCREAM;
            case 1:
                return Sound.ENTITY_ZOMBIE_AMBIENT;
            case 2:
                return Sound.ENTITY_GHAST_AMBIENT;
            case 3:
                return Sound.ENTITY_SPIDER_AMBIENT;
            case 4:
                return Sound.ENTITY_CREEPER_PRIMED;
        }
    }

    public static Sound getRandomAmbientSound() {
        int roll = ThreadLocalRandom.current().nextInt(0, 9);
        switch (roll) {
            default:
            case 0:
                return Sound.AMBIENT_CAVE;
            case 1:
                return Sound.ENTITY_RABBIT_AMBIENT;
            case 2:
                return Sound.ENTITY_BAT_AMBIENT;
            case 3:
                return Sound.ENTITY_CREEPER_PRIMED;
            case 4:
                return Sound.ENTITY_CAT_AMBIENT;
            case 5:
                return Sound.ENTITY_ENDERDRAGON_FIREBALL_EXPLODE;
            case 6:
                return Sound.BLOCK_CLOTH_BREAK;
            case 7:
                return Sound.ENTITY_ZOMBIE_INFECT;
            case 8:
                return Sound.ENTITY_ZOMBIE_PIG_ANGRY;
        }
    }

    public static String getParanoiaMessage(){
        switch(ThreadLocalRandom.current().nextInt( 10)){
            case 0:
                return "They are always watching you";
            case 1:
                return "I can see you";
            case 2:
                return "I know where you live";
            case 3:
                return "There is a camera in front of you, say hi :)";
            case 4:
                return "Don't go home, it's not safe";
            case 5:
                return "Even when you don't see me I am there";
            case 6:
                return "The computer never turns off";
            case 7:
                return "The police know what you have done";
            case 8:
                return "I can see what you do behind closed doors";
            case 9:
                return "I am the monster under your bed";
            default:
                return "";
        }
    }


    /*ublic static void sendWorldEnvironment(Player player, World.Environment environment) {
        CraftPlayer craftPlayer = (CraftPlayer) player;
        CraftWorld world = (CraftWorld) player.getWorld();
        Location location = player.getLocation();

        PacketPlayOutRespawn packet = new PacketPlayOutRespawn(environment.getId(), EnumDifficulty.getById(world.getDifficulty().getValue()), WorldType.NORMAL, EnumGamemode.getById(player.getGameMode().getValue()));

        craftPlayer.getHandle().playerConnection.sendPacket(packet);

        int viewDistance = GTM.getInstance().getServer().getViewDistance();

        int xMin = location.getChunk().getX() - viewDistance;
        int xMax = location.getChunk().getX() + viewDistance;
        int zMin = location.getChunk().getZ() - viewDistance;
        int zMax = location.getChunk().getZ() + viewDistance;

        for (int x = xMin; x < xMax; ++x){
            for (int z = zMin; z < zMax; ++z){
                world.refreshChunk(x, z);
            }
        }

        player.updateInventory();

        player.teleport(player.getLocation());
    }*/


    public static Collection<Block> getNearbyBlocks(Location location, int radius) {
        Collection<Block> blocks = new ArrayList<>();

        for (int x = location.getBlockX() - radius ; x <= location.getBlockX() + radius ; x++) {
            for (int z = location.getBlockZ() - radius ; z <= location.getBlockZ() + radius ; z++) {
                for(int y = location.getBlockY() - radius ; y <= location.getBlockY() + radius; y++) {
                    Block block = location.getWorld().getBlockAt(x, y, z);
                    if (!block.isEmpty() && !IGNORE_BLOCKS.contains(block.getType())) {
                        blocks.add(block);
                    }
                }
            }
        }
        return blocks;
    }

    public static ItemStack setDisplayName(ItemStack itemStack, String displayName) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(displayName);
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    public static ItemStack addLore(ItemStack itemStack, String... lore) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        List<String> lores = itemMeta.hasLore() ? itemMeta.getLore() : new ArrayList<>();
        for (String a : lore) lores.add(Utils.f(a));
        itemMeta.setLore(lores);
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    public static ItemStack clearLore(ItemStack itemStack) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setLore(new ArrayList<>());
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    public static ItemStack hideDurability(ItemStack itemStack) {
        ItemStack is = itemStack.clone();
        ItemMeta itemMeta = is.getItemMeta();
        itemMeta.spigot().setUnbreakable(true);
        itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES,
                ItemFlag.HIDE_UNBREAKABLE,
                ItemFlag.HIDE_PLACED_ON,
                ItemFlag.HIDE_ENCHANTS,
                ItemFlag.HIDE_POTION_EFFECTS,
                ItemFlag.HIDE_DESTROYS);
        is.setItemMeta(itemMeta);
        return is;
    }

    public static Optional<Map<Integer, ItemStack>> findItem(Inventory inventory, ItemStack itemStack) {
        Map<Integer, ItemStack> map = new HashMap<>();
        for (int i = 0; i < inventory.getSize(); i++) {
            if (inventory.getItem(i) == null) continue;
            ItemStack search = inventory.getItem(i);
            System.out.print(search.getType().toString() + " :: "
                    + itemStack.getType().toString());
            System.out.print(search.getData().getData() + " :: "
                    + itemStack.getData().getData());
            System.out.print(search.getItemMeta().getDisplayName() + " :: "
                    + itemStack.getItemMeta().getDisplayName());
            if (search.getType() == itemStack.getType()
                    && search.getData().getData() == itemStack.getData().getData()
                    && search.getItemMeta().getDisplayName().equals(itemStack.getItemMeta().getDisplayName())) {
                map.put(i, search);
                System.out.print("found");
                break;
            }
        }
        return map.isEmpty() ? Optional.empty() : Optional.of(map);
    }


}
