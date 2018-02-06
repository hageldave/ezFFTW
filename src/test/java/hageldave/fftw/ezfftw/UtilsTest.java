package hageldave.fftw.ezfftw;


import static org.junit.Assert.*;
import org.junit.Test;

import hageldave.ezfftw.NativeDoubleArray;
import hageldave.ezfftw.Utils;

public class UtilsTest {

	@Test
	public void testSanity() {
		Utils.requirePositive(1, ()->"");
		JunitUtils.testException(()->Utils.requirePositive(0, ()->""), IllegalArgumentException.class);
		JunitUtils.testException(()->Utils.requirePositive(-1, ()->""), IllegalArgumentException.class);

		Utils.requirePosititveDimensions(1,2,3,1);
		JunitUtils.testException(()->Utils.requirePosititveDimensions(0,2,3,1), IllegalArgumentException.class);
		JunitUtils.testException(()->Utils.requirePosititveDimensions(1,2,3,-1), IllegalArgumentException.class);

		String a = "hello";
		String b = "hel"; b += "lo";
		Utils.requireEqual(a, b, ()->"");
		Utils.requireEqual(1, 1, ()->"");
		JunitUtils.testException(()->Utils.requireEqual(1, 2, ()->""), IllegalArgumentException.class);
		JunitUtils.testException(()->Utils.requireEqual("hello", "hellu", ()->""), IllegalArgumentException.class);

		Utils.sanityCheckArray(new double[]{1,2,3}, 3, "");
		JunitUtils.testException(()->Utils.sanityCheckArray(null, 3, ""), NullPointerException.class);
		JunitUtils.testException(()->Utils.sanityCheckArray(new double[]{1,2,3,4}, 3, ""), IllegalArgumentException.class);
	}

	@Test
	public void testCoordinates() {
		assertEquals(0,Utils.numElementsFromDimensions(new long[]{}));
		assertEquals(0,Utils.numElementsFromDimensions(new long[]{0}));
		assertEquals(1,Utils.numElementsFromDimensions(new long[]{1}));
		assertEquals(1,Utils.numElementsFromDimensions(new long[]{1,1}));
		assertEquals(2,Utils.numElementsFromDimensions(new long[]{1,2}));
		assertEquals(4,Utils.numElementsFromDimensions(new long[]{2,2}));
		assertEquals(4,Utils.numElementsFromDimensions(new long[]{1,2,2}));

		long[] dims = new long[]{3,3,3};
		assertEquals(0, Utils.indexFromCoordinates(new long[]{0,0,0}, dims));
		assertEquals(1, Utils.indexFromCoordinates(new long[]{1,0,0}, dims));
		assertEquals(3, Utils.indexFromCoordinates(new long[]{0,1,0}, dims));
		assertEquals(3*3, Utils.indexFromCoordinates(new long[]{0,0,1}, dims));
		assertEquals(3*3*3-1, Utils.indexFromCoordinates(new long[]{2,2,2}, dims));

		long[] coords = new long[]{0,0,0};
		Utils.incrementCoords(coords, dims);
		assertArrayEquals(new long[]{1,0,0}, coords);
		Utils.incrementCoords(coords, dims);
		assertArrayEquals(new long[]{2,0,0}, coords);
		Utils.incrementCoords(coords, dims);
		assertArrayEquals(new long[]{0,1,0}, coords);

		for(
				long i = Utils.indexFromCoordinates(coords, dims);
				i < Utils.numElementsFromDimensions(dims);
				i++, Utils.incrementCoords(coords, dims))
		{
			assertEquals(i, Utils.indexFromCoordinates(coords, dims));
		}
		// test that last increment did not overflow but set most significant coordinate to most significant dimension
		assertArrayEquals(new long[]{0,0,3}, coords);
	}


	@Test
	public void testNativeDoubleArray() {
		try(NativeDoubleArray a = new NativeDoubleArray(10);){
			a.fill(3.1415);
			Utils.readNativeArrayToWriter(a, (v, coords)->{
				assertEquals(1, coords.length);
				assertEquals(3.1415, v, 0);
			},a.length);

			Utils.fillNativeArrayFromSampler(a, (coords)->coords[0], a.length);
			for(long i = 0; i < a.length; i++){
				assertEquals(i, a.get(i), 0);
			}

			Utils.readNativeArrayToWriter(a, (v, coords)->{
				assertEquals(2, coords.length);
				if(coords[1]==0){
					assertEquals(coords[0], v, 0);
				} else {
					assertEquals(coords[0]+5, v, 0);
				}
			}, 5,2);

			Utils.fillNativeArrayFromSampler(a, (coords)->coords[0]+coords[1]*10, 5,2);
			for(long i = 0; i < a.length; i++){
				if(i < 5){
					assertEquals(i, a.get(i), 0);
				} else {
					assertEquals((i-5)+10, a.get(i), 0);
				}
			}

			// exceptions
			JunitUtils.testException(()->{
				Utils.fillNativeArrayFromSampler(a, (coords)->0);
			}, IllegalArgumentException.class);
			JunitUtils.testException(()->{
				Utils.fillNativeArrayFromSampler(a, (coords)->0, -1,-10);
			}, IllegalArgumentException.class);
			JunitUtils.testException(()->{
				Utils.fillNativeArrayFromSampler(a, (coords)->0, 2,2,2);
			}, IllegalArgumentException.class);

			JunitUtils.testException(()->{
				Utils.readNativeArrayToWriter(a, (v,coords)->{});
			}, IllegalArgumentException.class);
			JunitUtils.testException(()->{
				Utils.readNativeArrayToWriter(a, (v,coords)->{},-1,-10);
			}, IllegalArgumentException.class);
			JunitUtils.testException(()->{
				Utils.readNativeArrayToWriter(a, (v,coords)->{},2,2,2);
			}, IllegalArgumentException.class);
		}

	}

}
