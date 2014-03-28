package uk.ac.ox.zoo.seeg.abraid.mp.common.web.json.views.support;

public class PojoView implements DataView {

    private final Object data;
    private final Class<? extends BaseView> view;

    public PojoView(Object data, Class<? extends BaseView> view) {
        this.data = data;
        this.view = view;
    }

    @Override
    public boolean hasView() {
        return true;
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
