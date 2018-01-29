package hageldave.fftw.ezfftw.writers;

public interface RealValuedWriter {
	void setValueAt(double value, long... coordinates);

	default ComplexValuedWriter addImaginaryComponent(RealValuedWriter imaginaryWriter){
		RealValuedWriter self = this;
		return (value, imaginary, coordinates) -> {
			if(imaginary)
				imaginaryWriter.setValueAt(value, coordinates);
			else
				self.setValueAt(value, coordinates);
		};
	}
}