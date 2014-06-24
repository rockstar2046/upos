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

import java.util.Set;

import com.rockagen.upos.enums.IsoType;

/**
 * ISO8583 Predefine structure.
 * 
 * @author RA
 * @since JDK1.6
 */
public class IsoPredefine {

	/** The headers. */
	private Set<IsoHeader> headers;

	/** The templates. */
	private Set<IsoTemplate> templates;

	/** The parsers. */
	private Set<IsoParser> parsers;

	/**
	 * Gets the headers.
	 * 
	 * @return the headers
	 */
	public Set<IsoHeader> getHeaders() {
		return headers;
	}

	/**
	 * Sets the headers.
	 * 
	 * @param headers
	 *            the new headers
	 */
	public void setHeaders(Set<IsoHeader> headers) {
		this.headers = headers;
	}

	/**
	 * Gets the templates.
	 * 
	 * @return the templates
	 */
	public Set<IsoTemplate> getTemplates() {
		return templates;
	}

	/**
	 * Sets the templates.
	 * 
	 * @param templates
	 *            the new templates
	 */
	public void setTemplates(Set<IsoTemplate> templates) {
		this.templates = templates;
	}

	/**
	 * Gets the parsers.
	 * 
	 * @return the parsers
	 */
	public Set<IsoParser> getParsers() {
		return parsers;
	}

	/**
	 * Sets the parsers.
	 * 
	 * @param parsers
	 *            the new parsers
	 */
	public void setParsers(Set<IsoParser> parsers) {
		this.parsers = parsers;
	}

	/**
	 * The Class IsoHeader.
	 */
	public static class IsoHeader {

		/** The type. */
		private String type;

		/** The value. */
		private String value;

		/**
		 * Instantiates a new iso header.
		 */
		public IsoHeader() {

		}

		/**
		 * Instantiates a new iso header.
		 * 
		 * @param type
		 *            the type
		 * @param value
		 *            the value
		 */
		public IsoHeader(String type, String value) {
			this.type = type;
			this.value = value;
		}

		/**
		 * Gets the type.
		 * 
		 * @return the type
		 */
		public String getType() {
			return type;
		}

		/**
		 * Gets the value.
		 * 
		 * @return the value
		 */
		public String getValue() {
			return value;
		}

		/**
		 * Sets the type.
		 * 
		 * @param type
		 *            the new type
		 */
		public void setType(String type) {
			this.type = type;
		}

		/**
		 * Sets the value.
		 * 
		 * @param value
		 *            the new value
		 */
		public void setValue(String value) {
			this.value = value;
		}

		/**
		 * Hash code.
		 * 
		 * @return the int
		 */
		@Override
		public int hashCode() {
			return 1618 * type.hashCode();
		}

		/**
		 * Equals.
		 * 
		 * @param obj
		 *            the obj
		 * @return true, if successful
		 */
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			IsoHeader other = (IsoHeader) obj;
			if (type == null) {
				if (other.type != null)
					return false;
			} else if (!type.equals(other.type))
				return false;
			return true;
		}
		
