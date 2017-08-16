package system.mainPackage;

import system.facades.AdminFacade;
import system.facades.ClientType;
import system.facades.CompanyFacade;
import system.facades.CouponClientFacade;
import system.facades.CustomerFacade;

public class CouponSystem implements CouponClientFacade{

	private static CouponSystem couponSystem;
	private static ExpiredCouponsCleaner cleaner;
	
	private CouponSystem(){
		cleaner = new ExpiredCouponsCleaner();
		cleaner.start();
	}
	
	public CouponClientFacade login(String username, String password, ClientType type) {
		switch (type) {
		case admin:
			AdminFacade admin = new AdminFacade(password, username);
			if(admin.isLogged())
			{
				System.out.println("Login Succesful");
				return admin;
			}
			else
			{
				System.out.println("Login Faild");
				return null;
			}
		case company:
			CompanyFacade company = new CompanyFacade(password, username);
			if(company.isLogged())
			{
				System.out.println("Login Succesful");
				return company;
			}
			else
			{
				System.out.println("Login Faild");
				return null;
			}
		case customer:
			CustomerFacade customer = new CustomerFacade(password, username);
			if(customer.isLogged())
			{
				System.out.println("Login Succesful");
				return customer;
			}
			else
			{
				System.out.println("Login Faild");
				return null;
			}
		default:
			break;
		}
		return null;
	}
	
	public void shutDown() {
		cleaner.stopTask();
		
	}
	public static CouponSystem getInstance(){
		if(couponSystem == null){
			couponSystem = new CouponSystem();
		}
		return couponSystem;	
	}
	
	
	
}
