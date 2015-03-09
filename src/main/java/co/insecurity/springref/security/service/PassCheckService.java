package co.insecurity.springref.security.service;

import org.springframework.stereotype.Service;

@Service
public interface PassCheckService {

	public boolean isCommon(String password);
}
