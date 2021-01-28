package org.hua.hermes.frontend.repository.impl;

import org.hua.hermes.frontend.repository.OrganizationRepository;
import org.hua.hermes.keycloak.client.HermesKeycloak;
import org.keycloak.representations.idm.GroupRepresentation;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class OrganizationRepositoryImpl implements OrganizationRepository {

    private final HermesKeycloak client;

    public OrganizationRepositoryImpl(HermesKeycloak client) {
        this.client = client;
    }

    @Override
    public List<GroupRepresentation> findAll(int offset, int limit) {
        return client.organizations().list(offset,limit);
    }

    @Override
    public Integer count() {
        return client.organizations().count();
    }
}
