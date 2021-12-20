package com.lucis.relaylocation.repository;

import com.lucis.relaylocation.domain.LocationMap;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static com.lucis.relaylocation.domain.QLocationMap.locationMap;

@Repository
public class LocationMapRepository extends QuerydslRepositorySupport {

    private EntityManager em;
    private JPAQueryFactory queryFactory;

    @Autowired
    private Environment env;

    public LocationMapRepository() {
        super(LocationMap.class);
    }

    @Override
    @PersistenceContext(unitName="mainEntityManager")
    public void setEntityManager(EntityManager entityManager) {
        super.setEntityManager(entityManager);
        this.em = entityManager;
        this.queryFactory = new JPAQueryFactory(entityManager);
    }

    /**
     * 위치 정보를 저장합니다.
     * @param locationMap
     */
    public void save(LocationMap locationMap) {
        em.persist(locationMap);
    }

    /**
     * 위치 정보를 삭제합니다.
     * @param locationMap
     */
    public void delete(LocationMap locationMap) {
        em.remove(locationMap);
    }

    /**
     * 지정된 위치 정보를 조회합니다.
     *
     * @param id 일련번호
     * @return Optional product
     */
    public LocationMap findById(long id) {
        return em.find(LocationMap.class, id);
    }

    /**
     * 지정된 위치 정보를 조회합니다.
     *
     * @param search 위치 정보
     * @return Optional product
     */
    public LocationMap find(LocationMap search) {
        return this.getQuery(search).fetchOne();
    }

    /**
     * 위치 정보 목록을 조회합니다.
     * @param search
     * @return
     */
    public List<LocationMap> findList(LocationMap search) {
        return this.getQuery(search).fetch();
    }

    /**
     * 쿼리를 구합니다.
     *
     * @param search 검색
     * @return 값
     */
    private JPQLQuery<LocationMap> getQuery(LocationMap search) {
        JPQLQuery<LocationMap> query = queryFactory.selectFrom(locationMap);

        // 검색조건 추가
        List<Predicate> schConds = this.getSchConds(search);
        if (schConds.size() > 0) {
            query.where(schConds.stream().toArray(Predicate[]::new));
        }

        return query;
    }

    /**
     * 검색조건를 생성합니다.
     *
     * @param search 검색
     * @return 목록
     */
    private List<Predicate> getSchConds(LocationMap search) {
        List<Predicate> schConds = new ArrayList<>();

        if (search != null) {
            if (search.getTagId() != null) {
                schConds.add(locationMap.tagId.eq(search.getTagId()));
            }
            if (search.getMacAddress() != null) {
                schConds.add(locationMap.macAddress.eq(search.getMacAddress()));
            }
            if (search.getSendDateTime() != null) {
                schConds.add(locationMap.sendDateTime.between(search.getSendDateTime().minusSeconds(Long.parseLong(env.getProperty("relay-location.sch-seconds"))), search.getSendDateTime()));
            }
            if (search.getSchDate() != null) {
                schConds.add(locationMap.sendDateTime.between(
                        search.getSchDate(),
                        LocalDateTime.of(search.getSchDate().toLocalDate(), LocalTime.MAX).withNano(0)
                ));
            }
            if (search.getLoeCreatedDateTime() != null) {
                schConds.add(locationMap.createdDateTime.loe(search.getLoeCreatedDateTime()));
            }
        }

        return schConds;
    }
}
