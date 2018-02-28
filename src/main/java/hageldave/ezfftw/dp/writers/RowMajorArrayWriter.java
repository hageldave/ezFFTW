package hageldave.ezfftw.dp.writers;

import hageldave.ezfftw.GeneralUtils;
import hageldave.ezfftw.Annotations.DoublePrecisionVersion;

@DoublePrecisionVersion
public class RowMajorArrayWriter implements RealValuedWriter {

	public final double[] array;
	private final long[] dimensions;

	public RowMajorArrayWriter(double[] array, long... dimensions) {
		this.array = array;
		this.dimensions = dimensions;
	}

	@Override
	public void setValueAt(double val, long... coordinates) {
		array[(int)GeneralUtils.indexFromCoordinates(coordinates, dimensions)] = val;
	}
	
}