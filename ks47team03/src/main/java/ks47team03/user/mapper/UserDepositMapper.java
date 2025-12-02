package ks47team03.user.mapper;

import java.util.List;
import java.util.Map;

import ks47team03.user.dto.TossPayment;
import org.apache.ibatis.annotations.Mapper;

import ks47team03.user.dto.Account;
import ks47team03.user.dto.Deposit;
import ks47team03.user.dto.Point;
import org.json.simple.JSONObject;

@Mapper
public interface UserDepositMapper {

	public List<Map<String,Object>> getUserDepositManageList(Map<String,Object>paramMap);
	public int getUserDepositManageListCount(String userId);
	
	public List<Map<String,Object>> getUserDepositPayList(Map<String,Object>paramMap);
	public int getUserDepositPayListCount(String userId);
	
	public void modifyUserAccount(Account account);
	public void addUserAccount(Account account);
	
	public Deposit getUserDeposit(String userId);
	public Account getUserAccount(String userId);
	public void createDepositPayById(Deposit depositPayHistory);
	public String getIncreaseCode(String tableDbName);
	
	public List<Map<String, Object>> getUserDepositPaySuccessList(String userId);


    public void createDepositRefundById(Deposit depositRefundHistory);


	public void payByTossPaymentsById(TossPayment tossPayment);
	
	public int updatePaymentStatusToDone(String orderId);
}
