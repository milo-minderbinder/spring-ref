package co.insecurity.springref.security.service;

import java.io.IOException;

import co.insecurity.util.passcheck.PassCheck;


public class BFPassCheckService implements PassCheckService {

	private PassCheck passcheck;
	
	
	public BFPassCheckService() {
		try {
			passcheck = new PassCheck();
		} catch (IOException e) {
			e.printStackTrace();
			passcheck = null;
		}
	}
	
	@Override
	public boolean isCommon(String password) {
		return passcheck.isCommon(password);
	}
}