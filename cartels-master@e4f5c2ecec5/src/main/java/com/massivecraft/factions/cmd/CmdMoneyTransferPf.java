package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.P;
import com.massivecraft.factions.eco.EcoResult;
import com.massivecraft.factions.iface.EconomyParticipator;
import com.massivecraft.factions.integration.Econ;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.zcore.util.TL;
import net.grandtheftmc.core.util.Utils;
import net.grandtheftmc.vice.Vice;
import net.grandtheftmc.vice.users.ViceUser;
import org.bukkit.ChatColor;

import java.text.NumberFormat;
import java.util.Locale;


public class CmdMoneyTransferPf extends FCommand {

    private final Locale locale = Locale.US;
    private final NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(locale);

    public CmdMoneyTransferPf() {
        this.aliases.add("pf");

        this.requiredArgs.add("amount");
        this.requiredArgs.add("player");
        this.requiredArgs.add("cartel");

        //this.optionalArgs.put("", "");

        this.permission = Permission.MONEY_P2F.node;

        senderMustBePlayer = false;
        senderMustBeMember = false;
        senderMustBeModerator = false;
        senderMustBeAdmin = false;
    }

    @Override
    public void perform() {
        double amount = this.argAsDouble(0, 0d);
        FPlayer from = this.argAsBestFPlayerMatch(1);
        if (from == null) return;
        if(from.getPlayer() == null) return;

        Faction to = this.argAsFaction(2);
        if (to == null) return;

        ViceUser user = Vice.getUserManager().getLoadedUser(from.getPlayer().getUniqueId());
        if(user == null) return;
        if(!user.hasMoney(amount)) {
            sender.sendMessage(Utils.f("&cInsufficient funds."));
            return;
        }

        if(to.addToStash(amount) != EcoResult.SUCCESS) return;
        user.takeMoney(amount);

        P.p.log(ChatColor.stripColor(P.p.txt.parse(TL.COMMAND_MONEYTRANSFERPF_TRANSFER.toString(), fme.getName(), currencyFormatter.format(amount), from.describeTo(null), to.describeTo(null))));
        msg(TL.COMMAND_MONEYTRANSFERPF_TRANSFER.toString(), fme.getName(), currencyFormatter.format(amount), from.describeTo(null), to.describeTo(null));

    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_MONEYTRANSFERPF_DESCRIPTION;
    }
}
