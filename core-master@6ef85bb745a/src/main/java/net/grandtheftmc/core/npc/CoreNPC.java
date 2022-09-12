package net.grandtheftmc.core.npc;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.google.common.collect.Lists;
import com.mojang.authlib.properties.Property;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.event.DespawnReason;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.npc.skin.SkinnableEntity;
import net.citizensnpcs.trait.Age;
import net.citizensnpcs.trait.Controllable;
import net.citizensnpcs.trait.Gravity;
import net.citizensnpcs.trait.LookClose;
import net.citizensnpcs.trait.SkinLayers;
import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.util.ServerUtil;

/**
 * Created by Timothy Lampen on 1/13/2018.
 *
 * @apiNote When you implements one of the interfaces you still have to set the targetable / damageable / collidable / etc traits using this class.
 * @apiNote When making changes to the NPC entity such as it's location, rather do NPC#teleport than Entity#teleport.
 */
public abstract class CoreNPC {

    private final NPC npc;
    private final int id;
    private final Location startingLoc;
    private final Hologram hologram;
    private final EntityType type;

    /**
     * @apiNote default constructor for the CoreNPC, does generateNewNPC after it is completed.
     */
    public CoreNPC(Location loc, EntityType type, String displayName) {
        this(loc, type, displayName, null);
    }

    /**
     * @apiNote if you use this constructor, you cannot TP the entity expect for the first, original location. If functionality is required, talk to teddeh to implement negative slime shit.
     */
    public CoreNPC(Location loc, EntityType type, String... lines) {
        this.type = type;

        if (!loc.getChunk().isLoaded())
            loc.getChunk().load();

        //Remove left over NPC's if any.
        List<NPC> toRemove = Lists.newArrayList();
        CitizensAPI.getNPCRegistry().forEach(citizensNPC -> {
            if (citizensNPC.getEntity() != null && citizensNPC.getEntity().getLocation() != null) {
                if (citizensNPC.getEntity().getLocation().distance(loc) < 1) {
                    toRemove.add(citizensNPC);
                }
            }
        });

        for (NPC remove : toRemove) {
            try { remove.despawn(); } catch (Exception e) {}
            try { remove.destroy(); } catch (Exception e) {}
        }

        if (lines.length == 1) {
            this.npc = CitizensAPI.getNPCRegistry().createNPC(type, ChatColor.translateAlternateColorCodes('&', lines[0]));
            this.hologram = null;
        }
        else {
            this.npc = CitizensAPI.getNPCRegistry().createNPC(type, "");
            this.hologram = HologramsAPI.createHologram(Core.getInstance(), loc.clone().add(0, 2.575, 0));
            for (String s : lines)
                this.hologram.appendTextLine(ChatColor.translateAlternateColorCodes('&', s));
        }

        this.startingLoc = loc.clone();
        this.id = this.npc.getId();
        this.npc.spawn(loc);

        generateNewNPC();
        Core.getNPCManager().registerCoreNPC(this);
    }

    public Location getStartingLoc() {
        return this.startingLoc;
    }

    public int getID() {
        return this.id;
    }

    /**
     * @apiNote delete the dispStand and NPC (not clear from MySQL)
     */
    public void delete() {
        if (hologram != null)
            hologram.delete();

        if (this.npc != null) {
            this.npc.despawn(DespawnReason.PLUGIN);
            this.npc.destroy();
            
            if (npc != null && npc.getEntity() != null){
            	this.npc.getEntity().remove();
            }
        }
    }

    /**
     * @param damageable whether or not the entity can be damaged
     * @apiNote default state is false
     */
    protected void setDamageable(boolean damageable) {
        this.npc.data().setPersistent(NPC.DEFAULT_PROTECTED_METADATA, !damageable);
    }

    /**
     * @param targetable whether or not other mobs will target this entity.
     * @apiNote default is true
     */
    protected void setTargetable(boolean targetable) {
        this.npc.data().setPersistent(NPC.TARGETABLE_METADATA, targetable);
    }

    /**
     * @param layer    the layer that will be set on the entity.
     * @param turnedOn whether or not the layer is turned on or off.
     * @return if the method was successful.
     * @apiNote default all true.
     */
    protected boolean setSkinLayer(SkinLayers.Layer layer, boolean turnedOn) {
        if (!(this.npc.getEntity() instanceof Player))
            return false;

        SkinLayers trait = this.npc.getTrait(SkinLayers.class);
        trait.setVisible(layer, turnedOn);
        return true;
    }


    /**
     * @param option the path finding option. (AVOID_WATER = boolean, others are integers)
     * @param o      the object of the option (boolean or int)
     */
    protected void setPathfindingOption(PathfindOption option, Object o) {
        switch (option) {
            case AVOID_WATER:
                npc.getNavigator().getDefaultParameters().avoidWater((boolean) o);
                break;
            case ATTACK_RANGE:
                npc.getNavigator().getDefaultParameters().attackRange((int) o);
                break;
            case DISTANCE_MARGIN:
                npc.getNavigator().getDefaultParameters().distanceMargin((int) o);
                break;
            case STATIONARY_TICKS:
                npc.getNavigator().getDefaultParameters().stationaryTicks((int) o);
                break;
        }
    }

