package ks47team03.admin.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ks47team03.user.dto.Deposit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;


import ks47team03.admin.mapper.AdminDepositMapper;
import ks47team03.user.dto.DepositStandard;

@Service
public class AdminDepositService {

	private static final Logger log = LoggerFactory.getLogger(AdminCommonService.class);

	private final AdminDepositMapper adminDepositMapper;

	public AdminDepositService(AdminDepositMapper adminDepositMapper) {
		this.adminDepositMapper = adminDepositMapper;
	}

	//회원 보증금 환급 부분
	public Map<String,Object> getDepositRefundList(int currentPage) {
		int rowPerPage = 16;
		//페이지 계산(시작될 행의 인덱스)
		int startIndex = (currentPage-1)*rowPerPage;
		double rowsCount = adminDepositMapper.getDepositRefundListCount();
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
		log.info("paramMap:{}",paramMap);


		List<Map<String,Object>> depositRefundList = adminDepositMapper.getDepositRefundList(paramMap);
		log.info("전체 회원 보증금 목록:{}",depositRefundList);

		//controller에 전달
		paramMap.clear(); // map 객체 안의 data초기화
		paramMap.put("lastPage", lastPage);
		paramMap.put("depositRefundList", depositRefundList);
		paramMap.put("startPageNum", startPageNum);
		paramMap.put("endPageNum", endPageNum);

		return paramMap;
	}

	//회원 보증금 관리 부분

	public Map<String,Object> getDepositManageList(int currentPage) {
		int rowPerPage = 16;

		//페이지 계산(시작될 행의 인덱스)
		int startIndex = (currentPage-1)*rowPerPage;

		double rowsCount = adminDepositMapper.getDepositManageListCount();

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
		log.info("paramMap:{}",paramMap);


		List<Map<String,Object>> depositManageList = adminDepositMapper.getDepositManageList(paramMap);
		log.info("전회 회원 보증금 목록:{}",depositManageList);

		//controller에 전달
		paramMap.clear(); // map 객체 안의 data초기화
		paramMap.put("lastPage", lastPage);
		paramMap.put("depositManageList", depositManageList);
		paramMap.put("startPageNum", startPageNum);
		paramMap.put("endPageNum", endPageNum);

		return paramMap;
	}


	//보증금 결제 관리
	public Map<String,Object> getDepositPayList(int currentPage, String depositSearch, String depositSearchText ) {
		int rowPerPage = 16;

		//페이지 계산(시작될 행의 인덱스)
		int startIndex = (currentPage-1)*rowPerPage;

		double rowsCount = adminDepositMapper.getDepositPayListCount(depositSearch,depositSearchText);

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
		paramMap.put("depositSearch", depositSearch);
		paramMap.put("depositSearchText", depositSearchText);
		log.info("paramMap:{}",paramMap);


		List<Map<String,Object>> depositPayList = adminDepositMapper.getDepositPayList(paramMap);
		log.info("전체 회원 목록:{}",depositPayList);

		paramMap.clear();
		paramMap.put("lastPage", lastPage);
		paramMap.put("depositPayList", depositPayList);
		paramMap.put("startPageNum", startPageNum);
		paramMap.put("endPageNum", endPageNum);

		return paramMap;
	}

	// 컵 대여 보증금 기준 관리
	public int getDepositPrice(){
		int depositPrice = adminDepositMapper.getDepositPrice();

		return depositPrice;
	}


	// 보증금 기준 관리
	public Map<String,Object> getDepositStandardList(int currentPage) {
		//보여질 행의 갯수
		int rowPerPage = 16;

		//페이지 계산(시작될 행의 인덱스)
		int startIndex = (currentPage-1)*rowPerPage;

		//마지막 페이지 계산
		//1. 보여질 테이블의 전체 행의 갯수
		double rowsCount = adminDepositMapper.getDepositStandardListCount();
		//int보다 더 넓은 자료형을 담아 줄 수 있는게 double 타입 int넣으면 소숫점 절삭
		// ex) 102/5 =20.4 int로 담을경우 소숫점 절삭되서 20으로 됨
		//2. 마지막 페이지
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
		log.info("paramMap:{}",paramMap);
		List<Map<String,Object>> depositStandardList = adminDepositMapper.getDepositStandardList(paramMap);
		log.info("전체 회원 목록:{}",depositStandardList);
		//controller에 전달
		paramMap.clear(); // map 객체 안의 data초기화
		paramMap.put("lastPage", lastPage);
		paramMap.put("depositStandardList", depositStandardList);
		paramMap.put("startPageNum", startPageNum);
		paramMap.put("endPageNum", endPageNum);

		return paramMap;
	}

		
	
