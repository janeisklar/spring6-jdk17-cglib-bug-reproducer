package com.framework;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

public class Interceptor implements MethodInterceptor {

	@Override
	public Object invoke(final MethodInvocation invocation) {
		return Boolean.TRUE;
	}
}
