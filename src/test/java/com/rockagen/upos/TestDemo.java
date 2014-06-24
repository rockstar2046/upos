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

import java.util.Map;

import org.junit.Test;

import com.rockagen.commons.util.CommUtil;
import com.rockagen.upos.message.ReadIsoMessage;
import com.rockagen.upos.message.WriteIsoMessage;

/**
 * A demo test
 * @author RA
 */
public class TestDemo {

	// stuff (message type,bitmap...)
	static String hex = "30313030A23A448188C1801000000000020000013331303030303032313732333233353131323639303232333233353130323137303231373630313130313239323038313130303030313130384D303030303038303233323335313132363930325445524E304131303032303031363239303032333435363033384644303131303030303030303030302020202020202020203030303030303030343438373623313536303134303030303030303030333030303031313332313030303030303031B33FCBFFD612A316";

	/**
	 * MESSAGE TYPE: "0100" BITMAP: "{0, 2, 6, 10, 11, 12, 14, 17, 21, 24, 31,
	 * 32, 36, 40, 41, 47, 48, 59, 102, 127} ISO8583 data
	 * 
	 * <pre>
	 * [3]:310000
	 * [7]:0217232351
	 * [11]:126902
	 * [12]:232351
	 * [13]:0217
	 * [15]:0217
	 * [18]:6011
	 * [22]:012
	 * [25]:92
	 * [32]:11000011
	 * [33]:M0000080
	 * [37]:232351126902
	 * [41]:TERN0A10
	 * [42]:020016290023456
	 * [48]:FD0110000000000         0000000044876#
	 * [49]:156
	 * [60]:00000000030000
	 * [103]:32100000001
	 * [128]:........
	 * </pre>
	 */

	@Test
	public void testDemo() {

		byte[] bytes = CommUtil.hexdecode(hex);

		int isodlen = bytes.length - 4;
		// 4 bytes type (0100)
		byte[] type = new byte[4];
		// iso8583 data
		byte[] isodata = new byte[isodlen];
		System.arraycopy(bytes, 0, type, 0, 4);
		System.arraycopy(bytes, 4, isodata, 0, isodlen);

		String mtype = new String(type);

		// request
		ReadIsoMessage rim = new ReadIsoMessage(mtype, isodata);

		StringBuffer sb = new StringBuffer();

		// Get your data by rim.getIsoValue(i),i index is start 2
		for (int i = 2; i <= 128; i++) {
			if (hasBytes(rim.getIsoValue(i))) {
				sb.append("[" + i + "]:" + new String(rim.getIsoValue(i)));
				sb.append(String.format("%n"));
			}
		}

		print("#READ");
		print("###############################################");
		print(sb.toString());
		print("###############################################");

		// response
		WriteIsoMessage wim = new WriteIsoMessage("0110");
		Map<Integer, byte[]> map = wim.getValueMap();
		initializeMap(wim, map, rim);
		// here handle your logic
		// return code (00)
		map.put(39, "00".getBytes());
		byte[] response = wim.generateIsoMessage();
		print("#WRITE");
		print("###############################################");
		print(CommUtil.prettyHexdump(response));
		print("###############################################");

	}

	private void initializeMap(WriteIsoMessage wim, Map<Integer, byte[]> map,
			ReadIsoMessage rim) {

		for (int i = 2; i <= 128; i++) {
			if (hasBytes(rim.getIsoValue(i))) {
				map.put(i, rim.getIsoValue(i));
			}
		}
	}

	private boolean hasBytes(byte[] bytes) {
		if (bytes != null && bytes.length > 0) {
			return true;
		}

		return false;
	}

	private void print(Object x) {
		System.out.println(x);
	}
}
