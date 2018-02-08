package hageldave.ezfftw.dp.samplers;

public interface ComplexValuedSampler {
	double getValueAt(boolean imaginary, long...coordinates);

	default ComplexValuedSampler swapRealImaginary(){
		ComplexValuedSampler self = this;
		return (imaginary, coordinates) -> self.getValueAt(!imaginary, coordinates);
	}

	default RealValuedSampler getPartSampler(boolean imaginary){
		ComplexValuedSampler self = this;
		return (coordinates)->self.getValueAt(imaginary, coordinates);
	}
}