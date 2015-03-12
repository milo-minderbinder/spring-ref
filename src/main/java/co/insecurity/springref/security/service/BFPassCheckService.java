package co.insecurity.springref.security.service;

import co.insecurity.util.passcheck.PassCheck;
import co.insecurity.util.passcheck.config.PassCheckConfig;


public class BFPassCheckService implements PassCheckService {

	private PassCheck passcheck;
	
	
	public BFPassCheckService() {
		PassCheckConfig config = PassCheckConfig.getConfig()
				.withFalsePositiveProbability(0.001)
				.withMinLength(9)
				.withIgnoreCase(true);
		passcheck = new PassCheck(config);
	}
	
	@Override
	public boolean isCommon(String password) {
		return passcheck.isCommon(password);
	}
}