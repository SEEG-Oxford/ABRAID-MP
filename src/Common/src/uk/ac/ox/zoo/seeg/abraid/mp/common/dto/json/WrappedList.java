package uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json;

import java.util.List;

/**
 * foo
 * Copyright (c) 2014 University of Oxford
 */
public class WrappedList<T> {
    private List<T> list;

    public WrappedList(List<T> list) {
        this.list = list;
    }

    public WrappedList() {
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }
}
