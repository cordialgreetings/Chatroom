package com.example.achatroom.PO;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MessagePO {
    String id;
    String text;
    String timestamp;
}
