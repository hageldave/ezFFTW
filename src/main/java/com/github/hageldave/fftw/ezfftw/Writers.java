package com.github.hageldave.fftw.ezfftw;

public class Writers {

	static interface RealValuedWriter {
		public void setValueAt(double value, int... coordinates);

		public default ComplexValuedWriter addImaginaryComponent(RealValuedWriter imaginaryWriter){
			RealValuedWriter self = this;
			return (value, imaginary, coordinates) -> {
				if(imaginary)
					imaginaryWriter.setValueAt(value, coordinates);
				else
					self.setValueAt(value, coordinates);
			};
		}
	}

	static interface ComplexValuedWriter {
		public void setValueAt(double value, boolean imaginary, int... coordinates);
	}

	static class RowMajorArrayWriter2D implements RealValuedWriter {

		public final double[] array;
		public final int stride;

		public RowMajorArrayWriter2D(double[] array, int stride) {
			this.array = array;
			this.stride = stride;
		}

		@Override
		public void setValueAt(double val, int... coordinates) {
			array[coordinates[1]*stride+coordinates[0]] = val;
		}
	}

	static class MultiDimArrayWriter2D implements RealValuedWriter {

		public final double[][] array;

		public MultiDimArrayWriter2D(double[][] array) {
			this.array = array;
		}

		@Override
		public void setValueAt(double value, int... coordinates) {
			array[coordinates[0]][coordinates[1]] = value;
		}
	}

}
