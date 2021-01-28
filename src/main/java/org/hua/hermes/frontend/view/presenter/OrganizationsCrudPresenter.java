package org.hua.hermes.frontend.view.presenter;

import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.hua.hermes.frontend.error.exception.InternalServerErrorException;
import org.hua.hermes.frontend.repository.OrganizationRepository;
import org.hua.hermes.frontend.view.HasNotifications;
import org.hua.hermes.keycloak.client.exception.ConflictException;
import org.keycloak.representations.idm.GroupRepresentation;

import javax.ws.rs.ClientErrorException;
import javax.ws.rs.NotFoundException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;

@Log4j2
public class OrganizationsCrudPresenter
{
    protected final OrganizationRepository repository;

    @Setter
    protected HasNotifications view;

    public OrganizationsCrudPresenter(OrganizationRepository repository)
    {
        this.repository = repository;
    }

    public List<GroupRepresentation> findAll(int offset, int limit) {
        try {
            return execute(() -> repository.findAll(offset, limit));
        } catch (ClientErrorException e) {
            return Collections.emptyList();
        }
    }

    public int count() {
        try {
            return execute(() -> repository.count());
        } catch (ClientErrorException e) {
            return 0;
        }
    }


    protected <V> V execute(Callable<V> callable)
    {
        if(view == null) throw new IllegalStateException("View has not been set");
        try{
            return callable.call();
        } catch (NotFoundException ex){
            //If resource is not found then go to the beautiful 404 page
            log.error(ex);
            throw new com.vaadin.flow.router.NotFoundException();
        }
        catch (ConflictException ex){
            //In case of conflict, inform the user about it.
            //Unfortunately, keycloak's API doesn't return any info on what caused the conflict.
            log.error(ex);
            view.showNotification("A conflict has occurred. " + ex.getMessage());
            throw ex;
        } catch (Exception ex){
            //We treat any other exception as a 500 because that is not intended behavior.
            log.error(ex);
            throw new InternalServerErrorException(ex);
        }
    }

}
