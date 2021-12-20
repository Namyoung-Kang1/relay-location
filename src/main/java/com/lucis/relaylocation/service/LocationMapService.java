package com.lucis.relaylocation.service;

import com.lucis.relaylocation.domain.LocationMap;

import java.util.List;

public interface LocationMapService {
    void save(LocationMap locationMap);

    List<LocationMap> getLocationMaps(LocationMap locationMap);

    LocationMap getLocationMap(Long locationMapId);
}
