package com.saxolab.openapi.client;

import com.saxolab.openapi.model.chart.ChartData;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

@HttpExchange("/chart/v1")
public interface ChartClient {

    @GetExchange("/charts")
    ChartData getChartData(
            @RequestParam int Uic,
            @RequestParam String AssetType,
            @RequestParam int Horizon,
            @RequestParam int Count);
}
