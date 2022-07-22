package foo;

import org.springframework.aop.framework.AdvisedSupport;
import org.springframework.aop.framework.AopProxy;
import org.springframework.beans.factory.SmartFactoryBean;

import bar.DifferentServiceInterface;

public class FooBeanFactory implements SmartFactoryBean {

	private final Class serviceInterface;

	public FooBeanFactory(final Class serviceInterface) {
		this.serviceInterface = serviceInterface;
	}

	@Override
	public Object getObject() {
		AdvisedSupport advisedSupport = new AdvisedSupport();
		advisedSupport.setProxyTargetClass(false);
		advisedSupport.setInterfaces(serviceInterface);
		advisedSupport.addAdvice(new Interceptor());
		advisedSupport.setTargetClass(TargetClass.class);
		advisedSupport.setOptimize(true);
		advisedSupport.setOpaque(false);
		advisedSupport.setExposeProxy(false);
		advisedSupport.setFrozen(true);

		final AopProxy aopProxy = new FooProxyFactory().createAopProxy(advisedSupport);
		return aopProxy.getProxy();
	}

	@Override
	public Class<?> getObjectType() {
		return DifferentServiceInterface.class;
	}
}
