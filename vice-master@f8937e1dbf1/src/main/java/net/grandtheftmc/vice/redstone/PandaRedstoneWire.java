package net.grandtheftmc.vice.redstone;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import net.grandtheftmc.vice.utils.ReflectUtil;
import net.minecraft.server.v1_12_R1.*;
import org.apache.commons.lang.ArrayUtils;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockRedstoneEvent;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * @author md_5
 * @author Teddeh
 */
public class PandaRedstoneWire extends BlockRedstoneWire {

    private List<BlockPosition> turnOff;
    private List<BlockPosition> turnOn;
    private final Set<BlockPosition> updatedRedstoneWire;
    private static final EnumDirection[] facingsHorizontal;
    private static final EnumDirection[] facingsVertical;
    private static final EnumDirection[] facings;
    private static final BaseBlockPosition[] surroundingBlocksOffset;
    private boolean g;

    public PandaRedstoneWire() {
        this.turnOff = Lists.newArrayList();
        this.turnOn = Lists.newArrayList();
        this.updatedRedstoneWire = Sets.newLinkedHashSet();
        this.g = true;
        this.c(0.0f);
        this.a(SoundEffectType.d);
        this.c("redstoneDust");
        this.p();
    }

    private void e(final World world, final BlockPosition blockposition) {
        this.calculateCurrentChanges(world, blockposition);
        final Set<BlockPosition> blocksNeedingUpdate = Sets.newLinkedHashSet();
        for (final BlockPosition posi : this.updatedRedstoneWire) {
            this.addBlocksNeedingUpdate(world, posi, blocksNeedingUpdate);
        }
        final Iterator<BlockPosition> it = Lists.newLinkedList(this.updatedRedstoneWire).descendingIterator();
        while (it.hasNext()) {
            this.addAllSurroundingBlocks(it.next(), blocksNeedingUpdate);
        }
        blocksNeedingUpdate.removeAll(this.updatedRedstoneWire);
        this.updatedRedstoneWire.clear();
        for (final BlockPosition posi2 : blocksNeedingUpdate) {
            world.a(posi2, this, blockposition);
        }
    }

    private void calculateCurrentChanges(final World world, final BlockPosition blockposition) {
        if (world.getType(blockposition).getBlock() == this) {
            this.turnOff.add(blockposition);
        }
        else {
            this.checkSurroundingWires(world, blockposition);
        }
        while (!this.turnOff.isEmpty()) {
            final BlockPosition pos = this.turnOff.remove(0);
            IBlockData state = world.getType(pos);
            final int oldPower = state.get(PandaRedstoneWire.POWER);
            this.g = false;
            final int blockPower = world.z(pos);
            this.g = true;
            int wirePower = this.getSurroundingWirePower(world, pos);
            --wirePower;
            int newPower = Math.max(blockPower, wirePower);
            if (oldPower != newPower) {
                final BlockRedstoneEvent event = new BlockRedstoneEvent(world.getWorld().getBlockAt(pos.getX(), pos.getY(), pos.getZ()), oldPower, newPower);
                world.getServer().getPluginManager().callEvent(event);
                newPower = event.getNewCurrent();
            }
            if (newPower < oldPower) {
                if (blockPower > 0 && !this.turnOn.contains(pos)) {
                    this.turnOn.add(pos);
                }
                state = this.setWireState(world, pos, state, 0);
            }
            else if (newPower > oldPower) {
                state = this.setWireState(world, pos, state, newPower);
            }
            this.checkSurroundingWires(world, pos);
        }
        while (!this.turnOn.isEmpty()) {
            final BlockPosition pos = this.turnOn.remove(0);
            IBlockData state = world.getType(pos);
            final int oldPower = state.get(PandaRedstoneWire.POWER);
            this.g = false;
            final int blockPower = world.z(pos);
            this.g = true;
            int wirePower = this.getSurroundingWirePower(world, pos);
            --wirePower;
            int newPower = Math.max(blockPower, wirePower);
            if (oldPower != newPower) {
                final BlockRedstoneEvent event = new BlockRedstoneEvent(world.getWorld().getBlockAt(pos.getX(), pos.getY(), pos.getZ()), oldPower, newPower);
                world.getServer().getPluginManager().callEvent(event);
                newPower = event.getNewCurrent();
            }
            if (newPower > oldPower) {
                state = this.setWireState(world, pos, state, newPower);
            }
            else if (newPower < oldPower) {}
            this.checkSurroundingWires(world, pos);
        }
        this.turnOff.clear();
        this.turnOn.clear();
    }

    private void addWireToList(final World worldIn, final BlockPosition pos, final int otherPower) {
        final IBlockData state = worldIn.getType(pos);
        if (state.getBlock() == this) {
            final int power = state.get(PandaRedstoneWire.POWER);
            if (power < otherPower - 1 && !this.turnOn.contains(pos)) {
                this.turnOn.add(pos);
            }
            if (power > otherPower && !this.turnOff.contains(pos)) {
                this.turnOff.add(pos);
            }
        }
    }

