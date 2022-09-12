package net.grandtheftmc.core.neural;

import java.util.ArrayList;
import java.util.Arrays;

public class NeuralTrainSet {

    public final int INPUT_SIZE;
    public final int OUTPUT_SIZE;

    //double[][] <- index1: 0 = input, 1 = output || index2: index of element
    private ArrayList<double[][]> data = new ArrayList<>();

    public NeuralTrainSet(int INPUT_SIZE, int OUTPUT_SIZE) {
        this.INPUT_SIZE = INPUT_SIZE;
        this.OUTPUT_SIZE = OUTPUT_SIZE;
    }

    public void addData(double[] in, double[] expected) {
        if (in.length != INPUT_SIZE || expected.length != OUTPUT_SIZE) return;
        data.add(new double[][]{in, expected});
    }

    public NeuralTrainSet extractBatch(int size) {
        if (size > 0 && size <= this.size()) {
            NeuralTrainSet set = new NeuralTrainSet(INPUT_SIZE, OUTPUT_SIZE);
            Integer[] ids = NeuralNetTools.randomValues(0, this.size() - 1, size);
            for (Integer i : ids) set.addData(this.getInput(i), this.getOutput(i));
            return set;
        } else return this;
    }

    public static void main(String[] args) {
        NeuralTrainSet set = new NeuralTrainSet(3, 2);

        for (int i = 0; i < 8; i++) {
            double[] a = new double[3];
            double[] b = new double[2];
            for (int k = 0; k < 3; k++) {
                a[k] = (double) ((int) (Math.random() * 10)) / (double) 10;
                if (k < 2) b[k] = (double) ((int) (Math.random() * 10)) / (double) 10;
            }
            set.addData(a, b);
        }

        System.out.println(set);
        System.out.println(set.extractBatch(3));
    }

    public String toString() {
        StringBuilder s = new StringBuilder("TrainSet [" + INPUT_SIZE + " ; " + OUTPUT_SIZE + "]\n");
        int index = 0;
        for (double[][] r : data) {
            s.append(index)
                    .append(": ")
                    .append(Arrays.toString(r[0]))
                    .append(" | ")
                    .append(Arrays.toString(r[1]))
                    .append("\n");
            index++;
        }
        return s.toString();
    }

    public int size() {
        return data.size();
    }

    public double[] getInput(int index) {
        return index >= 0 && index < size() ? data.get(index)[0] : null;
//        if (index >= 0 && index < size())
//            return data.get(index)[0];
//        else return null;
    }

    public double[] getOutput(int index) {
        return index >= 0 && index < size() ? data.get(index)[1] : null;
//        if (index >= 0 && index < size())
//            return data.get(index)[1];
//        else return null;
    }

    public int getINPUT_SIZE() {
        return INPUT_SIZE;
    }

    public int getOUTPUT_SIZE() {
        return OUTPUT_SIZE;
    }
}
