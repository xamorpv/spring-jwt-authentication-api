package ru.ls.pjwt.domain.user.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.ls.pjwt.base.DatabaseIntegrationEnvironment;
import ru.ls.pjwt.common.property.AuthoritiesProperties;
import ru.ls.pjwt.domain.user.entity.User;

@SpringBootTest
class AuthorityServiceSpringTest extends DatabaseIntegrationEnvironment {
  @Autowired private AuthorityService authorityService;

  @Autowired private AuthoritiesProperties authoritiesProperties;

  @Test
  void shouldAssignUserRoleWhenAssigningDefaultAuthority() {
    final User user = new User();
    authorityService.assignDefaultAuthority(user);
    assertEquals(1, user.getAuthorities().size(), "default authorities size");
    assertEquals(
        authoritiesProperties.user(),
        user.getAuthorities().stream().findFirst().orElseThrow().getName(),
        "default authority");
  }
}
