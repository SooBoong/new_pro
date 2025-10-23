package ks47team03.admin.controller;

import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import ks47team03.admin.mapper.AdminDepositMapper;
import ks47team03.admin.service.AdminDepositService;
import ks47team03.user.dto.DepositStandard;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/admin/deposit")
public class AdminDepositController {

	// 의존성 주입
	private final AdminDepositService depositService;
	private final AdminDepositMapper depositMapper;
	public AdminDepositController(AdminDepositService depositService, AdminDepositMapper depositMapper) {
		this.depositService = depositService;
		this.depositMapper = depositMapper;
	}

	//보증금 내역 관리
	@SuppressWarnings("unchecked")
	@GetMapping("/depositManage")
	public String depositManage(@RequestParam(value="currentPage",
			required = false ,defaultValue = "1")int currentPage,
								Model model) {
		Map<String,Object> resultMap = depositService.getDepositManageList(currentPage);
		int lastPage = (int)resultMap.get("lastPage");
		List<Map<String,Object>> depositManageList = (List<Map<String,Object>>)resultMap.get("depositManageList");
		log.info("depositManageList:{}",depositManageList);
		model.addAttribute("title","회원 보증금 관리");
		
		int startPageNum = (int) resultMap.get("startPageNum");
		int endPageNum = (int) resultMap.get("endPageNum");
		model.addAttribute("title","회원 보증금 관리");
		model.addAttribute("currentPage", currentPage);
		model.addAttribute("lastPage", lastPage);
		model.addAttribute("depositManageList", depositManageList);
		model.addAttribute("startPageNum", startPageNum);
		model.addAttribute("endPageNum", endPageNum);

		return "admin/deposit/depositManage";
	}



	//보증금 결제 관리
	@SuppressWarnings("unchecked")
	@GetMapping("/depositPayManage")
	public String depositPayManage(@RequestParam(value="currentPage", required = false ,defaultValue = "1")int currentPage,
								   String depositSearch,
								   String depositSearchText,
								   Model model) {
		Map<String,Object> resultMap = depositService.getDepositPayList(currentPage, depositSearch, depositSearchText);
		int lastPage = (int)resultMap.get("lastPage");

		List<Map<String,Object>> depositPayList = (List<Map<String,Object>>)resultMap.get("depositPayList");
		log.info("depositPayList:{}",depositPayList);

		int startPageNum = (int) resultMap.get("startPageNum");
		int endPageNum = (int) resultMap.get("endPageNum");



		model.addAttribute("title","보증금 결제 관리");
		model.addAttribute("depositSearch",depositSearch);
		model.addAttribute("depositSearchText",depositSearchText);

		model.addAttribute("currentPage", currentPage);
		model.addAttribute("lastPage", lastPage);
		model.addAttribute("depositPayList", depositPayList);
		model.addAttribute("startPageNum", startPageNum);
		model.addAttribute("endPageNum", endPageNum);


		return "admin/deposit/depositPayManage";
	}




	//보증금 환급 관리
	@SuppressWarnings("unchecked")
	@GetMapping("/depositRefundManage")
	public String depositRefundManage(@RequestParam(value="currentPage", required = false ,defaultValue = "1")int currentPage,
									  Model model) {
		Map<String,Object> resultMap = depositService.getDepositRefundList(currentPage);
		int lastPage = (int)resultMap.get("lastPage");
		List<Map<String,Object>> depositRefundList = (List<Map<String,Object>>)resultMap.get("depositRefundList");
		log.info("depositRefundList:{}",depositRefundList);
		model.addAttribute("title","보증금 환급 관리");
		int startPageNum = (int) resultMap.get("startPageNum");
		int endPageNum = (int) resultMap.get("endPageNum");

		model.addAttribute("title","보증금 환급 관리");
		model.addAttribute("currentPage", currentPage);
		model.addAttribute("lastPage", lastPage);
		model.addAttribute("depositRefundList", depositRefundList);
		model.addAttribute("startPageNum", startPageNum);
		model.addAttribute("endPageNum", endPageNum);

		return "admin/deposit/depositRefundManage";
	}



	// 보증금 기준 관리 조회
	@SuppressWarnings("unchecked")
	@GetMapping("/depositStandard")
	public String depositStandard(@RequestParam(value="currentPage", required = false ,defaultValue = "1")
								  int currentPage, Model model) {
		Map<String,Object> resultMap = depositService.getDepositStandardList(currentPage);
		int lastPage = (int)resultMap.get("lastPage");
		int startPageNum = (int) resultMap.get("startPageNum");
		int endPageNum = (int) resultMap.get("endPageNum");

		List<Map<String,Object>> depositStandardList = (List<Map<String,Object>>)resultMap.get("depositStandardList");
		log.info("depositStandardList:{}",depositStandardList);

		model.addAttribute("title","보증금 기준 관리");
		model.addAttribute("currentPage", currentPage);
		model.addAttribute("lastPage", lastPage);
		model.addAttribute("startPageNum", startPageNum);
		model.addAttribute("endPageNum", endPageNum);
		model.addAttribute("depositStandardList", depositStandardList);

		return "admin/deposit/depositStandard";
	}
	
	
	
	//보증금 기준 관리 생성
	@GetMapping("/createDepositStandard")
	public String createDepositStandardGet(@RequestParam(value="msg", required = false) String msg
										   ,HttpSession session
										   ,Model model) {
		String userId = (String) session.getAttribute("SID");
		model.addAttribute("title", "보증금 기준 생성");
		model.addAttribute("userId", userId);
		if(msg != null) model.addAttribute("msg", msg);

		return "admin/deposit/createDepositStandard";
	}
	
