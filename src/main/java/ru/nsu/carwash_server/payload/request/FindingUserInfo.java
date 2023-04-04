package ru.nsu.carwash_server.payload.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Builder
public class FindingUserInfo {
    private String username;
}
