package hageldave.fftw.ezfftw;

import org.junit.Test;

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
	
}
