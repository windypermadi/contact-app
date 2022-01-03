package com.windypermadi.yestodopersonal.ui.grup.tambah.model;

public class KontakGrupModel {
    String id_friend;
    String fullname;
    String image;
    private boolean check;

    public KontakGrupModel() {}

    public KontakGrupModel(String id_friend, String fullname, String image, boolean check) {
        this.id_friend = id_friend;
        this.fullname = fullname;
        this.image = image;
        this.check = check;
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

    public boolean isCheckbox() {
        return check;
    }

    public void setCheckbox(boolean check) {
        this.check = check;
    }
}
