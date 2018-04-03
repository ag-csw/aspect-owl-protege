package de.fuberlin.csw.aspectowl.util;

import de.fuberlin.csw.aspectowl.owlapi.model.*;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PointcutComparator implements Comparator<OWLAxiomPointcut> {

    private List<Class> pointcutTypes = Stream.of(
            OWLModulePointcut.class,
            DLQueryPointcut.class,
            SPARQLPointcut.class,
            OWLJoinPointAxiomPointcut.class).collect(Collectors.toList());

    @Override
    public int compare(OWLAxiomPointcut o1, OWLAxiomPointcut o2) {
        return 0;
    }
}
