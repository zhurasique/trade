package com.example.trade.repo;

import com.example.trade.domain.Instrument;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InstrumentRepo extends JpaRepository<Instrument, Long> {
    boolean existsByInstrument(String instrument);
    Instrument findByInstrument(String instrument);
}
