package com.massivecraft.factions.cmd;

import com.massivecraft.factions.*;
import com.massivecraft.factions.eco.EcoResult;
import com.massivecraft.factions.event.FactionRenameEvent;
import com.massivecraft.factions.scoreboards.FTeamWrapper;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.util.MiscUtil;
import com.massivecraft.factions.zcore.util.TL;
import net.grandtheftmc.core.util.NumeralUtil;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.Locale;

public class CmdTag extends FCommand {

    public CmdTag() {
        this.aliases.add("tag");
        this.aliases.add("rename");

        this.requiredArgs.add("cartel tag");
        //this.optionalArgs.put("", "");

        this.permission = Permission.TAG.node;
        this.disableOnLock = true;

        senderMustBePlayer = true;
        senderMustBeMember = false;
        senderMustBeModerator = true;
        senderMustBeAdmin = false;
    }

    @Override
    public void perform() {
        String tag = this.argAsString(0);

        // TODO does not first test cover selfcase?
        if (Factions.getInstance().isTagTaken(tag) && !MiscUtil.getComparisonString(tag).equals(myFaction.getComparisonTag())) {
            msg(TL.COMMAND_TAG_TAKEN);
            System.out.println("1");
            return;
        }

        ArrayList<String> errors = MiscUtil.validateTag(tag);
        if (errors.size() > 0) {
            sendMessage(errors);
            System.out.println("2");
            return;
        }

        // if economy is enabled, they're not on the bypass list, and this command has a cost set, make sure they can pay
//        if (!canAffordCommand(Conf.econCostTag, TL.COMMAND_TAG_TOCHANGE.toString())) {
//            return;
//        }

        if(Conf.econCostTag > 0) {
            EcoResult result = myFaction.takeFromStash(Conf.econCostTag);
            if(result == EcoResult.LOW_FUNDS) {
                fme.msg("<h>%s<i> can't afford <h>%s<i> %s.", myFaction.describeTo(myFaction, true), NumeralUtil.toCurrency(Conf.econCostTag, Locale.US), TL.COMMAND_TAG_TOCHANGE);
                System.out.println("3");
                return;
            }

            if(result != EcoResult.SUCCESS) {
                System.out.println("4");
                return;
            }
        }

        // trigger the faction rename event (cancellable)
        FactionRenameEvent renameEvent = new FactionRenameEvent(fme, tag);
        Bukkit.getServer().getPluginManager().callEvent(renameEvent);
        if (renameEvent.isCancelled()) {
            System.out.println("5");
            return;
        }

        // then make 'em pay (if applicable)
        if (!payForCommand(Conf.econCostTag, TL.COMMAND_TAG_TOCHANGE, TL.COMMAND_TAG_FORCHANGE)) {
            System.out.println("6");
            return;
        }

        String oldtag = myFaction.getTag();
        myFaction.setTag(tag);
        System.out.println("7");

        // Inform
        for (FPlayer fplayer : FPlayers.getInstance().getOnlinePlayers()) {
            if (fplayer.getFactionId().equals(myFaction.getId())) {
                fplayer.msg(TL.COMMAND_TAG_FACTION, fme.describeTo(myFaction, true), myFaction.getTag(myFaction));
                continue;
            }

            // Broadcast the tag change (if applicable)
            if (Conf.broadcastTagChanges) {
                Faction faction = fplayer.getFaction();
                fplayer.msg(TL.COMMAND_TAG_CHANGED, fme.getColorTo(faction) + oldtag, myFaction.getTag(faction));
            }
        }

        FTeamWrapper.updatePrefixes(myFaction);
        System.out.println("8");
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_TAG_DESCRIPTION;
    }

}
