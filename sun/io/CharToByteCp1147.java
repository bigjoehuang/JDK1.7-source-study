/*
 * Copyright (c) 1998, 2003, Oracle and/or its affiliates. All rights reserved.
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

/*
 * (C) Copyright IBM Corp. 1998 - All Rights Reserved
 *
 */

package sun.io;

import sun.nio.cs.ext.IBM1147;

/**
 * Tables and data to convert Unicode to Cp1147
 *
 * @author  ConverterGenerator tool
 */

public class CharToByteCp1147 extends CharToByteSingleByte {

    private final static IBM1147 nioCoder = new IBM1147();

    public String getCharacterEncoding() {
        return "Cp1147";
    }

    public CharToByteCp1147() {
        super.mask1 = 0xFF00;
        super.mask2 = 0x00FF;
        super.shift = 8;
        super.index1 = nioCoder.getEncoderIndex1();
        super.index2 = nioCoder.getEncoderIndex2();
    }
}
