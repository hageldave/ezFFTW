//#FLOATGEN_SKIPFILE
package hageldave.ezfftw.dp.example;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.function.DoubleBinaryOperator;

import javax.imageio.ImageIO;

import hageldave.ezfftw.dp.FFTW_Guru;
import hageldave.ezfftw.dp.NativeRealArray;

public class FilteredBackProjection {

	public static void main(String[] args) throws IOException {
		DoubleBinaryOperator objectFn = (x,y)->{
			if(x<0.1 && x>-0.1 && y<0.1 && y>-0.1){
				return 0;
			} else {
				double circValue = ((x*2-0.3)*(x*2-0.3)+y*y*4);
				double squareValue = Math.abs(x*2+0.2)+Math.abs(y*2-0.3);
				return 1-Math.min(1, squareValue/2*circValue);
			}
		};
		ImageIO.write(makeImage(sampleObject(objectFn, 200)), "png", new File("object.png"));
		double[][] radon = radonTransform(objectFn, 200,200);
		ImageIO.write(makeImage(radon), "png", new File("radon.png"));
		double[][] fbp = filteredBackProjection(radon, 200);
		ImageIO.write(makeImage(radon), "png", new File("radon_filtered.png"));
		ImageIO.write(makeImage(fbp), "png", new File("fbp.png"));
	}
	
	static double[][] sampleObject(DoubleBinaryOperator objectFunction, int res){
		double[][] samples = new double[res][res];
		for(int j = 0; j < res; j++){
			double y = ((j*1.0/res)-0.5)*2;
			for(int i = 0; i < res; i++){
				double x = ((i*1.0/res)-0.5)*2;
				samples[j][i] = objectFunction.applyAsDouble(x, y);
			}
		}
		return samples;
	}

	static double[][] radonTransform(DoubleBinaryOperator objectFunction, int width, int numProjections) {
		final int numProjectionSamples = width;
		double[][] projections = new double[numProjections][width];
		for(int p = 0; p < numProjections; p++){
			double angle = p*Math.PI/numProjections;
			double sin = Math.sin(angle);
			double cos = Math.cos(angle);
			for(int r = 0; r < width; r++){
				double radius = ((r*1.0/width)-0.5)*2;
				double xSeed = radius*cos;
				double ySeed = radius*sin;
				double sum = 0;
				for(int i = 0; i < numProjectionSamples; i++){
					double t = ((i*1.0/numProjectionSamples)-0.5)*2;
					double x = xSeed + t*sin;
					double y = ySeed - t*cos;
					sum += objectFunction.applyAsDouble(x, y);
				}
				sum /= numProjectionSamples;
				projections[p][r] = sum;
			}
		}
		return projections;
	}

	private static double[][] filteredBackProjection(double[][] radon, int outputResolution) {
		/* apply ramp filter to radon transform - 1st step fourier transform
		 * (every line is one projection and has to be filtered separately)
		 */
		try(
				NativeRealArray projection = new NativeRealArray(radon[0].length);
				NativeRealArray fft_r = new NativeRealArray(projection.length);
				NativeRealArray fft_i = new NativeRealArray(projection.length);
		){
			for(int i = 0; i < radon.length; i++){
				projection.set(radon[i]);
				FFTW_Guru.execute_split_r2c(projection, fft_r, fft_i, projection.length);
				projection.get(0, radon[i]);
				// 2nd step apply ramp filter ( multiply by abs(freq) with normalized freq )
				long spectrumWidth = projection.length;
				long highestFreq = spectrumWidth/2;
				for(long k = 0; k < spectrumWidth; k++){
					long freq = ((k+highestFreq)%spectrumWidth)-highestFreq;
					double scaling = Math.abs(freq*1.0/highestFreq);
					fft_r.set(k, fft_r.get(k)*scaling);
					fft_i.set(k, fft_i.get(k)*scaling);
				}
				// 3rd step, inverse fft
				FFTW_Guru.execute_split_c2r(fft_r, fft_i, projection, projection.length);
				projection.get(0, radon[i]);
			}
		}
		// now do back projection
		double[][] output = new double[outputResolution][outputResolution];
		double toRadians = Math.PI/radon.length;
		int projectionWidth = radon[0].length;
		for(int i = 0; i < outputResolution; i++){
			double y = ((i*1.0/outputResolution)-0.5)*2;
			for(int j = 0; j < outputResolution; j++){
				double x = ((j*1.0/outputResolution)-0.5)*2;
				double sum = 0;
				int numSummands = 0;
				// for each projection find radius where [x,y] was projected to
				for(int p = 0; p < radon.length; p++){
					double angle = p*toRadians;
					double radius = x*Math.cos(angle)+y*Math.sin(angle);
					int r = (int)((radius+1)/2*(projectionWidth-1));
					if(r >=0 && r < projectionWidth){
						sum += radon[p][r];
						numSummands++;
					}
				}
				if(numSummands > 0)
					sum /= numSummands;
				output[i][j] = sum;
			}
		}
		return output;
	}
	
	static BufferedImage makeImage(double[][] values){
		BufferedImage img = new BufferedImage(values[0].length, values.length, BufferedImage.TYPE_INT_ARGB);
		for(int y = 0; y < img.getHeight(); y++){
			for(int x = 0; x < img.getWidth(); x++){
				int r,g,b; r=g=b= Math.min(255, Math.max(0,(int)(255*values[y][x])));
				int argb = 0xff000000 | r << 16 | g << 8 | b;
				img.setRGB(x, y, argb);
			}
		}
		return img;
	}
	
}
