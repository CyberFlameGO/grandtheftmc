package net.grandtheftmc.core.editmode;

public class WorldConfig {


    private String world;
    private boolean editMode;
    private RestrictedType type;
    private String restricted;

    public WorldConfig(String world, boolean editMode) {
        this.world = world;
        this.editMode = editMode;
        this.type = RestrictedType.NONE;
    }

    public WorldConfig(String world, boolean editMode, RestrictedType type, String restricted) {
        this.world = world;
        this.editMode = editMode;
        this.type = type;
        this.restricted = restricted;
    }

    public String getWorld() {
        return this.world;
    }

    public void setWorld(String world) {
        this.world = world;
    }

    public boolean isEditMode() {
        return this.editMode;
    }

    public void setEditMode(boolean editMode) {
        this.editMode = editMode;
    }

    public RestrictedType getType() {
        return this.type;
    }

    public void setType(RestrictedType type) {
        this.type = type;
    }

    public String getRestricted() {
        return this.restricted;
    }

    public void setRestricted(String restricted) {
        this.restricted = restricted;
    }

    public boolean isRestricted() {
        return this.type != RestrictedType.NONE;
    }

    public enum RestrictedType {
        USERRANK,
        RESTRICTED,
        GAMERANK,
        NONE;
    }
}
