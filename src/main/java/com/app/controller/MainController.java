package com.app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class MainController {


    @RequestMapping(value="/", method= RequestMethod.GET)
    public String index() {
        return "index";
    }


}
