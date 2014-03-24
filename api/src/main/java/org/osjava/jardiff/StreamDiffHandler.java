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
package org.osjava.jardiff;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import org.objectweb.asm.Type;

/**
 * A specific type of DiffHandler which uses an OutputStream to create an 
 * XML document describing the changes in the diff.
 * This is needed for java 1.2 compatibility for the ant task.
 *
 * @author <a href="mailto:antony@cyberiantiger.org">Antony Riley</a>
 */
public class StreamDiffHandler implements DiffHandler
{
    /**
     * The XML namespace used.
     */
    public static final String XML_URI = "http://www.osjava.org/jardiff/0.1";

    /**
     * The javax.xml.transform.sax.Transformer used to convert
     * the DOM to text.
     */
    private final BufferedWriter out;

    /**
     * Create a new StreamDiffHandler which writes to System.out
     *
     * @throws DiffException when there is an underlying exception, e.g.
     *                       writing to a file caused an IOException
     */
    public StreamDiffHandler() throws DiffException {
        try {
            out = new BufferedWriter(
                    new OutputStreamWriter(System.out, "UTF-8")
                    );
        } catch (IOException ioe) {
            throw new DiffException(ioe);
        }
    }
    
    /**
     * Create a new StreamDiffHandler with the specified OutputStream.
     *
     * @param out Where to write output.
     */
    public StreamDiffHandler(OutputStream out)
        throws DiffException
    {
        try {
            this.out = new BufferedWriter(
                    new OutputStreamWriter(out, "UTF-8")
                    );
        } catch (IOException ioe) {
            throw new DiffException(ioe);
        }
    }
    
    /**
     * Start the diff.
     * This writes out the start of a &lt;diff&gt; node.
     *
     * @param oldJar name of old jar file.
     * @param newJar name of new jar file.
     * @throws DiffException when there is an underlying exception, e.g.
     *                       writing to a file caused an IOException
     */
    public void startDiff(String oldJar, String newJar) throws DiffException {
        try {
            out.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
            out.write("<diff xmlns=\"");
            out.write(xmlEscape(XML_URI));
            out.write("\" old=\"");
            out.write(xmlEscape(oldJar));
            out.write("\" new=\"");
            out.write(xmlEscape(newJar));
            out.write("\">");
        } catch (IOException ioe) {
            throw new DiffException(ioe);
        }
    }

    /**
     * Start the list of old contents.
     *
     * @throws DiffException when there is an underlying exception, e.g.
     *                       writing to a file caused an IOException
     */
    public void startOldContents() throws DiffException {
        try {
            out.write("<oldcontents>");
        } catch (IOException ioe) {
            throw new DiffException(ioe);
        }
    }

    /**
     * Start the list of old contents.
     *
     * @throws DiffException when there is an underlying exception, e.g.
     *                       writing to a file caused an IOException
     */
    public void startNewContents() throws DiffException {
        try {
            out.write("<newcontents>");
        } catch (IOException ioe) {
            throw new DiffException(ioe);
        }
    }

    /**
     * Add a contained class.
     *
     * @param info information about a class
     * @throws DiffException when there is an underlying exception, e.g.
     *                       writing to a file caused an IOException
     */
    public void contains(ClassInfo info) throws DiffException {
        try {
            out.write("<class name=\"");
            out.write(xmlEscape(info.getName()));
            out.write("\"/>");
        } catch (IOException ioe) {
            throw new DiffException(ioe);
        }
    }

    /**
     * End the list of old contents.
     *
     * @throws DiffException when there is an underlying exception, e.g.
     *                       writing to a file caused an IOException
     */
    public void endOldContents() throws DiffException {
        try {
            out.write("</oldcontents>");
        } catch (IOException ioe) {
            throw new DiffException(ioe);
        }
    }

    /**
     * End the list of new contents.
     *
     * @throws DiffException when there is an underlying exception, e.g.
     *                       writing to a file caused an IOException
     */
    public void endNewContents() throws DiffException {
        try {
            out.write("</newcontents>");
        } catch (IOException ioe) {
            throw new DiffException(ioe);
        }
    }
    
