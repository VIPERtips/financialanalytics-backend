package com.tadiwa.financialanalytics.controller;

import java.time.LocalDate;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.tadiwa.financialanalytics.model.ApiResponse;
import com.tadiwa.financialanalytics.model.User;
import com.tadiwa.financialanalytics.model.UserProfile;
import com.tadiwa.financialanalytics.service.JwtService;
import com.tadiwa.financialanalytics.service.ReportService;
import com.tadiwa.financialanalytics.service.UserService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    @Autowired
    private ReportService reportService;
    
    @Autowired
	private JwtService jwtService;
	
	@Autowired
	private UserService userService;

	@GetMapping("/pnl")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getProfitAndLossReport(
            @RequestParam("from") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate from,
            @RequestParam("to") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate to,
            HttpServletRequest req) {
        String token = extractTokenFromRequest(req);
        String username = jwtService.extractUsername(token);
        User user = userService.getUserByUsername(username);
        UserProfile profile = userService.getUserProfile(user);
        double salary = profile.getIncome();
        Map<String, Object> pnlReport = reportService.generateValidatedProfitAndLossReport(from, to, salary);
        return ResponseEntity.ok(new ApiResponse<>("Profit and Loss report generated", true, pnlReport));
    }



    @GetMapping("/balance-sheet")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getBalanceSheetReport(
        @RequestParam("from") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate from,
        @RequestParam("to") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate to) {

        Map<String, Object> balanceSheetReport = reportService.generateBalanceSheetReport(from, to);
        return ResponseEntity.ok(new ApiResponse<>("Balance Sheet report generated", true, balanceSheetReport));
    }

    
    @GetMapping("/trends")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getTrendsReport(
        @RequestParam("from") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate from,
        @RequestParam("to") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate to) {

        Map<String, Object> trendsReport = reportService.generateTrendsReport(from, to);
        return ResponseEntity.ok(new ApiResponse<>("Trends sheet generated", true, trendsReport));
    }
    
    @GetMapping("/summary")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getSummaryReport(
        @RequestParam("from") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate from,
        @RequestParam("to") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate to) {

        Map<String, Object> summaryReport = reportService.generateSummaryReport(from, to);
        return ResponseEntity.ok(new ApiResponse<>("Summary report generated", true, summaryReport));
    }
    
    private String extractTokenFromRequest(HttpServletRequest request) {
		String token = request.getHeader("Authorization");
		if (token != null && token.startsWith("Bearer ")) {
			return token.substring(7);
		}
		return null;
	}

}
