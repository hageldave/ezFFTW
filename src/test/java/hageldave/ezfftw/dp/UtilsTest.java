package hageldave.ezfftw.dp;


import static org.junit.Assert.*;
import org.junit.Test;

import hageldave.ezfftw.GeneralUtils;
import hageldave.ezfftw.dp.NativeRealArray;

public class UtilsTest {

	@Test
	public void testSanity() {
		GeneralUtils.requirePositive(1, ()->"");
		JunitUtils.testException(()->GeneralUtils.requirePositive(0, ()->""), IllegalArgumentException.class);
		JunitUtils.testException(()->GeneralUtils.requirePositive(-1, ()->""), IllegalArgumentException.class);

		GeneralUtils.requirePosititveDimensions(1,2,3,1);
		JunitUtils.testException(()->GeneralUtils.requirePosititveDimensions(0,2,3,1), IllegalArgumentException.class);
		JunitUtils.testException(()->GeneralUtils.requirePosititveDimensions(1,2,3,-1), IllegalArgumentException.class);

		String a = "hello";
		String b = "hel"; b += "lo";
		GeneralUtils.requireEqual(a, b, ()->"");
		GeneralUtils.requireEqual(1, 1, ()->"");
		JunitUtils.testException(()->GeneralUtils.requireEqual(1, 2, ()->""), IllegalArgumentException.class);
		JunitUtils.testException(()->GeneralUtils.requireEqual("hello", "hellu", ()->""), IllegalArgumentException.class);

		PrecisionDependentUtils.sanityCheckArray(new double[]{1,2,3}, 3, "");
		JunitUtils.testException(()->PrecisionDependentUtils.sanityCheckArray(null, 3, ""), NullPointerException.class);
		JunitUtils.testException(()->PrecisionDependentUtils.sanityCheckArray(new double[]{1,2,3,4}, 3, ""), IllegalArgumentException.class);
	}

	@Test
	public void testCoordinates() {
		assertEquals(0,GeneralUtils.numElementsFromDimensions(new long[]{}));
		assertEquals(0,GeneralUtils.numElementsFromDimensions(new long[]{0}));
		assertEquals(1,GeneralUtils.numElementsFromDimensions(new long[]{1}));
		assertEquals(1,GeneralUtils.numElementsFromDimensions(new long[]{1,1}));
		assertEquals(2,GeneralUtils.numElementsFromDimensions(new long[]{1,2}));
		assertEquals(4,GeneralUtils.numElementsFromDimensions(new long[]{2,2}));
		assertEquals(4,GeneralUtils.numElementsFromDimensions(new long[]{1,2,2}));

		long[] dims = new long[]{3,3,3};
		assertEquals(0, GeneralUtils.indexFromCoordinates(new long[]{0,0,0}, dims));
		assertEquals(1, GeneralUtils.indexFromCoordinates(new long[]{1,0,0}, dims));
		assertEquals(3, GeneralUtils.indexFromCoordinates(new long[]{0,1,0}, dims));
		assertEquals(3*3, GeneralUtils.indexFromCoordinates(new long[]{0,0,1}, dims));
		assertEquals(3*3*3-1, GeneralUtils.indexFromCoordinates(new long[]{2,2,2}, dims));

		long[] coords = new long[]{0,0,0};
		GeneralUtils.incrementCoords(coords, dims);
		assertArrayEquals(new long[]{1,0,0}, coords);
		GeneralUtils.incrementCoords(coords, dims);
		assertArrayEquals(new long[]{2,0,0}, coords);
		GeneralUtils.incrementCoords(coords, dims);
		assertArrayEquals(new long[]{0,1,0}, coords);

		for(
				long i = GeneralUtils.indexFromCoordinates(coords, dims);
				i < GeneralUtils.numElementsFromDimensions(dims);
				i++, GeneralUtils.incrementCoords(coords, dims))
		{
			assertEquals(i, GeneralUtils.indexFromCoordinates(coords, dims));
		}
		// test that last increment did not overflow but set most significant coordinate to most significant dimension
		assertArrayEquals(new long[]{0,0,3}, coords);
	}


	@Test
	public void testNativeDoubleArray() {
		try(NativeRealArray a = new NativeRealArray(10);){
			a.fill(3.1415);
			PrecisionDependentUtils.readNativeArrayToWriter(a, (v, coords)->{
				assertEquals(1, coords.length);
				assertEquals(3.1415, v, 0);
			},a.length);

			PrecisionDependentUtils.fillNativeArrayFromSampler(a, (coords)->coords[0], a.length);
			for(long i = 0; i < a.length; i++){
				assertEquals(i, a.get(i), 0);
			}

			PrecisionDependentUtils.readNativeArrayToWriter(a, (v, coords)->{
				assertEquals(2, coords.length);
				if(coords[1]==0){
					assertEquals(coords[0], v, 0);
				} else {
					assertEquals(coords[0]+5, v, 0);
				}
			}, 5,2);

			PrecisionDependentUtils.fillNativeArrayFromSampler(a, (coords)->coords[0]+coords[1]*10, 5,2);
			for(long i = 0; i < a.length; i++){
				if(i < 5){
					assertEquals(i, a.get(i), 0);
				} else {
					assertEquals((i-5)+10, a.get(i), 0);
				}
			}

			// exceptions
			JunitUtils.testException(()->{
				PrecisionDependentUtils.fillNativeArrayFromSampler(a, (coords)->0);
			}, IllegalArgumentException.class);
			JunitUtils.testException(()->{
				PrecisionDependentUtils.fillNativeArrayFromSampler(a, (coords)->0, -1,-10);
			}, IllegalArgumentException.class);
			JunitUtils.testException(()->{
				PrecisionDependentUtils.fillNativeArrayFromSampler(a, (coords)->0, 2,2,2);
			}, IllegalArgumentException.class);

			JunitUtils.testException(()->{
				PrecisionDependentUtils.readNativeArrayToWriter(a, (v,coords)->{});
			}, IllegalArgumentException.class);
			JunitUtils.testException(()->{
				PrecisionDependentUtils.readNativeArrayToWriter(a, (v,coords)->{},-1,-10);
			}, IllegalArgumentException.class);
			JunitUtils.testException(()->{
				PrecisionDependentUtils.readNativeArrayToWriter(a, (v,coords)->{},2,2,2);
			}, IllegalArgumentException.class);
		}

	}

}
