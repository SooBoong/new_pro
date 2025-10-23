package ks47team03.admin.mapper;

import java.util.List;
import java.util.Map;

import ks47team03.admin.dto.AdminPoint;
import ks47team03.user.dto.Deposit;
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
	public int modifyCheck(int waitingDepositPeriod);
	// 기간 중복 검사
	public int checkPeriodDuplicateExcludeSelf(Map<String, Object> params);
	//보증금 y 사용유무 
	public int checkUseDuplicateExcludeSelf(Map<String, Object> params);
	public void createDepositStandard(DepositStandard depositStandard);
	
	//보증금 기준 삭제
	public void deleteDepositStandardById(DepositStandard depositStandard);
	public Map<String, Object> findAdminUserById(String adminId);
	
	//수정전 선택한 보증금 기준 
	
	
	//중복확인(미완)
	public boolean depositStandardUseCheck(String waitingDepositStandardCode);

	public int getDepositPayListCount(String depositSearch, String depositSearchText);

	// 기간 생성 중복 조회
	public int isPeriodDuplicated(int waitingDepositPeriod);
	public void createDepositStandardById(DepositStandard depositStandard);

	;
}
