package net.grandtheftmc.houses.houses;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.Lang;
import net.grandtheftmc.core.database.BaseDatabase;
import net.grandtheftmc.core.util.Callback;
import net.grandtheftmc.core.util.ServerUtil;
import net.grandtheftmc.houses.HouseUtils;
import net.grandtheftmc.houses.Houses;
import net.grandtheftmc.houses.dao.HouseDAO;
import net.grandtheftmc.houses.dao.PremiumHouseDAO;

public class HousesManager {

    private List<House> houses = new ArrayList<>();
    private List<PremiumHouse> premiumHouses = new ArrayList<>();

    public HousesManager() {
        this.load();
        this.saveTask();
    }

    public void saveTask() {
//        Bukkit.getScheduler().scheduleSyncRepeatingTask(Houses.getInstance(), new Runnable() {
//            @Override
//            public void run() {
//                HousesManager.this.save();
//            }
//        }, 600L, 36000L); // 30 minutes
    }

    public House getHouse(int id) {
        for (House house : this.houses)
            if (house.getId() == id)
                return house;
        return null;
    }

    public List<House> getHouses() {
        return this.houses;
    }

    public void addHouse(int id, int price, Callback<House> callback) {
//        House house = new House(id, price);
//        this.houses.add(house);
//        return house;

        ServerUtil.runTaskAsync(() -> {
            House house = null;
            try (Connection connection = BaseDatabase.getInstance().getConnection()) {
                house = HouseDAO.addHouse(connection, id, price);
            } catch (SQLException e) {
                e.printStackTrace();
                callback.call(null);
                return;
            }

            if (house == null) {
                callback.call(null);
                return;
            }

            this.houses.add(house);
            callback.call(house);
        });
    }

    public PremiumHouse getPremiumHouse(int id) {
        for (PremiumHouse house : this.premiumHouses)
            if (house.getId() == id)
                return house;
        return null;
    }

    public List<PremiumHouse> getPremiumHouses() {
        return this.premiumHouses;
    }

    public void addPremiumHouse(int id, int permits, Callback<PremiumHouse> callback) {
//        PremiumHouse house = new PremiumHouse(id, permits);
//        this.premiumHouses.add(house);
//        return house;
        ServerUtil.runTaskAsync(() -> {
            PremiumHouse house = null;
            try (Connection connection = BaseDatabase.getInstance().getConnection()) {
                house = PremiumHouseDAO.addHouse(connection, id, permits);
            } catch (SQLException e) {
                e.printStackTrace();
                callback.call(null);
                return;
            }

            if (house == null) {
                callback.call(null);
                return;
            }

            this.premiumHouses.add(house);
            callback.call(house);
        });
    }

    public House getHouseFromChest(Location loc) {
        for (House house : this.houses)
            for (HouseChest chest : house.getChests())
                if (HouseUtils.locEqualsLoc(loc, chest.getLoc1(), false) || HouseUtils.locEqualsLoc(loc, chest.getLoc2(), false))
                    return house;
        return null;
    }

    public Object[] getHouseAndChest(Location loc) {
        for (House house : this.houses)
            for (HouseChest chest : house.getChests())
//                if (loc.equals(chest.getLoc1()) || loc.equals(chest.getLoc2()))
                if (HouseUtils.locEqualsLoc(loc, chest.getLoc1(), false) || HouseUtils.locEqualsLoc(loc, chest.getLoc2(), false))
                    return new Object[]{house, chest};

        for (PremiumHouse house : this.premiumHouses)
            for (PremiumHouseChest chest : house.getChests())
//                if (loc.equals(chest.getLoc1()) || loc.equals(chest.getLoc2()))
                if (HouseUtils.locEqualsLoc(loc, chest.getLoc1(), false) || HouseUtils.locEqualsLoc(loc, chest.getLoc2(), false))
                    return new Object[]{house, chest};
        return null;
    }

    public House getHouseFromDoor(Location loc) {
        for (House house : this.houses)
            for (HouseDoor door : house.getDoors())
//                if (loc.equals(door.getLocation()))
                if (HouseUtils.locEqualsLoc(loc, door.getLocation(), false))
                    return house;
        return null;
    }

