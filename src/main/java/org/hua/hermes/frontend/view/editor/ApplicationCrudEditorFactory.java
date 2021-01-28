package org.hua.hermes.frontend.view.editor;

import com.vaadin.componentfactory.enhancedcrud.BinderCrudEditor;
import com.vaadin.componentfactory.enhancedcrud.CrudEditor;
import com.vaadin.flow.component.accordion.Accordion;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.provider.DataProvider;
import org.apache.commons.validator.GenericValidator;
import org.hua.hermes.backend.entity.Application;
import org.hua.hermes.frontend.constant.DateTimeConstants;
import org.hua.hermes.frontend.converter.LocalDateToStringConverter;
import org.hua.hermes.frontend.view.presenter.OrganizationsCrudPresenter;
import org.keycloak.representations.idm.GroupRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Configuration
public class ApplicationCrudEditorFactory {

    private OrganizationsCrudPresenter organizationsCrudPresenter;

    @Bean
    @Scope(value = "prototype")
    public CrudEditor<Application> createApplicationBinder() {
//        ComboBox<GroupRepresentation> comboBox = new ComboBox<>();
//        comboBox.setLabel("Organizations");
//        List<GroupRepresentation> organizationsList = organizationsCrudPresenter.findAll(0, organizationsCrudPresenter.count()-1);



//        comboBox.setItemLabelGenerator(GroupRepresentation::getName);
//        comboBox.setItems(organizationsList);

        DataProvider<GroupRepresentation, Void> dataProvider = DataProvider.fromCallbacks(
                query -> {
                    int offset = query.getOffset();
                    int limit = query.getLimit();
                    List<GroupRepresentation> organizationsList = organizationsCrudPresenter.findAll(offset, limit);

                    return organizationsList.stream();
                },
                query -> organizationsCrudPresenter.count());

        ;
        Grid<GroupRepresentation> grid = new Grid<>();
        grid.setDataProvider(dataProvider);


        TextArea textArea = new TextArea("Application details");
        textArea.setPlaceholder("Write your application details");


        DateTimePicker dateTimePicker = new DateTimePicker();
        LocalDate today = LocalDate.now();
        LocalDateTime min = LocalDateTime.of(today, LocalTime.MIN);
        LocalDateTime max = LocalDateTime.of(today.plusYears(2), LocalTime.MAX);

        dateTimePicker.setMin(min);
        dateTimePicker.setMax(max);

        var basicInfoForm = new FormLayout(grid,textArea,dateTimePicker);

        Accordion accordion = new Accordion();
        accordion.add("Basic Info", basicInfoForm);

        var binder = new Binder<>(Application.class);
        var editor = new BinderCrudEditor<>(binder, accordion);

        return editor;
    }
}
