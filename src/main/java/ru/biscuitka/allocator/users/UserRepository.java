package ru.biscuitka.allocator.users;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, Long> {
    @Query("SELECT u FROM User u WHERE u.ticketSeries = :series AND u.ticketNumber = :number")
    User findUserByTicket(@Param("series") Long series, @Param("number") Long number);
}
