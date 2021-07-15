package com.plooh.adssi.dial.examples.validator;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.plooh.adssi.dial.crypto.BitcoinAddress;
import com.plooh.adssi.dial.crypto.CommonCurveKeyService;
import com.plooh.adssi.dial.crypto.CryptoService;
import com.plooh.adssi.dial.data.Declarations;
import com.plooh.adssi.dial.data.EncodedECPublicKey;
import com.plooh.adssi.dial.data.OrganizationDeclaration;
import com.plooh.adssi.dial.data.OrganizationMember;
import com.plooh.adssi.dial.data.ParticipantDeclaration;
import com.plooh.adssi.dial.data.Proof;
import com.plooh.adssi.dial.data.ProofPurpose;
import com.plooh.adssi.dial.data.Service;
import com.plooh.adssi.dial.data.ServiceNames;
import com.plooh.adssi.dial.data.TreasuryAccount;
import com.plooh.adssi.dial.data.TreasuryAccountControler;
import com.plooh.adssi.dial.data.VerificationMethod;
import com.plooh.adssi.dial.data.VoteAssertionMethod;
import com.plooh.adssi.dial.examples.participant.VerificationMethodData;
import com.plooh.adssi.dial.examples.utils.VerificationMethodUtils;
import com.plooh.adssi.dial.json.JSON;
import com.plooh.adssi.dial.parser.TimeFormat;

import org.bitcoinj.params.MainNetParams;

public class CreateValidatorDeclaration {
    public String handle(Instant dateTime, List<ParticipantDeclaration> nodes) throws JsonProcessingException {
        String creationDate = TimeFormat.format(dateTime);

        Declarations declarations = new Declarations();
        declarations.setType("Declaration");
        declarations.setDeclaration(new ArrayList<>());

        OrganizationDeclaration organizationDeclaration = new OrganizationDeclaration();
        declarations.getDeclaration().add(organizationDeclaration);
        organizationDeclaration.setCreated(creationDate);

        organizationDeclaration.setVerificationMethod(new ArrayList<>());
        VerificationMethodData verif_ed25519 = VerificationMethodUtils.verificationMethodEd25519(null, creationDate, 0);
        organizationDeclaration.getVerificationMethod().add(verif_ed25519.getVerificationMethod());
        organizationDeclaration.setId(verif_ed25519.getVerificationMethod().getPublicKeyMultibase());

        organizationDeclaration.setController(Arrays.asList(organizationDeclaration.getId()));

        VoteAssertionMethod voteAssertionMethod = new VoteAssertionMethod(
                organizationDeclaration.getId() + "#" + creationDate + "#am-0");
        voteAssertionMethod.setQuorum((nodes.size() / 2) + 1);
        voteAssertionMethod.setMember(new ArrayList<OrganizationMember>());
        for (ParticipantDeclaration pd : nodes) {
            OrganizationMember organizationMember = new OrganizationMember();
            organizationMember.setId(pd.getId());
            organizationMember.setShares(1);
            voteAssertionMethod.getMember().add(organizationMember);
        }
        organizationDeclaration.setAssertionMethod(Arrays.asList(voteAssertionMethod));

        TreasuryAccount treasuryAccount = new TreasuryAccount();
        final List<EncodedECPublicKey> ecPublicKeys = new ArrayList<>();
        final List<String> verificationMethodIds = new ArrayList<>();
        for (ParticipantDeclaration pd : nodes) {
            VerificationMethod verificationMethod = pd.getVerificationMethod().stream()
                    .filter(vm -> vm.getType().equals("Secp256k1VerificationKey2021")).findAny().get();
            verificationMethodIds.add(verificationMethod.getId());
            CommonCurveKeyService keyService = CryptoService.findKeyService(verificationMethod.getType());
            EncodedECPublicKey publicKey = keyService.publicKeyFromMultibase(verificationMethod.getPublicKeyMultibase(),
                    verificationMethod.getId());
            ecPublicKeys.add(publicKey);
        }
        int quorum = (ecPublicKeys.size() / 2) + 1;

        String p2shAddress = BitcoinAddress.p2shAddress(MainNetParams.get(), quorum, ecPublicKeys);
        treasuryAccount.setAddress(p2shAddress);
        treasuryAccount.setNetwork(MainNetParams.get().getId());
        TreasuryAccountControler control = new TreasuryAccountControler();
        control.setQuorum(quorum);
        control.setVerificationMethod(verificationMethodIds);
        treasuryAccount.setControl(control);
        organizationDeclaration.setAccount(Arrays.asList(treasuryAccount));

        organizationDeclaration.setService(new ArrayList<Service>());
        addService(organizationDeclaration, 0, "https://node0.first-dial-validator.io/publisher",
                ServiceNames.PublisherService.name());
        addService(organizationDeclaration, 1, "https://node1.first-dial-validator.io/publisher",
                ServiceNames.PublisherService.name());
        addService(organizationDeclaration, 2, "https://node2.first-dial-validator.io/publisher",
                ServiceNames.PublisherService.name());
        addService(organizationDeclaration, 0, "https://open.first-dial-validator.io/lookup",
                ServiceNames.LookupService.name());

        Proof ed25519Proof = new Proof();
        ed25519Proof.setIssuer(organizationDeclaration.getId());
        ed25519Proof.setProofPurpose(ProofPurpose.PoP.name());
        ed25519Proof.setVerificationMethod(verif_ed25519.getVerificationMethod().getId());
        ed25519Proof.setCreated(creationDate);
        ed25519Proof.setNonce(UUID.randomUUID().toString());
        String signedRecord = CryptoService.ed25519SignatureService
                .signDeclaration(JSON.MAPPER.writeValueAsString(declarations), ed25519Proof, verif_ed25519.getKeyPair())
                .getSignedRecord();

        return signedRecord;
    }

    private void addService(OrganizationDeclaration organizationDeclaration, int index, String url,
            String serviceType) {
        Service service = new Service();
        service.setId(organizationDeclaration.getId() + "#" + organizationDeclaration.getCreated() + "#" + serviceType
                + "-" + index);
        service.setAssertionMethod(organizationDeclaration.getAssertionMethod().get(0).getId());
        service.setType(serviceType);
        service.setServiceEndpoint(url);
        organizationDeclaration.getService().add(service);
    }
}