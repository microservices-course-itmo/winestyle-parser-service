package com.wine.to.up.winestyle.parser.service.repository;

import com.wine.to.up.winestyle.parser.service.domain.entity.Alcohol;
import com.wine.to.up.winestyle.parser.service.domain.entity.ErrorOnSaving;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ErrorOnSavingRepository
        extends JpaRepository<ErrorOnSaving, Long>, JpaSpecificationExecutor<ErrorOnSaving> {
    void save(Alcohol alcohol);
}
