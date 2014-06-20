package uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json.views.support;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Adapted from https://github.com/martypitt/JsonViewExample.
 *
 * An annotation to specify the JsonView to use in a RequestMapping.
 * @author martypitt
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface ResponseView {
    Class<? extends BaseView> value();
}
