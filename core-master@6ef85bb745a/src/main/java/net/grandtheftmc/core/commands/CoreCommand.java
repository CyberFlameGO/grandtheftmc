package net.grandtheftmc.core.commands;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.users.User;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.util.Arrays;

/**
 * Created by Luke Bingham on 06/07/2017.
 */
public abstract class CoreCommand<Sender extends CommandSender> extends BukkitCommand {

    private static CommandMap commandMap = null;

    private String noPermission = ChatColor.RED + "You don't have permission to use this!";
    private final String description;
    private String[] aliases;

    /**
     * Construct a new command.
     *
     * @param command command label
     * @param description command description
     * @param aliases command aliases
     */
    public CoreCommand(String command, String description, String... aliases) {
        super(command);
        this.description = description;
        this.aliases = aliases;

        register();
    }

    /**
     * Construct a new command.
     *
     * @param command command label
     * @param description command description
     */
    public CoreCommand(String command, String description) {
        super(command);
        this.description = description;

        register();
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] strings) {
        if(commandSender instanceof Player && this instanceof RankedCommand) {
            User u = Core.getUserManager().getLoadedUser(((Player) commandSender).getUniqueId());
            if(u.getUserRank().hasRank(((RankedCommand) this).requiredRank()))
                execute((Sender) commandSender, strings);
            else
                sendNoPermission((Sender) commandSender);
            return true;
        }

        execute((Sender) commandSender, strings);
        return true;
    }

    /**
     * This method is fired when the command is executed.
     *
     * @param sender sender type of the command
     * @param args command arguments
     */
    public abstract void execute(Sender sender, String[] args);

    private void register() {
        if (commandMap != null) {
            if(this.aliases != null && this.aliases.length > 0)
                setAliases(Arrays.asList(this.aliases));
            setDescription(this.description);
            commandMap.register(super.getName(), this);
            return;
        }

        try {
            Field field = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            field.setAccessible(true);
            commandMap = (CommandMap) field.get(Bukkit.getServer());

            if(this.aliases != null && this.aliases.length > 0)
                setAliases(Arrays.asList(this.aliases));
            setDescription(this.description);

            commandMap.register(super.getName(), this);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getCommand() {
        return super.getName();
    }

    @Override
    public String getDescription() {
        return description;
    }

    /**
     * This string is displayed the user<br>
     * when they don't have the required rank.
     *
     * @param input permission input
     */
    public void setNoPermissionMessage(String input) {
        this.noPermission = ChatColor.translateAlternateColorCodes('&', input);
    }

    public void sendNoPermission(Sender sender) {
        sender.sendMessage(this.noPermission);
    }
}
