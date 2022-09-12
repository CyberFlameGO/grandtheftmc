package net.grandtheftmc.houses.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.Material;
import org.json.JSONObject;

import com.google.common.collect.Lists;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.util.UUIDUtil;
import net.grandtheftmc.houses.HouseUtils;
import net.grandtheftmc.houses.JSONHelper;
import net.grandtheftmc.houses.houses.EditableBlock;
import net.grandtheftmc.houses.houses.HouseSign;
import net.grandtheftmc.houses.houses.PremiumHouse;
import net.grandtheftmc.houses.houses.PremiumHouseChest;
import net.grandtheftmc.houses.houses.PremiumHouseDoor;
import net.grandtheftmc.houses.houses.PremiumHouseGuest;
import net.grandtheftmc.houses.houses.PremiumHouseTrashcan;

public class PremiumHouseDAO {

    public static List<PremiumHouse> loadHouses(Connection connection) {
        List<PremiumHouse> list = Lists.newArrayList();

        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT id, house_num, price FROM gtm_house WHERE server_key=? AND premium=?;")) {
            statement.setString(1, Core.name().toUpperCase());
            statement.setBoolean(2, true);

            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    int uniqueHouseIdentifier = result.getInt("id");
                    PremiumHouse house = new PremiumHouse(uniqueHouseIdentifier, result.getInt("house_num"), result.getInt("price"));
                    loadChests(connection, uniqueHouseIdentifier, house);
                    loadDoors(connection, uniqueHouseIdentifier, house);
                    loadSigns(connection, uniqueHouseIdentifier, house);
                    loadTrashcans(connection, uniqueHouseIdentifier, house);
                    loadEditableBlocks(connection, uniqueHouseIdentifier, house);
                    loadUsers(connection, uniqueHouseIdentifier, house);
                    list.add(house);
                }

