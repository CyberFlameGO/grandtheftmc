package net.grandtheftmc.gtm.gang.relation;

public enum RelationType {

    NEUTRAL(null, null),
    ALLY("ALLY", "Ally"),
    ENEMY("ENEMY", "Enemy"),
    ;

    private final String key;
    private final String name;

    RelationType(String key, String name) {
        this.key = key;
        this.name = name;
    }

    public String getKey() {
        return key;
    }

    public String getName() {
        return name;
    }

    public static RelationType getByKey(String str) {
        for(RelationType type : values()) {
            if (type.key == null) continue;
            if (type.key.equalsIgnoreCase(str)) {
                return type;
            }
        }
        return NEUTRAL;
    }
}