    public Object[] getHouseAndDoor(Location loc) {
        for (House house : this.houses)
            for (HouseDoor door : house.getDoors())
//                if (loc.equals(door.getLocation()))
                if (HouseUtils.locEqualsLoc(loc, door.getLocation(), false))
                    return new Object[]{house, door};

        for (PremiumHouse house : this.premiumHouses)
            for (PremiumHouseDoor door : house.getDoors())
//                if (loc.equals(door.getLocation()))
                if (HouseUtils.locEqualsLoc(loc, door.getLocation(), false))
                    return new Object[]{house, door};
        return null;
    }

    public PremiumHouse getPremiumHouseFromChest(Location loc) {
        for (PremiumHouse house : this.premiumHouses)
            for (PremiumHouseChest chest : house.getChests())
//                if (loc.equals(chest.getLoc1()) || loc.equals(chest.getLoc2()))
                if (HouseUtils.locEqualsLoc(loc, chest.getLoc1(), false) || HouseUtils.locEqualsLoc(loc, chest.getLoc2(), false))
                    return house;
        return null;
    }

    public void load() {
        this.houses = new ArrayList<>();
        this.premiumHouses = new ArrayList<>();

        ServerUtil.runTaskAsync(() -> {
            try (Connection connection = BaseDatabase.getInstance().getConnection()) {
                long start = System.currentTimeMillis();
                
            	// set all house data to valid
            	HouseDAO.resetValidData(connection);
            	
            	this.houses.addAll(HouseDAO.loadHouses(connection));
                System.out.println("Houses and data loaded. [" + this.houses.size() + "] (took " + (System.currentTimeMillis() - start) + "ms)");

                start = System.currentTimeMillis();
                this.premiumHouses.addAll(PremiumHouseDAO.loadHouses(connection));
                System.out.println("PremiumHouses and data loaded. [" + this.premiumHouses.size() + "] (took " + (System.currentTimeMillis() - start) + "ms)");

            } catch (SQLException e) {
                e.printStackTrace();
            }
        });

//        YamlConfiguration c = Houses.getSettings().getHousesConfig();
//        for (String idString : c.getKeys(false)) {
//            try {
//                int id;
//                try {
//                    id = Integer.parseInt(idString);
//                } catch (NumberFormatException e) {
//                    continue;
//                }
//                int price = c.getInt(id + ".price");
//                List<HouseDoor> doors = new ArrayList<>();
//                if (c.get(id + ".doors") != null)
//                    for (String doorIdString : c.getConfigurationSection(id + ".doors").getKeys(false)) {
//                        int doorId;
//                        try {
//                            doorId = Integer.parseInt(doorIdString);
//                        } catch (NumberFormatException e) {
//                            continue;
//                        }
//                        Location doorLocation = Utils
//                                .blockLocationFromString(c.getString(id + ".doors." + doorId + ".location"));
//                        Location insideLocation = Utils
//                                .teleportLocationFromString(c.getString(id + ".doors." + doorId + ".insideLocation"));
//                        Location outsideLocation = Utils
//                                .teleportLocationFromString(c.getString(id + ".doors." + doorId + ".outsideLocation"));
//                        doors.add(new HouseDoor(doorId, id, doorLocation, insideLocation, outsideLocation));
//                    }
//                List<HouseChest> chests = new ArrayList<>();
//                if (c.get(id + ".chests") != null)
//                    for (String chestIdString : c.getConfigurationSection(id + ".chests").getKeys(false)) {
//                        int chestId;
//                        try {
//                            chestId = Integer.parseInt(chestIdString);
//                        } catch (NumberFormatException e) {
//                            continue;
//                        }
//                        Location loc1 = Utils.blockLocationFromString(c.getString(id + ".chests." + chestId + ".loc1"));
//                        Location loc2 = Utils.blockLocationFromString(c.getString(id + ".chests." + chestId + ".loc2"));
//                        chests.add(new HouseChest(chestId, id, loc1, loc2));
//                    }
//                List<Location> signs = new ArrayList<>();
//                if (c.get(id + ".signs") != null)
//                    signs.addAll(c.getStringList(id + ".signs").stream().map(Utils::blockLocationFromString).collect(Collectors.toList()));
//                this.houses.add(new House(id, price, chests, doors, signs));
//            } catch (Exception e) {
//                Houses.error("Error while parsing house " + idString);
//                e.printStackTrace();
//            }
//        }

//        this.premiumHouses = new ArrayList<>();
//        c = Houses.getSettings().getPremiumHousesConfig();
//        for (String idString : c.getKeys(false)) {
//            try {
//                int id;
//                try {
//                    id = Integer.parseInt(idString);
//                } catch (NumberFormatException e) {
//                    continue;
//                }
//                int permits = c.get(id + ".permits") == null ? 1000 : c.getInt(id + ".permits");
//                UUID owner = c.get(id + ".owner") == null ? null : UUID.fromString(c.getString(id + ".owner"));
//                String ownerName = c.getString(id + ".ownerName");
//                List<PremiumHouseDoor> doors = new ArrayList<>();
//                if (c.get(id + ".doors") != null) {
//                    for (String doorIdString : c.getConfigurationSection(id + ".doors").getKeys(false)) {
//                        int doorId;
//                        try {
//                            doorId = Integer.parseInt(doorIdString);
//                        } catch (NumberFormatException e) {
//                            continue;
//                        }
//                        Location doorLocation = Utils
//                                .blockLocationFromString(c.getString(id + ".doors." + doorId + ".location"));
//                        Location insideLocation = Utils
//                                .teleportLocationFromString(c.getString(id + ".doors." + doorId + ".insideLocation"));
//                        Location outsideLocation = Utils
//                                .teleportLocationFromString(c.getString(id + ".doors." + doorId + ".outsideLocation"));
//                        doors.add(new PremiumHouseDoor(doorId, id, doorLocation, insideLocation, outsideLocation));
//                    }
//                }
//                List<PremiumHouseChest> chests = new ArrayList<>();
//                if (c.get(id + ".chests") != null) {
//                    for (String chestIdString : c.getConfigurationSection(id + ".chests").getKeys(false)) {
//                        int chestId;
//                        try {
//                            chestId = Integer.parseInt(chestIdString);
//                        } catch (NumberFormatException e) {
//                            continue;
//                        }
//                        Location loc1 = Utils.blockLocationFromString(c.getString(id + ".chests." + chestId + ".loc1"));
//                        Location loc2 = Utils.blockLocationFromString(c.getString(id + ".chests." + chestId + ".loc2"));
//                        chests.add(new PremiumHouseChest(chestId, id, loc1, loc2));
//                    }
//                }
//                List<Location> signs = new ArrayList<>();
//                if (c.get(id + ".signs") != null) {
//                    signs.addAll(c.getStringList(id + ".signs").stream().map(Utils::blockLocationFromString).collect(Collectors.toList()));
//                }
//                List<PremiumHouseGuest> guests = new ArrayList<>();
//                if (c.get(id + ".guests") != null) {
//                    for (String uuidString : c.getConfigurationSection(id + ".guests").getKeys(false)) {
//                        guests.add(new PremiumHouseGuest(UUID.fromString(uuidString),
//                                c.getString(id + ".guests." + uuidString)));
//                    }
//                }
//                Collection<EditableBlock> editableBlocks = new ArrayList<>();
//                if(c.contains(id + ".blocks")) {
//                    for(String loc : c.getConfigurationSection(id + ".blocks").getKeys(false)) {
//                        String[] original = String.valueOf(c.get(id + ".blocks." + loc + ".default")).split(",");
//                        String originalMaterial = String.valueOf(original[0]);
//                        byte originalData = Byte.valueOf(original[1]);
//                        editableBlocks.add(new EditableBlock(Utils.blockLocationFromString(loc), Material.matchMaterial(originalMaterial), originalData));
//                    }
//                }
//                Collection<PremiumHouseTrashcan> trashcans = new ArrayList<>();
//                if (c.contains(id + ".trashcans")) {
//                    for (String trashcanIdString : c.getConfigurationSection(id + ".trashcans").getKeys(false)) {
//                        int trashcanId;
//                        try {
//                            trashcanId = Integer.parseInt(trashcanIdString);
//                        } catch (NumberFormatException e) {
//                            continue;
//                        }
//                        Location location = Utils.blockLocationFromString(c.getString(id + ".trashcans." + trashcanIdString + ".loc"));
//                        boolean owned = c.getBoolean(id + ".trashcans." + trashcanIdString + ".owned");
//                        trashcans.add(new PremiumHouseTrashcan(trashcanId, id, location, owned));
//                    }
//                }
//                this.premiumHouses.add(new PremiumHouse(id, permits, owner, ownerName, doors, chests, signs, guests, editableBlocks, trashcans));
//            } catch (Exception e) {
//                Houses.error("Error while parsing premium house " + idString);
//                e.printStackTrace();
//            }
//        }
    }

