package com.framework;

import org.springframework.aop.framework.AdvisedSupport;
import org.springframework.aop.framework.AopProxy;
import org.springframework.beans.factory.SmartFactoryBean;

public class FooBeanFactory implements SmartFactoryBean {

	private final Class<?> serviceInterface;
	private final NamingStrategyEnum namingStrategy;
	private final Class<?> targetClass;

	public FooBeanFactory(final Class<?> serviceInterface,
	                      final NamingStrategyEnum namingStrategy,
	                      final Class<?> targetClass) {
		this.serviceInterface = serviceInterface;
		this.namingStrategy = namingStrategy;
		this.targetClass = targetClass;
	}

	@Override
	public Object getObject() {
		AdvisedSupport advisedSupport = new AdvisedSupport();
		advisedSupport.setProxyTargetClass(false);
		advisedSupport.setInterfaces(serviceInterface);
		advisedSupport.addAdvice(new Interceptor());
		advisedSupport.setTargetClass(targetClass);
		advisedSupport.setOptimize(true);
		advisedSupport.setOpaque(false);
		advisedSupport.setExposeProxy(false);
		advisedSupport.setFrozen(true);

		final AopProxy aopProxy = new FooProxyFactory(namingStrategy).createAopProxy(advisedSupport);
		return aopProxy.getProxy();
	}

	@Override
	public Class<?> getObjectType() {
			return serviceInterface;
	}
}
