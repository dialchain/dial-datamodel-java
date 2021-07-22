package com.plooh.adssi.dial.key;

import com.plooh.adssi.dial.data.Declarations;
import com.plooh.adssi.dial.data.ECPublicKey;
import com.plooh.adssi.dial.data.OctetPublicKey;
import com.plooh.adssi.dial.data.ParticipantDeclaration;
import com.plooh.adssi.dial.json.JSON;

public class ParticipantDeclarationPublicKeySource implements PublicKeySource {
    final ParticipantDeclaration _delclaration;

    ParticipantDeclarationPublicKeySource(ParticipantDeclaration delclaration) {
        _delclaration = delclaration;
    }

    ParticipantDeclarationPublicKeySource(String record) {
        final Declarations participantRecord = JSON.MAPPER.readValue(record, Declarations.class);
        return ParticipantDeclarationPublicKeySource.fromDeclaration(
            participantRecord.declaration.toList().first as ParticipantDeclaration);
    }

    KeyAgreement randomEncKey() {
        return _delclaration.keyAgreement.elementAt(twindow_random.nextInt(_delclaration.keyAgreement.length));
    }

    var ed25519KeyList;

  VerificationMethod randomEDSignKey() {
    // TODO: cache key list for reuse;
    ed25519KeyList ??= _delclaration.verificationMethod
        .where((vm) => vm.type == Ed25519VerificationKey2021Service.VERIFICATION_METHOD_TYPE)
        .toList();
    return ed25519KeyList.elementAt(twindow_random.nextInt(ed25519KeyList.length));
  }

    VerificationMethod?

    findSignKeyByMultibase(String publicKeyMultibase) {
    for (var vm in _delclaration.verificationMethod) {
      if (vm.publicKeyMultibase == publicKeyMultibase) {
        return vm;
      }
    }
    return null;
  }

  @override
  OctetPublicKey? ed25519(String keyId) {
    for (var vm in _delclaration.verificationMethod) {
      if (vm.id == keyId && vm.type == Ed25519VerificationKey2021Service.VERIFICATION_METHOD_TYPE) {
        return ed25519KeyService.publicKeyFromMultibase(vm.publicKeyMultibase, keyId);
      }
    }
    return null;
  }

  @override
  ECPublicKey? secp256k1(String keyId) {
    for (var vm in _delclaration.verificationMethod) {
      if (vm.id == keyId && vm.type == Secp2561k1VerificationKey2021Service.VERIFICATION_METHOD_TYPE) {
        return secp256k1KeyService.publicKeyFromMultibase(vm.publicKeyMultibase, keyId);
      }
    }
    return null;
  }

  @override
  OctetPublicKey? x25519(String keyId) {
    for (var vm in _delclaration.keyAgreement) {
      if (vm.id == keyId && vm.type == X25519KeyAgreementKey2021Service.VERIFICATION_METHOD_TYPE) {
        return x25519KeyService.publicKeyFromMultibase(vm.publicKeyMultibase, keyId);
      }
    }
    return null;
  }

    @Override
    public OctetPublicKey ed25519(String keyId) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public OctetPublicKey x25519(String keyId) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ECPublicKey secp256k1(String keyId) {
        // TODO Auto-generated method stub
        return null;
    }
}
