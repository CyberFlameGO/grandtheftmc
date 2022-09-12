package net.grandtheftmc.houses.listeners;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import net.grandtheftmc.core.database.BaseDatabase;
import net.grandtheftmc.core.util.ServerUtil;
import net.grandtheftmc.houses.dao.HouseDAO;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.Lang;
import net.grandtheftmc.core.menus.MenuManager;
import net.grandtheftmc.core.users.User;
import net.grandtheftmc.core.util.Utils;
import net.grandtheftmc.gtm.trashcan.TrashCanManager;
import net.grandtheftmc.houses.HouseUtils;
import net.grandtheftmc.houses.Houses;
import net.grandtheftmc.houses.houses.Blocks;
import net.grandtheftmc.houses.houses.EditableBlock;
import net.grandtheftmc.houses.houses.House;
import net.grandtheftmc.houses.houses.HouseChest;
import net.grandtheftmc.houses.houses.HouseDoor;
import net.grandtheftmc.houses.houses.HousesManager;
import net.grandtheftmc.houses.houses.PremiumHouse;
import net.grandtheftmc.houses.houses.PremiumHouseChest;
import net.grandtheftmc.houses.houses.PremiumHouseDoor;
import net.grandtheftmc.houses.houses.PremiumHouseTrashcan;
import net.grandtheftmc.houses.users.HouseUser;

