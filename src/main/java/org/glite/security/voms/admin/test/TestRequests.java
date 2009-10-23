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
package org.glite.security.voms.admin.test;

import java.util.Date;

import org.glite.security.voms.admin.dao.generic.RequestDAO;
import org.glite.security.voms.admin.dao.hibernate.HibernateDAOFactory;
import org.glite.security.voms.admin.database.HibernateFactory;
import org.glite.security.voms.admin.model.request.RequesterInfo;

public class TestRequests {

	public void createRequest(String dn, String ca, String email) {

	}

	public void testCreateVOMembershipRequest() {

		HibernateFactory.beginTransaction();

		RequesterInfo ri = new RequesterInfo();

		ri.setCertificateSubject(TestUtils.myDn);
		ri.setCertificateIssuer(TestUtils.myCA);
		ri.setEmailAddress(TestUtils.myEmail);

		ri.setVoMember(false);

		RequestDAO rDAO = HibernateDAOFactory.instance().getRequestDAO();

		rDAO.createVOMembershipRequest(ri, new Date());

		HibernateFactory.commitTransaction();

	}

	public TestRequests() {

		testCreateVOMembershipRequest();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		TestUtils.configureLogging();
		TestUtils.setupVOMSConfiguration();
		TestUtils.setupVOMSDB();

		new TestRequests();

		System.exit(0);

	}

}
