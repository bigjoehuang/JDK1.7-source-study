/*
 * Copyright (c) 2007, 2008, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 */

package com.sun.tools.classfile;

/*
 *  <p><b>This is NOT part of any supported API.
 *  If you write code that depends on this, you do so at your own risk.
 *  This code and its internal interfaces are subject to change or
 *  deletion without notice.</b>
 */
public class DefaultAttribute extends Attribute {
    DefaultAttribute(ClassReader cr, int name_index, byte[] data) {
        super(name_index, data.length);
        info = data;
    }

    public DefaultAttribute(ConstantPool constant_pool, int name_index, byte[] info) {
        super(name_index, info.length);
        this.info = info;
    }

    public <R, P> R accept(Visitor<R, P> visitor, P p) {
        return visitor.visitDefault(this, p);
    }

    public final byte[] info;
}