public class Interact implements Listener {

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    protected final void onInteract(PlayerInteractEvent e) {
		
		if (e.getHand() != EquipmentSlot.HAND) {
			return;
		}
		
        Player player = e.getPlayer();
        UUID uuid = player.getUniqueId();
        User u = Core.getUserManager().getLoadedUser(uuid);
        if (!u.hasEditMode()) return;
        BlockState state = e.getClickedBlock() == null ? null : e.getClickedBlock().getState();
        HousesManager hm = Houses.getManager();
        HouseUser user = Houses.getUserManager().getLoadedUser(uuid);

        if (u.isAdmin() && user.isRemovingDoor() && state != null && state.getType() == Material.IRON_DOOR_BLOCK) {
            e.setCancelled(true);
            Block underneath = e.getClickedBlock().getRelative(BlockFace.DOWN);
            if (underneath.getType() == Material.IRON_DOOR_BLOCK)
                state = underneath.getState();
            Location loc = state.getLocation();
            Object[] houseAndDoor = hm.getHouseAndDoor(loc);
            if (houseAndDoor == null) {
                player.sendMessage(Utils.f(Lang.HOUSES + "&7This door doesn't belong to any house!"));
                return;
            }
            if (houseAndDoor[0] instanceof PremiumHouse) {
                PremiumHouse premiumHouse = (PremiumHouse) houseAndDoor[0];
                if (!premiumHouse.equals(user.getEditingPremiumHouse())) {
                    player.sendMessage(Lang.HOUSES
                            .f("&7This door does not belong to the house you are editing! It belongs to premium house &a"
                                    + premiumHouse.getId() + "&7!"));
                    return;
                }
                PremiumHouseDoor door = (PremiumHouseDoor) houseAndDoor[1];
                premiumHouse.removeDoor(door);
                player.sendMessage(Utils.f(Lang.HOUSES + "&7You removed a door with id &a" + door.getId()
                        + "&7 from premium house &a" + premiumHouse.getId() + "&7."));
                return;
            }
            House house = (House) houseAndDoor[0];
            if (!house.equals(user.getEditingHouse())) {
                player.sendMessage(Utils.f(
                        Lang.HOUSES + "&7This door does not belong to the house you are editing! It belongs to house &a"
                                + house.getId() + "&7!"));
                return;
            }
            HouseDoor door = (HouseDoor) houseAndDoor[1];
            house.removeDoor(door);
            player.sendMessage(Utils.f(Lang.HOUSES + "&7You removed a door with id &a" + door.getId()
                    + "&7 from house &a" + house.getId() + "&7."));
            return;
        }

        if (u.isAdmin() && (user.isAddingDoor() || user.isAddingPremiumDoor())) {
            if (((user.isAddingDoor() && user.getAddingDoor().getLocation() == null) || (user.isAddingPremiumDoor() && user.getAddingPremiumDoor().getLocation() == null)) && state != null && state.getType() == Material.IRON_DOOR_BLOCK) {
                e.setCancelled(true);
                Block underneath = e.getClickedBlock().getRelative(BlockFace.DOWN);
                if (underneath.getType() == Material.IRON_DOOR_BLOCK)
                    state = underneath.getState();
                Location loc = state.getLocation();
                Object[] houseAndDoor = hm.getHouseAndDoor(loc);
                if (houseAndDoor != null) {
                    if (houseAndDoor[0] instanceof PremiumHouse) {
                        PremiumHouse house = (PremiumHouse) houseAndDoor[0];
                        player.sendMessage(Utils.f(Lang.HOUSES + "&7This door is added to premium house &a" + house.getId() + "&7 already!"));
                        return;
                    }
                    House house = (House) houseAndDoor[0];
                    player.sendMessage(Utils.f(Lang.HOUSES + "&7This door is added to house &a" + house.getId() + "&7 already!"));
                    return;
                }

                if (user.isAddingPremiumDoor()) {
                    PremiumHouseDoor door = user.getAddingPremiumDoor();
                    door.setLocation(state.getLocation());
                    player.sendMessage(Utils
                            .f(Lang.HOUSES + "&7You set the door location for premium door &a" + door.getId() + "&7."));
                    if (door.getLocation() != null && door.getInsideLocation() != null
                            && door.getOutsideLocation() != null) {
                        user.setAddingPremiumDoor(null);
                        player.sendMessage(Utils.f(Lang.HOUSES + "&7You have completed the setup for door &a" + door.getId() + "&7!"));
                        ServerUtil.runTaskAsync(() -> {
                            try (Connection connection = BaseDatabase.getInstance().getConnection()) {
                                HouseDAO.updateDoor(connection, door.getHotspotId(), door.getId(), door.getLocation(), door.getInsideLocation(), door.getOutsideLocation());
                            } catch (SQLException ex) {
                                ex.printStackTrace();
                            }
                        });
                    }
                    return;
                }

                HouseDoor door = user.getAddingDoor();
                door.setLocation(state.getLocation());
                player.sendMessage(Utils.f(Lang.HOUSES + "&7You set the door location for door &a" + door.getId() + "&7."));
                if (door.getLocation() != null && door.getInsideLocation() != null && door.getOutsideLocation() != null) {
                    user.setAddingDoor(null);
                    player.sendMessage(Utils.f(Lang.HOUSES + "&7You have completed the setup for door &a" + door.getId() + "&7!"));
                    ServerUtil.runTaskAsync(() -> {
                        try (Connection connection = BaseDatabase.getInstance().getConnection()) {
                            HouseDAO.updateDoor(connection, door.getHotspotId(), door.getId(), door.getLocation(), door.getInsideLocation(), door.getOutsideLocation());
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                        }
                    });
                }

                return;
            }
            switch (e.getAction()) {
                case LEFT_CLICK_AIR:
                case LEFT_CLICK_BLOCK: {
                    e.setCancelled(true);
                    if (user.isAddingPremiumDoor()) {
                        PremiumHouseDoor door = user.getAddingPremiumDoor();
                        door.setInsideLocation(player.getLocation());
                        player.sendMessage(Utils.f(Lang.HOUSES + "&7You set the inside location for door &a" + door.getId() + "&7."));
                        if (door.getLocation() != null && door.getInsideLocation() != null && door.getOutsideLocation() != null) {
                            user.setAddingPremiumDoor(null);
                            player.sendMessage(Utils.f(Lang.HOUSES + "&7You have completed the setup for door &a" + door.getId() + "&7!"));
                            ServerUtil.runTaskAsync(() -> {
                                try (Connection connection = BaseDatabase.getInstance().getConnection()) {
                                    HouseDAO.updateDoor(connection, door.getHotspotId(), door.getId(), door.getLocation(), door.getInsideLocation(), door.getOutsideLocation());
                                } catch (SQLException ex) {
                                    ex.printStackTrace();
                                }
                            });
                        }
                        return;
                    }
                    HouseDoor door = user.getAddingDoor();
                    door.setInsideLocation(player.getLocation());
                    player.sendMessage(Utils.f(Lang.HOUSES + "&7You set the inside location for door &a" + door.getId() + "&7."));
                    if (door.getLocation() != null && door.getInsideLocation() != null && door.getOutsideLocation() != null) {
                        user.setAddingDoor(null);
                        player.sendMessage(Utils.f(Lang.HOUSES + "&7You have completed the setup for door &a" + door.getId() + "&7!"));
                        ServerUtil.runTaskAsync(() -> {
                            try (Connection connection = BaseDatabase.getInstance().getConnection()) {
                                HouseDAO.updateDoor(connection, door.getHotspotId(), door.getId(), door.getLocation(), door.getInsideLocation(), door.getOutsideLocation());
                            } catch (SQLException ex) {
                                ex.printStackTrace();
                            }
                        });
                    }
                    return;
                }
                case RIGHT_CLICK_AIR:
                case RIGHT_CLICK_BLOCK:
                    e.setCancelled(true);
                    if (user.isAddingPremiumDoor()) {
                        PremiumHouseDoor door = user.getAddingPremiumDoor();
                        door.setOutsideLocation(player.getLocation());
                        player.sendMessage(
                                Utils.f(Lang.HOUSES + "&7You set the outside location for door &a" + door.getId() + "&7."));
                        if (door.getLocation() != null && door.getInsideLocation() != null
                                && door.getOutsideLocation() != null) {
                            user.setAddingPremiumDoor(null);
                            player.sendMessage(Utils
                                    .f(Lang.HOUSES + "&7You have completed the setup for door &a" + door.getId() + "&7!"));
                            ServerUtil.runTaskAsync(() -> {
                                try (Connection connection = BaseDatabase.getInstance().getConnection()) {
                                    HouseDAO.updateDoor(connection, door.getHotspotId(), door.getId(), door.getLocation(), door.getInsideLocation(), door.getOutsideLocation());
                                } catch (SQLException ex) {
                                    ex.printStackTrace();
                                }
                            });
                        }
                        return;
                    }
                    HouseDoor door = user.getAddingDoor();
                    door.setOutsideLocation(player.getLocation());
                    player.sendMessage(
                            Utils.f(Lang.HOUSES + "&7You set the outside location for door &a" + door.getId() + "&7."));
                    if (door.getLocation() != null && door.getInsideLocation() != null
                            && door.getOutsideLocation() != null) {
                        user.setAddingDoor(null);
                        player.sendMessage(
                                Utils.f(Lang.HOUSES + "&7You have completed the setup for door &a" + door.getId() + "&7!"));
                        ServerUtil.runTaskAsync(() -> {
                            try (Connection connection = BaseDatabase.getInstance().getConnection()) {
                                HouseDAO.updateDoor(connection, door.getHotspotId(), door.getId(), door.getLocation(), door.getInsideLocation(), door.getOutsideLocation());
                            } catch (SQLException ex) {
                                ex.printStackTrace();
                            }
                        });
                    }
                    return;
                default:
                    return;
            }
        }

        switch (e.getAction()) {
            case RIGHT_CLICK_BLOCK:
                if (user.isAddingBlocks()) {
                    PremiumHouse premiumHouse = user.getEditingPremiumHouse();
                    if (!Blocks.getMaterials().contains(e.getClickedBlock().getType())) {
                        player.sendMessage(Lang.HOUSES.f("&7This block type cannot be added."));
                        return;
                    }
                    boolean a = false;
                    for (EditableBlock editableBlock : premiumHouse.getEditableBlocks()) {
                        if (editableBlock.getLocation().getBlockX() == e.getClickedBlock().getLocation().getBlockX()
                                && editableBlock.getLocation().getBlockY() == e.getClickedBlock().getLocation().getBlockY()
                                && editableBlock.getLocation().getBlockZ() == e.getClickedBlock().getLocation().getBlockZ()) {
                            a = true;
                        }
                    }
                    if (a) {
                        player.sendMessage(Lang.HOUSES.f("&7Block has already been added!"));
                    } else {
//                        EditableBlock editableBlock = new EditableBlock(e.getClickedBlock().getLocation(), e.getClickedBlock().getType(), e.getClickedBlock().getData());
//                        premiumHouse.getEditableBlocks().add(editableBlock);
//                        player.sendMessage(Lang.HOUSES.f("&7Block has been added!"));

                        premiumHouse.addEditableBlock(e.getClickedBlock().getLocation(), e.getClickedBlock().getType(), e.getClickedBlock().getData(), result -> {
                            if (result == null) return;

                            ServerUtil.runTask(() -> player.sendMessage(Lang.HOUSES.f("&7Block has been added!")));
                        });
                    }
                    return;
                } else if (user.isRemovingBlocks()) {
                    PremiumHouse premiumHouse = user.getEditingPremiumHouse();
                    EditableBlock editableBlock = null;
                    for (EditableBlock block : premiumHouse.getEditableBlocks()) {
                        if (block.getLocation().getBlockX() == e.getClickedBlock().getLocation().getBlockX()
                                && block.getLocation().getBlockY() == e.getClickedBlock().getLocation().getBlockY()
                                && block.getLocation().getBlockZ() == e.getClickedBlock().getLocation().getBlockZ()) {
                            editableBlock = block;
                            break;
                        }
                    }
                    if (editableBlock != null) {
                        premiumHouse.getEditableBlocks().remove(editableBlock);
                        player.sendMessage(Lang.HOUSES.f("&7Block has been removed."));
                    }
                    return;
                }
                switch (state.getType()) {
                    case TRAPPED_CHEST:
                    case CHEST: {
                        Chest chestBlock = (Chest) state;
                        Location loc = chestBlock.getLocation();
                        Object[] houseAndChest = hm.getHouseAndChest(loc);
                        if (user.isAddingChests()) {
                            e.setCancelled(true);
                            if (houseAndChest != null) {
                                if (houseAndChest[0] instanceof PremiumHouse) {
                                    PremiumHouse premiumHouse = (PremiumHouse) houseAndChest[0];
                                    player.sendMessage(Utils.f(Lang.HOUSES + "&7This chest is added to premium house &a"
                                            + premiumHouse.getId() + "&7 already!"));
                                    return;
                                }
                                House house = (House) houseAndChest[0];
                                player.sendMessage(Utils
                                        .f(Lang.HOUSES + "&7This chest is added to house &a" + house.getId() + "&7 already!"));
                                return;
                            }
                            Block secondBlock = Utils.getSecondHalfChest(e.getClickedBlock());
                            boolean isDub = secondBlock != null;
                            House house = user.getEditingHouse();
                            if (house == null) {
                                PremiumHouse premiumHouse = user.getEditingPremiumHouse();
                                if (premiumHouse == null) {
                                    user.setAddingChests(false);
                                    return;
                                }

//                                PremiumHouseChest chest = premiumHouse.addChest(new PremiumHouseChest(premiumHouse.getUnusedChestId(), premiumHouse.getId(),
//                                                chestBlock.getLocation(), isDub ? secondBlock.getState().getLocation() : null));
//                                player.sendMessage(Utils.f(Lang.HOUSES + "&7You have added a" + (isDub ? " double" : "") + " chest with id &a" + chest.getId() + "&7 to premium house &a" + premiumHouse.getId() + "&7."));

                                premiumHouse.addChest(premiumHouse.getUnusedChestId(), premiumHouse.getId(), chestBlock.getLocation(), isDub ? secondBlock.getState().getLocation() : null, result -> {
                                    if (result == null) return;

                                    ServerUtil.runTask(() -> player.sendMessage(Utils.f(Lang.HOUSES + "&7You have added a" + (isDub ? " double" : "") + " chest with id &a" + result.getId() + "&7 to premium house &a" + premiumHouse.getId() + "&7.")));
                                });
                                return;
                            }

//                            HouseChest chest = house.addChest(new HouseChest(house.getUnusedChestId(), house.getId(),
//                                    chestBlock.getLocation(), isDub ? secondBlock.getState().getLocation() : null));

                            house.addChest(house.getUnusedChestId(), house.getId(), chestBlock.getLocation(), isDub ? secondBlock.getState().getLocation() : null, result -> {
                                if (result != null) {
                                    ServerUtil.runTask(() -> player.sendMessage(Utils.f(Lang.HOUSES + "&7You have added a" + (isDub ? " double" : "") + " chest with id &a" + result.getId() + "&7 to house &a" + house.getId() + "&7.")));
                                }
                            });

//                            player.sendMessage(Utils.f(Lang.HOUSES + "&7You have added a" + (isDub ? " double" : "")
//                                    + " chest with id &a" + chest.getId() + "&7 to house &a" + house.getId() + "&7."));
                            return;
                        } else if (user.isRemovingChests()) {
                            e.setCancelled(true);
                            if (houseAndChest == null) {
                                player.sendMessage(Utils.f(Lang.HOUSES + "&7This chest does not belong to any house!"));
                                return;
                            }
                            if (houseAndChest[0] instanceof PremiumHouse) {
                                PremiumHouse premiumHouse = (PremiumHouse) houseAndChest[0];
                                if (!premiumHouse.equals(user.getEditingPremiumHouse())) {
                                    player.sendMessage(Utils.f(Lang.HOUSES
                                            + "&7This chest does not belong to the house you are editing! It belongs to premium house &a"
                                            + premiumHouse.getId() + "&7!"));
                                    return;
                                }
                                PremiumHouseChest chest = (PremiumHouseChest) houseAndChest[1];
                                premiumHouse.removeChest(chest);
                                player.sendMessage(Utils.f(Lang.HOUSES + "&7You removed a chest with id &a" + chest.getId()
                                        + "&7 from premium house &a" + premiumHouse.getId() + "&7."));
                                return;
                            }
                            House house = (House) houseAndChest[0];
                            if (!house.equals(user.getEditingHouse())) {
                                player.sendMessage(Utils.f(Lang.HOUSES
                                        + "&7This chest does not belong to the house you are editing! It belongs to house &a"
                                        + house.getId() + "&7!"));
                                return;
                            }
                            HouseChest chest = (HouseChest) houseAndChest[1];
                            house.removeChest(chest);
                            player.sendMessage(Utils.f(Lang.HOUSES + "&7You removed a chest with id &a" + chest.getId()
                                    + "&7 from house &a" + house.getId() + "&7."));
                            return;
                        }
                        return;
                    }
                    case DROPPER: {
                        Location loc = state.getLocation();
                        if (user.getEditingPremiumHouse() == null) {
                            player.sendMessage(Lang.HOUSES.f("&7You must be editing a premium house!"));
                            return;
                        }
                        PremiumHouse premiumHouse = user.getEditingPremiumHouse();
                        if (user.isAddingTrashcans()) {
                            for (PremiumHouseTrashcan trashcan : premiumHouse.getTrashcans()) {
                                if (trashcan.getLocation().equals(loc)) {
                                    player.sendMessage(Lang.HOUSES.f("&cThis Trashcan has already been added!"));
                                    return;
                                }
                            }
                            int trashcanId = premiumHouse.getTrashcans().size() + 1;
//                            PremiumHouseTrashcan trashcan = new PremiumHouseTrashcan(trashcanId, premiumHouse.getId(), loc, false);
//                            premiumHouse.getTrashcans().add(trashcan);
//                            player.sendMessage(Lang.HOUSES.f("&7Trashcan &a" + trashcanId + " &7has been added to premium house &a" + premiumHouse.getId() + "&7!"));

                            premiumHouse.addTrashcan(trashcanId, premiumHouse.getId(), loc, false, result -> {
                                if (result == null) return;

                                ServerUtil.runTask(() -> player.sendMessage(Lang.HOUSES.f("&7Trashcan &a" + trashcanId + " &7has been added to premium house &a" + premiumHouse.getId() + "&7!")));
                            });

                            e.setCancelled(true);
                        } else if (user.isRemovingTrashcans()) {
                            boolean found = false;
                            for (PremiumHouseTrashcan trashcan : premiumHouse.getTrashcans()) {
                                if (trashcan.getLocation().equals(loc)) {
                                    player.sendMessage(Lang.HOUSES.f("&7Trashcan &a" + trashcan.getId() +
                                            " &7has been removed from premium house &a" + premiumHouse.getId() + "&7!"));
                                    premiumHouse.getTrashcans().remove(trashcan);
                                    found = true;
                                    return;
                                }
                            }
                            if (!found) {
                                player.sendMessage(Lang.HOUSES.f("&cTrashcan is not registered."));
                            }
                            e.setCancelled(true);
                        }
                        return;
                    }
                    case SIGN_POST:
                    case WALL_SIGN:
                        Sign signBlock = (Sign) state;
                        Location loc = signBlock.getLocation();
                        if (user.isAddingSigns()) {
                            e.setCancelled(true);
                            House house = hm.getHouseFromSign(loc);
                            if (house != null) {
                                player.sendMessage(Utils
                                        .f(Lang.HOUSES + "&7This sign is added to house &a" + house.getId() + "&7 already!"));
                                return;
                            }
                            PremiumHouse premiumHouse = hm.getPremiumHouseFromSign(loc);
                            if (premiumHouse != null) {
                                player.sendMessage(Utils.f(Lang.HOUSES + "&7This sign is added to premium house &a"
                                        + premiumHouse.getId() + "&7 already!"));
                                return;
                            }
                            House h = user.getEditingHouse();
                            if (h == null) {
                                PremiumHouse ph = user.getEditingPremiumHouse();
                                if (ph == null) {
                                    user.setAddingSigns(false);
                                    return;
                                }
//                                ph.addSign(loc);
//                                player.sendMessage(Utils.f(Lang.HOUSES + "&7You have added a sign to premium house &a"
//                                        + ph.getId() + "&7 at location &a" + Utils.blockLocationToString(loc) + "&7."));
                                ph.addSign(loc, result -> {
                                    if (result == null) return;
                                    ServerUtil.runTask(() -> player.sendMessage(Utils.f(Lang.HOUSES + "&7You have added a sign to premium house &a" + ph.getId() + "&7 at location &a" + Utils.blockLocationToString(loc) + "&7.")));
                                });
                                return;
                            }
//                            h.addSign(loc);
//                            player.sendMessage(Utils.f(Lang.HOUSES + "&7You have added a sign to house &a" + h.getId()
//                                    + "&7 at location &a" + Utils.blockLocationToString(loc) + "&7."));

                            h.addSign(loc, result -> {
                                if (result == null) return;
                                ServerUtil.runTask(() -> player.sendMessage(Utils.f(Lang.HOUSES + "&7You have added a sign to house &a" + h.getId() + "&7 at location &a" + Utils.blockLocationToString(loc) + "&7.")));
                            });
                            return;
                        } else if (user.isRemovingSigns()) {
                            e.setCancelled(true);
                            House house = hm.getHouseFromSign(loc);
                            PremiumHouse premiumHouse = hm.getPremiumHouseFromSign(loc);

                            if (house == null) {
                                if (premiumHouse == null) {
                                    player.sendMessage(Utils.f(Lang.HOUSES + "&7This sign does not belong to any house!"));
                                    return;
                                }
                                if (!premiumHouse.equals(user.getEditingPremiumHouse())) {
                                    player.sendMessage(Utils.f(Lang.HOUSES
                                            + "&7This sign does not belong to the house you are editing! It belongs to premium house &a"
                                            + premiumHouse.getId() + "&7!"));
                                    return;
                                }
                                premiumHouse.removeSign(loc);
                                player.sendMessage(Utils
                                        .f(Lang.HOUSES + "&7You removed a sign from premium house &a" + premiumHouse.getId()
                                                + "&7 at location &a" + Utils.blockLocationToString(loc) + "&7."));
                                return;
                            }
                            if (!house.equals(user.getEditingHouse())) {
                                player.sendMessage(Utils.f(Lang.HOUSES
                                        + "&7This sign does not belong to the house you are editing! It belongs to house &a"
                                        + house.getId() + "&7!"));
                                return;
                            }
                            house.removeSign(loc);
                            player.sendMessage(Utils.f(Lang.HOUSES + "&7You removed a sign from house &a" + house.getId()
                                    + "&7 at location &a" + Utils.blockLocationToString(loc) + "&7."));
                            return;
                        }
                        return;

                    default:
                        break;
                }
            default:
                break;
        }
    }

