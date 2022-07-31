
package com.consumer;

import static com.framework.NamingStrategyEnum.DEFAULT;
import static com.framework.NamingStrategyEnum.INTERFACE;
import static com.framework.NamingStrategyEnum.TARGET;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.function.Predicate;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.support.FactoryBeanRegistrySupport;
import org.springframework.cglib.proxy.Proxy;

import com.framework.FooBeanFactory;
import com.framework.FrameworkServiceInterface;
import com.framework.NamingStrategyEnum;
import com.framework.TargetClass;
import com.framework.different.DifferentFrameworkServiceInterface;
import com.library.LibraryServiceInterface;
import com.library.LibraryTargetClass;

class BugTest {

	private static Stream<Arguments> scenarioProvider() {
		return Stream
			.of(
				//         can
				//      create     can       naming
				//       proxy    call     strategy                 superclass                           proxy interface
				scenario(true,   true,     DEFAULT,         TargetClass.class,          FrameworkServiceInterface.class, FrameworkServiceInterface::foo),
				scenario(true,   true,     DEFAULT,         TargetClass.class, DifferentFrameworkServiceInterface.class, DifferentFrameworkServiceInterface::foo),
				scenario(false, false,     DEFAULT,         TargetClass.class,            LibraryServiceInterface.class, LibraryServiceInterface::foo),
				scenario(false, false,     DEFAULT,         TargetClass.class,           ConsumerServiceInterface.class, ConsumerServiceInterface::foo),
				scenario(true,   true,   INTERFACE,         TargetClass.class,          FrameworkServiceInterface.class, FrameworkServiceInterface::foo),
				scenario(true,   true,   INTERFACE,         TargetClass.class, DifferentFrameworkServiceInterface.class, DifferentFrameworkServiceInterface::foo),
				scenario(false, false,   INTERFACE,         TargetClass.class,            LibraryServiceInterface.class, LibraryServiceInterface::foo),
				scenario(false, false,   INTERFACE,         TargetClass.class,           ConsumerServiceInterface.class, ConsumerServiceInterface::foo),
				scenario(true,   true,      TARGET,         TargetClass.class,          FrameworkServiceInterface.class, FrameworkServiceInterface::foo),
				scenario(true,   true,      TARGET,         TargetClass.class, DifferentFrameworkServiceInterface.class, DifferentFrameworkServiceInterface::foo),
				scenario(false, false,      TARGET,         TargetClass.class,            LibraryServiceInterface.class, LibraryServiceInterface::foo),
				scenario(true,   true,      TARGET,               Proxy.class,           ConsumerServiceInterface.class, ConsumerServiceInterface::foo),
				scenario(true,   true,     DEFAULT,               Proxy.class,          FrameworkServiceInterface.class, FrameworkServiceInterface::foo),
				scenario(true,   true,     DEFAULT,               Proxy.class, DifferentFrameworkServiceInterface.class, DifferentFrameworkServiceInterface::foo),
				scenario(true,   true,     DEFAULT,               Proxy.class,            LibraryServiceInterface.class, LibraryServiceInterface::foo),
				scenario(true,   true,     DEFAULT,               Proxy.class,           ConsumerServiceInterface.class, ConsumerServiceInterface::foo),
				scenario(true,   true,   INTERFACE,               Proxy.class,          FrameworkServiceInterface.class, FrameworkServiceInterface::foo),
				scenario(true,   true,   INTERFACE,               Proxy.class, DifferentFrameworkServiceInterface.class, DifferentFrameworkServiceInterface::foo),
				scenario(true,   true,   INTERFACE,               Proxy.class,            LibraryServiceInterface.class, LibraryServiceInterface::foo),
				scenario(true,   true,   INTERFACE,               Proxy.class,           ConsumerServiceInterface.class, ConsumerServiceInterface::foo),
				scenario(true,   true,      TARGET,               Proxy.class,          FrameworkServiceInterface.class, FrameworkServiceInterface::foo),
				scenario(true,   true,      TARGET,               Proxy.class, DifferentFrameworkServiceInterface.class, DifferentFrameworkServiceInterface::foo),
				scenario(true,   true,      TARGET,               Proxy.class,            LibraryServiceInterface.class, LibraryServiceInterface::foo),
				scenario(true,   true,      TARGET,               Proxy.class,           ConsumerServiceInterface.class, ConsumerServiceInterface::foo),
				scenario(true,   true,      TARGET, ConsumerTargetClass.class,           ConsumerServiceInterface.class, ConsumerServiceInterface::foo),
				scenario(true,   true,     DEFAULT, ConsumerTargetClass.class,          FrameworkServiceInterface.class, FrameworkServiceInterface::foo),
				scenario(true,   true,     DEFAULT, ConsumerTargetClass.class, DifferentFrameworkServiceInterface.class, DifferentFrameworkServiceInterface::foo),
				scenario(true,   true,     DEFAULT, ConsumerTargetClass.class,            LibraryServiceInterface.class, LibraryServiceInterface::foo),
				scenario(true,   true,     DEFAULT, ConsumerTargetClass.class,           ConsumerServiceInterface.class, ConsumerServiceInterface::foo),
				scenario(true,   true,   INTERFACE, ConsumerTargetClass.class,          FrameworkServiceInterface.class, FrameworkServiceInterface::foo),
				scenario(true,   true,   INTERFACE, ConsumerTargetClass.class, DifferentFrameworkServiceInterface.class, DifferentFrameworkServiceInterface::foo),
				scenario(true,   true,   INTERFACE, ConsumerTargetClass.class,            LibraryServiceInterface.class, LibraryServiceInterface::foo),
				scenario(true,   true,   INTERFACE, ConsumerTargetClass.class,           ConsumerServiceInterface.class, ConsumerServiceInterface::foo),
				scenario(true,   true,      TARGET, ConsumerTargetClass.class,          FrameworkServiceInterface.class, FrameworkServiceInterface::foo),
				scenario(true,   true,      TARGET, ConsumerTargetClass.class, DifferentFrameworkServiceInterface.class, DifferentFrameworkServiceInterface::foo),
				scenario(true,   true,      TARGET, ConsumerTargetClass.class,            LibraryServiceInterface.class, LibraryServiceInterface::foo),
				scenario(true,   true,      TARGET, ConsumerTargetClass.class,           ConsumerServiceInterface.class, ConsumerServiceInterface::foo),
				scenario(false, false,      TARGET, LibraryTargetClass.class,           ConsumerServiceInterface.class, ConsumerServiceInterface::foo),
				scenario(false, false,     DEFAULT, LibraryTargetClass.class,          FrameworkServiceInterface.class, FrameworkServiceInterface::foo),
				scenario(false, false,     DEFAULT, LibraryTargetClass.class, DifferentFrameworkServiceInterface.class, DifferentFrameworkServiceInterface::foo),
				scenario(false, false,     DEFAULT, LibraryTargetClass.class,            LibraryServiceInterface.class, LibraryServiceInterface::foo),
				scenario(false, false,     DEFAULT, LibraryTargetClass.class,           ConsumerServiceInterface.class, ConsumerServiceInterface::foo),
				scenario(false, false,   INTERFACE, LibraryTargetClass.class,          FrameworkServiceInterface.class, FrameworkServiceInterface::foo),
				scenario(false, false,   INTERFACE, LibraryTargetClass.class, DifferentFrameworkServiceInterface.class, DifferentFrameworkServiceInterface::foo),
				scenario(false, false,   INTERFACE, LibraryTargetClass.class,            LibraryServiceInterface.class, LibraryServiceInterface::foo),
				scenario(false, false,   INTERFACE, LibraryTargetClass.class,           ConsumerServiceInterface.class, ConsumerServiceInterface::foo),
				scenario(false, false,      TARGET, LibraryTargetClass.class,          FrameworkServiceInterface.class, FrameworkServiceInterface::foo),
				scenario(false, false,      TARGET, LibraryTargetClass.class, DifferentFrameworkServiceInterface.class, DifferentFrameworkServiceInterface::foo),
				scenario(false, false,      TARGET, LibraryTargetClass.class,            LibraryServiceInterface.class, LibraryServiceInterface::foo),
				scenario(false, false,      TARGET, LibraryTargetClass.class,           ConsumerServiceInterface.class, ConsumerServiceInterface::foo)
			);
	}

