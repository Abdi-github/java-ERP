package ch.swiftapp.erp.qualitycontrol.service;

import ch.swiftapp.erp.qualitycontrol.QualityControlModuleApi;
import ch.swiftapp.erp.qualitycontrol.repository.NonConformanceReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.UUID;

@Service @RequiredArgsConstructor
public class QualityControlModuleApiFacade implements QualityControlModuleApi {
    private final NonConformanceReportRepository ncrRepo;
    @Override public boolean hasOpenNcrs(UUID productionOrderId) { return ncrRepo.hasOpenNcrsForProductionOrder(productionOrderId); }
}

