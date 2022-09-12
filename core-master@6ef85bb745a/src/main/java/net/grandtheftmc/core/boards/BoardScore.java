package net.grandtheftmc.core.boards;

public class BoardScore {

    private String name;
    private int score;

    public BoardScore(String name, int currentLine) {
        this.name = name;
        this.score = currentLine;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getScore() {
        return this.score;
    }

    public void setScore(int i) {
        this.score = i;
    }

}
