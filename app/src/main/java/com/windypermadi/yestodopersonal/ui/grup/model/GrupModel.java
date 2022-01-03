package com.windypermadi.yestodopersonal.ui.grup.model;

public class GrupModel {
    String id_group;
    String name;
    String image;
    String counting;

    public GrupModel(String id_group, String name, String image, String counting) {
        this.id_group = id_group;
        this.name = name;
        this.image = image;
        this.counting = counting;
    }

    public String getId_group() {
        return id_group;
    }

    public String getName() {
        return name;
    }

    public String getImage() {
        return image;
    }

    public String getCounting() {
        return counting;
    }
}
