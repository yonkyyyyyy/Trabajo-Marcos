package com.example.RDMProject.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DeliveryController {

    @GetMapping("/delivery")
    public String delivery() {
        return "delivery";
    }
}
