package net.grandtheftmc.houses.houses;

import org.bukkit.Material;

import java.util.ArrayList;
import java.util.Collection;

public enum Blocks {

    STONE(Material.STONE, (byte)0),
    GRASS(Material.GRASS, (byte)0),
    DIRT(Material.DIRT, (byte)0),
    COBBLESTONE(Material.COBBLESTONE, (byte)0),
    OAK_WOOD(Material.WOOD, (byte)0),
    SPRUCE_WOOD(Material.WOOD, (byte)1),
    BIRCH_WOOD(Material.WOOD, (byte)2),
    JUNGLE_WOOD(Material.WOOD, (byte)3),
    ACACIA_WOOD(Material.WOOD, (byte)4),
    DARKOAK_WOOD(Material.WOOD, (byte)5),
    SAND(Material.SAND, (byte)0),
    GRAVEL(Material.GRAVEL, (byte)0),
    LOG(Material.LOG, (byte)0),
    GLASS(Material.THIN_GLASS, (byte)0),
    GLOWSTONE(Material.GLOWSTONE, (byte)0),
    SEA_LANTERN(Material.SEA_LANTERN, (byte)0),
    COAL_BLOCK(Material.COAL_BLOCK, (byte)0),
    GLASS_0(Material.STAINED_GLASS, (byte)0),
    GLASS_7(Material.STAINED_GLASS, (byte)7),
    GLASS_8(Material.STAINED_GLASS, (byte)8),
    CARPET_0(Material.CARPET, (byte)0),
    CARPET_1(Material.CARPET, (byte)1),
    CARPET_2(Material.CARPET, (byte)2),
    CARPET_3(Material.CARPET, (byte)3),
    CARPET_4(Material.CARPET, (byte)4),
    CARPET_6(Material.CARPET, (byte)6),
    CARPET_7(Material.CARPET, (byte)7),
    CARPET_8(Material.CARPET, (byte)8),
    CARPET_9(Material.CARPET, (byte)9),
    CARPET_10(Material.CARPET, (byte)10),
    CARPET_11(Material.CARPET, (byte)11),
    CARPET_12(Material.CARPET, (byte)12),
    CARPET_13(Material.CARPET, (byte)13),
    CARPET_14(Material.CARPET, (byte)14),
    CARPET_15(Material.CARPET, (byte)15),
    CLAY_0(Material.STAINED_CLAY, (byte)0),
    CLAY_1(Material.STAINED_CLAY, (byte)1),
    CLAY_2(Material.STAINED_CLAY, (byte)2),
    CLAY_3(Material.STAINED_CLAY, (byte)3),
    CLAY_4(Material.STAINED_CLAY, (byte)4),
    CLAY_5(Material.STAINED_CLAY, (byte)5),
    CLAY_6(Material.STAINED_CLAY, (byte)6),
    CLAY_7(Material.STAINED_CLAY, (byte)7),
    CLAY_8(Material.STAINED_CLAY, (byte)8),
    CLAY_9(Material.STAINED_CLAY, (byte)9),
    CLAY_10(Material.STAINED_CLAY, (byte)10),
    CLAY_11(Material.STAINED_CLAY, (byte)11),
    CLAY_12(Material.STAINED_CLAY, (byte)12),
    CLAY_13(Material.STAINED_CLAY, (byte)13),
    CLAY_14(Material.STAINED_CLAY, (byte)14),
    CLAY_15(Material.STAINED_CLAY, (byte)15),
    DIAMOND_BLOCK(Material.DIAMOND_BLOCK, (byte)0);

    public static Collection<Material> materials;
    private Material type;
    private byte data;

    Blocks(Material type, byte data) {
        this.type = type;
        this.data = data;
    }

    public static Collection<Material> getMaterials() {
        if(materials == null || materials.isEmpty()) {
            materials = new ArrayList<>();
            for (Blocks mat : values()) {
                materials.add(mat.getType());
            }
        }
        return materials;
    }

    public static Blocks match(Material material, Byte data) {
        for(Blocks block : Blocks.values()) {
            if(block.getType() == material && block.getData() == data) return block;
        }
        return null;
    }

    public Material getType() {
        return this.type;
    }

    public byte getData() {
        return data;
    }
}