    private void checkSurroundingWires(final World worldIn, final BlockPosition pos) {
        final IBlockData state = worldIn.getType(pos);
        int ownPower = 0;
        if (state.getBlock() == this) {
            ownPower = state.get(PandaRedstoneWire.POWER);
        }
        for (final EnumDirection facing : PandaRedstoneWire.facingsHorizontal) {
            final BlockPosition offsetPos = pos.shift(facing);
            if (facing.k().c()) {
                this.addWireToList(worldIn, offsetPos, ownPower);
            }
        }
        for (final EnumDirection facingVertical : PandaRedstoneWire.facingsVertical) {
            final BlockPosition offsetPos = pos.shift(facingVertical);
            final boolean solidBlock = worldIn.getType(offsetPos).k();
            for (final EnumDirection facingHorizontal : PandaRedstoneWire.facingsHorizontal) {
                if ((facingVertical == EnumDirection.UP && !solidBlock) || (facingVertical == EnumDirection.DOWN && solidBlock && !worldIn.getType(offsetPos.shift(facingHorizontal)).k())) {
                    this.addWireToList(worldIn, offsetPos.shift(facingHorizontal), ownPower);
                }
            }
        }
    }

    private int getSurroundingWirePower(final World worldIn, final BlockPosition pos) {
        int wirePower = 0;
        for (final EnumDirection enumfacing : EnumDirection.EnumDirectionLimit.HORIZONTAL) {
            final BlockPosition offsetPos = pos.shift(enumfacing);
            wirePower = this.getPower(worldIn, offsetPos, wirePower);
            if (worldIn.getType(offsetPos).l() && !worldIn.getType(pos.up()).l()) {
                wirePower = this.getPower(worldIn, offsetPos.up(), wirePower);
            }
            else {
                if (worldIn.getType(offsetPos).l()) {
                    continue;
                }
                wirePower = this.getPower(worldIn, offsetPos.down(), wirePower);
            }
        }
        return wirePower;
    }

    private void addBlocksNeedingUpdate(final World worldIn, final BlockPosition pos, final Set<BlockPosition> set) {
        final List<EnumDirection> connectedSides = this.getSidesToPower(worldIn, pos);
        for (final EnumDirection facing : PandaRedstoneWire.facings) {
            final BlockPosition offsetPos = pos.shift(facing);
            if ((connectedSides.contains(facing.opposite()) || facing == EnumDirection.DOWN || (facing.k().c() && a(worldIn.getType(offsetPos), facing))) && this.canBlockBePoweredFromSide(worldIn.getType(offsetPos), facing, true)) {
                set.add(offsetPos);
            }
        }
        for (final EnumDirection facing : PandaRedstoneWire.facings) {
            final BlockPosition offsetPos = pos.shift(facing);
            if ((connectedSides.contains(facing.opposite()) || facing == EnumDirection.DOWN) && worldIn.getType(offsetPos).l()) {
                for (final EnumDirection facing2 : PandaRedstoneWire.facings) {
                    if (this.canBlockBePoweredFromSide(worldIn.getType(offsetPos.shift(facing2)), facing2, false)) {
                        set.add(offsetPos.shift(facing2));
                    }
                }
            }
        }
    }

    private boolean canBlockBePoweredFromSide(final IBlockData state, final EnumDirection side, final boolean isWire) {
        if (state.getBlock() instanceof BlockPiston && state.get(BlockPiston.FACING) == side.opposite()) {
            return false;
        }
        if (state.getBlock() instanceof BlockDiodeAbstract && state.get(BlockDiodeAbstract.FACING) != side.opposite()) {
            return isWire && state.getBlock() instanceof BlockRedstoneComparator && (state.get(BlockRedstoneComparator.FACING)).k() != side.k() && side.k().c();
        }
        return !(state.getBlock() instanceof BlockRedstoneTorch) || (!isWire && state.get(BlockRedstoneTorch.FACING) == side);
    }

    private List<EnumDirection> getSidesToPower(final World worldIn, final BlockPosition pos) {
        final List retval = Lists.newArrayList();
        for (final EnumDirection facing : PandaRedstoneWire.facingsHorizontal) {
            if (this.b(worldIn, pos, facing)) {
                retval.add(facing);
            }
        }
        if (retval.isEmpty()) {
            return Lists.newArrayList(PandaRedstoneWire.facingsHorizontal);
        }
        final boolean northsouth = retval.contains(EnumDirection.NORTH) || retval.contains(EnumDirection.SOUTH);
        final boolean eastwest = retval.contains(EnumDirection.EAST) || retval.contains(EnumDirection.WEST);
        if (northsouth) {
            retval.remove(EnumDirection.EAST);
            retval.remove(EnumDirection.WEST);
        }
        if (eastwest) {
            retval.remove(EnumDirection.NORTH);
            retval.remove(EnumDirection.SOUTH);
        }
        return retval;
    }

    private void addAllSurroundingBlocks(final BlockPosition pos, final Set<BlockPosition> set) {
        for (final BaseBlockPosition vect : PandaRedstoneWire.surroundingBlocksOffset) {
            set.add(pos.a(vect));
        }
    }

