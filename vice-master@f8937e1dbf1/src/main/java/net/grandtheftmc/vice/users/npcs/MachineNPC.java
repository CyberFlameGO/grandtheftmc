package net.grandtheftmc.vice.users.npcs;

import net.citizensnpcs.api.event.NPCLeftClickEvent;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.grandtheftmc.core.Utils;
import net.grandtheftmc.core.npc.CoreNPC;
import net.grandtheftmc.core.npc.interfaces.ClickableNPC;
import net.grandtheftmc.vice.machine.MachineManager;
import net.grandtheftmc.vice.machine.repair.MachineRepairMenu;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;

/**
 * Created by Timothy Lampen on 2/12/2018.
 */
public class MachineNPC extends CoreNPC implements ClickableNPC {

    private final MachineManager machineManager;

    public MachineNPC(MachineManager machineManager, Location loc) {
        super(loc, EntityType.PLAYER, Utils.f("&6&lHenry Ford"), Utils.f("&7&oTrade your machine shards here!"));
        this.machineManager = machineManager;
    }

    @Override
    protected void generateNewNPC() {
        setSkin(
                "eyJ0aW1lc3RhbXAiOjE1MTg1MjA2MjU2MTksInByb2ZpbGVJZCI6IjAyMjEyMmM5Yjk5ZjRhNmY4MTNkNzVkNmMwMTk5NWU2IiwicHJvZmlsZU5hbWUiOiJNZWNoYW5pYyIsInNpZ25hdHVyZVJlcXVpcmVkIjp0cnVlLCJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTVkNzY5ZDEyZWUyN2Y1ZDM4ZGQ2ZjNjMGI0Mjg2NjU2NDU4Njg3NGI3MWFhMjczNTIzZDBhYWM1ZjM3ZSJ9fX0=",
                "BFTXOx0chlORiL9UHDrnkQDsU9yFZDriJ18Si52h1vyDnsKwWskAFvp5SnUtQ1gBUebE+vz//CAJWetCdONIKbTU8MDpGS524YI6C6F1lV6bHbvb1MAEX3ezEDpinB7G/gNRQRddBYuuYd0iXqkYbiLrYzGs3O2KTQ+HEz8M2detE6B9JkFwBk797DJ6A7RJyMsJk7NRgTKM2i9cBZhlOklwobQDUH4tQ9zzVsrbKxXBB9rbUKZxIWPMOk9CfPIgZHu14N/rbYfQ9KFRd2BhlSWuFRxXLh3htGZHESsifzbZg0TkKtMpKAOTX7mYbxS8/KUhoQH3Q816CP6EI93B2gvlq7fVNCiyi19ad4A0NkCMBoiv1KBqB4J91CVMoomGalaWxVt+3JJ/IE/No9/mfWhGA5EKs3uHYJD7x6OmsYxbahnV+GyaiXQFJz3x/ObGr5evLBXt5JgQP2DQX/7+2HU7dNh8J6ZwWtEg+Yh33y46Oh6A60ixqpFXN1pwHkXgplS/4HVsliCFbwhTDqBS9sX8pYlj+MEdmPWvYrVk0KsNqEYLawR/8ukTosBRZgWPo2HDHXQ+/IQ7AfKhC/NH0pnTMQrFYTV8p+kqKJCO5Tcu/GefsA7miVAktYFCTGxM8YdZDYFCpf1wC3pJRnHYbjzaHpny839Mh2t7kOHVCCQ="
        );
        setLookClose(true);
    }

    @Override
    public void onRightClick(NPCRightClickEvent npcRightClickEvent) {
        new MachineRepairMenu(this.machineManager).openInventory(npcRightClickEvent.getClicker());
    }

    @Override
    public void onLeftClick(NPCLeftClickEvent npcLeftClickEvent) {
        new MachineRepairMenu(this.machineManager).openInventory(npcLeftClickEvent.getClicker());
    }
}