    /**
     * Start the removed node.
     * This writes out a &lt;removed&gt; node.
     *
     * @throws DiffException when there is an underlying exception, e.g.
     *                       writing to a file caused an IOException
     */
    public void startRemoved() throws DiffException {
        try {
            out.write("<removed>");
        } catch (IOException ioe) {
            throw new DiffException(ioe);
        }
    }
    
    /**
     * Write out class info for a removed class.
     * This writes out the nodes describing a class
     *
     * @param info The info to write out.
     * @throws DiffException when there is an underlying exception, e.g.
     *                       writing to a file caused an IOException
     */
    public void classRemoved(ClassInfo info) throws DiffException {
        try {
            writeClassInfo(info);
        } catch (IOException ioe) {
            throw new DiffException(ioe);
        }
    }
    
    /**
     * End the removed section.
     * This closes the &lt;removed&gt; tag.
     *
     * @throws DiffException when there is an underlying exception, e.g.
     *                       writing to a file caused an IOException
     */
    public void endRemoved() throws DiffException {
        try {
            out.write("</removed>");
        } catch (IOException ioe) {
            throw new DiffException(ioe);
        }
    }
    
    /**
     * Start the added section.
     * This opens the &lt;added&gt; tag.
     *
     * @throws DiffException when there is an underlying exception, e.g.
     *                       writing to a file caused an IOException
     */
    public void startAdded() throws DiffException {
        try {
            out.write("<added>");
        } catch (IOException ioe) {
            throw new DiffException(ioe);
        }
    }
    
    /**
     * Write out the class info for an added class.
     * This writes out the nodes describing an added class.
     *
     * @param info The class info describing the added class.
     * @throws DiffException when there is an underlying exception, e.g.
     *                       writing to a file caused an IOException
     */
    public void classAdded(ClassInfo info) throws DiffException {
        try {
            writeClassInfo(info);
        } catch (IOException ioe) {
            throw new DiffException(ioe);
        }
    }
    
    /**
     * End the added section.
     * This closes the &lt;added&gt; tag.
     *
     * @throws DiffException when there is an underlying exception, e.g.
     *                       writing to a file caused an IOException
     */
    public void endAdded() throws DiffException {
        try {
            out.write("</added>");
        } catch (IOException ioe) {
            throw new DiffException(ioe);
        }
    }
    
    /**
     * Start the changed section.
     * This writes out the &lt;changed&gt; node.
     *
     * @throws DiffException when there is an underlying exception, e.g.
     *                       writing to a file caused an IOException
     */
    public void startChanged() throws DiffException {
        try {
            out.write("<changed>");
        } catch (IOException ioe) {
            throw new DiffException(ioe);
        }
    }
    
    /**
     * Start a changed section for an individual class.
     * This writes out an &lt;classchanged&gt; node with the real class
     * name as the name attribute.
     *
     * @param internalName the internal name of the class that has changed.
     * @throws DiffException when there is an underlying exception, e.g.
     *                       writing to a file caused an IOException
     */
    public void startClassChanged(String internalName) throws DiffException 
    {
        try {
            out.write("<classchanged name=\"");
            out.write(xmlEscape(internalName));
            out.write("\">");
        } catch (IOException ioe) {
            throw new DiffException(ioe);
        }
    }
    
    /**
     * Write out info about a removed field.
     * This just writes out the field info, it will be inside a start/end
     * removed section.
     *
     * @param info Info about the field that's been removed.
     * @throws DiffException when there is an underlying exception, e.g.
     *                       writing to a file caused an IOException
     */
    public void fieldRemoved(FieldInfo info) throws DiffException {
        try {
            writeFieldInfo(info);
        } catch (IOException ioe) {
            throw new DiffException(ioe);
        }
    }
    
    /**
     * Write out info about a removed method.
     * This just writes out the method info, it will be inside a start/end 
     * removed section.
     *
     * @param info Info about the method that's been removed.
     * @throws DiffException when there is an underlying exception, e.g.
     *                       writing to a file caused an IOException
     */
    public void methodRemoved(MethodInfo info) throws DiffException {
        try {
            writeMethodInfo(info);
        } catch (IOException ioe) {
            throw new DiffException(ioe);
        }
    }
    
