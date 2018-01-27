package hageldave.fftw.ezfftw.writers;

import hageldave.fftw.ezfftw.Utils;

public class RowMajorArrayWriter implements RealValuedWriter {

	public final double[] array;
	private final int[] dimensions;

	public RowMajorArrayWriter(double[] array, int[] dimensions) {
		this.array = array;
		this.dimensions = dimensions;
	}

	@Override
	public void setValueAt(double val, int... coordinates) {
		array[Utils.indexFromCoordinates(coordinates, dimensions)] = val;
	}
	
}