package ks47team03.user.dto;

import lombok.Data;

@Data
public class TossPayment {
    // 기존 필드들...
    private String userId;
    private String orderId;
    private String orderName;
    private String amount;
    private String method;
    private String virtualAccountNumber;
    private String customerName;
    private String virtualBank;   
    private String paymentKey; 
}