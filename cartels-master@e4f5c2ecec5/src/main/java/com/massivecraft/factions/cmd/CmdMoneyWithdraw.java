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
import net.grandtheftmc.core.util.C;
import net.grandtheftmc.vice.Vice;
import net.grandtheftmc.vice.users.ViceUser;
import org.bukkit.ChatColor;

import java.text.NumberFormat;
import java.util.Locale;


public class CmdMoneyWithdraw extends FCommand {

    private final Locale locale = Locale.US;
    private final NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(locale);

    public CmdMoneyWithdraw() {
        this.aliases.add("w");
        this.aliases.add("wdraw");
        this.aliases.add("withdraw");

        this.requiredArgs.add("amount");
        this.optionalArgs.put("cartel", "yours");

        this.permission = Permission.MONEY_WITHDRAW.node;

        senderMustBePlayer = true;
        senderMustBeMember = false;
        senderMustBeModerator = false;
        senderMustBeAdmin = false;
    }

    @Override
    public void perform() {
        double amount = this.argAsDouble(0, 0d);
        Faction faction = this.argAsFaction(1, myFaction);
        if (faction == null || !faction.isNormal()) return;
        if (!myFaction.getFPlayerAdmin().getName().equals(sender.getName())) {
            sender.sendMessage(Utils.f("&cOnly Cartel Admin(s) can withdraw!"));
            return;
        }

        EcoResult result = myFaction.takeFromStash(amount);
        if(result != EcoResult.SUCCESS) return;

        ViceUser user = Vice.getUserManager().getLoadedUser(fme.getPlayer().getUniqueId());
        user.addMoney(amount);

//        P.p.log(ChatColor.stripColor(P.p.txt.parse(TL.COMMAND_MONEYWITHDRAW_WITHDRAW.toString(), fme.getName(), currencyFormatter.format(amount), faction.describeTo(null))));
        myFaction.msg(C.GREEN + TL.COMMAND_MONEYWITHDRAW_WITHDRAW.toString(), fme.getName(), currencyFormatter.format(amount), faction.describeTo(null));
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_MONEYWITHDRAW_DESCRIPTION;
    }
}
