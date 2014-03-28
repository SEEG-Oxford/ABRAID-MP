package uk.ac.ox.zoo.seeg.abraid.mp.common.web.json.views.support;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface ResponseView {
    public Class<? extends BaseView> value();
}
