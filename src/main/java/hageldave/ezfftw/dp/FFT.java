package hageldave.ezfftw.dp;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

import hageldave.ezfftw.GeneralUtils;
import hageldave.ezfftw.dp.samplers.ComplexValuedSampler;
import hageldave.ezfftw.dp.samplers.RealValuedSampler;
import hageldave.ezfftw.dp.writers.ComplexValuedWriter;
import hageldave.ezfftw.dp.writers.RealValuedWriter;

public class FFT {

	public static void fft(RealValuedSampler realIn, ComplexValuedWriter complexOut, long... dimensions) {
		/* sanity checks */
		GeneralUtils.requirePositive(dimensions.length, ()->"Provided dimensions are empty, need to pass at least one.");
		GeneralUtils.requirePosititveDimensions(dimensions);
		/* setup argument lambdas */
		long numElements = GeneralUtils.numElementsFromDimensions(dimensions);
		Supplier<NativeRealArray> r_input = ()-> {
			NativeRealArray ri = new NativeRealArray(numElements);
			PrecisionDependentUtils.fillNativeArrayFromSampler(ri, realIn, dimensions);
			return ri;
		};
		BiConsumer<NativeRealArray, NativeRealArray> c_output = (real,imag) -> {
			PrecisionDependentUtils.readNativeArrayToWriter(real, complexOut.getPartWriter(false), dimensions);
			PrecisionDependentUtils.readNativeArrayToWriter(imag, complexOut.getPartWriter(true), dimensions);
		};
		fft(r_input, c_output, dimensions);
	}

	public static void fft(ComplexValuedSampler complexIn, ComplexValuedWriter complexOut, long... dimensions) {
		/* sanity checks */
		GeneralUtils.requirePositive(dimensions.length, ()->"Provided dimensions are empty, need to pass at least one.");
		GeneralUtils.requirePosititveDimensions(dimensions);
		/* setup argument lambdas */
		long numElements = GeneralUtils.numElementsFromDimensions(dimensions);
		Supplier<NativeRealArray> r_input = ()-> {
			NativeRealArray ri = new NativeRealArray(numElements);
			PrecisionDependentUtils.fillNativeArrayFromSampler(ri, complexIn.getPartSampler(false), dimensions);
			return ri;
		};
		Supplier<NativeRealArray> i_input = ()-> {
			NativeRealArray ii = new NativeRealArray(numElements);
			PrecisionDependentUtils.fillNativeArrayFromSampler(ii, complexIn.getPartSampler(true), dimensions);
			return ii;
		};
		BiConsumer<NativeRealArray, NativeRealArray> c_output = (real,imag) -> {
			PrecisionDependentUtils.readNativeArrayToWriter(real, complexOut.getPartWriter(false), dimensions);
			PrecisionDependentUtils.readNativeArrayToWriter(imag, complexOut.getPartWriter(true), dimensions);
		};
		fft(r_input, i_input, c_output, dimensions);
	}

	public static void ifft(ComplexValuedSampler complexIn, ComplexValuedWriter complexOut, long... dimensions) {
		fft(complexIn.swapRealImaginary(), complexOut.swapRealImaginary(), dimensions);
	}

	public static void ifft(ComplexValuedSampler complexIn, RealValuedWriter realOut, long... dimensions) {
		/* sanity checks */
		GeneralUtils.requirePositive(dimensions.length, ()->"Provided dimensions are empty, need to pass at least one.");
		GeneralUtils.requirePosititveDimensions(dimensions);
		/* setup argument lambdas */
		long numElements = GeneralUtils.numElementsFromDimensions(dimensions);
		Supplier<NativeRealArray> r_input = ()-> {
			NativeRealArray ri = new NativeRealArray(numElements);
			PrecisionDependentUtils.fillNativeArrayFromSampler(ri, complexIn.getPartSampler(false), dimensions);
			return ri;
		};
		Supplier<NativeRealArray> i_input = ()-> {
			NativeRealArray ii = new NativeRealArray(numElements);
			PrecisionDependentUtils.fillNativeArrayFromSampler(ii, complexIn.getPartSampler(true), dimensions);
			return ii;
		};
		Consumer<NativeRealArray> r_output = (real) -> {
			PrecisionDependentUtils.readNativeArrayToWriter(real, realOut, dimensions);
		};
		ifft(r_input, i_input, r_output, dimensions);
	}

