package com.example.kishorebaktha.blog;

import java.util.Date;

/**
 * Created by KISHORE BAKTHA on 7/31/2017.
 */

public class Blog {
    String title,desc,image,email,like;
     private long messageTime;
    public Blog()
    {
        messageTime=new Date().getTime();
    }

    public Blog(String title, String desc, String image,String email,String like) {
        this.title = title;
        this.desc = desc;
        this.image = image;
        this.like=like;
        this.email=email;
        messageTime=new Date().getTime();
    }

    public String getTitle() {
        return title;
    }

    public String getLike() {
        return like;
    }

    public long getMessageTime() {
        return messageTime;
    }

    public void setLike(String like) {
        this.like = like;
    }

    public String getDesc() {
        return desc;
    }

    public String getEmail() {
        return email;
    }

    public String getImage() {
        return image;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setMessageTime(long messageTime) {
        this.messageTime = messageTime;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
