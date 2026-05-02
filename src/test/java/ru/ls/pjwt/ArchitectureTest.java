package ru.ls.pjwt;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.library.Architectures;
import com.tngtech.archunit.library.GeneralCodingRules;
import org.springframework.web.bind.annotation.RestController;

@AnalyzeClasses(packages = "ru.ls.pjwt", importOptions = ImportOption.DoNotIncludeTests.class)
public class ArchitectureTest {

  @ArchTest
  private static final ArchRule NO_CYCLES =
      slices().matching("ru.ls.pjwt.(*)..").should().beFreeOfCycles();

  /**
   * The common package is an infrastructure layer and should not depend on domain logic or global
   * settings.
   */
  @ArchTest
  private static final ArchRule COMMON_INDEPENDENCE =
      noClasses()
          .that()
          .resideInAPackage("ru.ls.pjwt.common..")
          .should()
          .dependOnClassesThat()
          .resideInAnyPackage("ru.ls.pjwt.domain..", "ru.ls.pjwt.security..");

  /**
   * The user domain should not depend on web-related layers like auth or token. This ensures that
   * we never pass RegisterRequest into UserService.
   */
  @ArchTest
  private static final ArchRule USER_DOMAIN_INDEPENDENCE =
      noClasses()
          .that()
          .resideInAPackage("ru.ls.pjwt.domain.user..")
          .should()
          .dependOnClassesThat()
          .resideInAnyPackage("ru.ls.pjwt.domain.auth..", "ru.ls.pjwt.domain.token..");

  /** The token domain handles session mechanics and should not depend on auth web controllers. */
  @ArchTest
  private static final ArchRule TOKEN_DOMAIN_INDEPENDENCE =
      noClasses()
          .that()
          .resideInAPackage("ru.ls.pjwt.domain.token..")
          .should()
          .dependOnClassesThat()
          .resideInAPackage("ru.ls.pjwt.domain.auth..");

  /** Controllers should not directly access repositories; they should interact through services. */
  @ArchTest
  private static final ArchRule LAYERED_ARCHITECTURE =
      Architectures.layeredArchitecture()
          .consideringAllDependencies()
          .layer("Controller")
          .definedBy("..controller..")
          .layer("Service")
          .definedBy("..service..", "..mapper..")
          .layer("Repository")
          .definedBy("..repository..")
          .whereLayer("Controller")
          .mayNotBeAccessedByAnyLayer()
          .whereLayer("Repository")
          .mayOnlyBeAccessedByLayers("Service");

  @SuppressWarnings("PMD.LongVariable")
  @ArchTest
  private static final ArchRule ENTITIES_SHOULD_NOT_DEPEND_ON_DTOS =
      noClasses()
          .that()
          .resideInAPackage("..entity..")
          .should()
          .dependOnClassesThat()
          .resideInAPackage("..dto..");

  @SuppressWarnings("PMD.LongVariable")
  @ArchTest
  private static final ArchRule CONTROLLERS_SHOULD_BE_NAMED_PROPERLY =
      classes()
          .that()
          .areAnnotatedWith(RestController.class)
          .should()
          .haveSimpleNameEndingWith("Controller")
          .andShould()
          .resideInAPackage("..controller..");

  @SuppressWarnings("PMD.LongVariable")
  @ArchTest
  private static final ArchRule REPOSITORIES_SHOULD_BE_INTERFACES =
      classes()
          .that()
          .resideInAPackage("..repository..")
          .should()
          .beInterfaces()
          .andShould()
          .haveSimpleNameEndingWith("Repository");

  @ArchTest
  private static final ArchRule NO_SYSTEM_OUT =
      GeneralCodingRules.NO_CLASSES_SHOULD_ACCESS_STANDARD_STREAMS;

  @ArchTest
  private static final ArchRule NO_FIELD_INJECTION =
      GeneralCodingRules.NO_CLASSES_SHOULD_USE_FIELD_INJECTION;
}
