package hageldave.fftw.ezfftw.writers;

public interface ComplexValuedWriter {
	void setValueAt(double value, boolean imaginary, long... coordinates);
	
	default ComplexValuedWriter swapRealImaginary(){
		ComplexValuedWriter self = this;
		return (value, imaginary, coordinates) -> self.setValueAt(value, !imaginary, coordinates);
	}
}