package system.DBDAO;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import system.DAO.CouponDAO;
import system.beans.Company;
import system.beans.Coupon;
import system.beans.CouponType;
import system.exceptions.CouponException;
import system.mainPackage.ConnectionPool;



public class CouponDBDAO implements CouponDAO{
	
	private ConnectionPool pool;
	public CouponDBDAO(){
	try {
		this.pool = ConnectionPool.getInstance();
	} catch (SQLException | ClassNotFoundException e) {
		System.out.println("No Connection to DB");
		e.printStackTrace();
		} 
	}
	
	/**CREATE COUPON 
	 * takes in a Coupon Object & and the Company Object 
	 * crates a new coupon registered under the company id 
	 * in COUPNAY_COUPON
	 * 
	 * @return Coupon
	 * 
	 */
	
	@Override
	public Coupon createCoupon(Coupon coupon, Company company) {
	
	Connection connection = null;
	Statement cheackTitle, addToComp_coup = null;
	ResultSet titleRs,id = null;
	PreparedStatement createCoup = null;
	
	
	try{
		connection = pool.getConnection();
		cheackTitle = connection.createStatement();
		titleRs = cheackTitle.executeQuery("SELECT * FROM coupon WHERE title ='"+coupon.getTitle() +"'");
		if (titleRs.next()){
			throw new CouponException("Title already exists");
		}
		createCoup = connection.prepareStatement("INSERT INTO app.coupon (title, start_date, end_date, amount, type, message, price, image) values (?,?,?,?,?,?,?,?)",
					Statement.RETURN_GENERATED_KEYS);
		
		createCoup.setString(1, coupon.getTitle());
		java.sql.Date startDate= new java.sql.Date(new java.util.Date().getTime());
		createCoup.setDate(2, startDate);
		java.sql.Date endDate = new java.sql.Date(coupon.getEndDate().getTime());
		createCoup.setDate(3, endDate);
		createCoup.setInt(4, coupon.getAmount());
		createCoup.setInt(5, coupon.getType().ordinal());
		createCoup.setString(6, coupon.getMessage());
		createCoup.setDouble(7, coupon.getPrice());
		createCoup.setString(8, coupon.getImage());
		createCoup.execute();
		System.out.println("Coupon Added");
		
		id = createCoup.getGeneratedKeys();
		if(id.next()){
			coupon.setId(id.getLong(1));
			System.out.println(" ID = "+coupon.getId()+"\n Title :" + coupon.getTitle() );
		}
		addToComp_coup = connection.createStatement();
		addToComp_coup.executeUpdate("INSERT INTO app.company_coupon(comp_id, coupon_id) values(" + company.getId() + "," + coupon.getId() + ")");
			System.out.println("Coupon_added to comp_coup as well");
		
		
	}catch(SQLException e){
		System.out.println(e.getSQLState());
		System.out.println(e.getMessage());
	}finally{
		if(connection != null){
			pool.returnConnection(connection);
		}
	}
	return coupon;
	
		
	}
	
	/**REMOVE COUPON
	 * takes in a coupon
	 * checks to see if coupon exists
	 * if it does deleting it from all tables
	 * (including joined ones)
	 *
	 * @void 
	 */
	
	@Override
	public void removeCoupon(Coupon coupon) {
		
		Connection connection = null;
		Statement checkCoupon, removeCoupon = null;
		ResultSet couponRs = null;
		try{
		connection = pool.getConnection();
		checkCoupon = connection.createStatement();
		couponRs = checkCoupon.executeQuery("SELECT * FROM app.coupon WHERE id =" + coupon.getId());
		if(couponRs.next()){
			throw new CouponException("Coupon Does not Exsits");
		}
		removeCoupon = connection.createStatement();
		removeCoupon.executeUpdate("DELETE FROM app.company_coupon WHERE coupon_id = " + coupon.getId());
		removeCoupon.executeUpdate("DELETE FROM app.coupon WHERE title ='" + coupon.getTitle() + "'");
		/* check if working */removeCoupon.executeUpdate("DELETE FROM app.customer_coupon WHERE coupon_id = " + coupon.getId());
		System.out.println("Coupon Removed");
			
		}catch(SQLException e){
			e.printStackTrace();
			e.getMessage();
			
		}finally{
			if(connection != null){
				pool.returnConnection(connection);
			}
		}
		
		
	}
	
	/**UPDATE COUPON
	 * takes in a coupon 
	 * checks if coupon exists
	 * if it does updating end_date, price and amount by id
	 * @void  
	 */
	
	
	@Override
	public void updateCoupon(Coupon coupon) {
		
		Connection connection = null;
		Statement checkCoup = null; 
		ResultSet checkCoupRs = null;
		PreparedStatement updateCoup = null;
		try {
		connection = pool.getConnection();
		checkCoup = connection.createStatement();
		checkCoupRs = checkCoup.executeQuery("SELECT * FROM app.coupon WHERE id =" + coupon.getId());
		if(!checkCoupRs.next()){
			throw new CouponException("No such coupon Exsits");
		}
		updateCoup = connection.prepareStatement("UPDATE app.coupon SET end_date=?, price=?, amount=? WHERE id =" + coupon.getId());
		Date endDate = new Date(coupon.getEndDate().getTime());
		updateCoup.setDate(1, endDate);
		updateCoup.setDouble(2, coupon.getPrice());
		updateCoup.setInt(3, coupon.getAmount());
		updateCoup.executeUpdate();
		System.out.println("Update Successful"); 
			
		} catch (SQLException e) {
			e.printStackTrace();
			e.getMessage();
			
		}finally{
				if(connection != null){
				pool.returnConnection(connection);
			}
		}
		
		
	}
	
