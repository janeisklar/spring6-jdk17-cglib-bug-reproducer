
package foo;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.support.FactoryBeanRegistrySupport;

import bar.DifferentServiceInterface;

class BugTest {
	@Test
	void testServiceInterfaceInSamePackage() {
		final TestFactoryBeanRegistrySupport registrySupport = new TestFactoryBeanRegistrySupport();
		final FooBeanFactory beanFactory = new FooBeanFactory(ServiceInterface.class);

		assertDoesNotThrow(() -> registrySupport.getObjectFromFactoryBean(beanFactory, "foo", true));
	}

	@Test
	void testServiceInterfaceInDifferentPackage() {
		final TestFactoryBeanRegistrySupport registrySupport = new TestFactoryBeanRegistrySupport();
		final FooBeanFactory beanFactory = new FooBeanFactory(DifferentServiceInterface.class);
		assertThrows(BeanCreationException.class, () -> registrySupport.getObjectFromFactoryBean(beanFactory, "foo", true));
	}

	private static class TestFactoryBeanRegistrySupport extends FactoryBeanRegistrySupport {
		@Override
		public Object getObjectFromFactoryBean(final FactoryBean<?> factory, final String beanName, final boolean shouldPostProcess) {
			return super.getObjectFromFactoryBean(factory, beanName, shouldPostProcess);
		}
	}
}
