package hageldave.ezfftw;

import static hageldave.ezfftw.FFTW_Initializer.initFFTW;

import java.util.Objects;

import org.bytedeco.javacpp.fftw3;
import org.bytedeco.javacpp.fftw3.fftw_iodim64;
import org.bytedeco.javacpp.fftw3.fftw_iodim64_do_not_use_me;
import org.bytedeco.javacpp.fftw3.fftw_plan;


/**
 * Class for accessing the FFTW Guru interface. This class offers methods for
 * the following DFTs:
 * <ul>
 * <li>split r2c - real to complex DFT with separated real/imaginary output</li>
 * <li>split c2r - complex to real DFT with separated real/imaginary input</li>
 * <li>split c2c - complex to complex DFT with separated real/imaginary in/output</li>
 * </ul>
 * Split DFTs use seperate arrays for real and imaginary parts of complex numbers in contrast
 * to the interleaved real/imaginary format.
 *
 * @author hageldave
 * @see <a href="http://www.fftw.org/fftw3_doc/Guru-Interface.html">FFTW Guru Interface documentation (www.fftw.org)</a>
 */
public class FFTW_Guru {

	/**
	 * Performs a split real to complex DFT using the FFTW_ESTIMATE planner flag which
	 * chooses a DFT algorithm based on a simple heuristic.
	 *
	 * @param realIn real valued input array
	 * @param realOut real part of complex valued output array
	 * @param imagOut imaginary part of complex valued output array
	 * @param dimensions of the input (assuming input in row major order)
	 * e.g. {1024, 768} for a 2D signal of width=1024 and height=768
	 * @throws NullPointerException when one of the specified array arguments is null.
	 * @throws IllegalArgumentException </br>
	 * when no dimensions were specified,</br>
	 * when one of the specified dimensions is not positive,</br>
	 * when the number of elements determined from the dimensions does not match the lengths of the specified arrays.
	 */
	public static void execute_split_r2c(
			NativeDoubleArray realIn,
			NativeDoubleArray realOut,
			NativeDoubleArray imagOut,
			long... dimensions)
	{
		initFFTW();
		/* parameter sanity check */
		Objects.requireNonNull(realIn, ()->"Cannot use null as realIn parameter.");
		Objects.requireNonNull(realOut, ()->"Cannot use null as realOut parameter.");
		Objects.requireNonNull(imagOut, ()->"Cannot use null as imagOut parameter.");
		Utils.requirePositive(dimensions.length, ()->"Provided dimensions are empty, need to pass at least one.");
		Utils.requirePosititveDimensions(dimensions);
		long numElements = Utils.numElementsFromDimensions(dimensions);
		Utils.requireEqual(numElements, realIn.length,
				()->"provided real input does not have the same number of elements as determined from dimensions. "
						+ "Should be " + numElements + " but only has " + realIn.length);
		Utils.requireEqual(numElements, realOut.length,
				()->"provided real output does not have the same number of elements as determined from dimensions. "
						+ "Should be " + numElements + " but only has " + realOut.length);
		Utils.requireEqual(numElements, imagOut.length,
				()->"provided imaginary output does not have the same number of elements as determined from dimensions. "
						+ "Should be " + numElements + " but only has " + imagOut.length);
		/* declare native resources first */
		fftw_iodim64_do_not_use_me array = null;
		fftw_iodim64 dims = null;
		fftw_iodim64_do_not_use_me[] individualDims = new fftw_iodim64_do_not_use_me[dimensions.length];
		fftw_iodim64_do_not_use_me lastDim = null;
		fftw_plan plan = null;
		try {
			/* allocate native resources */
			array = new fftw_iodim64_do_not_use_me(dimensions.length+1);
			dims = new fftw_iodim64(array);
			for(int i = 0; i < dimensions.length; i++){
				individualDims[i] = new fftw_iodim64_do_not_use_me();
			}
			lastDim = new fftw_iodim64_do_not_use_me();
			/* fill native resources */
			long stride = 1;
			for(int i = 0; i < dimensions.length; i++){
				individualDims[i]
						.n(dimensions[i]) 	// dimension size
						.is((int)stride) 	// input stride
						.os((int)stride);	// output stride
				array.position(i).put(individualDims[i]);
				stride *= dimensions[i];
			}
			lastDim.n(1).is((int)stride).os((int)stride);
			array.position(dimensions.length).put(lastDim);
			/* make and execute plan */
			plan = fftw3.fftw_plan_guru64_split_dft_r2c(
					dimensions.length+1, dims,
					0, null,
					realIn.getPointer(), realOut.getPointer(), imagOut.getPointer(),
					(int)fftw3.FFTW_ESTIMATE);
			fftw3.fftw_execute_split_dft_r2c(plan, realIn.getPointer(), realOut.getPointer(), imagOut.getPointer());
			/* destroy plan after use */
			fftw3.fftw_destroy_plan(plan);
		} finally {
			/* close resources in reverse allocation order */
			if(plan != null) plan.close();
			if(lastDim != null) lastDim.close();
			for(int i = dimensions.length-1; i >= 0; i--){
				if(individualDims[i] != null) individualDims[i].close();
			}
			if(dims != null) dims.close();
			if(array != null) array.close();
		}
	}


