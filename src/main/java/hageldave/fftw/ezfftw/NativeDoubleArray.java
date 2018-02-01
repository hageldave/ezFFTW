package hageldave.fftw.ezfftw;

import org.bytedeco.javacpp.DoublePointer;

public class NativeDoubleArray implements AutoCloseable {

	public final long length;
	private final DoublePointer pointer;

	public NativeDoubleArray(long length) {
		FFTW_Initializer.initFFTW();
		Utils.requirePositive(length, ()->"Provided length is not positive");
		this.pointer = new DoublePointer(length);
		this.length = length;
	}

	public NativeDoubleArray set(long i, double v){
		pointer.position(0).put(i, v);
		return this;
	}

	public NativeDoubleArray set(double... values){
		return set(0, values);
	}

	public NativeDoubleArray set(long i, double... values){
		return set(i, values.length, 0, values);
	}

	public NativeDoubleArray set(long i, int length, int offset, double[] values){
		pointer.position(i).put(values, offset, length);
		return this;
	}

	public double get(long i){
		return pointer.position(0).get(i);
	}

	public double[] get(long i, int length, int offset, double[] destination){
		pointer.position(i).get(destination,offset,length);
		return destination;
	}

	public double[] get(long i, double[] destination) {
		return get(i, destination.length, 0, destination);
	}

	public double[] get(long i, int length){
		return get(i, length, 0, new double[length]);
	}

	public DoublePointer getPointer() {
		return pointer.position(0);
	}

	@Override
	public void close() {
		pointer.close();
	}

	@Override
	protected void finalize() throws Throwable {
		close();
		super.finalize();
	}


}
