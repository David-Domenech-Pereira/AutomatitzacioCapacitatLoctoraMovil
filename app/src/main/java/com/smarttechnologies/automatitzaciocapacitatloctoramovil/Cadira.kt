package com.smarttechnologies.automatitzaciocapacitatlocomotoramovil

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.os.Vibrator
import android.widget.Button
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.compose.ui.res.stringResource
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.smarttechnologies.automatitzaciocapacitatloctoramovil.MainActivity
import com.smarttechnologies.automatitzaciocapacitatloctoramovil.R

import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.Date

class Cadira : appBackground() {
    //variables necessàries per l'acceleròmetre

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        start("CHAIR_START")


    }
    override fun end(){
        //apaguem el sensor
        sensorManager.unregisterListener(sensorEventListener)
     //   sensorManager.unregisterListener(sensorEventListener_hr)
        vibrarReloj() //fem que vibri 4 cops
        vibrarReloj()
        vibrarReloj()
        vibrarReloj()
        //guardem les dades
        guardarDatos("End","CHAIR_END")
        //posem un missatge
        textView.text = "Test finalitzat"
        boton.text = "Tornar a l'inici"
        boton.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }




}