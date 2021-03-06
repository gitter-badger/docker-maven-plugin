package org.jolokia.docker.maven.util;

import java.util.ArrayList;
import java.util.List;

import edu.emory.mathcs.backport.java.util.Arrays;
import org.apache.maven.plugin.MojoExecutionException;
import org.junit.Test;

import static org.jolokia.docker.maven.util.StartOrderResolver.Resolvable;
import static org.junit.Assert.*;

/**
 * @author roland
 * @since 16.10.14
 */
public class StartOrderResolverTest {

    @Test
    public void simple() throws MojoExecutionException {
        checkData(new Object[][]{
                { new T[]{new T("1")}, new T[]{new T("1")}},
                { new T[]{new T("1", "2"), new T("2")}, new T[]{new T("2"), new T("1", "2")} },
                { new T[]{new T("1", "2", "3"), new T("2", "3"), new T("3")}, new T[]{new T("3"), new T("2", "3"), new T("1", "2", "3")} },
        });
    }

    @Test
    public void circularDep() throws MojoExecutionException {
        try {
            checkData(new Object[][] {
                    {new T[]{new T("1", "2"), new T("2", "1")}, new T[]{new T("1", "2"), new T("2", "1")}}
            });
            fail();
        } catch (MojoExecutionException exp) {

        }
    }

    private void checkData(Object[][] data) throws MojoExecutionException {
        for (Object[] aData : data) {
            Resolvable[] input = (Resolvable[]) aData[0];
            Resolvable[] expected = (Resolvable[]) aData[1];
            List<Resolvable> result = StartOrderResolver.resolve(Arrays.asList(input));
            assertArrayEquals(expected, new ArrayList(result).toArray());
        }
    }


    // ============================================================================

    private static class T implements Resolvable {

        private String id;
        private List<String> deps;

        private T(String id,String ... dep) {
            this.id = id;
            deps = new ArrayList<>();
            for (String d : dep) {
                deps.add(d);
            }
        }

        @Override
        public String getName() {
            return id;
        }

        @Override
        public String getAlias() {
            return null;
        }

        @Override
        public List<String> getDependencies() {
            return deps;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            T t = (T) o;

            if (id != null ? !id.equals(t.id) : t.id != null) return false;

            return true;
        }

        @Override
        public int hashCode() {
            return id != null ? id.hashCode() : 0;
        }

        @Override
        public String toString() {
            return "T{" +
                   "id='" + id + '\'' +
                   '}';
        }
    }
}
