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

/**
 * Simple trace number generator
 * @author RA
 * @since JDK1.6
 */
final public class SimpleTraceGenerator {

	private int value = 0;

	/**
	 * Creates a new instance that will use the specified initial value.
	 * 
	 * @param initialValue
	 *            a number between 1 and 999999.
	 * @throws IllegalArgumentException
	 *             if the number is less than 1 or greater than 999999.
	 */
	public SimpleTraceGenerator(int initialValue) {
		if (initialValue < 1 || initialValue > 999999) {
			throw new IllegalArgumentException(
					"Initial value must be between 1 and 999999");
		}
		value = initialValue - 1;
	}

	/**
	 * Creates a new instance that initial value is 1.
	 */
	public SimpleTraceGenerator() {
	}

	/**
	 * @return current value.
	 */
	public int get() {
		return value;
	}

	/**
	 * @return Returns the next number in the sequence.
	 */
	public synchronized int next() {
		value++;
		if (value > 999999) {
			value = 1;
		}
		return value;
	}

}
