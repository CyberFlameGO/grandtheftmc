package net.grandtheftmc.vice.users.npcs;

import net.citizensnpcs.api.event.NPCLeftClickEvent;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.grandtheftmc.core.Utils;
import net.grandtheftmc.core.inventory.CoreMenu;
import net.grandtheftmc.core.inventory.button.ClickableItem;
import net.grandtheftmc.core.inventory.button.MenuItem;
import net.grandtheftmc.core.menus.MenuManager;
import net.grandtheftmc.core.npc.CoreNPC;
import net.grandtheftmc.core.npc.interfaces.ClickableNPC;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Timothy Lampen on 1/24/2018.
 */
public class TaxiNPC extends CoreNPC implements ClickableNPC {
    public TaxiNPC(Location loc) {
        super(loc, EntityType.PLAYER,  "&e&lGerald Hackney", "&7&oGo to the game world!");
    }

    @Override
    protected void generateNewNPC() {
        setSkin("eyJ0aW1lc3RhbXAiOjE1MTU5NzAzNjYzNDUsInByb2ZpbGVJZCI6ImFkMWM2Yjk1YTA5ODRmNTE4MWJhOTgyMzY0OTllM2JkIiwicHJvZmlsZU5hbWUiOiJGdXJrYW5iejAwIiwic2lnbmF0dXJlUmVxdWlyZWQiOnRydWUsInRleHR1cmVzIjp7IlNLSU4iOnsidXJsIjoiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS9jYzUzZWQyMzJlYWQyNmU4Y2I0NzJiMGFmOGIyMDQyYjdhZjljZDMyOGRlM2M0YTZlNGQzNjNiNDNiZDlmNDUifX19", "hLKOkCuejv8K7bwWS/SKY6jrxJXNojg4eiv1/TPkKJ/1Hc+njUE99IPLCuexppJASlUEMe2815FfDwS4PTzMzgxBeLPdcB/xJR8BZw4FuvodIXfLubmVkxme245u0hRHfVlPLk31l4dyPFEwRMhbSmHacVroB8pebEov6+164p3fUnctqlM48bf6lNfpsbhY46nvqPVRVtv9ljTL6FwBPDvnZL97zTSsUqjKjLUJMTtuHIaAj26Q9+M9y4rP1VMInDWrgfXpEuwz32xy/2HiHHQrGMrNxU9MmshDX1BTJ4UAxmipmj+pJENRSon3GrIgLE7t/yP0Z1ZCcfHFqZtzLHKPXzt+u4jW5hl9bFUU9d9HSUEV0qt+nD68a1yNiWPE2rB0l549v+AZ5D8bktSpvdLy574/uBLLXbM8JJk8g1iFgyeEpQS8TJuHfnyV62KU6lML5+MTt7/zBXlRh2+Vz89Ti0fbZs1g6NdcJWQisCTtVPMBiws9yoitmSsqKk+8/8WYQX7EYXLGilL7gavoBZhlIyP0P8ltTc4oHfcwoOtoZvPivauUv8lHZu18tZpOE1kq28lNuBdytLTWTuJckDzeRwbB8pHCQKB628nRLt2Xp1N57CNnc9XW/3sWY+rKTRHRNw7BvVqoaCAXP4tNo+c7frELxv+CgWMxRcE2g5E=");
        setLookClose(true);
    }

    @Override
    public void onRightClick(NPCRightClickEvent event) {
        Player player = event.getClicker();
        new TaxiMenu().openInventory(player);
    }

    @Override
    public void onLeftClick(NPCLeftClickEvent event) {

    }

    private class TaxiMenu extends CoreMenu {

        public TaxiMenu() {
            super(3, Utils.f("&e&lTaxi"));

            for(int i = 0; i < 27; i++) {
                if(i==13)
                    continue;
                addItem(new MenuItem(i, new ItemStack(Material.STAINED_GLASS_PANE, 1, (short)7), false));
            }

            addItem(new ClickableItem(13, Utils.createItem(Material.SAPLING, "&d&lRandom Teleport"), (player, clickType) -> {
                player.chat("/rtp");
            }, false));
        }
    }
}
