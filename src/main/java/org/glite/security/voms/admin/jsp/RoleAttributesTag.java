/**
 * Copyright (c) Members of the EGEE Collaboration. 2006-2009.
 * See http://www.eu-egee.org/partners/ for details on the copyright holders.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Authors:
 * 	Andrea Ceccanti (INFN)
 */
package org.glite.security.voms.admin.jsp;

import java.util.Set;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.TagSupport;

import org.glite.security.voms.admin.model.VOMSGroup;
import org.glite.security.voms.admin.model.VOMSRole;

/**
 * @author andrea
 *
 */
/**
 * @author andrea
 * 
 */
public class RoleAttributesTag extends TagSupport {

	/**
     * 
     */
	private static final long serialVersionUID = 1L;

	private String var;

	private String groupVar;

	public String getGroupVar() {

		return groupVar;
	}

	public void setGroupVar(String groupVar) {

		this.groupVar = groupVar;
	}

	public String getVar() {

		return var;
	}

	public void setVar(String var) {

		this.var = var;
	}

	public int doStartTag() throws JspException {

		VOMSRole r = (VOMSRole) pageContext.getAttribute("vomsRole",
				PageContext.REQUEST_SCOPE);
		VOMSGroup g = (VOMSGroup) pageContext
				.findAttribute((groupVar == null) ? "vomsGroup" : groupVar);

		if (r == null)
			throw new JspTagException(
					"No role found in org.glite.security.voms.admin.request context!");

		if (g == null)
			throw new JspTagException(
					"No group found in org.glite.security.voms.admin.request context!");

		Set attributes = r.getAttributesInGroup(g);

		pageContext.setAttribute(var, attributes);

		return SKIP_BODY;
	}

}
