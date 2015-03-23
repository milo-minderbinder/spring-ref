package co.insecurity.springref.security.policy;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import co.insecurity.security.policy.PasswordPolicy;
import co.insecurity.security.policy.assertion.LengthAssertion;
import co.insecurity.security.policy.assertion.NotLeakedAssertion;

public class SimplePasswordPolicy extends PasswordPolicy {

	private static final Logger LOG = LoggerFactory.getLogger(SimplePasswordPolicy.class);

	public SimplePasswordPolicy() {
		this.assertions.add(
				new LengthAssertion(9, LengthAssertion.DISABLED));
		try {
			this.assertions.add(new NotLeakedAssertion
					.Builder().withFalsePositiveProbability(0.001)
					.withIgnoreCase(true)
					.withMaxNumPasswords(NotLeakedAssertion.MAX_NUM_PASSWORDS_DISABLED)
					.build());
		} catch (IOException e) {
			LOG.error("Failed to create and add NotLeakedAssertion to policy");
		}
	}
}
