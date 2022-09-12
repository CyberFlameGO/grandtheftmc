package net.grandtheftmc.core.voting;

import java.sql.Timestamp;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;

import com.vexsoftware.votifier.model.Vote;
import com.vexsoftware.votifier.model.VotifierEvent;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.Lang;
import net.grandtheftmc.core.menus.MenuManager;
import net.grandtheftmc.core.util.Utils;

public class VoteCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender s, Command c, String lbl, String[] args) {
        if (!(s instanceof Player)) {
            s.sendMessage(Utils.f("&cYou are not a player!"));
            return true;
        }
        Player player = (Player) s;
        if (args.length == 0 || !s.isOp()) {
            MenuManager.openMenu((Player) s, "vote");
            return true;
        }
        
        switch (args[0].toLowerCase()) {
            case "test": {
            	
            	// vote test [TWO]
            	
                PluginManager pm = Bukkit.getPluginManager();
                if (pm.getPlugin("NuVotifier") == null &&
                        pm.getPlugin("Votifier") == null) {
                    s.sendMessage(Lang.VOTE.f("&7This command is disabled!"));
                    return true;
                }
                
                VoteSite site = VoteSite.TWO;
                if (args.length == 2){
                	site = VoteSite.valueOf(args[1]);
                }
                
                Vote vote = new Vote();
                vote.setUsername(player.getName());
                vote.setServiceName(site.getName());
                vote.setAddress(site.getURL());
                vote.setTimeStamp(new Timestamp(System.currentTimeMillis()).toString());
                VotifierEvent event = new
                        VotifierEvent(vote);
                Bukkit.getPluginManager().callEvent(event);
                return true;
            }//
            case "reward": {
                if (args.length != 2) {
                    s.sendMessage(Utils.f("&c/vote reward <reward>"));
                    return true;
                }
                VoteReward reward = Core.getVoteManager().getVoteReward(args[1]);
                if (reward == null) {
                    s.sendMessage(Lang.VOTE.f("&7That vote reward does not exist!"));
                    return true;
                }
                s.sendMessage(Lang.VOTE.f("&7Executing vote reward "+reward.getDisplayName()+"&7:"));
                reward.give(player,Core.getUserManager().getLoadedUser(player.getUniqueId()));
                return true;
            }
            case "resetmonth":
            case "resetmonthly":
            	player.sendMessage(Utils.f("&7This command does not currently work."));
//                player.sendMessage(Utils.f("&7This month's top voters:"));
//                ServerUtil.runTaskAsync(() -> {
//                    VoteDAO.deleteLastMonthsVoters();
//
//                    Optional<VoteDAO.VoteUser[]> optional = VoteDAO.getTopTenVoters();
//                    if(!optional.isPresent()) {
//                        player.sendMessage(Utils.f("&cAn error occurred when fetching top voters :("));
//                        return;
//                    }
//
//                    for(VoteDAO.VoteUser voteUser : optional.get()) {
//                        player.sendMessage(Utils.f("&7#&6" + voteUser.getPossition() + " &7" + voteUser.getName() + " with &6" + voteUser.getVotes() + " &7votes."));
//                    }
//
//                    //Core.getVoteManager().resetMonthlyVotes();
//                    Core.getVoteManager().setLastMonthlyReset(System.currentTimeMillis());
//                    s.sendMessage(Lang.VOTE.f("&7Monthly vote count has been reset!"));
//                });

//                new BukkitRunnable() {
//                    @Override
//                    public void run() {
//                        try {
//                            int counter = 1;
//                            Core.sql.prepareStatement("TRUNCATE table last_months_voters;").execute();
//                            ResultSet rs = Core.sql.prepareStatement("SELECT * FROM votes ORDER BY `votes`.`monthlyVotes` DESC LIMIT 10;").executeQuery();
//                            while (rs.next()) {
//                                String name = rs.getString("name");
//                                int votes = rs.getInt("monthlyVotes");
//                                Core.sql.prepareStatement("INSERT INTO last_months_voters (slot, name) VALUES ('" + counter + "', '" + name + "');").execute();
//                                player.sendMessage(Utils.f("&7#&6" + counter + " &7" + name + " with &6" + votes + " &7votes."));
//                                counter++;
//                            }
//                            Core.getVoteManager().resetMonthlyVotes();
//                            Core.getVoteManager().setLastMonthlyReset(System.currentTimeMillis());
//                            s.sendMessage(Lang.VOTE.f("&7Monthly vote count has been reset!"));
//                        } catch (SQLException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }.runTaskAsynchronously(Core.getInstance());
                return true;
            default:
                return true;
        }
    }

}
