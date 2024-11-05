package de.mpg.aai.security.auth.model;

import java.security.Principal;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

/**
 * @author megger
 *
 */
public class BaseGroup extends BasePrincipal {
	private List<Principal> members = new Vector<Principal>();
	

	/**
	 * {@inheritDoc}
	 */
	public BaseGroup(String username) {
		super(username);
	}
	
	
	/**
	 * Adds the specified member to the group.
	 * 
	 * @param user the principal to add to this group.
	 * @return true if the member was successfully added, false if the principal was already a member.
	 *
	 * @see https://docs.oracle.com/javase/8/docs/api/java/security/acl/Group.html#addMember-java.security.Principal-
	 */
	public boolean addMember(Principal user) {
		// important to check to avoid duplicates 
		// <=> could not use a Set, instead used Vector
		//	to have an easy getter to return Enumerations in #members()
		//	<=> also must avoid duplicates for proper hashcode generation in #hashcode 
		if(this.members.contains(user))
			return false;
		return this.members.add(user);
	}
	
	
	/**
	 * Returns true if the passed principal is a member of the group. This method does a recursive search, so if a principal belongs to a group which is a member of this group, true is returned.
	 * 
	 * @param user the principal whose membership is to be checked.
	 * @return true if the principal is a member of this group, false otherwise.
	 *
	 * @see https://docs.oracle.com/javase/8/docs/api/java/security/acl/Group.html#isMember-java.security.Principal-
	 */
	public boolean isMember(Principal user) {
		return this.members.contains(user);
	}
	
	
	/**
	 * Returns an enumeration of the members in the group. The returned objects can be instances of either Principal or Group (which is a subclass of Principal).
	 * 
	 * @return an enumeration of the group members.
	 *
	 * @see https://docs.oracle.com/javase/8/docs/api/java/security/acl/Group.html#members--
	 */
	@SuppressWarnings("unchecked")
	public Enumeration<? extends Principal> members() {
		return ((Vector) this.members).elements();
	}
	
	
	/**
	 * Removes the specified member from the group.
	 * 
	 * @param user the principal to remove from this group.
	 * @return true if the principal was removed, or false if the principal was not a member.
	 *
	 * @see https://docs.oracle.com/javase/8/docs/api/java/security/acl/Group.html#removeMember-java.security.Principal-
	 */
	public boolean removeMember(Principal user) {
		return this.members.remove(user);
	}
	
	
	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unused")
	@Override
	protected Object clone() throws CloneNotSupportedException {
		BaseGroup result = new BaseGroup(this.getName());
		for(Principal member : this.members) {
			result.addMember(member);
		}
		return result;
	}
	
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object obj) {
		if(!super.equals(obj))
			return false;
		if(!(obj instanceof BaseGroup))	// strict
			return false;
		// check members: 
		Enumeration<? extends Principal> otherMembers = ((BaseGroup) obj).members();
		int count=0;
		for(Principal member ; otherMembers.hasMoreElements() ; count++) {
			member = otherMembers.nextElement();
			if(!this.members.contains(member))
				return false;
		}
		// ok, all other.members in this.members => same vice versa?
		return this.members.size() == count;	// sufficient <=> this.members must contain no duplicates 
	}
	
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		int result = 1;
		result = 31 * result + super.hashCode();	// using: name
		// add members (ignore order) 
//		for(Principal member : this.members) {
//			result += 31 * result + (member ==null ? 0 : member.hashCode());
//		}
		result += 31 * result + this.members.hashCode();
		return result;
	}
	
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		StringBuffer result = new StringBuffer("BaseGroup '");
		result.append(this.getName()).append("'");
		return result.toString();
	}
	
	
}