	@ParameterizedTest(name = "{index} - [{2}] {3} {4}")
	@SuppressWarnings({"unchecked", "unused"})
	@MethodSource("scenarioProvider")
	<T> void testAllScenarios(final boolean canBeProxied,
	                          final boolean canBeCalled,
	                          final NamingStrategyEnum strategy,
	                          final Class<?> targetClass,
	                          final Class<T> serviceInterface,
	                          final Predicate<T> proxyCaller) {
		final TestFactoryBeanRegistrySupport registrySupport = new TestFactoryBeanRegistrySupport();
		final FooBeanFactory beanFactory = new FooBeanFactory(serviceInterface, strategy, targetClass);

		if (canBeProxied) {
			final T proxy = assertDoesNotThrow(() -> (T) registrySupport.getObjectFromFactoryBean(beanFactory, "foo", true));
			if (canBeCalled) {
				assertTrue(proxyCaller.test(proxy));
			} else {
				assertThrows(Exception.class, () -> proxyCaller.test(proxy));
			}
		} else {
			assertThrows(BeanCreationException.class, () -> registrySupport.getObjectFromFactoryBean(beanFactory, "foo", true));
		}
	}

	private static <T> Arguments scenario(final boolean canBeProxied,
	                                      final boolean canBeCalled,
	                                      final NamingStrategyEnum strategy,
																				final Class<?> targetClass,
	                                      final Class<T> serviceInterface,
	                                      final Predicate<T> proxyCaller) {
		return Arguments.of(canBeProxied, canBeCalled, strategy, targetClass, serviceInterface, proxyCaller);
	}

	private static class TestFactoryBeanRegistrySupport extends FactoryBeanRegistrySupport {
		@Override
		public Object getObjectFromFactoryBean(final FactoryBean<?> factory, final String beanName, final boolean shouldPostProcess) {
			return super.getObjectFromFactoryBean(factory, beanName, shouldPostProcess);
		}
	}
}
