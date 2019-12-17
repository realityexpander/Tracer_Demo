package com.example.instanatracerdemo;

import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.SuperCall;

import java.util.concurrent.Callable;

public class MyServiceInterceptor {
    @RuntimeType
    public static Object intercept(@SuperCall Callable<?> zuper) throws Exception {
        return zuper.call();
    }
}