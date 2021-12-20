package com.lucis.relaylocation.api;

import com.lucis.relaylocation.domain.LocationMap;
import com.lucis.relaylocation.service.LocationMapService;
import com.lucis.relaylocation.util.CommonConstants;
import com.lucis.relaylocation.util.JsonResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Slf4j
@RestController
@RequestMapping("/api/location-map")
@RequiredArgsConstructor
public class LocationMapApiResource {

    private final LocationMapService locationMapService;

    /**
     * 사용자 위치 목록을 가져옵니다.
     * @param tagId
     * @param macAddress
     * @param schDate
     * @param authkey
     * @return
     */
    @GetMapping
    public ResponseEntity getList(@RequestParam(value = "tagId", required = false) String tagId
                            , @RequestParam(value = "macAddress", required = false) String macAddress
                            , @RequestParam(value = "schDate", required = false) LocalDate schDate
                            , @RequestHeader(value = "authKey") String authkey) {
        JsonResult jsonResult = new JsonResult();

        if (CommonConstants.AUTH_KEY.equals(authkey)) {
            try {
                if (schDate == null) {
                    schDate = LocalDate.now();
                }

                jsonResult.add("locationMaps", locationMapService.getLocationMaps(LocationMap.of(tagId, macAddress, schDate)));
            } catch (Exception e) {
                jsonResult.setResult(-1, "FAIL", "불러오기 실패했습니다.");
            }
        } else {
            jsonResult.setResult(-403, "FAIL", "auth key error");
            return new ResponseEntity<>(jsonResult, HttpStatus.BAD_REQUEST);
        }

        return ResponseEntity.ok(jsonResult);
    }

    /**
     * 사용자 위치를 가져옵니다.
     * @param authkey
     * @return
     */
    @GetMapping("/get")
    public ResponseEntity get(@RequestParam("locationMapId") Long locationMapId
                            , @RequestHeader(value = "authKey") String authkey) {
        JsonResult jsonResult = new JsonResult();

        if (CommonConstants.AUTH_KEY.equals(authkey)) {
            try {
                jsonResult.add("locationMap", locationMapService.getLocationMap(locationMapId));
            } catch (Exception e) {
                jsonResult.setResult(-1, "FAIL", "불러오기 실패했습니다.");
            }
        } else {
            jsonResult.setResult(-403, "FAIL", "auth key error");
            return new ResponseEntity<>(jsonResult, HttpStatus.BAD_REQUEST);
        }

        return ResponseEntity.ok(jsonResult);
    }

    /**
     * 사용자 위치를 저장합니다
     * @param locationMap
     * @param authkey
     * @return
     */
    @PostMapping
    public ResponseEntity add(@RequestBody LocationMap locationMap, @RequestHeader(value = "authKey") String authkey) {
        JsonResult jsonResult = new JsonResult();

        if (CommonConstants.AUTH_KEY.equals(authkey)) {
            try {
                locationMapService.save(locationMap);
            } catch (Exception e) {
                jsonResult.setResult(-1, "FAIL", "저장 실패했습니다.");
            }
        } else {
            jsonResult.setResult(-403, "FAIL", "auth key error");
            return new ResponseEntity<>(jsonResult, HttpStatus.BAD_REQUEST);
        }

        return ResponseEntity.ok(jsonResult);
    }
}
