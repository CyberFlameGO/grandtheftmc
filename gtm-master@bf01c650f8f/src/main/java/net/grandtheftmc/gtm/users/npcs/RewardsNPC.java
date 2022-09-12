package net.grandtheftmc.gtm.users.npcs;

import net.citizensnpcs.api.event.NPCCollisionEvent;
import net.citizensnpcs.api.event.NPCLeftClickEvent;
import net.citizensnpcs.api.event.NPCPushEvent;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.grandtheftmc.core.menus.MenuManager;
import net.grandtheftmc.core.npc.CoreNPC;
import net.grandtheftmc.core.npc.interfaces.ClickableNPC;
import net.grandtheftmc.core.npc.interfaces.CollideableNPC;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

/**
 * Created by Timothy Lampen on 1/14/2018.
 */
public class RewardsNPC extends CoreNPC implements ClickableNPC, CollideableNPC {

    public RewardsNPC(Location loc) {
        super(loc, EntityType.PLAYER, "&2&lFree Tony", "&7&oClaim rewards and vote here!");
    }

    @Override
    protected void generateNewNPC() {
        setLookClose(true);
        setSkin("eyJ0aW1lc3RhbXAiOjE1MTU5ODM4MzE4NDYsInByb2ZpbGVJZCI6IjdkYTJhYjNhOTNjYTQ4ZWU4MzA0OGFmYzNiODBlNjhlIiwicHJvZmlsZU5hbWUiOiJHb2xkYXBmZWwiLCJzaWduYXR1cmVSZXF1aXJlZCI6dHJ1ZSwidGV4dHVyZXMiOnsiU0tJTiI6eyJ1cmwiOiJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2I1ZGMyNmUzMjZhNTY5ODUxOTgyM2FiYWFlZWQyODM1YTk3YzY5Yzk4Y2JhMTNhMmYxNDdkMTI5MWU1ZTgifX19", "UonaJdHIdT7paVOCZ5sNTrrV2tMT6lIdGOC/AG5oleC0m1QXuPkoEiWlf26IOzHMV9KfZuTCZPcc35f9+X8ySG+8cMSMXvhAzM6sCxbidB8XoQ8MgfAt/eUBQzyhRU/f02pRpefTaYfNVmjucrpI+GK5Hrq6PfmqxFRyiFVaFcWxL6ZQ23j5REy7lHvbhcqIFy10VRJqzIy4TeNCh9X3aBRoNK/nosUHR+M0qQiTYrAM8eRFFTu/d55ofAZx0dgs7XM4Mp/UswGa1yUdgIwtJSKsVVxgiwQ5/Gj1iWY6SAmcBF9ueySiqSLtopC8NSUzzD8HxiuBvq0yQPSsnxzGljBFcwKUzzCu+c03b8XfcppwV5WXa3uXGDNRHL/MzE9Sdp7vi4Pg2e44kNnakuGA/w0jEMNu4EPz+lgBHL9QIzL1mLZiXc9xGquvDtNVR1QePYY8uRbqVTahXDJmHe/1D0wisrLjiaKQPHhjHsRqk6H6D/Zr+3U1/wH+XHdHqVWhAh4dqznOD3gLCmXmaegRLvkNi0LHhnnurdP1y/CY4UCL/VdC86Q2LANeKCxqx3BSnXctRyMXiL/ZUwK41flbdeiOv3zvR7QwBilqAufQ+zXZCeMUTBU0RDeXeXDUV3LY3LkKIpmO1LqK0PK7uOEDTvW8fX/s/cxXxBcUO15b1mQ=");
        setCollideable(true);
    }

    @Override
    public void onRightClick(NPCRightClickEvent event) {
        Player player = event.getClicker();
        MenuManager.openMenu(player, "rewards");
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
