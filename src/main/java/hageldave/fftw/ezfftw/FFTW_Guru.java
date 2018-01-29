package hageldave.fftw.ezfftw;

import static hageldave.fftw.ezfftw.FFTW_Initializer.initFFTW;

import java.util.Objects;

import org.bytedeco.javacpp.DoublePointer;
import org.bytedeco.javacpp.fftw3;
import org.bytedeco.javacpp.fftw3.fftw_iodim64;
import org.bytedeco.javacpp.fftw3.fftw_iodim64_do_not_use_me;
import org.bytedeco.javacpp.fftw3.fftw_plan;

import hageldave.fftw.ezfftw.samplers.ComplexValuedSampler;
import hageldave.fftw.ezfftw.samplers.RealValuedSampler;
import hageldave.fftw.ezfftw.writers.ComplexValuedWriter;
import hageldave.fftw.ezfftw.writers.RealValuedWriter;

public class FFTW_Guru {


	public static void execute_split_r2c(RealValuedSampler realIn, ComplexValuedWriter complexOut, long... dimensions){
		initFFTW();
		/* parameter sanity check */
		Objects.requireNonNull(realIn, ()->"Cannot use null as realIn parameter.");
		Objects.requireNonNull(complexOut, ()->"Cannot use null as complexOut parameter.");
		Utils.requirePositive(dimensions.length, ()->"Provided dimensions are empty, need to pass at least one.");
		Utils.requirePosititveDimensions(dimensions);
		/* start things now */
		long numElements = Utils.numElementsFromDimensions(dimensions);
		/* declare native resources first */
		fftw_iodim64_do_not_use_me array = null;
		fftw_iodim64 dims = null;
		fftw_iodim64_do_not_use_me[] individualDims = new fftw_iodim64_do_not_use_me[dimensions.length];
		fftw_iodim64_do_not_use_me lastDim = null;
		DoublePointer iR = null;
		DoublePointer oR = null;
		DoublePointer oI = null;
		fftw_plan plan = null;
		try {
			/* allocate native resources */
			array = new fftw_iodim64_do_not_use_me(dimensions.length+1);
			dims = new fftw_iodim64(array);
			for(int i = 0; i < dimensions.length; i++){
				individualDims[i] = new fftw_iodim64_do_not_use_me();
			}
			lastDim = new fftw_iodim64_do_not_use_me();
			iR = new DoublePointer(numElements);
			oR = new DoublePointer(numElements);
			oI = new DoublePointer(numElements);
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
			{
				/* fill native data array (inlined fillNativeArrayFromSampler)*/
				long index = 0;
				long[] coordinates = new long[dimensions.length];
				while(coordinates[dimensions.length-1] < dimensions[dimensions.length-1]){
					iR.put(index, realIn.getValueAt(coordinates));
					Utils.incrementCoords(coordinates, dimensions);
					index++;
				}
			}
			/* make and execute plan */
			plan = fftw3.fftw_plan_guru64_split_dft_r2c(
					dimensions.length+1, dims,
					0, null,
					iR, oR, oI,
					(int)fftw3.FFTW_ESTIMATE);
			fftw3.fftw_execute_split_dft_r2c(plan, iR, oR, oI);
			{
				/* get data from native arrays (inlined/adapted fillNativeArrayFromSampler)*/
				long index = 0;
				long[] coordinates = new long[dimensions.length];
				while(coordinates[dimensions.length-1] < dimensions[dimensions.length-1]){
					double rVal = oR.get(index);
					double iVal = oI.get(index);
					complexOut.setValueAt(rVal, false, coordinates);
					complexOut.setValueAt(iVal, true, coordinates);
					Utils.incrementCoords(coordinates, dimensions);
					index++;
				}
			}
			/* destroy plan after use */
			fftw3.fftw_destroy_plan(plan);
		} finally {
			/* close resources in reverse allocation order */
			if(plan != null) plan.close();
			if(oI != null) oI.close();
			if(oR != null) oR.close();
			if(iR != null) iR.close();
			if(lastDim != null) lastDim.close();
			for(int i = dimensions.length-1; i >= 0; i--){
				if(individualDims[i] != null) individualDims[i].close();
			}
			if(dims != null) dims.close();
			if(array != null) array.close();
		}
	}


