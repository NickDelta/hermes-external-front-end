package org.hua.hermes.frontend.view.presenter;

import lombok.Setter;
import org.hua.hermes.backend.entity.Application;
import org.hua.hermes.frontend.repository.ApplicationRepository;
import org.hua.hermes.frontend.view.HasNotifications;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;
import java.util.Optional;

public class ApplicationsCrudPresenter {
    private final ApplicationRepository repository;

    public ApplicationsCrudPresenter(ApplicationRepository repository) {
        this.repository = repository;
    }

    @Setter
    private HasNotifications view;

    public List<Application> findAll(int offset, int limit) {
        return repository.findAll(offset, limit);
    }

    public Optional<Application> findById(String id) {
        return repository.findById(id);
    }

    public int count() {
        return repository.count();
    }

    public boolean save(Application application) {
        try {
            return repository.save(application);
        } catch (HttpClientErrorException.Conflict ex) {
            view.showNotification(
                    "Could not save the application."
            );
            return false;
        }
    }

    public boolean update(Application application) {
        try {
            return repository.update(application);
        } catch (HttpClientErrorException.Conflict ex) {
            view.showNotification(
                    "Could not perform the action due to a conflict. " +
                            "One possible reason is that another user is currently editing the same resource. " +
                            "Please try again later."
            );
            return false;
        }
    }

}
