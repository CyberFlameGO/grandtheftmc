package net.grandtheftmc.gtm.event.easter;

import net.grandtheftmc.core.commands.CoreCommand;
import net.grandtheftmc.core.commands.RankedCommand;
import net.grandtheftmc.core.database.BaseDatabase;
import net.grandtheftmc.core.users.UserRank;
import net.grandtheftmc.core.util.C;
import net.grandtheftmc.core.util.Callback;
import net.grandtheftmc.core.util.ServerUtil;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.SQLException;

public final class EasterEggCommand extends CoreCommand<Player> implements RankedCommand {

	private final EasterEvent event;

	public EasterEggCommand(EasterEvent event) {
		super("easteregg", "Management command for setting up Easter Eggs.");
		this.event = event;
	}

	@Override
	public void execute(Player sender, String[] strings) {
		this.addEgg(sender, aBoolean -> {
			sender.sendMessage(aBoolean ? C.GREEN + "An easter egg location has been set." : C.RED + "There was an error adding an easter egg location..");
		});
	}

	@Override
	public UserRank requiredRank() {
		return UserRank.MANAGER;
	}

	private void addEgg(Player player, Callback<Boolean> callback) {
		ServerUtil.runTaskAsync(() -> {
			try (Connection connection = BaseDatabase.getInstance().getConnection()) {
				EasterEgg easterEgg = EasterDAO.addEasterEgg(connection, player.getLocation());
				if (easterEgg == null) {
					callback.call(false);
					return;
				}

				if (this.event != null)
					this.event.eggs.add(easterEgg);

				callback.call(true);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		});
	}
}
