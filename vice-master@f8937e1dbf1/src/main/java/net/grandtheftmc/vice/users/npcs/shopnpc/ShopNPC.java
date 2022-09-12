package net.grandtheftmc.vice.users.npcs.shopnpc;

import net.citizensnpcs.api.event.NPCLeftClickEvent;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.grandtheftmc.core.npc.CoreNPC;
import net.grandtheftmc.core.npc.interfaces.ClickableNPC;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

/**
 * Created by Timothy Lampen on 1/24/2018.
 */
public class ShopNPC extends CoreNPC implements ClickableNPC {

    /**
     * @apiNote the constructor for the actual npc.
     */
    public ShopNPC(Location loc) {
        super(loc, EntityType.PLAYER, "&9&lGary McNaggins", "&7&oBuy the best guns and ammo here!");
    }

    @Override
    protected void generateNewNPC() {
        setLookClose(true);
        setSkin("eyJ0aW1lc3RhbXAiOjE1MDc2MTM5NzI2NDUsInByb2ZpbGVJZCI6ImUzYjQ0NWM4NDdmNTQ4ZmI4YzhmYTNmMWY3ZWZiYThlIiwicHJvZmlsZU5hbWUiOiJNaW5pRGlnZ2VyVGVzdCIsInNpZ25hdHVyZVJlcXVpcmVkIjp0cnVlLCJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZGUyYjUwYzI4NDQ1YmQyOWE2OGRmZTIyM2M0YzlmZDYyZTYyOWNkNWU1ZDdjMTc3NzIwNmU4YjRkNjMxIn19fQ==", "gza3eHED3BMmxRjZmDDUQQliH10Q4e8U5uKNv0RaGfOKdPOxMToH2rqSpyNeS+odXQvAq6cDZulKk5LZgcs89kpv+Jkb3sfWdUb6HnJvqkeA+4iTw3n9BxRZpoC0lyBmiJSQPlSwywgjmd9cybGPtgX3+WpbExRDYy90X8ii3iN9dlFlNWFiInNZjBUUjslcqnD8VEkItonJwNbPbXkgvHu0qmiBon6bWmnI81cO0DekrxOGAbQQynNosnGVbV7oGTAtN87G9zM7McNvMXK+1BJqAxdqad3U2Jfnu3PHDZ1pDCJIA+5yQiiTblQPzYx9Fp73E2NpS51239/P5B0bWOa8MWGK2fKCznxRy/lZTd/3Ewojxu9guWann0ALLeYyvXA/FDY1vY6clRF50JyhgBR6Tf58lOF8kkq964gdpYlhtldI1ZWf8jn/inK//b3rNmqu046oKQLuhYjxVNoV4lrzzb+pzjjKx2/iBXqzxnWTjrTLZv6n6jLS9aFghryaLbUXc4IETj+MsZ5Z9WdPCG02V3f3Z+5aFZfMg2zkj1qQxDVhrdJr/87lE23ZupYDV1szocx39JF1gtwbKhTugVKlDV4UQZHokFdcFRtMLSpX7zJwNLiVK/+aMN1YbGQzdwII9CFXN2DtgawzTnQQafEBwNiyp3GAcPTE9VqffFY=");
    }

    @Override
    public void onRightClick(NPCRightClickEvent event) {
        Player player = event.getClicker();
        new CategoryMenu().openInventory(player);
    }

    @Override
    public void onLeftClick(NPCLeftClickEvent event) {
        Player player = event.getClicker();
        new CategoryMenu().openInventory(player);
    }
}
