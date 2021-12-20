package com.lucis.relaylocation.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity(name = "location_map")
@Getter @Setter
public class LocationMap implements Serializable {
    private static final long serialVersionUID = 2886593245414532456L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id; // 아이디

    private String tagId; // 태그아이디
    private String macAddress; // Mac 주소
    private String lat; // 위도
    private String lon; // 경도
    private String address; // 주소
    private short rssi; // 수신 강도

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyyMMddHHmmss", timezone = "Asia/Seoul")
    private LocalDateTime sendDateTime; // 보낸일시

    @CreationTimestamp
    private LocalDateTime createdDateTime; // 등록일시

    @Transient
    private LocalDateTime schDate;

    @Transient
    private LocalDateTime loeCreatedDateTime;

    public static LocationMap of(String tagId, String macAddress, LocalDate schDate) {
        LocationMap locationMap = new LocationMap();
        locationMap.setTagId(tagId);
        locationMap.setMacAddress(macAddress);
        locationMap.setSchDate(schDate.atStartOfDay());

        return locationMap;
    }
}
