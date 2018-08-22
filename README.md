# ezFFTW

A wrapper for the FFTW library built on top of bytedecos JavaCPP preset that hides the cumbersome native pointering for ez (easy) use. [See bytedeco's code here](https://github.com/bytedeco/javacpp-presets/tree/master/fftw). [See FFTW code here](https://github.com/FFTW/fftw3).

[![Build Status](https://travis-ci.org/hageldave/ezFFTW.svg?branch=master)](https://travis-ci.org/hageldave/ezFFTW)
[![Coverage Status](https://coveralls.io/repos/github/hageldave/ezFFTW/badge.svg?branch=master)](https://coveralls.io/github/hageldave/ezFFTW?branch=master)
[![Maven Central](https://img.shields.io/maven-central/v/com.github.hageldave.ezfftw/ezfftw.svg)](https://search.maven.org/search?q=g:com.github.hageldave.ezfftw)

Maven Dependency (Central Repository):
```xml
<dependency>
	<groupId>com.github.hageldave.ezfftw</groupId>
	<artifactId>ezfftw</artifactId>
	<version>0.1.2</version>
</dependency>
```

## Examples

### Sine + Cosine
(from [examples/Simple.java](../master/src/test/java/hageldave/ezfftw/dp/example/Simple.java))
```java
static void sinePlusCosine(){
  int numSamples = 16;
  double second = 2*Math.PI; // interval of one second
  // create samples
  double[] samples = new double[numSamples];
  for(int i = 0; i < numSamples; i++){
    samples[i] = Math.sin(i*second/numSamples);
    samples[i]+= Math.cos(i*second/numSamples);
  }
  // execute fft
  double[] realPart = new double[numSamples];
  double[] imagPart = new double[numSamples];
  FFT.fft(samples, realPart,imagPart, numSamples);
  // print result (omit conjugated complex results)
  for(int i = 0; i < 1+numSamples/2; i++) {
    System.out.format("%dHz | % .2f%+.2fi%n",i, realPart[i], imagPart[i]);
  }
}
```

### Image Band Pass
(from [examples/Filtering.java](../master/src/test/java/hageldave/ezfftw/dp/example/Filtering.java))
```java
static void bandPassImageFilter(InputStream input, OutputStream output, String outFormat){
  try {
    // load image
    BufferedImage loadedImg = ImageIO.read(input);
    final int width = loadedImg.getWidth();
    final int height = loadedImg.getHeight();
    // make sampler for image
    RealValuedSampler sampler = new RealValuedSampler() {
      @Override
      public double getValueAt(long... coordinates) {
        // we know coordinates will be 2D and in range of image dimensions
        int rgb = loadedImg.getRGB((int)coordinates[0], (int)coordinates[1]);
        // return average grey in range [0,1]
        return ((rgb>>16&0xff)+(rgb>>8&0xff)+(rgb&0xff))/(3*255.0); 
      }
    };
    // make fft storage (RowMajorArrayAccessor implements sampler and writer)
    RowMajorArrayAccessor realPart = new RowMajorArrayAccessor(new double[width*height], width,height);
    RowMajorArrayAccessor imagPart = new RowMajorArrayAccessor(new double[width*height], width,height);
    // execute fft
    FFT.fft(sampler, realPart.combineToComplexWriter(imagPart), width, height);
    // make sampler that will filter out frequencies
    ComplexValuedSampler filterSampler = new ComplexValuedSampler() {
      @Override
      public double getValueAt(boolean imaginary, long... coordinates) {
        // get coordinates with centered DC
        double x = ( ((coordinates[0]+ width/2)% width)- width/2 );
        double y = ( ((coordinates[1]+height/2)%height)-height/2 );
        // get length (corresponds to frequency)
        double l = Math.sqrt(x*x+y*y);
        // define band pass frequencies to be in ]50, 100[
        if(l > 50 && l < 100){
          return imaginary ? imagPart.getValueAt(coordinates[0],coordinates[1]) 
                  : realPart.getValueAt(coordinates[0],coordinates[1]);
        } else return 0;
      }
    };
    // make target image for writing result
    BufferedImage filteredImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    RealValuedWriter imgWriter = new RealValuedWriter() {
      @Override
      public void setValueAt(double value, long... coordinates) {
        value /= width*height; // remove scaling (FFTW does it like this)
        value = Math.max(0, Math.min(value, 1)); // clamp value between [0,1]
        int byteval = (int)(value*255);
        int argb = 0xff000000|(byteval<<16)|(byteval<<8)|byteval; // make greyscale argb
        filteredImg.setRGB((int)coordinates[0], (int)coordinates[1], argb);
            
      }
    };
    // execute inverse fft
    FFT.ifft(filterSampler, imgWriter, width, height);
    ImageIO.write(filteredImg, outFormat, output);
  } catch (IOException e){
    e.printStackTrace();
  }
}
```

### Filtered Back Projection
(from [examples/FilteredBackProjection.java](../master/src/test/java/hageldave/ezfftw/dp/example/FilteredBackProjection.java))
```java
static double[][] filteredBackProjection(double[][] radon, int outputResolution) {
  /* apply ramp filter to radon transform - 1st step fourier transform
   * (every line is one projection and has to be filtered separately)
   */
  try(//with resources
      NativeRealArray projection = new NativeRealArray(radon[0].length);
      NativeRealArray fft_r = new NativeRealArray(projection.length);
      NativeRealArray fft_i = new NativeRealArray(projection.length);
  ){
    for(int i = 0; i < radon.length; i++){
      projection.set(radon[i]);
      FFTW_Guru.execute_split_r2c(projection, fft_r, fft_i, projection.length);
      projection.get(0, radon[i]);
      // 2nd step apply ramp filter ( multiply by abs(freq) with normalized freq )
      long spectrumWidth = projection.length;
      long highestFreq = spectrumWidth/2;
      for(long k = 0; k < spectrumWidth; k++){
        long freq = ((k+highestFreq)%spectrumWidth)-highestFreq;
        double scaling = Math.abs(freq*1.0/highestFreq);
        fft_r.set(k, fft_r.get(k)*scaling);
        fft_i.set(k, fft_i.get(k)*scaling);
      }
      // 3rd step, inverse fft
      FFTW_Guru.execute_split_c2r(fft_r, fft_i, projection, projection.length);
      projection.get(0, radon[i]);
    }
  }
  // now do back projection
  double[][] output = new double[outputResolution][outputResolution];
  double toRadians = Math.PI/radon.length;
  int projectionWidth = radon[0].length;
  for(int i = 0; i < outputResolution; i++){
    double y = ((i*1.0/outputResolution)-0.5)*2;
    for(int j = 0; j < outputResolution; j++){
      double x = ((j*1.0/outputResolution)-0.5)*2;
      double sum = 0;
      int numSummands = 0;
      // for each projection find radius where [x,y] was projected to
      for(int p = 0; p < radon.length; p++){
        double angle = p*toRadians;
        double radius = x*Math.cos(angle)+y*Math.sin(angle);
        int r = (int)((radius+1)/2*(projectionWidth-1));
        if(r >=0 && r < projectionWidth){
          sum += radon[p][r];
          numSummands++;
        }
      }
      if(numSummands > 0)
        sum /= numSummands;
      output[i][j] = sum;
    }
  }
  return output;
}
```