	public static void fft(double[] realIn, double[] realOut, double[] imagOut, long... dimensions) {
		/* sanity checks */
		GeneralUtils.requirePositive(dimensions.length, ()->"Provided dimensions are empty, need to pass at least one.");
		GeneralUtils.requirePosititveDimensions(dimensions);
		long numElements = GeneralUtils.numElementsFromDimensions(dimensions);
		PrecisionDependentUtils.sanityCheckArray(realIn,  numElements, "real input");
		PrecisionDependentUtils.sanityCheckArray(realOut, numElements, "real output");
		PrecisionDependentUtils.sanityCheckArray(imagOut, numElements, "imaginary output");
		/* setup argument lambdas */
		Supplier<NativeRealArray> r_input = ()-> {
			NativeRealArray ri = new NativeRealArray(numElements);
			ri.set(realIn);
			return ri;
		};
		BiConsumer<NativeRealArray, NativeRealArray> c_output = (real,imag) -> {
			real.get(0, realOut);
			imag.get(0, imagOut);
		};
		fft(r_input, c_output, dimensions);
	}

	public static void fft(double[] realIn, double[] imagIn, double[] realOut, double[] imagOut, long... dimensions) {
		/* sanity checks */
		GeneralUtils.requirePositive(dimensions.length, ()->"Provided dimensions are empty, need to pass at least one.");
		GeneralUtils.requirePosititveDimensions(dimensions);
		long numElements = GeneralUtils.numElementsFromDimensions(dimensions);
		PrecisionDependentUtils.sanityCheckArray(realIn,  numElements, "real input");
		PrecisionDependentUtils.sanityCheckArray(imagIn,  numElements, "imaginary input");
		PrecisionDependentUtils.sanityCheckArray(realOut, numElements, "real output");
		PrecisionDependentUtils.sanityCheckArray(imagOut, numElements, "imaginary output");
		/* setup argument lambdas */
		Supplier<NativeRealArray> r_input = ()-> {
			NativeRealArray ri = new NativeRealArray(numElements);
			ri.set(realIn);
			return ri;
		};
		Supplier<NativeRealArray> i_input = ()-> {
			NativeRealArray ii = new NativeRealArray(numElements);
			ii.set(imagIn);
			return ii;
		};
		BiConsumer<NativeRealArray, NativeRealArray> c_output = (real,imag) -> {
			real.get(0, realOut);
			imag.get(0, imagOut);
		};
		fft(r_input, i_input, c_output, dimensions);
	}

	public static void ifft(double[] realIn, double[] imagIn, double[] realOut, double[] imagOut, long... dimensions) {
		/* sanity checks */
		GeneralUtils.requirePositive(dimensions.length, ()->"Provided dimensions are empty, need to pass at least one.");
		GeneralUtils.requirePosititveDimensions(dimensions);
		long numElements = GeneralUtils.numElementsFromDimensions(dimensions);
		PrecisionDependentUtils.sanityCheckArray(realIn,  numElements, "real input");
		PrecisionDependentUtils.sanityCheckArray(imagIn,  numElements, "imaginary input");
		PrecisionDependentUtils.sanityCheckArray(realOut, numElements, "real output");
		PrecisionDependentUtils.sanityCheckArray(imagOut, numElements, "imaginary output");
		/* setup argument lambdas */
		Supplier<NativeRealArray> r_input = ()-> {
			NativeRealArray ri = new NativeRealArray(numElements);
			ri.set(realIn);
			return ri;
		};
		Supplier<NativeRealArray> i_input = ()-> {
			NativeRealArray ii = new NativeRealArray(numElements);
			ii.set(imagIn);
			return ii;
		};
		BiConsumer<NativeRealArray, NativeRealArray> c_output = (real,imag) -> {
			real.get(0, realOut);
			imag.get(0, imagOut);
		};
		ifft(r_input, i_input, c_output, dimensions);
	}

	public static void ifft(double[] realIn, double[] imagIn, double[] realOut, long... dimensions) {
		/* sanity checks */
		GeneralUtils.requirePositive(dimensions.length, ()->"Provided dimensions are empty, need to pass at least one.");
		GeneralUtils.requirePosititveDimensions(dimensions);
		long numElements = GeneralUtils.numElementsFromDimensions(dimensions);
		PrecisionDependentUtils.sanityCheckArray(realIn,  numElements, "real input");
		PrecisionDependentUtils.sanityCheckArray(imagIn,  numElements, "imaginary input");
		PrecisionDependentUtils.sanityCheckArray(realOut, numElements, "real output");
		/* setup argument lambdas */
		Supplier<NativeRealArray> r_input = ()-> {
			NativeRealArray ri = new NativeRealArray(numElements);
			ri.set(realIn);
			return ri;
		};
		Supplier<NativeRealArray> i_input = ()-> {
			NativeRealArray ii = new NativeRealArray(numElements);
			ii.set(imagIn);
			return ii;
		};
		Consumer<NativeRealArray> r_output = (real) -> {
			real.get(0, realOut);
		};
		ifft(r_input, i_input, r_output, dimensions);
	}

