package net.grandtheftmc.vice.dao;

import com.google.common.collect.Lists;
import net.grandtheftmc.core.Core;
import net.grandtheftmc.vice.ViceUtils;
import net.grandtheftmc.vice.machine.BaseMachine;
import net.grandtheftmc.vice.machine.data.MachineDataType;
import net.grandtheftmc.vice.machine.type.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import java.sql.*;
import java.util.List;
import java.util.Optional;

public class MachineDAO {

//CREATE TABLE IF NOT EXISTS vice_machine (
//id INT NOT NULL AUTO_INCREMENT,
//server_key VARCHAR(10) NOT NULL,
//type INT NOT NULL,
//world VARCHAR(8) NOT NULL,
//x INT NOT NULL,
//y INT NOT NULL,
//z INT NOT NULL,
//PRIMARY KEY (id)
//);

//CREATE TABLE IF NOT EXISTS vice_machine_data (
//machine_id INT NOT NULL,
//fuel DOUBLE NOT NULL,
//durability DOUBLE NOT NULL,
//progress DOUBLE NOT NULL,
//content BLOB,
//PRIMARY KEY (machine_id),
//FOREIGN KEY (machine_id) REFERENCES vice_machine(id) ON DELETE CASCADE
//);

//INSERT INTO vice_machine (server_key, type, world, x, y, z) VALUES (?, ?, ?, ?, ?, ?, ?);
//INSERT INTO vice_machine_data (machine_id, fuel, durability, progress, content) VALUES (?, ?, ?, ?, ?);

//SELECT M.id, M.type, M.world, M.x, M.y, M.z, MD.fuel, MD.durability, MD.progress, MD.content
//FROM vice_machine M, vice_machine_data MD
//WHERE M.id=MD.id AND M.server_key=?;

//DELETE FROM vice_machine WHERE id=?;

    public static List<BaseMachine> getMachines(Connection connection) {
        final String query = "SELECT M.id, M.type, M.world, M.x, M.y, M.z, MD.fuel, MD.durability, MD.progress, MD.content"
                + " FROM vice_machine M, vice_machine_data MD WHERE M.id=MD.machine_id AND M.server_key=?;";

        List<BaseMachine> list = Lists.newArrayList();

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, Core.name());
            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    int uniqueId = result.getInt("id");
                    int machineType = result.getInt("type");
                    String world = result.getString("world");
                    int x = result.getInt("x");
                    int y = result.getInt("y");
                    int z = result.getInt("z");
                    double fuel = result.getDouble("fuel");
                    double durability = result.getDouble("durability");
                    double progress = result.getDouble("progress");
                    ItemStack[] content = ViceUtils.fromBase64(result.getString("content"));

                    BaseMachine machine = null;
                    switch (machineType) {
                        case 1: machine = new MachineSmallDryingChamber(); break;
                        case 2: machine = new MachineMediumDryingChamber(); break;
                        case 3: machine = new MachineLargeDryingMachine(); break;
                        case 4: machine = new MachineBeerDistillery(); break;
                        case 5: machine = new MachineVodkaDistillery(); break;
                        case 6: machine = new MachineCocaProcessor(); break;
                        case 7: machine = new MachinePulpCondenser(); break;
                        case 8: machine = new MachineBasicMethProducer(); break;
                        case 9: machine = new MachineAdvancedMethProducer(); break;
                        case 10: machine = new MachineSugarBox(); break;
                    }

                    if (machine == null) continue;

                    machine.setUniqueIdentifier(uniqueId);
                    machine.setLocation(new Location(Bukkit.getWorld(world), x, y, z));
                    machine.setContents(content);

                    machine.getData(MachineDataType.FUEL).setCurrent(fuel);
                    machine.getData(MachineDataType.DURABILITY).setCurrent(durability);
                    machine.getData(MachineDataType.PROGRESS).setCurrent(progress);

                    list.add(machine);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    public static <T extends BaseMachine> T addMachine(Connection connection, T machine) {
        final String query = "INSERT INTO vice_machine (server_key, type, world, x, y, z) VALUES (?, ?, ?, ?, ?, ?);";
        try (PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, Core.name());
            statement.setInt(2, machine.getMachineIdentifier());
            statement.setString(3, machine.getLocation().getWorld().getName());
            statement.setInt(4, machine.getLocation().getBlockX());
            statement.setInt(5, machine.getLocation().getBlockY());
            statement.setInt(6, machine.getLocation().getBlockZ());

            statement.executeUpdate();
            try (ResultSet result = statement.getGeneratedKeys()) {
                if (result.next()) {
                    machine.setUniqueIdentifier(result.getInt(1));
                    return machine;
                }

                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void updateMachineData(Connection connection, BaseMachine machine) {
        final String query = "INSERT INTO vice_machine_data (machine_id, fuel, durability, progress, content) VALUES (?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE fuel=?, durability=?, progress=?, content=?;";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, machine.getUniqueIdentifier());
            statement.setDouble(2, machine.getData(MachineDataType.FUEL).getCurrent());
            statement.setDouble(3, machine.getData(MachineDataType.DURABILITY).getCurrent());
            statement.setDouble(4, machine.getData(MachineDataType.PROGRESS).getCurrent());
            statement.setString(5, ViceUtils.toBase64(machine.getContents()));
            statement.setDouble(6, machine.getData(MachineDataType.FUEL).getCurrent());
            statement.setDouble(7, machine.getData(MachineDataType.DURABILITY).getCurrent());
            statement.setDouble(8, machine.getData(MachineDataType.PROGRESS).getCurrent());
            statement.setString(9, ViceUtils.toBase64(machine.getContents()));

            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void removeMachine(Connection connection, int uniqueIdentifier) {
        final String query = "DELETE FROM vice_machine WHERE id=?;";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, uniqueIdentifier);
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
