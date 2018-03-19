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

import java.util.function.Supplier;

/**
 * Class that offers methods for
 * <ul>
 * <li>sanity checks of method arguments </li>
 * <li>handling dimensions and coordinates of long[] type </li>
 * </ul>
 *
 * @author hageldave
 *
 */
public class GeneralUtils {

	/**
	 * Checks if specified number is greater than zero and throws an
	 * {@link IllegalArgumentException} with the message from the
	 * specified supplier if not.
	 * @param n number to check
	 * @param errmsg message to put in exception
	 * @throws IllegalArgumentException if zero or negative
	 */
	public static void requirePositive(long n, Supplier<String> errmsg){
		if(n < 1){
			throw new IllegalArgumentException(errmsg.get());
		}
	}

	/**
	 * Checks whether all specified dimensions are positive. Throws an
	 * {@link IllegalArgumentException} if not. The error message
	 * will inform about which element of the dimensions array is
	 * not positive.
	 * @param dimensions to check
	 * @throws IllegalArgumentException if one of the dimensions is not positive
	 */
	public static void requirePosititveDimensions(long... dimensions){
		for(int i = 0; i < dimensions.length; i++){
			final int i_ = i;
			requirePositive(dimensions[i], ()->"All dimensions need to be positive, but dimension number "+i_+" is "+dimensions[i_]+".");
		}
	}

	/**
	 * Checks whether the specified objects are equal in the sense of == or {@link Object#equals(Object)}
	 * and will throw an {@link IllegalArgumentException} with the specified error message
	 * if not.
	 * @param o1 object to test
	 * @param o2 object to test
	 * @param errmsg that will be put into exception.
	 * @throws IllegalArgumentException if == and .equals() are false
	 */
	public static void requireEqual(Object o1, Object o2, Supplier<String> errmsg){
		if(!(o1 == o2||o1.equals(o2)) ){
			throw new IllegalArgumentException(errmsg.get());
		}
	}

	/**
	 * Calculates the number of elements from specified dimensions.
	 * Example: given dimensions [16,8,4] this will result in
	 * 16*8*4 = 512 elements.
	 * @param dimensions from which num elements will be calculated
	 * @return number of elements from specified dimensions
	 */
	public static long numElementsFromDimensions(long[] dimensions){
		long numElements = dimensions.length == 0 ? 0:1;
		for(long d:dimensions){
			numElements *= d;
		}
		return numElements;
	}

	/**
	 * Calculates the row major index for the specified coordinates and dimensions.
	 * Example: given dimensions [16,8,4] and coordinates [3,0,2] this will
	 * return 3+0*16+2*16*8 = 259.
	 * @param coordinates for which index is to be calculated
	 * @param dimensions for the space of coordinates
	 * @return row major index for given coordinates and dimensions
	 */
	public static long indexFromCoordinates(long[] coordinates, long[] dimensions) {
		long index = 0;
		long stride = 1;
		for(int i = 0; i < dimensions.length; i++){
			index += coordinates[i]*stride;
			stride *= dimensions[i];
		}
		return index;
	}

	/**
	 * Increments the specified coordinates by 1. This increments the least significant
	 * coordinate first and cascades increments to next coordinate on overflow.
	 * Overflow in this case means that a coordinate gets larger than its specified
	 * corresponding dimension. For dimensions [2,2,2,2] this is like incrementing a
	 * binary number of 4 bits.
	 * When the most significant coordinate would overflow, the coordinates will not
	 * become 0 again but instead the most significant coordinate will be
	 * equal to its corresponding dimension to signalize the maximum coordinate.
	 * This way the you can check for the last increment when coords[n]==dims[n]
	 * with n = dims.length-1.
	 * @param coordinates to increment by one
	 * @param dimensions for the space of coordinates
	 */
	public static void incrementCoords(long[] coordinates, long[] dimensions){
		incrementCoords(0, coordinates, dimensions);
	}


	/* increment as in binary counter but with arbitrary limits for each position */
	private static void incrementCoords(int i, long[] coordinates, long[] dims){
		coordinates[i]++;
		if(coordinates[i] >= dims[i]){
			coordinates[i] -= dims[i];
			if(i < dims.length-1)
				incrementCoords(i+1,coordinates,dims);
			else
				coordinates[i] = dims[i]; // highest possible number, dont overflow to signalize end
		}
	}


}
