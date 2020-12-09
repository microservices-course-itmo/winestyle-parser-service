package com.wine.to.up.winestyle.parser.service.repository;

import com.wine.to.up.winestyle.parser.service.domain.entity.Timing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface TimingRepository extends JpaRepository<Timing, Long>, JpaSpecificationExecutor<Timing> {
    Timing findFirstByOrderByIdDesc();
}
