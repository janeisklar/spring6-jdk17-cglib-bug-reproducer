module consumer {
	requires library;
	requires spring.core;
	requires spring.aop;
	requires spring.beans;
	opens com.consumer to spring.core, spring.aop, spring.beans, library, framework;
}