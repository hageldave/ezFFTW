package hageldave.ezfftw.dp;

import hageldave.ezfftw.JunitUtils;
import hageldave.ezfftw.dp.samplers.ComplexValuedSampler;
import hageldave.ezfftw.dp.samplers.RealValuedSampler;
import hageldave.ezfftw.dp.writers.ComplexValuedWriter;
import hageldave.ezfftw.dp.writers.RealValuedWriter;

import static org.junit.Assert.*;
import org.junit.Test;

public class SamplerWriterTest {

	@Test
	public void test() {
		RowMajorArrayAccessor rmaa = new RowMajorArrayAccessor(new double[64], 4,4,4);
		double v;
		v = 0;
		for(int k = 0; k < 4; k++){
			for(int j = 0; j < 4; j++){
				for(int i = 0; i < 4; i++){
					rmaa.setValueAt(v, i,j,k);
					v+=1;
				}
			}
		}
		for(int i = 0; i < 64; i++){
			assertEquals(i, rmaa.array[i], 0);
		}
		v = 0;
		for(int k = 0; k < 4; k++){
			for(int j = 0; j < 4; j++){
				for(int i = 0; i < 4; i++){
					assertEquals(v, rmaa.getValueAt(i,j,k), 0);
					v+=1;
				}
			}
		}
		
		
		RowMajorArrayAccessor rmaai = new RowMajorArrayAccessor(4,4,4);
		ComplexValuedSampler cmplxSampler = rmaa.combineToComplexSampler(rmaai);
		ComplexValuedWriter cmplxWriter = rmaa.combineToComplexWriter(rmaai);
		v = 0;
		for(int k = 0; k < 4; k++){
			for(int j = 0; j < 4; j++){
				for(int i = 0; i < 4; i++){
					assertEquals(v, cmplxSampler.getValueAt(false, i,j,k), 0);
					assertEquals(0, cmplxSampler.getValueAt( true, i,j,k), 0);
					v+=1;
				}
			}
		}
		
		
		for(int k = 0; k < 4; k++){
			for(int j = 0; j < 4; j++){
				for(int i = 0; i < 4; i++){
					cmplxWriter.setValueAt(i, false, i,j,k);
					cmplxWriter.setValueAt(j,  true, i,j,k);
				}
			}
		}
		for(int k = 0; k < 4; k++){
			for(int j = 0; j < 4; j++){
				for(int i = 0; i < 4; i++){
					assertEquals(i, cmplxSampler.getValueAt(false, i,j,k), 0);
					assertEquals(j, cmplxSampler.getValueAt( true, i,j,k), 0);
				}
			}
		}
		
		
		ComplexValuedSampler cmplxSwappedSampler = cmplxSampler.getRealImaginarySwappedSampler();
		for(int k = 0; k < 4; k++){
			for(int j = 0; j < 4; j++){
				for(int i = 0; i < 4; i++){
					assertEquals(i, cmplxSwappedSampler.getValueAt(true, i,j,k), 0);
					assertEquals(j, cmplxSwappedSampler.getValueAt( false, i,j,k), 0);
					v+=1;
				}
			}
		}
		
		
		ComplexValuedWriter cmplxSwappedWriter = cmplxWriter.getRealImaginarySwappedWriter();
		for(int k = 0; k < 4; k++){
			for(int j = 0; j < 4; j++){
				for(int i = 0; i < 4; i++){
					cmplxSwappedWriter.setValueAt(i+1, false, i,j,k);
					cmplxSwappedWriter.setValueAt(j+1,  true, i,j,k);
					assertEquals(i+1, cmplxSwappedSampler.getValueAt(false, i,j,k), 0);
					assertEquals(j+1, cmplxSwappedSampler.getValueAt(true, i,j,k), 0);
				}
			}
		}
		
		
		RealValuedWriter realPartWriter = cmplxWriter.getPartWriter(false);
		RealValuedWriter imagPartWriter = cmplxWriter.getPartWriter(true);
		RealValuedSampler realPartSampler = cmplxSampler.getPartSampler(false);
		RealValuedSampler imagPartSampler = cmplxSampler.getPartSampler(true);
		v = 0;
		for(int k = 0; k < 4; k++){
			for(int j = 0; j < 4; j++){
				for(int i = 0; i < 4; i++){
					realPartWriter.setValueAt(v,  i,j,k);
					imagPartWriter.setValueAt(-v, i,j,k);
					assertEquals(v, realPartSampler.getValueAt(i,j,k), 0);
					assertEquals(-v, imagPartSampler.getValueAt(i,j,k), 0);
					v+=1;
				}
			}
		}
		for(int i = 0; i < 64; i++){
			assertEquals(i, rmaa.array[i], 0);
			assertEquals(-i, rmaai.array[i], 0);
		}
	}

	@Test
	public void testExceptions() {
		JunitUtils.testException(()->{
			// missing dimensions
			new RowMajorArrayAccessor(new double[4]);
		}, IllegalArgumentException.class);
		JunitUtils.testException(()->{
			// non positive dimension
			new RowMajorArrayAccessor(new double[4], -2, -2);
		}, IllegalArgumentException.class);
		JunitUtils.testException(()->{
			// non matching dimensions
			new RowMajorArrayAccessor(new double[4], 2,3);
		}, IllegalArgumentException.class);
		JunitUtils.testException(()->{
			// null array
			new RowMajorArrayAccessor(null, 1);
		}, NullPointerException.class);
		
		/* Constructor without array arg */
		
		JunitUtils.testException(()->{
			// missing dimensions
			new RowMajorArrayAccessor();
		}, IllegalArgumentException.class);
		JunitUtils.testException(()->{
			// non positive dimension
			new RowMajorArrayAccessor(-2, -2);
		}, IllegalArgumentException.class);
		JunitUtils.testException(()->{
			// too many elements
			new RowMajorArrayAccessor(Integer.MAX_VALUE, 2);
		}, IllegalArgumentException.class);
	}

}