    public void save() {
//        YamlConfiguration c = Houses.getSettings().getHousesConfig();
//        for (String s : c.getKeys(false))
//            c.set(s, null);
//
//        for (House house : this.houses) {
//            int id = house.getId();
//            c.set(id + ".price", house.getPrice());
//
//            //Door save
//            for (HouseDoor door : house.getDoors()) {
//                int doorId = door.getId();
//                c.set(id + ".doors." + doorId + ".location", Utils.blockLocationToString(door.getLocation()));
//                c.set(id + ".doors." + doorId + ".insideLocation", Utils.teleportLocationToString(door.getInsideLocation()));
//                c.set(id + ".doors." + doorId + ".outsideLocation", Utils.teleportLocationToString(door.getOutsideLocation()));
//            }
//
//            //Chest save
//            for (HouseChest chest : house.getChests()) {
//                int chestId = chest.getId();
//                c.set(id + ".chests." + chestId + ".loc1", Utils.blockLocationToString(chest.getLoc1()));
//                c.set(id + ".chests." + chestId + ".loc2", Utils.blockLocationToString(chest.getLoc2()));
//            }
//
//            //Sign save
//            c.set(id + ".signs", house.getSigns().stream().map(Utils::blockLocationToString).collect(Collectors.toList()));
//        }
//        Utils.saveConfig(c, "houses");
//
//        c = Houses.getSettings().getPremiumHousesConfig();
//        for (String s : c.getKeys(false))
//            c.set(s, null);
//
//        for (PremiumHouse house : this.premiumHouses) {
//            int id = house.getId();
//            c.set(id + ".permits", house.getPermits());
//            c.set(id + ".owner", house.getOwner() == null ? null : house.getOwner().toString());
//            c.set(id + ".ownerName", house.getOwnerName());
//
//            //Door save
//            for (PremiumHouseDoor door : house.getDoors()) {
//                int doorId = door.getId();
//                c.set(id + ".doors." + doorId + ".location", Utils.blockLocationToString(door.getLocation()));
//                c.set(id + ".doors." + doorId + ".insideLocation",
//                        Utils.teleportLocationToString(door.getInsideLocation()));
//                c.set(id + ".doors." + doorId + ".outsideLocation",
//                        Utils.teleportLocationToString(door.getOutsideLocation()));
//            }
//
//            //Chest save
//            for (PremiumHouseChest chest : house.getChests()) {
//                int chestId = chest.getId();
//                c.set(id + ".chests." + chestId + ".loc1", Utils.blockLocationToString(chest.getLoc1()));
//                c.set(id + ".chests." + chestId + ".loc2", Utils.blockLocationToString(chest.getLoc2()));
//            }
//
//            //Sign save
//            c.set(id + ".signs", house.getSigns().stream().map(Utils::blockLocationToString).collect(Collectors.toList()));
//
//            //Guest save
//            for (PremiumHouseGuest guest : house.getGuests()) {
//                String name = guest.getName();
//                c.set(id + ".guests." + guest.getUuid(), name == null ? guest.getUuid().toString() : name);
//            }
//
//            //Blocks save
//            for(EditableBlock location : house.getEditableBlocks()) {
//                c.set(id + ".blocks." + Utils.blockLocationToString(location.getLocation())
//                        + ".default", location.getDefaultType().name() + "," + location.getDefaultData());
//            }
//
//            //Trashcan save
//            for (PremiumHouseTrashcan premiumHouseTrashcan : house.getTrashcans()) {
//                int trashcanId = premiumHouseTrashcan.getId();
//                String location = Utils.blockLocationToString(premiumHouseTrashcan.getLocation());
//                c.set(id + ".trashcans." + trashcanId + ".loc", location);
//                c.set(id + ".trashcans." + trashcanId + ".owned", premiumHouseTrashcan.isOwned());
//            }
//        }
//        Utils.saveConfig(c, "premiumHouses");
    }

