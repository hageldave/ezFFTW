package hageldave.ezfftw.samplers;

public class MultiDimArraySampler2D implements RealValuedSampler {

	public final double[][] array;

	public MultiDimArraySampler2D(double[][] array) {
		this.array = array;
	}

	@Override
	public double getValueAt(long... coordinates) {
		return array[(int)coordinates[0]][(int)coordinates[1]];
		
	}
}