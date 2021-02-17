package org.hua.hermes.frontend.bean.editor;

import com.vaadin.componentfactory.enhancedcrud.BinderCrudEditor;
import com.vaadin.componentfactory.enhancedcrud.CrudEditor;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.server.VaadinSession;
import org.hua.hermes.backend.entity.Application;
import org.hua.hermes.backend.entity.ApplicationState;
import org.hua.hermes.frontend.constant.MessageConstants;
import org.hua.hermes.frontend.repository.OrganizationRepository;
import org.hua.hermes.frontend.view.presenter.OrganizationCrudPresenter;
import org.keycloak.representations.idm.GroupRepresentation;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Configuration
public class ApplicationCrudEditorFactory {

    @Bean
    @Scope("prototype")
    public CrudEditor<Application> createApplicationEditor(OrganizationRepository organizationRepository) {

        var organizationsPresenter = new OrganizationCrudPresenter(organizationRepository);

        ComboBox<GroupRepresentation> organization = new ComboBox<>("Organization");
        organization.setDataProvider(
                (filter, offset, limit) -> organizationsPresenter.findAll(offset, limit).stream(),
                (filter) -> organizationsPresenter.count());
        organization.setItemLabelGenerator(GroupRepresentation::getName);
        organization.getElement().setAttribute("colspan", "2");

        TextArea details = new TextArea("Details");
        details.getElement().setAttribute("colspan", "2");

        DateTimePicker appointmentDateTime = new DateTimePicker();
        appointmentDateTime.setLabel("Appointment Date");
        appointmentDateTime.getElement().setAttribute("colspan", "2");
        appointmentDateTime.setLocale(VaadinSession.getCurrent().getLocale());

        appointmentDateTime.setStep(Duration.ofMinutes(15));

        //Disabled because at the moment there is no mechanism for
        //auto-rejecting or auto-competing applications.
        //appointmentDateTime.setMin(LocalDateTime.now());

        var layout = new FormLayout(organization,details,appointmentDateTime);

        var binder = new Binder<>(Application.class);
        var editor = new BinderCrudEditor<>(binder, layout);

        binder.forField(organization)
              .asRequired(MessageConstants.REQUIRED)
              .withValidator(org -> {
                  if(editor.getItem() == null || editor.getItem().getId() == null) return true;
                  return editor.getItem().getOrganization().equals(org.getId());
              }, "Cannot change organization")
              .bind(application -> {
                  if(application != null && application.getId() != null)
                    return organizationsPresenter.findById(application.getOrganization()).orElse(null);
                  else
                    return null;
                  }, (application,org) -> application.setOrganization(org.getId()));

        binder.forField(details)
              .withValidator(value -> value.length() <= 1024 , "Details cannot exceed 1024 characters")
              .bind(Application::getDetails, ((application, value) -> {
                  application.setDetails(value);
                  //Also bind state here because citizen cannot set it for themselves
                  application.setState(ApplicationState.SUBMITTED);
              }));

        binder.forField(appointmentDateTime)
              .asRequired(MessageConstants.REQUIRED)
              .bind(application -> {
                  if (application.getAppointmentDate() == null) return null;
                  return application.getAppointmentDate().toInstant()
                                  .atZone(VaadinSession.getCurrent().getAttribute(ZoneId.class))
                                  .toLocalDateTime();
                      } ,
                      (application, value) ->
                              application.setAppointmentDate(Date.
                                      from(value.atZone(VaadinSession.getCurrent().getAttribute(ZoneId.class))
                                              .toInstant())));

        return editor;

    }
}