    /**
     * @param passive if the player should (true) or not attack nearby entities.
     * @apiNote default stat is true
     */
    protected void setPassive(boolean passive) {
        this.npc.data().setPersistent(NPC.DAMAGE_OTHERS_METADATA, !passive);
    }

    /**
     * @param lookClose whether or not the entity should look at nearby players.
     * @return true if changes were made.
     * @apiNote default state is false
     */
    protected boolean setLookClose(boolean lookClose) {
        LookClose trait = npc.getTrait(LookClose.class);

        while (trait.toggle() != lookClose)
            trait.toggle();
        return true;
    }

    /**
     * @param gravity whether or not the entity should have gravity.
     * @return true if changes were made to the entity.
     * @apiNote default state is true
     */
    protected boolean setGravity(boolean gravity) {
        Gravity trait = this.npc.getTrait(Gravity.class);

        if (trait.hasGravity() == gravity)
            return false;
        trait.toggle();
        return true;
    }

    /**
     * @param color the color that you want to surround the entity (player).
     * @return true if the method was successful.
     * @apiNote default state is not glowing
     * @deprecated seems not to be able to change color from white.
     */
    @Deprecated
	protected boolean setGlowing(ChatColor color) {
        if (!(this.npc.getEntity() instanceof Player))
            return false;
        if (color == null) {
            this.npc.data().remove(NPC.GLOWING_COLOR_METADATA);
            this.npc.data().remove(NPC.GLOWING_METADATA);
            return true;
        }

        this.npc.data().setPersistent(NPC.GLOWING_METADATA, true);
        this.npc.data().setPersistent(NPC.GLOWING_COLOR_METADATA, color.name());

        return true;
    }

    /**
     * @param rideable whether or not the npc is rideable
     * @apiNote default state is false.
     */
    protected void setRideable(boolean rideable) {
        if (!this.npc.hasTrait(Controllable.class))
            npc.addTrait(Controllable.class);
        Controllable trait = this.npc.getTrait(Controllable.class);

        trait.setEnabled(rideable);
    }

    /**
     * @param collideable whether or not the entity is collideable
     * @apiNote default state is false.
     * @apiNote sets the collidability of the entity
     */
    protected void setCollideable(boolean collideable) {
        this.npc.data().setPersistent(NPC.COLLIDABLE_METADATA, collideable);
    }

    /**
     * @param age          the tick age of the entity,  0 = adult, -24000 = baby.
     * @param passiveAging whether or not the npc will age over time (true = it will age).
     * @return true if it was successful.
     * @apiNote default state is adult (0).
     * @apiNote the npc must implement Ageable or be a Zombie
     */
    protected boolean setAge(int age, boolean passiveAging) {
        if (!this.npc.isSpawned() || (!(npc.getEntity() instanceof Ageable) && !(npc.getEntity() instanceof Zombie)))
            return false;

        Age trait = this.npc.getTrait(Age.class);
        trait.setAge(age);

        while (trait.toggle() == passiveAging)
            trait.toggle();//there is no set state method :(
        return true;
    }

    /**
     * @param textureValue     the base64 encoded value of the image.
     * @param textureSignature the base64 encoded signature of the image.
     * @apiNote allows the ability to set a player's skin to one that is not linked to a specific player. Use mineskin.org
     */
    protected boolean setSkin(String textureValue, String textureSignature) {
        if (this.npc.getEntity().getType() != EntityType.PLAYER) {
            return false;
        }
        ((SkinnableEntity) getNPC().getEntity()).getProfile().getProperties().removeAll("textures");
        ((SkinnableEntity) getNPC().getEntity()).getProfile().getProperties().put("textures", new Property("textures", textureValue, textureSignature));
        return true;
    }

    /**
     * @param playerName the name of the player whose skin you want to set the NPC to
     * @param snapshot   if the skin should update if the player selected changes their skin (snapshot = true means no update)
     * @apiNote the entity type has to be implementing SkinnableEntity (ex. player)
     */
    protected boolean setSkin(String playerName, boolean snapshot) {
        if (this.type != EntityType.PLAYER) {
            ServerUtil.debug("not skinnable");
            return false;
        }
        this.npc.data().setPersistent(NPC.PLAYER_SKIN_UUID_METADATA, playerName);
        this.npc.data().setPersistent(NPC.PLAYER_SKIN_USE_LATEST, !snapshot);

        if (this.npc.isSpawned())
            ((SkinnableEntity) this.npc.getEntity()).setSkinName(playerName);
        return true;
    }

    /**
     * @apiNote used to add the clothes and such of the npc;
     */
    protected abstract void generateNewNPC();

    /**
     * @return the npc entity of this class.
     */
    public NPC getNPC() {
        return npc;
    }

}
