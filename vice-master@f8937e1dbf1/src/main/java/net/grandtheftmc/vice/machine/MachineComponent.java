package net.grandtheftmc.vice.machine;

import net.grandtheftmc.core.Lang;
import net.grandtheftmc.core.database.BaseDatabase;
import net.grandtheftmc.core.util.C;
import net.grandtheftmc.core.util.Component;
import net.grandtheftmc.core.util.ServerUtil;
import net.grandtheftmc.vice.Vice;
import net.grandtheftmc.vice.dao.MachineDAO;
import net.grandtheftmc.vice.items.GameItem;
import net.grandtheftmc.vice.machine.data.MachineData;
import net.grandtheftmc.vice.machine.data.MachineDataType;
import net.grandtheftmc.vice.machine.event.MachineFuelEvent;
import net.grandtheftmc.vice.machine.event.MachineItemTransferEvent;
import net.grandtheftmc.vice.machine.event.MachinePlaceEvent;
import net.grandtheftmc.vice.machine.event.MachineRecipeCompleteEvent;
import net.grandtheftmc.vice.machine.recipe.MachineRecipeManager;
import net.grandtheftmc.vice.machine.recipe.misc.RecipeOutput;
import net.grandtheftmc.vice.utils.ItemStackUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Hopper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.minecart.HopperMinecart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Optional;

public class MachineComponent implements Component<MachineComponent, Vice> {

    private final MachineManager machineManager;
    private final MachineRecipeManager recipeManager;

    public MachineComponent(MachineManager machineManager, MachineRecipeManager recipeManager) {
        this.machineManager = machineManager;
        this.recipeManager = recipeManager;

//        ProtocolLibrary.getProtocolManager().addPacketListener(new MachineChunkPacket());
    }

