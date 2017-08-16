package system.beans;

import java.util.ArrayList;
import java.util.Collection;

/**
 * 
 * @author Liron
 *  this Class creates a Company POJO bean
 */
public class Company {
	private long id;
	private String compName;
	private String password;
	private String email;
	private Collection<Coupon> coupons = new ArrayList<Coupon>();
	
	public Company() {
		
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getCompName() {
		return compName;
	}

	public void setCompName(String compName) {
		this.compName = compName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Collection<Coupon> getCoupons() {
		return coupons;
	}

	public void setCoupons(Collection<Coupon> coupons) {
		this.coupons = coupons;
	}

	@Override
	public String toString() {
		return "Company [id=" + id + ", compName=" + compName + ", email=" + email + "]";
	}
	
	
	
}
