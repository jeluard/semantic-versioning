/*
 * org.osjava.jardiff.DOMDiffHandler
 *
 * $Id: IOThread.java 1952 2005-08-28 18:03:41Z cybertiger $
 * $URL: https://svn.osjava.org/svn/osjava/trunk/jardiff/src/ava/org/osjava/jardiff/DOMDiffHandler.java $
 * $Rev: 1952 $
 * $Date: 2005-08-28 18:03:41 +0000 (Sun, 28 Aug 2005) $
 * $Author: cybertiger $
 *
 * Copyright (c) 2005, Antony Riley
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * + Redistributions of source code must retain the above copyright notice,
 *   this list of conditions and the following disclaimer.
 *
 * + Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * + Neither the name JarDiff nor the names of its contributors may
 *   be used to endorse or promote products derived from this software without
 *   specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package org.osjava.jardiff;

/* Not in 1.4.2 
import javax.xml.XMLConstants;
*/
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.Result;
import org.w3c.dom.*;

import org.objectweb.asm.Type;

/**
 * A specific type of DiffHandler which uses DOM to create an XML document
 * describing the changes in the diff.
 *
 * @author <a href="mailto:antony@cyberiantiger.org">Antony Riley</a>
 */
public class DOMDiffHandler implements DiffHandler
{
    /**
     * The XML namespace used.
     */
    public static final String XML_URI = "http://www.osjava.org/jardiff/0.1";

    /**
     * The javax.xml.transform.sax.Transformer used to convert
     * the DOM to text.
     */
    private final Transformer transformer;

    /**
     * Where we write the result to.
     */
    private final Result result;

    /**
     * The document object we're building
     */
    private final Document doc;

    /**
     * The current Node.
     */
    private Node currentNode;
    
