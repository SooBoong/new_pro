package ks47team03.user.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import ks47team03.user.dto.Account;
import ks47team03.user.dto.Deposit;
import ks47team03.user.dto.Point;

@Mapper
public interface UserDepositMapper {

	public List<Map<String,Object>> getUserDepositManageList(Map<String,Object>paramMap);
	public int getUserDepositManageListCount();
	
	public List<Map<String,Object>> getUserDepositPayList(Map<String,Object>paramMap);
	public int getUserDepositPayListCount();
	
	public void modifyUserAccount(Account account);
	public void addUserAccount(Account account);
	
	public Deposit getUserDeposit(String userId);
	public Account getUserAccount(String userId);
	public void createDepositPayById(Deposit depositPayHistory);
	public String getIncreaseCode(String tableDbName);
	
	
	
	
	
	
	
}
