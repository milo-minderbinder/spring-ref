package co.insecurity.springref.config;

import org.jasypt.encryption.pbe.PBEStringEncryptor;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.EnvironmentStringPBEConfig;
import org.jasypt.encryption.pbe.config.StringPBEConfig;
import org.jasypt.spring31.properties.EncryptablePropertySourcesPlaceholderConfigurer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

@Configuration
public class JasyptConfig {
	
	private static final Logger LOG = LoggerFactory.getLogger(JasyptConfig.class);

	@Bean
	static StringPBEConfig environmentVariablesConfiguration() {
		EnvironmentStringPBEConfig config = new EnvironmentStringPBEConfig();
		config.setAlgorithm("PBEWithMD5AndDES");
		config.setPasswordEnvName("SPRINGREF_CONFIG_PW");
		return config;
	}
	
	@Bean
	public static PBEStringEncryptor stringEncryptor() {
		StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
		encryptor.setConfig(environmentVariablesConfiguration());
		return encryptor;
	}

	@Bean
	public static PropertySourcesPlaceholderConfigurer properties() {
		EncryptablePropertySourcesPlaceholderConfigurer propertyEncryptor = 
				new EncryptablePropertySourcesPlaceholderConfigurer(stringEncryptor());
		Resource[] resources = new ClassPathResource[] { new ClassPathResource("setup.properties") };
		propertyEncryptor.setLocations(resources);
		propertyEncryptor.setIgnoreUnresolvablePlaceholders(true);
		return propertyEncryptor;
	}
}