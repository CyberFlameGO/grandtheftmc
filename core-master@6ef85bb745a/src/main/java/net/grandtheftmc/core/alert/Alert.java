package net.grandtheftmc.core.alert;

import net.grandtheftmc.core.alert.type.AlertShowType;
import net.grandtheftmc.core.alert.type.AlertType;

import java.sql.Timestamp;

/**
 * Created by Luke Bingham on 10/09/2017.
 */
public interface Alert {

    int getUniqueIdentifier();
    void setUniqueIdentifier(int id);

    /**
     * Get the player who created this Alert.
     *
     * @return
     */
    String getPlayer();

    /**
     * Set the player who created this Alert.
     *
     * @param name
     */
    void setPlayer(String name);

    /**
     * Get the name of the Alert.
     *
     * @return
     */
    String getName();

    /**
     * Get the Alert description.
     * If POLL, This will be the 'question'.
     *
     * @return
     */
    String getDescription();

    /**
     * Get the Alert description.
     * If POLL, This will be the 'question'.
     *
     * @param desc
     */
    void setDescription(String desc);

    /**
     * Get the url of the image shown on the map.
     *
     * @return
     */
    String getImageUrl();

    /**
     * Get the ShowType of this Alert.
     *
     * @return
     */
    AlertShowType getShowType();

    /**
     * Get the AlertType of this specific Alert.
     *
     * @return
     */
    AlertType getAlertType();

    /**
     * Get the Link that the Alert shows when interacted with.
     *
     * @return
     */
    String getLink();

    /**
     * This Timestamp is when the Alert has or should begin.
     *
     * @return
     */
    Timestamp getStart();

    /**
     * This Timestamp is when the Alert will end.
     *
     * @return
     */
    Timestamp getEnd();

    /**
     * Is the Alert disabled or not started yet.
     *
     * @return
     */
    boolean isDisabled();

    /**
     * Is the current time greater than the ending Timestamp.
     *
     * @return
     */
    boolean hasExpired();

    /**
     * Has the Alert started?
     *
     * @return
     */
    boolean hasStarted();

    boolean isInProgress();
}
