package com.example.trade.controller;

import com.example.trade.domain.Instrument;
import com.example.trade.repo.InstrumentRepo;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class InstrumentController {
    private final InstrumentRepo instrumentRepo;

    @Autowired
    public InstrumentController(InstrumentRepo instrumentRepo) {
        this.instrumentRepo = instrumentRepo;
    }

    // GET request to see last values for all instruments
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
        return createdArray.toString();
    }

    // POST request to add instrument to db
    @PostMapping
    public Instrument create(String instrument, double bid, double ask, double last, String time){
        Instrument createdInstrument = new Instrument();
        // Deleting instrument from db, to have only last one in it
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

