package uk.ac.ox.zoo.seeg.abraid.mp.common.web.json.views.support;

public interface DataView {
    boolean hasView();

    Class<? extends BaseView> getView();

    Object getData();
}
