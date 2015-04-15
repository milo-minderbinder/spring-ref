package co.insecurity.springref.security.policy;

import java.io.IOException;

import orestes.bloomfilter.BloomFilter;
import orestes.bloomfilter.FilterBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import co.insecurity.policy.PasswordPolicy;
import co.insecurity.policy.directive.BFDictionaryDirective;
import co.insecurity.policy.directive.CharTypeDirective;
import co.insecurity.policy.directive.CharacterDirective;
import co.insecurity.policy.directive.Dictionary;
import co.insecurity.policy.directive.LengthDirective;

public class SimplePasswordPolicy extends PasswordPolicy {

	private static final Logger LOG = LoggerFactory.getLogger(SimplePasswordPolicy.class);
	
	public static final String DEFAULT_DICTIONARY = "passwords.dat";

	public SimplePasswordPolicy() {
		super();
		this.addDirective(new LengthDirective(9, 128));
		this.addDirective(new CharacterDirective()
			.allow(new CharTypeDirective().matchCharTypes(
					Character.LOWERCASE_LETTER,
					Character.UPPERCASE_LETTER,
					Character.DECIMAL_DIGIT_NUMBER))
			.require(new CharTypeDirective().matchCharTypes(
					Character.UPPERCASE_LETTER,
					Character.DECIMAL_DIGIT_NUMBER)));
		try {
			Dictionary dictionary = Dictionary.fromResource(DEFAULT_DICTIONARY);
			BloomFilter<String> filter = (new FilterBuilder())
					.expectedElements(dictionary.getNumWords())
					.falsePositiveProbability(0.001)
					.redisBacked(true)
					.name("SimplePasswordPolicy")
					.redisHost("redis")
					.redisPort(6379)
					.buildBloomFilter();
			BFDictionaryDirective bfDictionaryDirective = 
					new BFDictionaryDirective(filter);
			if (filter.getBitSet().isEmpty()) {
				LOG.debug("Loading Dictionary into {}", bfDictionaryDirective);
				bfDictionaryDirective.loadDictionary(dictionary);
			}
			this.addDirective(bfDictionaryDirective);
		} catch (IOException e) {
			LOG.error("Failed to construct and add BFDictionaryDirective: {}", e);
		}
	}
}