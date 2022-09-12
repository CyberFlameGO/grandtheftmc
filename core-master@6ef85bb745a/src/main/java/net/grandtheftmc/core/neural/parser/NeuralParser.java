package net.grandtheftmc.core.neural.parser;

import java.io.*;

public class NeuralParser {

    private NeuralNode mainContent;
    private String fileName;

    public void load(String file) {
        this.fileName = file;
        StringBuilder data = new StringBuilder("<mainContent>");
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null)
                if (!line.trim().equals("")) data.append(line);

            data.append("</mainContent>");
            this.generateNodes(data.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void create(String fileName) {
        this.fileName = fileName;
        this.mainContent = new NeuralNode("mainContent");
    }

    private void generateNodes(String data) {
        mainContent = NeuralNode.parse(data);
    }

    public NeuralNode getContent() {
        return mainContent;
    }

    public String getValue(String[] keys, String attr) {
        NeuralNode curr = mainContent;
        for (String k : keys) {
            curr = curr.getChild(k);
            if (curr == null) {
                return null;
            }
        }
        return curr.getAttribute(attr).getValue();
    }

    public void setValue(String[] keys, String attr, String value) {
        NeuralNode curr = mainContent;
        for (String k : keys) {
            curr = curr.getChild(k);
            if (curr == null) return;
        }
        if (curr.containsAttribute(new NeuralAttribute(attr, "")))
            curr.setAttribute(attr, value);
        else curr.addAttribute(attr, value);
    }

    public void addNode(String[] keys, NeuralNode n) {
        NeuralNode curr = mainContent;
        for (String k : keys) {
            curr = curr.getChild(k);
            if (curr == null) return;
        }
        curr.addChild(n);
    }

    public void addNode(String[] keys, String node) {
        NeuralNode curr = mainContent;
        for (String k : keys) {
            curr = curr.getChild(k);
            if (curr == null) return;
        }
        curr.addChild(new NeuralNode(node));
    }

    public void close() {
        try (PrintWriter out = new PrintWriter(fileName)) {
            for (NeuralNode n : mainContent.getChilds())
                out.print(n.toParse(0));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
