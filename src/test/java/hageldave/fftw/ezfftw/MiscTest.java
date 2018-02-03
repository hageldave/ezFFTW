package hageldave.fftw.ezfftw;

import hageldave.ezfftw.FFT;
import hageldave.ezfftw.FFTW_Guru;
import hageldave.ezfftw.NativeDoubleArray;
import hageldave.ezfftw.samplers.RowMajorArraySampler;
import hageldave.ezfftw.writers.RowMajorArrayWriter;
import hageldave.imagingkit.core.Img;
import hageldave.imagingkit.core.io.ImageLoader;
import hageldave.imagingkit.core.scientific.ColorImg;
import hageldave.imagingkit.core.util.ImageFrame;
import hageldave.imagingkit.fourier.ComplexImg;

public class MiscTest {

	public static void main(String[] args) {
		test();
	}

	static void test() {
		long w = 2048*2-1;
		long h = 2048*2+1;
		double[] in = new double[(int)(w*h)];
		double[] rout = new double[in.length];
		double[] iout = new double[in.length];
		for(int i = 0; i < in.length; i++) in[i] = Math.random();

		FFTW_Guru.execute_split_r2c(new NativeDoubleArray(4), new NativeDoubleArray(4), new NativeDoubleArray(4), 2,2);


		{
			long t = System.currentTimeMillis();
			FFT.fft(in, rout, iout, w,h);
			System.out.println("direct:"+(System.currentTimeMillis()-t));
		}
		
		{
			RowMajorArraySampler srin = new RowMajorArraySampler(in, w,h);
			RowMajorArrayWriter wrout = new RowMajorArrayWriter(rout, w,h);
			RowMajorArrayWriter wiout = new RowMajorArrayWriter(iout, w,h);
			long t = System.currentTimeMillis();
			FFT.fft(srin, wrout.addImaginaryComponent(wiout), w,h);
			System.out.println("generic:"+(System.currentTimeMillis()-t));
		}
		
	}

	static void testImg(){
		Img gauss = ImageLoader.loadImgFromURL("https://d37wxxhohlp07s.cloudfront.net/s3_images/754946/20b826df-c159-46a3-99f9-42403d5be59e_inline.gif");
		ColorImg img = new ColorImg(gauss, false);
		ImageFrame.display(img.getRemoteBufferedImage());
		//			ComplexImg fft = fft2D_real2complexImg(img.getDataR(), img.getWidth(),img.getHeight());
		ComplexImg fft = new ComplexImg(img.getWidth(),img.getHeight());
		{
			FFT.fft(img.getDataR(), fft.getDataReal(), fft.getDataImag(), img.getWidth(),img.getHeight());
		}
		ComplexImg copy = fft.copy();
		copy.shiftCornerToCenter().enableSynchronizePowerSpectrum(true).getDelegate().forEach(px->px.setValue(2, Math.log(px.getValue(2))));
		ImageFrame.display(copy.getDelegate().scaleChannelToUnitRange(2).getChannelImage(2).toBufferedImage());
		//			ifft2D_complexImg2complexImg(fft, fft);
		{

			FFT.ifft(fft.getDataReal(), fft.getDataImag(), fft.getDataReal(), img.getWidth(),img.getHeight());
		}
		fft.forEach(px->px.setReal(px.real()/(fft.getWidth()*fft.getHeight())));
		ImageFrame.display(fft.getDelegate().getChannelImage(0)./*scaleChannelToUnitRange(0).*/toBufferedImage());

	}

}
