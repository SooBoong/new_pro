package ks47team03.admin.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AdminDepositMapper {

	// 보증금 기준
	public List<Map<String,Object>> getDepositStandardList(Map<String,Object>paramMap);
	public int getDepositStandardListCount();

	//보증금 결제
	public List<Map<String,Object>> getDepositPayList(Map<String,Object>paramMap);
	public int getDepositPayListCount();

	//보증금 관리
	public List<Map<String,Object>> getDepositManageList(Map<String,Object>paramMap);
	public int getDepositManageListCount();
	
	//보증금 환급
	public List<Map<String,Object>> getDepositRefundList(Map<String,Object>paramMap);
	public int getDepositRefundListCount();


}