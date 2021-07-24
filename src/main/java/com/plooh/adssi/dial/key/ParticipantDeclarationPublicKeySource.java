package com.plooh.adssi.dial.key;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.plooh.adssi.dial.crypto.CryptoService;
import com.plooh.adssi.dial.crypto.Ed25519VerificationKey2021Service;
import com.plooh.adssi.dial.crypto.Secp256k1VerificationKey2021Service;
import com.plooh.adssi.dial.crypto.X25519KeyAgreementKey2021Service;
import com.plooh.adssi.dial.data.Declarations;
import com.plooh.adssi.dial.data.ECPublicKey;
import com.plooh.adssi.dial.data.KeyAgreement;
import com.plooh.adssi.dial.data.OctetPublicKey;
import com.plooh.adssi.dial.data.ParticipantDeclaration;
import com.plooh.adssi.dial.data.VerificationMethod;
import com.plooh.adssi.dial.json.JSON;

public class ParticipantDeclarationPublicKeySource implements PublicKeySource {
    final ParticipantDeclaration _delclaration;

    ParticipantDeclarationPublicKeySource(ParticipantDeclaration delclaration) {
        _delclaration = delclaration;
    }

    ParticipantDeclarationPublicKeySource(String record) {
        Declarations participantRecord;
        try {
            participantRecord = JSON.MAPPER.readValue(record, Declarations.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        _delclaration = (ParticipantDeclaration) participantRecord.getDeclaration().get(0);
    }

    static Random rand = new Random();

    private static <T> T random(List<T> map) {
        if (map.isEmpty())
            throw new IllegalStateException("Store has not keypair.");
        List<T> givenList = map.stream().collect(Collectors.toList());
        return givenList.get(rand.nextInt(givenList.size()));
    }

    KeyAgreement randomEncKey() {
        return random(_delclaration.getKeyAgreement());
    }

    List<VerificationMethod> ed25519KeyList;

    public VerificationMethod randomEDSignKey() {
        // TODO: cache key list for reuse;
        if (ed25519KeyList == null) {

            ed25519KeyList = _delclaration.getVerificationMethod().stream()
                    .filter(vm -> vm.getType().equals(Ed25519VerificationKey2021Service.VERIFICATION_METHOD_TYPE))
                    .collect(Collectors.toList());
        }
        return random(ed25519KeyList);
    }

    VerificationMethod findSignKeyByMultibase(String publicKeyMultibase) {
        for (VerificationMethod vm : _delclaration.getVerificationMethod()) {
            if (vm.getPublicKeyMultibase().equals(publicKeyMultibase)) {
                return vm;
            }
        }
        return null;
    }

    @Override
    public OctetPublicKey ed25519(String keyId) {
        for (VerificationMethod vm : _delclaration.getVerificationMethod()) {
            if (vm.getId().equals(keyId)
                    && vm.getType().equals(Ed25519VerificationKey2021Service.VERIFICATION_METHOD_TYPE)) {

                return CryptoService.ed25519KeyService.octetPublicKey(vm.getPublicKeyMultibase(), keyId);
            }
        }
        return null;
    }

    @Override
    public ECPublicKey secp256k1(String keyId) {
        for (VerificationMethod vm : _delclaration.getVerificationMethod()) {
            if (vm.getId().equals(keyId)
                    && vm.getType().equals(Secp256k1VerificationKey2021Service.VERIFICATION_METHOD_TYPE)) {
                return CryptoService.secp256k1KeyService.ecPublicKey(vm.getPublicKeyMultibase(), keyId);
            }
        }
        return null;
    }

    @Override
    public OctetPublicKey x25519(String keyId) {
        for (KeyAgreement vm : _delclaration.getKeyAgreement()) {
            if (vm.getId().equals(keyId)
                    && vm.getType().equals(X25519KeyAgreementKey2021Service.VERIFICATION_METHOD_TYPE)) {
                return CryptoService.x25519KeyService.octetPublicKey(vm.getPublicKeyMultibase(), keyId);
            }
        }
        return null;
    }

}
