package com.lucis.relaylocation.service;

import com.lucis.relaylocation.domain.LocationMap;
import com.lucis.relaylocation.repository.LocationMapRepository;
import com.lucis.relaylocation.util.ARIA256Util;
import com.lucis.relaylocation.util.JsonReader;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
@RequiredArgsConstructor
public class LocationMapServiceImpl implements LocationMapService {

    private final LocationMapRepository locationMapRepository;
    private final Environment env;

    /**
     * <p>사용자 위치를 저장</p>
     *
     * @param locationMap
     */
    @Override
    public void save(LocationMap locationMap) {
        List<LocationMap> locationMaps = locationMapRepository.findList(locationMap);

        if (locationMaps != null && locationMaps.size() > 2) {
            LocationMap minLocationMap = locationMaps.stream().min(Comparator.comparingInt(LocationMap::getRssi)).get();
            if (minLocationMap.getRssi() < locationMap.getRssi()) { // 기존 저장되있는 수신강도가 새로 들어온 수신강도보다 약할 경우 저장
                locationMapRepository.delete(minLocationMap);

                locationMap.setAddress(this.getAddress(locationMap.getLat(), locationMap.getLon(), locationMap.getSendDateTime()));
                locationMapRepository.save(locationMap);
            }
        } else {
            locationMap.setAddress(this.getAddress(locationMap.getLat(), locationMap.getLon(), locationMap.getSendDateTime()));
            locationMapRepository.save(locationMap);
        }
    }

    /**
     * <p>사용자 위치 목록</p>
     * @param locationMap
     * @return
     */
    @Override
    public List<LocationMap> getLocationMaps(LocationMap locationMap) {
        return locationMapRepository.findList(locationMap);
    }

    /**
     * <p>요청하는 사용자 위치를 반환</p>
     * @param locationMapId
     * @return
     */
    @Override
    public LocationMap getLocationMap(Long locationMapId) {
        LocationMap locationMap = locationMapRepository.findById(locationMapId);

        if (locationMap.getAddress() == null || locationMap.getAddress().equalsIgnoreCase("")) {
            locationMap.setAddress(this.getAddress(locationMap.getLat(), locationMap.getLon(), locationMap.getSendDateTime()));
        }

        return locationMap;
    }

    private String getAddress(String lat, String lon, LocalDateTime sendDateTime) {
        ARIA256Util aria256Util = new ARIA256Util();
        aria256Util.setAriaKeyGenerator(sendDateTime.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")));

//        lat = aria256Util.Decrypt(lat);
//        lon = aria256Util.Decrypt(lon);
        
        String result = null;

        String apiURL = env.getProperty("relay-location.gecoder.url");

        JsonReader jsonReader = new JsonReader(); // api 키값
        String reverseGeocodeURL = apiURL + "?";
        reverseGeocodeURL += "service=address";
        reverseGeocodeURL += "&request=getAddress";
        reverseGeocodeURL += "&version=2.0";
        reverseGeocodeURL += "&crs=epsg:4326";
        reverseGeocodeURL += "&zipcode=true";
        reverseGeocodeURL += "&simple=false";
        reverseGeocodeURL += "&format=json";
        reverseGeocodeURL += "&type=road";
        reverseGeocodeURL += "&errorFormat=json";
        reverseGeocodeURL += "&key=" + env.getProperty("relay-location.gecoder.key");
        reverseGeocodeURL += "&point=" + lat + "," + lon;

        String getJson = jsonReader.callURL(reverseGeocodeURL);
        Map<String, Object> map = jsonReader.string2Map(getJson);
        HashMap<String, Object> response = (HashMap<String, Object>) map.get("response");
        String status = (String) response.get("status");

        if (status.equalsIgnoreCase("OK")) {
            ArrayList geocodeResultArr = (ArrayList) response.get("result");
            HashMap<String, Object> tmp = (HashMap<String, Object>) geocodeResultArr.get(0);
            if (tmp != null) {
                result = (String) tmp.get("text");
            }
        }

        return result;
    }
}
