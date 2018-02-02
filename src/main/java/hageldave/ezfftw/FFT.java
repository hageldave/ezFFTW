package hageldave.ezfftw;

import hageldave.ezfftw.samplers.ComplexValuedSampler;
import hageldave.ezfftw.samplers.RealValuedSampler;
import hageldave.ezfftw.samplers.RowMajorArraySampler;
import hageldave.ezfftw.writers.ComplexValuedWriter;
import hageldave.ezfftw.writers.RealValuedWriter;
import hageldave.ezfftw.writers.RowMajorArrayWriter;

public class FFT {

	public static void fft(RealValuedSampler realIn, ComplexValuedWriter complexOut, long... dimensions) {
		/* sanity checks */
		Utils.requirePositive(dimensions.length, ()->"Provided dimensions are empty, need to pass at least one.");
		Utils.requirePosititveDimensions(dimensions);
		long numElements = Utils.numElementsFromDimensions(dimensions);
		try(
			/* allocate native resources */
			NativeDoubleArray a1 = new NativeDoubleArray(numElements);
			NativeDoubleArray a2 = new NativeDoubleArray(numElements);
		){
			/* put values from sampler into array */
			Utils.fillNativeArrayFromSampler(a1, realIn, dimensions);
			/* execute FFT */
			FFTW_Guru.execute_split_r2c(a1, a1, a2, dimensions);
			/* read real and imaginary part from arrays to complex writer */
			Utils.readNativeArrayToWriter(a1, complexOut.getPartWriter(false), dimensions); // real part
			Utils.readNativeArrayToWriter(a2, complexOut.getPartWriter(true), dimensions); // imaginary part
		}
	}

	public static void fft(ComplexValuedSampler complexIn, ComplexValuedWriter complexOut, long... dimensions) {
		/* sanity checks */
		Utils.requirePositive(dimensions.length, ()->"Provided dimensions are empty, need to pass at least one.");
		Utils.requirePosititveDimensions(dimensions);
		long numElements = Utils.numElementsFromDimensions(dimensions);
		try(
			/* allocate native resources */
			NativeDoubleArray a1 = new NativeDoubleArray(numElements);
			NativeDoubleArray a2 = new NativeDoubleArray(numElements);
		){
			/* put values from sampler into array */
			Utils.fillNativeArrayFromSampler(a1, complexIn.getPartSampler(false),dimensions); // real part
			Utils.fillNativeArrayFromSampler(a2, complexIn.getPartSampler(true), dimensions); // imaginary part
			/* execute FFT */
			FFTW_Guru.execute_split_c2c(a1, a2, a1, a2, dimensions);
			/* read real and imaginary part from arrays to complex writer */
			Utils.readNativeArrayToWriter(a1, complexOut.getPartWriter(false),dimensions); // real part
			Utils.readNativeArrayToWriter(a2, complexOut.getPartWriter(true), dimensions); // imaginary part
		}
	}

	public static void ifft(ComplexValuedSampler complexIn, ComplexValuedWriter complexOut, long... dimensions) {
		fft(complexIn.swapRealImaginary(), complexOut.swapRealImaginary(), dimensions);
	}

	public static void ifft(ComplexValuedSampler complexIn, RealValuedWriter realOut, long... dimensions) {
		/* sanity checks */
		Utils.requirePositive(dimensions.length, ()->"Provided dimensions are empty, need to pass at least one.");
		Utils.requirePosititveDimensions(dimensions);
		long numElements = Utils.numElementsFromDimensions(dimensions);
		try(
			/* allocate native resources */
			NativeDoubleArray a1 = new NativeDoubleArray(numElements);
			NativeDoubleArray a2 = new NativeDoubleArray(numElements);
		){
			/* put values from sampler into array */
			Utils.fillNativeArrayFromSampler(a1, complexIn.getPartSampler(false),dimensions); // real part
			Utils.fillNativeArrayFromSampler(a2, complexIn.getPartSampler(true), dimensions); // imaginary part
			/* execute FFT */
			FFTW_Guru.execute_split_c2r(a1, a2, a1, dimensions);
			/* read real and imaginary part from arrays to complex writer */
			Utils.readNativeArrayToWriter(a1, realOut, dimensions); // real part
		}
	}

