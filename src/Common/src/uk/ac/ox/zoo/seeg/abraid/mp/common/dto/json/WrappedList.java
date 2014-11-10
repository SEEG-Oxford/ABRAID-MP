package uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json;

import java.util.List;

/**
 * A wrapper to avoid using raw List objects as DTOs. This allows for filtering of compatible DTO types in
 * CSVMessageConverter when using CSV. This also helps avoid JSON hijacking when using JSON
 * and allows for more seamless extension of the DTO in the future.
 * http://haacked.com/archive/2008/11/20/anatomy-of-a-subtle-json-vulnerability.aspx
 *
 * @param <T> The type of the list elements.
 *
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
