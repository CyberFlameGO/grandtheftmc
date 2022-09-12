package net.grandtheftmc.core.neural;

import net.grandtheftmc.core.neural.parser.NeuralAttribute;
import net.grandtheftmc.core.neural.parser.NeuralNode;
import net.grandtheftmc.core.neural.parser.NeuralParser;
import net.grandtheftmc.core.neural.parser.NeuralParserTools;

import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NeuralNetwork {

    private double[][] output;
    private double[][][] weights;
    private double[][] bias;

    private double[][] errorSignal;
    private double[][] outputDerivative;

    /**
     * Neurons in a layer
     */
    private final int[] layerSizes;

    private final int inputSize, outputSize, networkSize;

    public NeuralNetwork(int... layerSizes) {
        this.layerSizes = layerSizes;
        this.inputSize = layerSizes[0];
        this.networkSize = layerSizes.length;
        this.outputSize = layerSizes[this.networkSize - 1];

        this.output = new double[this.networkSize][];
        this.weights = new double[this.networkSize][][];
        this.bias = new double[this.networkSize][];

        this.errorSignal = new double[this.networkSize][];
        this.outputDerivative = new double[this.networkSize][];

        for (int i = 0; i < this.networkSize; i++) {
            this.output[i] = new double[layerSizes[i]];
            this.errorSignal[i] = new double[layerSizes[i]];
            this.outputDerivative[i] = new double[layerSizes[i]];
            this.bias[i] = NeuralNetTools.createRandomArray(layerSizes[i], -0.5, 0.7);
            if (i > 0) this.weights[i] = NeuralNetTools.createRandomArray(layerSizes[i], layerSizes[i - 1], -1, 1);
        }
    }

    public double[] calculate(double... input) {
        if (input.length != this.inputSize) return null;
        this.output[0] = input;
        for (int layer = 1; layer < this.networkSize; layer++) {
            for (int neuron = 0; neuron < this.layerSizes[layer]; neuron++) {
                double x = this.bias[layer][neuron];
                for (int prevNeuron = 0; prevNeuron < this.layerSizes[layer - 1]; prevNeuron++)
                    x += this.output[layer - 1][prevNeuron] * this.weights[layer][neuron][prevNeuron];
                this.output[layer][neuron] = sigmoid(x);
                this.outputDerivative[layer][neuron] = this.output[layer][neuron] * (1 - this.output[layer][neuron]);
            }
        }
        return this.output[this.networkSize - 1];
    }

    public void train(NeuralTrainSet set, int loops, int batchSize) {
        if (set.INPUT_SIZE != this.inputSize || set.OUTPUT_SIZE != this.outputSize) return;
        for (int i = 0; i < loops; i++) {
            NeuralTrainSet batch = set.extractBatch(batchSize);
            for (int x = 0; x < batchSize; x++)
                this.train(batch.getInput(x), batch.getOutput(x), 0.3);
            System.out.println(MSE(batch));
        }
    }

    private double sigmoid(double x) {
        return 1d / (1 + Math.exp(-x));
    }

    public void train(double[] input, double[] target, double eta) {
        if (input.length != this.inputSize || target.length != this.outputSize) return;
        calculate(input);
        backpropError(target);
        updateWeights(eta);
    }

    public void backpropError(double[] target) {
        for (int neuron = 0; neuron < this.layerSizes[this.networkSize - 1]; neuron++)
            this.errorSignal[this.networkSize - 1][neuron] = (this.output[this.networkSize - 1][neuron] - target[neuron]) * this.outputDerivative[this.networkSize - 1][neuron];

        for (int layer = this.networkSize - 2; layer > 0; layer--) {
            for (int neuron = 0; neuron < this.layerSizes[layer]; neuron++) {
                double x = 0;
                for (int nextNeuron = 0; nextNeuron < this.layerSizes[layer + 1]; nextNeuron++)
                    x += this.weights[layer + 1][nextNeuron][neuron] * this.errorSignal[layer + 1][nextNeuron];
                this.errorSignal[layer][neuron] = x * this.outputDerivative[layer][neuron];
            }
        }
    }

    public void updateWeights(double eta) {
        for (int layer = 1; layer < this.networkSize; layer++) {
            for (int neuron = 0; neuron < this.layerSizes[layer]; neuron++) {
                double delta = -eta * this.errorSignal[layer][neuron];
                this.bias[layer][neuron] += delta;

                for (int prevNeuron = 0; prevNeuron < this.layerSizes[layer - 1]; prevNeuron++)
                    this.weights[layer][neuron][prevNeuron] += delta * this.output[layer - 1][prevNeuron];
            }
        }
    }

    public double MSE(double[] input, double[] target) {
        if (input.length != this.inputSize || target.length != this.outputSize) return 0;
        calculate(input);
        double v = 0;
        for (int i = 0; i < target.length; i++)
            v += (target[i] - this.output[this.networkSize - 1][i]) * (target[i] - this.output[this.networkSize - 1][i]);
        return v / (2d * target.length);
    }

    public double MSE(NeuralTrainSet set) {
        double v = 0;
        for (int i = 0; i < set.size(); i++)
            v += MSE(set.getInput(i), set.getOutput(i));
        return v / set.size();
    }

    public static void main1(String[] args) {
        NeuralNetwork network = new NeuralNetwork(4, 3, 3, 2);


//        double[] input = new double[] { 0.1, 0.2, 0.3, 0.4 };
//        double[] target = new double[] { 0.9, 0.1 };
//
//        for(int i = 0; i < 1000; i++) {
//            network.train(input, target, 0.3);
//        }
//
//        System.out.println(Arrays.toString(network.calculate(input)));


        NeuralTrainSet trainSet = new NeuralTrainSet(4, 2);
        trainSet.addData(new double[]{0.1, 0.2, 0.3, 0.4}, new double[]{0.9, 0.1});
        trainSet.addData(new double[]{0.9, 0.8, 0.7, 0.6}, new double[]{0.1, 0.9});
        trainSet.addData(new double[]{0.3, 0.8, 0.1, 0.4}, new double[]{0.3, 0.7});
        trainSet.addData(new double[]{0.9, 0.8, 0.1, 0.2}, new double[]{0.7, 0.3});

        network.train(trainSet, 10000, 4);

        for (int i = 0; i < 4; i++) {
            double[] result = network.calculate(trainSet.getInput(i));
            System.out.println(Arrays.toString(result));
            for (int x = 0; x < result.length; x++) {
                System.out.println("Goal(" + i + "," + x + "): " + trainSet.getOutput(i)[x]);
                System.out.println("Result(" + i + "," + x + "): " + result[x]);
                System.out.println("Percent: " + network.getPercentDifference(trainSet.getOutput(i)[x], result[x]) + "%");
                System.out.println();
            }
            System.out.println();
            System.out.println();
        }
    }

    public static void main(String[] args) {
//        NeuralNetwork network = new NeuralNetwork(4,3,2);
//        network.save("res/test.txt");

        NeuralNetwork network = NeuralNetwork.load("res/test.txt");
        System.out.println(Arrays.toString(network.layerSizes));
    }

    public double getPercentDifference(double goal, double result) {
        return 100 - Math.abs(((goal - result) / goal) * 100d);
    }

    public void save(String fileName) {
        NeuralParser parser = new NeuralParser();
        parser.create(fileName);
        NeuralNode root = parser.getContent();
        NeuralNode network = new NeuralNode("network");
        NeuralNode layers = new NeuralNode("layers");
        network.addAttribute(new NeuralAttribute("sizes", Arrays.toString(this.layerSizes)));
        network.addChild(layers);
        root.addChild(network);

        for (int layer = 1; layer < this.networkSize; layer++) {
            NeuralNode node = new NeuralNode("" + layer);
            layers.addChild(node);
            NeuralNode nodeWeights = new NeuralNode("weights");
            NeuralNode nodeBiases = new NeuralNode("biases");
            node.addChild(nodeWeights);
            node.addChild(nodeBiases);
            nodeBiases.addAttribute("values", Arrays.toString(this.bias[layer]));

            for (int we = 0; we < this.weights[layer].length; we++)
                nodeWeights.addAttribute("" + we, Arrays.toString(this.weights[layer][we]));
        }

        parser.close();
    }

    public static NeuralNetwork load(String fileName) {
        NeuralParser parser = new NeuralParser();
        parser.load(fileName);
        String sizes = parser.getValue(new String[]{"network"}, "sizes");
        int[] si = NeuralParserTools.parseIntArray(sizes);
        NeuralNetwork network = new NeuralNetwork(si);

        for (int i = 1; i < network.networkSize; i++) {
            network.bias[i] = NeuralParserTools.parseDoubleArray(parser.getValue(new String[]{"network", "layers", (i + ""), "biases"}, "values"));

            for (int n = 0; n < network.layerSizes[i]; n++)
                network.weights[i][n] = NeuralParserTools.parseDoubleArray(parser.getValue(new String[]{"network", "layers", (i + ""), "weights"}, "" + n));
        }

        parser.close();
        return network;
    }
}
