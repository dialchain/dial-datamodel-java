package com.plooh.adssi.dial.examples.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.plooh.adssi.dial.crypto.BitcoinAddress;
import com.plooh.adssi.dial.crypto.CryptoService;
import com.plooh.adssi.dial.crypto.Ed25519VerificationKey2021Service;
import com.plooh.adssi.dial.crypto.Secp256k1VerificationKey2021Service;
import com.plooh.adssi.dial.data.ECKeyPair;
import com.plooh.adssi.dial.data.EncodedECPublicKey;
import com.plooh.adssi.dial.data.KeyAgreement;
import com.plooh.adssi.dial.data.OctetKeyPair;
import com.plooh.adssi.dial.data.ParticipantDeclaration;
import com.plooh.adssi.dial.data.SignatureAssertionMethod;
import com.plooh.adssi.dial.data.TreasuryAccount;
import com.plooh.adssi.dial.data.TreasuryAccountControler;
import com.plooh.adssi.dial.data.VerificationMethod;
import com.plooh.adssi.dial.examples.participant.KeyAgreementData;
import com.plooh.adssi.dial.examples.participant.VerificationMethodData;

import org.bitcoinj.params.MainNetParams;

import io.ipfs.multibase.Multibase.Base;

public class VerificationMethodUtils {

    static final String keyIdSeparator = "#";

    private static int _parseKeyIndex(String keyId) {
        return Integer.parseInt(keyId.substring(keyId.lastIndexOf('-')));
    }

    private static String assertionMethodId(String id, String creationDate, int index) {
        return id + keyIdSeparator + creationDate + keyIdSeparator + "am-" + index;
    }

    public static VerificationMethodData verificationMethodEd25519(String participantId, String creationDate,
            int index) {
        String keyId = participantId + keyIdSeparator + creationDate + keyIdSeparator
                + CryptoService.ed25519KeyService.KEY_ID_SUFFIX + index;
        OctetKeyPair keyPair = CryptoService.ed25519KeyService.genKeyPair(keyId);
        SignatureAssertionMethod signatureAssertionMethod = new SignatureAssertionMethod(
                assertionMethodId(participantId, creationDate, index));
        signatureAssertionMethod.setVerificationMethod(keyId);
        return verificationMethodEd25519(keyPair, signatureAssertionMethod);
    }

    public static VerificationMethodData verificationMethodEd25519(OctetKeyPair keyPair,
            SignatureAssertionMethod signatureAssertionMethod) {
        EncodedECPublicKey publicKeyEnncoded = CryptoService.ed25519KeyService
                .publicKeyEnncoded(keyPair.getPublicKey());
        String publicKeyMultibase = CryptoService.ed25519KeyService.publicKeyMultibase(publicKeyEnncoded,
                Base.Base58BTC);
        VerificationMethod verificationMethod = new VerificationMethod();
        verificationMethod.setId(keyPair.getPublicKey().getKid());
        verificationMethod.setType(Ed25519VerificationKey2021Service.VERIFICATION_METHOD_TYPE);
        verificationMethod.setPublicKeyMultibase(publicKeyMultibase);
        return new VerificationMethodData(verificationMethod.getId(),
                CryptoService.ed25519KeyService.ecKeyEncoded(keyPair, keyPair.getPublicKey()), verificationMethod,
                signatureAssertionMethod);
    }

    public static VerificationMethodData verificationMethodSecp256k1(ECKeyPair keyPair,
            SignatureAssertionMethod signatureAssertionMethod) {
        VerificationMethod verificationMethod = new VerificationMethod();
        verificationMethod.setId(keyPair.getPublicKey().getKid());
        verificationMethod.setType(Secp256k1VerificationKey2021Service.VERIFICATION_METHOD_TYPE);
        EncodedECPublicKey publicKeyEnncoded = CryptoService.secp256k1KeyService
                .publicKeyEnncoded(keyPair.getPublicKey());
        String publicKeyMultibase = CryptoService.secp256k1KeyService.publicKeyMultibase(publicKeyEnncoded,
                Base.Base58BTC);
        verificationMethod.setPublicKeyMultibase(publicKeyMultibase);
        return new VerificationMethodData(verificationMethod.getId(),
                CryptoService.secp256k1KeyService.ecKeyEncoded(keyPair, keyPair.getPublicKey()), verificationMethod,
                signatureAssertionMethod);
    }

    public static VerificationMethodData verificationMethodSecp256k1(String participantId, String creationDate,
            int index) {
        String keyId = participantId + keyIdSeparator + creationDate + keyIdSeparator
                + CryptoService.secp256k1KeyService.KEY_ID_SUFFIX + index;
        ECKeyPair keyPair = CryptoService.secp256k1KeyService.genKeyPair(keyId);
        SignatureAssertionMethod signatureAssertionMethod = new SignatureAssertionMethod(
                assertionMethodId(participantId, creationDate, index));
        signatureAssertionMethod.setVerificationMethod(keyId);
        return verificationMethodSecp256k1(keyPair, signatureAssertionMethod);
    }

