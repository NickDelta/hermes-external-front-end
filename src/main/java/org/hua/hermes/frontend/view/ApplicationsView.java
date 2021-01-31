package org.hua.hermes.frontend.view;

import com.vaadin.componentfactory.enhancedcrud.*;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.*;
import de.codecamp.vaadin.security.spring.access.SecuredAccess;
import org.hua.hermes.backend.entity.Application;
import org.hua.hermes.backend.entity.ApplicationState;
import org.hua.hermes.frontend.component.StatusBadge;
import org.hua.hermes.frontend.constant.CrudConstants;
import org.hua.hermes.frontend.constant.RouteConstants;
import org.hua.hermes.frontend.constant.SecurityConstants;
import org.hua.hermes.frontend.repository.ApplicationRepository;
import org.hua.hermes.frontend.repository.OrganizationRepository;
import org.hua.hermes.frontend.util.TemplateUtil;
import org.hua.hermes.frontend.util.UIUtils;
import org.hua.hermes.frontend.util.style.css.lumo.BadgeColor;
import org.hua.hermes.frontend.util.style.css.lumo.BadgeShape;
import org.hua.hermes.frontend.util.style.css.lumo.BadgeSize;
import org.hua.hermes.frontend.view.presenter.ApplicationsCrudPresenter;
import org.hua.hermes.frontend.view.presenter.OrganizationCrudPresenter;
import org.springframework.beans.factory.annotation.Autowired;
import static org.hua.hermes.frontend.constant.DateTimeConstants.DATETIME_FORMATTER;

