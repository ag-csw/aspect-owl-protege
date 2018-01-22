package de.fuberlin.csw.aspectowl.owlapi.model;

import org.semanticweb.owlapi.model.*;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Optional;
import java.util.Set;

public class Test {
    public static void main(String[] args) {
        try {
            Class cls = Test.class.getClassLoader().loadClass("org.semanticweb.owlapi.model.AxiomType");
            System.out.println(cls);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
