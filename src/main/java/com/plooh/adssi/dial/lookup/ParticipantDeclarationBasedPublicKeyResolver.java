package com.plooh.adssi.dial.lookup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.plooh.adssi.dial.data.ParticipantDeclaration;
import com.plooh.adssi.dial.data.VerificationMethod;
import com.plooh.adssi.dial.parser.ParticipantDeclarationMapped;

public class ParticipantDeclarationBasedPublicKeyResolver extends MapBackedPublicKeyResolver {
    ParticipantDeclarationBasedPublicKeyResolver(String participantRecord) {
        super(_extractVerificationMethods(participantRecord));
    }

    private static Map<String, VerificationMethod> _extractVerificationMethods(String participantRecord) {
        ParticipantDeclarationMapped recordMap = new ParticipantDeclarationMapped(participantRecord);
        final Map<String, VerificationMethod> result = new HashMap<>();
        List<ParticipantDeclaration> declarations = recordMap.declarations();
        if (declarations == null || declarations.isEmpty()) {
            return result;
        }
        declarations = declarations == null ? new ArrayList<>() : declarations;

        for (var declaration : declarations) {
            List<VerificationMethod> vms = declaration.getVerificationMethod();
            if (vms != null) {
                vms.forEach(e -> {
                    result.put(e.getId(), e);
                });
            }
        }
        return result;
    }
}
