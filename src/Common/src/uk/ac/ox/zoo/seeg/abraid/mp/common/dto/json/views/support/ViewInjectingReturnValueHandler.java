package uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json.views.support;

import org.springframework.core.MethodParameter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 * Adapted from https://github.com/martypitt/JsonViewExample.
 *
 * Decorator that detects a declared {@link ResponseView}, and injects support if required.
 * @author martypitt
 */
class ViewInjectingReturnValueHandler implements
        HandlerMethodReturnValueHandler {

    private final HandlerMethodReturnValueHandler delegate;

    public ViewInjectingReturnValueHandler(HandlerMethodReturnValueHandler delegate) {
        this.delegate = delegate;
    }

    @Override
    public boolean supportsReturnType(MethodParameter returnType) {
        return delegate.supportsReturnType(returnType);
    }

    @Override
    public void handleReturnValue(Object returnValue,
                                  MethodParameter returnType, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest) throws Exception {

        Class<? extends BaseView> viewClass = getDeclaredViewClass(returnType);
        if (viewClass != null) {
            returnValue = wrapResult(returnValue, viewClass);
        }

        delegate.handleReturnValue(returnValue, returnType, mavContainer, webRequest);
    }

    /**
     * Returns the view class declared on the method, if it exists.
     * Otherwise, returns null.
     * @param returnType Used to link back to the RequestMapping controller method
     * @return The view class declared on the method
     */
    private Class<? extends BaseView> getDeclaredViewClass(MethodParameter returnType) {
        ResponseView annotation = returnType.getMethodAnnotation(ResponseView.class);
        if (annotation != null) {
            return annotation.value();
        } else {
            return null;
        }
    }

    private Object wrapResult(Object result, Class<? extends BaseView> viewClass) {
        Object response;
        if (result instanceof ResponseEntity) {
            // Really we just want to wrap the body of the response entity in a PojoView
            // but it's immutable, so we will have to build a new one
            ResponseEntity responseEntity = (ResponseEntity) result;
            response = new ResponseEntity<>(
                    new PojoView(responseEntity.getBody(), viewClass),
                    responseEntity.getHeaders(),
                    responseEntity.getStatusCode());
        } else {
            response = new PojoView(result, viewClass);
        }

        return response;
    }
}
