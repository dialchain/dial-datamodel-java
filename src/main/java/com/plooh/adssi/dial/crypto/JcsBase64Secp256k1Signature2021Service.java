package com.plooh.adssi.dial.crypto;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import com.plooh.adssi.dial.data.EncodedECKey;
import com.plooh.adssi.dial.data.EncodedECPublicKey;
import com.plooh.adssi.dial.data.Proof;
import com.plooh.adssi.dial.data.SignatureResult;
import com.plooh.adssi.dial.data.VerificationMethod;
import com.plooh.adssi.dial.encode.Base64URL;
import com.plooh.adssi.dial.parser.SignedDocumentMapped;

import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.ECKey.ECDSASignature;
import org.bitcoinj.core.SignatureDecodeException;
import org.bouncycastle.asn1.sec.SECNamedCurves;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.signers.ECDSASigner;
import org.bouncycastle.crypto.signers.HMacDSAKCalculator;

public class JcsBase64Secp256k1Signature2021Service extends CommonECSignature2021Service {
    static final String SIGNATURE_TYPE = "JcsBase64Secp256k1Signature2021";
    private Ed25519VerificationKey2021Service ed25519KeyService = Ed25519VerificationKey2021Service.instance;

    @Override
    public String signatureType() {
        return SIGNATURE_TYPE;
    }

    @Override
    public SignatureResult signDeclaration(String recordJson, Proof template, EncodedECKey keyPair)

    {

        // final Map<dynamic, dynamic> recordMap = json.decode(recordJson);
        final SignedDocumentMapped signedDocumentMapped = new SignedDocumentMapped(recordJson);
        // Secure proof block
        List<Proof> proofs = signedDocumentMapped.proof();
        if (proofs == null) {
            proofs = new ArrayList<>();
        }

        // build this proof entry with predefined value for this signature service
        final Proof signatureProof = prepareProof(template);

        // produce signature input string
        final byte[] signingInputBytes = prepareSignatureInputs(signedDocumentMapped, signatureProof);
        byte[] signingInputHash = CryptoUtils.sha256(signingInputBytes);
        // Sign
        ECDSASignature detSignature = detSign(signingInputHash, keyPair);
        ECDSASignature canonicalisedSignature = detSignature.toCanonicalised();
        return prepareSignatureResult(canonicalisedSignature.encodeToDER(), signatureProof, recordJson);
    }

    @Override
    protected boolean verifySignature(VerificationMethod verificationMethod, String jcs_utf8_base64urlHeader,
            String jcs_utf8_base64urlData, String signatureBe64URL) {
        // Read public key from verification method.
        final EncodedECPublicKey publicKey = ed25519KeyService
                .publicKeyFromMultibase(verificationMethod.getPublicKeyMultibase(), verificationMethod.getId());

        // JWS verify the proof
        final byte[] signingInputBytes = (jcs_utf8_base64urlHeader + "." + jcs_utf8_base64urlData)
                .getBytes(StandardCharsets.UTF_8);
        byte[] signatureBytes = Base64URL.decode_pad_utf8_base64Url(signatureBe64URL);
        byte[] sha256 = CryptoUtils.sha256(signingInputBytes);
        byte[] pkBytes = publicKey.getBytes();
        try {
            return ECKey.verify(sha256, signatureBytes, pkBytes);
        } catch (SignatureDecodeException e) {
            // TODO log.
            return false;
        }
    }

    static final X9ECParameters curve = SECNamedCurves.getByName("secp256k1");
    static final ECDomainParameters domain = new ECDomainParameters(curve.getCurve(), curve.getG(), curve.getN(),
            curve.getH());

    private ECDSASignature detSign(byte[] data, EncodedECKey keyPair) {
        ECDSASigner signer = new ECDSASigner(new HMacDSAKCalculator(new SHA256Digest()));
        ECKey ecKey = ECKey.fromPrivate(keyPair.getBytes());
        signer.init(true, new ECPrivateKeyParameters(ecKey.getPrivKey(), domain));
        BigInteger[] signature = signer.generateSignature(data);
        return new ECDSASignature(signature[0], signature[1]);
    }
}
