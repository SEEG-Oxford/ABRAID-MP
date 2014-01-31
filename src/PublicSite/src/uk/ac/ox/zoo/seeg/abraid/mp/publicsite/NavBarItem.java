package uk.ac.ox.zoo.seeg.abraid.mp.publicsite;

/**
 * Created by zool1250 on 28/01/14.
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
