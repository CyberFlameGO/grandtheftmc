package net.grandtheftmc.core.alert;

import net.grandtheftmc.core.alert.type.AlertShowType;
import net.grandtheftmc.core.alert.type.AlertType;

import java.sql.Timestamp;

/**
 * Created by Luke Bingham on 10/09/2017.
 */
public class AlertEntry implements Alert {

    private int uniqueId;
    private final String name, imageUrl;
    private final AlertShowType showType;
    private final AlertType alertType;
    private final Timestamp start, end;

    private boolean disabled;
    private String link, player, description = "none";
    private String addon;

    public AlertEntry(String name, String imageUrl, AlertShowType showType, AlertType alertType, Timestamp start, Timestamp end, boolean disabled) {
        this.name = name;
        this.imageUrl = imageUrl;
        this.showType = showType;
        this.alertType = alertType;
        this.start = start;
        this.end = end;
        this.disabled = disabled;
    }

    public AlertEntry(String name, String imageUrl, AlertShowType showType, AlertType alertType, String link, Timestamp start, Timestamp end, boolean disabled) {
        this(name, imageUrl, showType, alertType, start, end, disabled);
        this.link = link;
    }

    @Override
    public int getUniqueIdentifier() {
        return this.uniqueId;
    }

    @Override
    public void setUniqueIdentifier(int id) {
        this.uniqueId = id;
    }

    /**
     * Get the player who created this Alert.
     *
     * @return
     */
    @Override
    public String getPlayer() {
        return this.player;
    }

    /**
     * Set the player who created this Alert.
     *
     * @param name
     * @return
     */
    @Override
    public void setPlayer(String name) {
        this.player = name;
    }

    /**
     * Get the name of the Alert.
     *
     * @return
     */
    @Override
    public String getName() {
        return this.name;
    }

    /**
     * Get the Alert description.
     * If POLL, This will be the 'question'.
     *
     * @return
     */
    @Override
    public String getDescription() {
        return this.description;
    }

    /**
     * Get the Alert description.
     * If POLL, This will be the 'question'.
     *
     * @param desc
     */
    @Override
    public void setDescription(String desc) {
        this.description = desc;
    }

    /**
     * Get the url of the image shown on the map.
     *
     * @return
     */
    @Override
    public String getImageUrl() {
        return this.imageUrl;
    }

    /**
     * Get the ShowType of this Alert.
     *
     * @return
     */
    @Override
    public AlertShowType getShowType() {
        return this.showType;
    }

    /**
     * Get the AlertType of this specific Alert.
     *
     * @return
     */
    @Override
    public AlertType getAlertType() {
        return this.alertType;
    }

    /**
     * Get the Link that the Alert shows when interacted with.
     *
     * @return
     */
    @Override
    public String getLink() {
        return this.link;
    }

    /**
     * This Timestamp is when the Alert has or should begin.
     *
     * @return
     */
    @Override
    public Timestamp getStart() {
        return this.start;
    }

    /**
     * This Timestamp is when the Alert will end.
     *
     * @return
     */
    @Override
    public Timestamp getEnd() {
        return this.end;
    }

    /**
     * Is the Alert disabled or not started yet.
     *
     * @return
     */
    @Override
    public boolean isDisabled() {
        return this.disabled;
    }

    /**
     * Is the current time greater than the ending Timestamp.
     *
     * @return
     */
    @Override
    public boolean hasExpired() {
        return System.currentTimeMillis() > this.end.getTime();
    }

    @Override
    public boolean hasStarted() {
        return System.currentTimeMillis() >= this.start.getTime();
    }

    @Override
    public boolean isInProgress() {
        return !hasExpired() && hasStarted();
    }
}
