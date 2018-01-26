package com.github.hageldave.fftw.ezfftw;

import java.util.Objects;

import com.github.hageldave.fftw.ezfftw.Samplers.ComplexValuedSampler;
import com.github.hageldave.fftw.ezfftw.Samplers.RealValuedSampler;
import com.github.hageldave.fftw.ezfftw.Samplers.RowMajorArraySampler2D;
import com.github.hageldave.fftw.ezfftw.Writers.ComplexValuedWriter;
import com.github.hageldave.fftw.ezfftw.Writers.RealValuedWriter;
import com.github.hageldave.fftw.ezfftw.Writers.RowMajorArrayWriter2D;

//import hageldave.imagingkit.core.Img;
//import hageldave.imagingkit.core.io.ImageLoader;
//import hageldave.imagingkit.core.scientific.ColorImg;
//import hageldave.imagingkit.core.util.ImageFrame;
//import hageldave.imagingkit.fourier.ComplexImg;

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
		Objects.requireNonNull(realIn, ()->"real input array cannot be null");
		Objects.requireNonNull(realOut, ()->"realoutput array cannot be null");
		Objects.requireNonNull(imagOut, ()->"imaginary output array cannot be null");
		if(dimensions.length == 0)
			throw new IllegalArgumentException("dimensions argument is mandatory, cannot pass no dimensions");
		long numElements = FFTW_Guru.numElementsFromDimensions(dimensions);
		if(numElements != realIn.length) 
			throw new IllegalArgumentException("Number of elements determined from dimensions does not match array length of real input. " 
					+ numElements + " expected but is of length " + realIn.length);
		if(numElements != realOut.length) 
			throw new IllegalArgumentException("Number of elements determined from dimensions does not match array length of real output. " 
					+ numElements + " expected but is of length " + realOut.length);
		if(numElements != imagOut.length) 
			throw new IllegalArgumentException("Number of elements determined from dimensions does not match array length of imaginary output. " 
					+ numElements + " expected but is of length " + imagOut.length);
	}





//
//	// just my local testing
//	public static void main(String[] args) {
//		Img gauss = ImageLoader.loadImgFromURL("https://d37wxxhohlp07s.cloudfront.net/s3_images/754946/20b826df-c159-46a3-99f9-42403d5be59e_inline.gif");
//		ColorImg img = new ColorImg(gauss, false);
//		ImageFrame.display(img.getRemoteBufferedImage());
//		//			ComplexImg fft = fft2D_real2complexImg(img.getDataR(), img.getWidth(),img.getHeight());
//		ComplexImg fft = new ComplexImg(img.getWidth(),img.getHeight());
//		{
//			RowMajorArraySampler2D realIn = new RowMajorArraySampler2D(img.getDataR(), img.getWidth());
//			RowMajorArrayWriter2D realOut = new RowMajorArrayWriter2D(fft.getDataReal(), fft.getWidth());
//			RowMajorArrayWriter2D imagOut = new RowMajorArrayWriter2D(fft.getDataImag(), fft.getWidth());
//			ComplexValuedWriter complexOut = realOut.addImaginaryComponent(imagOut);
//
//			fft(realIn, complexOut, img.getWidth(),img.getHeight());
//		}
//		ComplexImg copy = fft.copy();
//		copy.shiftCornerToCenter().enableSynchronizePowerSpectrum(true).getDelegate().forEach(px->px.setValue(2, Math.log(px.getValue(2))));
//		ImageFrame.display(copy.getDelegate().scaleChannelToUnitRange(2).getChannelImage(2).toBufferedImage());
//		//			ifft2D_complexImg2complexImg(fft, fft);
//		{
//			RowMajorArraySampler2D realIn = new RowMajorArraySampler2D(fft.getDataReal(), fft.getWidth());
//			RowMajorArraySampler2D imagIn = new RowMajorArraySampler2D(fft.getDataImag(), fft.getWidth());
//			ComplexValuedSampler complexIn = realIn.addImaginaryComponent(imagIn);
//			RowMajorArrayWriter2D realOut = new RowMajorArrayWriter2D(fft.getDataReal(), fft.getWidth());
//			RowMajorArrayWriter2D imagOut = new RowMajorArrayWriter2D(fft.getDataImag(), fft.getWidth());
//			ComplexValuedWriter complexOut = realOut.addImaginaryComponent(imagOut);
//
//			ifft(complexIn, complexOut, fft.getWidth(), fft.getHeight());
//		}
//		fft.forEach(px->px.setReal(px.real()/(fft.getWidth()*fft.getHeight())));
//		ImageFrame.display(fft.getDelegate().getChannelImage(0)./*scaleChannelToUnitRange(0).*/toBufferedImage());
//	}
//
}


