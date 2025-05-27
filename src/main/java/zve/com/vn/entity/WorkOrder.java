package zve.com.vn.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Entity
@Getter
@Setter
@Table(name = "tbl_workorder") 
@FieldDefaults(level = AccessLevel.PRIVATE)
public class WorkOrder {
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	String id;
	
	@Column(unique = true)
	String woNumber;
	String line;
	String model;
	Integer fgQty;
	Integer status;
	String userId;
	
	@Temporal(TemporalType.TIMESTAMP)
	Date createdDate;
	
	@OneToMany(mappedBy = "workOrder", cascade = CascadeType.ALL, orphanRemoval = true)
	List<Order> orders = new ArrayList<>();

	
	@PrePersist
    protected void onCreate() {
        createdDate = new Date(); 
        if (status == null) {
            status = 1; 
        }
    }
}
