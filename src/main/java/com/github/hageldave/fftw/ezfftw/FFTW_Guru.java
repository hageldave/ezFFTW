package com.github.hageldave.fftw.ezfftw;

import java.util.Objects;
import java.util.function.Supplier;

import org.bytedeco.javacpp.DoublePointer;
import org.bytedeco.javacpp.Loader;
import org.bytedeco.javacpp.fftw3;
import org.bytedeco.javacpp.fftw3.fftw_iodim;
import org.bytedeco.javacpp.fftw3.fftw_iodim_do_not_use_me;
import org.bytedeco.javacpp.fftw3.fftw_plan;

import com.github.hageldave.fftw.ezfftw.Samplers.ComplexValuedSampler;
import com.github.hageldave.fftw.ezfftw.Samplers.RealValuedSampler;
import com.github.hageldave.fftw.ezfftw.Samplers.RowMajorArraySampler2D;
import com.github.hageldave.fftw.ezfftw.Writers.ComplexValuedWriter;
import com.github.hageldave.fftw.ezfftw.Writers.RowMajorArrayWriter2D;

// just for my local testing
//import hageldave.imagingkit.core.Img;
//import hageldave.imagingkit.core.io.ImageLoader;
//import hageldave.imagingkit.core.scientific.ColorImg;
//import hageldave.imagingkit.core.util.ImageFrame;
//import hageldave.imagingkit.fourier.ComplexImg;

public class FFTW_Guru {

	private static boolean setupDone = false;
	private static void setup(){
		if(!setupDone){
			synchronized (FFTW_Guru.class) {
				if(!setupDone){
					String loadedlib = Loader.load(fftw3.class);
					FFTW_Guru.setupDone = true;
					System.out.format("Loaded FFTW library [%s]%n",loadedlib);
				}
			}
		}
	}


