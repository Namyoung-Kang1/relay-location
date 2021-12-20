package com.lucis.relaylocation.schedule;

import com.lucis.relaylocation.domain.LocationMap;
import com.lucis.relaylocation.repository.LocationMapRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class LocationMapScheduler {

    private final LocationMapRepository locationMapRepository;

    /**
     * <p>보관기간이 지난 데이터들은 삭제합니다.</p>
     */
    @Scheduled(cron = "${relay-location.schedule.cron.delete}")
    @Transactional(value="socketTransactionManager")
    public void deleteLocationMap() {
        LocationMap locationMap = new LocationMap();
        locationMap.setLoeCreatedDateTime(LocalDateTime.now().minusMonths(3));
        List<LocationMap> locationMaps = locationMapRepository.findList(locationMap);

        if (locationMaps != null && locationMaps.size() > 0) {
            locationMaps.forEach(delLocationMap -> locationMapRepository.delete(delLocationMap));
        }
    }

}
