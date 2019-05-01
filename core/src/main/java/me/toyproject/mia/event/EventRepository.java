package me.toyproject.mia.event;

import org.hibernate.annotations.Where;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@Where(clause = "deleted = 0")
public interface EventRepository extends JpaRepository<Event, Long> {
    @Query("select e from Event  e where e.registerOpenPeriod.startDate >= :from and e.registerOpenPeriod.endDate <= :to")
    List<Event> findAllRegisterOpenBetween(@NotNull @Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

    @Query("select e from Event  e where e.registerOpenPeriod.startDate >= :from and e.registerOpenPeriod.endDate <= :to")
    Page<Event> findAllRegisterOpenBetween(@NotNull @Param("from") LocalDateTime from, @Param("to") LocalDateTime to, Pageable pageable);

    @Query("select e from Event  e where e.registerOpenPeriod.startDate <= :now and e.registerOpenPeriod.endDate >= :now")
    List<Event> findAllRegisterOpenNow(@Param("now") LocalDateTime now);

    @Query("select e from Event  e where e.registerOpenPeriod.startDate <= :now and e.registerOpenPeriod.endDate >= :now")
    Page<Event> findAllRegisterOpenNow(@Param("now") LocalDateTime now, Pageable pageable);
}
