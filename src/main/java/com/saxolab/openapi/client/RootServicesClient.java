package com.saxolab.openapi.client;

import com.saxolab.openapi.model.root.Session;
import com.saxolab.openapi.model.root.User;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PutExchange;

@HttpExchange("/root/v1")
public interface RootServicesClient {

    @GetExchange("/sessions/capabilities")
    Session getSessionCapabilities();

    @GetExchange("/user")
    User getUser();

    @PutExchange("/sessions/capabilities")
    void changeSessionCapabilities();
}
