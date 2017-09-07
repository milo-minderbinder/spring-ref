# spring-ref
This is a very basic [Spring](https://spring.io) app I've cobbled together to demonstrate and test different security
features and controls in Spring and [Spring Security](https://projects.spring.io/spring-security/), as well as Thymeleaf
(templating engine) and its integration with Spring Security, and Jasypt (simple crypto lib - used here to encrypt sensitive 
properties) [**NOTE**: Jasypt is basically unmaintained, so it should generally be ignored].

## Areas of Interest
* [Spring Security Config](./src/main/java/co/insecurity/springref/config/SecurityConfig.java)
* [Authentication Provider](./src/main/java/co/insecurity/springref/security/RateLimitingDaoAuthenticationProvider.java)
    * Custom PoC subclass of Spring Security's builtin DaoAuthenticationProvider, which adds authentication attempt rate 
      limiting (with configurable rate and burstiness tolerance).
* [Password Policy](./src/main/java/co/insecurity/springref/security/policy/SimplePasswordPolicy.java)
    * Implementation of [policy](https://github.com/milo-minderbinder/policy) that defines a basic password policy bean,
      which is used to enforce length and complexity requirements. It also enforces a "uniqueness"/"non-exposure" policy, which checks passwords against a list of 10,000 passwords collated from several public breaches. The list is stored in a bloom filter backed by Redis for quick/scalable checking.
* [UserValidator](./src/main/java/co/insecurity/springref/web/domain/UserValidator.java)
    * An example Spring `Validator` implementation, which is used to validate that user model objects satisfy certain validation checks. It serves as a centralized enforcement point for the aforementioned password policy, as well as checking that fields are not empty, and other similar checks. `Validator` classes are a good place to define and execute checks against untrusted inputs from various sources, since `Validator`s are not tied to a specific domain; in other words, the same class could be used to validate domain model objects for the persistence domain (e.g. database entity objects) as to validate web domain objects (e.g. objects exposed as `ModelAttribute`s in the view).
* [UserDetailsService](./src/main/java/co/insecurity/springref/persistence/service/UserPersistenceEventHandler.java)
