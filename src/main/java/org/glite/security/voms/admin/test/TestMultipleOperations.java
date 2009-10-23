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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.glite.security.voms.admin.operations.VOMSOperation;
import org.glite.security.voms.admin.operations.groups.DeleteGroupOperation;

public class TestMultipleOperations {

	
	protected VOMSOperation createOperation(Class opClass, Long id) throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException{
		
		
		Method instanceMethod = opClass.getMethod("instance", new Class[]{Long.class});
		
		VOMSOperation o = (VOMSOperation) instanceMethod.invoke(null, id);
		
		
		return o;
	}
	
	
	public TestMultipleOperations() throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		
		VOMSOperation o = createOperation(DeleteGroupOperation.class, 5L);
		o.execute();
		
		
		
	}
	/**
	 * @param args
	 * @throws NoSuchMethodException 
	 * @throws SecurityException 
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 * @throws IllegalArgumentException 
	 */
	public static void main(String[] args) throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		new TestMultipleOperations();
		

	}

}
