package system.DAO;

import java.util.Collection;

import system.beans.Company;
import system.beans.Coupon;

public interface CompanyDAO {
	public Company createCompany(Company company);
	public void removeCompany(Company company);
	public void updateCompany(Company company);
	public Company getCompany(long id);
	public Collection<Company> getAllCompanies();
	public Collection<Coupon> getCoupons(Company company);
	public Company login(String compName, String password); 
}
