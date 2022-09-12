package net.grandtheftmc.vice.machine;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import net.grandtheftmc.core.database.BaseDatabase;
import net.grandtheftmc.core.util.Component;
import net.grandtheftmc.core.util.ServerUtil;
import net.grandtheftmc.vice.Vice;
import net.grandtheftmc.vice.dao.MachineDAO;
import net.grandtheftmc.vice.machine.recipe.MachineRecipeManager;
import net.grandtheftmc.vice.machine.recipe.menu.RecipeMenuManager;
import net.grandtheftmc.vice.machine.type.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

public class MachineManager implements Component<MachineComponent, Vice> {

    private final HashSet<BaseMachine> statues;
    private final HashMap<Location, BaseMachine> machines;

    private final MachineComponent component;

    public MachineManager(JavaPlugin plugin) {
        this.statues = Sets.newHashSet();
        this.machines = Maps.newHashMap();

        this.statues.addAll(Arrays.asList(
                new MachineSmallDryingChamber(),
                new MachineMediumDryingChamber(),
                new MachineLargeDryingMachine(),
                new MachineBeerDistillery(),
                new MachineVodkaDistillery(),
                new MachineCocaProcessor(),
                new MachinePulpCondenser(),
                new MachineBasicMethProducer(),
                new MachineAdvancedMethProducer(),
                new MachineSugarBox()
        ));

        new MachineCommand(this);
        new RecipeMenuManager(plugin);
        MachineRecipeManager recipeManager = new MachineRecipeManager(this);
        Bukkit.getPluginManager().registerEvents(this.component = new MachineComponent(this, recipeManager), plugin);

        ServerUtil.runTaskAsync(() -> {
            try (Connection connection = BaseDatabase.getInstance().getConnection()) {
                long start = System.currentTimeMillis();
                for (BaseMachine machine : MachineDAO.getMachines(connection)) {
                    this.machines.put(machine.getLocation(), machine);
                    machine.setEnabled(false);
                }
                System.out.println("Machines loaded(" + this.machines.size() + "), took " + (System.currentTimeMillis() - start) + "ms.");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });

        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, new MachineTask(this, recipeManager), 10, 1);
    }

    @Override
    public MachineComponent onDisable(Vice plugin) {
        this.component.onDisable(plugin);
        return this.component;
    }

    public Optional<BaseMachine> constructByItem(ItemStack itemStack) {
        Optional<BaseMachine> optional = this.statues.stream().filter(m -> itemStack.isSimilar(m.getMachineItem())).findFirst();
        if (!optional.isPresent()) return Optional.empty();

        switch (optional.get().getMachineIdentifier()) {
            case 1: return Optional.of(new MachineSmallDryingChamber());
            case 2: return Optional.of(new MachineMediumDryingChamber());
            case 3: return Optional.of(new MachineLargeDryingMachine());
            case 4: return Optional.of(new MachineBeerDistillery());
            case 5: return Optional.of(new MachineVodkaDistillery());
            case 6: return Optional.of(new MachineCocaProcessor());
            case 7: return Optional.of(new MachinePulpCondenser());
            case 8: return Optional.of(new MachineBasicMethProducer());
            case 9: return Optional.of(new MachineAdvancedMethProducer());
            case 10: return Optional.of(new MachineSugarBox());
        }

        return Optional.empty();
    }

    public ItemStack getMachineItemById(int id) {
        Optional<BaseMachine> optional = this.statues.stream().filter(m -> m.getMachineIdentifier() == id).findFirst();
        if (!optional.isPresent()) return new ItemStack(Material.STONE);

        return optional.get().getMachineItem().clone();
    }

    public Optional<BaseMachine> getMachineById(int uniqueId) {
        return this.getMachines().stream().filter(m -> {
//            ServerUtil.debug(m.getUniqueIdentifier() + " -");
//            ServerUtil.debug(uniqueId + " --");
            return m.getUniqueIdentifier() == uniqueId;
        }).findFirst();
    }

    public boolean isType(Material material) {
        return this.statues.stream().anyMatch(m -> m.getMaterial() == material);
    }

    public void removeMachine(BaseMachine machine) {
        if(this.machines.containsKey(machine.getLocation()))
            this.machines.remove(machine.getLocation());
    }

    public Collection<BaseMachine> getMachines() {
        return this.machines.values().stream().filter(BaseMachine::isEnabled).collect(Collectors.toList());
    }

    public Collection<BaseMachine> getAllMachines() {
        return this.machines.values();
    }

    public void addMachine(BaseMachine machine) {
        this.machines.put(machine.getLocation(), machine);
    }

    public Optional<BaseMachine> getMachineByLocation(Location location) {
//        return this.machines.stream().filter(m -> m.getLocation() != null && this.isLoc(m.getLocation(), location)).findFirst();
        return this.machines.containsKey(location) ? Optional.of(this.machines.get(location)) : Optional.empty();
    }

//    public Optional<BaseMachine> isMachine(Inventory inventory) {
//        return this.machines.stream().filter(m -> m.getInventory().equals(inventory)).findFirst();
//    }

    private boolean isLoc(Location loc1, Location loc2) {
        return loc1.getBlockX() == loc2.getBlockX() && loc1.getBlockY() == loc2.getBlockY() && loc1.getBlockZ() == loc2.getBlockZ();
    }

    public HashSet<BaseMachine> getStatues() {
        return this.statues;
    }
}