	/**GET COUPON
	 * take in long ID
	 * @return Coupon 
	 * 
	 */
	
	@Override
	public Coupon getCoupon(long id) {
		
		Connection connection = null;
		Statement statement = null;
		ResultSet resultSet = null;
		
		try {
			connection = pool.getConnection();
			statement = connection.createStatement();
			resultSet = statement.executeQuery("SELECT * FROM app.coupon WHERE id =" + id);
			if (resultSet.next()) {
				Coupon coupon = new Coupon();
				coupon.setId(id);
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
				
				return coupon;
			}else{
				throw new CouponException("coupon does no exists in the system");
			}
			
		} catch (SQLException e) {
			e.getSQLState();
			e.getMessage();
			
		}finally{
			if(connection != null){
				pool.returnConnection(connection);
			}
		}
		return null;
	}
	
	
	/**GET ALL COUPON
	 * returns an ArrayList of all the system coupons
	 * @return ArrayList<Coupon>
	 */
	
	@Override
	public Collection<Coupon> getAllCoupon() {
		
		Connection connection = null;
		Statement statement = null;
		ResultSet resultSet = null;
		ArrayList<Coupon> allcoupons = new ArrayList<Coupon>();
		
		try {
	
			connection = pool.getConnection();
			statement = connection.createStatement();
			resultSet = statement.executeQuery("SELECT * FROM app.coupon");
		
			while(resultSet.next()){
				
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
				
				allcoupons.add(coupon);
			}
			return allcoupons;
			
		} catch (SQLException e) {
			e.printStackTrace();
			e.getMessage();
			
		}finally{
			
			if(connection != null){
				pool.returnConnection(connection);
			}
		}
		return null;
	}
	
	/**GET ALL COUPON BY TYPE
	 * takes in a couponType
	 * returns an ArrayList with all the coupons from that type 
	 * @return ArrayList<Coupon>
	 */
	
	@Override
	public Collection<Coupon> getCouponByType(CouponType type) {
		
		Connection connection = null;
		Statement statement, exist = null;
		ResultSet resultSet, existRs = null;
		ArrayList<Coupon> allCouponsByType = new ArrayList<Coupon>();
		
		
		try {
	
			connection = pool.getConnection();
			exist = connection.createStatement();
			existRs = exist.executeQuery("SELECT * FROM app.coupon WHERE type = '" + type.ordinal()+"'");
			if(!existRs.next()){
				
				throw new CouponException("No coupons of that type Exists in DB");
			}
			statement = connection.createStatement();
			resultSet = statement.executeQuery("SELECT * FROM app.coupon WHERE type = '" + type.ordinal()+"'");
			
			while(resultSet.next()){
				
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
				
				allCouponsByType.add(coupon);
			}
			return allCouponsByType;
			
		} catch (SQLException e) {
			e.printStackTrace();
			e.getMessage();
			e.getErrorCode();
			
		}finally{
			
			if(connection != null){
				pool.returnConnection(connection);
			}
		}
		return null;
	}
	
	//not needed
	
	@Override
	public void update_Coupon_price_id(Coupon coupon) {
		Connection connection = null;
		Statement existsCheck = null; 
		ResultSet existsAnswer = null;
		PreparedStatement update = null;
		
		try{
			connection = pool.getConnection();
			existsCheck = connection.createStatement();
			existsAnswer = existsCheck.executeQuery("SELECT * FROM app.coupon WHERE id = " + coupon.getId());
			if(!existsAnswer.next()){
				throw new CouponException("Coupon does not exists in DB");
			}
			update = connection.prepareStatement("update app.coupon set end_date=?, price=?, amount=? where id =" + coupon.getId());
			Date endDate = new Date(coupon.getEndDate().getTime());
			update.setDate(1, endDate);
			update.setDouble(2, coupon.getPrice());
			update.setInt(3, coupon.getAmount());
			System.out.println("Update Successful");
			
		}catch(SQLException e){
			e.getSQLState();
			e.getMessage();
		}finally {
			
			if(connection != null){
				pool.returnConnection(connection);
			}
			
		}
		
	}
	
	
	/**REMOVE THE COUPON FROM THE COUTOMER JOINT TABLE
	 * take in a coupon
	 * @void
	 */
	
	@Override
	public void removeCoupon_from_Customer_Coupon(Coupon coupon) {
		
		Connection connection = null;
		Statement existCheck, remover  = null;
		ResultSet resultSet = null;
		
		try {
			connection = pool.getConnection();
			existCheck = connection.createStatement();
			resultSet = existCheck.executeQuery("SELECT * FROM app.customer_coupon WHERE id = " + coupon.getId());
			if(!resultSet.next()){
				throw new CouponException("Coupon Does not exist");
			}
			remover = connection.createStatement();
			remover.executeUpdate("DELETE FROM app.customer_coupon WHERE id =" + coupon.getId());
		} catch (SQLException e) {
			e.getSQLState();
			e.getMessage();
		}finally {
			if(connection != null){
				pool.returnConnection(connection);
			}
		}
		
	}		

}
