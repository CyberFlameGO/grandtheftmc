package net.grandtheftmc.core.neural.parser;

import java.util.ArrayList;
import java.util.Objects;


public class NeuralNode {

    private String name;
    private ArrayList<NeuralNode> childs = new ArrayList<>();
    private ArrayList<NeuralAttribute> attributes = new ArrayList<>();

    public NeuralNode(String name) {
        this.name = name;
    }

    public boolean addAttribute(NeuralAttribute att) {
        if (att != null) {
            if (this.containsAttribute(att)) return false;
            attributes.add(att);
            return true;
        }
        return false;
    }

    public boolean addAttribute(String att, String value) {
        return this.addAttribute(new NeuralAttribute(att, value));
    }

    public boolean addChild(String name) {
        return this.addChild(new NeuralNode(name));
    }

    public boolean addChild(NeuralNode n) {
        if (n != null) {
            if (this.containsChild(n)) return false;
            childs.add(n);
            return true;
        }
        return false;
    }

    public NeuralNode getChild(String child) {
        for (NeuralNode r : childs) {
            if (r.getName().equals(child)) {
                return r;
            }
        }
        return null;
    }

    public NeuralAttribute getAttribute(String key) {
        for (NeuralAttribute r : attributes) {
            if (r.getName().equals(key)) return r;
        }
        return null;
    }

    public void setAttribute(String att, String value) {
        if (this.getAttribute(att) != null) {
            this.getAttribute(att).setValue(value);
        }
    }

    public boolean removeAttribute(String att) {
        return this.removeAttribute(new NeuralAttribute(att, ""));
    }

    public boolean removeChild(String child) {
        return this.removeChild(new NeuralNode(child));
    }

    public boolean removeAttribute(NeuralAttribute att) {
        int index = 0;
        for (NeuralAttribute r : attributes) {
            if (r.equals(att)) {
                childs.remove(index);
                return true;
            }
            index++;
        }
        return false;
    }

    public boolean removeChild(NeuralNode child) {
        int index = 0;
        for (NeuralNode r : childs) {
            if (child.equals(r)) {
                childs.remove(index);
                return true;
            }
            index++;
        }
        return false;
    }

    public boolean containsAttribute(NeuralAttribute s) {
        for (NeuralAttribute r : attributes) {
            if (r.equalAttribute(s)) {
                return true;
            }
        }
        return false;
    }

    public boolean containsChild(NeuralNode s) {
        for (NeuralNode r : childs) {
            if (s.equals(r)) {
                return true;
            }
        }
        return false;
    }

    public static NeuralNode parse(String c) {
        String[] lines = c.split("[;>]");
        NeuralNode n = new NeuralNode(lines[0].substring(1, lines[0].length()));

        int i = 1;
        while (i < lines.length - 1) {
            String currentLine = lines[i].trim();

            String nodeName = "";
            if (currentLine.startsWith("<")) {
                nodeName = currentLine.substring(1);
                String batch = "";
                while (!lines[i].trim().startsWith("</" + nodeName)) {
                    batch += lines[i].trim() + ";";
                    i++;
                }
                batch += lines[i].trim() + ";";
                n.addChild(NeuralNode.parse(batch));
            } else {
                n.addAttribute(NeuralAttribute.parse(currentLine));
            }
            i++;
        }
        return n;
    }

    public String toParse(int spacesLeft) {
        String res = NeuralParserTools.createSpaces(spacesLeft) + "<" + name + ">" + "\n";

        for (NeuralAttribute at : attributes)
            res += at.toParse(spacesLeft + 4) + "\n";

        for (NeuralNode n : childs)
            res += n.toParse(spacesLeft + 4);

        res += NeuralParserTools.createSpaces(spacesLeft) + "</" + name + ">" + "\n";
        return res;
    }

    public String getName() {
        return this.name;
    }

    public ArrayList<NeuralNode> getChilds() {
        return this.childs;
    }

    public boolean equals(Object o) {
        if (o.getClass() != this.getClass()) return false;
        if (!Objects.equals(((NeuralNode) o).getName(), this.getName())) return false;
        return true;
    }
}
