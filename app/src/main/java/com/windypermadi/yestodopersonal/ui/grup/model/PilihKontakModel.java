package com.windypermadi.yestodopersonal.ui.grup.model;

public class PilihKontakModel {
    String id_friend;
    String fullname;
    String image;

    public PilihKontakModel(String id_friend, String fullname, String image) {
        this.id_friend = id_friend;
        this.fullname = fullname;
        this.image = image;
    }

    public String getId_friend() {
        return id_friend;
    }

    public void setId_friend(String id_friend) {
        this.id_friend = id_friend;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
