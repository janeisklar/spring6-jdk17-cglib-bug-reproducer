module library {
	requires transitive framework;
	exports com.library;
	opens com.library to spring.core, spring.aop, spring.beans, framework;
}