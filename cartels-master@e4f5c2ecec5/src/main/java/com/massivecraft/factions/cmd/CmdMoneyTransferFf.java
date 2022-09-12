package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.P;
import com.massivecraft.factions.eco.EcoResult;
import com.massivecraft.factions.iface.EconomyParticipator;
import com.massivecraft.factions.integration.Econ;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.zcore.util.TL;
import net.grandtheftmc.core.util.Utils;
import org.bukkit.ChatColor;

import java.text.NumberFormat;
import java.util.Locale;


public class CmdMoneyTransferFf extends FCommand {

    private final Locale locale = Locale.US;
    private final NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(locale);

    public CmdMoneyTransferFf() {
        this.aliases.add("ff");

        this.requiredArgs.add("amount");
        this.requiredArgs.add("cartel");
        this.requiredArgs.add("cartel");

        //this.optionalArgs.put("", "");

        this.permission = Permission.MONEY_F2F.node;

        senderMustBePlayer = false;
        senderMustBeMember = false;
        senderMustBeModerator = false;
        senderMustBeAdmin = false;
    }

    @Override
    public void perform() {
        double amount = this.argAsDouble(0, 0d);
        Faction from = this.argAsFaction(1);
        if (from == null || !from.isNormal()) return;
        if (!from.getFPlayerAdmin().getName().equalsIgnoreCase(fme.getName())) {
            sender.sendMessage(Utils.f("&cOnly Cartel Admin(s) can send money!"));
            return;
        }

        Faction to = this.argAsFaction(2);
        if (to == null || !to.isNormal()) return;
        if(amount <= 0) return;

        EcoResult result = from.takeFromStash(amount);
        if(result != EcoResult.SUCCESS) return;

        result = to.addToStash(amount);
        if(result != EcoResult.SUCCESS) return;

//        P.p.log(ChatColor.stripColor(P.p.txt.parse(TL.COMMAND_MONEYTRANSFERFF_TRANSFER.toString(), fme.getName(), currencyFormatter.format(amount), from.describeTo(null), to.describeTo(null))));
        from.msg(TL.COMMAND_MONEYTRANSFERFF_TRANSFER.toString(), fme.getName(), currencyFormatter.format(amount), from.describeTo(null), to.describeTo(null));
        to.msg(TL.COMMAND_MONEYTRANSFERFF_TRANSFER.toString(), fme.getName(), currencyFormatter.format(amount), from.describeTo(null), to.describeTo(null));
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_MONEYTRANSFERFF_DESCRIPTION;
    }
}
