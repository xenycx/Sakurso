package com.tlat.Dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RoomDto {
    private Long id;

    @NotBlank(message = "Room number should not be empty")
    private String roomNumber;

    @NotBlank(message = "IP address should not be empty")
    @Pattern(regexp = "^\\d{1,3}(\\.\\d{1,3}){3}$", message = "Invalid IP address format")
    private String ipAddress;
}