package co.insecurity.springref.core.security;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

import co.insecurity.utils.TokenBucket;

public class RateLimitingDaoAuthenticationProvider extends DaoAuthenticationProvider {
	
	private static final Logger LOG = LoggerFactory.getLogger(RateLimitingDaoAuthenticationProvider.class);

	private final int ATTEMPTS_PER_INTERVAL;
	private final double RATE_LIMIT;

	private class RateLimitedAuthenticator {
		
		private final String username;
		private final String ipAddress;
		private TokenBucket rateLimiter;
		
		public RateLimitedAuthenticator(String username, String ipAddress) {
			this.username = username;
			this.ipAddress = ipAddress;
			this.rateLimiter = new TokenBucket((double) ATTEMPTS_PER_INTERVAL, RATE_LIMIT);
		}
		
		public String getUsername() {
			return username;
		}
		
		public String getIpAddress() {
			return ipAddress;
		}
		
		public boolean canAttemptAuthentication() {
			return rateLimiter.removeTokens(1);
		}
		
		@Override
		public String toString() {
			return String.format("RateLimitedAuthenticator[username=%s, ip_address=%s, rate_limiter=%s]", 
					this.username, this.ipAddress, this.rateLimiter.toString());
		}
	}
	
	private Map<String, RateLimitedAuthenticator> authnRequestors;
	
	/**
	 * Constructs a new {@code RateLimitingDaoAuthenticationProvider} which allows a user to authenticate 
	 * with a given username from the user's current IP address iff the number of authentication 
	 * attempts with that username and IP address has not exceeded the rate defined by 
	 * {@code rateLimit} attempts per second.
	 * 
	 * The rate limit is implemented as a token bucket, meaning that burstyness is tolerated such that 
	 * given a {@code RateLimitingDaoAuthenticationProvider} with {@code attemptsPerInterval} set to 3 
	 * and {@code rateLimit} set to 1.0, the {@code RateLimitingDaoAuthenticationPrrovider} will allow 
	 * a user to fail 2 authentication attempts and then successfully authenticate, all within a 
	 * single second.
	 * 
	 * 
	 * @param attemptsPerInterval the number of authentication attempts permitted within the given 
	 * {@code timeInterval} before authentication attempts are denied
	 * @param rateLimit the number of authentication attempts permitted per second
	 */
	public RateLimitingDaoAuthenticationProvider(int attemptsPerInterval, double rateLimit) {
		super();
		this.ATTEMPTS_PER_INTERVAL = attemptsPerInterval;
		this.RATE_LIMIT = rateLimit;
		this.authnRequestors = new HashMap<String, RateLimitedAuthenticator>();
	}
	
	/**
	 * Returns the {@code RateLimitedAuthenticator} if one already exists for the specified username and 
	 * IP address, otherwise a new one is created and returned.
	 * 
	 * @param username the username that was used in the authentication attempt
	 * @param ipAddress the IP address that was used in the authentication attempt
	 * @return a {@code RateLimitedAuthenticator} for the given username and IP address
	 */
	public RateLimitedAuthenticator getRateLimitedAuthenticator(String username, String ipAddress) {
		String requestorKey = username.concat(ipAddress); 
		RateLimitedAuthenticator requestor = authnRequestors.get(requestorKey);
		if (requestor == null) {
			LOG.debug("No previous authentication attempts found, adding new RateLimitedAuthenticator");
			requestor = new RateLimitedAuthenticator(username, ipAddress);
			authnRequestors.put(requestorKey, requestor);
		}
		return requestor;
	}
	
	@Override
	protected void additionalAuthenticationChecks(UserDetails userDetails, 
			UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
		String username = userDetails.getUsername();
		LOG.info("Password is here: {}", authentication.getCredentials().toString()); 
		Object obj = authentication.getDetails();
		if (obj instanceof WebAuthenticationDetails) {
			LOG.debug("Processing additional authentication checks!!");
			String ipAddr = ((WebAuthenticationDetails) obj).getRemoteAddress();
			RateLimitedAuthenticator requestor = getRateLimitedAuthenticator(username, ipAddr);
			if (requestor.canAttemptAuthentication()) {
				LOG.debug("Authentication rate ok ({})... attempting authentication.", requestor.toString());
				super.additionalAuthenticationChecks(userDetails, authentication);
			}
			else {
				String msg = "Authentication failed: authentication rate exceeded "
						+ "for requestor: " + requestor.toString(); 
				LOG.debug(msg);
				throw new AuthenticationRateException(msg);
			}
		} else {
			LOG.error("Could not perform rate limit checks - authentication for user '{}' "
					+ "does not contain WebAuthenticationDetails.", username);
			super.additionalAuthenticationChecks(userDetails, authentication);
		}
	}
}