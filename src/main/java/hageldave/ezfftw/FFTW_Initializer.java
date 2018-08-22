/*
 * ezFFTW - Copyright 2018 David Haegele
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package hageldave.ezfftw;

import org.bytedeco.javacpp.DoublePointer;
import org.bytedeco.javacpp.Loader;
import org.bytedeco.javacpp.fftw3;
import org.bytedeco.javacpp.fftw3.fftw_plan;

/**
 * Class with sole purpose of loading the native libraries via
 * {@link #initFFTW()}.
 * Also contains the {@link #PLANNER_LOCK} used for synchronizing 
 * calls to FFTW planner routines.
 * 
 * @author hageldave
 */
public final class FFTW_Initializer {

	private static boolean setupDone = false;

	/**
	 * Loads the native fftw3 library.
	 * This method has to be called before using any of the bindings to fftw or cpp
	 * (i.e. {@link DoublePointer} or {@link fftw_plan} and the like).
	 */
	public static void initFFTW(){
		if(!setupDone){
			synchronized (FFTW_Initializer.class) {
				if(!setupDone){
					String loadedlib = Loader.load(fftw3.class);
					setupDone = true;
					System.out.format("Loaded FFTW library [%s]%n",loadedlib);
				}
			}
		}
	}
	
	/**
	 * Object used for synchronizing calls to planner routines of FFTW
	 * since planning is not thread safe.
	 * @see <a href="http://www.fftw.org/fftw3_doc/Thread-safety.html">
	 * http://www.fftw.org/fftw3_doc/Thread-safety.html
	 * </a>
	 * @since 0.1.1
	 */
	public static final Object PLANNER_LOCK = new Object();

}
