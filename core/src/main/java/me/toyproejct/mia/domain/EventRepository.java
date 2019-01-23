package me.toyproejct.mia.domain;

import me.toyproejct.mia.domain.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {
    @Query("select e from Event  e where e.registerOpenPeriod.startDate >= :from and e.registerOpenPeriod.endDate <= :to")
    List<Event> findAllRegisterOpenBetween(@NotNull @Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

    @Query("select e from Event  e where e.registerOpenPeriod.startDate >= :from and e.registerOpenPeriod.endDate <= :to")
    Page<Event> findAllRegisterOpenBetween(@NotNull @Param("from") LocalDateTime from, @Param("to") LocalDateTime to, Pageable pageable);

    @Query("select e from Event  e where e.registerOpenPeriod.startDate >= current_date ")
    List<Event> findAllRegisterOpenNow();

    @Query("select e from Event  e where e.registerOpenPeriod.startDate >= current_date ")
    Page<Event> findAllRegisterOpenNow(Pageable pageable);
}
