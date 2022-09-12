package net.grandtheftmc.vice.users.storage;

/**
 * Created by Timothy Lampen on 8/3/2017.
 */
public enum BooleanStorageType {
    SEND_AWAY(false),
    TPA_HERE(false),
    BACK_WITHDRAWING(false),
    BANK_DEPOSITING(false),
    BUYING_LOTTERY_TICKETS(false),
    ADDING_LOOTCRATE(false),
    REMOVING_LOOTCRATE(false),
    CHECKING_LOOTCRATE(false),
    RESTOCKING_LOOTCRATE(false),
    BRIBING(false),
    KICKED(false),
    HAS_UPDATED(false),
    BACKPACK_OPEN(false),
    USED_LOGOUT(false);


    private final boolean defaultValue;
    BooleanStorageType(boolean defaultValue){
        this.defaultValue = defaultValue;
    }

    public boolean getDefaultValue(){
        return defaultValue;
    }
}
