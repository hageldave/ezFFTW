package hageldave.ezfftw.dp;

import static hageldave.ezfftw.FFTW_Initializer.initFFTW;

import java.util.Objects;

import org.bytedeco.javacpp.fftw3;
import org.bytedeco.javacpp.fftw3.fftw_iodim64; //#FLOATGEN_KEEPLINE
import org.bytedeco.javacpp.fftw3.fftw_plan;

import hageldave.ezfftw.GeneralUtils;


/**
 * Class for executing transforms through the FFTW Guru interface.
 * The provided methods offer a simplified/limited interface to the original
 * Guru methods, handling all the boilerplate code for creating the dimension
 * structs and arrays required.
 * This class provides methods for the following DFTs:
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
	 * chooses a DFT algorithm based on a simple heuristic. This method is usually used as
	 * the forward transform (DFT of a real signal).
	 * <p>
	 * The first array argument is the real valued data to be transformed,
	 * the second and third argument are the real and imaginary valued data that result
	 * from the transform.
	 * </br>
	 * The dimension argument specifies how many dimensions the data has and what the extent
	 * of each dimension is (e.g. {1024,768} for 2 dimensions of width=1024 and height=768).
	 * </br>
	 * The data is assumed to be in row major order (for dimensions {m,n} this means that the
	 * first m elements correspond to the first row of the 2-dimensional data, the last row
	 * starts at element (n-1)*m).
	 * <p>
	 * The resulting complex valued DFT has the origin at the element at index 0.
	 * Thus the DC (0hz) component of the DFT is the first element in the real output array
	 * and is the sum of all values of the input array.
	 * <p>
	 * The corresponding counter part to this method for computing the inverse DFT is
	 * {@link #execute_split_c2r(NativeRealArray, NativeRealArray, NativeRealArray, long...)}.
	 * Please note that FFTW was designed so that the inverse fourier transform of a fourier
	 * transform (or vice versa) would restore the original signal scaled by the number
	 * of elements </br>( <tt>idft(dft(x)) = x*x.length</tt> ).
	 * See also <a href="http://fftw.org/faq/section3.html#whyscaled">FFTW FAQ</a>.
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
			NativeRealArray realIn,
			NativeRealArray realOut,
			NativeRealArray imagOut,
			long... dimensions)
	{
		initFFTW();
		/* parameter sanity check */
		Objects.requireNonNull(realIn, ()->"Cannot use null as realIn parameter.");
		Objects.requireNonNull(realOut, ()->"Cannot use null as realOut parameter.");
		Objects.requireNonNull(imagOut, ()->"Cannot use null as imagOut parameter.");
		GeneralUtils.requirePositive(dimensions.length, ()->"Provided dimensions are empty, need to pass at least one.");
		GeneralUtils.requirePosititveDimensions(dimensions);
		long numElements = GeneralUtils.numElementsFromDimensions(dimensions);
		GeneralUtils.requireEqual(numElements, realIn.length,
				()->"provided real input does not have the same number of elements as determined from dimensions. "
						+ "Should be " + numElements + " but only has " + realIn.length);
		GeneralUtils.requireEqual(numElements, realOut.length,
				()->"provided real output does not have the same number of elements as determined from dimensions. "
						+ "Should be " + numElements + " but only has " + realOut.length);
		GeneralUtils.requireEqual(numElements, imagOut.length,
				()->"provided imaginary output does not have the same number of elements as determined from dimensions. "
						+ "Should be " + numElements + " but only has " + imagOut.length);
		/* declare native resources first */
		fftw_iodim64 array = null; //#FLOATGEN_IGNORE
		fftw_iodim64 dims = null;
		fftw_iodim64[] individualDims = new fftw_iodim64[dimensions.length]; //#FLOATGEN_IGNORE
		fftw_iodim64 lastDim = null; //#FLOATGEN_IGNORE
		fftw_plan plan = null;
		try {
			/* allocate native resources */
			array = new fftw_iodim64(dimensions.length+1); //#FLOATGEN_IGNORE
			dims = new fftw_iodim64(array);
			for(int i = 0; i < dimensions.length; i++){
				individualDims[i] = new fftw_iodim64(); //#FLOATGEN_IGNORE
			}
			lastDim = new fftw_iodim64(); //#FLOATGEN_IGNORE
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
			NativeRealArray realIn,
			NativeRealArray imagIn,
			NativeRealArray realOut,
			NativeRealArray imagOut,
			long... dimensions)
	{
		initFFTW();
		/* parameter sanity check */
		Objects.requireNonNull(realIn, ()->"Cannot use null as realIn parameter.");
		Objects.requireNonNull(imagIn, ()->"Cannot use null as imagIn parameter.");
		Objects.requireNonNull(realOut, ()->"Cannot use null as realOut parameter.");
		Objects.requireNonNull(imagOut, ()->"Cannot use null as imagOut parameter.");
		GeneralUtils.requirePositive(dimensions.length, ()->"Provided dimensions are empty, need to pass at least one.");
		GeneralUtils.requirePosititveDimensions(dimensions);
		long numElements = GeneralUtils.numElementsFromDimensions(dimensions);
		GeneralUtils.requireEqual(numElements, realIn.length,
				()->"provided real input does not have the same number of elements as determined from dimensions. "
						+ "Should be " + numElements + " but only has " + realIn.length);
		GeneralUtils.requireEqual(numElements, imagIn.length,
				()->"provided imaginary input does not have the same number of elements as determined from dimensions. "
						+ "Should be " + numElements + " but only has " + imagIn.length);
		GeneralUtils.requireEqual(numElements, realOut.length,
				()->"provided real output does not have the same number of elements as determined from dimensions. "
						+ "Should be " + numElements + " but only has " + realOut.length);
		GeneralUtils.requireEqual(numElements, imagOut.length,
				()->"provided imaginary output does not have the same number of elements as determined from dimensions. "
						+ "Should be " + numElements + " but only has " + imagOut.length);
		/* declare native resources first */
		fftw_iodim64 array = null; //#FLOATGEN_IGNORE
		fftw_iodim64 dims = null;
		fftw_iodim64[] individualDims = new fftw_iodim64[dimensions.length]; //#FLOATGEN_IGNORE
		fftw_iodim64 lastDim = null; //#FLOATGEN_IGNORE
		fftw_plan plan = null;
		try {
			/* allocate native resources */
			array = new fftw_iodim64(dimensions.length+1); //#FLOATGEN_IGNORE
			dims = new fftw_iodim64(array);
			for(int i = 0; i < dimensions.length; i++){
				individualDims[i] = new fftw_iodim64(); //#FLOATGEN_IGNORE
			}
			lastDim = new fftw_iodim64(); //#FLOATGEN_IGNORE
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

	/**
	 * Performs a split complex to real DFT using the FFTW_ESTIMATE planner flag which
	 * chooses a DFT algorithm based on a simple heuristic. This method is usually used as
	 * the backward transform (inverse DFT of a complex signal that originated from DFT of a real signal).
	 * <p>
	 * The first and second array argument are the real and imaginary valued data to be transformed,
	 * the third argument is the real valued data that results from the transform.
	 * </br>
	 * The dimension argument specifies how many dimensions the data has and what the extent
	 * of each dimension is (e.g. {1024,768} for 2 dimensions of width=1024 and height=768).
	 * </br>
	 * The data is assumed to be in row major order (for dimensions {m,n} this means that the
	 * first m elements correspond to the first row of the 2-dimensional data, the last row
	 * starts at element (n-1)*m).
	 * <p>
	 * The resulting complex valued DFT has the origin at the element at index 0.
	 * Thus the DC (0hz) component of the DFT is the first element in the real output array
	 * and is the sum of all values of the input array.
	 * <p>
	 * The corresponding counter part to this method for computing the forward transform (DFT) is
	 * {@link #execute_split_r2c(NativeRealArray, NativeRealArray, NativeRealArray, long...)}.
	 * Please note that FFTW was designed so that the inverse fourier transform of a fourier
	 * transform (or vice versa) would restore the original signal scaled by the number
	 * of elements </br>( <tt>dft(idft(xR,xI)) = xR*xR.length, xI*xR.length</tt> ).
	 * See also <a href="http://fftw.org/faq/section3.html#whyscaled">FFTW FAQ</a>.
	 *
	 * @param realIn real part of complex valued input array
	 * @param imagIn imaginary part of complex valued input array
	 * @param realOut real valued output array
	 * @param dimensions of the input (assuming input in row major order)
	 * e.g. {1024, 768} for a 2D signal of width=1024 and height=768
	 * @throws NullPointerException when one of the specified array arguments is null.
	 * @throws IllegalArgumentException </br>
	 * when no dimensions were specified,</br>
	 * when one of the specified dimensions is not positive,</br>
	 * when the number of elements determined from the dimensions does not match the lengths of the specified arrays.
	 */
	public static void execute_split_c2r(
			NativeRealArray realIn,
			NativeRealArray imagIn,
			NativeRealArray realOut,
			long... dimensions)
	{
		initFFTW();
		/* parameter sanity check */
		Objects.requireNonNull(realIn, ()->"Cannot use null as realIn parameter.");
		Objects.requireNonNull(imagIn, ()->"Cannot use null as realOut parameter.");
		Objects.requireNonNull(realOut, ()->"Cannot use null as imagOut parameter.");
		GeneralUtils.requirePositive(dimensions.length, ()->"Provided dimensions are empty, need to pass at least one.");
		GeneralUtils.requirePosititveDimensions(dimensions);
		long numElements = GeneralUtils.numElementsFromDimensions(dimensions);
		GeneralUtils.requireEqual(numElements, realIn.length,
				()->"provided real input does not have the same number of elements as determined from dimensions. "
						+ "Should be " + numElements + " but only has " + realIn.length);
		GeneralUtils.requireEqual(numElements, imagIn.length,
				()->"provided imaginary input does not have the same number of elements as determined from dimensions. "
						+ "Should be " + numElements + " but only has " + imagIn.length);
		GeneralUtils.requireEqual(numElements, realOut.length,
				()->"provided real output does not have the same number of elements as determined from dimensions. "
						+ "Should be " + numElements + " but only has " + realOut.length);
		/* declare native resources first */
		fftw_iodim64 array = null; //#FLOATGEN_IGNORE
		fftw_iodim64 dims = null;
		fftw_iodim64[] individualDims = new fftw_iodim64[dimensions.length]; //#FLOATGEN_IGNORE
		fftw_iodim64 lastDim = null; //#FLOATGEN_IGNORE
		fftw_plan plan = null;
		try {
			/* allocate native resources */
			array = new fftw_iodim64(dimensions.length+1); //#FLOATGEN_IGNORE
			dims = new fftw_iodim64(array);
			for(int i = 0; i < dimensions.length; i++){
				individualDims[i] = new fftw_iodim64(); //#FLOATGEN_IGNORE
			}
			lastDim = new fftw_iodim64(); //#FLOATGEN_IGNORE
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
