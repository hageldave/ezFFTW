package com.github.hageldave.fftw.ezfftw;

public class Samplers {

	static interface RealValuedSampler {
		double getValueAt(int... coordinates);

		default ComplexValuedSampler addImaginaryComponent(RealValuedSampler imaginarySampler){
			RealValuedSampler self = this;
			return (imaginary, coordinates) -> {
				if(imaginary) return imaginarySampler.getValueAt(coordinates);
				else return self.getValueAt(coordinates);
			};
		}
	}

	static interface ComplexValuedSampler {
		double getValueAt(boolean imaginary, int...coordinates);
	}

	static class RowMajorArraySampler2D implements RealValuedSampler {

		public final double[] array;
		public final int stride;

		public RowMajorArraySampler2D(double[] array, int stride) {
			this.array = array;
			this.stride = stride;
		}

		@Override
		public double getValueAt(int... coordinates) {
			return array[coordinates[1]*stride+coordinates[0]];
		}
	}

	static class MultiDimArraySampler2D implements RealValuedSampler {

		public final double[][] array;

		public MultiDimArraySampler2D(double[][] array) {
			this.array = array;
		}

		@Override
		public double getValueAt(int... coordinates) {
			return array[coordinates[0]][coordinates[1]];
		}
	}

}
