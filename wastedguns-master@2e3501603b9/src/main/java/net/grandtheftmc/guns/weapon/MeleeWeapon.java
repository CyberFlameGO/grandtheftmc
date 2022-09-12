package net.grandtheftmc.guns.weapon;

import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import com.j0ach1mmall3.jlib.methods.Sounds;
import com.j0ach1mmall3.wastedguns.MathUtil;

import net.grandtheftmc.core.util.C;

/**
 * Created by Luke Bingham on 18/07/2017.
 */
public class MeleeWeapon extends Weapon<MeleeWeapon> {

    protected double meleeDamage = 0.0, range = 0.0;

    /**
     * Construct a new Weapon.
     */
    public MeleeWeapon(short uniqueIdentifier, String name, WeaponType weaponType, AmmoType ammoType, ItemStack itemStack, Sound[] sounds) {
        super(uniqueIdentifier, name, weaponType, ammoType, itemStack, sounds);

        this.weaponSkins = new WeaponSkin[] {
                new WeaponSkin(weaponType, itemStack.getDurability(), "&e&lDefault")
        };
    }

    @Override
    public void onRightClick(Player player){
        if(getName().equalsIgnoreCase("Chainsaw")) {
            MathUtil.getNearbyEntities(player, this.range).forEach(e -> {
                if (player.hasLineOfSight(e)) {
                    Vector toEntity = e.getLocation().toVector().subtract(player.getLocation().toVector());
                    double dot = toEntity.normalize().dot(player.getLocation().getDirection());
                    if (dot <= 1 && dot >= 0) {
                        e.setNoDamageTicks(0);
                        e.damage(getMeleeDamage(), player);
                    }
                }
            });
            Sounds.broadcastSound(getSounds()[0], player.getEyeLocation());
        }
    }

    @Override
    public String[] getStatsBar() {
        String[] output = new String[1];
        String symbol = ":",
                done = ChatColor.GREEN.toString() + ChatColor.BOLD,
                empty = ChatColor.DARK_GRAY.toString() + ChatColor.BOLD;
        int bars = 10;
        double best = 0;
        double result;
        int stat;
        for(int i = 0; i < 1; i ++) {
            if(i == 0) {
                output[i] = "";
                best = 15.0;

                result = net.grandtheftmc.core.util.MathUtil.getPercentBetweenValues(best, this.meleeDamage);
                stat = (int) Math.floor(result) / 10;
                for(int x = 0; x < bars; x++) {
                    output[i] += (x <= stat ? done : empty) + symbol;
                }
                output[i] += C.GRAY + " Damage";
            }
//            else if(x == 1) {
//                output[x] = "Range ";
//                best = 100.0;
//                for(int i = 1; i < (bars+1); i++) {
//                    if(i * (best / bars) > this.range) output[x] += done+symbol;
//                    else output[x] += empty+symbol;
//                }
//            }
        }

        return output;
    }

    @Override
    public MeleeWeapon clone() {
        MeleeWeapon weapon = new MeleeWeapon(getUniqueIdentifier(), getName(), getWeaponType(), getAmmoType(), getBaseItemStack().clone(), getSounds());
        weapon.oldItemStack = super.oldItemStack.clone();
        weapon.deathMessages = super.deathMessages;
        weapon.walkSpeed = super.walkSpeed;
        weapon.delay = super.delay;
        weapon.meleeDamage = this.meleeDamage;
        weapon.range = this.range;
        return weapon;
    }

    public double getMeleeDamage() {
        return meleeDamage;
    }

    public double getRange() {
        return range;
    }

    @Override
	public WeaponSkin[] getWeaponSkins() {
        return weaponSkins;
    }

    protected void setWeaponSkins(WeaponSkin... skins) {
        WeaponSkin defaultSkin = this.weaponSkins[0];
        this.weaponSkins = new WeaponSkin[skins.length + 1];
        for(int i = 0; i < skins.length; i++)
            this.weaponSkins[i + 1] = skins[i];
        this.weaponSkins[0] = defaultSkin;
    }
}
