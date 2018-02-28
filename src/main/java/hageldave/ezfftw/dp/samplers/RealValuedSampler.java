package hageldave.ezfftw.dp.samplers;

import hageldave.ezfftw.Annotations.DoublePrecisionVersion;

@DoublePrecisionVersion
public interface RealValuedSampler {
	double getValueAt(long... coordinates);

	default ComplexValuedSampler addImaginaryComponent(RealValuedSampler imaginarySampler){
		RealValuedSampler self = this;
		return (imaginary, coordinates) -> {
			if(imaginary) return imaginarySampler.getValueAt(coordinates);
			else return self.getValueAt(coordinates);
		};
	}

}