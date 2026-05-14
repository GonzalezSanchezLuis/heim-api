package com.heim.api.notification.application.service.email;

public interface EmailSender {
    public void send(String to, String subject, String html, String from);
}
