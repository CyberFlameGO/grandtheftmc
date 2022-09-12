package net.grandtheftmc.core.anticheat.check;

public class CheatType {

    private final int identifier;
    private final String name;
    private final TriggerType triggerType;
    private final Type type;

    public CheatType(int identifier, String name, TriggerType triggerType, Type type) {
        this.identifier = identifier;
        this.name = name;
        this.triggerType = triggerType;
        this.type = type;
    }

    public int getIdentifier() {
        return identifier;
    }

    public String getName() {
        return name;
    }

    public Type getType() {
        return type;
    }

    public TriggerType getTriggerType() {
        return triggerType;
    }

    private static interface ITriggerType {}

    public static class Movement implements ITriggerType {
        public static final CheatType FLIGHT = new CheatType(1, "Flight", TriggerType.MOVEMENT, Type.FLIGHT);
        public static final CheatType SPEED = new CheatType(2, "Speed", TriggerType.MOVEMENT, Type.SPEED);
        public static final CheatType JESUS = new CheatType(3, "Jesus", TriggerType.MOVEMENT, Type.JESUS);
    }

    public static class Combat implements ITriggerType {

    }

    public enum TriggerType {
        MOVEMENT,
        COMBAT,
        ;
    }

    public enum Type {
        FLIGHT(Movement.class),
        SPEED(Movement.class),
        JESUS(Movement.class),
        ;

        private Class<? extends ITriggerType> trigger;

        Type(Class<? extends ITriggerType> trigger) {
            this.trigger = trigger;
        }
    }
}