    /**
     * Write out info about an added field.
     * This just writes out the field info, it will be inside a start/end 
     * added section.
     *
     * @param info Info about the added field.
     * @throws DiffException when there is an underlying exception, e.g.
     *                       writing to a file caused an IOException
     */
    public void fieldAdded(FieldInfo info) throws DiffException {
        try {
            writeFieldInfo(info);
        } catch (IOException ioe) {
            throw new DiffException(ioe);
        }
    }
    
    /**
     * Write out info about a added method.
     * This just writes out the method info, it will be inside a start/end
     * added section.
     *
     * @param info Info about the added method.
     * @throws DiffException when there is an underlying exception, e.g.
     *                       writing to a file caused an IOException
     */
    public void methodAdded(MethodInfo info) throws DiffException {
        try {
            writeMethodInfo(info);
        } catch (IOException ioe) {
            throw new DiffException(ioe);
        }
    }
    
    /**
     * Write out info aboout a changed class.
     * This writes out a &lt;classchange&gt; node, followed by a 
     * &lt;from&gt; node, with the old information about the class
     * followed by a &lt;to&gt; node with the new information about the
     * class.
     *
     * @param oldInfo Info about the old class.
     * @param newInfo Info about the new class.
     * @throws DiffException when there is an underlying exception, e.g.
     *                       writing to a file caused an IOException
     */
    public void classChanged(ClassInfo oldInfo, ClassInfo newInfo)
        throws DiffException 
    {
        try {
            out.write("<classchange><from>");
            writeClassInfo(oldInfo);
            out.write("</from><to>");
            writeClassInfo(newInfo);
            out.write("</to></classchange>");
        } catch (IOException ioe) {
            throw new DiffException(ioe);
        }
    }
    
    /**
     * Write out info aboout a changed field.
     * This writes out a &lt;fieldchange&gt; node, followed by a 
     * &lt;from&gt; node, with the old information about the field
     * followed by a &lt;to&gt; node with the new information about the
     * field.
     *
     * @param oldInfo Info about the old field.
     * @param newInfo Info about the new field.
     * @throws DiffException when there is an underlying exception, e.g.
     *                       writing to a file caused an IOException
     */
    public void fieldChanged(FieldInfo oldInfo, FieldInfo newInfo)
        throws DiffException 
    {
        try {
            out.write("<fieldchange><from>");
            writeFieldInfo(oldInfo);
            out.write("</from><to>");
            writeFieldInfo(newInfo);
            out.write("</to></fieldchange>");
        } catch (IOException ioe) {
            throw new DiffException(ioe);
        }
    }
    
    /**
     * Write out info aboout a changed method.
     * This writes out a &lt;methodchange&gt; node, followed by a 
     * &lt;from&gt; node, with the old information about the method
     * followed by a &lt;to&gt; node with the new information about the
     * method.
     *
     * @param oldInfo Info about the old method.
     * @param newInfo Info about the new method.
     * @throws DiffException when there is an underlying exception, e.g.
     *                       writing to a file caused an IOException
     */
    public void methodChanged(MethodInfo oldInfo, MethodInfo newInfo)
        throws DiffException
    {
        try {
            out.write("<methodchange><from>");
            writeMethodInfo(oldInfo);
            out.write("</from><to>");
            writeMethodInfo(newInfo);
            out.write("</to></methodchange>");
        } catch (IOException ioe) {
            throw new DiffException(ioe);
        }
    }
    
    /**
     * End the changed section for an individual class.
     * This closes the &lt;classchanged&gt; node.
     *
     * @throws DiffException when there is an underlying exception, e.g.
     *                       writing to a file caused an IOException
     */
    public void endClassChanged() throws DiffException {
        try {
            out.write("</classchanged>");
        } catch (IOException ioe) {
            throw new DiffException(ioe);
        }
    }
    
    /**
     * End the changed section.
     * This closes the &lt;changed&gt; node.
     *
     * @throws DiffException when there is an underlying exception, e.g.
     *                       writing to a file caused an IOException
     */
    public void endChanged() throws DiffException {
        try {
            out.write("</changed>");
        } catch (IOException ioe) {
            throw new DiffException(ioe);
        }
    }
    
