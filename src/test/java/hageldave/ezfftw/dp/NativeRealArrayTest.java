package hageldave.ezfftw.dp;

import static org.junit.Assert.*;
import org.junit.Test;

import hageldave.ezfftw.JunitUtils;
import hageldave.ezfftw.dp.NativeRealArray;

/* --- DOUBLE PRECISION VERSION --- */
public class NativeRealArrayTest {

	@Test
	public void test() {
		// just a single entry
		try(NativeRealArray a = new NativeRealArray(1);)
		{
			assertEquals(1, a.length);
			a.set(0, 0);
			assertEquals(0, a.get(0), 0);
			a.set(0, 1);
			assertEquals(1, a.get(0), 0);
		}

		// 10 entries
		try(NativeRealArray a = new NativeRealArray(10);)
		{
			double[] arr = new double[]{0,1,2,3,4,5,6,7,8,9};
			assertEquals(10, a.length);
			a.set(arr);
			for(int i = 0; i < a.length; i++){
				assertEquals(arr[i], a.get(i), 0);
			}
			double[] arr_ = a.get(0, 10);
			assertNotEquals(arr, arr_);
			assertArrayEquals(arr, arr_, 0);
			// just get a part of the array
			arr_ = a.get(2, 4);
			assertEquals(4, arr_.length);
			for(int i = 0; i < arr_.length; i++){
				assertEquals(arr[i+2], arr_[i], 0);
			}
			// now that arr_ is of length 4 use it as argument to get another part
			a.get(1, arr_);
			for(int i = 0; i < arr_.length; i++){
				assertEquals(arr[i+1], arr_[i], 0);
			}
			// lets put 2 elements starting at index 5 (element 5 and 6) into arr_ starting from 2 (element 2 and 3)
			a.get(5, 2, 2, arr_);
			// first two elements from before have not changed
			assertEquals(arr[0+1], arr_[0], 0);
			assertEquals(arr[1+1], arr_[1], 0);
			assertEquals(arr[5], arr_[2], 0);
			assertEquals(arr[6], arr_[3], 0);

			// change some entries in array
			a.set(4, 0,0,0);
			for(int i = 0; i < 4; i++){
				assertEquals(arr[i], a.get(i), 0);
			}
			assertEquals(0, a.get(4), 0);
			assertEquals(0, a.get(5), 0);
			assertEquals(0, a.get(6), 0);
			assertEquals(arr[7], a.get(7), 0);
		}

		try(NativeRealArray a = new NativeRealArray(10);){
			a.fill(0);
			for(long i = 0; i < a.length; i++)
				assertEquals(0, a.get(i), 0);
			a.fill(1);
			for(long i = 0; i < a.length; i++)
				assertEquals(1, a.get(i), 0);
		}

		try(NativeRealArray a = new NativeRealArray(0)){
			fail();
		} catch (Exception e) {
			assertEquals(IllegalArgumentException.class, e.getClass());
		}

		{
			NativeRealArray a = new NativeRealArray(1);
			a.close();
			// throws nullpointer exception instead of segfaulting
			JunitUtils.testException(()->a.get(0), NullPointerException.class);
		}
	}

}
