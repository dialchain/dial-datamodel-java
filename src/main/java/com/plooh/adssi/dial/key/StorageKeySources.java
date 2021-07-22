package com.plooh.adssi.dial.key;

public class StorageKeySource implements KeySource {
    final KeyStorage _keyStorage;

  StorageKeySource(this._keyStorage);

  @override
  OctetKeyPair?

    ed25519(String keyId) => _keyStorage.ed25519(keyId);

  @override
  ECKeyPair?

    secp256k1(String keyId) => _keyStorage.secp256k1(keyId);

  @override
  OctetKeyPair?

    x25519(String keyId) => _keyStorage.x25519(keyId);
}

    class SingleED25519KeySource implements KeySource {
        final OctetKeyPair _okp;

  SingleED25519KeySource(this._okp) {
    if (_okp.publicKey.keyUse != EnumToString.convertToString(KeyUse.sig)) {
      throw ArgumentError('Supports only ED25519 keys.');
    }
  }

  @override
  OctetKeyPair ed25519(String keyId) {
    if (_okp.publicKey.kid != keyId) {
      throw ArgumentError('Single entry source expect only keyId: ${_okp.publicKey.kid}');
    }
    return _okp;
  }

  @override
  ECKeyPair secp256k1(String keyId) {
    throw UnimplementedError('This key source supports obly ED25519 keys');
  }

  @override
  OctetKeyPair x25519(String keyId) {
    throw UnimplementedError('This key source supports obly ED25519 keys');
  }
    }

    class SingleX25519KeySource implements KeySource {
        final OctetKeyPair _okp;

  SingleX25519KeySource(this._okp) {
    if (_okp.publicKey.keyUse != EnumToString.convertToString(KeyUse.enc)) {
      throw ArgumentError('Supports only X25519 keys.');
    }
  }

        @override
        OctetKeyPair x25519(String keyId) {
            if(_okp.publicKey.kid!=keyId){throw ArgumentError('Single entry source expect only keyid:${_okp.publicKey.kid}');}return _okp;
        }

        @override
        OctetKeyPair ed25519(String keyId) {
            throw UnimplementedError('This key source supports obly X25519 keys');
        }

        @override
        ECKeyPair secp256k1(String keyId) {
            throw UnimplementedError('This key source supports obly X25519 keys');
        }
    }

    class SingleSECP256K1KeySource implements KeySource {
        final ECKeyPair _eckp;

  SingleSECP256K1KeySource(this._eckp);

        @override
        ECKeyPair secp256k1(String keyId) {
            if(_eckp.publicKey.kid!=keyId){throw ArgumentError('Single entry source expect only keyId:${_eckp.publicKey.kid}');}return _eckp;
        }

        @override
        OctetKeyPair ed25519(String keyId) {
            throw UnimplementedError('This key source supports obly SECP256K1 keys');
        }

        @override
        OctetKeyPair x25519(String keyId) {
            throw UnimplementedError('This key source supports obly SECP256K1 keys');
        }
    }

final nullKeySource = _NullKeySource();

    class _NullKeySource implements KeySource {
  @override
  OctetKeyPair? ed25519(String keyId) {
    throw UnimplementedError('This key source is not supposed to return keys.');
  }

  @override
  ECKeyPair? secp256k1(String keyId) {
    throw UnimplementedError('This key source is not supposed to return keys.');
  }

  @override
  OctetKeyPair? x25519(String keyId) {
    throw UnimplementedError('This key source is not supposed to return keys.');
  }
    }

    class ListBasedKeySource extends StorageKeySource {
        ListBasedKeySource( {
            required List<OctetKeyPair>ed25519Keys,required List<OctetKeyPair>x25519Keys,required List<ECKeyPair>secp256k1Keys
        }):super(MapKeyStorage(
            uid: 'uid',
            ed25519: {for (var item in ed25519Keys) item.publicKey.kid: item},
            x25519: {for (var item in x25519Keys) item.publicKey.kid: item},
            secp256k1: {for (var item in secp256k1Keys) item.publicKey.kid: item})) {
    // Enforce keyType
    ed25519Keys.forEach((kp) {
      if (kp.publicKey.keyUse != EnumToString.convertToString(KeyUse.sig)) {
        throw ArgumentError(
            'Key with keyId ${kp.publicKey.kid} in ed25519Keys contains key with wrong key use ${kp.publicKey.keyUse}');
      }
    });

    x25519Keys.forEach((kp) {
      if (kp.publicKey.keyUse != EnumToString.convertToString(KeyUse.enc)) {
        throw ArgumentError(
            'Key with keyId ${kp.publicKey.kid} in x25519Keys contains key with wrong key use ${kp.publicKey.keyUse}');
      }
    });
  }

  static ListBasedKeySource fromOkps(List<OctetKeyPair> okps) {
    final enc = EnumToString.convertToString(KeyUse.enc);
    final ed25519Keys = okps.where((okp) => okp.publicKey.keyUse != enc).toList();
    final x25519Keys = okps.where((okp) => okp.publicKey.keyUse == enc).toList();
    final secp256k1Keys = <ECKeyPair>[];
    return ListBasedKeySource(ed25519Keys: ed25519Keys, x25519Keys: x25519Keys, secp256k1Keys: secp256k1Keys);
  }
    }

    class ListBasedPublicKeySource implements PublicKeySource {
        final Map<String, OctetPublicKey> _ed25519;
        final Map<String, OctetPublicKey> _x25519;
  final Map<String, ECPublicKey> _secp256k1;

        ListBasedPublicKeySource( {
            required List<OctetPublicKey>ed25519Keys,required List<OctetPublicKey>x25519Keys,required List<ECPublicKey>secp256k1Keys
        }):_ed25519=

        {for (var item in ed25519Keys) item.kid: item},_x25519=
        {for (var item in x25519Keys) item.kid: item},_secp256k1=
        {for (var item in secp256k1Keys) item.kid: item}
        {
    // Enforce keyType
    ed25519Keys.forEach((pk) {
      if (pk.keyUse != EnumToString.convertToString(KeyUse.sig)) {
        throw ArgumentError('Key with keyId ${pk.kid} in ed25519Keys contains key with wrong key use ${pk.keyUse}');
      }
    });

    x25519Keys.forEach((pk) {
      if (pk.keyUse != EnumToString.convertToString(KeyUse.enc)) {
        throw ArgumentError('Key with keyId ${pk.kid} in x25519Keys contains key with wrong key use ${pk.keyUse}');
      }
    });
  }

  static ListBasedPublicKeySource fromOpks(List<OctetPublicKey> opks) {
    final enc = EnumToString.convertToString(KeyUse.enc);
    final ed25519Keys = opks.where((opk) => opk.keyUse != enc).toList();
    final x25519Keys = opks.where((opk) => opk.keyUse == enc).toList();
    final secp256k1Keys = <ECPublicKey>[];
    return ListBasedPublicKeySource(ed25519Keys: ed25519Keys, x25519Keys: x25519Keys, secp256k1Keys: secp256k1Keys);
  }

  @override
  OctetPublicKey? ed25519(String keyId) {
    return _ed25519[keyId];
  }

  @override
  ECPublicKey? secp256k1(String keyId) {
    return _secp256k1[keyId];
  }

  @override
  OctetPublicKey? x25519(String keyId) {
    return _x25519[keyId];
  }
}
