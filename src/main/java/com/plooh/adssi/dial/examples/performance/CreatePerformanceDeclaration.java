package com.plooh.adssi.dial.examples.performance;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.plooh.adssi.dial.data.Service;
import com.plooh.adssi.dial.data.ServiceNames;
import com.plooh.adssi.dial.examples.participant.CreateParticipantDeclaration;
import com.plooh.adssi.dial.examples.participant.NewParticipantDeclaration;
import com.plooh.adssi.dial.parser.TimeFormat;

public class CreatePerformanceDeclaration {
    final String publisherKey = "/publisher";

    public NewParticipantDeclaration handle(Instant dateTime, NewParticipantDeclaration participant,
            List<String> serviceUrls) {
        String creationDate = TimeFormat.format(dateTime);
        final List<Service> services = new ArrayList<>();

        for (var i = 0; i < serviceUrls.size(); i++) {
            services.add(_service(participant.getId(), i, serviceUrls.get(i) + participant.getId() + publisherKey,
                    ServiceNames.PublisherService, creationDate));
        }

        CreateParticipantDeclaration createParticipantDeclaration = new CreateParticipantDeclaration();
        try {
            int assertionKeyCount = 1;
            int walletKeyCount = 1;
            int keyAgreementKeyCount = 1;
            return createParticipantDeclaration.handle(dateTime, participant.getId(), serviceUrls,
                    participant.getKeyIndex(), assertionKeyCount, walletKeyCount, keyAgreementKeyCount, services);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private static Service _service(String id, int index, String url, ServiceNames serviceType, String created) {
        final String hSign = "#";
        final String serviceStr = serviceType.name();
        final String idString = id + hSign + created + hSign + serviceStr + "-" + index;
        return Service.builder().id(idString).serviceEndpoint(url).type(serviceStr).build();
    }
}
