package system.DBDAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import system.DAO.CompanyDAO;
import system.beans.Company;
import system.beans.Coupon;
import system.beans.CouponType;
import system.exceptions.CompanyException;
import system.mainPackage.ConnectionPool;

public class CompanyDBDAO implements CompanyDAO {

	private ConnectionPool pool;

	public CompanyDBDAO()  {
		try {
			this.pool = ConnectionPool.getInstance();
		} catch (SQLException | ClassNotFoundException e) {
			System.out.println("No Connection to DB");
			e.printStackTrace();
		} 
	}
	
	
	/**CREATE COMPANY
	 * takes in a company Object
	 * and inserting it to the DB
	 * @return Company Object
	 */
	
	
	@Override
	public Company createCompany(Company company) {
		Connection connection = null;
		ResultSet nameChecker = null;
		PreparedStatement add = null;
		ResultSet id = null;
		
		
		try {
			connection = pool.getConnection();
			
			//Checking to see if the company already exists
			nameChecker = connection.createStatement().executeQuery( "SELECT * FROM app.company WHERE comp_name = '" + company.getCompName() +"'");
			
			
			if (nameChecker.next()){
				throw new CompanyException("Company already exists in the DB");
			}
			
			// Creates the company in the database
			String sql = "INSERT INTO app.company (comp_name, password, email) VALUES ( ?, ?, ?)";
			add = connection.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
			add.setString(1, company.getCompName());
			add.setString(2, company.getPassword());
			add.setString(3, company.getEmail());
			add.execute();
			System.out.println("Company added");
			
			//assigning Auto Generated Keys to the company Object
			id = add.getGeneratedKeys();
			if(id.next()){
				company.setId(id.getLong(1));
				System.out.println(" ID = "+company.getId()+"\n Name: " + company.getCompName() + "\n Email: " +company.getEmail() );
			}
			return company;
		}
		catch(SQLException e)
		{
			System.out.println(e.getMessage());
			System.out.println(e.getSQLState());
			
		}
		finally {
			if(connection != null) 
				pool.returnConnection(connection);
		}
		return null;
	}

	
	/**REMOVE COMPANY
	 * removes all company information
	 * including the company coupons
	 * @void
	 */
	
	@Override
	public void removeCompany(Company company) {
		Connection connection = null;
		ResultSet nameChecker = null;
		Statement couponsRemover = null;
		try {
			connection = pool.getConnection();
			//checks to see if there is a company by that name 
			nameChecker = connection.createStatement().executeQuery("SELECT * FROM app.company WHERE comp_name = '" +company.getCompName() +"'");
			if (!nameChecker.next()){
				throw new CompanyException("Company Does Not Exist");
			}
			//Remove all existing company information from the database 
			Statement statement = connection.createStatement();
			statement.execute("DELETE FROM Company WHERE comp_name = '" +company.getCompName()+"'");
			
			//Removes all company coupons from DB 
			couponsRemover = connection.createStatement();
			couponsRemover.executeUpdate("delete from app.company_coupon where comp_id = " + company.getId());
			
		}
		catch(SQLException e)
		{
			System.out.println(e.getMessage());
			System.out.println(e.getSQLState());
		}
		finally {
			if(connection != null) 
				pool.returnConnection(connection);
		}
		
	}

	/**UPDATE COMPANY
	 * updating company details 
	 * dosn't updating the company name 
	 * @void
	 */
	
	@Override
	public void updateCompany(Company company) {
		
			Connection connection = null;
			Statement removeCompCoup, addCompCoup = null;
			PreparedStatement updateCompDetails = null;
			
			//updates company details
			//password and email
			
			try {
				connection = pool.getConnection();
				updateCompDetails = connection.prepareStatement("update app.company set password = ?, email  = ? where comp_id = " + company.getId() );
				updateCompDetails.setString(1, company.getPassword());
				updateCompDetails.setString(2, company.getEmail());
				updateCompDetails.executeUpdate();
				removeCompCoup = connection.createStatement();
				removeCompCoup.executeUpdate("delete from app.company_coupon where comp_id = " + company.getId());
				addCompCoup = connection.createStatement();
				for (Coupon coupon : company.getCoupons()) {
					addCompCoup.executeUpdate("insert into app.company_coupon (comp_id, coupon_id)"
							+ " values (" + company.getId() + "," + coupon.getId() + ")");
				}
				
				System.out.println("Mission Accomplished");
				
				
			}
			catch(SQLException e)
			{
				System.out.println(e.getMessage());
				System.out.println(e.getSQLState());
			}
			finally {
				if(connection != null) 
					pool.returnConnection(connection);
			}
		
	}

	
	
	/**GET COMAPNY
	 * getting all company information
	 * takes in a long id 
	 * @return Company Object
	 */
	
