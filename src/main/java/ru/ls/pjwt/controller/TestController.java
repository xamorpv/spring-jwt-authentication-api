package ru.ls.pjwt.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.ls.pjwt.dto.StandardResponse;
import ru.ls.pjwt.utils.ApiResponse;
import ru.ls.pjwt.utils.LogUtils;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/test")
public class TestController {
    @GetMapping("/public")
    public ResponseEntity<StandardResponse<Void>> publicData() {
        log.debug("someone get public data");
        return ApiResponse.success("public data", HttpStatus.OK);
    }

    @GetMapping("/protected")
    public ResponseEntity<StandardResponse<UserDetails>> protectedData(@AuthenticationPrincipal UserDetails userDetails) {
        log.info("request to protected endpoint {}", LogUtils.safeUserDetails(userDetails, false));
        return ApiResponse.success(userDetails, "your details", HttpStatus.OK);
    }

    @GetMapping("/user-only")
    public ResponseEntity<StandardResponse<Void>> userOnly() {
        log.debug("request to user only endpoint");
        return ApiResponse.success("userOnly data", HttpStatus.OK);
    }

//    @GetMapping("/critical")
//    public ResponseEntity<StandardResponse<Void>> criticalData() {
//        return ApiResponse.success("public data", HttpStatus.OK);
//    }
}
