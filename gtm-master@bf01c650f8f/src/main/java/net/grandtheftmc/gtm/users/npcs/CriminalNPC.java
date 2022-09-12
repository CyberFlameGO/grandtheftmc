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

public class CriminalNPC extends CoreNPC implements ClickableNPC, CollideableNPC {

    public CriminalNPC(Location loc) {
        super(loc, EntityType.PLAYER, "&3&lCriminal", "&o&7Back to simple criminal lifestyle..");
    }

    @Override
    protected void generateNewNPC() {
        setLookClose(true);
        setSkin("eyJ0aW1lc3RhbXAiOjE1MjEyMjg4MDMyNjgsInByb2ZpbGVJZCI6IjExODAxYzEzMGNiYzQ5MGY4YmEzM2E0MTMxMTZiNzk1IiwicHJvZmlsZU5hbWUiOiIweDE5ZCIsInNpZ25hdHVyZVJlcXVpcmVkIjp0cnVlLCJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNGQ2ZjlhY2NiN2JhMjM1ZWY3ODZmNzFkOWFkNjUzOTE5NjIyYjZhYWE1MjE5NDk5ODA1N2U2Y2JiMDczYSJ9fX0=",
                "XkAbsDP7YR1guq32Xh/FZRQx4nmHK5JnR7FMonif3ynlRD07eLEacxob3PhUpilqPb3+/UNsho+dTSt7rqiTcEXHI971bSi+v/rtOW7nsjMnNnCB0Wi30by6kIw0qd231VVD17046hkVUis+N9HptnN1QqtMV1J9+HTldC+OmepoyNV6Ir0Usyl1d/oqNZWRCVebCMCEX9y0PuV6HQx5wdl4MtEKl78HMf5epdGOcgUaP0qF/RdNQ88dizlgMNHYa5+eq3N2npYlwR+DevSzIJEnokcJkG0wb0Kz9x/3UNLtNR+9/K6T2UVCCPZqV6iWZ+ryL3tExFBByaJYL4Gd8PcjfYVdA0jovN55db7OSZtfix7Uj9k5mqiw4Egj2WfilQ2HLebOm5+jkwWDIchoOWsl15xy+1INrwFi2uRMeV3/ajy0SFXg2WlkAc6XmOPNfYpYNYzd2+fUp4X0RzOyXeqx+BV2moGcUHoJVghK2ddPoIU70H4OxMjwio+A/bOshWrJlDfrnuWlzWRaiIeNIgP2O+P/ojb9jnjWywbRzMy5386Ng6U0GteWQaxxw+RD5iv7ryaziVr4j8oMj20z57StqfgXGZXoC7vMU7lDd55fm5br8cnpKTwCok5E5F2bZFPvx+IenMaVZsGBFpH2EJalOHT6lnQ9K80xlLTLDVc=");
    }

    @Override
    public void onRightClick(NPCRightClickEvent event) {
        Player player = event.getClicker();

        User user = Core.getUserManager().getLoadedUser(player.getUniqueId());
        if (Core.getSettings().isUseEditMode() && user.hasEditMode())
            return;

        JobMode mode = JobMode.getModeOrNull("CRIMINAL");
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
