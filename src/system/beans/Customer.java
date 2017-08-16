package system.beans;

import java.util.ArrayList;
import java.util.Collection;

/**
 * 
 * @author Liron
 * this Class creates a Customer POJO bean  
 */
public class Customer {
	private long id;
	private String custName;
	private String password;
	private Collection<Coupon> coupons = new ArrayList<Coupon>();
	
	public Customer() {
		
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getCustName() {
		return custName;
	}

	public void setCustName(String custName) {
		this.custName = custName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Collection<Coupon> getCoupons() {
		return coupons;
	}

	public void setCoupons(Collection<Coupon> coupons) {
		this.coupons = coupons;
	}
	
	@Override
	public String toString() {
		return custName;
	}
}
