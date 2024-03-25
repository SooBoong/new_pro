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
import ks47team03.admin.mapper.AdminCommonMapper;
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
				tossPayment.setVirtualBank(((String)  ((JSONObject)jsonObject.get("virtualAccount")).get("bank")));
				tossPayment.setAmount(((String)  ((JSONObject)jsonObject.get("virtualAccount")).get("totalAmount")));

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
	public String tossPaySuccess(HttpServletRequest request, Model model





			/*@RequestParam String paymentKey, @RequestParam String orderId, @RequestParam Long amount*/) throws Exception{

/*
		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", "Basic" + Base64.getEncoder().encodeToString((SECRET_KEY+":").getBytes()).getBytes());
		headers.setContentType(MediaType.APPLICATION_JSON);

		Map<String, String> payloadMap = new HashMap<>();
		payloadMap.put("orderId", orderId);
		payloadMap.put("amount", String.valueOf(amount));

		ObjectMapper objectMapper = new ObjectMapper();
		String requestBody = (String) objectMapper.writeValueAsString(payloadMap);

		HttpEntity<String> httpEntity = new HttpEntity<>(requestBody,headers);

		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<JsonNode> responseEntity = restTemplate.postForEntity(
				"https://api.tosspayment.com/v1/payments/" + paymentKey, httpEntity, JsonNode.class);


		if(responseEntity.getStatusCode() == HttpStatus.OK){
			JsonNode successNode = responseEntity.getBody();
			model.addAttribute("orderId",successNode.get("orderId").asText());
			String secret = successNode.get("secret").asText();
			userDepositService.payByTossPayments(tossPayment);
			return "user/deposit/success";
		}else {
			JsonNode failNode = responseEntity.getBody();
			model.addAttribute("message", failNode.get("message").asText());
			model.addAttribute("code",failNode.get("code").asText());
			return "user/deposit/fail";
		}
*/



	return "user/deposit/success";}

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
