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

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rockagen.commons.util.CommUtil;
import com.rockagen.upos.define.IsoPredefine;
import com.rockagen.upos.util.BitUtil;

/**
 * Write the server response message 
 * @author RA
 * @since JDK1.6
 */
public class WriteIsoMessage extends IsoMessage {

	private static final Logger log = LoggerFactory
			.getLogger(WriteIsoMessage.class);
	private final String type;
	private final String header;
	private final Charset charset;
	private Map<Integer, IsoPredefine.IsoField> paserMap = new HashMap<Integer, IsoPredefine.IsoField>();
	private Map<Integer, IsoPredefine.IsoField> templateMap = new HashMap<Integer, IsoPredefine.IsoField>();
	private final Map<Integer, byte[]> valueMap = new HashMap<Integer, byte[]>();

	// ~ Constructors ==================================================

	/**
	 * Create a instance and initialize some object,you can call getXXX method
	 * obtain some useful object Create a instance.
	 * 
	 * @param type
	 *            message type eg: 0210,0110...
	 */
	public WriteIsoMessage(String type) {
		this(type,null, null);
	}
	
	/**
	 * Create a instance and initialize some object,you can call getXXX method
	 * obtain some useful object.
	 * 
	 * @param type
	 *            iso8583 message type eg: 0210,0110...
	 * @param isopredefine
	 *            iso8583 {@link IsoPredefine}
	 */
	public WriteIsoMessage(String type,IsoPredefine isopredefine) {
		this(type,null, isopredefine);
	}


	/**
	 * Create a instance and initialize some object,you can call getXXX method
	 * obtain some useful object.
	 * 
	 * @param type
	 *            iso8583 message type eg: 0210,0110...
	 * @param charset
	 *            charset
	 * @param isopredefine
	 *            iso8583 {@link IsoPredefine}
	 */
	public WriteIsoMessage(String type,Charset charset, IsoPredefine isopredefine) {
		super(isopredefine);
		
		// set charset
		if(charset==null){
			charset=Charset.defaultCharset();
		}
		this.charset=charset;
		this.type = type;
		// ISO8583 HEADER
		IsoPredefine.IsoHeader isoheader = super.getIsoHeader(this.type);
		if (isoheader == null) {
			log.debug("type [ {} ] iso8583 header is empty", this.type);
			header = "";
		} else {
			header = isoheader.getValue();
		}

		IsoPredefine.IsoParser parser = super.geIsoParser(this.type);
		if (parser != null  && parser.getFields()!=null) {
			for (IsoPredefine.IsoField pf : parser.getFields()) {
				if (pf == null)
					continue;
				paserMap.put(pf.getNum(), pf);
			}
		}

		IsoPredefine.IsoTemplate tepmlate = super.geIsoTemplate(this.type);
		if (tepmlate != null && tepmlate.getFields()!=null) {
			for (IsoPredefine.IsoField tf : tepmlate.getFields()) {
				if (tf == null)
					continue;
				templateMap.put(tf.getNum(), tf);
			}
		}

		// Read-only
		paserMap = Collections.unmodifiableMap(paserMap);
		// Read-only
		templateMap = Collections.unmodifiableMap(templateMap);

		valueMap.clear();
	}

	/**
	 * This map will be used to construct iso8583 message.
	 * <p>
	 * First get this map instance,then call put(num,bytes) fill iso8583 data
	 * </p>
	 * <p>
	 * You should based <a href="http://en.wikipedia.org/wiki/ISO_8583">wiki
	 * SO_8583<a> set some value, this map key start index should be 2,in other
	 * words,this map will doing action as follow:
	 * </p>
	 * <p>
	 * 
	 * <pre>
	 * 
	 * Object[] keys = map.keySet().toArray();
	 * Arrays.sort(keys);
	 * int max = (Integer) keys[keys.length - 1];
	 * 
	 * </pre>
	 * 
	 * if max &gt; 64,the bitmap offset 0 digit is <b>1</b> and construct a 128
	 * digits bit map
	 * 
	 * <pre>
	 *  Then:
	 *           Based valueMap generate bit map,type,data,and add iso8583 header(if exist)
	 *           
	 *           finally, call {@link #generateIsoMessage} method return a iso8583 bytes.
	 * </pre>
	 * 
	 * </p>
	 * 
	 * @return final valueMap instance
	 */
	public Map<Integer, byte[]> getValueMap() {

		return valueMap;
	}

	/**
	 * Generate iso message.
	 * <p>
	 * This bytes include header(if exist),type,bitmap,data
	 * </p>
	 * <p>
	 * You should invoke {@link #getValueMap()} method fill iso8583 data at
	 * first,finally,call this method generate iso8583 message
	 * </p>
	 * 
	 * @return bytes
	 */
	public byte[] generateIsoMessage() {
		int headerlen = 0;
		if (header != null) {
			headerlen = header.getBytes(charset).length;
		}
		int typelen = type.getBytes(charset).length;

		int bitmaplen = 8;
		Object[] keys = valueMap.keySet().toArray();
		Arrays.sort(keys);
		int max = (Integer) keys[keys.length - 1];

		if (max > 64) {
			bitmaplen = 16;
		}

		BitSet bitmap = new BitSet(bitmaplen * 8);
		if (bitmaplen > 8) {
			// 128 digits
			bitmap.set(0);
		}

		byte[] isodata = new byte[0];

		Map<Integer, byte[]> treeMap = new TreeMap<Integer, byte[]>();
		treeMap.putAll(valueMap);

		for (Map.Entry<Integer, byte[]> entry : treeMap.entrySet()) {
			int key = entry.getKey();
			byte[] bytes = entry.getValue();
			if (key > 1) {
				bitmap.set(key - 1);
				int dlen = bytes.length;
				if (dlen > 0) {
					byte[] newdata = new byte[isodata.length + dlen];

					System.arraycopy(isodata, 0, newdata, 0, isodata.length);
					System.arraycopy(bytes, 0, newdata, isodata.length, dlen);

					isodata = Arrays.copyOf(newdata, newdata.length);

				}

			}
		}

		// header + type + bitmap + isodata
		final byte[] finalIsodata = new byte[headerlen + typelen + bitmaplen
				+ isodata.length];

		if (headerlen > 0) {
			// add header
			System.arraycopy(header.getBytes(charset), 0, finalIsodata, 0, headerlen);
		}
		// add type
		System.arraycopy(type.getBytes(charset), 0, finalIsodata, headerlen, typelen);

		// add bitmap
		byte[] bitArray = BitUtil.bitValue(bitmap);
		System.arraycopy(bitArray, 0, finalIsodata, headerlen + typelen,
				bitmaplen);

		// add data
		System.arraycopy(isodata, 0, finalIsodata, headerlen + typelen
				+ bitmaplen, isodata.length);
		log.debug("response bitmap: {}", CommUtil.prettyHexdump(bitArray));
		log.debug("\n response data: {}", CommUtil.prettyHexdump(isodata));
		return finalIsodata;

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
	 * Get iso8583 message header.
	 * 
	 * @return iso8583 message header
	 */
	public String getHeader() {
		return header;
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
	 * Get Predefine {@link IsoPredefine.IsoTemplate} Field by field num.
	 * 
	 * @param num
	 * @return {@link IsoPredefine.IsoField}
	 */
	public IsoPredefine.IsoField getTemplateIsoField(int num) {
		return templateMap.get(num);
	}
}
