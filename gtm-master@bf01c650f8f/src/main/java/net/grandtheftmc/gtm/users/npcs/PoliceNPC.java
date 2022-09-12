package net.grandtheftmc.gtm.users.npcs;

import net.citizensnpcs.api.event.NPCCollisionEvent;
import net.citizensnpcs.api.event.NPCLeftClickEvent;
import net.citizensnpcs.api.event.NPCPushEvent;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.menus.MenuManager;
import net.grandtheftmc.core.npc.CoreNPC;
import net.grandtheftmc.core.npc.interfaces.ClickableNPC;
import net.grandtheftmc.core.npc.interfaces.CollideableNPC;

import net.grandtheftmc.core.users.User;
import net.grandtheftmc.gtm.GTM;
import net.grandtheftmc.gtm.GTMUtils;
import net.grandtheftmc.gtm.users.JobMode;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class PoliceNPC extends CoreNPC implements ClickableNPC, CollideableNPC {

    public PoliceNPC(Location loc) {
        super(loc, EntityType.PLAYER, "&3&lCop", "&o&7Become a Cop and fight crime!");
    }

    @Override
    protected void generateNewNPC() {
        setLookClose(true);
        setSkin("eyJ0aW1lc3RhbXAiOjE1MjEyMjg2NTk5NjEsInByb2ZpbGVJZCI6IjExODAxYzEzMGNiYzQ5MGY4YmEzM2E0MTMxMTZiNzk1IiwicHJvZmlsZU5hbWUiOiIweDE5ZCIsInNpZ25hdHVyZVJlcXVpcmVkIjp0cnVlLCJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTU5MGFmOGIzNTNiZjk4MzRiMTlhMjk0ZWZhNTUzYmM3NjM5OTEzOTkzNjRlYTRlY2Y4YzBjNjNkOTNhYTU3In19fQ==",
                "LlCVPj90DakqMhenHks4mEwK2sHPYf/H3Np4F6dgtkDeUDAVmG7ZJiAewEPtjTXeLcD9RdF1NmZJoLT+FOy4ZVDYTuIMCYis8IHRUCm5zPwV6kqvHFO6ZWBpj4SQEdRE/QeaUVyBpFMHVUx4uknti4gw42pSXnBuik/xMv3Jhf8Qe80YG6CLMB+0mIjb9l1ImlT1O5BGtAlC2gk+mnk1R4sNaW4oy+C4RT7K//T/S+v7YUzjwHQf/0065Cx7EoyhfBXcldnOYdxWDqpYo8JLmq0utuR9g3CDZBwTznu4TLByDmJnFutPNVVXSU1cAYUEPXfAnm5Zkgj1WwnytAh4xG+zn0psk567Po+2ouT7zEiYEfK1jAAU3VkjmrSAgCTu2hisXAZibMzKgBubcnquIipfQin4lGQ9Kp4s51u5tOvl33zsINsTNZi+bSks9j9pN7fqYQmzu3DWIBKKxFwW+LaMY5uGlzaeaYSmSlFEpcreMDsbxTKnJ7oNWOGOwmX6vwJw74is1MmI54g3y6HxCsBCGXvg6LhEjKF7lgwgvV1PLP0B0AgFZkc8H+mF/uU9fNaE95TC0zq7KetNE8/93tnF7G2UyJv6IdM/ZYDEMP1Bi/41T6FTIFRuW2tviri2JwyAF7QnoXmB39M2n8lzqWhFYnfb5w/yQQEbp1v1AWc=");
    }

    @Override
    public void onRightClick(NPCRightClickEvent event) {
        Player player = event.getClicker();

        User user = Core.getUserManager().getLoadedUser(player.getUniqueId());
        if (Core.getSettings().isUseEditMode() && user.hasEditMode())
            return;

        JobMode mode = JobMode.getModeOrNull("COP");
        if (mode == null) return;
        GTMUtils.chooseJobMode(player, user, GTM.getUserManager().getLoadedUser(player.getUniqueId()), mode);
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
