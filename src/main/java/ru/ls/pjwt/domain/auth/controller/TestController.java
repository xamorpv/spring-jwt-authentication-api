package ru.ls.pjwt.domain.auth.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.ls.pjwt.domain.auth.property.ControllerDevProperties;
import ru.ls.pjwt.common.web.dto.api.StandardResponse;
import ru.ls.pjwt.common.web.api.ApiResponse;

@Profile("dev")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/test")
public class TestController {
    private final ApiResponse apiResponse;
    private final ControllerDevProperties controllerDevProperties;

    @GetMapping("/public")
    public ResponseEntity<StandardResponse<Void>> getPublicData() {
        log.debug("public data requested");
        return apiResponse.success(controllerDevProperties.publicResponse(), HttpStatus.OK);
    }

    @GetMapping("/protected")
    public ResponseEntity<StandardResponse<UserDetails>> getProtectedData(@AuthenticationPrincipal UserDetails userDetails) {
        log.info("request to protected endpoint {}", userDetails.getUsername());
        return apiResponse.success(userDetails, controllerDevProperties.protectedResponse(), HttpStatus.OK);
    }

    @GetMapping("/user-only")
    public ResponseEntity<StandardResponse<Void>> getUserOnlyData() {
        log.debug("request to user only endpoint");
        return apiResponse.success(controllerDevProperties.userOnlyResponse(), HttpStatus.OK);
    }

//    @GetMapping("/critical")
//    public ResponseEntity<StandardResponse<Void>> criticalData() {
//        return ApiResponse.success("public data", HttpStatus.OK);
//    }
}
