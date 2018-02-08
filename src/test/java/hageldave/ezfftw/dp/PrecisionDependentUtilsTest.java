package hageldave.ezfftw.dp;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import hageldave.ezfftw.JunitUtils;

public class PrecisionDependentUtilsTest {

	@Test
	public void testSanity(){
		PrecisionDependentUtils.sanityCheckArray(new double[]{1,2,3}, 3, "");
		JunitUtils.testException(()->PrecisionDependentUtils.sanityCheckArray(null, 3, ""), NullPointerException.class);
		JunitUtils.testException(()->PrecisionDependentUtils.sanityCheckArray(new double[]{1,2,3,4}, 3, ""), IllegalArgumentException.class);
	}

	@Test
	public void testNativeDoubleArray() {
		try(NativeRealArray a = new NativeRealArray(10);){
			double pi = ((double)3.1415);
			a.fill(pi);
			PrecisionDependentUtils.readNativeArrayToWriter(a, (v, coords)->{
				assertEquals(1, coords.length);
				assertEquals(pi, v, 0);
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