    /**
     * End the diff.
     * This closes the &lt;diff&gt; node.
     *
     * @throws DiffException when there is an underlying exception, e.g.
     *                       writing to a file caused an IOException
     */
    public void endDiff() throws DiffException {
        try {
            out.write("</diff>");
            out.newLine();
            out.close();
        } catch (IOException ioe) {
            throw new DiffException(ioe);
        }
    }
    
    /**
     * Write out information about a class.
     * This writes out a &lt;class&gt; node, which contains information about
     * what interfaces are implemented each in a &lt;implements&gt; node.
     *
     * @param info Info about the class to write out.
     * @throws IOException when there is an underlying IOException.
     */
    protected void writeClassInfo(ClassInfo info) throws IOException {
        out.write("<class");
        addAccessFlags(info);
        if(info.getName() != null) {
            out.write(" name=\"");
            out.write(xmlEscape(info.getName()));
            out.write("\"");
        }
        if(info.getSignature() != null) {
            out.write(" signature=\"");
            out.write(xmlEscape(info.getSignature()));
            out.write("\"");
        }
        if(info.getSupername() != null) {
            out.write(" superclass=\"");
            out.write(xmlEscape(info.getSupername()));
            out.write("\">");
        }
        String[] interfaces = info.getInterfaces();
        for (int i = 0; i < interfaces.length; i++) {
            out.write("<implements name=\"");
            out.write(xmlEscape(interfaces[i]));
            out.write("\"/>");
        }
        out.write("</class>");
    }
    
    /**
     * Write out information about a method.
     * This writes out a &lt;method&gt; node which contains information about
     * the arguments, the return type, and the exceptions thrown by the 
     * method.
     *
     * @param info Info about the method.
     * @throws IOException when there is an underlying IOException.
     */
    protected void writeMethodInfo(MethodInfo info) throws IOException {
        out.write("<method");

        addAccessFlags(info);

        if (info.getName() != null) {
            out.write(" name=\"");
            out.write(xmlEscape(info.getName()));
            out.write("\"");
        }
        if (info.getSignature() != null) {
            out.write(" signature=\"");
            out.write(xmlEscape(info.getSignature()));
            out.write("\"");
        }
        out.write(">");
        if (info.getDesc() != null) {
            addMethodNodes(info.getDesc());
        }
        String[] exceptions = info.getExceptions();
        if (exceptions != null) {
            for (int i = 0; i < exceptions.length; i++) {
                out.write("<exception name=\"");
                out.write(xmlEscape(exceptions[i]));
                out.write("\"/>");
            }
        }
        out.write("</method>");
    }
    
    /**
     * Write out information about a field.
     * This writes out a &lt;field&gt; node with attributes describing the
     * field.
     *
     * @param info Info about the field.
     * @throws IOException when there is an underlying IOException.
     */
    protected void writeFieldInfo(FieldInfo info) throws IOException {
        out.write("<field");

        addAccessFlags(info);

        if(info.getName() != null) {
            out.write(" name=\"");
            out.write(xmlEscape(info.getName()));
            out.write("\"");
        }
        if (info.getSignature() != null) {
            out.write(" signature=\"");
            out.write(xmlEscape(info.getSignature()));
            out.write("\"");
        }
        if (info.getValue() != null) {
            out.write(" value=\"");
            out.write(xmlEscape(info.getValue().toString()));
            out.write("\"");
        }
        out.write(">");
        if (info.getDesc() != null) {
            addTypeNode(info.getDesc());
        }
        out.write("</field>");
    }
    
