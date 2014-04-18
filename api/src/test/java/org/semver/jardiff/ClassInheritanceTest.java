/**
 * Copyright 2012-2014 Julien Eluard and contributors
 * This project includes software developed by Julien Eluard: https://github.com/jeluard/
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.semver.jardiff;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Test;
import org.objectweb.asm.ClassReader;
import org.osjava.jardiff.ClassInfo;
import org.osjava.jardiff.DiffCriteria;
import org.osjava.jardiff.DiffHandler;
import org.osjava.jardiff.JarDiff;
import org.osjava.jardiff.SimpleDiffCriteria;
import org.semver.Delta;
import org.semver.Delta.Change;

public class ClassInheritanceTest {

  public static abstract class InheritanceRoot {
    public abstract void aMethod();
  }

  public static class DirectDescendant extends InheritanceRoot {
    @Override
    public void aMethod() {}
  }

  public static class ClassA extends InheritanceRoot {
    @Override
    public void aMethod() {}
  }

  public static class ClassB extends DirectDescendant {
  }

  @Test
  public void shouldInheritedMethodMatchImplementedMethod() throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException, IOException {
    /**
     * The situation we are testing is as follows:
     * Abstract class InheritanceRoot is initially implemented directly by ClassA.
     * ClassA is later modified to extend another implementation of InheritanceRoot
     * and the methods required by InheritanceRoot are now removed from ClassA directly,
     * and instead inherited from the new parent, DirectDescendant. For the purposes of
     * this test, this new ClassA is represented by ClassB (as we can't have the same
     * class declared twice in a test -- in real life, this would both be ClassA's,
     * in different jars).
     */
    Map<String, ClassInfo> oldClassInfoMap = new HashMap<String, ClassInfo>();
    Map<String, ClassInfo> newClassInfoMap = new HashMap<String, ClassInfo>();
    JarDiff jd = new JarDiff();
    Method loadInfoMethod = JarDiff.class.getDeclaredMethod("loadClassInfo", ClassReader.class);
    Method diffMethod = JarDiff.class.getDeclaredMethod("diff", DiffHandler.class, DiffCriteria.class,
        String.class, String.class,
        Map.class, Map.class);
    diffMethod.setAccessible(true);
    loadInfoMethod.setAccessible(true);
    addClassInfo(oldClassInfoMap, ClassA.class, jd, loadInfoMethod);
    addClassInfo(oldClassInfoMap, DirectDescendant.class, jd, loadInfoMethod);
    addClassInfo(oldClassInfoMap, InheritanceRoot.class, jd, loadInfoMethod);
    addClassInfo(newClassInfoMap, ClassB.class, jd, loadInfoMethod);
    addClassInfo(newClassInfoMap, DirectDescendant.class, jd, loadInfoMethod);
    addClassInfo(newClassInfoMap, InheritanceRoot.class, jd, loadInfoMethod);

    // Make B look like A
    ClassInfo a = oldClassInfoMap.get("org/semver/jardiff/ClassInheritanceTest$ClassA");
    ClassInfo b = newClassInfoMap.get("org/semver/jardiff/ClassInheritanceTest$ClassB");
    newClassInfoMap.put(a.getName(), new ClassInfo(b.getVersion(), b.getAccess(), a.getName(),
            b.getSignature(), b.getSupername(), b.getInterfaces(),
            b.getMethodMap(), b.getFieldMap()));
    newClassInfoMap.remove(b.getName());
    DifferenceAccumulatingHandler handler = new DifferenceAccumulatingHandler();
    diffMethod.invoke(jd, handler, new SimpleDiffCriteria(),
        "0.1.0", "0.2.0", oldClassInfoMap, newClassInfoMap);

    for (Delta.Difference d: handler.getDelta().getDifferences()) {
      System.err.println(d.getClassName() + " : " + d.getClass().getName()
          + " : " + d.getInfo().getName() + " : " + d.getInfo().getAccessType());
      if (d instanceof Change) {
        System.err.println("  : " + ((Change) d).getModifiedInfo().getName());
      }
    }
    Assert.assertEquals("differences found", 1, handler.getDelta().getDifferences().size());
  }

  private void addClassInfo(Map<String, ClassInfo> classMap, Class klass, JarDiff jd,
      Method loadInfoMethod) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, IOException {
    ClassInfo classInfo = (ClassInfo) loadInfoMethod.invoke(jd, new ClassReader(klass.getName()));
    classMap.put(classInfo.getName(), classInfo);
  }

}
