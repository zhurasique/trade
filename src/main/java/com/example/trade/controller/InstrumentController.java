package com.example.trade.controller;


import com.example.trade.domain.Instrument;
import com.example.trade.repo.InstrumentRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api")
public class InstrumentController {
    private final InstrumentRepo instrumentRepo;

    @Autowired
    public InstrumentController(InstrumentRepo instrumentRepo) {
        this.instrumentRepo = instrumentRepo;
    }

    @GetMapping
    public List<Instrument> list(){
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

