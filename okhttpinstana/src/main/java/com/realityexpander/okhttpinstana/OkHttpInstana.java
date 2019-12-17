package com.realityexpander.okhttpinstana;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.dynamic.loading.ClassReloadingStrategy;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatchers;

import static net.bytebuddy.matcher.ElementMatchers.isDeclaredBy;
import static net.bytebuddy.matcher.ElementMatchers.named;
import static net.bytebuddy.matcher.ElementMatchers.returns;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

/**
 * Created by Chris Athanas on 2019-12-14.
 */

class Source {
    public String hello(String name) { return null; }
}

class Target {
    public static String hello(String name) {
        return "Hello " + name + "!";
    }
}

public class OkHttpInstana {



    public void test() {

        String helloWorld = null;
        try {
            helloWorld = new ByteBuddy()
                    .subclass(Source.class)
                    .method(named("hello")).intercept(MethodDelegation.to(Target.class))
                    .make()
                    .load(getClass().getClassLoader())
                    .getLoaded()
                    .newInstance()
                    .hello("World");
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }

        System.out.println(helloWorld);

//        try {
//            String r = new ByteBuddy()
//                    .subclass(Foo.class)
//                    .method(named("sayHelloFoo")
//                            .and(isDeclaredBy(Foo.class)
//                                    .and(returns(String.class))))
//                    .intercept(MethodDelegation.to(Bar.class))
//                    .make()
//                    .load(getClass().getClassLoader())
//                    .getLoaded()
//                    .newInstance()
//                    .sayHelloFoo();
//
//            assertEquals( r, Bar.sayHelloBar());
//        } catch (IllegalAccessException e) {
//            e.printStackTrace();
//        } catch (InstantiationException e) {
//            e.printStackTrace();
//        }

//        Class<?> dynamicType = new ByteBuddy()
//                .subclass(Object.class)
//                .method(ElementMatchers.named("toString"))
//                .intercept(MethodDelegation.to(ToStringInterceptor.class))
//                .make()
//                .load(getClass().getClassLoader(),
//                        ClassLoadingStrategy.Default.WRAPPER)
//                .getLoaded();
//
//        try {
//            assertThat(dynamicType.newInstance().toString(),
//                    is("Hello World!"));
//        } catch (IllegalAccessException e) {
//            e.printStackTrace();
//        } catch (InstantiationException e) {
//            e.printStackTrace();
//        }

    }


}
