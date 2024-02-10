package ks47team03.admin.mapper;

import java.util.List;
import java.util.Map;

import ks47team03.admin.dto.AdminPoint;
import org.apache.ibatis.annotations.Mapper;

import ks47team03.user.dto.Cup;
import ks47team03.user.dto.DepositStandard;


@Mapper
public interface AdminDepositMapper {

	// 보증금 기준
	public List<Map<String,Object>> getDepositStandardList(Map<String,Object>paramMap);
	public int getDepositStandardListCount();

	//보증금 결제
	public List<Map<String,Object>> getDepositPayList(Map<String,Object>paramMap);
	public int getDepositPayListCount();

	//컵 대여 보증금 기준 조회
	public int getDepositPrice();

	//보증금 관리
	public List<Map<String,Object>> getDepositManageList(Map<String,Object>paramMap);
	public int getDepositManageListCount();


	//보증금 환급
	public List<Map<String,Object>> getDepositRefundList(Map<String,Object>paramMap);
	public int getDepositRefundListCount();


	public DepositStandard getDepositStandardInfoById(String waitingDepositStandardCode);
	public int modifyDepositStandard(DepositStandard depositStandard);

	//보증금 기준 생성
	public void createDepositStandardById(DepositStandard depositStandard);
	
	//보증금 기준 삭제
	public void deleteDepositStandardById(DepositStandard depositStandard);

	//수정전 선택한 보증금 기준 
	public int modifyCheck(int waitingDepositPeriod);
	
	//중복확인(미완)
	public boolean depositStandardUseCheck(String depositStandardUse);


}
