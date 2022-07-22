package foo;

import static java.util.stream.Stream.concat;
import static java.util.stream.Stream.of;
import static org.springframework.asm.Opcodes.ACC_FINAL;
import static org.springframework.util.ReflectionUtils.findField;
import static org.springframework.util.ReflectionUtils.getField;
import static org.springframework.util.ReflectionUtils.makeAccessible;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.stream.Collectors;

import org.springframework.aop.framework.AdvisedSupport;
import org.springframework.aop.framework.AopConfigException;
import org.springframework.aop.framework.AopProxy;
import org.springframework.aop.framework.DefaultAopProxyFactory;
import org.springframework.asm.ClassVisitor;
import org.springframework.asm.MethodVisitor;
import org.springframework.cglib.core.ClassGenerator;
import org.springframework.cglib.core.DefaultNamingPolicy;
import org.springframework.cglib.core.GeneratorStrategy;
import org.springframework.cglib.core.NamingPolicy;
import org.springframework.cglib.core.Predicate;
import org.springframework.cglib.proxy.Enhancer;

class FooProxyFactory extends DefaultAopProxyFactory {

	private static final String CREATE_ENHANCER = "createEnhancer";

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
		final ProxyNamingPolicy namingPolicy = new ProxyNamingPolicy(config, "Impl");
		return new Enhancer() {
			@Override
			public void setStrategy(final GeneratorStrategy strategy) {
				super.setStrategy(new DelegatingGeneratorStrategy(strategy, config));
			}

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

		ProxyNamingPolicy(final AdvisedSupport advisedSupport, final String repositoryImplementationPostFix) {
			this.advisedSupport = advisedSupport;
			this.repositoryImplementationPostFix = repositoryImplementationPostFix;
		}

		@Override
		public String getClassName(final String prefix, final String source, final Object key, final Predicate names) {
			Class<?> proxiedInterface = this.advisedSupport.getProxiedInterfaces()[0];
			return super.getClassName(proxiedInterface.getName() + repositoryImplementationPostFix, source, key, names);
		}

		@Override
		protected String getTag() {
			return "BY_TEST_AT_CGLIB";
		}
	}

	private static final class DelegatingGeneratorStrategy implements GeneratorStrategy {

		private final GeneratorStrategy delegate;
		private final AdvisedSupport config;

		DelegatingGeneratorStrategy(final GeneratorStrategy delegate, final AdvisedSupport config) {
			this.delegate = delegate;
			this.config = config;
		}

		@Override
		public byte[] generate(final ClassGenerator classGenerator) throws Exception {
			return this.delegate.generate(new DelegatingClassGenerator(classGenerator, this.config));
		}

		@Override
		public boolean equals(final Object obj) {
			return this.delegate.equals(obj)
				|| (obj instanceof DelegatingGeneratorStrategy && delegate.equals(((DelegatingGeneratorStrategy) obj).delegate));
		}

		@Override
		public int hashCode() {
			return this.delegate.hashCode();
		}
	}

	private static class DelegatingClassGenerator implements ClassGenerator {

		private final ClassGenerator delegate;
		private final AdvisedSupport config;

		DelegatingClassGenerator(final ClassGenerator delegate, final AdvisedSupport config) {
			this.delegate = delegate;
			this.config = config;
		}

		@Override
		public void generateClass(final ClassVisitor classVisitor) throws Exception {
			this.delegate.generateClass(new DelegatingClassVisitor(classVisitor, this.config));
		}
	}

	private static class DelegatingClassVisitor extends ClassVisitor {

		private final AdvisedSupport config;

		DelegatingClassVisitor(final ClassVisitor delegate, final AdvisedSupport config) {
			super(getApi(delegate), delegate);
			this.config = config;
		}

		@Override
		public MethodVisitor visitMethod(final int access, final String name, final String desc, final String signature, final String[] exceptions) {
			int modifiers = isFinalMethod(name) ? access : access & ~ACC_FINAL;
			return super.visitMethod(modifiers, name, desc, signature, exceptions);
		}

		private boolean isFinalMethod(final String name) {
			Class<?> targetClass = config.getTargetClass();
			Class<?> proxiedInterface = config.getProxiedInterfaces()[0];
			return !concat(of(targetClass.getMethods()), of(proxiedInterface.getMethods())).map(Method::getName).collect(Collectors.toSet()).contains(name);
		}

		private static int getApi(final ClassVisitor delegate) {
			Field api = findField(delegate.getClass(), "api");
			makeAccessible(api);
			return (int) getField(api, delegate);
		}
	}
}
