package com.plooh.adssi.dial.lookup;

import com.plooh.adssi.dial.util.DataUtil;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.plooh.adssi.dial.data.ParticipantDeclaration;
import com.plooh.adssi.dial.data.VerificationMethod;
import com.plooh.adssi.dial.parser.ParticipantDeclarationMapped;

public class ParticipantDeclarationBasedPublicKeyResolver extends MapBackedPublicKeyResolver {
    public ParticipantDeclarationBasedPublicKeyResolver(String participantRecord) {
        super(_extractVerificationMethods(participantRecord));
    }

    private static Map<String, VerificationMethod> _extractVerificationMethods(String participantRecord) {
        final Map<String, VerificationMethod> result = new HashMap<>();

        ParticipantDeclarationMapped recordMap = new ParticipantDeclarationMapped(participantRecord);
        List<ParticipantDeclaration> declarations = recordMap.declarations();
        if (DataUtil.isNullOrEmpty(declarations)) {
            return result;
        }

        declarations.stream()
            .filter( declaration -> !DataUtil.isNullOrEmpty(declaration.getVerificationMethod()))
            .map( declaration -> declaration.getVerificationMethod() )
            .flatMap(List::stream)
            .forEach( verificationMethod -> result.put(verificationMethod.getId(), verificationMethod));
        return result;
    }
}
