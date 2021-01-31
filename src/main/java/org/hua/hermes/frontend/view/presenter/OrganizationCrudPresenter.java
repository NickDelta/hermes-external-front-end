package org.hua.hermes.frontend.view.presenter;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.hua.hermes.backend.entity.Application;
import org.hua.hermes.frontend.repository.OrganizationRepository;
import org.hua.hermes.frontend.view.HasNotifications;
import org.hua.hermes.keycloak.client.exception.ConflictException;
import org.keycloak.representations.idm.GroupRepresentation;
import org.springframework.web.client.HttpClientErrorException;

import javax.ws.rs.NotFoundException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;

@AllArgsConstructor
@Log4j2
public class OrganizationCrudPresenter
{
    private final OrganizationRepository repository;

    public Optional<GroupRepresentation> findById(String id) {
        try {
            return execute(() -> repository.findById(id));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public Optional<GroupRepresentation> findByName(String name) throws Exception {
        try{
            return execute(() -> repository.findByName(name));
        } catch (Exception ex){
            return Optional.empty();
        }
    }

    public List<GroupRepresentation> findAll(int offset, int limit) {
        try {
            return execute(() -> repository.findAll(offset, limit));
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    public int count() {
        try {
            return execute(() -> repository.count());
        } catch (Exception e) {
            return 0;
        }
    }

    protected <V> V execute(Callable<V> callable) throws Exception
    {
        try{
            return callable.call();
        } catch (Exception ex){
            log.error(ex);

            Dialog dialog = new Dialog();
            dialog.setCloseOnEsc(true);
            dialog.setCloseOnOutsideClick(true);

            Text text = new Text("Something went wrong. Please try executing the same action again.");
            Button okButton = new Button("OK", event -> dialog.close());
            VerticalLayout layout = new VerticalLayout(text,okButton);
            layout.setDefaultHorizontalComponentAlignment(FlexComponent.Alignment.CENTER);
            dialog.add(layout);

            dialog.open();

            throw ex;
        }
    }

}
