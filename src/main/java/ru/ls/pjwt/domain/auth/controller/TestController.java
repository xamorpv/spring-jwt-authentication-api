package ru.ls.pjwt.domain.auth.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.ls.pjwt.common.web.dto.api.StandardResponse;
import ru.ls.pjwt.common.web.api.ApiResponse;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/test")
public class TestController {
    private final ApiResponse apiResponse;

    @GetMapping("/public")
    public ResponseEntity<StandardResponse<Void>> getPublicData() {
        log.debug("someone get public data");
        return apiResponse.success("public data", HttpStatus.OK);
    }

    @GetMapping("/protected")
    public ResponseEntity<StandardResponse<UserDetails>> getProtectedData(@AuthenticationPrincipal UserDetails userDetails) {
        log.info("request to protected endpoint {}", userDetails.getUsername());
        return apiResponse.success(userDetails, "your details", HttpStatus.OK);
    }

    @GetMapping("/user-only")
    public ResponseEntity<StandardResponse<Void>> getUserOnlyData() {
        log.debug("request to user only endpoint");
        return apiResponse.success("userOnly data", HttpStatus.OK);
    }

//    @GetMapping("/critical")
//    public ResponseEntity<StandardResponse<Void>> criticalData() {
//        return ApiResponse.success("public data", HttpStatus.OK);
//    }
}
