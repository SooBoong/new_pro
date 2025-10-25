package ks47team03.user.dto;

import lombok.Data;

@Data
public class TossPayment {

	private String orderId;
    private String orderName;
    private String method; // <-- 오류가 발생했던 필드
    private String amount;
    private String customerName;
    private String virtualBank;
    private String virtualAccountNumber;
    
    private String userId;

}
