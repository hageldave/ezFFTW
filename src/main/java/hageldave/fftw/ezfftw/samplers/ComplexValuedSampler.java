package hageldave.fftw.ezfftw.samplers;

public interface ComplexValuedSampler {
	double getValueAt(boolean imaginary, int...coordinates);
	
	default ComplexValuedSampler swapRealImaginary(){
		ComplexValuedSampler self = this;
		return (imaginary, coordinates) -> self.getValueAt(!imaginary, coordinates);
	}
}