package org.hua.hermes.frontend.error.handler;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.router.*;
import de.codecamp.vaadin.security.spring.access.route.RouteAccessDeniedException;
import lombok.extern.log4j.Log4j2;

import javax.servlet.http.HttpServletResponse;

  @Tag(Tag.DIV)
  @Log4j2
  class NotFoundExceptionHandler extends RouteNotFoundError
  {
    @Override
    public int setErrorParameter(BeforeEnterEvent event, ErrorParameter<NotFoundException> parameter) {
      //FIXME lots of errors with sw.js appear on logs but they are not real 404's.
      // Application runs normally. I don't know what's causing this.
      // Disabling logging for now
      //log.error(parameter.getException());
      event.rerouteTo("404");
      return HttpServletResponse.SC_NOT_FOUND;
    }
  }

  @Tag(Tag.DIV)
  @Log4j2
  class InternalServerErrorExceptionHandler extends InternalServerError
  {

    @Override
    public int setErrorParameter(BeforeEnterEvent event, ErrorParameter<Exception> parameter)
    {
      log.error(parameter.getException());
      event.rerouteTo("500");
      return HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
    }

  }

  @Tag(Tag.DIV)
  @Log4j2
  class ForbiddenExceptionHandler
          extends Component
          implements HasErrorParameter<RouteAccessDeniedException>
  {

    @Override
    public int setErrorParameter(BeforeEnterEvent event, ErrorParameter<RouteAccessDeniedException> parameter)
    {
      log.error(parameter.getException());
      event.rerouteTo("403");
      return HttpServletResponse.SC_FORBIDDEN;
    }

  }




