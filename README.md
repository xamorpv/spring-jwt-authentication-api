## JavaDoc Policy
- **Public methods** must be documented according to Checkstyle rules.
- **Generated methods** (Spring Data JPA query methods) are exempt from JavaDoc requirements
  and are suppressed with `@SuppressWarnings("checkstyle:MissingJavadocMethod")`.
- **Spring DI constructors** (plain dependency injection constructors without additional logic)
  may be suppressed with `@SuppressWarnings("checkstyle:MissingJavadocMethod")`.