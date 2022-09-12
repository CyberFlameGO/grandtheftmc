package net.grandtheftmc.guns.weapon;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import java.util.HashMap;
import java.util.HashSet;

/**
 * Created by Luke Bingham on 17/07/2017.
 */
public enum WeaponType {

    /* Ranged Weapons */
    PISTOL, SMG, SHOTGUN, ASSAULT, LMG, SNIPER,

    /* Unkown / Special */
    SPECIAL, MINIGUN, NETGUN, FLAMETHROWER, CLAUSINATOR,

    /* Rocket Launcher */
    LAUNCHER,

    /* Hand held Melee Weapons */
    MELEE,

    /* Throwable Weapons (Grenade) */
    THROWABLE,

    /* Droppable 'Weapons' (Airstrike, Nuke) */
    DROPPABLE
    ;

//    /* PISTOLS */
//    PISTOL("Pistol", new Pistol(), Type.RANGED, AmmoType.PISTOL),
//    STUN_GUN("Stun Gun", new StunGun(), Type.RANGED, AmmoType.PISTOL),
//    COMBAT_PISTOL("Combat Pistol", new CombatPistol(), Type.RANGED, AmmoType.PISTOL),
//    HEAVY_PISTOL("Heavy Pistol", new HeavyPistol(), Type.RANGED, AmmoType.PISTOL),
//    MARKSMAN_PISTOL("Marksman Pistol", new MarksmanPistol(), Type.RANGED, AmmoType.PISTOL),
//
//    /* SUB MACHINE GUNS */
//    MICRO_SMG("Micro SMG", new MicroSMG(), Type.RANGED, AmmoType.SMG),
//    SMG("SMG", new SMG(), Type.RANGED, AmmoType.SMG),
//    ASSAULT_SMG("Assault SMG", new AssaultSMG(), Type.RANGED, AmmoType.SMG),
//    COMBAT_PDW("Combat PDW", new CombatPDW(), Type.RANGED, AmmoType.SMG),
//    GUSENBERG_SWEEPER("Gusenberg Sweeper", new GusenbergSweeper(), Type.RANGED, AmmoType.SMG),
//
//    /* SHOTGUNS */
//    SAWEDOFF_SHOTGUN("Sawdoff Shotgun", new SawedoffShotgun(), Type.RANGED, AmmoType.SHOTGUN),
//    PUMP_SHOTGUN("Pump Shotgun", new PumpShotgun(), Type.RANGED, AmmoType.SHOTGUN),
//    MUSKET("Musket", new Musket(), Type.RANGED, AmmoType.SHOTGUN),
//    ASSAULT_SHOTGUN("Assault Shotgun", new AssaultShotgun(), Type.RANGED, AmmoType.SHOTGUN),
//    HEAVY_SHOTGUN("Heavy Shotgun", new HeavyShotgun(), Type.RANGED, AmmoType.SHOTGUN),
//
//    /* ASSULT RIFLES */
//    ASSAULT_RIFLE("Assault Rifle", new AssaultRifle(), Type.RANGED, AmmoType.ASSAULT_RIFLE),
//    CARBINE_RIFLE("Carbine Rifle", new CarbineRifle(), Type.RANGED, AmmoType.ASSAULT_RIFLE),
//    BULLPUP_RIFLE("Bullpup Rifle", new BullpupRifle(), Type.RANGED, AmmoType.ASSAULT_RIFLE),
//    ADVANCED_RIFLE("Advanced Rifle", new AdvancedRifle(), Type.RANGED, AmmoType.ASSAULT_RIFLE),
//    SPECIAL_CARBINE("Special Carbine", new SpecialCarbine(), Type.RANGED, AmmoType.ASSAULT_RIFLE),
//
//    /* MG */
//    MG("MG", new MG(), Type.RANGED, AmmoType.LMG),
//    COMBAT_MG("Combat MG", new CombatMG(), Type.RANGED, AmmoType.LMG),
//
//    /* SNIPER RIFLES */
//    SNIPER_RIFLE("Sniper Rifle", new SniperRifle(), Type.RANGED, AmmoType.SNIPER),
//    HEAVY_SNIPER("Heavy Sniper", new HeavySniper(), Type.RANGED, AmmoType.SNIPER),
//
//    /* SPECIAL */
//    MINIGUN("Minigun", new Minigun(), Type.RANGED, AmmoType.MINIGUN),
//
//    /* LAUNCHERS */
//    RPG("RPG", new RPG(), Type.RANGED, AmmoType.LAUNCHER),
//    HOMING_LAUNCHER("Homing Launcher", new HomingLauncher(), Type.RANGED, AmmoType.LAUNCHER),
//    GRENADE_LAUNCHER("Grenade Launcher", new GrenadeLauncher(), Type.RANGED, AmmoType.EXPLOSIVE),
//
//    /* MELEE */
//    RAKE("Rake", new Rake(), Type.MELEE, AmmoType.MELEE),
//    NIGHT_STICK("Night Stick", new NightStick(), Type.MELEE, AmmoType.MELEE),
//    BASEBALL_BAT("Baseball Bat", new BaseballBat(), Type.MELEE, AmmoType.MELEE),
//    KNIFE("Knife", new Knife(), Type.MELEE, AmmoType.MELEE),
//    CHAINSAW("Chainsaw", new Chainsaw(), Type.MELEE, AmmoType.ENERGY),
//
//    /* EXPLOSIVES */
//    GRENADE("Grenade", new Grenade(), Type.THROWABLE, AmmoType.EXPLOSIVE),
//    TEAR_GAS("Tear Gas", new TearGas(), Type.THROWABLE, AmmoType.EXPLOSIVE),
//    MOLOTOV_COCKTAIL("Molotov Cocktail", new MolotovCocktail(), Type.THROWABLE, AmmoType.EXPLOSIVE),
//    STICKY_BOMB("Sticky Bomb", new StickyBomb(), Type.THROWABLE, AmmoType.EXPLOSIVE),
//    PROXIMITY_MINE("Proximity Mine", new ProximityMine(), Type.THROWABLE, AmmoType.EXPLOSIVE),
//
//    /* DROPPABLE */
//    AIRSTRIKE("Airstrike", new Airstrike(), Type.DROPPABLE),
//    NUKE("Nuke", new Nuke(), Type.DROPPABLE),
//    ;
//
//    private static final HashSet<Weapon> ALL_WEAPONS = Sets.newHashSet();
//    private static final HashMap<Type, HashSet<Weapon>> TYPE_WEAPONS = Maps.newHashMap();
//
//    private final String name;
//    private final Weapon weapon;
//    private final Type type;
//    private AmmoType ammoType;
//
//    WeaponType(String name, Weapon weapon, Type type) {
//        this.name = name;
//        this.weapon = weapon;
//        this.type = type;
//    }
//
//    WeaponType(String name, Weapon weapon, Type type, AmmoType ammoType) {
//        this.name = name;
//        this.weapon = weapon;
//        this.type = type;
//        this.ammoType = ammoType;
//    }
//
//    public String getName() {
//        return name;
//    }
//
//    public Weapon getWeapon() {
//        return weapon;
//    }
//
//    public Type getType() {
//        return type;
//    }
//
//    public AmmoType getAmmoType() {
//        return ammoType;
//    }
//
//    public static HashSet<Weapon> getWeaponsFromType(Type type) {
//        if(!TYPE_WEAPONS.containsKey(type)) {
//            HashSet<Weapon> temp = Sets.newHashSet();
//            for (WeaponType weaponType : values()) {
//                if (weaponType.type != type) continue;
//                temp.add(weaponType.getWeapon());
//            }
//            TYPE_WEAPONS.put(type, temp);
//        }
//
//        return TYPE_WEAPONS.get(type);
//    }
//
//    public static HashSet<Weapon> getWeapons() {
//        if (ALL_WEAPONS.isEmpty())
//            for (WeaponType weaponType : values()) ALL_WEAPONS.add(weaponType.getWeapon());
//
//        return ALL_WEAPONS;
//    }
//
//    public enum Type {
//        RANGED, THROWABLE, MELEE, DROPPABLE;
//    }
}
