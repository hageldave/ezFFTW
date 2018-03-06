package hageldave.ezfftw.dp.example;

import hageldave.ezfftw.dp.NativeRealArray;

public class ExamplesForJavadoc {

	/**
	 * Example in Class doc of {@link NativeRealArray}
	 */
	@SuppressWarnings({ "resource", "unused" })
	private static void exampleCode() {
		// suppose you have lots of data in 3D space.
		// So much it won't fit in a single java array.
		// 2048^3 > 2^32 --> 2048^3 * 8bytes = 64GB of memory
		double[][][] my3DArray = new double[2048][2048][2048];
		NativeRealArray natArray = new NativeRealArray(2048L*2048L*2048L);
		// lets populate natArray
		for(long i = 0; i < natArray.length; i++){
			double value = ((double)i)/natArray.length;
			natArray.set(i, value);
		}
		// lets read everything to my3DArray
		for(int i = 0; i < 2048; i++){
			for(int j = 0; j < 2048; j++){
				double[] row_ij = my3DArray[i][j];
				long stride = i*2048L*2048L + j*2048L;
				// read as much data as fits into row_ij starting from stride
				natArray.get(stride, row_ij);
			}
		}
	}
	
}