    @Override
    public MachineComponent onDisable(Vice plugin) {
        try (Connection connection = BaseDatabase.getInstance().getConnection()) {
            this.machineManager.getMachines().forEach(machine -> MachineDAO.updateMachineData(connection, machine));
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return this;
    }

    @EventHandler(ignoreCancelled = true)
    protected final void onBlockPlace(BlockPlaceEvent event) {
        ItemStack itemStack = event.getItemInHand();
        if (itemStack == null) return;

        ServerUtil.debug(itemStack.getType().name());
        if (!this.machineManager.isType(itemStack.getType())) return;
        if (!itemStack.hasItemMeta()) return;
        Optional<BaseMachine> optional = this.machineManager.constructByItem(itemStack);
        if (!optional.isPresent()) return;
        MachinePlaceEvent placeEvent = new MachinePlaceEvent(event.getPlayer(), event.getBlockPlaced().getLocation(), optional.get());
        Bukkit.getPluginManager().callEvent(placeEvent);
        if (placeEvent.isCancelled()) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    protected final void onMachinePlace(MachinePlaceEvent event) {
        event.getMachine().setLocation(event.getLocation());
        this.machineManager.addMachine(event.getMachine());
        ServerUtil.debug("Machine added");

        ServerUtil.runTaskAsync(() -> {
            try (Connection connection = BaseDatabase.getInstance().getConnection()) {
                MachineDAO.addMachine(connection, event.getMachine());
                MachineDAO.updateMachineData(connection, event.getMachine());
                ServerUtil.debug("Machine query complete.");

                ServerUtil.runTask(() -> event.getMachine().setEnabled(true));

            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    @EventHandler(ignoreCancelled = true)
    protected final void onBlockBreak(BlockBreakEvent event) {
        if (!this.machineManager.isType(event.getBlock().getType())) return;

        Optional<BaseMachine> optional = this.machineManager.getMachineByLocation(event.getBlock().getLocation());
        if (!optional.isPresent()) return;

        this.machineManager.removeMachine(optional.get());
        ServerUtil.runTaskAsync(() -> {
            try (Connection connection = BaseDatabase.getInstance().getConnection()) {
                MachineDAO.removeMachine(connection, optional.get().getUniqueIdentifier());
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });

        if (event.getBlock().getState() instanceof InventoryHolder) {
            InventoryHolder holder = (InventoryHolder) event.getBlock().getState();
            holder.getInventory().clear();
        }
        event.setDropItems(false);

        ItemStack machineFragment = Vice.getItemManager().getItem("machinefragment").getItem();
        ItemMeta meta = machineFragment.getItemMeta();
        meta.setLore(Arrays.asList(
                C.GRAY + C.ITALIC + " " + optional.get().getName(),
                "",
                C.AQUA + C.BOLD + "INFO",
                C.GRAY + " Visit the Machine Mechanic",
                C.GRAY + " at spawn to turn these fragments",
                C.GRAY + " into a functional machine!"
        ));

        machineFragment.setItemMeta(meta);
        machineFragment = ItemStackUtil.addTag(machineFragment, "machineid", optional.get().getMachineIdentifier());

        event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), machineFragment);

        for (int slot : optional.get().getOpenSlots()) {
            ItemStack itemStack = optional.get().getInventory().getItem(slot);
            if (itemStack == null || itemStack.getType() == Material.AIR) continue;
            event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), itemStack);
        }

        for (int slot : optional.get().getOutputSlots()) {
            ItemStack itemStack = optional.get().getInventory().getItem(slot);
            if (itemStack == null || itemStack.getType() == Material.AIR) continue;
            event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), itemStack);
        }

        for (int slot : optional.get().getData(MachineDataType.FUEL).getSlots()) {
            ItemStack itemStack = optional.get().getInventory().getItem(slot);
            if (itemStack == null || itemStack.getType() == Material.AIR) continue;
            event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), itemStack);//
        }
    }

    @EventHandler
    public void onExplode(EntityExplodeEvent event) {
        Iterator<Block> iter = event.blockList().iterator();

        while (iter.hasNext()) {
            Block b = iter.next();
            Optional<BaseMachine> optional = this.machineManager.getMachineByLocation(b.getLocation());
            if (!optional.isPresent()) continue;

            this.machineManager.removeMachine(optional.get());
            ServerUtil.runTaskAsync(() -> {
                try (Connection connection = BaseDatabase.getInstance().getConnection()) {
                    MachineDAO.removeMachine(connection, optional.get().getUniqueIdentifier());
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            });

            if (b.getState() instanceof InventoryHolder) {
                InventoryHolder holder = (InventoryHolder) b.getState();
                holder.getInventory().clear();
            }

            ItemStack machineFragment = Vice.getItemManager().getItem("machinefragment").getItem();
            ItemMeta meta = machineFragment.getItemMeta();
            meta.setLore(Arrays.asList(
                    C.GRAY + C.ITALIC + " " + optional.get().getName(),
                    "",
                    C.AQUA + C.BOLD + "INFO",
                    C.GRAY + " Visit the Machine Mechanic",
                    C.GRAY + " at spawn to turn these fragments",
                    C.GRAY + " into a functional machine!"
            ));

            machineFragment.setItemMeta(meta);
            machineFragment = ItemStackUtil.addTag(machineFragment, "machineid", optional.get().getMachineIdentifier());

            b.getWorld().dropItemNaturally(b.getLocation(), machineFragment);

            for (int slot : optional.get().getOpenSlots()) {
                ItemStack itemStack = optional.get().getInventory().getItem(slot);
                if (itemStack == null || itemStack.getType() == Material.AIR) continue;
                b.getWorld().dropItemNaturally(b.getLocation(), itemStack);
            }

            for (int slot : optional.get().getOutputSlots()) {
                ItemStack itemStack = optional.get().getInventory().getItem(slot);
                if (itemStack == null || itemStack.getType() == Material.AIR) continue;
                b.getWorld().dropItemNaturally(b.getLocation(), itemStack);
            }

            for (int slot : optional.get().getData(MachineDataType.FUEL).getSlots()) {
                ItemStack itemStack = optional.get().getInventory().getItem(slot);
                if (itemStack == null || itemStack.getType() == Material.AIR) continue;
                b.getWorld().dropItemNaturally(b.getLocation(), itemStack);//
            }

            b.setType(Material.AIR);
            iter.remove();
        }
    }

    @EventHandler(ignoreCancelled = true)
    protected final void onInteract(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) return;
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        Player player = event.getPlayer();
        Block block = event.getClickedBlock();
        if (block == null || block.getType() == Material.AIR) return;

        ItemStack hand = event.getPlayer().getInventory().getItemInMainHand();
        if (event.getPlayer().isSneaking() && (hand != null && hand.getType() != Material.AIR)) return;


        Optional<BaseMachine> optional = this.machineManager.getMachineByLocation(block.getLocation());
        if (!optional.isPresent()) return;

        event.setCancelled(true);

        GameItem gameItem = Vice.getItemManager().getItem(hand);
        if(gameItem!=null && gameItem.getName().equalsIgnoreCase("machinefragment") && ChatColor.stripColor(hand.getItemMeta().getLore().get(0)).replaceAll("\\s+","").equals(optional.get().getName().replaceAll("\\s+",""))) {
            double percentDiff = optional.get().getData(MachineDataType.DURABILITY).getCurrent() / optional.get().getData(MachineDataType.DURABILITY).getMax() * 100;
            if(percentDiff > 80) {
                player.sendMessage(Lang.VICE.f("&cThat machine is not at least 20% damaged, you'd be wasting this machine fragment!"));
            }
            else {
                MachineData durability = optional.get().getData(MachineDataType.DURABILITY);
                durability.setCurrent(durability.getCurrent() + (int)Math.round(durability.getMax() *.20));
                optional.get().updateDurability();
                player.sendMessage(Lang.VOTE.f("&6You have repaired your machine by 20% &7(&6Current Durability: &b" + Math.round(percentDiff + 20) + "%&7)"));
                hand.setAmount(hand.getAmount()-1);
                player.updateInventory();
            }
            return;
        }


        if (optional.get().isEnabled()) {
            event.getPlayer().openInventory(optional.get().getInventory());
        }
    }

    @EventHandler(ignoreCancelled = true)
    protected final void onInventoryClick(InventoryClickEvent event) {
        if (event.getInventory() == null || event.getView().getTopInventory() == null) return;
        if (event.getWhoClicked() == null) return;

//        ServerUtil.debug("[>---------------------------<]");
//        ServerUtil.debug("Action: " + event.getAction().name());
//        ServerUtil.debug("Slot: " + event.getSlot());
//        ServerUtil.debug("Raw Slot: " + event.getRawSlot());
//        ServerUtil.debug("Click Type: " + event.getClick().name());
//        ServerUtil.debug("Clicked Title: " + (event.getClickedInventory() == null ? "." : event.getClickedInventory().getTitle()));
//        ServerUtil.debug("Clicked Item: " + (event.getCurrentItem() == null ? "." : event.getCurrentItem().getType()));
//        ServerUtil.debug("Cursor Item: " + (event.getCursor() == null ? "." : event.getCursor().getType()));
//        ServerUtil.debug("Slot Type: " + event.getSlotType().name());
//        ServerUtil.debug("Hotbar Button: " + event.getHotbarButton());
//        ServerUtil.debug("[>---------------------------<]");
//        ServerUtil.debug(" ");

        Inventory top = event.getView().getTopInventory();
        if (top.getType() != InventoryType.CHEST) return;

        if(top.getHolder()!=null) return; //basically if the inventory is created out of thin air. So players can't rename their own chests.

        Optional<BaseMachine> optional = this.machineManager.getStatues().stream().filter(m -> m.getInventory().getName().equals(top.getName())).findFirst();
        if (!optional.isPresent()) return;


        if (event.isShiftClick()) {
            if (event.getRawSlot() >= 27) {
                int slot = top.firstEmpty();
                int addSlot = top.first(event.getCurrentItem().getType());

                if (optional.get().isOutputSlot(slot)) {
                    event.setCancelled(true);
                    ((Player) event.getWhoClicked()).updateInventory();
                    return;
                }

                MachineData data = optional.get().getData(MachineDataType.FUEL);
                if (data.isSlot(slot)) {
                    if (!MachineUtil.isFuelType(event.getCurrentItem().getType())) {
                        event.setCancelled(true);
                        ((Player) event.getWhoClicked()).updateInventory();
                    } else {
                        MachineFuelEvent machineFuelEvent = new MachineFuelEvent(optional.get(), data, event.getCursor().getType(), event.getRawSlot());
                        Bukkit.getPluginManager().callEvent(machineFuelEvent);
                        if (machineFuelEvent.isCancelled()) {
                            event.setCancelled(true);
                            ((Player) event.getWhoClicked()).updateInventory();
                        }
                    }
                }
            }
        }

        //Check if the clicked slot is blocked.
        if (optional.get().isBlockedSlot(event.getRawSlot())) {
            event.setCancelled(true);
            ((Player) event.getWhoClicked()).updateInventory();
            return;
        }

        if (optional.get().isOutputSlot(event.getRawSlot())) {
            switch (event.getAction()) {
                case PICKUP_ALL:
                case PICKUP_SOME:
                case PICKUP_HALF:
                case PICKUP_ONE:
                case MOVE_TO_OTHER_INVENTORY:
                    break;

                default:
                    event.setCancelled(true);
                    ((Player) event.getWhoClicked()).updateInventory();
                    break;
            }
            return;
        }

        MachineData data = optional.get().getData(MachineDataType.FUEL);
        if (data != null) {
            if (data.isSlot(event.getRawSlot())) {
                switch (event.getAction()) {
                    case PICKUP_ALL:
                    case PICKUP_SOME:
                    case PICKUP_HALF:
                    case PICKUP_ONE:
                    case COLLECT_TO_CURSOR:
                    case MOVE_TO_OTHER_INVENTORY:
                        break;

                    case PLACE_ALL:
                    case PLACE_SOME:
                    case PLACE_ONE:
                    case SWAP_WITH_CURSOR:
                        if (event.getCursor() == null || !MachineUtil.isFuelType(event.getCursor().getType())) {
                            event.setCancelled(true);
                            ((Player) event.getWhoClicked()).updateInventory();
                            return;
                        }

                        MachineFuelEvent machineFuelEvent = new MachineFuelEvent(optional.get(), data, event.getCursor().getType(), event.getRawSlot());
                        Bukkit.getPluginManager().callEvent(machineFuelEvent);
                        if (machineFuelEvent.isCancelled()) {
                            event.setCancelled(true);
                            ((Player) event.getWhoClicked()).updateInventory();
                        }
                        break;

                    //TODO: Change this to listen on 'default'
                    case DROP_ALL_CURSOR:
                    case DROP_ONE_CURSOR:
                    case DROP_ALL_SLOT:
                    case DROP_ONE_SLOT:
                    case HOTBAR_MOVE_AND_READD:
                    case HOTBAR_SWAP:
                    case CLONE_STACK:
                    case UNKNOWN:
                        event.setCancelled(true);
                        ((Player) event.getWhoClicked()).updateInventory();
                        break;
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    protected final void onMachineFuel(MachineFuelEvent event) {
//        MachineData data = event.getMachine().getData(MachineDataType.FUEL);
//        data.add(20);
//        ServerUtil.debug("FUEL ADDED.");
    }

    @EventHandler
    protected final void onMachineRecipeComplete(MachineRecipeCompleteEvent event) {
        BaseMachine m = event.getMachine();
        MachineData fuelData = m.getData(MachineDataType.FUEL);
        fuelData.take(event.getRecipeData().getRecipe().getFuelUsage());
        m.updateFuel();

        MachineData durabilityData = m.getData(MachineDataType.DURABILITY);
        durabilityData.take(event.getRecipeData().getRecipe().getDurabilityUsage());
        m.updateDurability();



        for (int i = 0; i < event.getRecipeData().getRecipe().getOutput().length; i++) {
            RecipeOutput output = event.getRecipeData().getRecipe().getOutput()[i];

            ItemStack itemStack = output.getItemStack().clone();
            itemStack.setAmount(output.getAmount());

            Optional<Entity> minecart = m.getLocation().getWorld().getNearbyEntities(m.getLocation(), 2, 2, 2).stream().filter(e -> e.getType() == EntityType.MINECART_HOPPER).findFirst();
            if(minecart.isPresent()) {
                HopperMinecart storage = (HopperMinecart)minecart.get();
                if(storage.getInventory().firstEmpty()==-1) {
                    MachineUtil.addOutput(m.getInventory(), m.getOutputSlots()[i], itemStack);
                    continue;
                }
                storage.getInventory().addItem(itemStack);
                if(m.getInventory().getItem(m.getOutputSlots()[i])!=null) {
                    storage.getInventory().addItem(m.getInventory().getItem(m.getOutputSlots()[i]));
                    m.getInventory().setItem(m.getOutputSlots()[i], new ItemStack(Material.AIR));
                }
                continue;
            }

            Block block = m.getLocation().getBlock().getRelative(BlockFace.DOWN);
            if (block == null || block.getType() == Material.AIR) {
                MachineUtil.addOutput(m.getInventory(), m.getOutputSlots()[i], itemStack);
                continue;
            }

            if (block.getType() != Material.HOPPER) {
                MachineUtil.addOutput(m.getInventory(), m.getOutputSlots()[i], itemStack);
                continue;
            }

            Hopper hopper = (Hopper) block.getState();
            if (hopper.getInventory().firstEmpty() == -1) {
                MachineUtil.addOutput(m.getInventory(), m.getOutputSlots()[i], itemStack);
                continue;
            }

            hopper.getInventory().addItem(itemStack);
            if(m.getInventory().getItem(m.getOutputSlots()[i])!=null) {
                hopper.getInventory().addItem(m.getInventory().getItem(m.getOutputSlots()[i]));
                m.getInventory().setItem(m.getOutputSlots()[i], new ItemStack(Material.AIR));
            }
        }

        MachineData progressData = m.getData(MachineDataType.PROGRESS);
        progressData.setCurrent(0);
        m.updateProgress();

//        this.recipeManager.removeRecipeData(event.getRecipeData());
        m.setRecipeData(null);
    }

    @EventHandler(ignoreCancelled = true)
    protected final void onItemMove(InventoryMoveItemEvent event) {
        if (event.getInitiator() == null) return;
        if (event.getInitiator().getType() != InventoryType.HOPPER) return;
        if (event.getInitiator().getLocation() != null) {
            if (!event.getInitiator().getLocation().getChunk().isLoaded()) {
                event.setCancelled(true);
                return;
            }
        }

        if (event.getItem() == null) return;
        if (event.getItem().getType() == Material.AIR) return;

        // Machine -> Hopper
        if (event.getSource() != null && event.getSource().getType() == InventoryType.DROPPER) {

            Optional<BaseMachine> optional = this.machineManager.getMachineByLocation(event.getSource().getLocation());
            if (optional.isPresent()) {
//                MachineItemTransferEvent itemTransferEvent = new MachineItemTransferEvent(optional.get(), event.getSource(), event.getDestination(), MachineItemTransferEvent.TransferType.FROM_MACHINE, event.getItem());
//                Bukkit.getPluginManager().callEvent(itemTransferEvent);
                event.setCancelled(true);

                event.getSource().clear();

//                if (itemTransferEvent.isTransferred()) {
//                    //TODO transfer output to hopper.
//                }
            }
        }

        // Hopper -> Machine
        if (event.getDestination() != null && event.getDestination().getType() == InventoryType.DROPPER) {

            Optional<BaseMachine> optional = this.machineManager.getMachineByLocation(event.getDestination().getLocation());
            if (optional.isPresent()) {
                MachineItemTransferEvent itemTransferEvent = new MachineItemTransferEvent(optional.get(), event.getSource(), event.getDestination(), MachineItemTransferEvent.TransferType.TO_MACHINE, event.getItem());
                Bukkit.getPluginManager().callEvent(itemTransferEvent);

                if (!itemTransferEvent.isTransferred()) {
                    event.setCancelled(true);
                    return;
                }

                MachineUtil.removeSimilarItem(event.getDestination(), event.getItem());
                event.getDestination().clear();
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    protected final void onMachineItemTransfer(MachineItemTransferEvent event) {
        if (event.getTransferType() == MachineItemTransferEvent.TransferType.TO_MACHINE) {
            boolean bool = MachineUtil.tryAddingItem(event.getMachine(), event.getFrom().getLocation(), event.getItemStack());
            event.setTransferred(bool);

            return;
        }
    }

    @EventHandler
    protected final void onChunkLoad(ChunkLoadEvent event) {
        if (!event.getWorld().getName().equals("world")) return;

        for (BlockState state : event.getChunk().getTileEntities()) {
            if (state == null || state.getBlock() == null) continue;
            if (!this.machineManager.isType(state.getType())) continue;

            Optional<BaseMachine> optional = this.machineManager.getMachineByLocation(state.getBlock().getLocation());
            if (!optional.isPresent()) continue;

            if (!optional.get().isEnabled())
                optional.get().setEnabled(true);

        }
    }

    @EventHandler
    protected final void onChunkUnload(ChunkUnloadEvent event) {
        if (!event.getWorld().getName().equals("world")) return;

        for (BlockState state : event.getChunk().getTileEntities()) {
            if (state == null || state.getBlock() == null) continue;
            if (!this.machineManager.isType(state.getType())) continue;

            Optional<BaseMachine> optional = this.machineManager.getMachineByLocation(state.getBlock().getLocation());
            if (!optional.isPresent()) continue;

            if (optional.get().isEnabled())
                optional.get().setEnabled(false);

        }
    }

//    private class MachineChunkPacket implements PacketListener {
//
//        @Override
//        public void onPacketSending(PacketEvent packetEvent) {
//            System.out.println("onPacketSending  " + packetEvent.getPacketType().name());
//        }
//
//        @Override
//        public void onPacketReceiving(PacketEvent packetEvent) {
//            System.out.println("onPacketReceiving  " + packetEvent.getPacketType().name());
//        }
//
//        @Override
//        public ListeningWhitelist getSendingWhitelist() {
////            return ListeningWhitelist.newBuilder().types(PacketType.Play.Server.MAP_CHUNK).build();
//            return ListeningWhitelist.EMPTY_WHITELIST;
//        }
//
//        @Override
//        public ListeningWhitelist getReceivingWhitelist() {
//            return ListeningWhitelist.newBuilder().types(PacketType.Play.Server.MAP_CHUNK).build();
//        }
//
//        @Override
//        public Plugin getPlugin() {
//            return Vice.getInstance();
//        }
//    }
}