	@Override
	public Company getCompany(long id) {
		Connection connection = null;
		Company company = new Company();
		Collection<Coupon> companyCoupons = new ArrayList<Coupon>();
		
		//gets the company  by id
		//the function takes in a long id 
		//and returns a company object matching the id 
		
		
		try {
			connection = pool.getConnection();
			connection.createStatement();
			
			
			//Retrieves all the company's Information by id
			Statement statement = connection.createStatement();
			ResultSet result = statement.executeQuery(String.format("SELECT * FROM Company WHERE comp_id = %d", id));

			if(result.next()) {
				company = new Company();
				company.setId(result.getLong("comp_id"));
				company.setCompName(result.getString("comp_name"));
				company.setPassword(result.getString("password"));
				company.setEmail(result.getString("email"));
				
			//gets the company coupons 
			Statement getCoupons = connection.createStatement();
			ResultSet getCouponsRs = getCoupons.executeQuery("SELECT * FROM company_coupon WHERE comp_id = " + company.getId());
			while(getCouponsRs.next()){
				CouponDBDAO couponDBDAO = new CouponDBDAO();
				companyCoupons.add(couponDBDAO.getCoupon(getCouponsRs.getLong(2)));
			}
			
			return company;
					
			}else {
				throw new CompanyException("Company id is not Valid");
			}
			
		}
		catch(SQLException e)
		{
			System.out.println(e.getMessage());
			System.out.println(e.getSQLState());
		}
		finally {
			if(connection != null) 
				pool.returnConnection(connection);
		}
		return null;
		
	
	}

	/**GET ALL COMPANIES
	 * gets all the companies in the system
	 * @return HashSet<COMPANY>
	 * (HashSet of Companies will eliminate Duplications)
	 */
	
	@Override
	public Collection<Company> getAllCompanies() {
		
		Connection connection = null;
		Company company = null;
		HashSet<Company> allCompanies= new HashSet<Company>();
		Collection<Coupon> allCompanyCoup = new ArrayList<Coupon>();
		try {
			connection = pool.getConnection();
			
			
			Statement statement = connection.createStatement();
			ResultSet result = statement.executeQuery("SELECT * FROM app.company");
			
			while(result.next()){
				
				company = new Company();
				company.setId(result.getLong("comp_id"));
				company.setCompName(result.getString("comp_name"));
				company.setPassword(result.getString("password"));
				company.setEmail(result.getString("email"));
				
				Statement getCoupons = connection.createStatement();
				ResultSet getCouponsRs = getCoupons.executeQuery("SELECT * FROM app.company_coupon Where comp_id =" + company.getId());
				
				while(getCouponsRs.next()){
					CouponDBDAO couponDBDAO = new CouponDBDAO();
					allCompanyCoup.add(couponDBDAO.getCoupon(getCouponsRs.getLong("COUPON_ID")));
				}
				company.setCoupons(allCompanyCoup);
				allCompanies.add(company);
			}
		
		}
		catch(SQLException e)
		{
			System.out.println(e.getMessage());
			System.out.println(e.getSQLState());
		}
		finally {
			if(connection != null) 
				pool.returnConnection(connection);
		}
		return allCompanies;
	}

	/**GET COUPONS 
	 *takes in a Company Object and returns
	 *the company coupons
	 *@return HashSet<COUPONS> 
	 */
	
	@Override
	public Collection<Coupon> getCoupons(Company company) {
		Connection connection = null;
		HashSet<Coupon> allCoupons= new HashSet<Coupon>();
		try {
			connection = pool.getConnection();
			
			Statement statement = connection.createStatement();
			ResultSet result = statement.executeQuery("SELECT * FROM app.company_coupon where COMP_ID =" + company.getId());
			
			while(result.next()){
				
			Statement getTheCoupon = connection.createStatement();
			ResultSet couponRs = getTheCoupon.executeQuery("SELECT * FROM app.coupon WHERE id =" + result.getLong("COUPON_ID"));
			while(couponRs.next())
				{
				Coupon coupon = new Coupon();
				coupon.setId(couponRs.getLong("ID"));
				coupon.setTitle(couponRs.getString("Title"));
				coupon.setStartDate(couponRs.getDate("Start_Date"));
				coupon.setEndDate(couponRs.getDate("End_Date"));
				coupon.setAmount(couponRs.getInt("Amount"));
				coupon.setMessage(couponRs.getString("Message"));
				coupon.setType(CouponType.values()[couponRs.getInt("Type")]);
				coupon.setPrice(couponRs.getDouble("Price"));
				coupon.setImage(couponRs.getString("Image"));
				allCoupons.add(coupon);
				}
			return allCoupons;
			}
		
			
			
		}
		catch(SQLException e)
		{
			System.out.println(e.getMessage());
			System.out.println(e.getSQLState());
		}
		finally {
			if(connection != null) 
				pool.returnConnection(connection);
		}
		return null;
	}

	/**LOGIN COMPANY
	 *  checks to see if a company with 
	 *  this password and username exists in the system 
	 *  and returns it if one exists  
	 * @return company
	 */
	@Override
	public Company login(String compName, String password) {
		
		Connection connection = null;
		PreparedStatement checkCompany = null;
		ResultSet checkCompanyRs = null;
		
		try {
			connection = pool.getConnection();
			
			checkCompany = connection.prepareStatement("select * from app.company where comp_name =? and password=?");
			checkCompany.setString(1, compName);
			checkCompany.setString(2, password);
			checkCompanyRs = checkCompany.executeQuery();
			
			if(checkCompanyRs.next()){
				
				return getCompany(checkCompanyRs.getLong("comp_id"));
				
				}else {
				System.out.println("Incorrect username or password");
				return null;
				}
				
			}catch (SQLException e) {
				e.getMessage();
			
			} finally {
			if(connection != null){
				pool.returnConnection(connection);
			}
			
		
		}
		return null;

	
	} 
}