	@PostMapping("/createDepositStandard")
	public String createDepositStandardPost(DepositStandard depositStandard, RedirectAttributes reAttr) {
	    
	    // 1. 제출된 일수(period) 값을 가져옵니다.
	    int period = depositStandard.getWaitingDepositPeriod();
	    
	    // 2. 서비스에 중복 검사를 요청합니다.
	    boolean isDuplicate = depositService.isPeriodDuplicated(period);
	    
	    // 3. 중복된 경우
	    if(isDuplicate) {
	        // 리다이렉트 페이지에 일회성 메시지를 전달합니다.
	        reAttr.addFlashAttribute("msg", "이미 존재하고 있는 기준입니다.");
	        // 다시 생성 페이지로 리다이렉트합니다.
	        return "redirect:/admin/deposit/createDepositStandard";
	    }
	    
	    // 4. 중복되지 않은 경우, 기존 로직대로 기준을 생성합니다.
	    depositService.createDepositStandard(depositStandard);
	    log.info("depositStandard: {}", depositStandard);

	    // 성공 시 목록 페이지로 리다이렉트합니다.
	    return "redirect:/admin/deposit/depositStandard";
	}
	
	//보증금 기준 관리 수정
	@GetMapping("/modifyDepositStandard")
	public String modifyDepositStandardGet(@RequestParam(value="waitingDepositStandardCode") String waitingDepositStandardCode,
										   Model model) {
		DepositStandard depositStandardInfo = depositService.getDepositStandardInfoById(waitingDepositStandardCode);

		model.addAttribute("title", "보증금 기준 수정");
		model.addAttribute("depositStandardInfo", depositStandardInfo);

		return "admin/deposit/modifyDepositStandard";
	}
	@PostMapping("/modifyDepositStandard")
public String modifyDepositStandardPost(DepositStandard depositStandard, RedirectAttributes reAttr) {
		
        // 1. 검증 및 수정 로직이 포함된 서비스 호출
        String resultCode = depositService.modifyDepositStandard(depositStandard);

        // 2. 성공 시 목록으로
        if ("SUCCESS".equals(resultCode)) {
    		return "redirect:depositStandard";
        }

        // 3. 실패 시 (중복 발견 시)
        String msg = "";
        if ("ERR_PERIOD_DUPLICATE".equals(resultCode)) {
            msg = "이미 사용 중인 일수입니다. (다른 기준)";
        } else if ("ERR_USE_DUPLICATE".equals(resultCode)) {
            msg = "다른 기준이 이미 'Y' (사용중)으로 설정되어 있습니다.";
        } else {
            msg = "알 수 없는 오류로 수정에 실패했습니다.";
        }
        
        // 4. 실패 메시지(msg)와 코드를 가지고 수정 페이지로 리다이렉트
		reAttr.addAttribute("waitingDepositStandardCode", depositStandard.getWaitingDepositStandardCode());
		reAttr.addFlashAttribute("msg", msg);
		return "redirect:/admin/deposit/modifyDepositStandard";
	}
	// ajax 기준 체크 
	@PostMapping("/modifyCheck")
	@ResponseBody
	public int modifyCheck(@RequestParam(value="waitingDepositPeriod")int waitingDepositPeriod, Model model) {
		log.info("수정 체크:{}",waitingDepositPeriod);
		int userDepositStandardPeriod =depositService.modifyCheck(waitingDepositPeriod);
		return userDepositStandardPeriod;
	}

	
	//보증금 기준 관련 삭제
	@GetMapping("/deleteDepositStandard")
	public String deleteDepositStandardGet(@RequestParam(value="waitingDepositStandardCode") String waitingDepositStandardCode,
	        @RequestParam(value="msg", required = false) String msg,
	        Model model) {
	    
	    model.addAttribute("title", "보증금 기준 삭제");
	    model.addAttribute("waitingDepositStandardCode", waitingDepositStandardCode);
	    	   
	    if(msg != null) model.addAttribute("msg", msg);
	    
	    return "admin/deposit/deleteDepositStandard";
	}
	
	
	@PostMapping("/deleteDepositStandard")
	public String deleteDepositStandardPost(DepositStandard depositStandard,
            RedirectAttributes redirectAttributes) { // RedirectAttributes 파라미터 추가

// [수정] 서비스 호출 후 결과 코드를 받음
			String resultCode = depositService.deleteDepositStandard(depositStandard);
				
				if ("SUCCESS".equals(resultCode)) {
				return "redirect:depositStandard";
				}						
				String msg = "";
				if ("ERR_ID_NOT_FOUND".equals(resultCode)) {
				msg = "관리자 아이디가 존재하지 않습니다.";
				} else if ("ERR_PW_MISMATCH".equals(resultCode)) {
				msg = "관리자 비밀번호가 일치하지 않습니다.";
				} else {			
				msg = "삭제에 실패했습니다";
				}
					
				redirectAttributes.addFlashAttribute("msg", msg);
						
				redirectAttributes.addAttribute("waitingDepositStandardCode", 
				         depositStandard.getWaitingDepositStandardCode());
				
				return "redirect:/admin/deposit/deleteDepositStandard";
				}
	
	
	@PostMapping("/depositStandardUseCheck")
	@ResponseBody
	public boolean depositStandardUseCheck(@RequestParam(value ="waitingDepositStandardCode") String waitingDepositStandardCode) {
		boolean result = depositService.depositStandardUseCheck(waitingDepositStandardCode);

		log.info("기준 사용 유무 중복체크 결과값:{}", result);

		return result;
	} 

	
	
	
	
	
	

}