    private IBlockData setWireState(final World worldIn, final BlockPosition pos, IBlockData state, final int power) {
        state = state.set(PandaRedstoneWire.POWER, power);
        worldIn.setTypeAndData(pos, state, 2);
        this.updatedRedstoneWire.add(pos);
        return state;
    }

    public void onPlace(final World world, final BlockPosition blockposition, final IBlockData iblockdata) {
        if (!world.isClientSide) {
            this.e(world, blockposition);
            for (final EnumDirection enumdirection : EnumDirection.EnumDirectionLimit.VERTICAL) {
                world.applyPhysics(blockposition.shift(enumdirection), this, false);
            }
            for (final EnumDirection enumdirection : EnumDirection.EnumDirectionLimit.HORIZONTAL) {
                this.b(world, blockposition.shift(enumdirection));
            }
            for (final EnumDirection enumdirection : EnumDirection.EnumDirectionLimit.HORIZONTAL) {
                final BlockPosition blockposition2 = blockposition.shift(enumdirection);
                if (world.getType(blockposition2).l()) {
                    this.b(world, blockposition2.up());
                }
                else {
                    this.b(world, blockposition2.down());
                }
            }
        }
    }

    public void remove(final World world, final BlockPosition blockposition, final IBlockData iblockdata) {
        super.remove(world, blockposition, iblockdata);
        if (!world.isClientSide) {
            for (final EnumDirection enumdirection : EnumDirection.values()) {
                world.applyPhysics(blockposition.shift(enumdirection), this, false);
            }
            this.e(world, blockposition);
            for (final EnumDirection enumdirection2 : EnumDirection.EnumDirectionLimit.HORIZONTAL) {
                this.b(world, blockposition.shift(enumdirection2));
            }
            for (final EnumDirection enumdirection2 : EnumDirection.EnumDirectionLimit.HORIZONTAL) {
                final BlockPosition blockposition2 = blockposition.shift(enumdirection2);
                if (world.getType(blockposition2).l()) {
                    this.b(world, blockposition2.up());
                }
                else {
                    this.b(world, blockposition2.down());
                }
            }
        }
    }

    public void a(final IBlockData iblockdata, final World world, final BlockPosition blockposition, final Block block, final BlockPosition blockposition1) {
        if (!world.isClientSide) {
            if (this.canPlace(world, blockposition)) {
                this.e(world, blockposition);
            }
            else {
                this.b(world, blockposition, iblockdata, 0);
                world.setAir(blockposition);
            }
        }
    }

    public int b(final IBlockData iblockdata, final IBlockAccess iblockaccess, final BlockPosition blockposition, final EnumDirection enumdirection) {
        if (!this.g) {
            return 0;
        }
        final int i = iblockdata.get(BlockRedstoneWire.POWER);
        if (i == 0) {
            return 0;
        }
        if (enumdirection == EnumDirection.UP) {
            return i;
        }
        if (this.getSidesToPower((World)iblockaccess, blockposition).contains(enumdirection)) {
            return i;
        }
        return 0;
    }

    private boolean b(final IBlockAccess iblockaccess, final BlockPosition blockposition, final EnumDirection enumdirection) {
        final BlockPosition blockposition2 = blockposition.shift(enumdirection);
        final IBlockData iblockdata = iblockaccess.getType(blockposition2);
        final boolean flag = iblockdata.l();
        final boolean flag2 = iblockaccess.getType(blockposition.up()).l();
        return (!flag2 && flag && c(iblockaccess, blockposition2.up())) || a(iblockdata, enumdirection) || (iblockdata.getBlock() == Blocks.POWERED_REPEATER && iblockdata.get(BlockDiodeAbstract.FACING) == enumdirection) || (!flag && c(iblockaccess, blockposition2.down()));
    }

    static {
        facingsHorizontal = new EnumDirection[] { EnumDirection.WEST, EnumDirection.EAST, EnumDirection.NORTH, EnumDirection.SOUTH };
        facingsVertical = new EnumDirection[] { EnumDirection.DOWN, EnumDirection.UP };
        facings = (EnumDirection[]) ArrayUtils.addAll(PandaRedstoneWire.facingsVertical, PandaRedstoneWire.facingsHorizontal);
        final Set<BaseBlockPosition> set = Sets.newLinkedHashSet();
        for (final EnumDirection facing : PandaRedstoneWire.facings) {
            set.add(ReflectUtil.getOfT(facing, BaseBlockPosition.class));
        }
        for (final EnumDirection facing2 : PandaRedstoneWire.facings) {
            final BaseBlockPosition v1 = ReflectUtil.getOfT(facing2, BaseBlockPosition.class);
            for (final EnumDirection facing3 : PandaRedstoneWire.facings) {
                final BaseBlockPosition v2 = ReflectUtil.getOfT(facing3, BaseBlockPosition.class);
                set.add(new BlockPosition(v1.getX() + v2.getX(), v1.getY() + v2.getY(), v1.getZ() + v2.getZ()));
            }
        }
        set.remove(BlockPosition.ZERO);
        surroundingBlocksOffset = set.toArray(new BaseBlockPosition[set.size()]);
    }
}
