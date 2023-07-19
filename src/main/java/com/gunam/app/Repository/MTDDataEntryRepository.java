package com.gunam.app.Repository;

import com.gunam.app.Entity.MTDData;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MTDDataEntryRepository extends JpaRepository<MTDData, Long> {
    MTDData findByDateAndTime(String date, String time);
}
