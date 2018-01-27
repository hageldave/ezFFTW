package hageldave.fftw.ezfftw.samplers;

import hageldave.fftw.ezfftw.Utils;

public class RowMajorArraySampler implements RealValuedSampler {

	public final double[] array;
	private final int[] dimensions;

	public RowMajorArraySampler(double[] array, int... dimensions) {
		this.array = array;
		this.dimensions = dimensions;
	}

	@Override
	public double getValueAt(int... coordinates) {
		return array[Utils.indexFromCoordinates(coordinates, dimensions)];
	}
}