	public static void execute_split_c2c(
			NativeDoubleArray realIn,
			NativeDoubleArray imagIn,
			NativeDoubleArray realOut,
			NativeDoubleArray imagOut,
			long... dimensions)
	{
		initFFTW();
		/* parameter sanity check */
		Objects.requireNonNull(realIn, ()->"Cannot use null as realIn parameter.");
		Objects.requireNonNull(imagIn, ()->"Cannot use null as imagIn parameter.");
		Objects.requireNonNull(realOut, ()->"Cannot use null as realOut parameter.");
		Objects.requireNonNull(imagOut, ()->"Cannot use null as imagOut parameter.");
		Utils.requirePositive(dimensions.length, ()->"Provided dimensions are empty, need to pass at least one.");
		Utils.requirePosititveDimensions(dimensions);
		long numElements = Utils.numElementsFromDimensions(dimensions);
		Utils.requireEqual(numElements, realIn.length,
				()->"provided real input does not have the same number of elements as determined from dimensions. "
						+ "Should be " + numElements + " but only has " + realIn.length);
		Utils.requireEqual(numElements, imagIn.length,
				()->"provided imaginary input does not have the same number of elements as determined from dimensions. "
						+ "Should be " + numElements + " but only has " + imagIn.length);
		Utils.requireEqual(numElements, realOut.length,
				()->"provided real output does not have the same number of elements as determined from dimensions. "
						+ "Should be " + numElements + " but only has " + realOut.length);
		Utils.requireEqual(numElements, imagOut.length,
				()->"provided imaginary output does not have the same number of elements as determined from dimensions. "
						+ "Should be " + numElements + " but only has " + imagOut.length);
		/* declare native resources first */
		fftw_iodim64_do_not_use_me array = null;
		fftw_iodim64 dims = null;
		fftw_iodim64_do_not_use_me[] individualDims = new fftw_iodim64_do_not_use_me[dimensions.length];
		fftw_iodim64_do_not_use_me lastDim = null;
		fftw_plan plan = null;
		try {
			/* allocate native resources */
			array = new fftw_iodim64_do_not_use_me(dimensions.length+1);
			dims = new fftw_iodim64(array);
			for(int i = 0; i < dimensions.length; i++){
				individualDims[i] = new fftw_iodim64_do_not_use_me();
			}
			lastDim = new fftw_iodim64_do_not_use_me();
			/* fill native resources */
			long stride = 1;
			for(int i = 0; i < dimensions.length; i++){
				individualDims[i]
						.n(dimensions[i]) 	// dimension size
						.is((int)stride) 	// input stride
						.os((int)stride);	// output stride
				array.position(i).put(individualDims[i]);
				stride *= dimensions[i];
			}
			lastDim.n(1).is((int)stride).os((int)stride);
			array.position(dimensions.length).put(lastDim);
			/* make and execute plan */
			plan = fftw3.fftw_plan_guru64_split_dft(
					dimensions.length+1, dims,
					0, null,
					realIn.getPointer(), imagIn.getPointer(), realOut.getPointer(), imagOut.getPointer(),
					(int)fftw3.FFTW_ESTIMATE);
			fftw3.fftw_execute_split_dft(plan, realIn.getPointer(), imagIn.getPointer(), realOut.getPointer(), imagOut.getPointer());
			/* destroy plan after use */
			fftw3.fftw_destroy_plan(plan);
		} finally {
			/* close resources in reverse allocation order */
			if(plan != null) plan.close();
			if(lastDim != null) lastDim.close();
			for(int i = dimensions.length-1; i >= 0; i--){
				if(individualDims[i] != null) individualDims[i].close();
			}
			if(dims != null) dims.close();
			if(array != null) array.close();
		}


	}

