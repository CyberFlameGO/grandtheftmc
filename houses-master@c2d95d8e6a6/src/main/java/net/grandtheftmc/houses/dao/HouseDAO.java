package net.grandtheftmc.houses.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.json.JSONObject;

import com.google.common.collect.Lists;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.gtm.GTMUtils;
import net.grandtheftmc.houses.HouseUtils;
import net.grandtheftmc.houses.JSONHelper;
import net.grandtheftmc.houses.houses.House;
import net.grandtheftmc.houses.houses.HouseChest;
import net.grandtheftmc.houses.houses.HouseDoor;
import net.grandtheftmc.houses.houses.HouseSign;
import net.grandtheftmc.houses.houses.HousesManager;
import net.grandtheftmc.houses.users.HouseUser;
import net.grandtheftmc.houses.users.UserHouse;
import net.grandtheftmc.houses.users.UserHouseChest;

public class HouseDAO {

    public static List<House> loadHouses(Connection connection) {
        List<House> list = Lists.newArrayList();

        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT id, house_num, price FROM gtm_house WHERE server_key=? AND premium=?;")) {
            statement.setString(1, Core.name().toUpperCase());
            statement.setBoolean(2, false);

            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    int uniqueHouseIdentifier = result.getInt("id");
                    House house = new House(uniqueHouseIdentifier, result.getInt("house_num"), result.getInt("price"));
                    loadChests(connection, uniqueHouseIdentifier, house);
                    loadDoors(connection, uniqueHouseIdentifier, house);
                    loadSigns(connection, uniqueHouseIdentifier, house);
                    list.add(house);
                }