	public static void execute_split_c2c(ComplexValuedSampler complexIn, ComplexValuedWriter complexOut, long... dimensions){
		initFFTW();
		/* parameter sanity check */
		Objects.requireNonNull(complexIn, ()->"Cannot use null as realIn parameter.");
		Objects.requireNonNull(complexOut, ()->"Cannot use null as complexOut parameter.");
		Utils.requirePositive(dimensions.length, ()->"Provided dimensions are empty, need to pass at least one.");
		Utils.requirePosititveDimensions(dimensions);
		/* start things now */
		long numElements = Utils.numElementsFromDimensions(dimensions);
		/* declare native resources first */
		fftw_iodim64_do_not_use_me array = null;
		fftw_iodim64 dims = null;
		fftw_iodim64_do_not_use_me[] individualDims = new fftw_iodim64_do_not_use_me[dimensions.length];
		fftw_iodim64_do_not_use_me lastDim = null;
		DoublePointer iR = null;
		DoublePointer iI = null;
		DoublePointer oR = null;
		DoublePointer oI = null;
		fftw_plan plan = null;
		try {
			/* allocate native resources */
			array = new fftw_iodim64_do_not_use_me(dimensions.length+1);
			dims = new fftw_iodim64(array);
			for(int i = 0; i < dimensions.length; i++){
				individualDims[i] = new fftw_iodim64_do_not_use_me();
			}
			lastDim = new fftw_iodim64_do_not_use_me();
			iR = new DoublePointer(numElements);
			iI = new DoublePointer(numElements);
			oR = new DoublePointer(numElements);
			oI = new DoublePointer(numElements);
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
			{
				/* fill native data array (inlined fillNativeArrayFromSampler)*/
				long index = 0;
				long[] coordinates = new long[dimensions.length];
				while(coordinates[dimensions.length-1] < dimensions[dimensions.length-1]){
					iR.put(index, complexIn.getValueAt(false,coordinates));
					iI.put(index, complexIn.getValueAt(true, coordinates));
					Utils.incrementCoords(coordinates, dimensions);
					index++;
				}
			}
			/* make and execute plan */
			plan = fftw3.fftw_plan_guru64_split_dft(
					dimensions.length+1, dims,
					0, null,
					iR,iI, oR, oI,
					(int)fftw3.FFTW_ESTIMATE);
			fftw3.fftw_execute_split_dft(plan, iR, iI, oR, oI);
			{
				/* get data from native arrays (inlined/adapted fillNativeArrayFromSampler)*/
				long index = 0;
				long[] coordinates = new long[dimensions.length];
				while(coordinates[dimensions.length-1] < dimensions[dimensions.length-1]){
					double rVal = oR.get(index);
					double iVal = oI.get(index);
					complexOut.setValueAt(rVal, false, coordinates);
					complexOut.setValueAt(iVal, true, coordinates);
					Utils.incrementCoords(coordinates, dimensions);
					index++;
				}
			}
			/* destroy plan after use */
			fftw3.fftw_destroy_plan(plan);
		} finally {
			/* close resources in reverse allocation order */
			if(plan != null) plan.close();
			if(oI != null) oI.close();
			if(oR != null) oR.close();
			if(iI != null) iI.close();
			if(iR != null) iR.close();
			if(lastDim != null) lastDim.close();
			for(int i = dimensions.length-1; i >= 0; i--){
				if(individualDims[i] != null) individualDims[i].close();
			}
			if(dims != null) dims.close();
			if(array != null) array.close();
		}
	}
	
	public static void execute_split_c2r(ComplexValuedSampler complexIn, RealValuedWriter realOut, long... dimensions){
		initFFTW();
		/* parameter sanity check */
		Objects.requireNonNull(complexIn, ()->"Cannot use null as realIn parameter.");
		Objects.requireNonNull(realOut, ()->"Cannot use null as complexOut parameter.");
		Utils.requirePositive(dimensions.length, ()->"Provided dimensions are empty, need to pass at least one.");
		Utils.requirePosititveDimensions(dimensions);
		/* start things now */
		long numElements = Utils.numElementsFromDimensions(dimensions);
		/* declare native resources first */
		fftw_iodim64_do_not_use_me array = null;
		fftw_iodim64 dims = null;
		fftw_iodim64_do_not_use_me[] individualDims = new fftw_iodim64_do_not_use_me[dimensions.length];
		fftw_iodim64_do_not_use_me lastDim = null;
		DoublePointer iR = null;
		DoublePointer iI = null;
		DoublePointer oR = null;
		fftw_plan plan = null;
		try {
			/* allocate native resources */
			array = new fftw_iodim64_do_not_use_me(dimensions.length+1);
			dims = new fftw_iodim64(array);
			for(int i = 0; i < dimensions.length; i++){
				individualDims[i] = new fftw_iodim64_do_not_use_me();
			}
			lastDim = new fftw_iodim64_do_not_use_me();
			iR = new DoublePointer(numElements);
			iI = new DoublePointer(numElements);
			oR = new DoublePointer(numElements);
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
			{
				/* fill native data array (inlined fillNativeArrayFromSampler)*/
				long index = 0;
				long[] coordinates = new long[dimensions.length];
				while(coordinates[dimensions.length-1] < dimensions[dimensions.length-1]){
					iR.put(index, complexIn.getValueAt(false,coordinates));
					iI.put(index, complexIn.getValueAt(true, coordinates));
					Utils.incrementCoords(coordinates, dimensions);
					index++;
				}
			}
			/* make and execute plan */
			plan = fftw3.fftw_plan_guru64_split_dft_c2r(
					dimensions.length+1, dims,
					0, null,
					iR,iI, oR,
					(int)fftw3.FFTW_ESTIMATE);
			fftw3.fftw_execute_split_dft_c2r(plan, iR, iI, oR);
			{
				/* get data from native arrays (inlined/adapted fillNativeArrayFromSampler)*/
				long index = 0;
				long[] coordinates = new long[dimensions.length];
				while(coordinates[dimensions.length-1] < dimensions[dimensions.length-1]){
					double val = oR.get(index);
					realOut.setValueAt(val, coordinates);
					Utils.incrementCoords(coordinates, dimensions);
					index++;
				}
			}
			/* destroy plan after use */
			fftw3.fftw_destroy_plan(plan);
		} finally {
			/* close resources in reverse allocation order */
			if(plan != null) plan.close();
			if(oR != null) oR.close();
			if(iI != null) iI.close();
			if(iR != null) iR.close();
			if(lastDim != null) lastDim.close();
			for(int i = dimensions.length-1; i >= 0; i--){
				if(individualDims[i] != null) individualDims[i].close();
			}
			if(dims != null) dims.close();
			if(array != null) array.close();
		}
	}


	@SuppressWarnings("unused")
	private static void fillNativeArrayFromSampler(DoublePointer p, RealValuedSampler sampler, long[] dimensions){
		long index = 0;
		long[] coordinates = new long[dimensions.length];
		while(coordinates[dimensions.length-1] < dimensions[dimensions.length-1]){
			double val = sampler.getValueAt(coordinates);
			p.put(index++, val);
			Utils.incrementCoords(coordinates, dimensions);
		}
	}




}
