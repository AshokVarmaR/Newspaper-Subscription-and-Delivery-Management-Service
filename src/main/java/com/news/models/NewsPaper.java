package com.news.models;

import java.time.LocalDateTime;
import java.util.Base64;

import com.news.enums.Language;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="newspaper")
public class NewsPaper {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private long id;
	private LocalDateTime deliveredTime;
	private String name;
	
	@Enumerated(EnumType.STRING)
	private Language language;
	
	@Embedded
	private SubscriptionPrice subscriptionPrice;

	
	@Lob
	private byte[] photo;
	
	
	private String b64Photo;
	
	private String publisher;
	
	private String description;
	
	private boolean isActive;
	
	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name="employee_id")
	private Employee employee;
	
	public String getB64Photo() {
		if (photo != null)
			return "data:image/jpeg;base64," + Base64.getEncoder().encodeToString(this.photo);
		else return null;
	}
	
}
