package co.insecurity.springref.security.service;

import co.insecurity.util.passcheck.PassCheck;


public class BFPassCheckService implements PassCheckService {

	private PassCheck passcheck;
	
	
	public BFPassCheckService() {
			passcheck = new PassCheck();
	}
	
	@Override
	public boolean isCommon(String password) {
		return passcheck.isCommon(password);
	}
}