package ks47team03.user.controller;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;


import ch.qos.logback.classic.Logger;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.servlet.http.HttpServletRequest;
import ks47team03.user.dto.TossPayment;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import ks47team03.user.dto.Account;
import ks47team03.user.dto.Deposit;
import ks47team03.user.dto.Point;
import ks47team03.user.mapper.UserDepositMapper;
import ks47team03.user.service.UserDepositService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

import static javax.crypto.Cipher.SECRET_KEY;

@Slf4j
@Controller
@RequestMapping("/deposit")
public class UserDepositController {
	
	// 의존성 주입
	private final UserDepositService userDepositService;

	private final String SECRET_KEY = "test_sk_LBa5PzR0ArngwDn2wKx8vmYnNeDM";



	public UserDepositController(UserDepositService userDepositService) {
		this.userDepositService = userDepositService;
	}

	@RequestMapping(value = "/confirm")
	public ResponseEntity<JSONObject> confirmPayment( Model model, HttpSession session,
													  @RequestBody String jsonBody, TossPayment tossPayment) throws Exception {
	 String userName= (String) session.getAttribute("SNAME");
		JSONParser parser = new JSONParser();
		String orderId;
		String amount;
		String paymentKey;

		try {
			JSONObject requestData = (JSONObject) parser.parse(jsonBody);
			paymentKey = (String) requestData.get("paymentKey");
			orderId = (String) requestData.get("orderId");
			amount = (String) requestData.get("amount");

		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
		;
		JSONObject obj = new JSONObject();
		obj.put("orderId", orderId);
		obj.put("amount", amount);
		obj.put("paymentKey", paymentKey);

		log.info(String.valueOf(obj));
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

		model.addAttribute("responseStr", jsonObject.toJSONString());
		System.out.println(jsonObject.toJSONString());

		if (((String) jsonObject.get("method")).equals("가상계좌")) {
				tossPayment.setMethod("가상계좌");
				tossPayment.setVirtualAccountNumber((String) ((JSONObject) jsonObject.get("virtualAccount")).get("accountNumber"));
				tossPayment.setCustomerName(((String)  ((JSONObject)jsonObject.get("virtualAccount")).get("customerName")));
				tossPayment.setVirtualBank((String)  ((JSONObject)jsonObject.get("virtualAccount")).get("bank"));
				tossPayment.setAmount((String)  ((JSONObject)jsonObject.get("virtualAccount")).get("totalAmount"));

		} else if (((String) jsonObject.get("method")).equals("계좌이체")) {
			model.addAttribute("bank", (String) ((JSONObject) jsonObject.get("transfer")).get("bank"));

		}else {
			model.addAttribute("code", (String) jsonObject.get("code"));
			model.addAttribute("message", (String) jsonObject.get("message"));
		}
		tossPayment.setOrderId((String) jsonObject.get("orderId"));
		tossPayment.setOrderName((String) jsonObject.get("orderName"));

		log.info("test"+String.valueOf(tossPayment));
		this.userDepositService.payByTossPayments(tossPayment);


		return ResponseEntity.status(code).body(jsonObject);
	}





	/*@PostMapping*/

	@RequestMapping(value = "/success", method = RequestMethod.GET)
	public String tossPaySuccess(HttpServletRequest request, Model model ,HttpSession session, @RequestParam String paymentKey, 
			 					@RequestParam String orderId, 
			 					@RequestParam Long amount, 
			 					TossPayment tossPayment) throws Exception { 
			
		// 1. /confirm 에 있던 승인 요청 JSON 객체 생성 로직
				JSONObject obj = new JSONObject();
				obj.put("orderId", orderId);
				obj.put("amount", amount);
				obj.put("paymentKey", paymentKey);

				log.info("Toss 승인 요청 JSON: " + String.valueOf(obj));

				// 2. /confirm 에 있던 Toss API 서버-to-서버 호출 로직
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
				boolean isSuccess = (code == 200);

				InputStream responseStream = isSuccess ? connection.getInputStream() : connection.getErrorStream();
				
				JSONParser parser = new JSONParser(); // JSON 파서 생성
				Reader reader = new InputStreamReader(responseStream, StandardCharsets.UTF_8);
				JSONObject jsonObject = (JSONObject) parser.parse(reader);
				responseStream.close();

				model.addAttribute("responseStr", jsonObject.toJSONString());
				System.out.println("Toss 승인 응답: " + jsonObject.toJSONString());

				// 3. /confirm 에 있던 응답 결과 파싱 및 DB 저장 로직
				if (isSuccess) {
										
		            
					String userId = (String) session.getAttribute("SID");
					// tossPayment DTO에 필요한 정보 채우기
					// (TossPayment DTO에 userId 필드가 없다면 추가해야 합니다)
					// tossPayment.setUserId(userId); 
					tossPayment.setOrderId((String) jsonObject.get("orderId"));
					tossPayment.setOrderName((String) jsonObject.get("orderName"));
					tossPayment.setAmount(String.valueOf(jsonObject.get("totalAmount"))); // Long을 String으로

					if (((String) jsonObject.get("method")).equals("가상계좌")) {
						tossPayment.setMethod("가상계좌");
						JSONObject virtualAccount = (JSONObject) jsonObject.get("virtualAccount");
						tossPayment.setVirtualAccountNumber((String) virtualAccount.get("accountNumber"));
						tossPayment.setCustomerName((String) virtualAccount.get("customerName"));
						tossPayment.setVirtualBank((String) virtualAccount.get("bank"));
						
					} else if (((String) jsonObject.get("method")).equals("계좌이체")) {
						tossPayment.setMethod("계좌이체");
						JSONObject transfer = (JSONObject) jsonObject.get("transfer");
						model.addAttribute("bank", (String) transfer.get("bank"));
						// 계좌이체 관련 정보 DTO에 추가 (필요시)
					}
					
					log.info("DB 저장될 TossPayment DTO: " + String.valueOf(tossPayment));
					
					tossPayment.setUserId((String) session.getAttribute("SID"));
					// 4. 서비스 호출하여 DB에 저장
					this.userDepositService.payByTossPayments(tossPayment);

					// 5. 성공 페이지로 이동
					return "redirect:/deposit/depositCheckSuccess";

				} else {
					//6. 실패 시 실패 페이지로 이동													
					log.error("Toss 결제 승인 실패: {}", jsonObject.toJSONString()); 
				    
				    model.addAttribute("code", (String) jsonObject.get("code"));
				    model.addAttribute("message", (String) jsonObject.get("message"));
				    return "user/deposit/depositCheckFail";
				    
				    			
				}
			}



	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String index(HttpServletRequest request, Model model) throws Exception {

		return "user/deposit/checkout";
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

		return "user/deposit/depositCheckFail";
	}
	
	
	/*
	 * @PostMapping("/webhook/toss") public ResponseEntity<String>
	 * handleTossWebhook(@RequestBody JsonNode payload) {
	 * 
	 * // 1. 이벤트 타입 (eventType) 확인 String eventType =
	 * payload.get("eventType").asText(); log.info("Toss Webhook 수신. 이벤트 타입: {}",
	 * eventType);
	 * 
	 * try { // 2. 이벤트 타입별 비즈니스 로직 처리 switch (eventType) { case
	 * "VIRTUAL_ACCOUNT_DEPOSIT_COMPLETE": // 🔔 [중요] 가상계좌 입금 완료 처리 String orderId =
	 * payload.get("data").get("orderId").asText(); String secret =
	 * payload.get("data").get("secret").asText(); // 시크릿 키 검증용 (선택)
	 * log.info("가상계좌 입금 완료. 주문번호: {}", orderId);
	 * 
	 * // TODO: // 1. DB에서 orderId로 주문 정보 조회 (userDepositService 이용) // 2. (보안)
	 * TOSS에서 받은 secret이 DB에 저장된 secret과 일치하는지 확인 // 3. 주문 상태를 "입금 완료" 또는 "결제 완료"로
	 * 변경 // 예: userDepositService.updatePaymentStatus(orderId,
	 * "PAYMENT_COMPLETED"); // 4. DB에 저장
	 * 
	 * break;
	 * 
	 * case "PAYMENT_STATUS_CHANGED": // 결제 상태 변경 처리 (예: 에스크로 상태 변경) String
	 * paymentStatusOrderId = payload.get("data").get("orderId").asText(); String
	 * status = payload.get("data").get("status").asText();
	 * log.info("결제 상태 변경: {} / 상태: {}", paymentStatusOrderId, status);
	 * 
	 * // TODO: 필요한 경우 주문 상태 업데이트 로직 break;
	 * 
	 * // TODO: 필요한 다른 이벤트 타입들 처리 (예: REFUND_STATUS_CHANGED 등)
	 * 
	 * default: log.warn("처리되지 않는 이벤트 타입: {}", eventType); break; }
	 * 
	 * // 3. Toss Payments 서버에 "성공" 응답 (200 OK) // 이 응답을 보내야 Toss 측에서 재요청을 보내지 않습니다.
	 * return ResponseEntity.ok("success");
	 * 
	 * } catch (Exception e) { // 4. 로직 처리 중 에러 발생 시 // 에러 로그를 남기고, Toss에는 500
	 * Internal Server Error 응답 // Toss는 이 경우 웹훅을 재시도합니다. log.error("웹훅 처리 중 에러 발생",
	 * e); return ResponseEntity.internalServerError().body("error"); } }
	 * 
	 * 
	 * //일반 결제 성공 페이지
	 */	
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
		System.out.println("userId: "+ userId);
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
	public String depositRefundGet(Model model, HttpSession session) {
		String userId = (String) session.getAttribute("SID");
		String userName = (String) session.getAttribute("NAME");
		Deposit userDeposit = userDepositService.getUserDeposit(userId);

		int currentDeposit = userDeposit.getCurrentHoldingDeposit();
		model.addAttribute("userId", userId);
		model.addAttribute("userName", userName);
		model.addAttribute("currentDeposit", currentDeposit);
		return "user/deposit/mydepositRefund";
	}
	@PostMapping("mydepositRefund")
	public String depositRefundPost(Deposit depositRefundHistory){
		userDepositService.createDepositRefund(depositRefundHistory);
		return "redirect:mydepositRefund";
	}
	
	
	
	
	
	
	
	
	
	

}
