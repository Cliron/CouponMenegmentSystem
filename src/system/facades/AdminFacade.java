package system.facades;

import java.util.Collection;

import system.DAO.CompanyDAO;
import system.DAO.CouponDAO;
import system.DAO.CustomerDAO;
import system.DBDAO.CompanyDBDAO;
import system.DBDAO.CouponDBDAO;
import system.DBDAO.CustomerDBDAO;
import system.beans.Company;
import system.beans.Coupon;
import system.beans.Customer;
import system.exceptions.CompanyException;
import system.exceptions.CustomerException;

public class AdminFacade implements CouponClientFacade {

	private CompanyDAO companyDBDAO;
	private CustomerDAO customerDBDAO;
	private CouponDAO couponDBDAO;
	
	private boolean loggedIn;
	
	
	public AdminFacade(String password,String username){
		
		if (username.equals("admin")&& password.equals("1234")){
			loggedIn = true;
			companyDBDAO = new CompanyDBDAO();
			customerDBDAO = new CustomerDBDAO();
			couponDBDAO = new CouponDBDAO();
		}	
	}
	
	public boolean isLogged(){
		return loggedIn;
	}
	
	
	public Company createCompany(Company company) throws CompanyException {
		return companyDBDAO.createCompany(company);
	}
	
	public void removeCompany (Company company) throws CompanyException{
		if(company != null){
		companyDBDAO.removeCompany(company);
		for (Coupon coupon : company.getCoupons()) {
			couponDBDAO.removeCoupon(coupon);
			}
		}
	}
	
	public void updateCompany (Company company) throws CompanyException{
		companyDBDAO.updateCompany(company);
	}
	
	public Collection<Company> getAllCompanies() throws CompanyException {
		return companyDBDAO.getAllCompanies();
	}
	
	public Company getCompnay(long id) throws CompanyException {
		return companyDBDAO.getCompany(id);
	}
	
	public Customer createCustomer(Customer customer) throws CustomerException {
		return customerDBDAO.createCustomer(customer);
	}
	
	public void removeCustomer(Customer customer) throws CustomerException {
		customerDBDAO.removeCustomer(customer);
	}
	
	public void updateCustomer(Customer customer) throws CustomerException {
		customerDBDAO.updateCustomer(customer);
	}
	
	public Collection<Customer> getAllCustomers() throws CustomerException {
		return customerDBDAO.getAllCustomer();
	}
	
	public Customer getCustomer(long id) throws CustomerException {
		return customerDBDAO.getCustomer(id);
	}
	

}