		/**
		 * To string.
		 * 
		 * @return the string
		 */
		@Override
		public String toString() {
			return "IsoHeader["+type+"] value={"+value+"}";
		}
		
		

	}

	/**
	 * The Class IsoTemplate.
	 */
	public static class IsoTemplate {

		/** The type. */
		private String type;

		/** The fields. */
		private Set<IsoField> fields;

		/**
		 * Instantiates a new iso template.
		 * 
		 * @param type
		 *            the type
		 * @param fields
		 *            the fields
		 */
		public IsoTemplate(String type, Set<IsoField> fields) {
			this.type = type;
			this.fields = fields;
		}

		/**
		 * Gets the type.
		 * 
		 * @return the type
		 */
		public String getType() {
			return type;
		}

		/**
		 * Sets the type.
		 * 
		 * @param type
		 *            the new type
		 */
		public void setType(String type) {
			this.type = type;
		}

		/**
		 * Gets the fields.
		 * 
		 * @return the fields
		 */
		public Set<IsoField> getFields() {
			return fields;
		}

		/**
		 * Sets the fields.
		 * 
		 * @param fields
		 *            the new fields
		 */
		public void setFields(Set<IsoField> fields) {
			this.fields = fields;
		}

		/**
		 * Hash code.
		 * 
		 * @return the int
		 */
		@Override
		public int hashCode() {
			return 1618 * type.hashCode();
		}

		/**
		 * Equals.
		 * 
		 * @param obj
		 *            the obj
		 * @return true, if successful
		 */
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			IsoTemplate other = (IsoTemplate) obj;
			if (type == null) {
				if (other.type != null)
					return false;
			} else if (!type.equals(other.type))
				return false;
			return true;
		}
		
		/**
		 * To string.
		 * 
		 * @return the string
		 */
		@Override
		public String toString() {
			String s_fields="empty";
			if(fields!=null){
				StringBuffer sb=new StringBuffer();
				sb.append("##START##");
				sb.append(",");
				for(IsoField field:fields){
					sb.append(field);
					sb.append(", ");
				}
				sb.append("##END##");
				s_fields=sb.toString();
			}
			 
			return "IsoTemplate["+type+"] IsoFields={"+s_fields+"}";
		}
	}

	/**
	 * The Class IsoParser.
	 */
	public static class IsoParser {

		/** The type. */
		private String type;

		/** The fields. */
		private Set<IsoField> fields;

		/**
		 * Instantiates a new iso parser.
		 * 
		 * @param type
		 *            the type
		 * @param fields
		 *            the fields
		 */
		public IsoParser(String type, Set<IsoField> fields) {
			this.type = type;
			this.fields = fields;
		}

		/**
		 * Gets the type.
		 * 
		 * @return the type
		 */
		public String getType() {
			return type;
		}

		/**
		 * Sets the type.
		 * 
		 * @param type
		 *            the new type
		 */
		public void setType(String type) {
			this.type = type;
		}

		/**
		 * Gets the fields.
		 * 
		 * @return the fields
		 */
		public Set<IsoField> getFields() {
			return fields;
		}

		/**
		 * Sets the fields.
		 * 
		 * @param fields
		 *            the new fields
		 */
		public void setFields(Set<IsoField> fields) {
			this.fields = fields;
		}

		/**
		 * Hash code.
		 * 
		 * @return the int
		 */
		@Override
		public int hashCode() {
			return 1618 * type.hashCode();
		}

		/**
		 * Equals.
		 * 
		 * @param obj
		 *            the obj
		 * @return true, if successful
		 */
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			IsoParser other = (IsoParser) obj;
			if (type == null) {
				if (other.type != null)
					return false;
			} else if (!type.equals(other.type))
				return false;
			return true;
		}
		
		
		/**
		 * To string.
		 * 
		 * @return the string
		 */
		@Override
		public String toString() {
			String s_fields="empty";
			if(fields!=null){
				StringBuffer sb=new StringBuffer();
				sb.append("##START##");
				sb.append(",");
				for(IsoField field:fields){
					sb.append(field);
					sb.append(", ");
				}
				sb.append("##END##");
				s_fields=sb.toString();
			}
			 
			return "IsoParser["+type+"] IsoFields={"+s_fields+"}";
		}
	}

	/**
	 * The Class IsoField.
	 */
	public static class IsoField implements Comparable<IsoField> {

		/** The num. */
		private int num;

		/** The IsoType. */
		private IsoType type;

		/** The length. */
		private int length;

		/** The value. */
		private String value;

		/**
		 * Instantiates a new iso field.
		 * 
		 * @param num
		 *            the num
		 * @param type
		 *            the type
		 * @param length
		 *            the length
		 * @param value
		 *            the value
		 */
		public IsoField(int num, IsoType type, int length, String value) {
			this.num = num;
			this.type = type;
			this.length = length;
			this.value = value;
		}

		/**
		 * Instantiates a new iso field.
		 * 
		 * @param num
		 *            the num
		 * @param type
		 *            the IsoType
		 * @param length
		 *            the length
		 */
		public IsoField(int num, IsoType type, int length) {
			this.num = num;
			this.type = type;
			this.length = length;
			this.value = null;
		}

		/**
		 * Instantiates a new iso field.
		 * 
		 * @param num
		 *            the num
		 * @param type
		 *            the type
		 */
		public IsoField(int num, IsoType type) {
			this.num = num;
			this.type = type;
			this.length = type.getLen();
			this.value = null;
		}

		/**
		 * Gets the num.
		 * 
		 * @return the num
		 */
		public int getNum() {
			return num;
		}

		/**
		 * Gets the IsoType.
		 * 
		 * @return the type
		 */
		public IsoType getType() {
			return type;
		}

		/**
		 * Gets the length.
		 * 
		 * @return the length
		 */
		public int getLength() {
			return length;
		}

		/**
		 * Gets the value.
		 * 
		 * @return the value
		 */
		public String getValue() {
			return value;
		}

		/**
		 * Sets the num.
		 * 
		 * @param num
		 *            the new num
		 */
		public void setNum(int num) {
			this.num = num;
		}

		/**
		 * Sets the IsoType.
		 * 
		 * @param type IsoType
		 *            the new type
		 */
		public void setType(IsoType type) {
			this.type = type;
		}

		/**
		 * Sets the length.
		 * 
		 * @param length
		 *            the new length
		 */
		public void setLength(int length) {
			this.length = length;
		}

		/**
		 * Sets the value.
		 * 
		 * @param value
		 *            the new value
		 */
		public void setValue(String value) {
			this.value = value;
		}

		/**
		 * Hash code.
		 * 
		 * @return the int
		 */
		@Override
		public int hashCode() {
			return 1618 * num << 2;
		}

		/**
		 * Equals.
		 * 
		 * @param obj
		 *            the obj
		 * @return true, if successful
		 */
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			IsoField other = (IsoField) obj;
			if (num == 0) {
				if (other.num != 0)
					return false;
			} else if (num != other.num)
				return false;
			return true;
		}

		/**
		 * To string.
		 * 
		 * @return the string
		 */
		@Override
		public String toString() {
			return "IsoField [num=" + num + ", type=" + type + ", length="
					+ length + ", value=" + value + "]";
		}

		@Override
		public int compareTo(IsoField o) {
			return this.num - o.num;
		}

	}

}
