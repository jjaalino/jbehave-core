package org.jbehave.core.util;

import java.util.Collection;
import java.util.List;

import net.sf.cotta.TDirectory;
import net.sf.cotta.TFileFactory;
import net.sf.cotta.memory.InMemoryFileSystem;

import org.jbehave.core.mock.Matcher;
import org.jbehave.core.mock.UsingMatchers;

public class BehaviourCollectorBehaviour extends UsingMatchers {
    private TDirectory directory;

    public void setUp() throws Exception {
        directory = new TFileFactory(new InMemoryFileSystem()).dir("/");
    }

    public void shouldCollectFilesMatchingNamePattern() throws Exception {
        directory.file("OneBehaviour.class").create();
        directory.file("TwoBehaviour.class").create();
        directory.file("RegularClass.class").create();
        List list = new BehaviourCollector(directory, "").collectNames();
        ensureThat(list, collectionContains("OneBehaviour"));
        ensureThat(list, collectionContains("TwoBehaviour"));
        ensureThat(list.size(), eq(2));
    }

    public void shouldConstructCorrectFullClassNameWithPackageName() throws Exception {
        directory.file("ThisBehaviour.class").create();
        List list = new BehaviourCollector(directory, "one.two.three").collectNames();
        ensureThat(list, collectionContains("one.two.three.ThisBehaviour"));
        ensureThat(list.size(), eq(1));
    }

    public void shouldTraverseToSubDirectories() throws Exception {
        directory.dir("directory").file("AnotherBehaviour.class").create();
        List list = new BehaviourCollector(directory, "").collectNames();
        ensureThat(list, collectionContains("directory.AnotherBehaviour"));
        ensureThat(list.size(), eq(1));
    }

    private Matcher collectionContains(final Object expected) {
        return new Matcher() {
            public boolean matches(Object arg) {
                return arg != null && ((Collection) arg).contains(expected);
            }

            public String toString() {
                return "collection should contains <" + expected + ">";
            }
        };
    }
}