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
package com.rockagen.upos.message;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.BitSet;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rockagen.commons.util.CommUtil;
import com.rockagen.upos.define.IsoPredefine;
import com.rockagen.upos.define.IsoPredefine.IsoField;
import com.rockagen.upos.enums.IsoType;
import com.rockagen.upos.util.BitUtil;

/**
 * Read the client request message,and parse that
 * 
 * @author RA
 * @since JDK1.6
 */
public class ReadIsoMessage extends IsoMessage {

	private static final Logger log = LoggerFactory
			.getLogger(ReadIsoMessage.class);
	private final String type;
	private Map<Integer, IsoPredefine.IsoField> paserMap = new HashMap<Integer, IsoPredefine.IsoField>();
	private Map<Integer, byte[]> valueMap = new HashMap<Integer, byte[]>();

	/**
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
	 */
	private final BitSet bitMap;

	/**
	 * exclude bitmap data.
	 */
	private final ByteBuffer data;

	/**
	 * Create a instance and initialize some object,you can call getXXX method
	 * obtain some useful object.
	 * 
	 * @param type
	 *            iso8583 message type eg: 0200,0100...
	 * @param bytes
	 *            iso8583 data
	 */
	public ReadIsoMessage(String type, byte[] bytes) {
		// Based iso8583.xml file create IsoPredine
		this(type, bytes,Charset.defaultCharset(), null);
	}

	/**
	 * Create a instance and initialize some object,you can call getXXX method
	 * obtain some useful object.
	 * 
	 * @param type
	 *            iso8583 message type eg: 0200,0100...
	 * @param bytes
	 *            iso8583 data
	 * @param charset
	 *            charset
	 * @param isopredefine
	 *            iso8583 {@link IsoPredefine}
	 */
	public ReadIsoMessage(String type, byte[] bytes, Charset charset,IsoPredefine isopredefine) {
		super(isopredefine);
		this.type = type;
		if (bytes == null) {
			throw new IllegalArgumentException("Iso8583 data must not be null");
		}
		int bytelen = bytes.length;
		int bitlen = 8;
		if (((bytes[0] & (1 << 7)) >>> 7) == 1) {
			// BIT MAP,EXTENDED
			bitlen = 16;
		}
		byte[] bitm = new byte[bitlen];
		System.arraycopy(bytes, 0, bitm, 0, bitlen);
		bitMap = BitUtil.bitSet(bitm);
		log.debug("BitMap value index {}", bitMap.toString());
		int isobyteslen = bytelen - bitlen;
		byte[] isobytes = new byte[isobyteslen];
		System.arraycopy(bytes, bitlen, isobytes, 0, isobyteslen);
		data = ByteBuffer.wrap(isobytes);
		log.debug("Iso8583 data: {} bytes\n{}", isobyteslen,
				CommUtil.prettyHexdump(isobytes));

		IsoPredefine.IsoParser parser = super.geIsoParser(this.type);
		if (parser != null) {
			for (IsoPredefine.IsoField pf : parser.getFields()) {
				if (pf == null)
					continue;
				paserMap.put(pf.getNum(), pf);
			}
			// Read-only
			paserMap = Collections.unmodifiableMap(paserMap);
			generateValeMap(getData(), getBitMap(),charset);
			// Read-only
			valueMap = Collections.unmodifiableMap(valueMap);
		}

	}

	/**
	 * Generate value map,start index is 2.
	 * 
	 * @param buf
	 *            iso8583 data
	 * @param bm
	 *            bit map
	 */
	protected void generateValeMap(ByteBuffer buf, BitSet bm,Charset charset) {
		for (int i = 1; i < bm.length(); i++) {
			if (bm.get(i)) {
				IsoField field = getParserIsoField(i + 1);
				if (field == null || field.getType()==null) {
					log.warn(
							"Invalidate IsoField,please check your iso8583.xml,index on {parser[{}].field[{}] ",
							type, i + 1);
					continue;
				}
				int bytelen = 0;
				// Fixed length: DATE10 DATE4_YM DATE4_MD TIME AMOUNT
				switch (field.getType()) {
				case DATE10:
					bytelen = IsoType.DATE10.getLen();
					break;
				case DATE4_YM:
					bytelen = IsoType.DATE4_YM.getLen();
					break;
				case DATE4_MD:
					bytelen = IsoType.DATE4_MD.getLen();
					break;
				case TIME:
					bytelen = IsoType.TIME.getLen();
					break;
				case AMOUNT:
					bytelen = IsoType.AMOUNT.getLen();
					break;
				case NUMERIC:
					bytelen = field.getLength();
					break;
				case ALPHA:
					bytelen = field.getLength();
					break;
				case BINARY:
					bytelen = field.getLength();
					break;
				case LLVAR:
					byte[] llen = new byte[2];
					buf.get(llen);
					bytelen = Integer.parseInt(new String(llen,charset));
					break;
				case LLLVAR:
					byte[] lllen = new byte[3];
					buf.get(lllen);
					bytelen = Integer.parseInt(new String(lllen,charset));
					break;
				default:
					log.warn(
							"Illegal [IsoTtype],please check your iso8583.xml,index on {parser[{}].field[{}] ",
							getType(), i + 1);
					break;
				}

				byte[] data = new byte[bytelen];
				buf.get(data);

				valueMap.put((i + 1), data);

			}

		}
	}

	/**
	 * Get BitSet.
	 * <p>
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
	 * @return BitSet
	 */
	public BitSet getBitMap() {
		return (BitSet) bitMap.clone();
	}

	/**
	 * Get iso8583 data.
	 * <p>
	 * <b>NOTE: this data exclude bitmap,and read only</b>
	 * </p>
	 * 
	 * @return ByteBuffer
	 */
	public ByteBuffer getData() {
		return data.asReadOnlyBuffer();
	}

	/**
	 * Get iso8583 message type.
	 * 
	 * @return iso8583 message type
	 */
	public String getType() {
		return type;
	}

	/**
	 * Get Predefine {@link IsoPredefine.IsoParser} Field by field num.
	 * 
	 * @param num
	 * @return {@link IsoPredefine.IsoField}
	 */
	public IsoPredefine.IsoField getParserIsoField(int num) {
		return paserMap.get(num);
	}

	/**
	 * Get Iso8583 value by field num.
	 * <p>
	 * note: by default,start index is 2,that is, start value is <b>Primary
	 * account number</b>
	 * </p>
	 * 
	 * @param num
	 * @return bytes
	 */
	public byte[] getIsoValue(int num) {
		return valueMap.get(num);
	}
}
