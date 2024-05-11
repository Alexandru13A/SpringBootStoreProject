package ro.store.checkout;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class CheckoutInfo {

 private float productCost;
	private float productTotal;
	private float shippingCostTotal;
	private float paymentTotalForOrder;
	private int deliverDays;
	private boolean codSupported;




  public Date getDeliverDate(){
    Calendar calendar = Calendar.getInstance();
    calendar.add(Calendar.DATE, deliverDays);
    return calendar.getTime();
  }

  public String getPaymentTotalForPayPal(){
    DecimalFormat formatter  = new DecimalFormat("##.##");
    return formatter.format(paymentTotalForOrder);
  }
  


}