@Route(value = RouteConstants.ROUTE_APPLICATIONS, layout = MainLayout.class)
@PageTitle(RouteConstants.ROUTE_APPLICATIONS_PAGETITLE)
@SecuredAccess(SecurityConstants.SECURITY_CITIZEN)
public class ApplicationsView
        extends Crud<Application>
        implements HasNotifications, HasUrlParameter<String>, HasStyle {

    private final OrganizationCrudPresenter organizationPresenter;
    private final ApplicationsCrudPresenter applicationsPresenter;

    public ApplicationsView(@Autowired ApplicationRepository applicationRepository,
                            @Autowired OrganizationRepository organizationRepository,
                            @Autowired CrudEditor<Application> editor) {
        super(Application.class, new Grid<>(),editor);

        applicationsPresenter = new ApplicationsCrudPresenter(applicationRepository,this);
        organizationPresenter = new OrganizationCrudPresenter(organizationRepository);

        this.setEditorPosition(CrudEditorPosition.ASIDE);
        this.getGrid().setSelectionMode(Grid.SelectionMode.SINGLE);

        this.getGrid().setDataProvider(DataProvider.fromCallbacks(
                fetch -> applicationsPresenter.findAll(fetch.getOffset(),fetch.getLimit()).stream(),
                count -> applicationsPresenter.count()));

        //Disable delete
        this.getDelete().setEnabled(false);
        this.getDelete().getStyle().set("visibility","hidden");

        setupGrid(getGrid());
        setI18n(setupI18n());
        setupEventListeners();
        setSizeFull();
    }

    public CrudI18n setupI18n()
    {
        CrudI18n crudI18n = CrudI18n.createDefault();
        crudI18n.setNewItem("New " + Application.ENTITY_NAME);
        crudI18n.setEditItem("Edit " + Application.ENTITY_NAME);
        crudI18n.setEditLabel("Edit" + Application.ENTITY_NAME);
        crudI18n.getConfirm().getCancel().setContent(CrudConstants.DISCARD_MESSAGE);
        crudI18n.getConfirm().getDelete().setContent(String.format(CrudConstants.DELETE_MESSAGE, Application.ENTITY_NAME));
        crudI18n.setDeleteItem("Delete");
        return crudI18n;
    }

    public void setupEventListeners() {

        this.addSaveListener(e -> {
            var application = e.getItem();
            if(application.getId() != null){
                if (!applicationsPresenter.update(application))
                    this.cancelSave();
            } else {
                if (!applicationsPresenter.save(application))
                    this.cancelSave();
            }
        });

        this.addEditListener(e -> {
            this.getEditor().setItem(e.getItem());
            navigateToApplication(e.getItem().getId());
        });

        this.addCancelListener(e -> navigateToApplication(null));
    }

    private void navigateToApplication(String id) {
        getUI().ifPresent(ui -> ui.navigate(TemplateUtil.generateLocation(RouteConstants.ROUTE_APPLICATIONS, id)));
    }

    protected void setupGrid(Grid<Application> grid)
    {
        grid.addColumn(Application::getId).setHeader(Application.ID_LABEL);
        grid.addColumn(Application::getCreatedBy).setHeader(Application.CREATED_BY_LABEL);
        grid.addColumn((application) -> DATETIME_FORMATTER.format(application.getCreatedDate())).setHeader(Application.CREATED_DATE_LABEL);
        grid.addColumn(Application::getLastModifiedBy).setHeader(Application.LAST_MODIFIED_BY_LABEL);
        grid.addColumn((application) -> DATETIME_FORMATTER.format(application.getLastModifiedDate())).setHeader(Application.LAST_MODIFIED_ON_LABEL);

        grid.addComponentColumn(application -> {
            BadgeColor color;
            String status;
            switch (application.getState()){
                case APPROVED:
                case SUBMITTED:
                case COMPLETED:
                    color = BadgeColor.SUCCESS_PRIMARY;
                    break;
                case CANCELED:
                case REJECTED:
                    color = BadgeColor.ERROR_PRIMARY;
                    break;
                case RESUBMISSION_REQUIRED:
                    color = BadgeColor.CONTRAST_PRIMARY;
                    break;
                default:
                    throw new IllegalStateException("Illegal application state");
            }
            status = application.getState().getName();
            var badge = new StatusBadge(status, color, BadgeSize.M, BadgeShape.PILL);
            badge.getElement().setProperty("title", status);
            return badge;
        }).setHeader(Application.STATE_LABEL);

        grid.addColumn((application) -> DATETIME_FORMATTER.format(application.getAppointmentDate()))
                .setHeader(Application.APPOINTMENT_DATE_LABEL);

        grid.setItemDetailsRenderer(new ComponentRenderer<>(item -> {
            var label = new Label("Details:");
            var text = new Label(item.getDetails());
            text.getStyle().set("font-weight", "bold");
            return new HorizontalLayout(label,text);
        }));

        grid.addColumn(application -> organizationPresenter.findById(application.getOrganization()).get().getName())
                .setHeader(Application.ORGANIZATION_LABEL);

        grid.addComponentColumn(application -> {
            var button = UIUtils.createButton("Edit",VaadinIcon.EDIT, ButtonVariant.LUMO_ICON);
            if(!application.getState().equals(ApplicationState.RESUBMISSION_REQUIRED))
                button.setEnabled(false);
            button.addClickListener(listener -> edit(application, EditMode.EXISTING_ITEM));
            return button;
        });

        grid.addComponentColumn(application -> {
            var button = UIUtils.createButton("Cancel",VaadinIcon.CLOSE,ButtonVariant.LUMO_ERROR);
            if(application.getState().equals(ApplicationState.CANCELED) ||
               application.getState().equals(ApplicationState.COMPLETED) ||
               application.getState().equals(ApplicationState.REJECTED))
                    button.setEnabled(false);

            button.addClickListener(listener -> {

               ConfirmDialog dialog = new ConfirmDialog();

               var confirmButton = UIUtils.createButton("Confirm",ButtonVariant.LUMO_PRIMARY);
               confirmButton.addClickListener(l -> {
                   application.setState(ApplicationState.CANCELED);
                   applicationsPresenter.update(application);
                   dialog.close();
                   this.getGrid().getDataProvider().refreshAll();
               });

               var cancelButton = UIUtils.createButton("Cancel",ButtonVariant.LUMO_TERTIARY);
               cancelButton.addClickListener(l -> dialog.close());

               dialog.setConfirmButton(confirmButton);
               dialog.setCancelButton(cancelButton);

               Text text = new Text("Are you sure you want to cancel this application ? This action cannot be undone.");
               VerticalLayout layout = new VerticalLayout(text);
               layout.setDefaultHorizontalComponentAlignment(FlexComponent.Alignment.CENTER);
               dialog.add(layout);
               dialog.open();

            });
            return button;
        });

        grid.getColumns().forEach(column -> column.setResizable(true).setAutoWidth(true));
    }

    @Override
    public void setParameter(BeforeEvent event, @OptionalParameter String id)
    {
        if (id != null) {
            var application = getEditor().getItem();
            if (application != null && id.equals(application.getId())) {
                return;
            }
            try {
                application = applicationsPresenter
                        .findById(id)
                        .orElseThrow(NotFoundException::new);
                if(application.getState().equals(ApplicationState.RESUBMISSION_REQUIRED))
                    edit(application, EditMode.EXISTING_ITEM);
                else{
                    Dialog dialog = new Dialog();
                    dialog.setCloseOnEsc(true);
                    dialog.setCloseOnOutsideClick(true);

                    Text text = new Text("You cannot edit this application.");
                    Button okButton = new Button("OK", e -> dialog.close());
                    VerticalLayout layout = new VerticalLayout(text,okButton);
                    layout.setDefaultHorizontalComponentAlignment(FlexComponent.Alignment.CENTER);
                    dialog.add(layout);
                    dialog.open();
                }
            } catch (Exception ex){
                throw new RuntimeException(ex);
            }
        } else {
            setOpened(false);
        }
    }
}
