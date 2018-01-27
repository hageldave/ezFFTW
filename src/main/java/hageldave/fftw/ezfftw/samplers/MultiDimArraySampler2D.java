package hageldave.fftw.ezfftw.samplers;

public class MultiDimArraySampler2D implements RealValuedSampler {

	public final double[][] array;

	public MultiDimArraySampler2D(double[][] array) {
		this.array = array;
	}

	@Override
	public double getValueAt(int... coordinates) {
		return array[coordinates[0]][coordinates[1]];
	}
}