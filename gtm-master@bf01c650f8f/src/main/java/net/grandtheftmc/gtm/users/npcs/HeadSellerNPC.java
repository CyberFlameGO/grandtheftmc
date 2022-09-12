package net.grandtheftmc.gtm.users.npcs;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import net.citizensnpcs.api.event.NPCCollisionEvent;
import net.citizensnpcs.api.event.NPCLeftClickEvent;
import net.citizensnpcs.api.event.NPCPushEvent;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.grandtheftmc.core.menus.MenuManager;
import net.grandtheftmc.core.npc.CoreNPC;
import net.grandtheftmc.core.npc.interfaces.ClickableNPC;
import net.grandtheftmc.core.npc.interfaces.CollideableNPC;

/**
 * Created by Timothy Lampen on 1/14/2018.
 */
public class HeadSellerNPC extends CoreNPC implements ClickableNPC, CollideableNPC {
    public HeadSellerNPC(Location loc) {
        super(loc, EntityType.PLAYER, "&a&lHead Seller", "&7&oSell your heads here!");
    }

    @Override
    protected void generateNewNPC() {
        setLookClose(true);
        setSkin("eyJ0aW1lc3RhbXAiOjE1MTU5ODIyMzkzMTUsInByb2ZpbGVJZCI6IjcwOTU2NDU0NTJkOTRiYTI5YzcwZDFmYTY3YjhkYTQyIiwicHJvZmlsZU5hbWUiOiJIaWRkdXMiLCJzaWduYXR1cmVSZXF1aXJlZCI6dHJ1ZSwidGV4dHVyZXMiOnsiU0tJTiI6eyJ1cmwiOiJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzNmZjM3ZjdiYTBlYzlmMDczYWJhM2VlYTk0OGI3NDMzYzg3YmVmZWNkNzNmYjdjOWQyNjExYWZhMmVlNzY2In19fQ==", "d1UU+O/OvjxxspCnb7gRPXVW0RxpfpcG2ygVWiCZeBC9Mz5ztKRgsska9jqk3bOlupJR+ApefO88DgWrhHIsT0SZyG5e+kqg1r0pYWvE2LpYKmFyL4kgWs9om+KKXP1NGHXVJT9yJ+SdCHwAsqjosuLpg0IGs6e9V+2Tw9Lw3Fhq7GvQuufZjnvpWOKXsMnMRzil8bX3AlLkAvRnYXwOAmWeK/L6BGtT7olb+ewOjZwoKIbLH13kXJJ6BTEoOoFLlczTU+bCYx/Lc5tEkTqJoiTZtxV6oWZaLK7Q8YxdOsegP1yo7ZLGyH6IuKPPmtTVOC9C09PqBA9sWkBJUIKP5iA+gfNnPxkApo4ooY/gMEenzaPihHPt+USvpLvgvB/GtNkjSmtSXuYygyRAg6ukejM9PC+3waecH0rGtYuNZ2gKzdAboPMIlL7Z066A3b7+xrqbicYVtmKRrtdJZ3nuztNcFFhD6sBkl2fY6i2Xf8bYbE45LiNSrKB8a+DrgFRCuhlgtagesFALik5a9zBSSVIARMFlDgZqrJimQIbsTwprIAuqoUN2MDMTlmKIFzDheOZHyo8IGp+JyXztttzWz35RfPP68stJWJ0+2t5X2HFw6ERBTLpXeg/2RZ/CgHWNKJxBhOGKZONFZUOwPGy6BDQq9TX+ZQG6T3Eyhbg0zFM=");
        setCollideable(true);
    }

    @Override
    public void onRightClick(NPCRightClickEvent event) {
        Player player = event.getClicker();
        //MenuManager.openMenu(player, "heads");
        ItemStack item = player.getInventory().getItemInMainHand();
        MenuManager.openMenu(player, item != null && item.getType() == Material.SKULL_ITEM ? "auctionhead" : "heads");
    }

    @Override
    public void onLeftClick(NPCLeftClickEvent event) {

    }

    @Override
    public void onCollide(NPCCollisionEvent event) {
        
    }

    @Override
    public void onPush(NPCPushEvent event) {
        
    }
}