    public void createHouse(Callback<House> callback) {
//        House house = new House(this.getUnusedHouseId());
//        this.houses.add(house);
//        return house;

        ServerUtil.runTaskAsync(() -> {
            House house = null;
            try (Connection connection = BaseDatabase.getInstance().getConnection()) {
                house = HouseDAO.addHouse(connection, this.getUnusedHouseId(), 1);
            } catch (SQLException e) {
                e.printStackTrace();
                callback.call(null);
                return;
            }

            if (house == null) {
                callback.call(null);
                return;
            }

            this.houses.add(house);
            callback.call(house);
        });
    }

    public void createPremiumHouse(Callback<PremiumHouse> callback) {
//        PremiumHouse house = new PremiumHouse(this.getUnusedPremiumHouseId());
//        this.premiumHouses.add(house);
//        return house;
        ServerUtil.runTaskAsync(() -> {
            PremiumHouse house = null;
            try (Connection connection = BaseDatabase.getInstance().getConnection()) {
                house = PremiumHouseDAO.addHouse(connection, this.getUnusedPremiumHouseId(), 1);
            } catch (SQLException e) {
                e.printStackTrace();
                callback.call(null);
                return;
            }

            if (house == null) {
                callback.call(null);
                return;
            }

            this.premiumHouses.add(house);
            callback.call(house);
        });
    }

