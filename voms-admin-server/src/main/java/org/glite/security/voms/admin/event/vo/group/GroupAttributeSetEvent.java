package org.glite.security.voms.admin.event.vo.group;

import org.glite.security.voms.admin.event.EventDescription;
import org.glite.security.voms.admin.event.MainEventDataPoints;
import org.glite.security.voms.admin.persistence.model.VOMSGroup;
import org.glite.security.voms.admin.persistence.model.VOMSGroupAttribute;

@MainEventDataPoints({"groupAttributeContext", "groupAttributeName", "groupAttributeValue"})
@EventDescription(message="set attribute '%s' for group '%s'", params={"groupAttributeName", "groupName"})
public class GroupAttributeSetEvent extends GroupAttributeEvent {

  public GroupAttributeSetEvent(VOMSGroup g, VOMSGroupAttribute groupAttribute) {

    super(g, groupAttribute);

  }

}
