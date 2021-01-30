package org.hua.hermes.frontend.repository.impl;

import org.hua.hermes.frontend.repository.OrganizationRepository;
import org.hua.hermes.keycloak.client.HermesKeycloak;
import org.keycloak.representations.idm.GroupRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class OrganizationRepositoryImpl implements OrganizationRepository {

    private final HermesKeycloak client;

    @Value("${keycloak.realm}")
    private String realm;

    public OrganizationRepositoryImpl(HermesKeycloak client) {
        this.client = client;
    }

    @Override
    public Optional<GroupRepresentation> findById(String id)
    {

        return Optional.ofNullable(
                client.realm(realm)
                        .groups()
                        .group(id)
                        .toRepresentation());
    }

    @Override
    public Optional<GroupRepresentation> findByName(String name)
    {
        return Optional.ofNullable(client.
                organizations()
                .organization(name)
                .manage()
                .toRepresentation());
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
