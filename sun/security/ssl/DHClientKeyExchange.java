/*
 * Copyright (c) 1997, 2007, Oracle and/or its affiliates. All rights reserved.
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


package sun.security.ssl;

import java.io.IOException;
import java.io.PrintStream;
import java.math.BigInteger;


/*
 * Message used by clients to send their Diffie-Hellman public
 * keys to servers.
 *
 * @author David Brownell
 */
final class DHClientKeyExchange extends HandshakeMessage {

    int messageType() {
        return ht_client_key_exchange;
    }

    /*
     * This value may be empty if it was included in the
     * client's certificate ...
     */
    private byte dh_Yc[];               // 1 to 2^16 -1 bytes

    BigInteger getClientPublicKey() {
        return new BigInteger(1, dh_Yc);
    }

    /*
     * Either pass the client's public key explicitly (because it's
     * using DHE or DH_anon), or implicitly (the public key was in the
     * certificate).
     */
    DHClientKeyExchange(BigInteger publicKey) {
        dh_Yc = toByteArray(publicKey);
    }

    DHClientKeyExchange() {
        dh_Yc = null;
    }

    /*
     * Get the client's public key either explicitly or implicitly.
     * (It's ugly to have an empty record be sent in the latter case,
     * but that's what the protocol spec requires.)
     */
    DHClientKeyExchange(HandshakeInStream input) throws IOException {
        dh_Yc = input.getBytes16();
    }

    int messageLength() {
        if (dh_Yc == null) {
            return 0;
        } else {
            return dh_Yc.length + 2;
        }
    }

    void send(HandshakeOutStream s) throws IOException {
        s.putBytes16(dh_Yc);
    }

    void print(PrintStream s) throws IOException {
        s.println("*** ClientKeyExchange, DH");

        if (debug != null && Debug.isOn("verbose")) {
            Debug.println(s, "DH Public key", dh_Yc);
        }
    }
}
