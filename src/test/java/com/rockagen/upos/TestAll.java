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
package com.rockagen.upos;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import com.rockagen.commons.util.CommUtil;
import com.rockagen.commons.util.XmlUtil;
import com.rockagen.commons.util.XmlUtil.XAlias;
import com.rockagen.commons.util.XmlUtil.XAliasAttribute;
import com.rockagen.commons.util.XmlUtil.XConverter;
import com.rockagen.commons.util.XmlUtil.XImplicitCollection;
import com.rockagen.upos.define.IsoPreDefineParser;
import com.rockagen.upos.define.IsoPredefine;
import com.rockagen.upos.define.IsoPredefine.IsoField;
import com.rockagen.upos.define.IsoPredefine.IsoHeader;
import com.rockagen.upos.define.IsoPredefine.IsoParser;
import com.rockagen.upos.define.IsoPredefine.IsoTemplate;
import com.rockagen.upos.enums.IsoType;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

/**
 * Test
 * 
 * @author RA
 */
public class TestAll {

	@Test
	public void testIsoType() {
		Calendar cl = Calendar.getInstance();
		cl.set(Calendar.YEAR, 2013);
		cl.set(Calendar.MONTH, 10);
		cl.set(Calendar.DAY_OF_MONTH, 5);
		cl.set(Calendar.HOUR_OF_DAY, 10);
		cl.set(Calendar.MINUTE, 4);
		cl.set(Calendar.SECOND, 0);
		Date d = cl.getTime();
		Assert.assertEquals("1311", IsoType.DATE4_YM.format(d));
		Assert.assertEquals("1105", IsoType.DATE4_MD.format(d));
		Assert.assertEquals("1105100400", IsoType.DATE10.format(d));
		
		Assert.assertEquals("12  ", IsoType.ALPHA.format("12",4));
		Assert.assertEquals("0012", IsoType.NUMERIC.format(12,4));
		Assert.assertEquals("000000001200", IsoType.AMOUNT.format(12, 0));
	}

	@Test
	@Ignore
	public void testConf(){
		IsoPredefine ipd=IsoPreDefineParser.createDefault();
		Set<IsoParser> tls=ipd.getParsers();
		for(IsoParser tl: tls)
		System.out.println(tl); 
		
	}

	@Test
	@Ignore
	public void testISO8583Config() throws IOException{
		
		IsoPredefine conf=new IsoPredefine();
		
		IsoHeader h1 =new IsoHeader("0200","ISO123");
		IsoHeader h2=new IsoHeader("0210","ISO123");
		
		IsoField p1=new IsoField(0,IsoType.ALPHA,6,"650000");
		IsoField p2=new IsoField(32,IsoType.ALPHA,6,"abcd");
		
		
		Set<IsoField> fields=new HashSet<IsoPredefine.IsoField>();
		fields.add(p1);
		fields.add(p2);
		IsoTemplate tp=new IsoTemplate("0200",fields);
		IsoTemplate tp2=new IsoTemplate("0210",fields);
		
		IsoParser pa=new IsoParser("0200",fields);
		IsoParser pa2=new IsoParser("0210",fields);
		Set<IsoTemplate> tps=new HashSet<IsoPredefine.IsoTemplate>();
		tps.add(tp);
		tps.add(tp2);
		Set<IsoHeader> hs=new HashSet<IsoPredefine.IsoHeader>();
		hs.add(h1);
		hs.add(h2);
		Set<IsoParser> pas=new HashSet<IsoPredefine.IsoParser>();
		pas.add(pa);
		pas.add(pa2);
		
		conf.setHeaders(hs);
		conf.setParsers(pas);
		conf.setTemplates(tps);
		
		XAlias[] xa = { new XAlias("define", IsoPredefine.class),
				 new XAlias("parser", IsoParser.class),
				 new XAlias("header", IsoHeader.class),
				 new XAlias("template", IsoTemplate.class)
		};
		  XAliasAttribute[] xaa={
				  new XAliasAttribute("type",IsoHeader.class,"type"),
				  new XAliasAttribute("type",IsoParser.class,"type"),
				  new XAliasAttribute("type",IsoTemplate.class,"type"),
				  new XAliasAttribute("num",IsoField.class,"num"),
				  new XAliasAttribute("type",IsoField.class,"type"),
				  new XAliasAttribute("length",IsoField.class,"length")
		  };
			XImplicitCollection[] xic={
					  new XImplicitCollection(IsoParser.class,"fields","field",IsoField.class),
					  new XImplicitCollection(IsoTemplate.class,"fields","field",IsoField.class)
				  };
			
			/**
			 * XStream Converter
			 */
			XConverter[] XC={
				new XConverter(new IsoFieldValueConverter(), 1),
				new XConverter(new IsoHeaderValueConverter(), 2)};
		
		String xmlString=XmlUtil.formatPretty(XmlUtil.toXml(conf, xa, null,xaa,null,xic,null,XC));
		System.out.println(xmlString);

	}
	
	
	/**
	 * Attribute names
	 */
	private final static String A_NUM="num";
	private final static String A_TYPE="type";
	private final static String A_LEN="length";

	/**
	 * IsoHeader value converter.
	 * 
	 * @author RA
	 */
	static class IsoHeaderValueConverter implements Converter {

		@SuppressWarnings("rawtypes")
		@Override
		public boolean canConvert(Class type) {
			if (type == null)
				return false;
			return IsoHeader.class.equals(type);
		}

		@Override
		public void marshal(Object source, HierarchicalStreamWriter writer,
				MarshallingContext context) {
			IsoHeader header = (IsoHeader) source;
			writer.addAttribute(A_TYPE, header.getType());
			writer.setValue(header.getValue());
		}

		@Override
		public Object unmarshal(HierarchicalStreamReader reader,
				UnmarshallingContext context) {
			IsoHeader header = new IsoHeader();
			header.setType(reader.getAttribute(A_TYPE));
			header.setValue(reader.getValue());
			return header;
		}

	}

	/**
	 * IsoField value converter
	 * 
	 * @author RA
	 */
	static class IsoFieldValueConverter implements Converter {

		@SuppressWarnings("rawtypes")
		@Override
		public boolean canConvert(Class type) {
			if (type == null)
				return false;
			return IsoField.class.equals(type);
		}

		@Override
		public void marshal(Object source, HierarchicalStreamWriter writer,
				MarshallingContext context) {
			IsoField field = (IsoField) source;
			writer.addAttribute(A_NUM, String.valueOf(field.getNum()));
			writer.addAttribute(A_TYPE, field.getType().name());
			writer.addAttribute(A_LEN, String.valueOf(field.getLength()));
			writer.setValue(field.getValue());
		}

		@Override
		public Object unmarshal(HierarchicalStreamReader reader,
				UnmarshallingContext context) {
			String num = reader.getAttribute(A_NUM);
			String length = reader.getAttribute(A_LEN);
			String type = reader.getAttribute(A_TYPE);
			String value = reader.getValue();

			if (CommUtil.isBlank(length))
				length = "0";

			IsoType itype = IsoType.valueOf(type);
			IsoField header = new IsoField(Integer.valueOf(num), itype,
					Integer.valueOf(length), value);
			return header;
		}

	}
}
