package net.grandtheftmc.gtm.gang.relation;

public interface GangRelation {

    int getRelativeId();

    String getRelativeName();
    void setRelativeName(String name);

    RelationType getRelationType();
    void setRelationType(RelationType relationType);
}
