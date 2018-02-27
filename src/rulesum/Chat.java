/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rulesum;

import java.util.Date;

/**
 *
 * @author ACER
 */
public class Chat {
    private String sender;
    private String message;
    private String project;
    private String client;
    private String activity;
    private String group;
    private Date date;

    public String getSender() {
        return sender;
    }

    public String getMessage() {
        return message;
    }

    public String getProject() {
        return project;
    }

    public String getClient() {
        return client;
    }

    public String getActivity() {
        return activity;
    }

    public String getGroup() {
        return group;
    }

    public Date getDate() {
        return date;
    }

    public Chat(String sender, String message, String project, String client, String activity, String group, Date date) {
        this.sender = sender;
        this.message = message;
        this.project = project;
        this.client = client;
        this.activity = activity;
        this.group = group;
        this.date = date;
    }
    
    
}
