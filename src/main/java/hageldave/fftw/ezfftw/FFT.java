package hageldave.fftw.ezfftw;

import hageldave.fftw.ezfftw.samplers.ComplexValuedSampler;
import hageldave.fftw.ezfftw.samplers.RealValuedSampler;
import hageldave.fftw.ezfftw.samplers.RowMajorArraySampler;
import hageldave.fftw.ezfftw.writers.ComplexValuedWriter;
import hageldave.fftw.ezfftw.writers.RealValuedWriter;
import hageldave.fftw.ezfftw.writers.RowMajorArrayWriter;

public class FFT {

	public static void fft(RealValuedSampler realIn, ComplexValuedWriter complexOut, int... dimensions) {
		FFTW_Guru.execute_split_r2c(realIn, complexOut, dimensions);
	}

	public static void fft(ComplexValuedSampler complexIn, ComplexValuedWriter complexOut, int... dimensions) {
		FFTW_Guru.execute_split_c2c(complexIn, complexOut, dimensions);
	}

	public static void ifft(ComplexValuedSampler complexIn, ComplexValuedWriter complexOut, int... dimensions) {
		FFTW_Guru.execute_split_c2c(complexIn.swapRealImaginary(), complexOut.swapRealImaginary(), dimensions);
	}

	public static void ifft(ComplexValuedSampler complexIn, RealValuedWriter realOut, int... dimensions) {
		FFTW_Guru.execute_split_c2r(complexIn, realOut, dimensions);
	}

	public static void fft(double[] realIn, double[] realOut, double[] imagOut, int... dimensions) {
		/* sanity checks */
		Utils.requirePositive(dimensions.length, ()->"Provided dimensions are empty, need to pass at least one.");
		Utils.requirePosititveDimensions(dimensions);
		long numElements = Utils.numElementsFromDimensions(dimensions);
		Utils.sanityCheckArray(realIn,  numElements, "real input");
		Utils.sanityCheckArray(realOut, numElements, "real output");
		Utils.sanityCheckArray(imagOut, numElements, "imaginary output");
		/* start now */
		RowMajorArraySampler rIn = new RowMajorArraySampler(realIn, dimensions);
		RowMajorArrayWriter rOut = new RowMajorArrayWriter(realOut, dimensions);
		RowMajorArrayWriter iOut = new RowMajorArrayWriter(imagOut, dimensions);
		fft(rIn, rOut.addImaginaryComponent(iOut), dimensions);
	}
	
	public static void fft(double[] realIn, double[] imagIn, double[] realOut, double[] imagOut, int... dimensions) {
		/* sanity checks */
		Utils.requirePositive(dimensions.length, ()->"Provided dimensions are empty, need to pass at least one.");
		Utils.requirePosititveDimensions(dimensions);
		long numElements = Utils.numElementsFromDimensions(dimensions);
		Utils.sanityCheckArray(realIn,  numElements, "real input");
		Utils.sanityCheckArray(imagIn,  numElements, "imaginary input");
		Utils.sanityCheckArray(realOut, numElements, "real output");
		Utils.sanityCheckArray(imagOut, numElements, "imaginary output");
		/* start now */
		RowMajorArraySampler rIn = new RowMajorArraySampler(realIn, dimensions);
		RowMajorArraySampler iIn = new RowMajorArraySampler(imagIn, dimensions);
		RowMajorArrayWriter rOut = new RowMajorArrayWriter(realOut, dimensions);
		RowMajorArrayWriter iOut = new RowMajorArrayWriter(imagOut, dimensions);
		fft(rIn.addImaginaryComponent(iIn), rOut.addImaginaryComponent(iOut), dimensions);
	}
	
	public static void ifft(double[] realIn, double[] imagIn, double[] realOut, double[] imagOut, int... dimensions) {
		/* sanity checks */
		Utils.requirePositive(dimensions.length, ()->"Provided dimensions are empty, need to pass at least one.");
		Utils.requirePosititveDimensions(dimensions);
		long numElements = Utils.numElementsFromDimensions(dimensions);
		Utils.sanityCheckArray(realIn,  numElements, "real input");
		Utils.sanityCheckArray(imagIn,  numElements, "imaginary input");
		Utils.sanityCheckArray(realOut, numElements, "real output");
		Utils.sanityCheckArray(imagOut, numElements, "imaginary output");
		/* start now */
		RowMajorArraySampler rIn = new RowMajorArraySampler(realIn, dimensions);
		RowMajorArraySampler iIn = new RowMajorArraySampler(imagIn, dimensions);
		RowMajorArrayWriter rOut = new RowMajorArrayWriter(realOut, dimensions);
		RowMajorArrayWriter iOut = new RowMajorArrayWriter(imagOut, dimensions);
		ifft(rIn.addImaginaryComponent(iIn), rOut.addImaginaryComponent(iOut), dimensions);
	}
	
	public static void ifft(double[] realIn, double[] imagIn, double[] realOut, int... dimensions) {
		/* sanity checks */
		Utils.requirePositive(dimensions.length, ()->"Provided dimensions are empty, need to pass at least one.");
		Utils.requirePosititveDimensions(dimensions);
		long numElements = Utils.numElementsFromDimensions(dimensions);
		Utils.sanityCheckArray(realIn,  numElements, "real input");
		Utils.sanityCheckArray(imagIn,  numElements, "imaginary input");
		Utils.sanityCheckArray(realOut, numElements, "real output");
		/* start now */
		RowMajorArraySampler rIn = new RowMajorArraySampler(realIn, dimensions);
		RowMajorArraySampler iIn = new RowMajorArraySampler(imagIn, dimensions);
		RowMajorArrayWriter rOut = new RowMajorArrayWriter(realOut, dimensions);
		ifft(rIn.addImaginaryComponent(iIn), rOut, dimensions);
	}
	

}


