package org.zkoss.zkspringboot.demo.service;

import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class TestService {
	public Date getTime() {
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new Date();
	}
}