    /**
     * Add attributes describing some access flags.
     * This adds the attributes to the attr field.
     *
     * @param info Info describing the access flags.
     * @throws IOException when there is an underlying IOException.
     */
    protected void addAccessFlags(AbstractInfo info) throws IOException {
        out.write(" access=\"");
        // Doesn't need escaping.
        out.write(info.getAccessType());
        out.write("\"");
        if (info.isAbstract())
            out.write(" abstract=\"yes\"");
        if (info.isAnnotation())
            out.write(" annotation=\"yes\"");
        if (info.isBridge())
            out.write(" bridge=\"yes\"");
        if (info.isDeprecated())
            out.write(" deprecated=\"yes\"");
        if (info.isEnum())
            out.write(" enum=\"yes\"");
        if (info.isFinal())
            out.write(" final=\"yes\"");
        if (info.isInterface())
            out.write(" interface=\"yes\"");
        if (info.isNative())
            out.write(" native=\"yes\"");
        if (info.isStatic())
            out.write(" static=\"yes\"");
        if (info.isStrict())
            out.write(" strict=\"yes\"");
        if (info.isSuper())
            out.write(" super=\"yes\"");
        if (info.isSynchronized())
            out.write(" synchronized=\"yes\"");
        if (info.isSynthetic())
            out.write(" synthetic=\"yes\"");
        if (info.isTransient())
            out.write(" transient=\"yes\"");
        if (info.isVarargs())
            out.write(" varargs=\"yes\"");
        if (info.isVolatile())
            out.write(" volatile=\"yes\"");
    }
    
    /**
     * Add the method nodes for the method descriptor.
     * This writes out an &lt;arguments&gt; node containing the 
     * argument types for the method, followed by a &lt;return&gt; node
     * containing the return type.
     *
     * @param desc The descriptor for the method to write out.
     * @throws IOException when there is an underlying IOException.
     */
    protected void addMethodNodes(String desc) throws IOException {
        Type[] args = Type.getArgumentTypes(desc);
        Type ret = Type.getReturnType(desc);
        out.write("<arguments>");
        for (int i = 0; i < args.length; i++)
            addTypeNode(args[i]);
        out.write("</arguments>");
        out.write("<return>");
        addTypeNode(ret);
        out.write("</return>");
    }
    
    /**
     * Add a type node for the specified descriptor.
     *
     * @param desc A type descriptor.
     * @throws IOException when there is an underlying IOException.
     */
    protected void addTypeNode(String desc) throws IOException {
        addTypeNode(Type.getType(desc));
    }
    
    /**
     * Add a type node for the specified type.
     * This writes out a &lt;type&gt; node with attributes describing
     * the type.
     *
     * @param type The type to describe.
     * @throws IOException when there is an underlying IOException.
     */
    protected void addTypeNode(Type type) throws IOException {
        out.write("<type");
        int i = type.getSort();
        if (i == Type.ARRAY) {
            out.write(" array=\"yes\" dimensions=\"");
            out.write(""+type.getDimensions());
            out.write("\"");
            type = type.getElementType();
            i = type.getSort();
        }
        switch (i) {
        case Type.BOOLEAN:
            out.write(" primitive=\"yes\" name=\"boolean\"/>");
            break;
        case Type.BYTE:
            out.write(" primitive=\"yes\" name=\"byte\"/>");
            break;
        case Type.CHAR:
            out.write(" primitive=\"yes\" name=\"char\"/>");
            break;
        case Type.DOUBLE:
            out.write(" primitive=\"yes\" name=\"double\"/>");
            break;
        case Type.FLOAT:
            out.write(" primitive=\"yes\" name=\"float\"/>");
            break;
        case Type.INT:
            out.write(" primitive=\"yes\" name=\"int\"/>");
            break;
        case Type.LONG:
            out.write(" primitive=\"yes\" name=\"long\"/>");
            break;
        case Type.OBJECT:
            out.write(" name=\"");
            out.write(xmlEscape(type.getInternalName()));
            out.write("\"/>");
            break;
        case Type.SHORT:
            out.write(" primitive=\"yes\" name=\"short\"/>");
            break;
        case Type.VOID:
            out.write(" primitive=\"yes\" name=\"void\"/>");
            break;
        }
    }

    /**
     * Escape some text into a format suitable for output as xml.
     *
     * @param str the text to format
     * @return the formatted text
     */
    private final String xmlEscape(final String str) {
        StringBuffer ret = new StringBuffer(str.length());
        for(int i=0;i<str.length();i++) {
            char ch = str.charAt(i);
            switch(ch) {
                case '<':
                    ret.append("&lt;");
                    break;
                case '&':
                    ret.append("&amp;");
                    break;
                case '>':
                    ret.append("&gt;");
                    break;
                default:
                    ret.append(ch);
            }
        }
        return ret.toString();
    }
}
