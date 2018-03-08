//#FLOATGEN_SKIPFILE
package hageldave.ezfftw.dp.example;

import hageldave.ezfftw.dp.FFT;

public class Simple {

	public static void main(String[] args) {
		sinePlusCosine();
	}
	
	static void sinePlusCosine(){
		int numSamples = 16;
		double second = 2*Math.PI; // interval of one second
		// create samples
		double[] samples = new double[numSamples];
		for(int i = 0; i < numSamples; i++){
			samples[i] = Math.sin(i*second/numSamples);
			samples[i]+= Math.cos(i*second/numSamples);
		}
		// execute fft
		double[] realPart = new double[numSamples];
		double[] imagPart = new double[numSamples];
		FFT.fft(samples, realPart,imagPart, numSamples);
		// print result (omit conjugated complex results)
		for(int i = 0; i < 1+numSamples/2; i++) {
			System.out.format("%dHz | % .2f%+.2fi%n",i, realPart[i], imagPart[i]);
		}
	}
	
}
