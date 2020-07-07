package com.example.trade.controller;

import com.example.trade.domain.Instrument;
import com.example.trade.repo.InstrumentRepo;
import com.example.trade.service.InstrumentService;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class InstrumentController {
    private final InstrumentService instrumentService;

    @Autowired
    public InstrumentController(InstrumentService instrumentService) {
        this.instrumentService = instrumentService;
    }

    // GET request to see last values for all instruments
    @GetMapping
    public String list(){
        return instrumentService.findAll();
    }

    // POST request to add instrument to db
    @PostMapping
    public Instrument create(String instrument, double bid, double ask, double last, String time){
        return instrumentService.create(instrument, bid, ask, last, time);
    }
}

