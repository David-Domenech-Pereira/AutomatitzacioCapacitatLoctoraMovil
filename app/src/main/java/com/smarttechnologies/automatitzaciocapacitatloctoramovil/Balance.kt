package com.smarttechnologies.automatitzaciocapacitatloctoramovil

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.smarttechnologies.automatitzaciocapacitatlocomotoramovil.appBackground

class Balance : appBackground() {
    //variables necessàries per l'acceleròmetre
    private var pos = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        start("BALANCE_START")


    }
    public override fun end(){
        if(pos == 2) {
            //apaguem el sensor
            sensorManager.unregisterListener(sensorEventListener)
            //  sensorManager.unregisterListener(sensorEventListener_hr)
            vibrarReloj() //fem que vibri 4 cops
            Thread.sleep(200);
            vibrarReloj()
            Thread.sleep(200);
            vibrarReloj()
            Thread.sleep(200);
            vibrarReloj()
            //guardem les dades
            guardarDatos("End", "BALANCE_END")
            //posem un missatge
            textView.text = "Test finalitzat"
            boton.text = "Tornar a l'inici"
            boton.setOnClickListener {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
        }else{
            textView.text = "Següent posició"
            pos++
            guardarDatos(pos.toString(),"BALANCE_POS")
            //passem a la següent posició
            vibrarReloj()
            //tornem en 10 segons
            val handler = Handler(Looper.getMainLooper())

            // Post a runnable to the handler to run after 10 seconds
            handler.postDelayed({
                end()
            }, 10000)
        }
    }


}