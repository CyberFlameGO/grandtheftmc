package net.grandtheftmc.vice.weapon;

import net.grandtheftmc.guns.WeaponManager;
import net.grandtheftmc.vice.weapon.explosive.*;
import net.grandtheftmc.vice.weapon.melee.*;
import net.grandtheftmc.vice.weapon.ranged.assault.*;
import net.grandtheftmc.vice.weapon.ranged.launcher.GrenadeLauncher;
import net.grandtheftmc.vice.weapon.ranged.launcher.HomingLauncher;
import net.grandtheftmc.vice.weapon.ranged.launcher.RPG;
import net.grandtheftmc.vice.weapon.ranged.launcher.NetLauncher;
import net.grandtheftmc.vice.weapon.ranged.lmg.CombatMG;
import net.grandtheftmc.vice.weapon.ranged.lmg.MG;
import net.grandtheftmc.vice.weapon.ranged.pistol.*;
import net.grandtheftmc.vice.weapon.ranged.shotgun.*;
import net.grandtheftmc.vice.weapon.ranged.smg.*;
import net.grandtheftmc.vice.weapon.ranged.sniper.HeavySniper;
import net.grandtheftmc.vice.weapon.ranged.sniper.SniperRifle;
import net.grandtheftmc.vice.weapon.ranged.special.Flamethrower;
import net.grandtheftmc.vice.weapon.ranged.special.GoldMinigun;
import net.grandtheftmc.vice.weapon.ranged.special.Minigun;

import java.util.Arrays;

/**
 * Created by Luke Bingham on 25/07/2017.
 */
public class WeaponRegistry {

    public WeaponRegistry(WeaponManager weaponManager) {
        weaponManager.registerWeapons(Arrays.asList(

                //PISTOL
                new Pistol(),
                new StunGun(),
                new CombatPistol(),
                new MarksmanPistol(),
                new HeavyPistol(),

                //SMG
                new SMG(),
                new MicroSMG(),
                new CombatPDW(),
                new GusenbergSweeper(),
                new AssaultSMG(),

                //SHOTGUN
                new SawedoffShotgun(),
                new PumpShotgun(),
                new Musket(),
                new AssaultShotgun(),
                new HeavyShotgun(),

                //ASSAULT RIFLE
                new AssaultRifle(),
                new CarbineRifle(),
                new BullpupRifle(),
                new AdvancedRifle(),
                new SpecialCarbine(),

                //LMG
                new MG(),
                new CombatMG(),

                //SNIPER
                new SniperRifle(),
                new HeavySniper(),

                //SPECIAL
                new Minigun(),
                new GoldMinigun(),
                new NetLauncher(),
                new Flamethrower(),

                //LAUNCHER
                new RPG(),
                new HomingLauncher(),
                new GrenadeLauncher(),

                //MELEE
                new Knife(),
                new BaseballBat(),
                new Rake(),
                new NightStick(),
                new Chainsaw(),
                new Katana(),
                new Dildo(),

                //THROWABLE
                new Grenade(),
                new MolotovCocktail(),
                new ProximityMine(),
                new StickyBomb(),
                new TearGas()
        ));
    }
}
