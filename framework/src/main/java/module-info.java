module framework {
	requires spring.core;
	requires spring.aop;
	requires spring.beans;
	exports com.framework;
	exports com.framework.different;
	opens com.framework to spring.core, spring.aop, spring.beans;
	opens com.framework.different to spring.core, spring.aop, spring.beans;
}