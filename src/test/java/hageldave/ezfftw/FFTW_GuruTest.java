package hageldave.ezfftw;

import static org.junit.Assert.*;
import org.junit.Test;

import hageldave.ezfftw.FFTW_Guru;
import hageldave.ezfftw.NativeDoubleArray;

public class FFTW_GuruTest {

	@Test
	public void testFunctionality() {
		try	(
				NativeDoubleArray a1 = new NativeDoubleArray(64);
				NativeDoubleArray a2 = new NativeDoubleArray(64);
				)
		{
			a1.fill(0);
			// exec r2c with input a1 and outputs a1,a2
			FFTW_Guru.execute_split_r2c(a1, a1, a2, 8,8);
			// since all values were 0, no frequencies should be present and DC is also 0
			for(long i = 0; i < 64; i++){
				assertEquals(0, a1.get(i), 0);
				assertEquals(0, a2.get(i), 0);
			}

			a1.fill(2);
			// exec r2c with input a1 and outputs a1,a2
			FFTW_Guru.execute_split_r2c(a1, a1, a2, 8,8);
			// since all values were 2, no frequencies should be present and DC is sum of grey values (2*64)
			for(long i = 1; i < 64; i++){
				assertEquals(0, a1.get(i), 0);
				assertEquals(0, a2.get(i), 0);
			}
			assertEquals(64*2,a1.get(0),0); // DC check
			assertEquals(0, a2.get(0),0);

			// do ifft of this (c2r)
			FFTW_Guru.execute_split_c2r(a1, a2, a1, 8,8);
			// should recover original signal scaled by number of elements
			for(long i = 0; i < 64; i++){
				assertEquals(2*64, a1.get(i), 0);
				assertEquals(0, a2.get(i), 0);
			}

			// do again but with c2c
			a1.fill(3);
			FFTW_Guru.execute_split_c2c(a1, a2, a1, a2, 8,8);
			// since all values were 3, no frequencies should be present and DC is sum of grey values (3*64)
			for(long i = 1; i < 64; i++){
				assertEquals(0, a1.get(i), 0);
				assertEquals(0, a2.get(i), 0);
			}
			assertEquals(64*3,a1.get(0),0); // DC check
			assertEquals(0, a2.get(0),0);

			// do ifft (using swapped arguments)
			FFTW_Guru.execute_split_c2c(a2, a1, a2, a1, 8,8);
			// should recover original signal scaled by number of elements
			for(long i = 0; i < 64; i++){
				assertEquals(3*64, a1.get(i), 0);
				assertEquals(0, a2.get(i), 0);
			}
		}

		try(
				NativeDoubleArray a1 = new NativeDoubleArray(4);
				NativeDoubleArray a2 = new NativeDoubleArray(4);
				)
		{
			a1.set(-1.0, 1.0, 1.0, -1.0); // sum=0
			FFTW_Guru.execute_split_r2c(a1, a1, a2, 2,2);
			assertEquals(0, a1.get(0),0);
		}
	}

	@Test
	public void testExceptions() {
		try(
				NativeDoubleArray a1 = new NativeDoubleArray(4);
				NativeDoubleArray a2 = new NativeDoubleArray(4);
				NativeDoubleArray a3 = new NativeDoubleArray(5);
				)
		{
			// nullpointer for null argument arrays
			JunitUtils.testException(()->FFTW_Guru.execute_split_r2c(null, a1, a2, 2,2), NullPointerException.class);
			JunitUtils.testException(()->FFTW_Guru.execute_split_r2c(a1, null, a2, 2,2), NullPointerException.class);
			JunitUtils.testException(()->FFTW_Guru.execute_split_r2c(a1, a1, null, 2,2), NullPointerException.class);
			JunitUtils.testException(()->FFTW_Guru.execute_split_c2r(null, a1, a2, 2,2), NullPointerException.class);
			JunitUtils.testException(()->FFTW_Guru.execute_split_c2r(a1, null, a2, 2,2), NullPointerException.class);
			JunitUtils.testException(()->FFTW_Guru.execute_split_c2r(a1, a1, null, 2,2), NullPointerException.class);
			JunitUtils.testException(()->FFTW_Guru.execute_split_c2c(null, a2, a1, a2, 2,2), NullPointerException.class);
			JunitUtils.testException(()->FFTW_Guru.execute_split_c2c(a1, null, a1, a2, 2,2), NullPointerException.class);
			JunitUtils.testException(()->FFTW_Guru.execute_split_c2c(a1, a2, null, a2, 2,2), NullPointerException.class);
			JunitUtils.testException(()->FFTW_Guru.execute_split_c2c(a1, a2, a1, null, 2,2), NullPointerException.class);

			// missing dimensions
			JunitUtils.testException(()->FFTW_Guru.execute_split_r2c(a1, a1, a2), IllegalArgumentException.class);
			JunitUtils.testException(()->FFTW_Guru.execute_split_c2r(a1, a2, a1), IllegalArgumentException.class);
			JunitUtils.testException(()->FFTW_Guru.execute_split_c2c(a1, a2, a1, a2), IllegalArgumentException.class);
			// non positive dimensions
			JunitUtils.testException(()->FFTW_Guru.execute_split_r2c(a1, a1, a2, -2,-2), IllegalArgumentException.class);
			JunitUtils.testException(()->FFTW_Guru.execute_split_c2r(a1, a2, a1, -2,-2), IllegalArgumentException.class);
			JunitUtils.testException(()->FFTW_Guru.execute_split_c2c(a1, a2, a1, a2, -2,-2), IllegalArgumentException.class);

			// wrong number of elements (assuming dimensions 2x2 but a3 is of length 5)
			JunitUtils.testException(()->FFTW_Guru.execute_split_r2c(a1, a1, a3, 2,2), IllegalArgumentException.class);
			JunitUtils.testException(()->FFTW_Guru.execute_split_r2c(a1, a3, a2, 2,2), IllegalArgumentException.class);
			JunitUtils.testException(()->FFTW_Guru.execute_split_r2c(a3, a1, a2, 2,2), IllegalArgumentException.class);
			JunitUtils.testException(()->FFTW_Guru.execute_split_c2r(a1, a2, a3, 2,2), IllegalArgumentException.class);
			JunitUtils.testException(()->FFTW_Guru.execute_split_c2r(a1, a3, a2, 2,2), IllegalArgumentException.class);
			JunitUtils.testException(()->FFTW_Guru.execute_split_c2r(a3, a1, a2, 2,2), IllegalArgumentException.class);
			JunitUtils.testException(()->FFTW_Guru.execute_split_c2c(a2, a1, a2, a3, 2,2), IllegalArgumentException.class);
			JunitUtils.testException(()->FFTW_Guru.execute_split_c2c(a2, a1, a3, a2, 2,2), IllegalArgumentException.class);
			JunitUtils.testException(()->FFTW_Guru.execute_split_c2c(a2, a3, a1, a2, 2,2), IllegalArgumentException.class);
			JunitUtils.testException(()->FFTW_Guru.execute_split_c2c(a3, a1, a2, a1, 2,2), IllegalArgumentException.class);
		}

	}


}
