package hageldave.ezfftw.writers;

import hageldave.ezfftw.Utils;

public class RowMajorArrayWriter implements RealValuedWriter {

	public final double[] array;
	private final long[] dimensions;

	public RowMajorArrayWriter(double[] array, long... dimensions) {
		this.array = array;
		this.dimensions = dimensions;
	}

	@Override
	public void setValueAt(double val, long... coordinates) {
		array[(int)Utils.indexFromCoordinates(coordinates, dimensions)] = val;
	}
	
}