package net.grandtheftmc.core.neural.parser;

public class NeuralAttribute {

    private String name, value;

    public NeuralAttribute(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((value == null) ? 0 : value.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        NeuralAttribute other = (NeuralAttribute) obj;

        if (name == null) {
            if (other.name != null) return false;
        }
        else if (!name.equals(other.name)) return false;

        if (value == null) {
            if (other.value != null) return false;
        }
        else if (!value.equals(other.value)) return false;
        return true;
    }

    public boolean equalAttribute(NeuralAttribute b) {
        return this.name.equals(b.name);
    }

    public static NeuralAttribute parse(String c) {
        if (c.contains(";")) c = c.substring(0, c.length() - 1);
        String[] split = c.split(":");
        if (split.length != 2) return null;
        return new NeuralAttribute(split[0].trim(), split[1].trim());
    }

    public String toParse(int spacesLeft) {
        return NeuralParserTools.createSpaces(spacesLeft) + name + " : " + value + ";";
    }
}
