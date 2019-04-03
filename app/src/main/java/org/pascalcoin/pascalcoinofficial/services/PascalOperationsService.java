package org.pascalcoin.pascalcoinofficial.services;

import com.github.davidbolet.jpascalcoin.crypto.model.OfflineSignResult;
import com.github.davidbolet.jpascalcoin.crypto.model.PascOperation;
import com.github.davidbolet.jpascalcoin.crypto.model.PascPrivateKey;

import org.pascalcoin.pascalcoinofficial.model.PrivateKeyInfo;
import org.pascalcoin.pascalcoinofficial.services.rest.PascalcoinServiceProvider;

public class PascalOperationsService {

    private PascalcoinServiceProvider pascalcoinServiceProvider;

    private PascalOperationsService(PascalcoinServiceProvider pascalcoinServiceProvider) {
        this.pascalcoinServiceProvider=pascalcoinServiceProvider;
    }

    public static PascalOperationsService getInstance(PascalcoinServiceProvider pascalcoinServiceProvider) {
        PascalOperationsService pascalOperationsService=new PascalOperationsService(pascalcoinServiceProvider);
        return pascalOperationsService;
    }

    public void signAndExecute(PascOperation operation, String privateKey, PrivateKeyInfo keyInfo){
        PascPrivateKey pascPrivateKey=PascPrivateKey.fromPrivateKey(privateKey,keyInfo.getPascKeyType());
        byte[] opDigest=operation.generateOpDigest(4.0f);
        OfflineSignResult res=pascPrivateKey.sign(opDigest);
        String rawOps=operation.getRawOperations(res.getStringR(), res.getStringS());
        pascalcoinServiceProvider.executeOperations(rawOps,operation);
    }
}
