package net.grandtheftmc.gtm.gang.relation;

public class GTMGangRelation implements GangRelation {

    private final int relativeId;
    private String relativeName;
    private RelationType relationType;

    public GTMGangRelation(int relativeId, RelationType relationType) {
        this.relativeId = relativeId;
        this.relationType = relationType;
    }

    public GTMGangRelation(int relativeId, String relativeName, RelationType relationType) {
        this(relativeId, relationType);
        this.relativeName = relativeName;
    }

    @Override
    public int getRelativeId() {
        return relativeId;
    }

    @Override
    public String getRelativeName() {
        return relativeName;
    }

    @Override
    public void setRelativeName(String relativeName) {
        this.relativeName = relativeName;
    }

    @Override
    public RelationType getRelationType() {
        return relationType;
    }

    @Override
    public void setRelationType(RelationType relationType) {
        this.relationType = relationType;
    }
}
