package system.DBDAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import system.DAO.CustomerDAO;
import system.beans.Coupon;
import system.beans.CouponType;
import system.beans.Customer;
import system.exceptions.CustomerException;
import system.mainPackage.ConnectionPool;

public class CustomerDBDAO implements CustomerDAO {

	private ConnectionPool pool;

	public CustomerDBDAO()  {
		try {
			this.pool = ConnectionPool.getInstance();
		} catch (SQLException | ClassNotFoundException e) {
			System.out.println("No Connection to DB");
			e.printStackTrace();
		} 
	}

	/**CREATE CUSTOMER
	 * take in customer 
	 * adds to system
	 * @return Customer
	 * 
	 */
	
	@Override
	public Customer createCustomer(Customer customer) {
		Connection connection = null;
		ResultSet nameChecker, id = null;
		PreparedStatement add = null;
		
		
		
		
		try {
			connection = pool.getConnection();
			
			//Checking to see if the Customer already exists
			nameChecker = connection.createStatement().executeQuery( "SELECT * FROM app.customer "
					+ "WHERE cust_name = '" + customer.getCustName() +"'");
			
			if (nameChecker.next()){
				throw new CustomerException("Customer already exists in the DB");
			}else{
			
			// Creates the company in the database
			String sql = "INSERT INTO app.customer (cust_name, password) VALUES ( ?, ?)";
			add = connection.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
			add.setString(1, customer.getCustName());
			add.setString(2, customer.getPassword());
			add.execute();
			System.out.println("Customer added");
			
			id = add.getGeneratedKeys();
			if(id.next()){
				customer.setId(id.getLong(1));
				System.out.println(" ID = "+customer.getId()+"\n Name :" + customer.getCustName());
			}
			return customer;
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

	/**REMOVE CUSTOMER
	 * take in customer
	 * removes it from the system
	 * @void
	 */
	
	@Override
	public void removeCustomer(Customer customer) {
	
		Connection connection = null;
		ResultSet nameChecker = null;
		Statement couponsRemover = null;
		try {
			connection = pool.getConnection();
			
			nameChecker = connection.createStatement().executeQuery("SELECT * FROM app.customer WHERE cust_name = '" +customer.getCustName() +"'");
			if (!nameChecker.next()){
				throw new CustomerException("Customer Does Not Exist");
			}
			//Remove all existing Customer information from the database 
			Statement statement = connection.createStatement();
			statement.execute("DELETE FROM Customer WHERE cust_name = '" +customer.getCustName()+"'");
			
			//Removes all Customer coupons from DB 
			couponsRemover = connection.createStatement();
			couponsRemover.executeUpdate("delete from app.customer_coupon where cust_id = " + customer.getId());
			System.out.println("The customer named = " + customer.getCustName() + " was deleted from the system ");
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

	/**UPDATE CUSTOMER 
	 * takes in a customer 
	 * updates all customer details except name
	 * @void
	 */
	
	@Override
	public void updateCustomer(Customer customer) {
		
		Connection connection = null;
		Statement exist, removeCustCoup, addCustCoup, updates = null;
		ResultSet existRs = null;
		
		
		try{
		connection = pool.getConnection();
		exist = connection.createStatement();
		existRs = exist.executeQuery("SELECT * FROM app.customer WHERE cust_id =" + customer.getId());
		
		if(existRs.next()){
		removeCustCoup = connection.createStatement();
		removeCustCoup.executeUpdate("delete from app.customer_coupon where cust_id = " + customer.getId());
		addCustCoup = connection.createStatement();
		for (Coupon coupon : customer.getCoupons()) {
			addCustCoup.executeUpdate("insert into app.customer_coupon (cust_id, coupon_id)"
					+ " values (" + customer.getId() + "," + coupon.getId() + ")");	
		}
		updates = connection.createStatement();
		updates.executeUpdate("UPDATE app.customer SET password = '" + customer.getPassword() + "'"
				+ "WHERE CUST_ID = " + customer.getId());
		
		System.out.println("The Customer " + customer.getCustName() + " Updated");
		
		}
		
			
		}catch(SQLException e){
			System.out.println(e.getMessage());
			System.out.println(e.getSQLState());
			
		}finally{
			if(connection != null){
				pool.returnConnection(connection);
			}
			
		}
		
	}

	/**GET CUSTOMER 
	 * take in long ID 
	 * @return Customer
	 */
	@Override
	public Customer getCustomer(long id) {
		
		Connection connection = null;
		Statement statement, getCoupons = null;
		ResultSet resultSet, couponRs = null;
		Customer customer = null;
		ArrayList<Coupon> coupons = new ArrayList<>();
		try {
			connection = pool.getConnection();
			statement = connection.createStatement();
			resultSet = statement.executeQuery("SELECT * FROM app.customer WHERE cust_id ="+ id);
			if(resultSet.next()){
			    customer = new Customer();
				customer.setId(resultSet.getLong("cust_id"));
				customer.setCustName(resultSet.getString("cust_name"));
				customer.setPassword(resultSet.getString("password"));
				
				CouponDBDAO couponDB = new CouponDBDAO();
				getCoupons = connection.createStatement();
				couponRs = getCoupons.executeQuery("SELECT * FROM app.customer_coupon WHERE "
						+ "cust_id = " + customer.getId());
				
				while(couponRs.next()){
					coupons.add(couponDB.getCoupon(couponRs.getLong(2)));
				}
				customer.setCoupons(coupons);
				return customer;
			}
			
			
		} catch (SQLException e) {
			System.out.println(e.getSQLState());
		}finally {
			if(connection != null){
				pool.returnConnection(connection);
			}
			
		}
		return null;
	}

	/**GET ALL CUSTOMERS 
	 * self-explanatory 
	 * @return HashSet<Customer>
	 */
	
	@Override
	public Collection<Customer> getAllCustomer() {
		
		Connection connection = null;
		Statement statement, getCoupons = null;
		ResultSet resultSet, couponRs = null;
		HashSet<Customer> allCustomers = new HashSet<Customer>();
		ArrayList<Coupon> customerCoupons = new ArrayList<Coupon>();
		CouponDBDAO couponDB = new CouponDBDAO();
		
		try {
			connection = pool.getConnection();
			statement = connection.createStatement();
			resultSet = statement.executeQuery("SELECT * FROM app.customer");
			
			while(resultSet.next()){
				Customer customer = new Customer();
				customer.setId(resultSet.getLong(1));
				customer.setCustName(resultSet.getString(2));
				customer.setPassword(resultSet.getString(3));
				
				getCoupons = connection.createStatement();
				couponRs = getCoupons.executeQuery("SELECT * FROM app.customer_coupon WHERE "
						+ "cust_id = " + customer.getId());

				while(couponRs.next()){
					customerCoupons.add(couponDB.getCoupon(couponRs.getLong(2)));
				}
				allCustomers.add(customer);
			}
			
		}catch(SQLException e){
			
			e.getSQLState();
			e.getMessage();
			
		}finally {
		
			if(connection != null){
				pool.returnConnection(connection);
			}
		}
		return allCustomers;
	}

	/**GET COUPONS 
	 * takes in a customer
	 * uses a HashSet because a customer can't
	 * buy a coupon more then once
	 * @return HashSet<Coupon>
	 */
	
	@Override
	public Collection<Coupon> getCoupons(Customer customer) {
		
		Connection connection = null;
		Statement getAllCustomerCoup, getCoupons = null;
		ResultSet resultSet, couponsRs = null;
		HashSet<Coupon> allCustomerCoupons = new HashSet<Coupon>();
		
		try {
			connection = pool.getConnection();
			getAllCustomerCoup = connection.createStatement();
			resultSet = getAllCustomerCoup.executeQuery("SELECT * FROM app.customer_coupon WHERE cust_id = " + customer.getId());
			while(resultSet.next()){
				getCoupons = connection.createStatement();
				couponsRs = getCoupons.executeQuery("SELECT * FROM app.coupon WHERE id =" + resultSet.getLong("coupon_id"));
				while(couponsRs.next()){

					Coupon coupon = new Coupon();
					coupon.setId(resultSet.getInt(1));
					coupon.setTitle(resultSet.getString(2));
					java.util.Date startDate = new java.util.Date(resultSet.getDate(3).getTime());
					coupon.setStartDate(startDate);
					java.util.Date endDate = new java.util.Date(resultSet.getDate(4).getTime());
					coupon.setEndDate(endDate);
					coupon.setAmount(resultSet.getInt(5));
					coupon.setType(CouponType.values()[resultSet.getInt(6)]);
					coupon.setMessage(resultSet.getString(7));
					coupon.setPrice(resultSet.getDouble(8));
					coupon.setImage(resultSet.getString(9));
					
					allCustomerCoupons.add(coupon);
				}
			}
			
		}catch(SQLException e){
			e.getMessage();
			e.getSQLState();
		} finally {
			if(connection != null){
				pool.returnConnection(connection);
			}
		}
		return allCustomerCoupons;
	}

	/**LOGIN CUSTOMER 
	 * same same but different
	 * @return customer
	 */
	@Override
	public Customer Login(String customerName, String password) {
			

		Connection connection = null;
		PreparedStatement checkCustomer = null;
		ResultSet checkCustomerRs = null;
		
		try {
			connection = pool.getConnection();
			
			checkCustomer = connection.prepareStatement("select * from app.customer where cust_name =? and password=?");
			checkCustomer.setString(1, customerName);
			checkCustomer.setString(2, password);
			checkCustomerRs = checkCustomer.executeQuery();
			
			if(checkCustomerRs.next()){
				
				return getCustomer(checkCustomerRs.getLong("comp_id"));
				
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
	


