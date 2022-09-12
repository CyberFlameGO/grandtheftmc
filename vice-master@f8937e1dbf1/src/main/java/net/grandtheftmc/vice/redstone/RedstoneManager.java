package net.grandtheftmc.vice.redstone;

import net.grandtheftmc.vice.utils.ReflectUtil;
import net.minecraft.server.v1_12_R1.Block;
import net.minecraft.server.v1_12_R1.Blocks;
import net.minecraft.server.v1_12_R1.IBlockData;
import net.minecraft.server.v1_12_R1.MinecraftKey;

/**
 * @author md_5
 * @author Teddeh
 */
public class RedstoneManager {

    public RedstoneManager() {
        add(55, "redstone_wire", (Block)new PandaRedstoneWire());
        ReflectUtil.setStatic("REDSTONE_WIRE", Blocks.class, get("redstone_wire"));
        System.out.println("Redstone Manager has been initialised.");
    }

    private static Block get(final String s) {
        return Block.REGISTRY.get(new MinecraftKey(s));
    }

    private static void add(final int i, final String s, final Block block) {
        Block.REGISTRY.a(i, new MinecraftKey(s), block);
        for (final IBlockData iblockdata : block.s().a()) {
            final int k = Block.REGISTRY.a(block) << 4 | block.toLegacyData(iblockdata);
            Block.REGISTRY_ID.a(iblockdata, k);
        }
    }
}
