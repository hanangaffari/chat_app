package com.example.chat_app.status;

public class firebasemodel {
    String name;
    String Image;
    String uid;
    String status;
    String statusPost;
    String statustext;

    public firebasemodel() {
    }

    public firebasemodel(String name, String Image, String uid, String status, String statuspost , String statustext) {
        this.name = name;
        this.Image = Image;
        this.uid = uid;
        this.status = status;
        this.statusPost = statuspost;
        this.statustext = statustext;
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

    public String getStatustext() {
        return statustext;
    }

    public void setStatustext(String statustext) {
        this.statustext = statustext;
    }
}
