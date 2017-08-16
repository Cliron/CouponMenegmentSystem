package system.DAO;

import java.util.Collection;

import system.beans.Company;
import system.beans.Coupon;
import system.beans.CouponType;

public interface CouponDAO {
	
	 public Coupon createCoupon(Coupon coupon,Company company); 
	 public void removeCoupon(Coupon coupon);
	 public void updateCoupon(Coupon coupon);
	 public Coupon getCoupon(long id);
	 public Collection<Coupon> getAllCoupon();
	 public Collection<Coupon> getCouponByType(CouponType type);
	 public void update_Coupon_price_id(Coupon coupon);
	 public void removeCoupon_from_Customer_Coupon(Coupon coupon);

}
