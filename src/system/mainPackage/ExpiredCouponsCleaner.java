package system.mainPackage;

import java.util.Collection;
import java.util.Date;

import system.DBDAO.CouponDBDAO;
import system.beans.Coupon;

public class ExpiredCouponsCleaner extends Thread{

	private boolean isRunning = true;
	CouponDBDAO couponDBDAO;
	
	
	protected ExpiredCouponsCleaner() {
		couponDBDAO = new CouponDBDAO();
	}


	@Override
	public void run() {
		System.out.println("Daily Expired Coupons Cleaner Initialized");
		Collection<Coupon> allSystemCoupons = couponDBDAO.getAllCoupon(); 
		while(isRunning == true && !allSystemCoupons.isEmpty()){
			for (Coupon coupon : allSystemCoupons) {
				
				if (coupon.getEndDate().getTime() < new Date().getTime()){
					couponDBDAO.removeCoupon(coupon);
				}
					
		}try{
			
			Thread.sleep(86400000);
		}catch(InterruptedException e){
			e.printStackTrace();
		}
		System.out.println("Daily Expired Coupons Cleaner Done");
		
		}

	}
	public void stopTask(){
		isRunning = false;
		Thread.currentThread().interrupt();
	}
}
