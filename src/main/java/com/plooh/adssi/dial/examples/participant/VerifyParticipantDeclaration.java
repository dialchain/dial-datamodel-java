package com.plooh.adssi.dial.examples.participant;

import java.util.List;

import com.nimbusds.jose.jwk.JWK;
import com.plooh.adssi.dial.crypto.CommonCurveKeyService;
import com.plooh.adssi.dial.crypto.CryptoService;
import com.plooh.adssi.dial.crypto.JcsBase64EcSignature2021Service;
import com.plooh.adssi.dial.data.ParticipantDeclaration;
import com.plooh.adssi.dial.data.Proof;
import com.plooh.adssi.dial.data.VerificationMethod;
import com.plooh.adssi.dial.parser.ParticipantDeclarationMapped;

public class VerifyParticipantDeclaration {

    public boolean handle(String dialRecordString) {
        ParticipantDeclarationMapped doc = new ParticipantDeclarationMapped(dialRecordString);
        ParticipantDeclaration declaration = doc.declarations().get(0);
        List<VerificationMethod> verificationMethods = declaration.getVerificationMethod();
        List<Proof> proofs = doc.proof();
        for (int j = 0; j < verificationMethods.size(); j++) {
            VerificationMethod verificationMethod = verificationMethods.get(j);
            // Find a proof for this verification method
            Proof proof = proofs.stream().filter(p -> verificationMethod.getId().equals(p.getVerificationMethod()))
                    .findFirst().orElseThrow(() -> new IllegalStateException(
                            "Invalid record. Missing proof for declared verification method"));

            CommonCurveKeyService keyService = CryptoService.findKeyService(verificationMethod.getType());
            JWK publicJWK = keyService.publicKeyFromMultibase(verificationMethod.getPublicKeyMultibase(),
                    verificationMethod.getId());
            JcsBase64EcSignature2021Service verifService = CryptoService.findSignatureService(proof.getType());
            boolean verified = verifService.verify(dialRecordString, publicJWK, proof);
            if (!verified)
                throw new IllegalStateException(
                        "Could not verify proof for verification method with id " + verificationMethod.getId());
        }
        return true;
    }

}