package hageldave.fftw.ezfftw.samplers;

import hageldave.fftw.ezfftw.Utils;

public class RowMajorArraySampler implements RealValuedSampler {

	public final double[] array;
	private final long[] dimensions;

	public RowMajorArraySampler(double[] array, long... dimensions) {
		this.array = array;
		this.dimensions = dimensions;
	}

	@Override
	public double getValueAt(long... coordinates) {
		return array[(int)Utils.indexFromCoordinates(coordinates, dimensions)];
	}
}