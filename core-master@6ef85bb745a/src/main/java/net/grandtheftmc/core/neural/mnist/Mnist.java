package net.grandtheftmc.core.neural.mnist;

import net.grandtheftmc.core.neural.NeuralNetTools;
import net.grandtheftmc.core.neural.NeuralNetwork;
import net.grandtheftmc.core.neural.NeuralTrainSet;

import java.io.File;

/**
 * Created by Luecx on 10.08.2017.
 */
public class Mnist {

    public static void main(String[] args) {
//        NeuralNetwork network = new NeuralNetwork(784, 50, 10);
//        trainData(network, createTrainSet(0,100), 1000, 100, 100, "res/mnist1.txt");

        NeuralNetwork network = NeuralNetwork.load("res/mnist1.txt");
        testTrainSet(network, createTrainSet(0,50000), 1000);
    }

    public static NeuralTrainSet createTrainSet(int start, int end) {
        NeuralTrainSet set = new NeuralTrainSet(28 * 28, 10);
        try {
            String path = new File("").getAbsolutePath();
            MnistImageFile m = new MnistImageFile(path + "/res/trainImage.idx3-ubyte", "rw");
            MnistLabelFile l = new MnistLabelFile(path + "/res/trainLabel.idx1-ubyte", "rw");
            for(int i = start; i <= end; i++) {
                if(i % 100 ==  0){
                    System.out.println("prepared: " + i);
                }

                double[] input = new double[28 * 28];
                double[] output = new double[10];

                output[l.readLabel()] = 1d;
                for(int j = 0; j < 28*28; j++){
                    input[j] = (double)m.read() / (double)256;
                }

                set.addData(input, output);
                m.next();
                l.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

         return set;
    }

    public static void trainData(NeuralNetwork net,NeuralTrainSet set, int epochs, int loops, int batch_size, String outputFile) {
        for(int e = 0; e < epochs;e++) {
            net.train(set, loops, batch_size);
            System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>   "+ e+ "   <<<<<<<<<<<<<<<<<<<<<<<<<<");

            net.save(outputFile);
        }
    }

    public static void testTrainSet(NeuralNetwork net, NeuralTrainSet set, int printSteps) {
        int correct = 0;
        for(int i = 0; i < set.size(); i++) {

            double highest = NeuralNetTools.indexOfHighestValue(net.calculate(set.getInput(i)));
            double actualHighest = NeuralNetTools.indexOfHighestValue(set.getOutput(i));
            if(highest == actualHighest) {

                correct ++ ;
            }
            if(i % printSteps == 0) {
                System.out.println(i + ": " + (double)correct / (double) (i + 1));
            }
        }
        System.out.println("Testing finished, RESULT: " + correct + " / " + set.size()+ "  -> " + (double)correct / (double)set.size() +" %");
    }
}
