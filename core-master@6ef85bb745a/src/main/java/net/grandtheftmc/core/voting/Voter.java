package net.grandtheftmc.core.voting;

public class Voter {

    private final String name;
    private int votes;
    private long lastVote = -1;

    public Voter(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public int getVotes() {
        return this.votes;
    }

    public void addVote() {
        this.votes++;
        this.lastVote = System.currentTimeMillis();
    }

    public void setVotes(int i) {
        this.votes = i;
    }

//    public long getLastVote() {
//        return this.lastVote;
//    }
}
