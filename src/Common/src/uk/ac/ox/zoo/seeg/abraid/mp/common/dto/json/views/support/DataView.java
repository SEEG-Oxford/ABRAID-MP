package uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json.views.support;

/**
 * Adapted from https://github.com/martypitt/JsonViewExample.
 *
 * Interface defining composite object to wrap a json DTO & its target JsonView name.
 * @author martypitt
 */
interface DataView {
    /**
     * The JsonView identifier.
     * @return The view.
     */
    Class<? extends BaseView> getView();

    /**
     * The JSON DTO.
     * @return The DTO.
     */
    Object getData();
}