	public static void execute_split_c2r(
			NativeDoubleArray realIn,
			NativeDoubleArray imagIn,
			NativeDoubleArray realOut,
			long... dimensions)
	{
		initFFTW();
		/* parameter sanity check */
		Objects.requireNonNull(realIn, ()->"Cannot use null as realIn parameter.");
		Objects.requireNonNull(imagIn, ()->"Cannot use null as realOut parameter.");
		Objects.requireNonNull(realOut, ()->"Cannot use null as imagOut parameter.");
		Utils.requirePositive(dimensions.length, ()->"Provided dimensions are empty, need to pass at least one.");
		Utils.requirePosititveDimensions(dimensions);
		long numElements = Utils.numElementsFromDimensions(dimensions);
		Utils.requireEqual(numElements, realIn.length,
				()->"provided real input does not have the same number of elements as determined from dimensions. "
						+ "Should be " + numElements + " but only has " + realIn.length);
		Utils.requireEqual(numElements, imagIn.length,
				()->"provided imaginary input does not have the same number of elements as determined from dimensions. "
						+ "Should be " + numElements + " but only has " + imagIn.length);
		Utils.requireEqual(numElements, realOut.length,
				()->"provided real output does not have the same number of elements as determined from dimensions. "
						+ "Should be " + numElements + " but only has " + realOut.length);
		/* declare native resources first */
		fftw_iodim64_do_not_use_me array = null;
		fftw_iodim64 dims = null;
		fftw_iodim64_do_not_use_me[] individualDims = new fftw_iodim64_do_not_use_me[dimensions.length];
		fftw_iodim64_do_not_use_me lastDim = null;
		fftw_plan plan = null;
		try {
			/* allocate native resources */
			array = new fftw_iodim64_do_not_use_me(dimensions.length+1);
			dims = new fftw_iodim64(array);
			for(int i = 0; i < dimensions.length; i++){
				individualDims[i] = new fftw_iodim64_do_not_use_me();
			}
			lastDim = new fftw_iodim64_do_not_use_me();
			/* fill native resources */
			long stride = 1;
			for(int i = 0; i < dimensions.length; i++){
				individualDims[i]
						.n(dimensions[i]) 	// dimension size
						.is((int)stride) 	// input stride
						.os((int)stride);	// output stride
				array.position(i).put(individualDims[i]);
				stride *= dimensions[i];
			}
			lastDim.n(1).is((int)stride).os((int)stride);
			array.position(dimensions.length).put(lastDim);
			/* make and execute plan */
			plan = fftw3.fftw_plan_guru64_split_dft_c2r(
					dimensions.length+1, dims,
					0, null,
					realIn.getPointer(), imagIn.getPointer(), realOut.getPointer(),
					(int)fftw3.FFTW_ESTIMATE);
			fftw3.fftw_execute_split_dft_c2r(plan, realIn.getPointer(), imagIn.getPointer(), realOut.getPointer());
			/* destroy plan after use */
			fftw3.fftw_destroy_plan(plan);
		} finally {
			/* close resources in reverse allocation order */
			if(plan != null) plan.close();
			if(lastDim != null) lastDim.close();
			for(int i = dimensions.length-1; i >= 0; i--){
				if(individualDims[i] != null) individualDims[i].close();
			}
			if(dims != null) dims.close();
			if(array != null) array.close();
		}
	}






}
