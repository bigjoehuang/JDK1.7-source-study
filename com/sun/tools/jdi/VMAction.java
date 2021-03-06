/*
 * Copyright (c) 1999, 2008, Oracle and/or its affiliates. All rights reserved.
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

package com.sun.tools.jdi;

import com.sun.jdi.*;
import java.util.EventObject;

/*
 * The name "action" is used to avoid confusion
 * with JDI events.
 */
class VMAction extends EventObject {
    // Event ids
    static final int VM_SUSPENDED = 1;
    static final int VM_NOT_SUSPENDED = 2;

    int id;
    ThreadReference resumingThread;

    VMAction(VirtualMachine vm, int id) {
        this(vm, null, id);
    }

    // For id = VM_NOT_SUSPENDED, if resumingThread != null, then it is
    // the only thread that is being resumed.
     VMAction(VirtualMachine vm,  ThreadReference resumingThread, int id) {
        super(vm);
        this.id = id;
        this.resumingThread = resumingThread;
    }
    VirtualMachine vm() {
        return (VirtualMachine)getSource();
    }
    int id() {
        return id;
    }

    ThreadReference resumingThread() {
        return resumingThread;
    }
}
