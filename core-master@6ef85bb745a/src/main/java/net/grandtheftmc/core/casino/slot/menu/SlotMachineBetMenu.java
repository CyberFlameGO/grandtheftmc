package net.grandtheftmc.core.casino.slot.menu;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.Lang;
import net.grandtheftmc.core.Utils;
import net.grandtheftmc.core.casino.coins.ChipAmount;
import net.grandtheftmc.core.casino.game.CasinoGame;
import net.grandtheftmc.core.casino.game.bet.CasinoBet;
import net.grandtheftmc.core.casino.game.bet.SlotMachineBet;
import net.grandtheftmc.core.inventory.CoreMenu;
import net.grandtheftmc.core.inventory.CoreMenuFlag;
import net.grandtheftmc.core.inventory.button.ClickableItem;
import net.grandtheftmc.core.inventory.button.IMenuClickAction;
import net.grandtheftmc.core.util.C;
import net.grandtheftmc.core.util.factory.ItemFactory;
import net.grandtheftmc.core.util.title.NMSTitle;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

public class SlotMachineBetMenu extends CoreMenu {

    private final CasinoGame game;

    public SlotMachineBetMenu(CasinoGame game) {
        super(3, "Place your bet", CoreMenuFlag.CLOSE_ON_NULL_CLICK, CoreMenuFlag.RESET_CURSOR_ON_OPEN);
        this.game = game;

        addItem(new ClickableItem(11, ChipAmount.TEN.getItemStack(), (player, clickType) -> this.proceed(player, SlotMachineBet.TINY)));
        addItem(new ClickableItem(12, ChipAmount.FIFTY.getItemStack(), (player, clickType) -> this.proceed(player, SlotMachineBet.SMALL)));
        addItem(new ClickableItem(13, ChipAmount.HUNDRED.getItemStack(), (player, clickType) -> this.proceed(player, SlotMachineBet.HIGH)));
        addItem(new ClickableItem(14, ChipAmount.THREE_HUNDRED.getItemStack(), (player, clickType) -> this.proceed(player, SlotMachineBet.INSANE)));
        addItem(new ClickableItem(15, ChipAmount.EIGHT_HUNDRED.getItemStack(), (player, clickType) -> this.proceed(player, SlotMachineBet.ALL_IN)));


        addItem(new ClickableItem(26, new ItemFactory(Material.GOLD_INGOT).setName(C.YELLOW + C.BOLD + "Rewards").setLore("", C.GRAY + "Click to see the roll rewards!").build(), new IMenuClickAction(){

			@Override
			public void onClick(Player player, ClickType clickType) {
				new SlotRewardMenu().openInventory(player);
			}
        }));
    }

    private void proceed(Player player, SlotMachineBet bet) {
        player.closeInventory();
        if(game.isInProgress()) {
            NMSTitle.sendTitle(player, "", Utils.f("&cThis Casino Game is in progress"), 1*20, 2*20, 1*20);
            player.sendMessage(Lang.CASINO.f("This Casino Game is in progress."));
            return;
        }

        if(!Core.getCoinManager().hasCasinoChips(player, bet.getCost().getAmount())) {
            NMSTitle.sendTitle(player, "", Utils.f("&cYou cannot afford this bet"), 1*20, 2*20, 1*20);
            player.sendMessage(Lang.CASINO.f("You cannot afford that bet, You have " + Core.getCoinManager().getTotalCasinoChips(player) + " chips."));
            return;
        }

        Core.getCoinManager().removeCasinoChips(player, bet.getCost().getAmount());
        ((CasinoBet) game).start(player, bet);
    }
}
