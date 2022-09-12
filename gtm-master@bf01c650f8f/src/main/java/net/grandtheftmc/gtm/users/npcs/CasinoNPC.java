package net.grandtheftmc.gtm.users.npcs;

import net.citizensnpcs.api.event.NPCLeftClickEvent;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.grandtheftmc.core.casino.coins.VendorMenu;
import net.grandtheftmc.core.npc.CoreNPC;
import net.grandtheftmc.core.npc.interfaces.ClickableNPC;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

/**
 * Created by Timothy Lampen on 3/23/2018.
 */
public class CasinoNPC extends CoreNPC implements ClickableNPC {
    public CasinoNPC(Location loc) {
        super(loc, EntityType.PLAYER, "&eMr. Goldman", "&7&oGet you're casino chips here!");
    }

    @Override
    protected void generateNewNPC() {
        setLookClose(true);
        setSkin("eyJ0aW1lc3RhbXAiOjE1MjE4NTQyNjM3OTgsInByb2ZpbGVJZCI6IjkxOGEwMjk1NTlkZDRjZTZiMTZmN2E1ZDUzZWZiNDEyIiwicHJvZmlsZU5hbWUiOiJCZWV2ZWxvcGVyIiwic2lnbmF0dXJlUmVxdWlyZWQiOnRydWUsInRleHR1cmVzIjp7IlNLSU4iOnsidXJsIjoiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS9mYTRjNThlNTFjZDY1MjJhMTdlNWY3MGE5YzUzMDJkMTVjNzU4MThhNzY1NGNiNGM4ZTM2Zjk5Njk2NTg5OTkifX19", "EwjF5VIfbHDAaAp+UEE9ctxqoD3IpyboYOMBIMtGZWikS4FAa5Ju56Ip5cuCW9vZj2Oo4ZvGrK6Jzm0BkMz+uXlzNYjUVOFgfatVa2Ibr+CGn4KdbMFAXb0vK4moVWVurapYeXkDs9wih5evf3CI/9g2deoyRNul/iGdG8hbIUq910Cc2m0JHFMeRVyRivnyw7l7SWhcL54m/+goiRtklpYnKcqiQSaHhfi5rPBNldvRF0T6f1raJzTrh3hGriiRRz2et5zP8ZBphWGUzij07kUZLuVSXm9FMltQGrOzFrePejSrGE9tjIefHphVqScM0+tqbCyC7b7TtZ0aYCFpv2DLDlYt/wCrbyxpi7l1hDvB4xK3aLfm8HRD4HK8lgpiqcD17WNCg0P2+m43jbuIIMK/AWDr74LAL0mblp66vqaR3fr2ifp7bbgXXiV7hoykkJ5B+ozxGV73J4u58fEPgZRGVUQaALeRyQsNLaeLGgkvsu3FFnqvHf0CBA9tEjElHt+7XleHoKkvVKmaVTkq/APiItowHbDDgeWGcAHi+6jPnfz5d7k0P/KcePYCCcRMPK+1/UP/StXJ80Dj+tFyiUVg82nLiLvq0Uv2thrqZQvNxL0YvkFuRjYIKspu4jXJvCfEPNVjczdmYNFxtO56P9oEPL+6byvzPPbXW/Q4L0Q=");
    }

    @Override
    public void onRightClick(NPCRightClickEvent event) {
        Player player = event.getClicker();
        new VendorMenu().openInventory(player);
    }

    @Override
    public void onLeftClick(NPCLeftClickEvent npcLeftClickEvent) {

    }
}
