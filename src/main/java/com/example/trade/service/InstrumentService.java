package com.example.trade.service;

import com.example.trade.domain.Instrument;
import com.example.trade.repo.InstrumentRepo;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Service
public class InstrumentService {
    private final InstrumentRepo instrumentRepo;

    public InstrumentService(InstrumentRepo instrumentRepo) {
        this.instrumentRepo = instrumentRepo;
    }

    public String findAll(){
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

    public void delete(String instrument){
        if(instrumentRepo.existsByInstrument(instrument)) {
            long id = instrumentRepo.findByInstrument(instrument).getId();
            instrumentRepo.deleteById(id);
        }
    }

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
}