	public static void fft(
			Supplier<NativeRealArray> realIn, 
			BiConsumer<NativeRealArray,NativeRealArray> complexOut, 
			long... dimensions)
	{
		/* sanity checks */
		GeneralUtils.requirePositive(dimensions.length, ()->"Provided dimensions are empty, need to pass at least one.");
		GeneralUtils.requirePosititveDimensions(dimensions);
		long numElements = GeneralUtils.numElementsFromDimensions(dimensions);
		try(
				/* allocate native resources */
			NativeRealArray a1 = realIn.get();
			NativeRealArray a2 = new NativeRealArray(numElements);
		){
			GeneralUtils.requireEqual(a1.length, numElements, ()->
			"The array returned by realIn supplier does not have the length determined from dimensions. "
			+ "From dimensions:" + numElements + " array:" + a1.length);
			/* execute FFT */
			FFTW_Guru.execute_split_r2c(a1, a1, a2, dimensions);
			complexOut.accept(a1, a2);
		}
	}

	public static void fft(
			Supplier<NativeRealArray> realIn,
			Supplier<NativeRealArray> imagIn,
			BiConsumer<NativeRealArray,NativeRealArray> complexOut, 
			long... dimensions)
	{
		/* sanity checks */
		GeneralUtils.requirePositive(dimensions.length, ()->"Provided dimensions are empty, need to pass at least one.");
		GeneralUtils.requirePosititveDimensions(dimensions);
		long numElements = GeneralUtils.numElementsFromDimensions(dimensions);
		try(
			/* allocate native resources */
			NativeRealArray a1 = realIn.get();
			NativeRealArray a2 = imagIn.get();
		){
			GeneralUtils.requireEqual(a1.length, numElements, ()->
			"The array returned by realIn supplier does not have the length determined from dimensions. "
			+ "From dimensions:" + numElements + " array:" + a1.length);
			GeneralUtils.requireEqual(a2.length, numElements, ()->
			"The array returned by imagIn supplier does not have the length determined from dimensions. "
			+ "From dimensions:" + numElements + " array:" + a1.length);
			/* execute FFT */
			FFTW_Guru.execute_split_c2c(a1, a2, a1, a2, dimensions);
			complexOut.accept(a1, a2);
		}
	}
	
	public static void ifft(
			Supplier<NativeRealArray> realIn,
			Supplier<NativeRealArray> imagIn,
			BiConsumer<NativeRealArray,NativeRealArray> complexOut, 
			long... dimensions)
	{
		/* sanity checks */
		GeneralUtils.requirePositive(dimensions.length, ()->"Provided dimensions are empty, need to pass at least one.");
		GeneralUtils.requirePosititveDimensions(dimensions);
		long numElements = GeneralUtils.numElementsFromDimensions(dimensions);
		try(
			/* allocate native resources */
			NativeRealArray a1 = realIn.get();
			NativeRealArray a2 = imagIn.get();
		){
			GeneralUtils.requireEqual(a1.length, numElements, ()->
			"The array returned by realIn supplier does not have the length determined from dimensions. "
			+ "From dimensions:" + numElements + " array:" + a1.length);
			GeneralUtils.requireEqual(a2.length, numElements, ()->
			"The array returned by imagIn supplier does not have the length determined from dimensions. "
			+ "From dimensions:" + numElements + " array:" + a1.length);
			/* execute FFT */
			FFTW_Guru.execute_split_c2c(a2, a1, a2, a1, dimensions);// swapped arguments
			complexOut.accept(a1, a2);
		}
	}
	
	public static void ifft(
			Supplier<NativeRealArray> realIn,
			Supplier<NativeRealArray> imagIn,
			Consumer<NativeRealArray> complexOut, 
			long... dimensions)
	{
		/* sanity checks */
		GeneralUtils.requirePositive(dimensions.length, ()->"Provided dimensions are empty, need to pass at least one.");
		GeneralUtils.requirePosititveDimensions(dimensions);
		long numElements = GeneralUtils.numElementsFromDimensions(dimensions);
		try(
			/* allocate native resources */
			NativeRealArray a1 = realIn.get();
			NativeRealArray a2 = imagIn.get();
		){
			GeneralUtils.requireEqual(a1.length, numElements, ()->
			"The array returned by realIn supplier does not have the length determined from dimensions. "
			+ "From dimensions:" + numElements + " array:" + a1.length);
			GeneralUtils.requireEqual(a2.length, numElements, ()->
			"The array returned by imagIn supplier does not have the length determined from dimensions. "
			+ "From dimensions:" + numElements + " array:" + a1.length);
			/* execute FFT */
			FFTW_Guru.execute_split_c2r(a1, a2, a1, dimensions);// swapped arguments
			complexOut.accept(a1);
		}
	}

}