    /**
     * Create a new DOMDiffHandler which writes to System.out
     *
     * @throws DiffException when there is an underlying exception, e.g.
     *                       writing to a file caused an IOException
     */
    public DOMDiffHandler() throws DiffException {
        try {
            TransformerFactory tf = TransformerFactory.newInstance();
            this.transformer = tf.newTransformer();
            this.result = new StreamResult(System.out);
            this.currentNode = null;
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);
            DocumentBuilder db = dbf.newDocumentBuilder();
            this.doc = db.newDocument();
        } catch (TransformerConfigurationException tce) {
            throw new DiffException(tce);
        } catch (ParserConfigurationException pce) {
            throw new DiffException(pce);
        }
    }
    
    /**
     * Create a new DOMDiffHandler with the specified Transformer and Result.
     * This method allows the user to choose what they are going to do with
     * the output in a flexible manner, allowing a stylesheet to be specified
     * and some result object.
     *
     * @param transformer The transformer to transform the output with.
     * @param result Where to put the result.
     */
    public DOMDiffHandler(Transformer transformer, Result result) 
        throws DiffException
    {
        try {
            this.transformer = transformer;
            this.result = result;
            this.currentNode = null;
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);
            DocumentBuilder db = dbf.newDocumentBuilder();
            this.doc = db.newDocument();
        } catch (ParserConfigurationException pce) {
            throw new DiffException(pce);
        }
    }
    
    /**
     * Start the diff.
     * This writes out the start of a &lt;diff&gt; node.
     *
     * @param oldJar ignored
     * @param newJar ignored
     * @throws DiffException when there is an underlying exception, e.g.
     *                       writing to a file caused an IOException
     */
    public void startDiff(String oldJar, String newJar) throws DiffException {
        Element tmp = doc.createElementNS(XML_URI, "diff");
        tmp.setAttribute( "old", oldJar);
        tmp.setAttribute( "new", newJar);
        doc.appendChild(tmp);
        currentNode = tmp;
    }

    /**
     * Start the list of old contents.
     *
     * @throws DiffException when there is an underlying exception, e.g.
     *                       writing to a file caused an IOException
     */
    public void startOldContents() throws DiffException {
        Element tmp = doc.createElementNS(XML_URI, "oldcontents");
        currentNode.appendChild(tmp);
        currentNode = tmp;
    }

    /**
     * Start the list of old contents.
     *
     * @throws DiffException when there is an underlying exception, e.g.
     *                       writing to a file caused an IOException
     */
    public void startNewContents() throws DiffException {
        Element tmp = doc.createElementNS(XML_URI, "newcontents");
        currentNode.appendChild(tmp);
        currentNode = tmp;
    }

    /**
     * Add a contained class.
     *
     * @param info information about a class
     * @throws DiffException when there is an underlying exception, e.g.
     *                       writing to a file caused an IOException
     */
    public void contains(ClassInfo info) throws DiffException {
        Element tmp = doc.createElementNS(XML_URI, "class");
        tmp.setAttribute("name", info.getName());
        currentNode.appendChild(tmp);
    }

    /**
     * End the list of old contents.
     *
     * @throws DiffException when there is an underlying exception, e.g.
     *                       writing to a file caused an IOException
     */
    public void endOldContents() throws DiffException {
        currentNode = currentNode.getParentNode();
    }

    /**
     * End the list of new contents.
     *
     * @throws DiffException when there is an underlying exception, e.g.
     *                       writing to a file caused an IOException
     */
    public void endNewContents() throws DiffException {
        currentNode = currentNode.getParentNode();
    }
    
    /**
     * Start the removed node.
     * This writes out a &lt;removed&gt; node.
     *
     * @throws DiffException when there is an underlying exception, e.g.
     *                       writing to a file caused an IOException
     */
    public void startRemoved() throws DiffException {
        Element tmp = doc.createElementNS(XML_URI, "removed");
        currentNode.appendChild(tmp);
        currentNode = tmp;
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
        writeClassInfo(info);
    }
    
    /**
     * End the removed section.
     * This closes the &lt;removed&gt; tag.
     *
     * @throws DiffException when there is an underlying exception, e.g.
     *                       writing to a file caused an IOException
     */
    public void endRemoved() throws DiffException {
        currentNode = currentNode.getParentNode();
    }
    
    /**
     * Start the added section.
     * This opens the &lt;added&gt; tag.
     *
     * @throws DiffException when there is an underlying exception, e.g.
     *                       writing to a file caused an IOException
     */
    public void startAdded() throws DiffException {
        Element tmp = doc.createElementNS(XML_URI, "added");
        currentNode.appendChild(tmp);
        currentNode = tmp;
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
        writeClassInfo(info);
    }
    
    /**
     * End the added section.
     * This closes the &lt;added&gt; tag.
     *
     * @throws DiffException when there is an underlying exception, e.g.
     *                       writing to a file caused an IOException
     */
    public void endAdded() throws DiffException {
        currentNode = currentNode.getParentNode();
    }
    
    /**
     * Start the changed section.
     * This writes out the &lt;changed&gt; node.
     *
     * @throws DiffException when there is an underlying exception, e.g.
     *                       writing to a file caused an IOException
     */
    public void startChanged() throws DiffException {
        Element tmp = doc.createElementNS(XML_URI, "changed");
        currentNode.appendChild(tmp);
        currentNode = tmp;
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
        Element tmp = doc.createElementNS(XML_URI, "classchanged");
        tmp.setAttribute( "name", internalName);
        currentNode.appendChild(tmp);
        currentNode = tmp;
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
        writeFieldInfo(info);
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
        writeMethodInfo(info);
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
        writeFieldInfo(info);
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
        writeMethodInfo(info);
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
        Node currentNode = this.currentNode;
        Element tmp = doc.createElementNS(XML_URI, "classchange");
        Element from = doc.createElementNS(XML_URI, "from");
        Element to = doc.createElementNS(XML_URI, "to");
        tmp.appendChild(from);
        tmp.appendChild(to);
        currentNode.appendChild(tmp);
        this.currentNode = from;
        writeClassInfo(oldInfo);
        this.currentNode = to;
        writeClassInfo(newInfo);
        this.currentNode = currentNode;
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
        Node currentNode = this.currentNode;
        Element tmp = doc.createElementNS(XML_URI, "fieldchange");
        Element from = doc.createElementNS(XML_URI, "from");
        Element to = doc.createElementNS(XML_URI, "to");
        tmp.appendChild(from);
        tmp.appendChild(to);
        currentNode.appendChild(tmp);
        this.currentNode = from;
        writeFieldInfo(oldInfo);
        this.currentNode = to;
        writeFieldInfo(newInfo);
        this.currentNode = currentNode;
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
        Node currentNode = this.currentNode;
        Element tmp = doc.createElementNS(XML_URI, "methodchange");
        Element from = doc.createElementNS(XML_URI, "from");
        Element to = doc.createElementNS(XML_URI, "to");
        tmp.appendChild(from);
        tmp.appendChild(to);
        currentNode.appendChild(tmp);
        this.currentNode = from;
        writeMethodInfo(oldInfo);
        this.currentNode = to;
        writeMethodInfo(newInfo);
        this.currentNode = currentNode;
    }
    
    /**
     * End the changed section for an individual class.
     * This closes the &lt;classchanged&gt; node.
     *
     * @throws DiffException when there is an underlying exception, e.g.
     *                       writing to a file caused an IOException
     */
    public void endClassChanged() throws DiffException {
        currentNode = currentNode.getParentNode();
    }
    
    /**
     * End the changed section.
     * This closes the &lt;changed&gt; node.
     *
     * @throws DiffException when there is an underlying exception, e.g.
     *                       writing to a file caused an IOException
     */
    public void endChanged() throws DiffException {
        currentNode = currentNode.getParentNode();
    }
    
    /**
     * End the diff.
     * This closes the &lt;diff&gt; node.
     *
     * @throws DiffException when there is an underlying exception, e.g.
     *                       writing to a file caused an IOException
     */
    public void endDiff() throws DiffException {
        DOMSource source = new DOMSource(doc);
        try {
        transformer.transform(source, result);
        } catch (TransformerException te) {
            throw new DiffException(te);
        }
    }
    
    /**
     * Write out information about a class.
     * This writes out a &lt;class&gt; node, which contains information about
     * what interfaces are implemented each in a &lt;implements&gt; node.
     *
     * @param info Info about the class to write out.
     */
    protected void writeClassInfo(ClassInfo info) {
        Node currentNode = this.currentNode;
        Element tmp = doc.createElementNS(XML_URI, "class");
        currentNode.appendChild(tmp);
        this.currentNode = tmp;
        addAccessFlags(info);
        if (info.getName() != null)
            tmp.setAttribute( "name",
                    info.getName());
        if (info.getSignature() != null)
            tmp.setAttribute( "signature",
                    info.getSignature());
        if (info.getSupername() != null)
            tmp.setAttribute( "superclass",
                              info.getSupername());
        String[] interfaces = info.getInterfaces();
        for (int i = 0; i < interfaces.length; i++) {
            Element iface = doc.createElementNS(XML_URI, "implements");
            tmp.appendChild(iface);
            iface.setAttribute( "name", 
                    interfaces[i]);
        }
        this.currentNode = currentNode;
    }
    
    /**
     * Write out information about a method.
     * This writes out a &lt;method&gt; node which contains information about
     * the arguments, the return type, and the exceptions thrown by the 
     * method.
     *
     * @param info Info about the method.
     */
    protected void writeMethodInfo(MethodInfo info) {
        Node currentNode = this.currentNode;
        Element tmp = doc.createElementNS(XML_URI, "method");
        currentNode.appendChild(tmp);
        this.currentNode = tmp;
        addAccessFlags(info);

        if (info.getName() != null)
            tmp.setAttribute( "name", info.getName());
        if (info.getSignature() != null)
            tmp.setAttribute( "signature", info.getSignature());
        if (info.getDesc() != null)
            addMethodNodes(info.getDesc());
        String[] exceptions = info.getExceptions();
        if (exceptions != null) {
            for (int i = 0; i < exceptions.length; i++) {
                Element excep = doc.createElementNS(XML_URI, "exception");
                excep.setAttribute( "name", exceptions[i]);
                tmp.appendChild(excep);
            }
        }
        this.currentNode = currentNode;
    }
    
    /**
     * Write out information about a field.
     * This writes out a &lt;field&gt; node with attributes describing the
     * field.
     *
     * @param info Info about the field.
     */
    protected void writeFieldInfo(FieldInfo info) {
        Node currentNode = this.currentNode;
        Element tmp = doc.createElementNS(XML_URI, "field");
        currentNode.appendChild(tmp);
        this.currentNode = tmp;
        addAccessFlags(info);

        if (info.getName() != null)
            tmp.setAttribute( "name", 
                    info.getName());
        if (info.getSignature() != null)
            tmp.setAttribute( "signature", 
                    info.getSignature());
        if (info.getValue() != null)
            tmp.setAttribute( "value",
                    info.getValue().toString());
        if (info.getDesc() != null)
            addTypeNode(info.getDesc());
        this.currentNode = currentNode;
    }
    
    /**
     * Add attributes describing some access flags.
     * This adds the attributes to the attr field.
     *
     * @see #attr
     * @param info Info describing the access flags.
     */
    protected void addAccessFlags(AbstractInfo info) {
        Element currentNode = (Element) this.currentNode;
        currentNode.setAttribute( "access", info.getAccessType());
        if (info.isAbstract())
            currentNode.setAttribute( "abstract", "yes");
        if (info.isAnnotation())
            currentNode.setAttribute( "annotation", "yes");
        if (info.isBridge())
            currentNode.setAttribute( "bridge", "yes");
        if (info.isDeprecated())
            currentNode.setAttribute( "deprecated", "yes");
        if (info.isEnum())
            currentNode.setAttribute( "enum", "yes");
        if (info.isFinal())
            currentNode.setAttribute( "final", "yes");
        if (info.isInterface())
            currentNode.setAttribute( "interface", "yes");
        if (info.isNative())
            currentNode.setAttribute( "native", "yes");
        if (info.isStatic())
            currentNode.setAttribute( "static", "yes");
        if (info.isStrict())
            currentNode.setAttribute( "strict", "yes");
        if (info.isSuper())
            currentNode.setAttribute( "super", "yes");
        if (info.isSynchronized())
            currentNode.setAttribute( "synchronized", "yes");
        if (info.isSynthetic())
            currentNode.setAttribute( "synthetic", "yes");
        if (info.isTransient())
            currentNode.setAttribute( "transient", "yes");
        if (info.isVarargs())
            currentNode.setAttribute( "varargs", "yes");
        if (info.isVolatile())
            currentNode.setAttribute( "volatile", "yes");
    }
    
    /**
     * Add the method nodes for the method descriptor.
     * This writes out an &lt;arguments&gt; node containing the 
     * argument types for the method, followed by a &lt;return&gt; node
     * containing the return type.
     *
     * @param desc The descriptor for the method to write out.
     */
    protected void addMethodNodes(String desc) {
        Type[] args = Type.getArgumentTypes(desc);
        Type ret = Type.getReturnType(desc);
        Node currentNode = this.currentNode;
        Element tmp = doc.createElementNS(XML_URI,"arguments");
        currentNode.appendChild(tmp);
        this.currentNode = tmp;
        for (int i = 0; i < args.length; i++)
            addTypeNode(args[i]);
        tmp = doc.createElementNS(XML_URI,"return");
        currentNode.appendChild(tmp);
        this.currentNode = tmp;
        addTypeNode(ret);
        this.currentNode = currentNode;
    }
    
    /**
     * Add a type node for the specified descriptor.
     *
     * @param desc A type descriptor.
     */
    protected void addTypeNode(String desc) {
        addTypeNode(Type.getType(desc));
    }
    
    /**
     * Add a type node for the specified type.
     * This writes out a &lt;type&gt; node with attributes describing
     * the type.
     *
     * @param type The type to describe.
     */
    protected void addTypeNode(Type type) {
        Element tmp = doc.createElementNS(XML_URI, "type");
        currentNode.appendChild(tmp);
        int i = type.getSort();
        if (i == Type.ARRAY) {
            tmp.setAttribute( "array", "yes");
            tmp.setAttribute( "dimensions",
                              "" + type.getDimensions());
            type = type.getElementType();
            i = type.getSort();
        }
        switch (i) {
        case Type.BOOLEAN:
            tmp.setAttribute( "primitive", "yes");
            tmp.setAttribute( "name", "boolean");
            break;
        case Type.BYTE:
            tmp.setAttribute( "primitive", "yes");
            tmp.setAttribute( "name", "byte");
            break;
        case Type.CHAR:
            tmp.setAttribute( "primitive", "yes");
            tmp.setAttribute( "name", "char");
            break;
        case Type.DOUBLE:
            tmp.setAttribute( "primitive", "yes");
            tmp.setAttribute( "name", "double");
            break;
        case Type.FLOAT:
            tmp.setAttribute( "primitive", "yes");
            tmp.setAttribute( "name", "float");
            break;
        case Type.INT:
            tmp.setAttribute( "primitive", "yes");
            tmp.setAttribute( "name", "int");
            break;
        case Type.LONG:
            tmp.setAttribute( "primitive", "yes");
            tmp.setAttribute( "name", "long");
            break;
        case Type.OBJECT:
            tmp.setAttribute( "name", type.getInternalName());
            break;
        case Type.SHORT:
            tmp.setAttribute( "primitive", "yes");
            tmp.setAttribute( "name", "short");
            break;
        case Type.VOID:
            tmp.setAttribute( "primitive", "yes");
            tmp.setAttribute( "name", "void");
            break;
        }
    }
}
