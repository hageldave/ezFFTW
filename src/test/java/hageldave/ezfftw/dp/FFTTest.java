package hageldave.ezfftw.dp;

import static org.junit.Assert.*;
import static hageldave.ezfftw.JunitUtils.*;

import java.util.Arrays;

import org.junit.Test;

import hageldave.ezfftw.dp.samplers.RowMajorArraySampler;
import hageldave.ezfftw.dp.writers.RowMajorArrayWriter;

/* --- DOUBLE PRECISION VERSION --- */
public class FFTTest {

	@Test
	public void test_r2c2r() {
		double[] realIn = new double[64];
		// generate sine wave with +1 y offset
		double to2pi = ((double)(Math.PI*2/realIn.length));
		for(int i = 0; i < realIn.length; i++){
			realIn[i] = (double)Math.sin(i*to2pi)+1;
		}

		double[] realOut = new double[realIn.length];
		double[] imagOut = new double[realIn.length];
		double[] realInToCheck = new double[realIn.length];

		{// array arguments
			FFT.fft(realIn, realOut, imagOut, realIn.length);

			// sum of sinus values has to be zero, but due to y offset by 1 is 1*length. (DC check)
			assertEquals(0, imagOut[0], 0);
			assertEquals(realIn.length, realOut[0], doubleTolerance);
			// for sinus the imaginary part has to show frequency != 0 for 1hz
			// also real part has to zero except for 0hz
			for(int i = 0; i < realIn.length; i++){
				if( i == 1 || i == realIn.length-1 ){
					assertNotEquals(0, imagOut[i], 1);
				} else {
					assertEquals(0, imagOut[i], doubleTolerance);
				}
				if(i != 0){
					assertEquals(0, realOut[i], doubleTolerance);
				}
			}

			// back transform
			FFT.ifft(realOut, imagOut, realInToCheck, realIn.length);

			// should restore original but scaled by number of elements
			for(int i = 0; i < realIn.length; i++){
				assertEquals(realIn[i]*realIn.length, realInToCheck[i], doubleTolerance);
			}
		}
		
		Arrays.fill(realInToCheck, 0);
		Arrays.fill(realOut, 0);
		Arrays.fill(imagOut, 0);
		
		{// sampler/writer arguments
			
			FFT.fft(	new RowMajorArraySampler(realIn, realIn.length), 
						new RowMajorArrayWriter(realOut, realIn.length)
						.addImaginaryComponent( 
						new RowMajorArrayWriter(imagOut, realIn.length)), 
						realIn.length);

			// sum of sinus values has to be zero, but due to y offset by 1 is 1*length. (DC check)
			assertEquals(0, imagOut[0], 0);
			assertEquals(realIn.length, realOut[0], doubleTolerance);
			// for sinus the imaginary part has to show frequency != 0 for 1hz
			// also real part has to zero except for 0hz
			for(int i = 0; i < realIn.length; i++){
				if( i == 1 || i == realIn.length-1 ){
					assertNotEquals(0, imagOut[i], 1);
				} else {
					assertEquals(0, imagOut[i], doubleTolerance);
				}
				if(i != 0){
					assertEquals(0, realOut[i], doubleTolerance);
				}
			}

			// back transform
			FFT.ifft(	new RowMajorArraySampler(realOut, realIn.length)
						.addImaginaryComponent(
						new RowMajorArraySampler(imagOut, realIn.length)), 
						new RowMajorArrayWriter(realInToCheck, realIn.length), 
						realIn.length);

			// should restore original but scaled by number of elements
			for(int i = 0; i < realIn.length; i++){
				assertEquals(realIn[i]*realIn.length, realInToCheck[i], doubleTolerance);
			}
		}

	}

}
