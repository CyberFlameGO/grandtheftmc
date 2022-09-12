package net.grandtheftmc.gtm.users.npcs;

import net.citizensnpcs.api.event.NPCCollisionEvent;
import net.citizensnpcs.api.event.NPCLeftClickEvent;
import net.citizensnpcs.api.event.NPCPushEvent;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.grandtheftmc.core.Core;
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

public class HitmanNPC extends CoreNPC implements ClickableNPC, CollideableNPC {

    public HitmanNPC(Location loc) {
        super(loc, EntityType.PLAYER, "&3&lHitman", "&o&7Seek down bounties for money.");
    }

    @Override
    protected void generateNewNPC() {
        setLookClose(true);
        setSkin("eyJ0aW1lc3RhbXAiOjE1MjEyMjg5MTkxMTYsInByb2ZpbGVJZCI6IjExODAxYzEzMGNiYzQ5MGY4YmEzM2E0MTMxMTZiNzk1IiwicHJvZmlsZU5hbWUiOiIweDE5ZCIsInNpZ25hdHVyZVJlcXVpcmVkIjp0cnVlLCJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjM3MDEwYzRiNjZkZjQwNzcyZDY0NDU0YTZlZGI5N2MzYjFkYTNhNzM3ZDY1YjVmYjViOGFmZTk3ZTcwNjIifX19",
                "qPMTqj42MLzHgymB8piUecAgQyGR+kjS7euyOZV0mLoVAfmJXfSWX2GWOmykBuQ8B2jy66AAT/EzGFFMJFdNsilXsGgdYamEV2hLXUfk36yOzKcqGDS3KNoRy352vIlbeVvZVooVhcTsB7sE5uZYx5Qkaqj1+1D3lyruC7ZffbZLFhRXeNlqsGCds9pG8Fp2jUjrYSDNN6aXEZ9p28AKL6PG0NjxuIav0MfITC8HizND1PuN36P2F2qqnxAU4VfIBYaCSLFXicCp/S6A87RclHEQLrXUDzDcJFrkXtcvp0RQsHhmLNAryaVbFwmDRzJ4jyPTc3aFS+66aVf/SbDkGylPl+sDRVltg0q8sbLc7k4y3exhdGUQRs2N55gzwcUBFcZ6ple/B640xy1uoTHNZs3xqUJhQK8jM9odgIxeLIQ/fktQ1OXnQmgrm1S7owJ9t3vy5/yR2mSix/b+2WCARIS/4A73YKKyFkt2X020ElFaQHB0d3D+RHJE9j4cLNNEps/vWgX8FN8G2JFXXV0lDq7an7vE8hJTqdqNBp3Lb3Ly2poasFNQn/gIasp6U008k8mhWvb0BYhL4eLhN9BwSVtdZ6uYxKyH4NCdhkYFVydSCIYvvNDiSGN5DmfiGHM0DUCvCSjr+rvAoulaXQT7Axby+XaO7937LskIjYsVC7k=");
    }

    @Override
    public void onRightClick(NPCRightClickEvent event) {
        Player player = event.getClicker();

        User user = Core.getUserManager().getLoadedUser(player.getUniqueId());
        if (Core.getSettings().isUseEditMode() && user.hasEditMode())
            return;

        JobMode mode = JobMode.getModeOrNull("HITMAN");
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
