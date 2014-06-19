/*
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.rockagen.upos.util;

import java.util.BitSet;

/**
 * Bit digit util
 * 
 * @author RA
 * @since JDK1.6
 */
final public class BitUtil {

	// ~ Methods ==================================================

	/**
	 * Create a BitSet instance,start index is 0.
	 * <p>
	 * example:
	 * 
	 * <pre>
	 *    byte:   50
	 *    binary: 0b110010
	 *    
	 *    +--------+---+---+---+---+---+---+---+---+
	 *    |  bits  | 0 | 0 | 1 | 1 | 0 | 0 | 1 | 0 |
	 *    +--------+---+---+---+---+---+---+---+---+
	 *    | bitset | 0 | 1 | 2 | 3 | 4 | 5 | 6 | 7 |
	 *    +--------+---+---+---+---+---+---+-------+
	 * 
	 * </pre>
	 * 
	 * </p>
	 * 
	 * @param bytes
	 * @return bitSet
	 */
	public static BitSet bitSet(byte[] bytes) {
		if (bytes == null) {
			throw new IllegalArgumentException("bitMap must not be null");
		}
		BitSet bit = new BitSet();
		int index = 0;
		for (int i = 0; i < bytes.length; i++) {
			for (int j = 7; j >= 0; j--) {
				bit.set(index++, (bytes[i] & (1 << j)) >>> j == 1);

			}
		}
		return bit;
	}

	/**
	 * Create a BitSet instance,start index is 0.
	 * <p>
	 * example:
	 * 
	 * <pre>
	 *    byte:   50
	 *    binary: 0b110010
	 *    
	 *    +--------+---+---+---+---+---+---+---+---+
	 *    | bitset | 0 | 1 | 2 | 3 | 4 | 5 | 6 | 7 |
	 *    +--------+---+---+---+---+---+---+---+---+
	 *    |  bits  | 0 | 0 | 1 | 1 | 0 | 0 | 1 | 0 |
	 *    +--------+---+---+---+---+---+---+---+---+
	 * 
	 * </pre>
	 * 
	 * </p>
	 * 
	 * @param bitSet
	 * @return bytes
	 */
	public static byte[] bitValue(BitSet bitSet) {
		if (bitSet == null) {
			throw new IllegalArgumentException("BitSet must not be null");
		}

		byte[] bytes = new byte[bitSet.size() / 8];

		int index = 0;
		int offset = 0;
		for (int i = 0; i < bitSet.size(); i++) {
			index = i / 8;
			offset = 7 - i % 8;
			bytes[index] |= (bitSet.get(i) ? 1 : 0) << offset;
		}
		return bytes;
	}

}
