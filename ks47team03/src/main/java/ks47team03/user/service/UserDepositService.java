package ks47team03.user.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*import ks47team03.user.dto.TossPayment;*/
import org.json.simple.JSONObject;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import ks47team03.user.dto.Account;
import ks47team03.user.dto.Deposit;
import ks47team03.user.dto.TossPayment;
import ks47team03.admin.mapper.AdminCommonMapper;
import ks47team03.user.mapper.UserDepositMapper;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
public class UserDepositService {


private final UserDepositMapper userDepositMapper;


public UserDepositService(UserDepositMapper userDepositMapper) {
	this.userDepositMapper = userDepositMapper;
}

public Map<String,Object> getUserDepositManageList(String userId, int currentPage) {

	int rowPerPage = 16;
	
	//페이지 계산(시작될 행의 인덱스)
	int startIndex = (currentPage-1)*rowPerPage;
	
	double rowsCount = userDepositMapper.getUserDepositManageListCount(userId);
	
	int lastPage = (int) Math.ceil(rowsCount/rowPerPage);
	//Math.ceil 올림 처리
	// 처음 번호
    int startPageNum = 1;

    // 마지막 번호
    int endPageNum = (lastPage < 10)? lastPage : 10;

    if(currentPage >= 7 && lastPage > 10) {
    	startPageNum = currentPage - 5;
        endPageNum = currentPage + 4;
        if(endPageNum >= lastPage) {
        	startPageNum = lastPage - 9;
        	endPageNum = lastPage;
        }
    }
    
	Map<String,Object> paramMap = new HashMap<String,Object>();
	paramMap.put("startIndex", startIndex);
	paramMap.put("rowPerPage", rowPerPage);
	paramMap.put("userId", userId);
	
	log.info("paramMap:{}",paramMap);
	

	List<Map<String,Object>> userDepositManageList = userDepositMapper.getUserDepositManageList(paramMap);
	log.info("특정 회원 보증금 목록:{}",userDepositManageList);
	
	
	//controller에 전달
	paramMap.clear(); // map 객체 안의 data초기화
	paramMap.put("lastPage", lastPage);
	paramMap.put("userDepositManageList", userDepositManageList);
	paramMap.put("startPageNum", startPageNum);
	paramMap.put("endPageNum", endPageNum);
	
	return paramMap;
}

public Map<String,Object>getUserDepositPayList(String userId, int currentPage) {
	
	int rowPerPage = 16;
	
	//페이지 계산(시작될 행의 인덱스)
	int startIndex = (currentPage-1)*rowPerPage;
	double rowsCount = userDepositMapper.getUserDepositPayListCount(userId);
	int lastPage = (int) Math.ceil(rowsCount/rowPerPage);
	//Math.ceil 올림 처리
	// 처음 번호
    int startPageNum = 1;

    // 마지막 번호
    int endPageNum = (lastPage < 10)? lastPage : 10;
    if(currentPage >= 7 && lastPage > 10) {
    	startPageNum = currentPage - 5;
        endPageNum = currentPage + 4;
        if(endPageNum >= lastPage) {
        	startPageNum = lastPage - 9;
        	endPageNum = lastPage;
        }
    }
    
	Map<String,Object> paramMap = new HashMap<String,Object>();
	paramMap.put("startIndex", startIndex);
	paramMap.put("rowPerPage", rowPerPage);	
	paramMap.put("userId", userId);
	log.info("paramMap:{}",paramMap);
	
	
	List<Map<String,Object>> userDepositPayList = userDepositMapper.getUserDepositPayList(paramMap);
	log.info("특정 회원 보증금 결제 목록:{}",userDepositPayList);
	//controller에 전달
	paramMap.clear(); // map 객체 안의 data초기화
	paramMap.put("lastPage", lastPage);
	paramMap.put("startPageNum", startPageNum);
	paramMap.put("endPageNum", endPageNum);
	paramMap.put("userDepositPayList", userDepositPayList);
	
	System.out.println("startPageNum: "+startPageNum);
	System.out.println("endPageNum: "+endPageNum);
	return paramMap;
}

public Deposit getUserDeposit(String userId) {
	Deposit userDeposit = userDepositMapper.getUserDeposit(userId);
	
	return userDeposit;
	}

public Account getUserAccount(String userId) {
	Account userAccount = userDepositMapper.getUserAccount(userId);
	
	return userAccount;
}
public Deposit getUserPoint(String userId) {
	Deposit userDeposit = userDepositMapper.getUserDeposit(userId);
	
	return userDeposit;
}

public void createDepositPay(Deposit depositPayHistory) {
	userDepositMapper.createDepositPayById(depositPayHistory);
	
}

public String getIncreaseCode(String tableDbName) {
	return userDepositMapper.getIncreaseCode(tableDbName);
}

public List<Map<String, Object>> getUserDepositPaySuccessList(String userId) {
	 List<Map<String,Object>> userDepositPaySuccessList = userDepositMapper.getUserDepositPaySuccessList(userId);
	return userDepositPaySuccessList;
}


    public void createDepositRefund(Deposit depositRefundHistory) {
	userDepositMapper.createDepositRefundById(depositRefundHistory);
}

	public void payByTossPayments1(TossPayment tossPayment) {
		// TODO Auto-generated method stub
		
	}

	public void payByTossPayments(TossPayment tossPayment) {
		userDepositMapper.payByTossPaymentsById(tossPayment);
		
	}	

	
	public void updateDepositStatus(String orderId) {
		log.info("서비스: 토스 웹훅 입금 확인 요청 - orderId: {}", orderId);
		
		// Mapper 호출하여 결제 상태 '결제완료'로 변경
		// 주의: UserDepositMapper에 updatePaymentStatusToDone 메서드가 있어야 합니다.
		int result = userDepositMapper.updatePaymentStatusToDone(orderId);
		
		if(result > 0) {
			log.info("서비스: DB 상태 업데이트 성공");
		} else {
			log.warn("서비스: DB 업데이트 실패 (해당 orderId 없음)");
		}
	}


}
