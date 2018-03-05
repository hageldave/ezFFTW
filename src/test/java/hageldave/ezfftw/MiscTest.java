package hageldave.ezfftw;

import hageldave.ezfftw.dp.FFT;
import hageldave.ezfftw.dp.FFTW_Guru;
import hageldave.ezfftw.dp.NativeRealArray;
import hageldave.ezfftw.dp.samplers.RowMajorArrayAccessor;
import hageldave.imagingkit.core.Img;
import hageldave.imagingkit.core.io.ImageLoader;
import hageldave.imagingkit.core.scientific.ColorImg;
import hageldave.imagingkit.core.util.ImageFrame;

public class MiscTest {

	public static void main(String[] args) {
		busa();
	}

	static void wusa() {
		long w = 2048*2-1;
		long h = 2048*2+1;
		double[] in = new double[(int)(w*h)];
		double[] rout = new double[in.length];
		double[] iout = new double[in.length];
		for(int i = 0; i < in.length; i++) in[i] = Math.random();

		FFTW_Guru.execute_split_r2c(new NativeRealArray(4), new NativeRealArray(4), new NativeRealArray(4), 2,2);


		{
			long t = System.currentTimeMillis();
			FFT.fft(in, rout, iout, w,h);
			System.out.println("direct:"+(System.currentTimeMillis()-t));
		}

		{
			RowMajorArrayAccessor srin = new RowMajorArrayAccessor(in, w,h);
			RowMajorArrayAccessor wrout = new RowMajorArrayAccessor(rout, w,h);
			RowMajorArrayAccessor wiout = new RowMajorArrayAccessor(iout, w,h);
			long t = System.currentTimeMillis();
			FFT.fft(srin, wrout.combineToComplexWriter(wiout), w,h);
			System.out.println("generic:"+(System.currentTimeMillis()-t));
		}

	}

	static void busa(){
		Img gauss = ImageLoader.loadImgFromURL("https://d37wxxhohlp07s.cloudfront.net/s3_images/754946/20b826df-c159-46a3-99f9-42403d5be59e_inline.gif");
		ColorImg img = new ColorImg(gauss, false);
		ImageFrame.display(img.getRemoteBufferedImage());
		//			ComplexImg fft = fft2D_real2complexImg(img.getDataR(), img.getWidth(),img.getHeight());
		ColorImg fft = new ColorImg(img.getWidth(),img.getHeight(),false);
		{
			FFT.fft(img.getDataR(), fft.getDataR(), fft.getDataG(), img.getWidth(),img.getHeight());
		}
		ColorImg copy = fft.copy();
		copy.forEach(px->px.setB_fromDouble(px.r_asDouble()*px.r_asDouble()+px.g_asDouble()*px.g_asDouble()));
		copy.forEach(px->px.setValue(2, Math.log(px.getValue(2))));
		ImageFrame.display(copy.scaleChannelToUnitRange(2).getChannelImage(2).toBufferedImage());
		copy.forEach(px->{
			if(px.getValue(2) > 0.5)
			{
				if((px.getXnormalized() > 0.1 && px.getXnormalized() < 0.9)
						|| (px.getYnormalized() > 0.1 && px.getYnormalized() < 0.9)) 
				{
					fft.getPixel(px.getX(), px.getY()).setR_fromDouble(0).setG_fromDouble(0);
					px.setB_fromDouble(0);
				}
			}
		});
		ImageFrame.display(copy.scaleChannelToUnitRange(2).getChannelImage(2).toBufferedImage());
		{

			FFT.ifft(fft.getDataR(), fft.getDataG(), fft.getDataR(), img.getWidth(),img.getHeight());
		}
		fft.forEach(px->px.setR_fromDouble(px.r_asDouble()/(fft.getWidth()*fft.getHeight())));
		ImageFrame.display(fft.getChannelImage(0)./*scaleChannelToUnitRange(0).*/toBufferedImage());

	}

}
