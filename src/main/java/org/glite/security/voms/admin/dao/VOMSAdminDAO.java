/*******************************************************************************
 *Copyright (c) Members of the EGEE Collaboration. 2006. 
 *See http://www.eu-egee.org/partners/ for details on the copyright
 *holders.  
 *
 *Licensed under the Apache License, Version 2.0 (the "License"); 
 *you may not use this file except in compliance with the License. 
 *You may obtain a copy of the License at 
 *
 *    http://www.apache.org/licenses/LICENSE-2.0 
 *
 *Unless required by applicable law or agreed to in writing, software 
 *distributed under the License is distributed on an "AS IS" BASIS, 
 *WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 *See the License for the specific language governing permissions and 
 *limitations under the License.
 *
 * Authors:
 *     Andrea Ceccanti - andrea.ceccanti@cnaf.infn.it
 *******************************************************************************/

package org.glite.security.voms.admin.dao;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.glite.security.voms.admin.common.Constants;
import org.glite.security.voms.admin.common.DNUtil;
import org.glite.security.voms.admin.common.NullArgumentException;
import org.glite.security.voms.admin.common.PathNamingScheme;
import org.glite.security.voms.admin.database.AlreadyExistsException;
import org.glite.security.voms.admin.database.HibernateFactory;
import org.glite.security.voms.admin.database.NoSuchAdminException;
import org.glite.security.voms.admin.database.NoSuchCAException;
import org.glite.security.voms.admin.database.VOMSDatabaseException;
import org.glite.security.voms.admin.model.Certificate;
import org.glite.security.voms.admin.model.VOMSAdmin;
import org.glite.security.voms.admin.model.VOMSCA;
import org.glite.security.voms.admin.model.VOMSGroup;
import org.glite.security.voms.admin.model.VOMSRole;
import org.glite.security.voms.admin.model.VOMSUser;
import org.glite.security.voms.admin.operations.VOMSPermission;
import org.hibernate.Query;



public class VOMSAdminDAO {

    public static final Log log = LogFactory.getLog( VOMSAdminDAO.class );

    protected VOMSAdminDAO() {

        HibernateFactory.beginTransaction();

    }

    public static VOMSAdminDAO instance() {

        return new VOMSAdminDAO();
    }

    public List getAll() {

        String query = "from org.glite.security.voms.admin.model.VOMSAdmin";
        List result = HibernateFactory.getSession().createQuery( query ).list();
        return result;
    }
    
    public List<VOMSAdmin> getNonInternalAdmins(){
        
        String caDN = "/O=VOMS/O=System%";
        String query = "from org.glite.security.voms.admin.model.VOMSAdmin where ca.subjectString not like :caDN";
        
        Query q = HibernateFactory.getSession().createQuery( query );
        q.setString( "caDN", caDN );
        
        return q.list();
        
    }

    public VOMSAdmin getAnyAuthenticatedUserAdmin() {

        String query = "from org.glite.security.voms.admin.model.VOMSAdmin as a where a.dn = :dn and a.ca.subjectString = :caDN";
        Query q = HibernateFactory.getSession().createQuery( query );

        q.setString( "dn", Constants.ANYUSER_ADMIN );
        q.setString( "caDN", Constants.VIRTUAL_CA );

        VOMSAdmin a = (VOMSAdmin) q.uniqueResult();

        if ( a == null )
            throw new VOMSDatabaseException(
                    "Database corrupted! ANYUSER_ADMIN not found in admins table!" );

        return a;

    }

    public VOMSAdmin getById(Long id){
        
        if (id == null)
            throw new NullArgumentException("id must be non-null!");
        
        return (VOMSAdmin)HibernateFactory.getSession().load(VOMSAdmin.class,id);
        
    }
    
    public VOMSAdmin getByName( String dn, String caDN ) {

        if ( dn == null )
            throw new NullArgumentException( "dn must be non-null!" );

        if ( caDN == null )
            throw new NullArgumentException( "caDN must be non-null!" );

        String query = "from org.glite.security.voms.admin.model.VOMSAdmin as a where a.dn = :dn and a.ca.subjectString = :caDN";
        Query q = HibernateFactory.getSession().createQuery( query );

        q.setString( "dn", dn );
        q.setString( "caDN", caDN );

        return (VOMSAdmin) q.uniqueResult();
        
    }
     
    public VOMSAdmin getFromUser(VOMSUser u){
        
        assert u != null: "Cannot look for an admin starting from a null user!";
        
        VOMSAdmin result = null;
        
        for(Certificate c: u.getCertificates()){
        
            // Return the first certificate found...
            result = getByName( c.getSubjectString(), c.getCa().getSubjectString() );
            
            if (result != null)
                break;
        }
        
        return result;
    }
    
