package net.grandtheftmc.vice.machine;

import de.slikey.effectlib.util.ParticleEffect;
import net.grandtheftmc.core.util.Callback;
import net.grandtheftmc.core.util.ServerUtil;
import net.grandtheftmc.vice.machine.data.MachineData;
import net.grandtheftmc.vice.machine.data.MachineDataType;
import net.grandtheftmc.vice.machine.recipe.MachineRecipeManager;
import net.grandtheftmc.vice.machine.recipe.misc.MachineRecipeData;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

public final class MachineTask implements Runnable {

    private final MachineManager machineManager;
    private final MachineRecipeManager recipeManager;
    private final Random random;

    public MachineTask(MachineManager machineManager, MachineRecipeManager recipeManager) {
        this.machineManager = machineManager;
        this.recipeManager = recipeManager;
        this.random = new Random();
    }

    /**
     * When an object implementing interface <code>Runnable</code> is used
     * to create a thread, starting the thread causes the object's
     * <code>run</code> method to be called in that separately executing
     * thread.
     * <p>
     * The general contract of the method <code>run</code> is that it may
     * take any action whatsoever.
     *
     * @see Thread#run()
     */
    @Override
    public void run() {
        for (BaseMachine machine : this.machineManager.getMachines()) {
            if (!machine.isEnabled()) continue;

            Location loc = machine.getLocation().clone();
            MachineData data = machine.getData(MachineDataType.DURABILITY);

            if(data !=null)
                if(data.getCurrent()==0) {
                    ParticleEffect.VILLAGER_ANGRY.display(0.3f, 0f, 0.3f, 1, 0, loc.clone().add(0.5, .5, 0.5), 50d);//durability is 0
                    continue;
                }

            if (!machine.getData(MachineDataType.FUEL).isFull()) {
                ServerUtil.runTask(() -> {
                    if (!refillFuel(machine) && machine.getData(MachineDataType.FUEL).getCurrent() == 0) {
                        ParticleEffect.REDSTONE.display(255f / 255f, 0f, 0f, 1, 0, loc.clone().add(0.5, 1.1, 0.5), 50d);//no fuel
                    }
                });
            }

            ServerUtil.runTask(() -> {
                machine.updateFuel();
                machine.updateDurability();
            });

            if (machine.getData(MachineDataType.FUEL).isEmpty()) continue;

            if (!machine.isRecipeActive()) {
                ServerUtil.runTask(() -> {
                    if (!this.recipeManager.tryInitRecipe(machine)) {
                        ParticleEffect.REDSTONE.display(255f / 255f, 102f / 255f, 0f, 1, 0, loc.clone().add(0.5, 1.1, 0.5), 50d);//recipe isnt active
                    }
                });
                continue;
            }
            else
                ParticleEffect.REDSTONE.display(102f/255f,255f/255f,102f/255f, 1,0, loc.clone().add(0.5, 1.1, 0.5), 50d);

            MachineRecipeData recipeData = machine.getRecipeData();
            if (recipeData.next()) {
                ServerUtil.runTask(machine::updateProgress);
            }
        }
    }

    public boolean refillFuel(BaseMachine machine) {
        for (int i : machine.getData(MachineDataType.FUEL).getSlots()) {
            ItemStack item = machine.getInventory().getItem(i);
            if (item == null || item.getType() == Material.AIR) return false;

            MachineData data = machine.getData(MachineDataType.FUEL);
            if (data == null) return false;

            if(data.getCurrent()+MachineUtil.getFuelByType(item.getType())>data.getMax()) return false;

            if (item.getAmount() <= 1) machine.getInventory().setItem(i, new ItemStack(Material.AIR));
            else item.setAmount(item.getAmount() - 1);

            data.add(MachineUtil.getFuelByType(item.getType()));//TODO: CHANGE TO ACTUAL AMOUNT
        }

        return true;
    }

    private void doStuff(BaseMachine machine, int i, boolean visual) {
        boolean x = this.random.nextBoolean();
        if (!x) return;

        MachineData data;
        if (i == 0) {

            //Another random because why not.. lmao
            if (this.random.nextBoolean()) return;

            data = machine.getData(MachineDataType.FUEL);
            if (data == null) return;

            data.take(1);
            machine.updateFuel();
        }
        else if (i == 1) {
            data = machine.getData(MachineDataType.DURABILITY);
            if (data == null) return;

            data.take(2);
            machine.updateDurability();
        }
        else if (i == 2) {
            data = machine.getData(MachineDataType.PROGRESS);
            if (data == null) return;

            data.add(1);
            machine.updateProgress();
        }
    }

    public void canRun(Location location, Callback<Boolean> callback) {
        ServerUtil.runTask(() -> {
            callback.call(location.getChunk().isLoaded());
        });
    }
}
