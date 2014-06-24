package uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json.views.support;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.servlet.mvc.method.annotation.HttpEntityMethodProcessor;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestResponseBodyMethodProcessor;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapted from https://github.com/martypitt/JsonViewExample.
 *
 * Modified Spring's internal Return value handlers, and wires up a decorator to add support for @JsonView.
 * @author martypitt
 */
public class JsonViewSupportFactoryBean implements InitializingBean {

    @Autowired
    private RequestMappingHandlerAdapter adapter;

    @Override
    public void afterPropertiesSet() throws Exception {
        List handlers = new ArrayList();
        for (HandlerMethodReturnValueHandler handler : adapter.getReturnValueHandlers()) {
            if (handler instanceof RequestResponseBodyMethodProcessor || handler instanceof HttpEntityMethodProcessor) {
                ViewInjectingReturnValueHandler decorator = new ViewInjectingReturnValueHandler(handler);
                handlers.add(decorator);
            } else {
                handlers.add(handler);
            }
        }
        adapter.setReturnValueHandlers(handlers);
    }
}
