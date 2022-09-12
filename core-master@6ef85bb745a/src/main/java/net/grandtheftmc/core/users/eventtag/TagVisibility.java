package net.grandtheftmc.core.users.eventtag;

/**
 * Created by Timothy Lampen on 1/7/2018.
 */
public enum TagVisibility {
    EVERYONE(0),
    THOSE_WHO_HAVE_IT_UNLOCKED(1),
    NO_ONE(2);

    private int id;
    TagVisibility(int id) {
        this.id = id;
    }

    public int getID() {
        return this.id;
    }

    public static TagVisibility fromID(int id) {
        for(TagVisibility v : TagVisibility.values())
            if(v.getID()==id)
                return v;
        return null;
    }
}
