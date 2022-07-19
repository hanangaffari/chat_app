package com.example.chat_app;

public class firebasemodel {
    String name;
    String Image;
    String uid;
    String status;
    String statusPost;
    int pesanbaru;

    public firebasemodel() {
    }

    public firebasemodel(String name, String Image, String uid, String status,String statuspost,int pesanbaru) {
        this.name = name;
        this.Image = Image;
        this.uid = uid;
        this.status = status;
        this.statusPost = statuspost;
        this.pesanbaru= pesanbaru;
    }



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String Image) {
        this.Image = Image;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getstatuspost() { return statusPost; }

    public void setstatuspost(String statuspost) { this.statusPost = statuspost; }

    public int getPesanbaru() { return pesanbaru; }

    public void setPesanbaru(int pesanbaru) { this.pesanbaru = pesanbaru; }
}
