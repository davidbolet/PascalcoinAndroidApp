package org.pascalcoin.pascalcoinofficial;

import com.github.davidbolet.jpascalcoin.common.helper.HexConversionsHelper;
import com.github.davidbolet.jpascalcoin.common.helper.OpenSslAes;
import com.github.davidbolet.jpascalcoin.common.model.KeyType;
import com.github.davidbolet.jpascalcoin.common.model.PascPublicKey;
import com.github.davidbolet.jpascalcoin.crypto.model.PascPrivateKey;

import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;

public class PrivateKeyTest {


    @Before
    public void init()
    {


    }

    @Test
    public void addition_isCorrect() {
        String privateKeyEnc="<a private key>";
        String pwd ="<a password>";
        String privateKey=  OpenSslAes.decrypt(pwd, privateKeyEnc);
        PascPrivateKey key = PascPrivateKey.fromPrivateKey(privateKey.substring(8), KeyType.fromValue(HexConversionsHelper.hexBigEndian2Int(privateKey.substring(0,4))));
        PascPublicKey publicKey = key.getPublicKey();
        assertNotNull(publicKey);
        assertEquals(key.getPublicKey().getBase58PubKey(),publicKey.getBase58PubKey());
    }


    @Test
    public void creation_isCorrect() throws Exception {
        PascPrivateKey key = PascPrivateKey.generate(KeyType.SECP256K1);
        String encrypted=OpenSslAes.encrypt("123456",key.getPrivateKey());

        assertEquals(key.getPrivateKey(),OpenSslAes.decrypt("123456",encrypted));
        assertNotNull(key.getPublicKey().getBase58PubKey());
    }

}
