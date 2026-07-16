package com.bwm.wallet.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Builder
@Table(name="wallet")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Wallet {
	@Id
	@Column(name = "user_id")
	private Integer userId;
	
	// TODO : 나중에 user 엔티티 만들어지면 연결
//	  @OneToOne(fetch = FetchType.LAZY) 
//    @MapsId wallet의 pk를 user의 pk와 연결
//    @JoinColumn(name = "user_id") 
//    private User user;
	
	@Column(name="balance", nullable = false)
	private Integer balance;
}
