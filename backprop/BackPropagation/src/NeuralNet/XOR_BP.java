package NeuralNet;

import java.io.*;
import java.util.Random;

public class XOR_BP {
	static int NUM_INPUTS = 3;
	static int NUM_EPOCH = 0;
	static int NUM_PATTERNS = 4;
	static int NUM_OUTPUTS = 1;
	public static double trainInputs[][] = new double[NUM_PATTERNS][NUM_INPUTS];
	public static double trainOutput[] = new double[NUM_PATTERNS]; // "Actual"
																	// output
																	// values.
	public static double errOut[] = new double[NUM_EPOCH];
	public static double errorOut;
	public static double outNeuron[] = new double[NUM_OUTPUTS];
	private static double RMSerror = 1.0;
	static boolean bipolar_flag = true; // set this flag for binary/bipolar

	private static void init() {
		trainInputs[0][0] = 1;
		trainInputs[0][1] = -1;
		trainInputs[0][2] = 1; // Bias
		trainOutput[0] = 1;

		trainInputs[1][0] = -1;
		trainInputs[1][1] = 1;
		trainInputs[1][2] = 1; // Bias
		trainOutput[1] = 1;

		trainInputs[2][0] = 1;
		trainInputs[2][1] = 1;
		trainInputs[2][2] = 1; // Bias
		trainOutput[2] = -1;

		trainInputs[3][0] = -1;
		trainInputs[3][1] = -1;
		trainInputs[3][2] = 1; // Bias
		trainOutput[3] = -1;
	}

	private static void initBinaryData() {
		trainInputs[0][0] = 1;
		trainInputs[0][1] = 0;
		trainInputs[0][2] = 1; // Bias
		trainOutput[0] = 1;

		trainInputs[1][0] = 0;
		trainInputs[1][1] = 1;
		trainInputs[1][2] = 1; // Bias
		trainOutput[1] = 1;

		trainInputs[2][0] = 1;
		trainInputs[2][1] = 1;
		trainInputs[2][2] = 1; // Bias
		trainOutput[2] = 0;

		trainInputs[3][0] = 0;
		trainInputs[3][1] = 0;
		trainInputs[3][2] = 1; // Bias
		trainOutput[3] = 0;
	}

	public static double squash(double x) {
		if (!bipolar_flag) {
			if (x > 0.5) {
				x = 1.0;
			} else if (x < 0.5) {
				x = 0.0;
			}
		} else {
			if (x > 0) {
				x = 1.0;
			} else if (x < 0) {
				x = -1.0;
			}
		}
		return x;
	}

	// Main function
	public static void main(final String[] args) throws IOException {
		int patNum = 0;
		double errorValue = 1; // initialize errorValue high
		double err = 0.0;
		final double errthresh = 0.02;
		String content_in = System.getProperty("line.separator");
		final int num_trials = 15;
		final double arrayEpoch[] = new double[num_trials];
		int wrong = 0, right = 0;

		//// MAIN FUNCTION ///
		for (int t = 0; t < num_trials; t++) {
			final NeuralNetInterface nn_if = new NeuralNet(2, // num inputs
					4, // num hidden (without bias)
					0.2, // learning rate
					0.0, // momentum
					-1, // lower bound
					1, // upper bound
					bipolar_flag, // bipolar flag
					true // Nguyen-Widrow
			);

			final File file = new File("C://Users/vpwong/Google Drive/backprop/data" + t + ".xls");
			if (!file.exists()) {
				file.createNewFile();
			}

			final FileWriter fw = new FileWriter(file.getAbsoluteFile());
			final BufferedWriter bw = new BufferedWriter(fw);

			NUM_EPOCH = 0; // reset NUM_EPOCH
			nn_if.zeroWeights();
			nn_if.initializeWeights();

			if (bipolar_flag)
				init();
			else
				initBinaryData();

			// Train the network.
			errorValue = 1;
			while (errorValue >= errthresh) {
				errorValue = 0;
				for (int k = 0; k < NUM_PATTERNS; k++) {
					patNum = new Random().nextInt(4);
					err = nn_if.train(trainInputs[patNum], trainOutput[patNum]);
					errorValue += Math.pow(err, 2);
				}
				errorValue /= 2; // total error calculation
				NUM_EPOCH++;
				RMSerror = Math.sqrt(errorValue);
				System.out.println("epoch = " + NUM_EPOCH + " Error = " + errorValue + " rms err = " + RMSerror);
				content_in = NUM_EPOCH + "," + errorValue;
				try {
					bw.write(content_in + "\n");
				} catch (final IOException e) {
					e.printStackTrace();
				}
			}
			nn_if.load(file.getName());
			arrayEpoch[t] = NUM_EPOCH;
			bw.close();
			for (int i = 0; i < NUM_PATTERNS; i++) {
				final double final_outNeuron = nn_if.outputFor(trainInputs[i]);
				final double ceil_neuron = squash(final_outNeuron);
				System.out.println(
						"pat = " + (i + 1) + " actual = " + trainOutput[i] + " neural model = " + final_outNeuron);
				if (ceil_neuron != trainOutput[i]) {
					wrong++;
				} else {
					right++;
				}
				content_in = "pat = " + (i + 1) + " actual = " + trainOutput[i] + " neural model = " + final_outNeuron;
			}
		}
		final Statistics stat = new Statistics(arrayEpoch);
		System.out.println("Mean Num Epoch = " + stat.getMean());
		System.out.println("Standard Deviation Num Epoch = " + stat.getStdDev());
		System.out.println("Num Epoch variance " + stat.getVariance());
		System.out.println("accuracy percentage " + (right * 100) / (wrong + right));
		System.out.println("Median Epochs " + stat.median());
		return;
	}

}