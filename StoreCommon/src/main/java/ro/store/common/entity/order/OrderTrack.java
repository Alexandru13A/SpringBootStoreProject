package ro.store.common.entity.order;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "order_track")
@Getter
@Setter
public class OrderTrack   {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    
	@Column(length = 256)
	private String notes;
	
	private Date updatedTime;
	
	@Enumerated(EnumType.STRING)
	@Column(length = 45, nullable = false)
	private OrderStatus status;
	
	@ManyToOne
	@JoinColumn(name = "order_id")
	private Order order;
  

    @Transient
	public String getUpdatedTimeOnForm() {
		DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss");
		return dateFormatter.format(this.updatedTime);
	}
	
	public void setUpdatedTimeOnForm(String dateString) {
		DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss");
		
		try {
			this.updatedTime = dateFormatter.parse(dateString);
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
    
}
