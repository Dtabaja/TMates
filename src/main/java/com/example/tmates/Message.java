package com.example.tmates;

import android.util.Patterns;

import java.util.regex.Pattern;

public class Message implements Comparable<Message>{
    private User author;
    private String messageId, senderId, receiverId, messageTitle, messageText, messageDate, email, phone;

    // Constructors.
    public Message(User author, String messageId, String senderId, String receiverId, String messageTitle, String messageText, String messageDate, String email, String phone) {
        this.author = author;
        this.messageId = messageId;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.messageTitle = messageTitle;
        this.messageText = messageText;
        this.messageDate = messageDate;
        this.email = email;
        this.phone = phone;
    }

    public Message(User author, String messageId, String senderId, String receiverId, String messageTitle, String messageText, String messageDate, String details) {
        this.author = author;
        this.messageId = messageId;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.messageTitle = messageTitle;
        this.messageText = messageText;
        this.messageDate = messageDate;
        Pattern pattern = Patterns.EMAIL_ADDRESS;
        if(pattern.matcher(details).matches()){
            this.email = details;
        } else {
            this.phone = details;
        }

    }

    public Message(){

    }


    // Getters and setters.
    public User getAuthor(){
        return this.author;
    }

    public void setAuthor(User author){
        this.author = author;
    }

    public String getMessageId(){
        return messageId;
    }

    public void setMessageId(String messageId){
        this.messageId = messageId;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getReceiverId(){
        return receiverId;
    }

    public void setReceiver(String receiverId){
        this.receiverId = receiverId;
    }

    public String getMessageTitle() {
        return messageTitle;
    }

    public void setMessageTitle(String messageTitle) {
        this.messageTitle = messageTitle;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public String getMessageDate() {
        return messageDate;
    }

    public void setMessageDate(String messageDate) {
        this.messageDate = messageDate;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Override
    public int compareTo(Message message) {
        return message.getMessageDate().compareTo(this.getMessageDate());
    }
}
