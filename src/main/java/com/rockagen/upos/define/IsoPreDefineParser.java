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
package com.rockagen.upos.define;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rockagen.commons.util.XmlUtil;
import com.rockagen.commons.util.XmlUtil.XAlias;
import com.rockagen.commons.util.XmlUtil.XAliasAttribute;
import com.rockagen.commons.util.XmlUtil.XImplicitCollection;

/**
 * Parsing iso8583.xml predefine file
 * <p>
 * Through this to get an {@link IsoPredefine} instance
 * </p>
 * 
 * @author RA
 * @see IsoPredefine
 * @since JDK1.6
 */
public class IsoPreDefineParser {

	// ~ Instance fields ==================================================

	private static final Logger log = LoggerFactory
			.getLogger(IsoPreDefineParser.class);

	/**
	 * XStream alias
	 */
	private final static XAlias[] XA = {
			new XAlias("define", IsoPredefine.class),
			// new XAlias("field", IsoField.class),
			new XAlias("parser", IsoPredefine.IsoParser.class),
			new XAlias("header", IsoPredefine.IsoHeader.class),
			new XAlias("template", IsoPredefine.IsoTemplate.class) };
	/**
	 * XStream aliasAttribute
	 */
	private final static XAliasAttribute[] XAA = {
			new XAliasAttribute("type", IsoPredefine.IsoHeader.class, "type"),
			new XAliasAttribute("value", IsoPredefine.IsoHeader.class, "value"),
			new XAliasAttribute("type", IsoPredefine.IsoParser.class, "type"),
			new XAliasAttribute("type", IsoPredefine.IsoTemplate.class, "type"),
			new XAliasAttribute("num", IsoPredefine.IsoField.class, "num"),
			new XAliasAttribute("type", IsoPredefine.IsoField.class, "type"),
			new XAliasAttribute("length", IsoPredefine.IsoField.class, "length"),
			new XAliasAttribute("value", IsoPredefine.IsoField.class, "value"), };
	/**
	 * XStream implicitCollection
	 */
	private final static XImplicitCollection[] XIC = {
			new XImplicitCollection(IsoPredefine.IsoParser.class, "fields",
					"field", IsoPredefine.IsoField.class),
			new XImplicitCollection(IsoPredefine.IsoTemplate.class, "fields",
					"field", IsoPredefine.IsoField.class) };

	/**
	 * IsoPredefine
	 */
	private static IsoPredefine isoPredefine = null;

	static {
		// initialize
		isoPredefine = create();
	}

	// ~ Methods ==================================================

	private static IsoPredefine create() {
		ClassLoader loader = IsoPreDefineParser.class.getClassLoader();
		log.debug("Load the iso8583.xml file from classpath");
		URL url = loader.getResource("iso8583.xml");
		if (url == null) {
			log.warn("Not found the iso8583.xml file from classpath,please checked ensure iso8583.xml file exist.");
			return null;
		}
		String path = url.getFile();
		return createFromPath(path);
	}

	/**
	 * Load the iso8583.xml file from classpath,then based this configuration
	 * create a {@link IsoPredefine}
	 * 
	 * @return {@link IsoPredefine}
	 */
	public static IsoPredefine createDefault() {
		if (isoPredefine == null) {
			isoPredefine = create();

		}

		return isoPredefine;
	}

	/**
	 * Create a {@link IsoPredefine} from a xmlString
	 * 
	 * @param xmlString
	 * @return {@link IsoPredefine}
	 */
	public static IsoPredefine createFromXml(String xmlString) {
		IsoPredefine bean = (IsoPredefine) XmlUtil.toBean(xmlString, XA, null,
				XAA, null, XIC, null, null);
		return bean;
	}

	/**
	 * Create a {@link IsoPredefine} from a xml file
	 * 
	 * @param path
	 * @return {@link IsoPredefine}
	 */
	public static IsoPredefine createFromPath(String path) {
		log.debug("Load the iso8583 configuration file [{}]", path);
		IsoPredefine bean = null;
		RandomAccessFile in = null;
		FileChannel fc = null;
		ByteBuffer bb = ByteBuffer.allocateDirect(1024 * 64);
		CharBuffer cb;
		try {
			in = new RandomAccessFile(path, "r");
			log.debug("Found on {}", path);
			fc = in.getChannel();
			fc.read(bb);
			bb.flip();
			cb = Charset.defaultCharset().decode(bb);
			String xmlString = cb.toString();
			bean = createFromXml(xmlString);
		} catch (IOException e) {
			log.error("{}", e.getMessage(), e);
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
				}
			}

			if (fc != null) {
				try {
					fc.close();
				} catch (IOException e) {
				}
			}
			bb = null;
		}

		return bean;

	}

}
