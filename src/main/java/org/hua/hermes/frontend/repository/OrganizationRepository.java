package org.hua.hermes.frontend.repository;

import org.keycloak.representations.idm.GroupRepresentation;

import java.util.List;
import java.util.Optional;

public interface OrganizationRepository {
    Optional<GroupRepresentation> findById(String id);
    Optional<GroupRepresentation> findByName(String name);
    List<GroupRepresentation> findAll(int offset, int limit);
    Integer count();
}
