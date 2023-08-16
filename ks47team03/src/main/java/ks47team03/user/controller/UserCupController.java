package ks47team03.user.controller;

import jakarta.servlet.http.HttpSession;
import ks47team03.user.dto.Deposit;
import ks47team03.user.dto.Point;
import ks47team03.user.service.UserPointService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import ks47team03.user.service.UserCupService;
import ks47team03.user.service.UserDepositService;


@Controller
@RequestMapping("/mypage")
public class UserCupController {
	
	// 의존성 주입
	private final UserCupService cupService;
	private final UserPointService userPointService;
	private final UserDepositService userDepositService;


	public UserCupController(UserCupService cupService, UserPointService userPointService, UserDepositService userDepositService) {
		this.cupService = cupService;
		this.userPointService = userPointService;
		this.userDepositService = userDepositService;
	}

	//나의 바코드
	@GetMapping("/myBarcode")
	public String myBarcode(Model model) {
		model.addAttribute("title","나의 바코드");

		return "user/mypage/myBarcode";
	}

	// 나의 정보
	@GetMapping("/myInfo")
	public String myInfo(Model model, HttpSession session) {		
		String userId = (String) session.getAttribute("SID");
		Point userPoint = userPointService.getUserPoint(userId);
		Deposit userDeposit = userDepositService.getUserDeposit(userId);
		int currentPoint = userPoint.getCurrentHoldingPoint();
		int currentDeposit = userDeposit.getCurrentHoldingDeposit();		
		model.addAttribute("title","나의 정보");
		model.addAttribute("currentPoint",currentPoint);
		model.addAttribute("currentDeposit", currentDeposit);

		return "user/mypage/myInfo";
	}
	
	// 나의 구구컵
	@GetMapping("/myCup")
	public String myCup(Model model) {
		model.addAttribute("title","나의 구구컵");
		return "user/mypage/myCup";
	}
	
	// 제휴업체 신청
	@GetMapping("/partnerApply")
	public String partnerApply(Model model) {			
		model.addAttribute("title","제휴업체 신청");			
		return "user/mypage/partnerApply";
	}

}
