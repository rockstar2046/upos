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
package com.rockagen.upos.enums;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;

/**
 * ISO8583 possible values type
 * <p>
 * 
 * <pre>
 * NUMERIC.format("256",4) will return "0256"
 * ALPHA.format("f12",4) will return "f12 "
 * DATE4_YM.format(new Date()) will return "1311"
 * DATE4_MD.format(new Date()) will return "1102"
 * TIME.format(new Date()) will return "004308"
 * ..
 * </pre>
 * 
 * </p>
 * 
 * @author RA
 * @since JDK1.6
 */
public enum IsoType implements Serializable {

	/**
	 * A fixed-length numeric value.It is zero-filled to the left.
	 */
	NUMERIC(true, 0),
	/**
	 * A fixed-length alphanumeric value. It is filled with spaces to the right.
	 */
	ALPHA(true, 0),
	/**
	 * A variable length alphanumeric value, identified length field is 2-digit.
	 * .
	 */
	LLVAR(false, 0),
	/**
	 * A variable length alphanumeric value, identified length field is 3-digit.
	 */
	LLLVAR(false, 0),
	/**
	 * A date format pattern in MMddHHmmss.
	 */
	DATE10(false, 10),
	/**
	 * A date format pattern in yyMM.
	 */
	DATE4_YM(false, 4),
	/**
	 * A date format pattern in MMdd.
	 */
	DATE4_MD(false, 4),
	/**
	 * A time format pattern in HHmmss.
	 */
	TIME(false, 6),
	/**
	 * An amount, a fixed length of 12.
	 */
	AMOUNT(false, 12),
	/**
	 * Binary type.
	 */
	BINARY(true, 0);

	/**
	 * This type needs specified length.
	 */
	private boolean nsLen;
	/**
	 * This type length.
	 */
	private int len;

	/**
	 * Create a enum with nsLen and length.
	 * 
	 * @param nsLen
	 *            This type needs specified length
	 * @param length
	 *            special type length
	 */
	IsoType(boolean nsLen, int length) {
		this.nsLen = nsLen;
		this.len = length;
	}

	/**
	 * Need special type length ?.
	 * 
	 * @return if needs return true
	 */
	public boolean isNsLen() {
		return nsLen;
	}

	/**
	 * Return type length.
	 * 
	 * @return type length
	 */
	public int getLen() {
		return len;
	}

	/**
	 * Formats a Date if the receiver is DATE10,DATE4_YM,DATE4_MD or TIME.
	 * 
	 * @param value
	 *            date
	 * @return formated String
	 */
	public String format(Date value) {
		if (this == DATE10) {
			return String.format("%Tm%<Td%<TH%<TM%<TS", value);
		} else if (this == DATE4_YM) {
			return String.format("%Ty%<Tm", value);
		} else if (this == DATE4_MD) {
			return String.format("%Tm%<Td", value);
		} else if (this == TIME) {
			return String.format("%TH%<TM%<TS", value);
		} else {
			return String.format("%Ty%<Tm%<Td%<TH%<TM%<TS%<TL", value);
		}
	}

	/**
	 * Formats a string with the length parameter (length is only useful if type
	 * is ALPHA, NUMERIC).
	 * 
	 * @param value
	 * @param length
	 * @return formated String
	 */
	public String format(String value, int length) {
		if (this == ALPHA) {
			if (value == null) {
				value = "";
			}
			if (value.length() > length) {
				return value.substring(0, length);
			} else if (value.length() == length) {
				return value;
			} else {
				return String.format(String.format("%%-%ds", length), value);
			}
		} else if (this == LLVAR) {
			int orilen = length;
			String orivalue = value;
			if (orilen > 99) {
				orilen = 99;
				orivalue = orivalue.substring(0, 99);
			}
			return String.format("%02d", orilen) + orivalue;
		}

		else if (this == LLLVAR) {
			int orilen = length;
			String orivalue = value;
			if (orilen > 999) {
				orilen = 999;
				orivalue = orivalue.substring(0, 999);
			}
			return String.format("%03d", orilen) + orivalue;
		} else if (this == NUMERIC) {
			if (value.length() > length)
				return value.substring(0, length);

			char[] c = new char[length];
			// initialize
			Arrays.fill(c, '0');
			char[] x = value.toCharArray();
			System.arraycopy(x, 0, c, (c.length - x.length), x.length);
			return new String(c);

		} else if (this == AMOUNT) {
			return String.format(String.format("%%0%dd", 12), new BigDecimal(
					value).movePointRight(2).longValue());
		} else {
			return value.substring(0, length);
		}
	}

	/**
	 * Formats the integer value as a NUMERIC, an AMOUNT, or a String.
	 * 
	 * @param value
	 * @param length
	 * @return formated string
	 */
	public String format(long value, int length) {
		if (this == NUMERIC) {
			String x = String.format(String.format("%%0%dd", length), value);
			if (x.length() > length) {
				return x.substring(0, length);
			}
			return x;
		} else if (this == ALPHA || this == LLVAR || this == LLLVAR) {
			return format(Long.toString(value), length);
		} else if (this == AMOUNT) {
			return String.format("%010d00", value);
		} else {
			// XXX
			return String.valueOf(value).substring(0, length);
		}
	}

	/**
	 * Formats the BigDecimal as an AMOUNT, NUMERIC, or a String.
	 * 
	 * @param value
	 * @param length
	 * @return formated string
	 */
	public String format(BigDecimal value, int length) {
		if (this == AMOUNT) {
			return String.format("%012d", value.movePointRight(2).longValue());
		} else if (this == NUMERIC) {
			return format(value.longValue(), length);
		} else if (this == ALPHA || this == LLVAR || this == LLLVAR) {
			return format(value.toString(), length);
		} else {
			// XXX
			return format(value.longValue(), length);
		}
	}

}
