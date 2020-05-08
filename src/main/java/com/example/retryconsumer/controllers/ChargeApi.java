package com.example.retryconsumer.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping(value = "/api/charge")
public class ChargeApi {

    /**
     * endpoint to fetch credits
     */
    @RequestMapping(value = "/creits", method = RequestMethod.GET)
    @ResponseBody
    public String fetchCredits(){
        return "hello testing pull request on github";
    }
}
