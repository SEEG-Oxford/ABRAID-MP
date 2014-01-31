package uk.ac.ox.zoo.seeg.abraid.mp.publicsite.domain;

/**
 * Copyright (c) 2014 University of Oxford
 */
public class NavBarItem {
    private String title;
    private String url;

    NavBarItem(String title, String url) {
        this.title = title;
        this.url = url;
    }

    public String getTitle() {
        return this.title;
    }

    public String getTemplate() {
        return "/" + this.url + ".ftl";
    }
}
