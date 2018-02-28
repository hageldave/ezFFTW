package hageldave.ezfftw.dp.writers;

import hageldave.ezfftw.Annotations.DoublePrecisionVersion;

@DoublePrecisionVersion
public interface ComplexValuedWriter {
	void setValueAt(double value, boolean imaginary, long... coordinates);

	default ComplexValuedWriter swapRealImaginary(){
		ComplexValuedWriter self = this;
		return (value, imaginary, coordinates) -> self.setValueAt(value, !imaginary, coordinates);
	}

	default RealValuedWriter getPartWriter(boolean imaginary){
		ComplexValuedWriter self = this;
		return (value, coordinates) -> self.setValueAt(value, imaginary, coordinates);
	}
}