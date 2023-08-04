package com.smarttechnologies.automatitzaciocapacitatloctoramovil

import kotlin.properties.Delegates

class sensorData {
    private var sensor_info by Delegates.notNull<String>()
    private var testData = mutableListOf<DataEntry>()
    data class DataEntry(val timestamp: Double, val values: List<Double>)
    constructor(sensor: String){
        this.sensor_info = sensor
    }
    public fun add(data:DataEntry){
        testData.add(data)
    }

    public fun getSensor(): String {
        return  sensor_info
    }

    fun getData():  List<DataEntry>{
        return  testData
    }
}