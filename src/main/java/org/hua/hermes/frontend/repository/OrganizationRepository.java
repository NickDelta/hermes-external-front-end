package org.hua.hermes.frontend.repository;

import org.keycloak.representations.idm.GroupRepresentation;

import java.util.List;

public interface OrganizationRepository {
    List<GroupRepresentation> findAll(int offset, int limit);
    Integer count();
}
