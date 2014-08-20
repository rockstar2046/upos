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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rockagen.upos.define.IsoPreDefineParser;
import com.rockagen.upos.define.IsoPredefine;

/**
 * ISO8583 Message
 * <p>
 * see also <a href="http://en.wikipedia.org/wiki/ISO_8583">wiki ISO_8583<a>
 * </p>
 * <p>
 * This class handles only ISO8583's bitmap, and over the next data
 * </p>
 * <p>
 * Note: This constructor will based your iso8583.xml create a
 * {@link IsoPredefine} Object, you can call geIsoXXX obtain some predefine
 * value
 * </p>
 * 
 * @author RA
 * @since JDK1.6
 */
public abstract class IsoMessage {

	private static final Logger log = LoggerFactory.getLogger(IsoMessage.class);

	/**
	 * Iso8583 predefine instance.
	 */
	private final IsoPredefine predefine;

	/**
	 * Create a instance.
	 * @param isopredefine
	 *            iso8583 predefine
	 */
	protected IsoMessage(IsoPredefine isopredefine) {
		IsoPredefine ipf = isopredefine;
		if (ipf == null) {
			ipf = IsoPreDefineParser.createDefault();
			log.debug("IsoPredefine not special,use dafeult");
		}
		predefine = ipf;
		log.debug("PreDefine [ {} ]", predefine);

	}

	/**
	 * Obtain the {@link IsoPredefine.IsoHeader} by type.
	 * <p>
	 * if not found,return null
	 * </p>
	 * 
	 * @param type
	 * @return {@link IsoPredefine.IsoHeader}
	 */
	public IsoPredefine.IsoHeader getIsoHeader(String type) {
		if (predefine == null) {
			log.error("predefina does not initialize.");
			return null;
		}
		for (IsoPredefine.IsoHeader header : predefine.getHeaders()) {
			if (header != null) {
				if (header.getType().equals(type)) {
					return header;
				}
			}
		}
		log.debug("IsoHeader [type={}] is null", type);
		return null;
	}

	/**
	 * Obtain the {@link IsoPredefine.IsoTemplate} by type.
	 * <p>
	 * if not found,return null
	 * </p>
	 * 
	 * @param type
	 * @return {@link IsoPredefine.IsoTemplate}
	 */
	public IsoPredefine.IsoTemplate geIsoTemplate(String type) {
		if (predefine == null) {
			log.error("predefina does not initialize.");
			return null;
		}
		for (IsoPredefine.IsoTemplate template : predefine.getTemplates()) {
			if (template != null) {
				if (template.getType().equals(type)) {
					return template;
				}
			}
		}
		log.debug("IsoTemplate [type={}] is null", type);
		return null;
	}

	/**
	 * Obtain the {@link IsoPredefine.IsoParser} by type.
	 * <p>
	 * if not found,return null
	 * </p>
	 * 
	 * @param type
	 * @return {@link IsoPredefineIsoParser}
	 */
	public IsoPredefine.IsoParser geIsoParser(String type) {
		if (predefine == null) {
			log.error("predefina does not initialize.");
			return null;
		}
		for (IsoPredefine.IsoParser parser : predefine.getParsers()) {
			if (parser != null) {
				if (parser.getType().equals(type)) {
					return parser;
				}
			}
		}
		log.warn("IsoParser [type={}] is null", type);
		return null;
	}

}
