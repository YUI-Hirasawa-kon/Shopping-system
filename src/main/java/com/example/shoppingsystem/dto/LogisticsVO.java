package com.example.shoppingsystem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LogisticsVO {
    private String company;
    private String trackingNo;
    private String status;
    private List<TraceVO> traces;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TraceVO {
        private String time;
        private String info;
    }
}
