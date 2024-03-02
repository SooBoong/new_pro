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
								   Model model) {
		Map<String,Object> resultMap = depositService.getDepositPayList(currentPage);
		int lastPage = (int)resultMap.get("lastPage");
		List<Map<String,Object>> depositPayList = (List<Map<String,Object>>)resultMap.get("depositPayList");
		log.info("depositPayList:{}",depositPayList);
		model.addAttribute("title","보증금 결제 관리");
		int startPageNum = (int) resultMap.get("startPageNum");
		int endPageNum = (int) resultMap.get("endPageNum");

		model.addAttribute("title","보증금 결제 관리");
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
    public String createDepositStandardPost(DepositStandard depositStandard) {
        depositService.createDepositStandard(depositStandard);

        return "redirect:depositStandard";
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
	public String modifyDepositStandardPost(DepositStandard depositStandard) {
		depositService.modifyDepositStandard(depositStandard);

		return "redirect:depositStandard";
	}
	// ajax 기준 체크 
	@PostMapping("/modifyCheck")
	@ResponseBody
	public int modifyCheck(@RequestParam(value="waitingDepositPeriod")int waitingDepositPeriod) {
		log.info("수정 체크:{}",waitingDepositPeriod);		
		return waitingDepositPeriod;
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
	public String deleteDepositStandardPost(DepositStandard depositStandard) {
		
			depositService.deleteDepositStandard(depositStandard);
			return "redirect:depositStandard";
		}	
	
	
	@PostMapping("/depositStandardUseCheck")
	@ResponseBody
	public boolean depositStandardUseCheck(@RequestParam(value="depositStandardUse") String depositStandardUse) {
		log.info("기준 사용 유무 중복체크:{}", depositStandardUse);
		boolean result = depositMapper.depositStandardUseCheck(depositStandardUse);
		log.info("기준 사용 유무 중복체크 결과값:{}", result);
		return result;
	} 
	
	
	
	
	
	
	

}