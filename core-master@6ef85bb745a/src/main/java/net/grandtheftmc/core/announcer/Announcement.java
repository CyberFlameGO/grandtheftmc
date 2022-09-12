package net.grandtheftmc.core.announcer;

public class Announcement {

    private int id;
    private String[] lines;

    public Announcement(int id, String[] lines) {
        this.id = id;
        this.lines = lines;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String[] getLines() {
        return this.lines.clone();
    }

    public void setLines(String[] lines) {
        this.lines = lines;
    }

}
