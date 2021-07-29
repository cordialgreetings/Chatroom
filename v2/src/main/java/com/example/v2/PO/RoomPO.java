package com.example.v2.PO;

import lombok.Data;

import java.io.Serializable;

@Data
public class RoomPO implements Serializable {
    int roomId;
    String name;
}
