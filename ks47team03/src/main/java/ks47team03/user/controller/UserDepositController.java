package ks47team03.user.controller;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ser.Serializers;
import jakarta.servlet.http.HttpServletRequest;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import ks47team03.admin.mapper.AdminCommonMapper;
import ks47team03.user.dto.Account;
import ks47team03.user.dto.Deposit;
import ks47team03.user.dto.Point;
import ks47team03.user.mapper.UserDepositMapper;
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

	@RequestMapping(value = "/confirm")
	public ResponseEntity<JSONObject> confirmPayment( Model model,
													  @RequestBody String jsonBody) throws Exception {

		JSONParser parser = new JSONParser();
		String orderId;
		String amount;
		String paymentKey;
		String method;
		String transactionAt;
		String orderName;
		try {
			JSONObject requestData = (JSONObject) parser.parse(jsonBody);
			paymentKey = (String) requestData.get("paymentKey");
			orderId = (String) requestData.get("orderId");
			amount = (String) requestData.get("amount");
			method = (String) requestData.get("method");
			transactionAt = (String) requestData.get("transactionAt");
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
		;
		JSONObject obj = new JSONObject();
		obj.put("orderId", orderId);
		obj.put("amount", amount);
		obj.put("paymentKey", paymentKey);


		String tossPaySecretKey = "test_sk_LBa5PzR0ArngwDn2wKx8vmYnNeDM";
		Base64.Encoder encoder = Base64.getEncoder();
		byte[] encodedBytes = encoder.encode((tossPaySecretKey + ":").getBytes("UTF-8"));
		String authorizations = "Basic " + new String(encodedBytes, 0, encodedBytes.length);

		URL url = new URL("https://api.tosspayments.com/v1/payments/confirm");
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestProperty("Authorization", authorizations);
		connection.setRequestProperty("Content-Type", "application/json");
		connection.setRequestMethod("POST");
		connection.setDoOutput(true);

		OutputStream outputStream = connection.getOutputStream();
		outputStream.write(obj.toString().getBytes("UTF-8"));

		int code = connection.getResponseCode();
		boolean isSuccess = code == 200 ? true : false;

		InputStream responseStream = isSuccess ? connection.getInputStream() : connection.getErrorStream();

		Reader reader = new InputStreamReader(responseStream, StandardCharsets.UTF_8);
		JSONObject jsonObject = (JSONObject) parser.parse(reader);
		responseStream.close();




		return ResponseEntity.status(code).body(jsonObject);
	}


	@RequestMapping(value = "/success", method = RequestMethod.GET)
	public String tossPaySuccess(HttpServletRequest request, Model model) throws Exception{
		return "user/deposit/success";
	}

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String index(HttpServletRequest request, Model model) throws Exception {

		return "user/deposit//checkout";
	}

	/**
	 * 인증실패처리
	 * @param request
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/fail", method = RequestMethod.GET)
	public String failPayment(HttpServletRequest request, Model model) throws Exception {
		String failCode = request.getParameter("code");
		String failMessage = request.getParameter("message");

		model.addAttribute("code", failCode);
		model.addAttribute("message", failMessage);

		return "user/deposit/fail";
	}


	//일반 결제 성공 페이지
	
	@GetMapping("/depositCheckSuccess")
	public String depositCheckSuccess(HttpSession session,		
										Model model) {
		String userId = (String) session.getAttribute("SID");

		List<Map<String, Object>> depositPaySuccessList = userDepositService.getUserDepositPaySuccessList(userId);
		 log.info("depositPaySuccessList:{}", depositPaySuccessList);
		model.addAttribute("title","결제 성공");
		model.addAttribute("userId", userId);
		model.addAttribute("depositPaySuccessList", depositPaySuccessList);
		return "user/deposit/depositCheckSuccess";		
	}
	
	@GetMapping("/depositCheckFail")
	public String depositCheckfail(Model model) {
		model.addAttribute("title","결제 실패");
		return "user/deposit/depositCheckFail";
	}
	//내 보증금 조회
	@GetMapping("/mydeposit")
	public String mydeposit(
							@RequestParam(value="currentPage", required = false ,defaultValue = "1")int currentPage,
							HttpSession session,
							Model model) {						
		String userId = (String) session.getAttribute("SID");

		Map<String,Object> resultMap = userDepositService.getUserDepositManageList(userId, currentPage);
		int lastPage = (int)resultMap.get("lastPage");
		
		List<Map<String,Object>> userDepositManageList = (List<Map<String,Object>>)resultMap.get("userDepositManageList");		
		int startPageNum = (int) resultMap.get("startPageNum");
		int endPageNum = (int) resultMap.get("endPageNum");	
		model.addAttribute("title","회원 보증금 관리");
		model.addAttribute("userId", userId);
		model.addAttribute("userDepositManageList", userDepositManageList);
		model.addAttribute("currentPage", currentPage);
		model.addAttribute("lastPage", lastPage);
		model.addAttribute("startPageNum", startPageNum);
		model.addAttribute("endPageNum", endPageNum);	
		return "user/deposit/mydeposit";
	}
	
	//내 보증금 결제
	@GetMapping("/mydepositPay")
    public String mydepositPay (@RequestParam (value = "currentPage", required = false, defaultValue = "1") int currentPage,
                                HttpSession session, 
                                Model model) {
     String userId = (String) session.getAttribute("SID"); 
     Map<String, Object> resultMap = userDepositService.getUserDepositPayList(userId, currentPage);    
     int lastPage = (int) resultMap.get("lastPage");    
     int startPageNum = (int) resultMap.get("startPageNum");
     int endPageNum = (int) resultMap.get("endPageNum");      

     List<Map<String, Object>> userDepositPayList = (List<Map<String, Object>>)resultMap.get("userDepositPayList");
     log.info("userDepositPayList:{}", userDepositPayList);

      model.addAttribute("title", "보증금 결제");
      model.addAttribute("userId", userId);
      model.addAttribute("currentPage", currentPage);
      model.addAttribute("lastPage", lastPage);
      model.addAttribute("startPageNum", startPageNum);
      model.addAttribute("endPageNum", endPageNum);
      model.addAttribute("userDepositPayList", userDepositPayList);    
      
      return "user/deposit/mydepositPay";
    }
	
    //보증금 결제1
    @PostMapping("/mydepositPay")   
	public String createDepositPay(Deposit depositPayHistory) {
		userDepositService.createDepositPay(depositPayHistory);		
		return "redirect:depositCheckSuccess";
	}
    
    
    //보증금 환급
	@GetMapping("/mydepositRefund")
	public String depoistRefundSponsorship(Model model, HttpSession session) {
		String accountName = (String) session.getAttribute("SNAME");
		String userId = (String) session.getAttribute("SID");
		String bankName = "notSelect";
		String accountNum = "계좌번호를 입력해주세요.";
		int currentHoldingDeposit = 0;
		
		Account userAccount = userDepositService.getUserAccount(userId);
		
		Deposit userDeposit = userDepositService.getUserDeposit(userId);
				
		if(userAccount != null) {			
			bankName = userAccount.getBankName();
			accountNum = userAccount.getAccountNumber();
		}
		
		if(userDeposit !=null) currentHoldingDeposit = userDeposit.getCurrentHoldingDeposit();
		
		model.addAttribute("title","구구컵 : 포인트 환급 신청");
		model.addAttribute("accountName",accountName);
		model.addAttribute("bankName",bankName);
		model.addAttribute("accountNum",accountNum);
		model.addAttribute("currentHoldingDeposit",currentHoldingDeposit);

		return "user/deposit/mydepositRefund";
	}
	
	
	
	
	
	
	
	
	
	
	

}
