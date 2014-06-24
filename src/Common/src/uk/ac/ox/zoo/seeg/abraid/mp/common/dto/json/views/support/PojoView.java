package uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json.views.support;

/**
 * Adapted from https://github.com/martypitt/JsonViewExample.
 *
 * A composite object to wrap a json DTO & its target JsonView name.
 * @author martypitt
 */
class PojoView implements DataView {
    private final Object data;
    private final Class<? extends BaseView> view;

    public PojoView(Object data, Class<? extends BaseView> view) {
        this.data = data;
        this.view = view;
    }

    @Override
    public Object getData() {
        return data;
    }

    @Override
    public Class<? extends BaseView> getView() {
        return view;
    }
}
