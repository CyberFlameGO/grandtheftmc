package com.massivecraft.factions.cmd;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.P;
import com.massivecraft.factions.eco.EcoResult;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.zcore.util.TL;
import net.grandtheftmc.core.util.Utils;
import net.grandtheftmc.vice.Vice;
import net.grandtheftmc.vice.users.ViceUser;
import org.bukkit.ChatColor;

import java.text.NumberFormat;
import java.util.Locale;


public class CmdMoneyTransferFp extends FCommand {

    private final Locale locale = Locale.US;
    private final NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(locale);

    public CmdMoneyTransferFp() {
        this.aliases.add("fp");

        this.requiredArgs.add("amount");
        this.requiredArgs.add("cartel");
        this.requiredArgs.add("player");

        //this.optionalArgs.put("", "");

        this.permission = Permission.MONEY_F2P.node;

        senderMustBePlayer = false;
        senderMustBeMember = false;
        senderMustBeModerator = false;
        senderMustBeAdmin = false;
    }

    @Override
    public void perform() {
        double amount = this.argAsDouble(0, 0d);
        Faction from = this.argAsFaction(1);
        if (from == null) return;
        if (!from.getFPlayerAdmin().getName().equalsIgnoreCase(fme.getName())) {
            sender.sendMessage(Utils.f("&cOnly Cartel Admin(s) can send money!"));
            return;
        }

        FPlayer to = this.argAsBestFPlayerMatch(2);
        if (to == null) return;
        if(to.getPlayer() == null) return;

        EcoResult result = from.takeFromStash(amount);
        if(result != EcoResult.SUCCESS) return;

        ViceUser user = Vice.getUserManager().getLoadedUser(to.getPlayer().getUniqueId());
        if(user == null) return;
        user.addMoney(amount);

//        P.p.log(ChatColor.stripColor(P.p.txt.parse(TL.COMMAND_MONEYTRANSFERFP_TRANSFER.toString(), fme.getName(), currencyFormatter.format(amount), from.describeTo(null), to.describeTo(null))));
        msg(TL.COMMAND_MONEYTRANSFERFP_TRANSFER.toString(), fme.getName(), currencyFormatter.format(amount), from.describeTo(null), to.describeTo(null));
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_MONEYTRANSFERFP_DESCRIPTION;
    }
}
