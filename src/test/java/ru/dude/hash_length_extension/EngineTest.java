package ru.dude.hash_length_extension;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Тесты
 *
 * @author Vladimir X
 * Date: 29.08.2021
 */
public class EngineTest {


    @Test
    void calcDigestByPrevTest() {

    }



    @Test
    void calcDigestTest() throws Exception {

        String hexRes = "\\xFB\\xC2\\xD3\\xE1\\x11\\x72\\x52\\xC5\\x2C\\x37\\xBF\\x8C\\xA9\\x8B\\x50\\x98\\xDA\\xD8\\x68\\x5E";
        
        assertEquals(hexRes,Util.toHex(Engine.calcDigest("AAAABBBBCCCC".getBytes())));
        assertEquals(hexRes,Util.toHex(Engine.calcDigest("AAAA".getBytes(),"BBBB".getBytes(),"CCCC".getBytes())));
    }


    @Test
    void blockCountTest() {
        assertEquals(1, Engine.blockCount(0));
        assertEquals(1, Engine.blockCount(55));
        assertEquals(2, Engine.blockCount(56));
        assertEquals(13, Engine.blockCount(800));
    }
}
