package hageldave.ezfftw.dp.samplers;

import hageldave.ezfftw.GeneralUtils;

/* --- DOUBLE PRECISION VERSION --- */
public class RowMajorArraySampler implements RealValuedSampler {

	public final double[] array;
	private final long[] dimensions;

	public RowMajorArraySampler(double[] array, long... dimensions) {
		this.array = array;
		this.dimensions = dimensions;
	}

	@Override
	public double getValueAt(long... coordinates) {
		return array[(int)GeneralUtils.indexFromCoordinates(coordinates, dimensions)];
	}
}