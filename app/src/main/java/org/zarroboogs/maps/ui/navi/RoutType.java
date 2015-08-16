package org.zarroboogs.maps.ui.navi;

/**
 * Created by andforce on 15/8/16.
 */
public class RoutType {

    public RoutType(String name, int type) {
        this.name = name;
        this.type = type;
    }

    private String name;
    private int type;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
