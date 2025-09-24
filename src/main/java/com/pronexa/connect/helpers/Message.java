package com.pronexa.connect.helpers;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Message {

    private String content; // The actual message text to display
    private MessageType type; // The type of message for styling (blue, red, green, yellow)

    // No need for @Builder anymore if you won't use it
}