    public VOMSAdmin createFromUser(VOMSUser u){
        
        assert u != null: "Cannot create an admin starting from a null user!";
        
        VOMSAdmin admin = getFromUser( u );
        
        if (admin != null)
            return admin;
        
        admin = new VOMSAdmin();
        Certificate c = u.getDefaultCertificate();
        
        admin.setDn( c.getSubjectString() );
        admin.setCa( c.getCa());
        admin.setEmailAddress( u.getEmailAddress() );
             
        HibernateFactory.getSession().save( admin );
        
        return admin;
    }
    
    public List getRoleAdmins(VOMSRole r){
        String searchString = "%Role="+r.getName();
        String query = "from org.glite.security.voms.admin.model.VOMSAdmin where dn like :searchString";
        
        return HibernateFactory.getSession().createQuery( query ).setString( "searchString", searchString ).list();
        
    }
    public void deleteRoleAdmins(VOMSRole r){
        
        String searchString = "%Role="+r.getName();
        String query = "from org.glite.security.voms.admin.model.VOMSAdmin where dn like :searchString";
        Iterator i= HibernateFactory.getSession().createQuery( query ).setString( "searchString", searchString ).iterate();
        
        while (i.hasNext()){
            
            VOMSAdmin a = (VOMSAdmin)i.next();
            delete( a );
        }
        
    }
    
    
    public List<VOMSAdmin> getTagAdmins(){
        
        String query = "from org.glite.security.voms.admin.model.VOMSAdmin where ca.subjectString = :caSubject";
        
        return HibernateFactory.getSession().createQuery( query ).setString( "caSubject", Constants.TAG_CA).list();
        
    }
    
    
    
    public VOMSAdmin getByFQAN(String fqan){
        
        if (fqan == null)
            throw new NullArgumentException("fqan must be non-null!");
        
        if (PathNamingScheme.isGroup( fqan ))
            
            return getByName( fqan, Constants.GROUP_CA );
        
        else if (PathNamingScheme.isQualifiedRole( fqan ))
            
            return getByName( fqan, Constants.ROLE_CA );
        
        return null;
    }
    
    public VOMSAdmin create( String fqan){
        
        if (fqan == null)
            throw new NullArgumentException("fqan must be non-null!");
        
        PathNamingScheme.checkSyntax( fqan );
        
        VOMSAdmin admin = new VOMSAdmin();
        admin.setDn( fqan );
        
        if (PathNamingScheme.isGroup( fqan ))
            
            admin.setCa( VOMSCADAO.instance().getGroupCA() );
        
        else if (PathNamingScheme.isQualifiedRole( fqan ))
            
            admin.setCa( VOMSCADAO.instance().getRoleCA());
        
        HibernateFactory.getSession().save( admin );
        
        return admin;
            
        
    }
    
    public VOMSAdmin getFromTag(String tagName){
        
        assert tagName != null: "Cannot get a VOMS Admin from a null tagName";
        
        String query = "from org.glite.security.voms.admin.model.VOMSAdmin where ca.subjectString = :caSubject and dn = :tagName";
        
        Query q = HibernateFactory.getSession().createQuery( query ).setString( "caSubject", Constants.TAG_CA).setString( "tagName", tagName); 
        return (VOMSAdmin) q.uniqueResult();
        
    }
    
    public VOMSAdmin createFromTag(String tagName){
        
        assert tagName != null: "Cannot create a VOMS Admin from a null tag name!";
        
        VOMSAdmin tagAdmin = getFromTag( tagName );
        
        if (tagAdmin != null)
            throw new AlreadyExistsException("Admin for tag '"+tagName+"' already exists!");
        
        return create(tagName,Constants.TAG_CA);
        
    }

    public VOMSAdmin create(String dn, String caDN){
        
        return create(dn,caDN,null);
    }
    
    public VOMSAdmin create( String dn, String caDN, String emailAddress ) {

        if ( dn == null )
            throw new NullArgumentException( "dn must be non-null!" );

        if ( caDN == null )
            throw new NullArgumentException( "caDN must be non-null!" );

        VOMSAdmin admin = new VOMSAdmin();
        VOMSCA ca = VOMSCADAO.instance().getByName( caDN );

        if ( ca == null )
            throw new IllegalArgumentException( "Unkown CA " + caDN
                    + " passed as argument!" );

        dn = DNUtil.normalizeEmailAddressInDN( dn );
        admin.setDn( dn );
        admin.setCa( ca );
        admin.setEmailAddress( emailAddress );
        HibernateFactory.getSession().save( admin );
        return admin;

    }

    public VOMSAdmin create( VOMSAdmin admin ) {

        if ( admin == null )
            throw new NullArgumentException( "admin must not be null!" );

        admin.setDn( DNUtil.normalizeEmailAddressInDN( admin.getDn() ) );
        HibernateFactory.getSession().save( admin );
        return admin;

    }