                return list;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static House addHouse(Connection connection, int houseId, int price) {
        try (PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO gtm_house (house_num, server_key, premium, currency, price) VALUES (?, ?, ?, ?, ?);", Statement.RETURN_GENERATED_KEYS)) {
            statement.setInt(1, houseId);
            statement.setString(2, Core.name().toUpperCase());
            statement.setBoolean(3, false);
            statement.setString(4, "MONEY");
            statement.setInt(5, price);

            statement.executeUpdate();

            try (ResultSet result = statement.getGeneratedKeys()) {
                if(result.next()) {
                    return new House(result.getInt(1), houseId, price);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static House deleteHouse(Connection connection, int uniqueHouseId) {
        try (PreparedStatement statement = connection.prepareStatement(
                "DELETE FROM gtm_house WHERE id=?")) {
            statement.setInt(1, uniqueHouseId);

            statement.execute();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean loadChests(Connection connection, int uniqueHouseIdentifier, House house) {
        
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
                                house.addChest(new HouseChest(hotspotID, data_id, house.getId(), loc1, loc2));
                            } else {
                                house.addChest(new HouseChest(hotspotID, data_id, house.getId(), loc1));
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
    			setValidData(connection, uniqueHouseIdentifier, hotspotID, false);
    		}
    	}
    	
    	return true;
    }

    public static boolean loadDoors(Connection connection, int uniqueHouseIdentifier, House house) {
        
    	// keep track of invalid doors (by hotspot)
    	List<Integer> invalidDoors = new ArrayList<>();
    	
    	try (PreparedStatement statement = connection.prepareStatement(
                "SELECT data, hotspot_id FROM gtm_house_data WHERE house_id=? AND hotspot_type=?;")) {
            statement.setInt(1, uniqueHouseIdentifier);
            statement.setString(2, "DOOR");

            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                	int hotspotID = result.getInt("hotspot_id");
                    JSONObject object = HouseUtils.dataToJson(result.getString("data"));

                    try {
                        if (object.has("id")) {
                            int data_id = object.getInt("id");
//                    System.out.println(object.toString());

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

                            house.addDoor(new HouseDoor(hotspotID, data_id, house.getId(), location, inside, outside));
                            
                            // if we get here it's valid
                            // this must be async because if its not, connection leak
//                            ServerUtil.runTaskAsync(() -> {
//                            	try (Connection conn = BaseDatabase.getInstance().getConnection()){
//                            		setValidData(conn, uniqueHouseIdentifier, hotspotID, true);
//                            	}
//                            	catch(Exception exc){
//                            		exc.printStackTrace();
//                            	}
//                            });
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
    			setValidData(connection, uniqueHouseIdentifier, hotspotID, false);
    		}
    	}
    	
        return true;
    }

    public static boolean loadSigns(Connection connection, int uniqueHouseIdentifier, House house) {
       
    	// keep track of invalid doors (by hotspot)
    	List<Integer> invalidSigns = new ArrayList<>();
    	
    	try (PreparedStatement statement = connection.prepareStatement(
                "SELECT data, hotspot_id FROM gtm_house_data WHERE house_id=? AND hotspot_type=?;")) {
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
                            
                            // if we get here it's valid
                            // this must be async because if its not, connection leak
//                            ServerUtil.runTaskAsync(() -> {
//                            	try (Connection conn = BaseDatabase.getInstance().getConnection()){
//                            		setValidData(conn, uniqueHouseIdentifier, hotspotID, true);
//                            	}
//                            	catch(Exception exc){
//                            		exc.printStackTrace();
//                            	}
//                            });
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
    			setValidData(connection, uniqueHouseIdentifier, hotspotID, false);
    		}
    	}
    	
        return true;
    }

    public static boolean addChest(Connection connection, int uniqueHouseId, HouseChest chest, JSONHelper data) {
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

    public static boolean addDoor(Connection connection, int uniqueHouseId, HouseDoor door, JSONHelper data) {
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

    public static boolean addSign(Connection connection, int uniqueHouseId, HouseSign sign, JSONHelper data) {
        try (PreparedStatement statement = connection.prepareStatement("INSERT INTO gtm_house_data (house_id, hotspot_type, data) VALUES (?, ?, ?);", Statement.RETURN_GENERATED_KEYS)) {
            statement.setInt(1, uniqueHouseId);
            statement.setString(2, "SIGN");
            statement.setString(3, data == null ? null : data.string());

            statement.executeUpdate();

            try (ResultSet result = statement.getGeneratedKeys()) {
                if (result.next()) {
                    sign.setHotspotId(result.getInt(1));
                    return true;
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public static boolean deleteSign(Connection connection, int hotspotId) {
        try (PreparedStatement statement = connection.prepareStatement("DELETE FROM gtm_house_data WHERE hotspot_id=?;")) {
            statement.setInt(1, hotspotId);

            statement.execute();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public static boolean deleteAllSigns(Connection connection, int uniqueHouseId) {
        try (PreparedStatement statement = connection.prepareStatement("DELETE FROM gtm_house_data WHERE house_id=? AND hotspot_type=?;")) {
            statement.setInt(1, uniqueHouseId);
            statement.setString(2, "SIGN");

            statement.execute();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public static boolean updateData(Connection connection, int hotspotId, JSONHelper data) {
        try (PreparedStatement statement = connection.prepareStatement("UPDATE gtm_house_data SET data=? WHERE hotspot_id=?;")) {
            statement.setString(1, data == null ? null : data.string());
            statement.setInt(2, hotspotId);

            statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public static boolean updateDoor(Connection connection, int hotspotId, int doorId, Location door, Location inside, Location outside) {
        try (PreparedStatement statement = connection.prepareStatement("UPDATE gtm_house_data SET data=? WHERE hotspot_id=?;")) {
            JSONHelper helper = new JSONHelper().put("id", doorId);
            helper.put("location", HouseUtils.locationToString(door));
            helper.put("insideLocation", HouseUtils.locationToString(inside));
            helper.put("outsideLocation", HouseUtils.locationToString(outside));
            statement.setString(1, helper.string());
            statement.setInt(2, hotspotId);

            statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public static boolean deleteChestData(Connection connection, int hotspotId) {
        try (PreparedStatement statement = connection.prepareStatement("DELETE FROM gtm_house_data WHERE hotspot_id=?;")) {
            statement.setInt(1, hotspotId);

            statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public static boolean deleteUserChest(Connection connection, int uniqueHouseId, int chestId) {
        try (PreparedStatement statement = connection.prepareStatement("DELETE FROM gtm_house_chest WHERE house_id=? AND chest_id=?;")) {
            statement.setInt(1, uniqueHouseId);
            statement.setInt(2, chestId);

            statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public static boolean deleteAllChestData(Connection connection, int uniqueHouseId) {
        try (PreparedStatement statement = connection.prepareStatement("DELETE FROM gtm_house_data WHERE house_id=? AND hotspot_type=?;")) {
            statement.setInt(1, uniqueHouseId);
            statement.setString(2, Core.name().toUpperCase());

            statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public static boolean deleteAllUserChest(Connection connection, int uniqueHouseId) {
        try (PreparedStatement statement = connection.prepareStatement("DELETE FROM gtm_house_chest WHERE house_id=?;")) {
            statement.setInt(1, uniqueHouseId);

            statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public static boolean deleteAllOwners(Connection connection, int uniqueHouseId) {
        try (PreparedStatement statement = connection.prepareStatement("DELETE FROM gtm_house_user WHERE house_id=?;")) {
            statement.setInt(1, uniqueHouseId);

            statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public static boolean deleteOwner(Connection connection, int uniqueHouseId, UUID uuid) {
        try (PreparedStatement statement = connection.prepareStatement("DELETE FROM gtm_house_user WHERE uuid=UNHEX(?) AND house_id=?;")) {
            statement.setString(1, uuid.toString().replaceAll("-", ""));
            statement.setInt(2, uniqueHouseId);

            statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public static boolean setPrice(Connection connection, int uniqueHouseId, int price) {
        try (PreparedStatement statement = connection.prepareStatement("UPDATE gtm_house SET price=? WHERE id=?;")) {
            statement.setInt(1, price);
            statement.setInt(2, uniqueHouseId);

            statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public static boolean removeDoor(Connection connection, int uniqueHouseId, int hotspotId) {
        try (PreparedStatement statement = connection.prepareStatement("DELETE FROM gtm_house_data WHERE hotspot_id=? AND house_id=?;")) {
            statement.setInt(1, hotspotId);
            statement.setInt(2, uniqueHouseId);

            statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public static boolean removeAllDoors(Connection connection, int uniqueHouseId) {
        try (PreparedStatement statement = connection.prepareStatement("DELETE FROM gtm_house_data WHERE house_id=? AND hotspot_type=?;")) {
            statement.setInt(1, uniqueHouseId);
            statement.setString(2, "DOOR");

            statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public static boolean addChestContent(Connection connection, int uniqueHouseId, int chestId, UUID uuid, ItemStack[] content) {
        try (PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO gtm_house_chest (house_id, uuid, chest_id, content) VALUES (?, UNHEX(?), ?, ?) ON DUPLICATE KEY UPDATE content=?;")) {
            statement.setInt(1, uniqueHouseId);
            statement.setString(2, uuid.toString().replaceAll("-", ""));
            statement.setInt(3, chestId);
            statement.setString(4, GTMUtils.toBase64(content));
            statement.setString(5, GTMUtils.toBase64(content));

            statement.execute();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean updateChestContent(Connection connection, int uniqueHouseId, int chestId, UUID uuid, ItemStack[] content) {
        try (PreparedStatement statement = connection.prepareStatement(
                "UPDATE gtm_house_chest SET content=? WHERE house_id=? AND uuid=UNHEX(?) AND chest_id=?")) {
            statement.setString(1, GTMUtils.toBase64(content));
            statement.setInt(2, uniqueHouseId);
            statement.setString(3, uuid.toString().replaceAll("-", ""));
            statement.setInt(4, chestId);

            statement.executeUpdate();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean getChestContent(Connection connection, HouseUser houseUser, UUID uuid) {
        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT * FROM gtm_house_chest WHERE uuid=UNHEX(?);")) {
            statement.setString(1, uuid.toString().replaceAll("-", ""));

            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    int id = result.getInt("house_id");
                    int chestId = result.getInt("chest_id");
                    UserHouse userHouse = houseUser.getUserHouseByUniqueId(id);
                    if (userHouse == null) continue;

                    UserHouseChest chest = userHouse.getChestOrNull(chestId);
                    if (chest == null) {
                        userHouse.addChest(new UserHouseChest(id, chestId, GTMUtils.fromBase64(result.getString("content"))));
                    } else {
                        chest.setContents(GTMUtils.fromBase64(result.getString("content")));
                    }
                }
            }
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static Optional<List<UserHouse>> loadUser(Connection connection, HousesManager housesManager, UUID uuid) {
        List<UserHouse> list = Lists.newArrayList();

        try (PreparedStatement statement = connection.prepareStatement("SELECT HU.house_id, H.house_num FROM gtm_house_user HU, gtm_house H WHERE HU.uuid=UNHEX(?) AND HU.house_id=H.id AND server_key=? AND H.premium=?;")) {
            statement.setString(1, uuid.toString().replaceAll("-", ""));
            statement.setString(2, Core.name().toUpperCase());
            statement.setBoolean(3, false);

            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    int uniqueId = result.getInt("house_id");
                    int number = result.getInt("house_num");

                    System.out.println("Debug 1");
                    House house = housesManager.getHouse(number);
                    if (house == null) continue;

                    System.out.println("Debug 2");
                    list.add(new UserHouse(uuid, uniqueId, house.getId()));
                }
                return Optional.of(list);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public static void addUser(Connection connection, int uniqueHouseId, UUID uuid) {
        List<UserHouse> list = Lists.newArrayList();

        try (PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO gtm_house_user (house_id, uuid, is_owner) VALUES (?, UNHEX(?), ?);")) {
            statement.setInt(1, uniqueHouseId);
            statement.setString(2, uuid.toString().replaceAll("-", ""));
            statement.setBoolean(3, true);

            statement.execute();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Sets the record for the house data as valid or invalid depending on the arguments.
     * <p>
     * This is used to see which records are getting loaded correctly, and which aren't.
     * </p>
     * 
     * @param conn - the database connection thread
     * @param uniqueHouseId - the unique id for the house, generated by the db
     * @param hotspotId - the unique id for the hotspot, generated by the db
     * @param isValid - {@code true} if this is valid data that was loaded, {@code false} for invalid
     * 
     * @return {@code true} if the query ran, {@code false} otherwise.
     */
    public static boolean setValidData(Connection conn, int uniqueHouseId, int hotspotId, boolean isValid){
    	
    	String query = "UPDATE gtm_house_data SET is_valid=? WHERE house_id=? AND hotspot_id=?;";
    	
    	try (PreparedStatement ps = conn.prepareStatement(query)){
    		ps.setBoolean(1, isValid);
    		ps.setInt(2, uniqueHouseId);
    		ps.setInt(3, hotspotId);
    		
    		ps.executeUpdate();
    		return true;
    	}
    	catch (SQLException e){
    		e.printStackTrace();
    	}
    	
    	return false;
    }
    
    /**
     * Resets all house data in the database as valid, in order to reset to not valid if it's still broken.
     * <p>
     * This is needed so during every server start up we can see if houses have been fixed.
     * </p>
     * 
     * @param conn - the database connection thread
     * 
     * @return {@code true} if the query ran, {@code false} otherwise.
     */
    public static boolean resetValidData(Connection conn){
    	
    	String query = "UPDATE gtm_house_data SET is_valid=1 WHERE house_id IN (SELECT id FROM gtm_house WHERE server_key=?)";
    	try (PreparedStatement ps = conn.prepareStatement(query)){
    		ps.setString(1, Core.name().toUpperCase());
    		ps.executeUpdate();
    		return true;
    	}
    	catch(SQLException exc){
    		exc.printStackTrace();
    	}
    	
    	return false;
    }
}
