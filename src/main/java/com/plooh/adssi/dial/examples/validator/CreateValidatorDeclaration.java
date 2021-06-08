package com.plooh.adssi.dial.examples.validator;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.plooh.adssi.dial.data.AddressType;
import com.plooh.adssi.dial.data.Declarations;
import com.plooh.adssi.dial.data.DialRecord;
import com.plooh.adssi.dial.data.OrganizationDeclaration;
import com.plooh.adssi.dial.data.OrganizationMember;
import com.plooh.adssi.dial.data.ParticipantDeclaration;
import com.plooh.adssi.dial.data.Service;
import com.plooh.adssi.dial.data.ServiceNames;
import com.plooh.adssi.dial.data.VoteAssertionMethod;
import com.plooh.adssi.dial.json.JSON;
import com.plooh.adssi.dial.parser.TimeFormat;

public class CreateValidatorDeclaration {
    public String handle(Instant dateTime, List<ParticipantDeclaration> nodes) throws JsonProcessingException {
        String creationDate = TimeFormat.DTF.format(dateTime);
        DialRecord dr = new DialRecord();
        dr.setDeclaration(new Declarations());
        dr.getDeclaration().setId(AddressType.uuid.normalize(UUID.randomUUID().toString()));
        dr.getDeclaration().setEntries(new ArrayList<>());

        OrganizationDeclaration organizationDeclaration = new OrganizationDeclaration();
        dr.getDeclaration().getEntries().add(organizationDeclaration);
        organizationDeclaration.setCreated(creationDate);
        organizationDeclaration.setId(AddressType.uuid.normalize(UUID.randomUUID().toString()));
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

        organizationDeclaration.setService(new ArrayList<Service>());
        addService(organizationDeclaration, 0, "https://node0.first-dial-validator.io/publisher",
                ServiceNames.PublisherService.name());
        addService(organizationDeclaration, 1, "https://node1.first-dial-validator.io/publisher",
                ServiceNames.PublisherService.name());
        addService(organizationDeclaration, 2, "https://node2.first-dial-validator.io/publisher",
                ServiceNames.PublisherService.name());
        addService(organizationDeclaration, 0, "https://open.first-dial-validator.io/lookup",
                ServiceNames.LookupService.name());

        return JSON.MAPPER.writeValueAsString(dr);
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