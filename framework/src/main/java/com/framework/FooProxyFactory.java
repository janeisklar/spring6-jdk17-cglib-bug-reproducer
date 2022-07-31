package com.framework;

import org.springframework.aop.framework.AdvisedSupport;
import org.springframework.aop.framework.AopConfigException;
import org.springframework.aop.framework.AopProxy;
import org.springframework.aop.framework.DefaultAopProxyFactory;
import org.springframework.cglib.core.DefaultNamingPolicy;
import org.springframework.cglib.core.NamingPolicy;
import org.springframework.cglib.core.Predicate;
import org.springframework.cglib.proxy.Enhancer;

class FooProxyFactory extends DefaultAopProxyFactory {

	private static final String CREATE_ENHANCER = "createEnhancer";

	private final NamingStrategyEnum strategy;

	public FooProxyFactory(final NamingStrategyEnum strategy) {
		this.strategy = strategy;
	}

	@Override
	public AopProxy createAopProxy(final AdvisedSupport config) throws AopConfigException {
		final AopProxy aopProxy = super.createAopProxy(config);

		Enhancer enhancer = new Enhancer();
		enhancer.setSuperclass(aopProxy.getClass());
		enhancer.setCallback((org.springframework.cglib.proxy.MethodInterceptor) (o, method, objects, methodProxy) -> {
			if (CREATE_ENHANCER.equals(method.getName())) {
				return getEnhancer(config);
			}
			return methodProxy.invokeSuper(o, objects);
		});
		enhancer.setInterfaces(new Class[]{AopProxy.class});

		return (AopProxy) enhancer.create(new Class[]{AdvisedSupport.class}, new Object[]{config});
	}

	private Enhancer getEnhancer(final AdvisedSupport config) {
		if (strategy == NamingStrategyEnum.DEFAULT) {
			return new Enhancer();
		}
		final ProxyNamingPolicy namingPolicy = new ProxyNamingPolicy(strategy, config, "Impl");
		return new Enhancer() {
			@Override
			public void setNamingPolicy(final NamingPolicy unused) {
				super.setNamingPolicy(namingPolicy);
			}

			@Override
			public NamingPolicy getNamingPolicy() {
				return namingPolicy;
			}
		};
	}

	private static class ProxyNamingPolicy extends DefaultNamingPolicy {
		private final AdvisedSupport advisedSupport;
		private final String repositoryImplementationPostFix;
		private final NamingStrategyEnum strategy;

		ProxyNamingPolicy(final NamingStrategyEnum strategy,
		                  final AdvisedSupport advisedSupport,
		                  final String repositoryImplementationPostFix) {
			this.strategy = strategy;
			this.advisedSupport = advisedSupport;
			this.repositoryImplementationPostFix = repositoryImplementationPostFix;
		}

		@Override
		public String getClassName(final String prefix, final String source, final Object key, final Predicate names) {
			final String name;
			if (strategy == NamingStrategyEnum.INTERFACE) {
				Class<?> proxiedInterface = this.advisedSupport.getProxiedInterfaces()[0];
				name = proxiedInterface.getName();
			} else {
				name = this.advisedSupport.getTargetSource().getTargetClass().getName();
			}
			return super.getClassName(name + repositoryImplementationPostFix, source, key, names);
		}

		@Override
		protected String getTag() {
			return "BY_TEST_AT_CGLIB";
		}
	}

}
