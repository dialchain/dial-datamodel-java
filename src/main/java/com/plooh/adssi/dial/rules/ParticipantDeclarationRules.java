package com.plooh.adssi.dial.rules;

import com.plooh.adssi.dial.crypto.Ed25519VerificationKey2021Service;
import com.plooh.adssi.dial.crypto.JcsBase64Ed25519Signature2021Service;
import com.plooh.adssi.dial.crypto.JcsBase64Secp256k1Signature2021Service;
import com.plooh.adssi.dial.crypto.Secp256k1VerificationKey2021Service;
import com.plooh.adssi.dial.data.ProofPurpose;
import com.plooh.adssi.dial.data.ValidationResult;
import com.plooh.adssi.dial.lookup.ParticipantDeclarationBasedPublicKeyResolver;
import com.plooh.adssi.dial.parser.ParticipantDeclarationMapped;
import com.plooh.adssi.dial.util.DataUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ParticipantDeclarationRules {

    public static String DIAL_001_001_001_RULE_PARTICIPANT_MISSING_DECLARATION_BLOCK = "DIAL_001_001_001_rule_participant_missing_declaration_block";
    public static String DIAL_001_001_002_RULE_PARTICIPANT_MISSING_PROOF_BLOCK = "DIAL_001_001_002_rule_participant_missing_proof_block";
    public static String DIAL_001_001_003_RULE_PARTICIPANT_MISSING_PROOF_FOR_VERIFICATION_METHOD = "DIAL_001_001_003_rule_participant_missing_proof_for_verificationMethod";
    public static String DIAL_001_001_005_RULE_PARTICIPANT_UNSUPPORTED_SIGNATURE_TYPE_IN_PROOF = "DIAL_001_001_005_rule_participant_unsupported_signatureType_in_proof";

    // Verify that in this participant declaration, each presented verification method is backed
    // up by a proof
    public List<ValidationResult> checkDIAL_001_001(String recordJson) {

        var result = new ArrayList<ValidationResult>();

        // Parse the self signed declaration
        final var participantDeclaration = new ParticipantDeclarationMapped(recordJson);

        // Get Declarations
        var declarations = participantDeclaration.declarations();
        if (DataUtil.isNullOrEmpty(declarations)) {
            result.add(ValidationResult.builder().key(DIAL_001_001_001_RULE_PARTICIPANT_MISSING_DECLARATION_BLOCK).build());
        }

        // Get Proofs
        var proofs = participantDeclaration.proof();
        if (DataUtil.isNullOrEmpty(proofs)) {
            result.add(ValidationResult.builder().key(DIAL_001_001_002_RULE_PARTICIPANT_MISSING_PROOF_BLOCK).build());
        }

        if (!DataUtil.isNullOrEmpty(result)) {
            return result;
        }

        // Parse all declarations into this store
        var participantDeclarationBasedPublicKeyResolver = new ParticipantDeclarationBasedPublicKeyResolver(recordJson);

        // For each verificationMethod declared, make sure we have a valid declaration of type PoP.
        participantDeclarationBasedPublicKeyResolver.getStore().values().forEach( verificationMethod -> {
            // find PoP proofparticipantDeclarationBasedPublicKeyResolver
            var relevantProofs = proofs
                .stream()
                .filter( proof ->
                    (proof.getVerificationMethod().equals(verificationMethod.getId()) &&
                ProofPurpose.PoP.name().equals(proof.getProofPurpose())))
                .collect(Collectors.toList());

            // Add error result if proof missing
            if (relevantProofs.isEmpty()) {
                result.add(ValidationResult.builder()
                    .key(DIAL_001_001_003_RULE_PARTICIPANT_MISSING_PROOF_FOR_VERIFICATION_METHOD)
                    .details(Map.of("verificationMethod", verificationMethod)).build());
            }

            // We assume one PoP proof per verification method. But if many, verify alls.
            relevantProofs.forEach( proof -> {
                switch(proof.getType())
                {
                    case Ed25519VerificationKey2021Service.VERIFICATION_METHOD_TYPE:
                        result.addAll(new JcsBase64Ed25519Signature2021Service().verifyDeclaration(
                            recordJson, proof, participantDeclarationBasedPublicKeyResolver));
                        break;
                    case Secp256k1VerificationKey2021Service.VERIFICATION_METHOD_TYPE:
                        result.addAll(new JcsBase64Secp256k1Signature2021Service().verifyDeclaration(
                            recordJson, proof, participantDeclarationBasedPublicKeyResolver));
                        break;
                    default:
                        result.add(ValidationResult.builder()
                            .key(DIAL_001_001_005_RULE_PARTICIPANT_UNSUPPORTED_SIGNATURE_TYPE_IN_PROOF)
                            .details(Map.of("proof", proof))
                            .build());
                }
            });
        });

        return result;
    }

}
