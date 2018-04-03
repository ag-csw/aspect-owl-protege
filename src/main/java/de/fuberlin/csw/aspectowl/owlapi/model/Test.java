package de.fuberlin.csw.aspectowl.owlapi.model;

import org.semanticweb.owlapi.model.*;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Optional;
import java.util.Set;

public class Test {
    public static void main(String[] args) {
        B b = new B();
        B b2 = new B();
        C c = new C();

        System.out.println(b.equals(b2));
        System.out.println(b2.equals(b));
        System.out.println(b.equals(c));
        System.out.println(b2.equals(c));
        System.out.println(c.equals(b));
        System.out.println(c.equals(b2));
    }

    static abstract class A {
        @Override
        public boolean equals(Object obj) {
            System.out.printf("I am a %s", getClass().getName());
            return (this.getClass() == obj.getClass());
        }
    }

    static class B extends A {

    }

    static class C extends A {

    }

}