	public static void execute_split_r2c(RealValuedSampler realIn, ComplexValuedWriter complexOut, int... dimensions){
		setup();
		/* parameter sanity check */
		Objects.requireNonNull(realIn, ()->"Cannot use null as realIn parameter.");
		Objects.requireNonNull(complexOut, ()->"Cannot use null as complexOut parameter.");
		assertPositive(dimensions.length, ()->"Provided dimensions are empty, need to pass at least one.");
		for(int i = 0; i < dimensions.length; i++){
			final int i_ = i;
			assertPositive(dimensions[i], ()->"All dimensions need to be positive, but dimension number "+i_+" is "+dimensions[i_]+".");
		}
		/* start things now */
		long numElements = 1;
		for(int d:dimensions){
			numElements *= d;
		}
		/* declare native resources first */
		fftw_iodim_do_not_use_me array = null;
		fftw_iodim dims = null;
		fftw_iodim_do_not_use_me[] individualDims = new fftw_iodim_do_not_use_me[dimensions.length];
		fftw_iodim_do_not_use_me lastDim = null;
		DoublePointer iR = null;
		DoublePointer oR = null;
		DoublePointer oI = null;
		fftw_plan plan = null;
		try {
			/* allocate native resources */
			array = new fftw_iodim_do_not_use_me(dimensions.length+1);
			dims = new fftw_iodim(array);
			for(int i = 0; i < dimensions.length; i++){
				individualDims[i] = new fftw_iodim_do_not_use_me();
			}
			lastDim = new fftw_iodim_do_not_use_me();
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
				int[] currentIndices = new int[dimensions.length];
				while(currentIndices[dimensions.length-1] < dimensions[dimensions.length-1]){
					iR.put(index, realIn.getValueAt(currentIndices));
					increment(0, currentIndices, dimensions);
					index++;
				}
			}
			/* make and execute plan */
			plan = fftw3.fftw_plan_guru_split_dft_r2c(
					dimensions.length+1, dims,
					0, null,
					iR, oR, oI,
					(int)fftw3.FFTW_ESTIMATE);
			fftw3.fftw_execute_split_dft_r2c(plan, iR, oR, oI);
			{
				/* get data from native arrays (inlined/adapted fillNativeArrayFromSampler)*/
				long index = 0;
				int[] currentIndices = new int[dimensions.length];
				double scaling = 1.0/numElements;
				while(currentIndices[dimensions.length-1] < dimensions[dimensions.length-1]){
					double rVal = oR.get(index)*scaling;
					double iVal = oI.get(index)*scaling;
					complexOut.setValueAt(rVal, false, currentIndices);
					complexOut.setValueAt(iVal, true, currentIndices);
					increment(0, currentIndices, dimensions);
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


	public static void execute_split_c2c(ComplexValuedSampler complexIn, ComplexValuedWriter complexOut, int... dimensions){
		setup();
		/* parameter sanity check */
		Objects.requireNonNull(complexIn, ()->"Cannot use null as realIn parameter.");
		Objects.requireNonNull(complexOut, ()->"Cannot use null as complexOut parameter.");
		assertPositive(dimensions.length, ()->"Provided dimensions are empty, need to pass at least one.");
		for(int i = 0; i < dimensions.length; i++){
			final int i_ = i;
			assertPositive(dimensions[i], ()->"All dimensions need to be positive, but dimension number "+i_+" is "+dimensions[i_]+".");
		}
		/* start things now */
		long numElements = 1;
		for(int d:dimensions){
			numElements *= d;
		}
		/* declare native resources first */
		fftw_iodim_do_not_use_me array = null;
		fftw_iodim dims = null;
		fftw_iodim_do_not_use_me[] individualDims = new fftw_iodim_do_not_use_me[dimensions.length];
		fftw_iodim_do_not_use_me lastDim = null;
		DoublePointer iR = null;
		DoublePointer iI = null;
		DoublePointer oR = null;
		DoublePointer oI = null;
		fftw_plan plan = null;
		try {
			/* allocate native resources */
			array = new fftw_iodim_do_not_use_me(dimensions.length+1);
			dims = new fftw_iodim(array);
			for(int i = 0; i < dimensions.length; i++){
				individualDims[i] = new fftw_iodim_do_not_use_me();
			}
			lastDim = new fftw_iodim_do_not_use_me();
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
				int[] currentIndices = new int[dimensions.length];
				while(currentIndices[dimensions.length-1] < dimensions[dimensions.length-1]){
					iR.put(index, complexIn.getValueAt(false,currentIndices));
					iI.put(index, complexIn.getValueAt(true, currentIndices));
					increment(0, currentIndices, dimensions);
					index++;
				}
			}
			/* make and execute plan */
			plan = fftw3.fftw_plan_guru_split_dft(
					dimensions.length+1, dims,
					0, null,
					iR,iI, oR, oI,
					(int)fftw3.FFTW_ESTIMATE);
			fftw3.fftw_execute_split_dft(plan, iR, iI, oR, oI);
			{
				/* get data from native arrays (inlined/adapted fillNativeArrayFromSampler)*/
				long index = 0;
				int[] currentIndices = new int[dimensions.length];
				double scaling = 1.0/numElements;
				while(currentIndices[dimensions.length-1] < dimensions[dimensions.length-1]){
					double rVal = oR.get(index)*scaling;
					double iVal = oI.get(index)*scaling;
					complexOut.setValueAt(rVal, false, currentIndices);
					complexOut.setValueAt(iVal, true, currentIndices);
					increment(0, currentIndices, dimensions);
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


	@SuppressWarnings("unused")
	private static void fillNativeArrayFromSampler(DoublePointer p, RealValuedSampler sampler, int[] dimensions){
		long index = 0;
		int[] currentIndices = new int[dimensions.length];
		while(currentIndices[dimensions.length-1] < dimensions[dimensions.length-1]){
			double val = sampler.getValueAt(currentIndices);
			p.put(index++, val);
			increment(0, currentIndices, dimensions);
		}
	}

	private static void increment(int i, int[] index, int[] dims){
		index[i]++;
		if(index[i] >= dims[i]){
			index[i] -= dims[i];
			if(i < dims.length-1)
				increment(i+1,index,dims);
			else
				index[i] = dims[i]; // highest possible number, dont overflow to signalize end
		}
	}


	private static void assertPositive(int n, Supplier<String> errmsg){
		if(n < 1){
			throw new IllegalArgumentException(errmsg.get());
		}
	}

// just my local testing
//	public static void main(String[] args) {
//		Img gauss = ImageLoader.loadImgFromURL("https://d37wxxhohlp07s.cloudfront.net/s3_images/754946/20b826df-c159-46a3-99f9-42403d5be59e_inline.gif");
//		ColorImg img = new ColorImg(gauss, false);
//		ImageFrame.display(img.getRemoteBufferedImage());
////		ComplexImg fft = fft2D_real2complexImg(img.getDataR(), img.getWidth(),img.getHeight());
//		ComplexImg fft = new ComplexImg(img.getWidth(),img.getHeight());
//		{
//			RowMajorArraySampler2D realIn = new RowMajorArraySampler2D(img.getDataR(), img.getWidth());
//			RowMajorArrayWriter2D realOut = new RowMajorArrayWriter2D(fft.getDataReal(), fft.getWidth());
//			RowMajorArrayWriter2D imagOut = new RowMajorArrayWriter2D(fft.getDataImag(), fft.getWidth());
//			ComplexValuedWriter complexOut = realOut.addImaginaryComponent(imagOut);
//
//			execute_split_r2c(realIn, complexOut, img.getWidth(),img.getHeight());
//		}
//		ComplexImg copy = fft.copy();
//		copy.shiftCornerToCenter().enableSynchronizePowerSpectrum(true).getDelegate().forEach(px->px.setValue(2, Math.log(px.getValue(2))));
//		ImageFrame.display(copy.getDelegate().scaleChannelToUnitRange(2).getChannelImage(2).toBufferedImage());
////		ifft2D_complexImg2complexImg(fft, fft);
//		{
//			RowMajorArraySampler2D realIn = new RowMajorArraySampler2D(fft.getDataReal(), fft.getWidth());
//			RowMajorArraySampler2D imagIn = new RowMajorArraySampler2D(fft.getDataImag(), fft.getWidth());
//			ComplexValuedSampler complexIn = realIn.addImaginaryComponent(imagIn);
//			RowMajorArrayWriter2D realOut = new RowMajorArrayWriter2D(fft.getDataReal(), fft.getWidth());
//			RowMajorArrayWriter2D imagOut = new RowMajorArrayWriter2D(fft.getDataImag(), fft.getWidth());
//			ComplexValuedWriter complexOut = realOut.addImaginaryComponent(imagOut);
//
//			execute_split_c2c(complexIn, complexOut, fft.getWidth(), fft.getHeight());
//		}
//		ImageFrame.display(fft.getDelegate().getChannelImage(0).scaleChannelToUnitRange(0).toBufferedImage());
//	}





}
