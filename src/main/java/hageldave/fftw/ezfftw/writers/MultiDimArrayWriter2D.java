package hageldave.fftw.ezfftw.writers;

public class MultiDimArrayWriter2D implements RealValuedWriter {

	public final double[][] array;

	public MultiDimArrayWriter2D(double[][] array) {
		this.array = array;
	}

	@Override
	public void setValueAt(double value, long... coordinates) {
		array[(int)coordinates[0]][(int)coordinates[1]] = value;
	}
}