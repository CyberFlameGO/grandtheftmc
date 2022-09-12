package net.grandtheftmc.gtm.commands;

import java.util.Optional;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.Utils;
import net.grandtheftmc.core.commands.CoreCommand;
import net.grandtheftmc.core.commands.RankedCommand;
import net.grandtheftmc.core.users.User;
import net.grandtheftmc.core.users.UserRank;
import net.grandtheftmc.gtm.GTM;
import net.grandtheftmc.gtm.users.GTMUser;
import net.grandtheftmc.guns.weapon.Weapon;
import net.grandtheftmc.guns.weapon.WeaponSkin;

public class SkinCommand extends CoreCommand<Player> implements RankedCommand {
    public SkinCommand() {
        super("skin", "A command used to manage weapon skins.");
    }

    @Override
    public void execute(Player sender, String[] args) {
        User user = Core.getUserManager().getLoadedUser(sender.getUniqueId());
        GTMUser gtmUser = GTM.getUserManager().getLoadedUser(sender.getUniqueId());

        if (user.isAdmin()) {
            if (args.length == 3) {
                if (args[0].equalsIgnoreCase("unlock")) {
                    Optional<Weapon<?>> weaponOpt = null;

                    try {
                        short weaponID = Short.parseShort(args[1]);

                        weaponOpt = GTM.getWastedGuns().getWeaponManager().getWeapon(weaponID);
                    } catch (NumberFormatException e) {
                        String weaponName = args[1];

                        weaponOpt = GTM.getWastedGuns().getWeaponManager().getWeapon(weaponName);
                    }

                    if (weaponOpt.isPresent()) {
                        Weapon<?> weapon = weaponOpt.get();

                        try {
                            short skinID = Short.parseShort(args[2]);
                            WeaponSkin skin = weapon.getWeaponSkins().length > skinID ? weapon.getWeaponSkins()[skinID] : null;

                            if (skin != null) {
                                if (gtmUser.getRawUnlockedWeaponSkins().get(weapon.getUniqueIdentifier()) == null
                                        || !gtmUser.getRawUnlockedWeaponSkins().get(weapon.getUniqueIdentifier()).contains((short) (skin.getIdentifier() - weapon.getWeaponIdentifier()))) {
                                    gtmUser.unlockWeaponSkin(weapon, skin);

                                    sender.sendMessage(Utils.f("&7You unlocked this skin!"));
                                } else {
                                    sender.sendMessage(Utils.f("&cThis skin has already been unlocked!"));
                                }
                            } else {
                                sender.sendMessage(Utils.f("&cA skin with this ID does not exist!"));
                            }
                        } catch (NumberFormatException e) {
                            sender.sendMessage(Utils.f("&cThe skin ID has to be a number!"));
                        }
                    } else {
                        sender.sendMessage(Utils.f("&cA weapon with this name/ID does not exist!"));
                    }
                } else if (args[0].equalsIgnoreCase("lock")) {
                    Optional<Weapon<?>> weaponOpt = null;

                    try {
                        short weaponID = Short.parseShort(args[1]);

                        weaponOpt = GTM.getWastedGuns().getWeaponManager().getWeapon(weaponID);
                    } catch (NumberFormatException e) {
                        String weaponName = args[1];

                        weaponOpt = GTM.getWastedGuns().getWeaponManager().getWeapon(weaponName);
                    }

                    if (weaponOpt.isPresent()) {
                        Weapon<?> weapon = weaponOpt.get();

                        try {
                            short skinID = Short.parseShort(args[2]);
                            WeaponSkin skin = weapon.getWeaponSkins().length > skinID ? weapon.getWeaponSkins()[skinID] : null;

                            if (skin != null) {
                                if (gtmUser.getRawUnlockedWeaponSkins().get(weapon.getUniqueIdentifier()) != null
                                        && gtmUser.getRawUnlockedWeaponSkins().get(weapon.getUniqueIdentifier()).contains((short) (skin.getIdentifier() - weapon.getWeaponIdentifier()))) {
                                    gtmUser.lockWeaponSkin(weapon, skin);

                                    sender.sendMessage(Utils.f("&7You locked this skin!"));
                                } else {
                                    sender.sendMessage(Utils.f("&cThis skin has already been locked!"));
                                }
                            } else {
                                sender.sendMessage(Utils.f("&cA skin with this ID does not exist!"));
                            }
                        } catch (NumberFormatException e) {
                            sender.sendMessage(Utils.f("&cThe skin ID has to be a number!"));
                        }
                    } else {
                        sender.sendMessage(Utils.f("&cA weapon with this name/ID does not exist!"));
                    }
                }
            } else if (args.length == 4) {
                if (args[0].equalsIgnoreCase("unlock")) {
                    Player otherPlayer = Bukkit.getPlayer(args[1]);

                    if (otherPlayer != null && otherPlayer.isOnline()) {
                        GTMUser otherGTMUser = GTM.getUserManager().getLoadedUser(otherPlayer.getUniqueId());
                        Optional<Weapon<?>> weaponOpt = null;

                        try {
                            short weaponID = Short.parseShort(args[2]);

                            weaponOpt = GTM.getWastedGuns().getWeaponManager().getWeapon(weaponID);
                        } catch (NumberFormatException e) {
                            String weaponName = args[2];

                            weaponOpt = GTM.getWastedGuns().getWeaponManager().getWeapon(weaponName);
                        }

                        if (weaponOpt.isPresent()) {
                            Weapon<?> weapon = weaponOpt.get();

                            try {
                                short skinID = Short.parseShort(args[3]);
                                WeaponSkin skin = weapon.getWeaponSkins().length > skinID ? weapon.getWeaponSkins()[skinID] : null;

                                if (skin != null) {
                                    if (otherGTMUser.getRawUnlockedWeaponSkins().get(weapon.getUniqueIdentifier()) == null
                                            || !otherGTMUser.getRawUnlockedWeaponSkins().get(weapon.getUniqueIdentifier()).contains((short) (skin.getIdentifier() - weapon.getWeaponIdentifier()))) {
                                        otherGTMUser.unlockWeaponSkin(weapon, skin);

                                        sender.sendMessage(Utils.f("&7You unlocked this skin for &a" + otherPlayer.getName() + "!"));
                                    } else {
                                        sender.sendMessage(Utils.f("&a" + otherPlayer.getName() + " already has this skin unlocked!"));
                                    }
                                } else {
                                    sender.sendMessage(Utils.f("&cA skin with this ID does not exist!"));
                                }
                            } catch (NumberFormatException e) {
                                sender.sendMessage(Utils.f("&cThe skin ID has to be a number!"));
                            }
                        } else {
                            sender.sendMessage(Utils.f("&cA weapon with this name/ID does not exist!"));
                        }
                    }
                } else if (args[0].equalsIgnoreCase("lock")) {
                    Player otherPlayer = Bukkit.getPlayer(args[1]);

                    if (otherPlayer != null && otherPlayer.isOnline()) {
                        GTMUser otherGTMUser = GTM.getUserManager().getLoadedUser(otherPlayer.getUniqueId());
                        Optional<Weapon<?>> weaponOpt = null;

                        try {
                            short weaponID = Short.parseShort(args[2]);

                            weaponOpt = GTM.getWastedGuns().getWeaponManager().getWeapon(weaponID);
                        } catch (NumberFormatException e) {
                            String weaponName = args[2];

                            weaponOpt = GTM.getWastedGuns().getWeaponManager().getWeapon(weaponName);
                        }

                        if (weaponOpt.isPresent()) {
                            Weapon<?> weapon = weaponOpt.get();

                            try {
                                short skinID = Short.parseShort(args[3]);
                                WeaponSkin skin = weapon.getWeaponSkins().length > skinID ? weapon.getWeaponSkins()[skinID] : null;

                                if (skin != null) {
                                    if (otherGTMUser.getRawUnlockedWeaponSkins().get(weapon.getUniqueIdentifier()) != null
                                            && otherGTMUser.getRawUnlockedWeaponSkins().get(weapon.getUniqueIdentifier()).contains((short) (skin.getIdentifier() - weapon.getWeaponIdentifier()))) {
                                        otherGTMUser.lockWeaponSkin(weapon, skin);

                                        sender.sendMessage(Utils.f("&7You locked this skin for &a" + otherPlayer.getName() + "!"));
                                    } else {
                                        sender.sendMessage(Utils.f("&a" + otherPlayer.getName() + " already has this skin locked!"));
                                    }
                                } else {
                                    sender.sendMessage(Utils.f("&cA skin with this ID does not exist!"));
                                }
                            } catch (NumberFormatException e) {
                                sender.sendMessage(Utils.f("&cThe skin ID has to be a number!"));
                            }
                        } else {
                            sender.sendMessage(Utils.f("&cA weapon with this name/ID does not exist!"));
                        }
                    }
                }
            }
        } else {
            sender.sendMessage(Utils.f("&cYou do not have permission to execute this command!"));
        }
    }

    @Override
    public UserRank requiredRank() {
        return UserRank.DEFAULT;
    }
}