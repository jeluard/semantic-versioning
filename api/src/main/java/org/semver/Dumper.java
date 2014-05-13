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
package org.semver;

import java.io.PrintStream;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.osjava.jardiff.AbstractInfo;
import org.osjava.jardiff.ClassInfo;
import org.semver.Delta.Difference;
import org.semver.Delta.Change;

/**
 *
 * Helper methods to dump {@link Delta}.
 *
 */
public class Dumper {

    private Dumper() {
    }

    protected static String extractActionType(final Difference difference) {
        final String actionType = difference.getClass().getSimpleName();
        return actionType.endsWith("e")?actionType+"d":actionType+"ed";
    }

    protected static String extractInfoType(final AbstractInfo info) {
        final String simpleClassName = info.getClass().getSimpleName();
        return simpleClassName.substring(0, simpleClassName.indexOf("Info"));
    }

    protected static String extractDetails(final Difference difference) {
        if (difference instanceof Change) {
            final Change change = (Change) difference;
            return extractDetails(difference.getInfo())+", access "+extractAccessDetails(difference.getInfo(), change.getModifiedInfo());
        } else {
            return extractDetails(difference.getInfo())+", access "+extractAccessDetails(difference.getInfo());
        }
    }

    protected static String extractDetails(final AbstractInfo info) {
        final StringBuilder builder = new StringBuilder();
        if (!(info instanceof ClassInfo)) {
            builder.append(info.getName());
            if( null != info.getSignature() ) {
                builder.append(", sig ").append(info.getSignature());
            }
            if( null != info.getDesc() ) {
                builder.append(", desc ").append(info.getDesc());
            }
        }
        return builder.toString();
    }

    protected static void accumulateAccessDetails(final String access, final boolean previousAccess, final boolean currentAccess, final List<String> added, final List<String> removed) {
        if (previousAccess != currentAccess) {
            if (previousAccess) {
                removed.add(access);
            } else {
                added.add(access);
            }
        }
    }

    protected static String extractAccessDetails(final AbstractInfo previousInfo, final AbstractInfo currentInfo) {
        final List<String> added = new LinkedList<String>();
        final List<String> removed = new LinkedList<String>();
        accumulateAccessDetails("abstract", previousInfo.isAbstract(), currentInfo.isAbstract(), added, removed);
        accumulateAccessDetails("annotation", previousInfo.isAnnotation(), currentInfo.isAnnotation(), added, removed);
        accumulateAccessDetails("bridge", previousInfo.isBridge(), currentInfo.isBridge(), added, removed);
        accumulateAccessDetails("enum", previousInfo.isEnum(), currentInfo.isEnum(), added, removed);
        accumulateAccessDetails("final", previousInfo.isFinal(), currentInfo.isFinal(), added, removed);
        accumulateAccessDetails("interface", previousInfo.isInterface(), currentInfo.isInterface(), added, removed);
        accumulateAccessDetails("native", previousInfo.isNative(), currentInfo.isNative(), added, removed);
        accumulateAccessDetails("package-private", previousInfo.isPackagePrivate(), currentInfo.isPackagePrivate(), added, removed);
        accumulateAccessDetails("private", previousInfo.isPrivate(), currentInfo.isPrivate(), added, removed);
        accumulateAccessDetails("protected", previousInfo.isProtected(), currentInfo.isProtected(), added, removed);
        accumulateAccessDetails("public", previousInfo.isPublic(), currentInfo.isPublic(), added, removed);
        accumulateAccessDetails("static", previousInfo.isStatic(), currentInfo.isStatic(), added, removed);
        accumulateAccessDetails("strict", previousInfo.isStrict(), currentInfo.isStrict(), added, removed);
        accumulateAccessDetails("super", previousInfo.isSuper(), currentInfo.isSuper(), added, removed);
        accumulateAccessDetails("synchronized", previousInfo.isSynchronized(), currentInfo.isSynchronized(), added, removed);
        accumulateAccessDetails("synthetic", previousInfo.isSynthetic(), currentInfo.isSynthetic(), added, removed);
        accumulateAccessDetails("transcient", previousInfo.isTransient(), currentInfo.isTransient(), added, removed);
        accumulateAccessDetails("varargs", previousInfo.isVarargs(), currentInfo.isVarargs(), added, removed);
        accumulateAccessDetails("volatile", previousInfo.isVolatile(), currentInfo.isVolatile(), added, removed);
        final StringBuilder details = new StringBuilder();
        if (!added.isEmpty()) {
            details.append("added: ");
            for (final String access : added) {
                details.append(access).append(" ");
            }
        }
        if (!removed.isEmpty()) {
            details.append("removed: ");
            for (final String access : removed) {
                details.append(access).append(" ");
            }
        }
        return details.toString().trim();
    }

    protected static void accumulateAccessDetails(final String access, final boolean hasAccess, final List<String> accessList) {
        if (hasAccess) {
            accessList.add(access);
        }
    }

    protected static String extractAccessDetails(final AbstractInfo info) {
        final List<String> accessList = new LinkedList<String>();
        accumulateAccessDetails("abstract", info.isAbstract(), accessList);
        accumulateAccessDetails("annotation", info.isAnnotation(), accessList);
        accumulateAccessDetails("bridge", info.isBridge(), accessList);
        accumulateAccessDetails("enum", info.isEnum(), accessList);
        accumulateAccessDetails("final", info.isFinal(), accessList);
        accumulateAccessDetails("interface", info.isInterface(), accessList);
        accumulateAccessDetails("native", info.isNative(), accessList);
        accumulateAccessDetails("package-private", info.isPackagePrivate(), accessList);
        accumulateAccessDetails("private", info.isPrivate(), accessList);
        accumulateAccessDetails("protected", info.isProtected(), accessList);
        accumulateAccessDetails("public", info.isPublic(), accessList);
        accumulateAccessDetails("static", info.isStatic(), accessList);
        accumulateAccessDetails("strict", info.isStrict(), accessList);
        accumulateAccessDetails("super", info.isSuper(), accessList);
        accumulateAccessDetails("synchronized", info.isSynchronized(), accessList);
        accumulateAccessDetails("synthetic", info.isSynthetic(), accessList);
        accumulateAccessDetails("transcient", info.isTransient(), accessList);
        accumulateAccessDetails("varargs", info.isVarargs(), accessList);
        accumulateAccessDetails("volatile", info.isVolatile(), accessList);
        final StringBuilder details = new StringBuilder();
        if (!accessList.isEmpty()) {
            for (final String access : accessList) {
                details.append(access).append(" ");
            }
        }
        return details.toString().trim();
    }

    /**
     *
     * Dumps on {@link System#out} all differences.
     *
     * @param differences
     */
    public static void dump(final Delta delta) {
        dump(delta, System.out);
    }

    /**
     *
     * Dumps on <code>out</code> all differences.
     *
     * @param differences
     * @param out
     */
    public static void dump(final Delta delta, final PrintStream out) {
        final List<Difference> sortedDifferences = new LinkedList<Difference>(delta.getDifferences());
        Collections.sort(sortedDifferences);
        String currentClassName = "";
        for (final Difference difference : sortedDifferences) {
            if (!currentClassName.equals(difference.getClassName())) {
                out.println("Class "+difference.getClassName());
            }
            out.println(" "+extractActionType(difference)+" "+extractInfoType(difference.getInfo())+" "+extractDetails(difference));
            currentClassName = difference.getClassName();
        }
    }

}
