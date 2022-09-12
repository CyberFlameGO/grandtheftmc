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
import net.grandtheftmc.vice.users.ViceUserManager;
import org.bukkit.ChatColor;

import java.text.NumberFormat;
import java.util.Locale;


public class CmdMoneyDeposit extends FCommand {

    private final Locale locale = Locale.US;
    private final NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(locale);

    public CmdMoneyDeposit() {
        super();
        this.aliases.add("d");
        this.aliases.add("deposit");

        this.requiredArgs.add("amount");
        this.optionalArgs.put("cartel", "yours");

        this.permission = Permission.MONEY_DEPOSIT.node;

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

        ViceUser user = Vice.getUserManager().getLoadedUser(fme.getPlayer().getUniqueId());
        if(!user.hasMoney(amount)) {
            sender.sendMessage(Utils.f("&cInsufficient funds."));
            return;
        }

        EcoResult result = myFaction.addToStash(amount);
        if(result != EcoResult.SUCCESS) return;
        user.takeMoney(amount);

//        P.p.log(ChatColor.stripColor(P.p.txt.parse(TL.COMMAND_MONEYDEPOSIT_DEPOSITED.toString(), fme.getName(), currencyFormatter.format(amount), faction.describeTo(null))));
        faction.msg(C.GREEN + TL.COMMAND_MONEYDEPOSIT_DEPOSITED.toString(), fme.getName(), currencyFormatter.format(amount), faction.describeTo(null));
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_MONEYDEPOSIT_DESCRIPTION;
    }

}
