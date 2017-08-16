package system.mainPackage;

import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import system.DBDAO.CompanyDBDAO;
import system.DBDAO.CouponDBDAO;
import system.DBDAO.CustomerDBDAO;
import system.beans.Company;
import system.beans.Coupon;
import system.beans.CouponType;
import system.beans.Customer;
import system.exceptions.CompanyException;
import system.facades.AdminFacade;
import system.facades.ClientType;
import system.facades.CompanyFacade;
import system.facades.CustomerFacade;

public class TestMain {

	public static void main(String[] args) {
		
		System.out.println("STARTING SYSTEM");
		
		
			
		CouponSystem couponSystem = CouponSystem.getInstance();
		
	
		
		//when couponSystem is starting a Thread will start the 
		//ExpirdCouponCleaner task that will do just that 
		//but there is no coupons yet
		System.out.println("loging to admin facade");
		
		
		AdminFacade adminFacade = (AdminFacade) couponSystem.login("admin", "1234", ClientType.admin);

		try {
			
		for (int i = 0; i < 10 ; i++) {
			Company company = new Company();
			String companyName = "Company" + i;
			String companyPassword = ""+i+i+i+i;
			company.setCompName(companyName);
			company.setEmail( companyName +"@support.com");
			company.setPassword(companyPassword);
			adminFacade.createCompany(company);
		}
		} catch (Exception e) {
			e.getMessage();
		}
		
		System.out.println("trying to create the a Company with the same name twice");
		
		try {
			Company company = new Company();
			company.setCompName("Company1");
			adminFacade.createCompany(company);
			
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		
		System.out.println("Removing company by geting compnay");
		
		try {
			adminFacade.removeCompany(adminFacade.getCompnay(8));
		} catch (Exception e) {
			e.getMessage();
		}
		
		System.out.println("updating Company Details");
		
		try {
			Company company = adminFacade.getCompnay(4);
			company.setEmail("xxx@support.com");
			company.setPassword("xxxx");
			adminFacade.updateCompany(company);
		} catch (Exception e) {
			e.getMessage();
		}
		
		System.out.println("getting all companies ");
		
		try {
			
		System.out.println(adminFacade.getAllCompanies());
			
		} catch (Exception e) {
			e.getMessage();
		}
		
		System.out.println("getting the company we just updated");
		
		try {
			
			System.out.println(adminFacade.getCompnay(4));
			
		} catch (Exception e) {
			e.getMessage();
		}
		
		System.out.println("creating some customers");
		
		try {
			
		for (int i = 1; i < 51; i++) {
			Customer customer = new Customer();
			String customerName = "customer" + i;
			customer.setCustName(customerName);
			customer.setPassword("1234");
			adminFacade.createCustomer(customer);
		}
		
		} catch (Exception e) {
			e.getMessage();
		}
		
		
		System.out.println("getting all customers and removing 9 of them");
		
		try {
			for (Customer c : adminFacade.getAllCustomers()) {
				if(c.getId() > 20 && c.getId() < 30){
					adminFacade.removeCustomer(c);
				}
			}
		} catch (Exception e) {
			e.getMessage();
		}
		
		System.out.println("geting all customers now");
		
		try {
			
			System.out.println(adminFacade.getAllCustomers());
			
		} catch (Exception e) {
			e.getMessage();
		}
		
		try {
			
		System.out.println("upadting customer details and getting customer");
			Customer customer = adminFacade.getCustomer(1);
			customer.setCustName("Moshe Moshe");
			adminFacade.updateCustomer(customer);
			System.out.println(adminFacade.getCustomer(1));
			
		} catch (Exception e) {
			e.getMessage();
		}
		
			
		
		System.out.println("loging in to Company facade");
		
		Company company = adminFacade.getCompnay(1);
	
		CompanyFacade companyFacade = (CompanyFacade) couponSystem.login(company.getCompName(), company.getPassword(), ClientType.company);
		
		
			System.out.println("Creating a few coupons\n");
			try {
				
				
				for (int i = 0; i < 10; i++) {
					Coupon coupon = new Coupon();
					coupon.setTitle("Food coupon" + i);
					coupon.setMessage("Food coupon "+i);
					coupon.setAmount(i*i);
					coupon.setPrice(i*100);
					coupon.setStartDate(new Date());
					Date startDate = new Date();
					coupon.setEndDate(startDate);
					Date endDate = new Date();
					coupon.setEndDate(endDate);
					coupon.setType(CouponType.FOOD);
					coupon.setImage("link");
					companyFacade.createCoupon(coupon);
			}
				
				for (int i = 0; i < 10; i++) {
					Coupon coupon = new Coupon();
					coupon.setTitle("Camping coupon" + i);
					coupon.setMessage("Camping coupon "+i);
					coupon.setAmount(i*i);
					coupon.setPrice(i*100);
					coupon.setStartDate(new Date());
					Date startDate = new Date();
					coupon.setEndDate(startDate);
					Date endDate = new Date();
					coupon.setEndDate(endDate);
					coupon.setType(CouponType.CAMPING);
					companyFacade.createCoupon(coupon);
			}
				
				
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
			
			System.out.println("Updateing a Coupon");
			Coupon coupon = companyFacade.getCoupon(5);
			coupon.setPrice(1200.00);
			coupon.setEndDate(new Date());
			try {
				companyFacade.updateCoupon(coupon);
				
			} catch (Exception e) {
				e.getMessage();
			}
			
			System.out.println("Getting coupon");
			System.out.println(companyFacade.getCoupon(5));
			
			
			System.out.println("Getting all the company coupons");
			
			System.out.println(companyFacade.getAllCoupons());
			
			System.out.println("Logging in to customer facade");
			
			Customer customer = adminFacade.getCustomer(1);
			
			CustomerFacade customerFacade = (CustomerFacade) couponSystem.login(customer.getCustName(), customer.getPassword(), ClientType.customer);
			
			customerFacade.purchaseCoupon(companyFacade.getCoupon(11));
			
			
			
			
			
		
		
	}

}
