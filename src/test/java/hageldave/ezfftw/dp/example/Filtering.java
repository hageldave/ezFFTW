//#FLOATGEN_SKIPFILE
package hageldave.ezfftw.dp.example;

import java.awt.image.BufferedImage;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

import javax.imageio.ImageIO;

import hageldave.ezfftw.dp.FFT;
import hageldave.ezfftw.dp.RowMajorArrayAccessor;
import hageldave.ezfftw.dp.samplers.ComplexValuedSampler;
import hageldave.ezfftw.dp.samplers.RealValuedSampler;
import hageldave.ezfftw.dp.writers.RealValuedWriter;

public class Filtering {

	public static void main(String[] args)  {
		try (
			InputStream input = new URL("http://r0k.us/graphics/kodak/kodak/kodim01.png").openStream();
			OutputStream output = new FileOutputStream("filteredImage.png");
		){
			bandPassImageFilter(input, output, "png");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	static void bandPassImageFilter(InputStream input, OutputStream output, String outFormat){
		try {
			// load image
			BufferedImage loadedImg = ImageIO.read(input);
			final int width = loadedImg.getWidth();
			final int height = loadedImg.getHeight();
			// make sampler for image
			RealValuedSampler sampler = new RealValuedSampler() {
				@Override
				public double getValueAt(long... coordinates) {
					// we know coordinates will be 2D and in range of image dimensions
					int rgb = loadedImg.getRGB((int)coordinates[0], (int)coordinates[1]);
					// return average grey in range [0,1]
					return ((rgb>>16&0xff)+(rgb>>8&0xff)+(rgb&0xff))/(3*255.0); 
				}
			};
			// make fft storage (RowMajorArrayAccessor implements sampler and writer)
			RowMajorArrayAccessor realPart = new RowMajorArrayAccessor(new double[width*height], width,height);
			RowMajorArrayAccessor imagPart = new RowMajorArrayAccessor(new double[width*height], width,height);
			// execute fft
			FFT.fft(sampler, realPart.combineToComplexWriter(imagPart), width, height);
			// make sampler that will filter out frequencies
			ComplexValuedSampler filterSampler = new ComplexValuedSampler() {
				@Override
				public double getValueAt(boolean imaginary, long... coordinates) {
					// get coordinates with centered DC
					double x = ( ((coordinates[0]+ width/2)% width)- width/2 );
					double y = ( ((coordinates[1]+height/2)%height)-height/2 );
					// get length (corresponds to frequency)
					double l = Math.sqrt(x*x+y*y);
					// define band pass frequencies to be in ]40, 80[
					if(l > 50 && l < 100){
						return imaginary ? imagPart.getValueAt(coordinates[0],coordinates[1]) 
										: realPart.getValueAt(coordinates[0],coordinates[1]);
					} else return 0;
				}
			};
			// make target image for writing result
			BufferedImage filteredImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
			RealValuedWriter imgWriter = new RealValuedWriter() {
				@Override
				public void setValueAt(double value, long... coordinates) {
					value /= width*height; // remove scaling (FFTW does it like this)
					value = Math.max(0, Math.min(value, 1)); // clamp value between [0,1]
					int byteval = (int)(value*255);
					int argb = 0xff000000|(byteval<<16)|(byteval<<8)|byteval; // make greyscale argb
					filteredImg.setRGB((int)coordinates[0], (int)coordinates[1], argb);
							
				}
			};
			// execute inverse fft
			FFT.ifft(filterSampler, imgWriter, width, height);
			ImageIO.write(filteredImg, outFormat, output);
		} catch (IOException e){
			e.printStackTrace();
		}
	}
}
