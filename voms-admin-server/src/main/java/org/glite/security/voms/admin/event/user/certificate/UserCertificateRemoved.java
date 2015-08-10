package org.glite.security.voms.admin.event.user.certificate;

import org.glite.security.voms.admin.event.EventDescription;
import org.glite.security.voms.admin.persistence.model.Certificate;
import org.glite.security.voms.admin.persistence.model.VOMSUser;

@EventDescription(message = "removed certificate '%s' from user '%s %s'",
params = { "userCertificate", "userName", "userSurname" })
public class UserCertificateRemoved extends UserCertificateEvent {

  public UserCertificateRemoved(VOMSUser payload, Certificate certificate) {

    super(payload, certificate);
   
  }

}