    /// Generate the identity key pair from an assertion key.
    public static String idKeyPairFromAssertionKey(String creationDate, int keyIndex, Map<String, Object> methodMap) {
        final OctetKeyPair idKeyPair = CryptoService.ed25519KeyService.keyPairsAutoPrefixed(1, creationDate, keyIndex)
                .get(0);

        final VerificationMethodData vm = verificationMethodEd25519(idKeyPair, null);
        final String participantKnownId = vm.getVerificationMethod().getPublicKeyMultibase();
        String assertionMethodId = assertionMethodId(participantKnownId, creationDate,
                _parseKeyIndex(idKeyPair.getPublicKey().getKid()));
        final SignatureAssertionMethod signatureAssertionMethod = new SignatureAssertionMethod(assertionMethodId);
        signatureAssertionMethod.setVerificationMethod(vm.getVerificationMethod().getId());
        vm.setAssertionMethod(signatureAssertionMethod);
        methodMap.put(vm.getId(), vm);
        return participantKnownId;
    }

    /// Generate the identity key pair from an wallet key.
    public static String idKeyPairFromWalletKey(String creationDate, int keyIndex, Map<String, Object> methodMap) {
        final ECKeyPair idKeyPair = CryptoService.secp256k1KeyService.keyPairsAutoPrefixed(1, creationDate, keyIndex)
                .get(0);

        final VerificationMethodData vm = verificationMethodSecp256k1(idKeyPair, null);
        final String participantKnownId = vm.getVerificationMethod().getPublicKeyMultibase();
        String assertionMethodId = assertionMethodId(participantKnownId, creationDate,
                _parseKeyIndex(idKeyPair.getPublicKey().getKid()));
        final SignatureAssertionMethod signatureAssertionMethod = new SignatureAssertionMethod(assertionMethodId);
        signatureAssertionMethod.setVerificationMethod(vm.getVerificationMethod().getId());
        vm.setAssertionMethod(signatureAssertionMethod);
        methodMap.put(vm.getId(), vm);
        return participantKnownId;
    }

    public static KeyAgreementData verificationMethodx25519(OctetKeyPair keyPair) {
        EncodedECPublicKey publicKeyEnncoded = CryptoService.ed25519KeyService
                .publicKeyEnncoded(keyPair.getPublicKey());
        String publicKeyMultibase = CryptoService.ed25519KeyService.publicKeyMultibase(publicKeyEnncoded,
                Base.Base58BTC);
        KeyAgreement keyAgreement = new KeyAgreement();
        keyAgreement.setId(keyPair.getPublicKey().getKid());
        keyAgreement.setType(Ed25519VerificationKey2021Service.VERIFICATION_METHOD_TYPE);
        keyAgreement.setPublicKeyMultibase(publicKeyMultibase);
        return new KeyAgreementData(keyAgreement.getId(),
                CryptoService.x25519KeyService.ecKeyEncoded(keyPair, keyPair.getPublicKey()), keyAgreement, null);
    }

    public static int generateKeyAgreementKeys(int keyAgreementKeyCount, String participantId, String creationDate,
            int keyIndex, ParticipantDeclaration participantDeclaration, Map<String, Object> methodMap) {
        if (keyAgreementKeyCount <= 0)
            return keyIndex;
        final List<OctetKeyPair> x25519KeyPairs = CryptoService.x25519KeyService.keyPairs(keyAgreementKeyCount,
                keyIndex, participantId, creationDate);
        keyIndex += keyAgreementKeyCount;
        x25519KeyPairs.forEach(x25519KeyPair -> {
            KeyAgreementData ka = VerificationMethodUtils.verificationMethodx25519(x25519KeyPair);
            methodMap.put(ka.getId(), ka);
            participantDeclaration.getKeyAgreement().add(ka.getKeyAgreement());
        });
        return keyIndex;
    }

    public static int generateWalletKeys(int walletKeyCount, String participantId, String creationDate, int keyIndex,
            ParticipantDeclaration participantDeclaration, Map<String, Object> methodMap) {
        if (walletKeyCount <= 0)
            return keyIndex;
        if (participantDeclaration.getAccount() == null) {
            participantDeclaration.setAccount(new ArrayList<>());
        }

        for (int i = 0; i < walletKeyCount; i++) {
            VerificationMethodData verif_secp2561 = VerificationMethodUtils.verificationMethodSecp256k1(participantId,
                    creationDate, keyIndex);
            participantDeclaration.getVerificationMethod().add(verif_secp2561.getVerificationMethod());
            methodMap.put(verif_secp2561.getId(), verif_secp2561);

            TreasuryAccount treasuryAccount = new TreasuryAccount();
            String encodedPk = BitcoinAddress.p2wpkhAddress(MainNetParams.get(),
                    verif_secp2561.getKeyPair().getPublicKey());

            treasuryAccount.setAddress(encodedPk);
            treasuryAccount.setNetwork(MainNetParams.get().getId());
            TreasuryAccountControler control = new TreasuryAccountControler();
            control.setQuorum(1);
            control.setVerificationMethod(Arrays.asList(verif_secp2561.getVerificationMethod().getId()));
            treasuryAccount.setControl(control);
            participantDeclaration.getAccount().add(treasuryAccount);
            keyIndex += 1;
        }
        return keyIndex;
    }

    public static int generateAssertionKeys(int assertionKeyCount, String participantId, String creationDate,
            int keyIndex, ParticipantDeclaration participantDeclaration, Map<String, Object> methodMap) {
        if (assertionKeyCount <= 0)
            return keyIndex;

        for (int i = 0; i < assertionKeyCount; i++) {
            VerificationMethodData verif_ed25519 = VerificationMethodUtils.verificationMethodEd25519(participantId,
                    creationDate, keyIndex);
            participantDeclaration.getVerificationMethod().add(verif_ed25519.getVerificationMethod());
            methodMap.put(verif_ed25519.getId(), verif_ed25519);
            keyIndex += 1;
        }
        return keyIndex;
    }
}