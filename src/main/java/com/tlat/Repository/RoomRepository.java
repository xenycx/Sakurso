package com.tlat.Repository;

import com.tlat.Entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomRepository extends JpaRepository<Room, Long> {
    Room findByRoomNumber(String roomNumber);
    Room findByIpAddress(String ipAddress);
}