    public void delete( VOMSAdmin admin ) {

        if ( admin == null )
            throw new NullArgumentException( "admin must not be null!" );

        HibernateFactory.getSession().delete( admin );

    }

    public List<VOMSAdmin> getFromCA(VOMSCA ca){
        
        assert ca != null: "ca must be non-null!";
        
        String query = "from VOMSAdmin where ca = :ca";
        return HibernateFactory.getSession().createQuery( query ).setEntity( "ca", ca ).list();
        
    }
    public void deleteFromCA(VOMSCA ca){
        
        assert ca != null: "ca must be non-null!";
                
        log.debug( "Deleting all admins from CA '"+ca+"'" );
        ACLDAO aclDAO = ACLDAO.instance();
        
        for (VOMSAdmin a: getFromCA( ca )){
            
            if (aclDAO.hasActivePermissions( a ))
                aclDAO.deletePermissionsForAdmin( a );
            
            HibernateFactory.getSession().delete( a );
        }
        
    }
    public void delete( String dn, String caDN ) {

        if ( dn == null )
            throw new NullArgumentException( "dn must be non-null!" );

        if ( caDN == null )
            throw new NullArgumentException( "caDN must be non-null!" );

        VOMSCA ca = VOMSCADAO.instance().getByName( caDN );

        if ( ca == null )
            throw new NoSuchCAException( "Unknown CA '" + caDN
                    + "'." );

        // FIXME: do it without using an HQL update!
        Query q = HibernateFactory.getSession().createQuery(
                "delete from org.glite.security.voms.admin.model.VOMSAdmin where dn = :dn and ca =:ca" )
                .setString( "dn", dn ).setParameter( "ca", ca );

        q.executeUpdate();
    }

    public VOMSAdmin createAuthorizedAdmin(String dn, String caDN){
        
        VOMSAdmin admin = create( dn,caDN );
        VOMSGroup g = VOMSGroupDAO.instance().getVOGroup();
        
        if (g == null)
            throw new VOMSDatabaseException("Vo group still undefined in org.glite.security.voms.admin.database!");
        
        g.getACL().setPermissions( admin, VOMSPermission.getAllPermissions() );
        
        HibernateFactory.getSession().save( admin );
        HibernateFactory.getSession().save( g );
        
        return admin;    
    }
    
    public void saveOrUpdate( VOMSAdmin a ) {

        HibernateFactory.getSession().saveOrUpdate( a );
    }

    public void update( VOMSAdmin a ) {

        HibernateFactory.getSession().update( a );
    }
    
    public void assignTag(VOMSAdmin a, VOMSAdmin tag){
        
        assert a != null: "Cannot assign a tag to a null admin!";
        assert tag != null: "Cannot assign an admin to a null tag!";
        
        if (a.isInternalAdmin())
            throw new IllegalArgumentException("Cannot assign a tag to an internal admin (tag, role or group admin)!");
        
        if (!tag.isTagAdmin())
            throw new IllegalArgumentException("tag is not a tag admin!");
        
        a.getTags().add( tag );
        HibernateFactory.getSession().update( a );
        
    }
    
    public void removeTag(VOMSAdmin a, VOMSAdmin tag){
        assert a != null: "Cannot remove a tag from a null admin!";
        assert tag != null: "Cannot remove a null tag from an admin !";
        
        if (a.isInternalAdmin())
            throw new IllegalArgumentException("Cannot assign a tag to an internal admin (tag, role or group admin)!");
        
        if (!tag.isTagAdmin())
            throw new IllegalArgumentException("tag is not a tag admin!");
        
        a.getTags().remove( tag );
        HibernateFactory.getSession().update( a );        
    }
    
    public List<VOMSAdmin> getUnassignedTagsForAdmin(long adminId){
        
        VOMSAdmin a = getById( adminId );
        if (a == null)
            throw new NoSuchAdminException("Admin with id '"+adminId+"' not found!");
        
        return getUnassignedTagsForAdmin( a );
    }
    public List<VOMSAdmin> getUnassignedTagsForAdmin(VOMSAdmin a){
        assert a != null: "Cannot get unassigned tags for a null admin!";
        
        if (a.isTagAdmin())
            throw new IllegalArgumentException("Cannot get unassigned tags for a tag admin!");
        
        List<VOMSAdmin> unassignedTags = new ArrayList<VOMSAdmin>();
        List<VOMSAdmin> allTags = getTagAdmins();
        
        for (VOMSAdmin tagAdmin: allTags){
            if (!a.getTags().contains( tagAdmin ))
                unassignedTags.add( tagAdmin );
        }
        
        return unassignedTags;
    }
}
