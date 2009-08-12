package org.glite.security.voms.admin.view.actions.aup;

import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.glite.security.voms.admin.dao.generic.AUPDAO;
import org.glite.security.voms.admin.dao.generic.DAOFactory;
import org.glite.security.voms.admin.view.actions.BaseAction;


@ParentPackage("base")
@Results({
	@Result(name=BaseAction.INPUT, location="aups"),
	@Result(name=BaseAction.SUCCESS, location="/aup/load.action", type="redirect")
})
public class SetActiveVersionAction extends AUPVersionActions {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Override
	public String execute() throws Exception {
	
		AUPDAO dao = DAOFactory.instance().getAUPDAO();
		
		dao.setActiveVersion(getModel(), getVersion());
		
		return SUCCESS;
	}
	
	

}