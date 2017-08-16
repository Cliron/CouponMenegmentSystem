package system.facades;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;

import system.DAO.CompanyDAO;
import system.DAO.CouponDAO;
import system.DAO.CustomerDAO;
import system.DBDAO.CompanyDBDAO;
import system.DBDAO.CouponDBDAO;
import system.DBDAO.CustomerDBDAO;
import system.beans.Company;
import system.beans.Coupon;
import system.beans.CouponType;
import system.beans.Customer;

public class CustomerFacade implements CouponClientFacade {

	private Customer customer;
	//private CompanyDAO companyDBDAO;
	private CustomerDAO customerDBDAO;
	private CouponDAO couponDBDAO;
	
	private boolean loggedIn;
	
	

public CustomerFacade(String customerName, String password){
		
		Customer theCustomer = new CustomerDBDAO().Login(customerName, password);{
			if(theCustomer != null)
			{
			customer = theCustomer;
			loggedIn = true;
			//companyDBDAO = new CompanyDBDAO();
			customerDBDAO = new CustomerDBDAO();
			couponDBDAO = new CouponDBDAO();
			}
		}	
	}

	public boolean isLogged() {
	return loggedIn;
	}
	
	public void purchaseCoupon(Coupon coupon){
	
		if(coupon.getAmount()>0){
			if(coupon.getEndDate().after(new Date())){
				for (Coupon c : customer.getCoupons()) {
					if(c.getId() == coupon.getId()){
						System.out.println("coupon has already been purchased one by the customer");
						break;
					}else {
						coupon.setAmount(coupon.getAmount()-1);
						couponDBDAO.updateCoupon(coupon);
					}
				}
			}
		}else{
			System.out.println("out of stock");
		}
	
	}
	
	public Collection<Coupon> getAllPurchasedCoupons(Customer customer){
		return customerDBDAO.getCoupons(customer);
	}
	
	public Collection<Coupon> getAllPurchasedCouponsByType(CouponType type){
		ArrayList<Coupon> customerPerchasedCuponsByType = new ArrayList<>();
		for (Coupon coupon : customer.getCoupons()) {
			if(coupon.getType().ordinal()== type.ordinal()){
				customerPerchasedCuponsByType.add(coupon);
			}if(customerPerchasedCuponsByType.isEmpty() == true){
				System.out.println("No Coupons of that type has been purchased");
				return null;
			}
		}
		return customerPerchasedCuponsByType;
	}
	
	public Collection<Coupon> getAllPurchasedCouponsUpToPrice(double price){
		
		ArrayList<Coupon> allCustomerUpToPrice = new ArrayList<Coupon>();
		for (Coupon coupon : customer.getCoupons()) {
			if (coupon.getPrice() < price)
				allCustomerUpToPrice.add(coupon);
		}
		if(allCustomerUpToPrice.isEmpty()){
			System.out.println("the customer did not purchased coupons under the price of "+ price);
			return null;
		}
			
		return allCustomerUpToPrice;
		
	}

}