                return list;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static PremiumHouse addHouse(Connection connection, int houseId, int price) {
        try (PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO gtm_house (house_num, server_key, premium, currency, price) VALUES (?, ?, ?, ?, ?);", Statement.RETURN_GENERATED_KEYS)) {
            statement.setInt(1, houseId);
            statement.setString(2, Core.name().toUpperCase());
            statement.setBoolean(3, true);
            statement.setString(4, "PERMIT");
            statement.setInt(5, price);

            statement.executeUpdate();

            try (ResultSet result = statement.getGeneratedKeys()) {
                if (result.next()) {
                    return new PremiumHouse(result.getInt(1), houseId, price);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean loadChests(Connection connection, int uniqueHouseIdentifier, PremiumHouse house) {
        
    	// keep track of invalid chests (by hotspot)
    	List<Integer> invalidChests = new ArrayList<>();
    	
    	try (PreparedStatement statement = connection.prepareStatement(
                "SELECT hotspot_id, data FROM gtm_house_data WHERE house_id=? AND hotspot_type=?;")) {
            statement.setInt(1, uniqueHouseIdentifier);
            statement.setString(2, "CHEST");

            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                	int hotspotID = result.getInt("hotspot_id");
                    JSONObject object = HouseUtils.dataToJson(result.getString("data"));

                    try {
                        if (object.has("id")) {
                            int data_id = object.getInt("id");
                            Location loc1 = HouseUtils.getLocationFromString(object.getString("loc1"));

                            if (object.has("loc2")) {
                                Location loc2 = HouseUtils.getLocationFromString(object.getString("loc2"));
                                house.addChest(new PremiumHouseChest(hotspotID, data_id, house.getId(), loc1, loc2));
                            } else {
                                house.addChest(new PremiumHouseChest(hotspotID, data_id, house.getId(), loc1));
                            }
                        }
                    } catch (Exception e) {
//                        e.printStackTrace();
                        Core.error("House (" + uniqueHouseIdentifier + ") couldn't load CHEST.");
                        invalidChests.add(hotspotID);
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    	
    	// set invalid in db so we can fix
    	if (!invalidChests.isEmpty()){
    		for (Integer hotspotID : invalidChests){   			
    			HouseDAO.setValidData(connection, uniqueHouseIdentifier, hotspotID, false);
    		}
    	}
    	
        return true;
    }

    public static boolean loadDoors(Connection connection, int uniqueHouseIdentifier, PremiumHouse house) {
        
    	// keep track of invalid chests (by hotspot)
    	List<Integer> invalidDoors = new ArrayList<>();
    	
    	try (PreparedStatement statement = connection.prepareStatement(
                "SELECT hotspot_id, data FROM gtm_house_data WHERE house_id=? AND hotspot_type=?;")) {
            statement.setInt(1, uniqueHouseIdentifier);
            statement.setString(2, "DOOR");

            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                	int hotspotID = result.getInt("hotspot_id");
                    JSONObject object = HouseUtils.dataToJson(result.getString("data"));

                    try {
                        if (object.has("id")) {
                            int data_id = object.getInt("id");
//                    Location location = HouseUtils.getLocationFromString(object.getString("location"));
//
//                    if (object.has("insideLocation") && object.has("outsideLocation")) {
//                        Location inside = HouseUtils.getLocationFromString(object.getString("insideLocation"));
//                        Location outside = HouseUtils.getLocationFromString(object.getString("outsideLocation"));
//                        house.addDoor(new PremiumHouseDoor(result.getInt("hotspot_id"), data_id, house.getId(), location, inside, outside));
//                    } else {
//                        house.addDoor(new PremiumHouseDoor(result.getInt("hotspot_id"), data_id, house.getId(), location));
//                    }

                            Location location = null, inside = null, outside = null;
                            if (object.has("location")) {
                                location = HouseUtils.getLocationFromString(object.getString("location"));
                            }

                            if (object.has("insideLocation")) {
                                inside = HouseUtils.getLocationFromString(object.getString("insideLocation"));
                            }

                            if (object.has("outsideLocation")) {
                                outside = HouseUtils.getLocationFromString(object.getString("outsideLocation"));
                            }

                            house.addDoor(new PremiumHouseDoor(hotspotID, data_id, house.getId(), location, inside, outside));
                        }
                    } catch (Exception e) {
//                        e.printStackTrace();
                        Core.error("House (" + uniqueHouseIdentifier + ") couldn't load DOOR.");
                        invalidDoors.add(hotspotID);
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    	
    	// set invalid in db so we can fix
    	if (!invalidDoors.isEmpty()){
    		for (Integer hotspotID : invalidDoors){   			
    			HouseDAO.setValidData(connection, uniqueHouseIdentifier, hotspotID, false);
    		}
    	}
    	
        return true;
    }

    public static boolean loadSigns(Connection connection, int uniqueHouseIdentifier, PremiumHouse house) {
        
    	// keep track of invalid chests (by hotspot)
    	List<Integer> invalidSigns = new ArrayList<>();
    	
    	try (PreparedStatement statement = connection.prepareStatement(
                "SELECT hotspot_id, data FROM gtm_house_data WHERE house_id=? AND hotspot_type=?;")) {
            statement.setInt(1, uniqueHouseIdentifier);
            statement.setString(2, "SIGN");

            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                	int hotspotID = result.getInt("hotspot_id");
                    JSONObject object = HouseUtils.dataToJson(result.getString("data"));

                    try {
                        if (object.has("loc")) {
                            Location loc = HouseUtils.getLocationFromString(object.getString("loc"));
                            house.addSign(new HouseSign(hotspotID, uniqueHouseIdentifier, loc));
                        }
                    } catch (Exception e) {
//                        e.printStackTrace();
                        Core.error("House (" + uniqueHouseIdentifier + ") couldn't load SIGN.");
                        invalidSigns.add(hotspotID);
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    	
    	// set invalid in db so we can fix
    	if (!invalidSigns.isEmpty()){
    		for (Integer hotspotID : invalidSigns){   			
    			HouseDAO.setValidData(connection, uniqueHouseIdentifier, hotspotID, false);
    		}
    	}
    	
        return true;
    }

    public static boolean loadTrashcans(Connection connection, int uniqueHouseIdentifier, PremiumHouse house) {
        
    	// keep track of invalid chests (by hotspot)
    	List<Integer> invalidTrashcans = new ArrayList<>();
    	
    	try (PreparedStatement statement = connection.prepareStatement(
                "SELECT hotspot_id, data FROM gtm_house_data WHERE house_id=? AND hotspot_type=?;")) {
            statement.setInt(1, uniqueHouseIdentifier);
            statement.setString(2, "TRASH");

            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                	int hotspotID = result.getInt("hotspot_id");
                    JSONObject object = HouseUtils.dataToJson(result.getString("data"));

                    try {
                        if (object.has("loc") && object.has("id") && object.has("owned")) {
                            Location loc = HouseUtils.getLocationFromString(object.getString("loc"));
                            int data_id = object.getInt("id");
                            boolean owned = object.getBoolean("owned");
                            house.addTrashcan(new PremiumHouseTrashcan(hotspotID, data_id, house.getId(), loc, owned));
                        }
                    } catch (Exception e) {
//                        e.printStackTrace();
                    	invalidTrashcans.add(hotspotID);
                    	Core.error("House (" + uniqueHouseIdentifier + ") couldn't load TRASHCAN.");
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    	
    	// set invalid in db so we can fix
    	if (!invalidTrashcans.isEmpty()){
    		for (Integer hotspotID : invalidTrashcans){   			
    			HouseDAO.setValidData(connection, uniqueHouseIdentifier, hotspotID, false);
    		}
    	}
    	
        return true;
    }

    public static boolean loadEditableBlocks(Connection connection, int uniqueHouseIdentifier, PremiumHouse house) {
        
    	// keep track of invalid chests (by hotspot)
    	List<Integer> invalidBlocks = new ArrayList<>();
    	
    	try (PreparedStatement statement = connection.prepareStatement(
                "SELECT hotspot_id, data FROM gtm_house_data WHERE house_id=? AND hotspot_type=?;")) {
            statement.setInt(1, uniqueHouseIdentifier);
            statement.setString(2, "BLOCK");

            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                	int hotspotID = result.getInt("hotspot_id");
                    JSONObject object = HouseUtils.dataToJson(result.getString("data"));

                    try {
                        if (object.has("loc") && object.has("block_data")) {
                            Location loc = HouseUtils.getLocationFromString(object.getString("loc"));
                            String[] split = object.getString("block_data").split(",");
                            house.addEditableBlock(new EditableBlock(hotspotID, loc, Material.valueOf(split[0]), Byte.parseByte(split[1])));
                        }
                    } catch (Exception e) {
//                        e.printStackTrace();
                        Core.error("House (" + uniqueHouseIdentifier + ") couldn't load BLOCK.");
                        invalidBlocks.add(hotspotID);
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    	
    	// set invalid in db so we can fix
    	if (!invalidBlocks.isEmpty()){
    		for (Integer hotspotID : invalidBlocks){   			
    			HouseDAO.setValidData(connection, uniqueHouseIdentifier, hotspotID, false);
    		}
    	}
    	
        return true;
    }

    public static boolean loadUsers(Connection connection, int uniqueHouseIdentifier, PremiumHouse house) {
        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT HEX(HU.uuid) as uid, U.name, HU.is_owner FROM gtm_house_user HU, user U WHERE HU.house_id=? AND U.uuid=HU.uuid;")) {
            statement.setInt(1, uniqueHouseIdentifier);

            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    UUID uuid = UUIDUtil.createUUID(result.getString("uid")).orElse(null);
                    if (result.getBoolean("is_owner")) {
                        house.setOwner(uuid, result.getString("name"));
                    } else {
                        house.addGuest(new PremiumHouseGuest(uuid, result.getString("name")));
                    }
                }

                return true;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean addChest(Connection connection, int uniqueHouseId, PremiumHouseChest chest, JSONHelper data) {
        try (PreparedStatement statement = connection.prepareStatement("INSERT INTO gtm_house_data (house_id, hotspot_type, data) VALUES (?, ?, ?);", Statement.RETURN_GENERATED_KEYS)) {
            statement.setInt(1, uniqueHouseId);
            statement.setString(2, "CHEST");
            statement.setString(3, data == null ? null : data.string());

            statement.executeUpdate();

            try (ResultSet result = statement.getGeneratedKeys()) {
                if (result.next()) {
                    chest.setHotspotId(result.getInt(1));
                    return true;
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public static boolean addDoor(Connection connection, int uniqueHouseId, PremiumHouseDoor door, JSONHelper data) {
        try (PreparedStatement statement = connection.prepareStatement("INSERT INTO gtm_house_data (house_id, hotspot_type, data) VALUES (?, ?, ?);", Statement.RETURN_GENERATED_KEYS)) {
            statement.setInt(1, uniqueHouseId);
            statement.setString(2, "DOOR");
            statement.setString(3, data == null ? null : data.string());

            statement.executeUpdate();

            try (ResultSet result = statement.getGeneratedKeys()) {
                if (result.next()) {
                    door.setHotspotId(result.getInt(1));
                    return true;
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public static boolean addBlock(Connection connection, int uniqueHouseId, EditableBlock block, JSONHelper data) {
        try (PreparedStatement statement = connection.prepareStatement("INSERT INTO gtm_house_data (house_id, hotspot_type, data) VALUES (?, ?, ?);", Statement.RETURN_GENERATED_KEYS)) {
            statement.setInt(1, uniqueHouseId);
            statement.setString(2, "BLOCK");
            statement.setString(3, data == null ? null : data.string());

            statement.executeUpdate();

            try (ResultSet result = statement.getGeneratedKeys()) {
                if (result.next()) {
                    block.setHotspotId(result.getInt(1));
                    return true;
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public static boolean addTrashcan(Connection connection, int uniqueHouseId, PremiumHouseTrashcan trashcan, JSONHelper data) {
        try (PreparedStatement statement = connection.prepareStatement("INSERT INTO gtm_house_data (house_id, hotspot_type, data) VALUES (?, ?, ?);", Statement.RETURN_GENERATED_KEYS)) {
            statement.setInt(1, uniqueHouseId);
            statement.setString(2, "TRASH");
            statement.setString(3, data == null ? null : data.string());

            statement.executeUpdate();

            try (ResultSet result = statement.getGeneratedKeys()) {
                if (result.next()) {
                    trashcan.setHotspotId(result.getInt(1));
                    return true;
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public static boolean deleteHouseUsers(Connection connection, int uniqueHouseId, boolean owner) {
        try (PreparedStatement statement = connection.prepareStatement("DELETE FROM gtm_house_user WHERE house_id=? AND is_owner=?;")) {
            statement.setInt(1, uniqueHouseId);
            statement.setBoolean(2, owner);

            statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public static boolean deleteHouseGuest(Connection connection, int uniqueHouseId, UUID uuid) {
        try (PreparedStatement statement = connection.prepareStatement("DELETE FROM gtm_house_user WHERE house_id=? AND uuid=UNHEX(?);")) {
            statement.setInt(1, uniqueHouseId);
            statement.setString(2, uuid.toString().replaceAll("-", ""));

            statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public static boolean addHouseGuest(Connection connection, int uniqueHouseId, UUID uuid, boolean owner) {
        try (PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO gtm_house_user (house_id, uuid, is_owner) VALUES (?,UNHEX(?),?) ON DUPLICATE KEY UPDATE is_owner=VALUES(is_owner);")) {
            statement.setInt(1, uniqueHouseId);
            statement.setString(2, uuid.toString().replaceAll("-", ""));
            statement.setBoolean(3, owner);

            statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public static boolean setTrashcanOwned(Connection connection, int hotspotId, JSONHelper helper) {
        try (PreparedStatement statement = connection.prepareStatement(
                "UPDATE gtm_house_data SET data=? WHERE hotspot_id=?")) {
            statement.setString(1, helper.string());
            statement.setInt(2, hotspotId);

            statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }
}
