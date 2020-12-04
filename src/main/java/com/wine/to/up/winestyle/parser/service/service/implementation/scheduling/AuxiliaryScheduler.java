package com.wine.to.up.winestyle.parser.service.service.implementation.scheduling;

import com.wine.to.up.winestyle.parser.service.components.WinestyleParserServiceMetricsCollector;
import com.wine.to.up.winestyle.parser.service.service.RepositoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuxiliaryScheduler {
    private final RepositoryService repositoryService;

    @Scheduled(fixedRateString = "${spring.task.scheduling.rate.auxiliary.fixed}")
    public void onScheduleUpdateLastSucceed() {
        WinestyleParserServiceMetricsCollector.updateSinceLastSucceed(repositoryService.sinceLastSucceedParsing());
    }
}