	public DepositStandard getDepositStandardInfoById(String waitingDepositStandardCode) {
		DepositStandard depositStandardInfo = adminDepositMapper.getDepositStandardInfoById(waitingDepositStandardCode);
		return depositStandardInfo;
	}

public String modifyDepositStandard(DepositStandard depositStandard) {
          
        Map<String, Object> periodParams = new HashMap<>();
        periodParams.put("waitingDepositPeriod", depositStandard.getWaitingDepositPeriod());
        periodParams.put("waitingDepositStandardCode", depositStandard.getWaitingDepositStandardCode());
        
        int periodCount = adminDepositMapper.checkPeriodDuplicateExcludeSelf(periodParams);
        
        if (periodCount > 0) {
            // 다른 기준이 이미 이 '일수'를 사용 중
            return "ERR_PERIOD_DUPLICATE";
        }

        // --- 2. 'Y' 사용 유무 중복 검사 (자신 제외) ---
        // 사용자가 이 기준을 'Y'(사용)로 설정하려고 할 때만 검사
        if ("Y".equalsIgnoreCase(depositStandard.getDepositStandardUse())) {
            
            Map<String, Object> useParams = new HashMap<>();
            useParams.put("waitingDepositStandardCode", depositStandard.getWaitingDepositStandardCode());
            
            // '나'를 제외하고 'Y'인 것이 있는지 확인
            int useCount = adminDepositMapper.checkUseDuplicateExcludeSelf(useParams);
            
            if (useCount > 0) {
                // 다른 기준이 이미 'Y'로 사용 중
                return "ERR_USE_DUPLICATE";
            }
        }
        
        // --- 3. 모든 검증 통과: DB 업데이트 실행 ---
        adminDepositMapper.modifyDepositStandard(depositStandard);
        
        return "SUCCESS";
    }
	


	public String deleteDepositStandard(DepositStandard depositStandard) {
	    
	    String formAdminId = depositStandard.getAdminId();
	    String formAdminPw = depositStandard.getAdminPw();
	
	    Map<String, Object> adminUser = adminDepositMapper.findAdminUserById(formAdminId);

	    if (adminUser == null || adminUser.isEmpty()) {
	        return "ERR_ID_NOT_FOUND"; // 아이디 없음
	    }

	    String dbAdminPw = (String) adminUser.get("adminPw");
	    	   
	    if (!formAdminPw.equals(dbAdminPw)) {
	        return "ERR_PW_MISMATCH"; // 비밀번호 불일치
	    }
	  	    	  
	    adminDepositMapper.deleteDepositStandardById(depositStandard);
	    
	    return "SUCCESS";
	}

	
	
	public void createDepositStandard(DepositStandard depositStandard) {
		adminDepositMapper.createDepositStandardById(depositStandard);

	}

	public Map<String, Object> isValidDepositStandard(String waitingDepositStandardCode, String adminId) {
		Map<String, Object> resultMap = new HashMap<String, Object>();

		boolean isValid = false;


		DepositStandard depositStandard = adminDepositMapper.getDepositStandardInfoById(waitingDepositStandardCode);
		if(depositStandard != null) {
			String checkAdminId = depositStandard.getWaitingDepositStandardCode();
			if(checkAdminId.equals(adminId)) {
				isValid = true;
				resultMap.put("DepositStandardInfo", depositStandard);
			}
		}
		resultMap.put("isValid", isValid);

		return resultMap;
	}

	public int modifyCheck(int waitingDepositPeriod) {
		int result =adminDepositMapper.modifyCheck(waitingDepositPeriod);
		return result;
	}
	
	public boolean depositStandardUseCheck(String waitingDepositStandardCode) {
		boolean result =adminDepositMapper.depositStandardUseCheck(waitingDepositStandardCode);
		log.info("서비스:" +result);
		return result;
		
	}

		public boolean isPeriodDuplicated(int waitingDepositPeriod) {			
			int count = adminDepositMapper.isPeriodDuplicated(waitingDepositPeriod);
			// count가 0보다 크면 중복된 데이터가 있다는 의미이므로 true를 반환합니다.
			return count > 0;
		}
	    // ====================================================
	


}