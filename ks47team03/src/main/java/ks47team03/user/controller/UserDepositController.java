package ks47team03.user.controller;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;
import ks47team03.admin.mapper.AdminCommonMapper;
import ks47team03.user.service.UserDepositService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/deposit")
public class UserDepositController {
	
	// 의존성 주입
	private final UserDepositService userDepositService;
	
	
	public UserDepositController(UserDepositService userDepositService) {
		this.userDepositService = userDepositService;
	}
	
	@GetMapping("/depositCheckSuccess")
	public String depositCheckSuccess(
			//@RequestParam(value="orderId",)
			Model model) {
		model.addAttribute("title","결제 성공");
		return "user/deposit/depositCheckSuccess";
	}
	
	@GetMapping("/depositCheckFail")
	public String depositCheckfail(Model model) {
		model.addAttribute("title","결제 실패");
		return "user/deposit/depositCheckFail";
	}

	@GetMapping("/mydeposit")
	public String mydeposit(@RequestParam(value="currentPage", required = false ,defaultValue = "1")int currentPage,
			Model model) {
		
		
		
		
		
		Map<String,Object> resultMap = userDepositService.getUserDepositManageList(currentPage);
		int lastPage = (int)resultMap.get("lastPage");
		List<Map<String,Object>> userDepositManageList = (List<Map<String,Object>>)resultMap.get("userDepositManageList");
		log.info("userDepositManageList:{}",userDepositManageList);
		model.addAttribute("title","회원 보증금 관리");
		int startPageNum = (int) resultMap.get("startPageNum");
		int endPageNum = (int) resultMap.get("endPageNum");
		model.addAttribute("title","회원 보증금 관리");
		model.addAttribute("currentPage", currentPage);
		model.addAttribute("lastPage", lastPage);
		model.addAttribute("userDepositManageList", userDepositManageList);
		model.addAttribute("startPageNum", startPageNum);
		model.addAttribute("endPageNum", endPageNum);
		
		model.addAttribute("title","보증금 조회");
		
		return "user/deposit/mydeposit";
	}
	
	
    @GetMapping("/mydepositPay")
    public String mydepositPay (@RequestParam (value = "currentPage", required = false, defaultValue = "1") int currentPage,
                               HttpSession session, Model model) {
      Map<String, Object> resultMap = userDepositService.getUserDepositPayList(currentPage);
      String userName = (String) session.getAttribute("SNAME");
      /* 2023.12.05 최수봉
      ** 페이징 처리 */
      int lastPage = (int) resultMap.get("lastPage");
      int startPageNum = (int) resultMap.get("startPageNum");
      int endPageNum = (int) resultMap.get("endPageNum");

      List<Map<String, Object>> userDepositPayList = (List<Map<String, Object>>) resultMap.get("userDepositPayList");
      log.info("userDepositPayList:{}", userDepositPayList);

      model.addAttribute("title", "보증금 결제");
      model.addAttribute("currentPage", currentPage);
      model.addAttribute("lastPage", lastPage);
      model.addAttribute("startPageNum", startPageNum);
      model.addAttribute("endPageNum", endPageNum);
      model.addAttribute("userDepositPayList", userDepositPayList);
      model.addAttribute("userName", userName);
      
      return "user/deposit/mydepositPay";
    }
	
	@GetMapping("/mydepositRefund")
	public String pointRefundSponsorship(Model model) {
		
		model.addAttribute("title","보증금 환급");
		
		return "user/deposit/mydepositRefund";
	}

}
