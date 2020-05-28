package com.github.bluecatlee.common.pay.factory;

import com.github.bluecatlee.common.pay.service.PayService;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class DefaultPayFactory extends AbstractPayFactory implements ApplicationContextAware {

	private ApplicationContext applicationContext;

	@Override
	public PayService create(String type) {
		return (PayService) applicationContext.getBean(type);
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

}