    @EventHandler
    public void onOpenChest(PlayerInteractEvent e) {
        if (e.isCancelled()) return;
        if (e.getHand() != EquipmentSlot.HAND) return;

        Player player = e.getPlayer();
        UUID uuid = player.getUniqueId();
        BlockState state = e.getClickedBlock().getState();
        HousesManager hm = Houses.getManager();
        HouseUser user = Houses.getUserManager().getLoadedUser(uuid);
        switch (e.getAction()) {
            case RIGHT_CLICK_BLOCK:
                switch (state.getType()) {
                    case TRAPPED_CHEST:
                    case CHEST: {
                        Chest chestBlock = (Chest) state;
                        Location loc = chestBlock.getLocation();
                        Object[] houseAndChest = hm.getHouseAndChest(loc);
                        if (houseAndChest == null) {
//                            e.setCancelled(true);
//                            player.sendMessage(Utils.f(Lang.HOUSES + "&7This house is having issues loading (&ferr2937&7)!"));
                            return;
                        }

                        if (houseAndChest[0] instanceof PremiumHouse) {
                            PremiumHouse house = (PremiumHouse) houseAndChest[0];
                            if (!house.hasAccess(player, user)) {
                                e.setCancelled(true);
                                player.sendMessage(Utils.f(Lang.HOUSES + "&7You don't own this premium house!"));
                                return;
                            }
                            user.setOpenChest(houseAndChest[1]);
                            return;
                        }
                        House house = (House) houseAndChest[0];
                        e.setCancelled(true);

                        if(!user.isInsidePremiumHouse() && !user.isInsideHouse()) {
                            player.sendMessage(Lang.HOUSES.f("&cError opening chest, Leave your house and re-enter to try again!"));
                            return;
                        }

                        HouseChest chest = (HouseChest) houseAndChest[1];

                        user.setLastChestId(chest == null ? -1 : chest.getId());
                        user.setOpenChest(chest);
                        house.openChest(player, loc, user, chest);
                        return;
                    }
                    case IRON_DOOR_BLOCK: {
                        Block underneath = e.getClickedBlock().getRelative(BlockFace.DOWN);
                        if (underneath.getType() == Material.IRON_DOOR_BLOCK)
                            state = underneath.getState();
                        Location loc = state.getLocation();
                        Object[] houseAndDoor = hm.getHouseAndDoor(loc);
                        if (houseAndDoor == null)
                            return;
                        if (houseAndDoor[0] instanceof PremiumHouse) {
                            PremiumHouse house = (PremiumHouse) houseAndDoor[0];
                            if (user.isTeleporting())
                                return;
                            if (!house.hasAccess(player, user) || player.isSneaking()) {
                                HouseUtils.openPremiumHouseMenu(player, house, user);
                                return;
                            }
                            PremiumHouseDoor door = (PremiumHouseDoor) houseAndDoor[1];
                            user.teleportInOrOutPremiumHouse(player, door);
                            return;
                        }
                        House house = (House) houseAndDoor[0];
                        if (!user.ownsHouse(house.getId())) {
                            HouseUtils.openHouseMenu(player, house, user);
                            return;
                        }
                        if (user.isTeleporting())
                            return;
                        if (player.isSneaking()) {
                            HouseUtils.openHouseMenu(player, house, user);
                            return;
                        }
                        HouseDoor door = (HouseDoor) houseAndDoor[1];
                        user.teleportInOrOutHouse(player, door);
                        return;
                    }
                    case SIGN:
                    case SIGN_POST:
                    case WALL_SIGN: {
                        Sign signBlock = (Sign) state;
                        Location loc = signBlock.getLocation();
                        House house = hm.getHouseFromSign(loc);
                        if (house != null) {
                            HouseUtils.openHouseMenu(player, house, user);
                        }
                        PremiumHouse premiumHouse = hm.getPremiumHouseFromSign(loc);
                        if (premiumHouse != null) {
                            HouseUtils.openPremiumHouseMenu(player, premiumHouse, user);
                        }
                        return;
                    }
                    case DROPPER: {
                        Location location = state.getLocation();
                        if (!user.isInsidePremiumHouse()) return;
                        PremiumHouse premiumHouse = Houses.getHousesManager().getPremiumHouse(user.getInsidePremiumHouse());
                        if (Objects.equals(premiumHouse.getOwner(), player.getUniqueId())
                                || premiumHouse.isGuest(player.getUniqueId())) {
                            Optional<PremiumHouseTrashcan> trashcanOptional = premiumHouse.getTrashcan(location);
                            PremiumHouseTrashcan trashcan = null;
                            if (!trashcanOptional.isPresent()) {
                                int trashcanId = premiumHouse.getTrashcans().size() + 1;
//                                trashcan = new PremiumHouseTrashcan(trashcanId, premiumHouse.getId(), location, false);
//                                premiumHouse.getTrashcans().add(trashcan);

                                premiumHouse.addTrashcan(trashcanId, premiumHouse.getId(), location, false, result -> {
                                    if (result == null) return;
                                    ServerUtil.runTask(() -> result.setOwned(true));
                                });
                            } else {
                                trashcan = trashcanOptional.get();
                            }

                            if (trashcan == null) return;

                            user.setOpenTrashcan(trashcan);
                            e.setCancelled(true);
                            player.closeInventory();
                            if (trashcan.isOwned()) {
                                TrashCanManager.openTrashCan(player);
                            } else {
                                MenuManager.openMenu(player, "buytrashcan");
                            }
                        } else {
                            player.sendMessage(Lang.HOUSES.f("&7You do not have access to this trashcan!"));
                        }
                        return;
                    }
                    default:
                        break;
                }
            default:
                break;
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent e) {
        Player player = (Player) e.getPlayer();
        HouseUser houseUser = Houses.getUserManager().getLoadedUser(player.getUniqueId());
        Inventory inv = e.getInventory();
        Collection<String> invNames = Arrays.asList("Trash Can", "Confirm Trashcan Purchase");
        if (e.getInventory().getType() != InventoryType.CHEST
                || !invNames.contains(ChatColor.stripColor(inv.getTitle())))
            return;
        houseUser.setOpenTrashcan(null);
    }


    @EventHandler
    public void onInteractEntity(PlayerInteractEntityEvent e) {
        Player player = e.getPlayer();
        HouseUser user = Houses.getUserManager().getLoadedUser(player.getUniqueId());
        if (user.isInsideHouse() || user.isInsidePremiumHouse())
            e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockClick(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (event.getClickedBlock() == null) return;
        if (!Blocks.getMaterials().contains(event.getClickedBlock().getType())) return;
        UUID uuid = player.getUniqueId();
        HouseUser houseUser = Houses.getUserManager().getLoadedUser(uuid);
        if (!houseUser.isChangingBlocks()) return;
        if (!houseUser.isInsidePremiumHouse()) return;
        PremiumHouse premiumHouse = houseUser.getPremiumHouse(houseUser.getInsidePremiumHouse());
        if (!premiumHouse.getOwnerName().equalsIgnoreCase(player.getName())) return;
        boolean a = false;
        for (EditableBlock editableBlock : premiumHouse.getEditableBlocks()) {
            if (editableBlock.getLocation().getBlockX() == event.getClickedBlock().getLocation().getBlockX()
                    && editableBlock.getLocation().getBlockY() == event.getClickedBlock().getLocation().getBlockY()
                    && editableBlock.getLocation().getBlockZ() == event.getClickedBlock().getLocation().getBlockZ()) {
                a = true;
            }
        }
        if (!a) {
            player.sendMessage(Lang.HOUSES.f("&7This block cannot be changed!"));
        } else {
            houseUser.setEditingBlock(event.getClickedBlock());
            if (player.isSneaking() && houseUser.getLastUsedMaterial() != null) {
                Blocks lastUsed = houseUser.getLastUsedMaterial();
                houseUser.getEditingBlock().setType(lastUsed.getType());
                houseUser.getEditingBlock().setData(lastUsed.getData());
                houseUser.setEditingBlock(null);
                player.sendMessage(Lang.HOUSES.f("&7Block has been set to &a" + lastUsed.getType().name() + "&7!"));
                return;
            }
            HouseUtils.openChangeBlocksMenu(player, premiumHouse, houseUser);
        }
    }
}
