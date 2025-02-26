package org.hua.hermes.frontend.error.handler;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.ErrorParameter;
import com.vaadin.flow.router.HasErrorParameter;
import de.codecamp.vaadin.security.spring.access.VaadinSecurity;
import de.codecamp.vaadin.security.spring.access.route.RouteAccessDeniedException;
import lombok.extern.log4j.Log4j2;

import javax.servlet.http.HttpServletResponse;

@Tag(Tag.DIV)
@Log4j2
public class RouteAccessDeniedHandler
  extends Component
  implements HasErrorParameter<RouteAccessDeniedException>
{

  @Override
  public int setErrorParameter(BeforeEnterEvent event, ErrorParameter<RouteAccessDeniedException> parameter)
  {
    log.warn("User {} attempted to access {}. Access has been denied.",
            VaadinSecurity.getAuthentication().getName(),
            event.getLocation().getPathWithQueryParameters());

    event.rerouteTo("403");
    return HttpServletResponse.SC_FORBIDDEN;
  }

}