	public static void fft(double[] realIn, double[] realOut, double[] imagOut, long... dimensions) {
		/* sanity checks */
		Utils.requirePositive(dimensions.length, ()->"Provided dimensions are empty, need to pass at least one.");
		Utils.requirePosititveDimensions(dimensions);
		long numElements = Utils.numElementsFromDimensions(dimensions);
		Utils.sanityCheckArray(realIn,  numElements, "real input");
		Utils.sanityCheckArray(realOut, numElements, "real output");
		Utils.sanityCheckArray(imagOut, numElements, "imaginary output");
		try(
			/* allocate native resources */
			NativeDoubleArray a1 = new NativeDoubleArray(numElements);
			NativeDoubleArray a2 = new NativeDoubleArray(numElements);
		){
			/* put values from sampler into array */
			a1.set(realIn);
			/* execute FFT */
			FFTW_Guru.execute_split_r2c(a1, a1, a2, dimensions);
			/* read real and imaginary part from arrays to complex writer */
			a1.get(0, realOut); // real part
			a2.get(0, imagOut); // imaginary part
		}
	}

	public static void fft(double[] realIn, double[] imagIn, double[] realOut, double[] imagOut, long... dimensions) {
		/* sanity checks */
		Utils.requirePositive(dimensions.length, ()->"Provided dimensions are empty, need to pass at least one.");
		Utils.requirePosititveDimensions(dimensions);
		long numElements = Utils.numElementsFromDimensions(dimensions);
		Utils.sanityCheckArray(realIn,  numElements, "real input");
		Utils.sanityCheckArray(imagIn,  numElements, "imaginary input");
		Utils.sanityCheckArray(realOut, numElements, "real output");
		Utils.sanityCheckArray(imagOut, numElements, "imaginary output");
		try(
			/* allocate native resources */
			NativeDoubleArray a1 = new NativeDoubleArray(numElements);
			NativeDoubleArray a2 = new NativeDoubleArray(numElements);
		){
			/* put values from sampler into array */
			a1.set(realIn);
			a2.set(imagIn);
			/* execute FFT */
			FFTW_Guru.execute_split_c2c(a1, a2, a1, a2, dimensions);
			/* read real and imaginary part from arrays to complex writer */
			a1.get(0, realOut); // real part
			a2.get(0, imagOut); // imaginary part
		}
	}

	public static void ifft(double[] realIn, double[] imagIn, double[] realOut, double[] imagOut, long... dimensions) {
		/* sanity checks */
		Utils.requirePositive(dimensions.length, ()->"Provided dimensions are empty, need to pass at least one.");
		Utils.requirePosititveDimensions(dimensions);
		long numElements = Utils.numElementsFromDimensions(dimensions);
		Utils.sanityCheckArray(realIn,  numElements, "real input");
		Utils.sanityCheckArray(imagIn,  numElements, "imaginary input");
		Utils.sanityCheckArray(realOut, numElements, "real output");
		Utils.sanityCheckArray(imagOut, numElements, "imaginary output");
		try(
			/* allocate native resources */
			NativeDoubleArray a1 = new NativeDoubleArray(numElements);
			NativeDoubleArray a2 = new NativeDoubleArray(numElements);
		){
			/* put values from sampler into array */
			a1.set(realIn);
			a2.set(imagIn);
			/* execute FFT */
			FFTW_Guru.execute_split_c2c(a2, a1, a2, a1, dimensions); // swapped real,imaginary arguments
			/* read real and imaginary part from arrays to complex writer */
			a1.get(0, realOut); // real part
			a2.get(0, imagOut); // imaginary part
		}
	}

	public static void ifft(double[] realIn, double[] imagIn, double[] realOut, long... dimensions) {
		/* sanity checks */
		Utils.requirePositive(dimensions.length, ()->"Provided dimensions are empty, need to pass at least one.");
		Utils.requirePosititveDimensions(dimensions);
		long numElements = Utils.numElementsFromDimensions(dimensions);
		Utils.sanityCheckArray(realIn,  numElements, "real input");
		Utils.sanityCheckArray(imagIn,  numElements, "imaginary input");
		Utils.sanityCheckArray(realOut, numElements, "real output");
		try(
			/* allocate native resources */
			NativeDoubleArray a1 = new NativeDoubleArray(numElements);
			NativeDoubleArray a2 = new NativeDoubleArray(numElements);
		){
			/* put values from sampler into array */
			a1.set(realIn);
			a2.set(imagIn);
			/* execute FFT */
			FFTW_Guru.execute_split_c2r(a1, a2, a1, dimensions);
			/* read real and imaginary part from arrays to complex writer */
			a1.get(0, realOut); // real part
		}
	}



}


