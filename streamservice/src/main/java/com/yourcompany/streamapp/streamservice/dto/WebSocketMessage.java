package com.yourcompany.streamapp.streamservice.dto;

public class WebSocketMessage {
    private String type;
    private String sender; // The username of the sender
    private Object payload;

    // Getters and Setters
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getSender() { return sender; }
    public void setSender(String sender) { this.sender = sender; }
    public Object getPayload() { return payload; }
    public void setPayload(Object payload) { this.payload = payload; }
}