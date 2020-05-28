package com.example.trade.controller;


import com.example.trade.domain.Instrument;
import com.example.trade.repo.InstrumentRepo;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class InstrumentController {
    private final InstrumentRepo instrumentRepo;

    @Autowired
    public InstrumentController(InstrumentRepo instrumentRepo) {
        this.instrumentRepo = instrumentRepo;
    }

    @GetMapping
    public String list(){
        // Mapped to here, to hide id on returned json
        List<Instrument> list = instrumentRepo.findAll();
        JsonArray createdArray = new JsonArray();

        for(Instrument instrument : list){
            JsonObject createdObject = new JsonObject();
            createdObject.addProperty("instrument", instrument.getInstrument());
            createdObject.addProperty("bid", instrument.getBid());
            createdObject.addProperty("ask", instrument.getAsk());
            createdObject.addProperty("last", instrument.getLast());
            createdObject.addProperty("time", instrument.getTime());

            createdArray.add(createdObject);
        }

        // Changed from JsonArray to String, because was troubles with returning json
        String st = String.valueOf(createdArray);
        return st;
    }

    // If u want to see id's of instruments
    @GetMapping("/ids")
    public List<Instrument> listWithIds(){
        return instrumentRepo.findAll();
    }

    @PostMapping
    public Instrument create(String instrument, double bid, double ask, double last, String time){
        Instrument createdInstrument = new Instrument();
        delete(instrument);

        createdInstrument.setInstrument(instrument);
        createdInstrument.setBid(bid);
        createdInstrument.setAsk(ask);
        createdInstrument.setLast(last);
        createdInstrument.setTime(time);

        return instrumentRepo.save(createdInstrument);
    }

    public void delete(String instrument){
        if(instrumentRepo.existsByInstrument(instrument)) {
            long id = instrumentRepo.findByInstrument(instrument).getId();
            instrumentRepo.deleteById(id);
        }
    }
}

