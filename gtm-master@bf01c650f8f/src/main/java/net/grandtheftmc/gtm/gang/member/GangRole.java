package net.grandtheftmc.gtm.gang.member;

public enum GangRole {
    MEMBER(1, "member", "Member"),
    TRUSTED(2, "trusted", "Trusted"),

    //Room for 2 more.

    CO_LEADER(5, "coleader", "Co-Leader"),
    LEADER(6, "leader", "Leader"),
    ;

    private final int rankId;
    private final String tag;
    private final String formattedTag;

    GangRole(int rankId, String tag, String formattedTag) {
        this.rankId = rankId;
        this.tag = tag;
        this.formattedTag = formattedTag;
    }

    public int getRankId() {
        return rankId;
    }

    public String getTag() {
        return tag;
    }

    public String getFormattedTag() {
        return formattedTag;
    }

    public static GangRole getById(int id) {
        for(GangRole role : values())
            if(role.rankId == id)
                return role;
        return MEMBER;
    }
}