    public void removeHouse(House house) {
        house.removeAllChestsNonDB();
        house.removeAllOwnersNonDB();
        this.houses.remove(house);

        ServerUtil.runTaskAsync(() -> {
            try (Connection connection = BaseDatabase.getInstance().getConnection()) {
                HouseDAO.deleteHouse(connection, house.getUniqueIdentifier());
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public void removePremiumHouse(PremiumHouse house) {
        this.premiumHouses.remove(house);
        ServerUtil.runTaskAsync(() -> {
            try (Connection connection = BaseDatabase.getInstance().getConnection()) {
                HouseDAO.deleteHouse(connection, house.getUniqueIdentifier());
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public int getUnusedHouseId() {
        for (int i = 1; i < Integer.MAX_VALUE; i++) {
            if (this.getHouse(i) == null)
                return i;
        }

        return -1;
    }

    public int getUnusedPremiumHouseId() {
        for (int i = 1; i < Integer.MAX_VALUE; i++) {
            if (this.getPremiumHouse(i) == null)
                return i;
        }

        return -1;
    }

    public List<House> getHouses(int start, int end) {
        return this.houses.stream().filter(house -> house.getId() >= start && house.getId() <= end).collect(Collectors.toList());
    }

    public List<PremiumHouse> getPremiumHouses(int start, int end) {
        return this.premiumHouses.stream().filter(house -> house.getId() >= start && house.getId() <= end).collect(Collectors.toList());
    }

    public House getHouseFromSign(Location loc) {
//        for (House house : this.houses)
//            for (Location l : house.getSigns())
//                if (l.equals(loc))
//                    return house;

        return this.houses.stream().filter(h -> h.getSigns().stream().anyMatch(sign -> loc.equals(sign.getLocation()))).findFirst().orElse(null);
    }

    public PremiumHouse getPremiumHouseFromSign(Location loc) {
//        for (PremiumHouse house : this.premiumHouses)
//            for (Location l : house.getSigns())
//                if (l.equals(loc))
//                    return house;

        return this.premiumHouses.stream().filter(h -> h.getSigns().stream().anyMatch(sign -> loc.equals(sign.getLocation()))).findFirst().orElse(null);
    }

    public void forceSell(PremiumHouse premiumHouse, Player sender) {
        Integer permits = premiumHouse.getPermits();
        premiumHouse.getChests().forEach(PremiumHouseChest::clear);
        premiumHouse.getEditableBlocks().forEach(block -> {
            block.getLocation().getBlock().setType(block.getDefaultType());
            block.getLocation().getBlock().setData(block.getDefaultData());
        });
        Collection<PremiumHouseTrashcan> trashcans = premiumHouse.getTrashcans().stream().filter(PremiumHouseTrashcan::isOwned).collect(Collectors.toList());
        trashcans.forEach(trashCan -> trashCan.setOwned(false));
        permits += trashcans.size() * 5;

//        Core.sql.update("update " + Core.name() + " set permits=permits+" + permits + " where uuid='" + premiumHouse.getOwner().toString() + "';");
        Integer finalPermits = permits;
        ServerUtil.runTaskAsync(() -> {
            BaseDatabase.runCustomQuery("update " + Core.name() + " set permits=permits+" + finalPermits + " where uuid='" + premiumHouse.getOwner().toString() + "';");
        });

        Bukkit.broadcastMessage(Lang.HOUSES.f(premiumHouse.getOwnerName() + " &7sold premium house " + "&a" + premiumHouse.getId() + " &7for &3&l" + premiumHouse.getPermits() + " Permits&7!"));
        premiumHouse.removeOwner(true);
        Houses.getHousesManager().save();
    }
}
