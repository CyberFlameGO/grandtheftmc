package net.grandtheftmc.core.casino.game.bet;

import org.bukkit.entity.Player;

public interface CasinoBet {

    CasinoBetType getBetType();

    void start(Player player, SlotMachineBet bet);

    void openMenu(Player player);
}
