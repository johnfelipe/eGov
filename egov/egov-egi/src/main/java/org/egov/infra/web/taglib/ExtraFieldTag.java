/*
 * eGov suite of products aim to improve the internal efficiency,transparency,
 * accountability and the service delivery of the government  organizations.
 *
 *  Copyright (C) 2016  eGovernments Foundation
 *
 *  The updated version of eGov suite of products as by eGovernments Foundation
 *  is available at http://www.egovernments.org
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program. If not, see http://www.gnu.org/licenses/ or
 *  http://www.gnu.org/licenses/gpl.html .
 *
 *  In addition to the terms of the GPL license to be adhered to in using this
 *  program, the following additional terms are to be complied with:
 *
 *      1) All versions of this program, verbatim or modified must carry this
 *         Legal Notice.
 *
 *      2) Any misrepresentation of the origin of the material is prohibited. It
 *         is required that all modified versions of this material be marked in
 *         reasonable ways as different from the original version.
 *
 *      3) This license does not grant any rights to any user of the program
 *         with regards to rights under trademark law for use of the trade names
 *         or trademarks of eGovernments Foundation.
 *
 *  In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
 */

package org.egov.infra.web.taglib;

import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyTagSupport;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class ExtraFieldTag extends BodyTagSupport {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Map extraFieldMap = null;

	public ExtraFieldTag() {
		super();
	}

	public Map getExtraFieldMap() {
		return this.extraFieldMap;
	}

	public void setExtraFieldMap(final Map extraFieldMap) {
		this.extraFieldMap = extraFieldMap;
	}

	@Override
	public int doStartTag() throws javax.servlet.jsp.JspTagException {
		return SKIP_BODY;
	}

	@Override
	public int doEndTag() {
		try {
			final Map extFieldMap = this.getExtraFieldMap();
			final Set keySet = extFieldMap.keySet();
			final Iterator setitr = keySet.iterator();
			String extString = "";
			while (setitr.hasNext()) {
				final String fieldname = (String) setitr.next();
				String value = (String) extFieldMap.get(fieldname);
				if (value == null || value.equals(" ")) {
					value = "N/A";
				}
				extString += "<TR><TD colspan = \"2\"><font id=\"normal_font\" >" + fieldname + "</font></td>" + "<TD colspan = \"2\" ><font id=\"normal_font\" >" + value + "</font></td></tr>";
			}
			final JspWriter out = this.pageContext.getOut();

			out.print(extString);
		} catch (final Exception ioe) {
			

		}

		return EVAL_PAGE;
	}

}
