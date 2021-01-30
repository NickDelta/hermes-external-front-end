package org.hua.hermes.frontend.repository;

import org.hua.hermes.backend.entity.Application;

import java.util.List;
import java.util.Optional;

public interface ApplicationRepository {
    Optional<Application> findById(String id);
    List<Application> findAll(int offset, int limit);
    Integer count();
    boolean save(Application application);
    boolean update(Application application);
}
