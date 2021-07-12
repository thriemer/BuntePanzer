package steamTanks.networkTesting;

import org.deeplearning4j.datasets.iterator.impl.EmnistDataSetIterator;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.nd4j.evaluation.classification.Evaluation;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.learning.config.Adam;
import org.nd4j.linalg.lossfunctions.LossFunctions;

import java.io.IOException;

public class MNist {

    public static void main(String[] args) throws IOException {
        int batchSize = 128;
        EmnistDataSetIterator.Set s = EmnistDataSetIterator.Set.BALANCED;
        EmnistDataSetIterator emnistTrain = new EmnistDataSetIterator(s, batchSize, true);
        EmnistDataSetIterator emnistTest = new EmnistDataSetIterator(s, batchSize, false);
        int outputNum = EmnistDataSetIterator.numLabels(s);
        int rndSeed = 123;
        int inputSize = 28;
        var conf = new NeuralNetConfiguration.Builder().seed(rndSeed).updater(new Adam()).l2(1e-4).list().
                layer(new DenseLayer.Builder().nIn(inputSize * inputSize).nOut(1000).activation(Activation.RELU).weightInit(WeightInit.XAVIER).build()).
                layer(new OutputLayer.Builder(LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD).nIn(1000).nOut(outputNum).activation(Activation.SOFTMAX).weightInit(WeightInit.XAVIER).build()).build();
        MultiLayerNetwork mln = new MultiLayerNetwork(conf);
        mln.init();
        int eachIterations = 10;
        mln.addListeners(new ScoreIterationListener(eachIterations));
        mln.fit(emnistTrain,4);
        Evaluation eval = mln.evaluate(emnistTest);
        System.out.println(eval);
    }

}
