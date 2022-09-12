package net.grandtheftmc.core.boards;

public class BoardValue {

    private String color;
    private String name;
    private String value;

    public BoardValue(String color, String name, String value) {
        this.color = color;
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public String getValue() {
        return this.value;
    }
    public void setValue(String value) {
        this.value = value;
    }
    public String getColor() {
        return this.color;
    }
    public void setColor(String color) {
        this.color = color;
    }

}
