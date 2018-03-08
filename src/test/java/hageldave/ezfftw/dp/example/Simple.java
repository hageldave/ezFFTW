//#FLOATGEN_SKIPFILE
package hageldave.ezfftw.dp.example;

import hageldave.ezfftw.dp.FFT;

public class Simple {

	public static void main(String[] args) {
		sinePlusCosine();
	}
	
	static void sinePlusCosine(){
		int sampleRate = 16;
		double second = 2*Math.PI; // interval of one second
		// create samples
		double[] samples = new double[sampleRate];
		for(int i = 0; i < sampleRate; i++){
			samples[i] = Math.sin(i*second/sampleRate);
			samples[i]+= Math.cos(i*second/sampleRate);
		}
		// execute fft
		double[] realPart = new double[sampleRate];
		double[] imagPart = new double[sampleRate];
		FFT.fft(samples, realPart,imagPart, sampleRate);
		// print result (omit conjugated complex results)
		for(int i = 0; i < 1+sampleRate/2; i++) {
			System.out.format("%dHz | % .2f%+.2fi%n",i, realPart[i], imagPart[i]);
		}
	}
	
}
