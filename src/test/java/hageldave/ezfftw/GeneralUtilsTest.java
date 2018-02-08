package hageldave.ezfftw;


import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class GeneralUtilsTest {

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




}
