package edu.utcn.gpstrack.server.position;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

/**
 * @author Radu Miron
 * @version 1
 */
public interface PositionRepository extends JpaRepository<Position, Integer> {

    @Query("SELECT p FROM Position p WHERE p.creationDate >= :startDate AND p.creationDate <= :endDate")
    List<Position> findAllBetweenDates(@Param("startDate") Date startDate, @Param("endDate") Date endDate);
}
