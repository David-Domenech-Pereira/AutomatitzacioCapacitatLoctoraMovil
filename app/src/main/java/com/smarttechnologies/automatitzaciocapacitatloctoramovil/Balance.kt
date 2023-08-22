package com.smarttechnologies.automatitzaciocapacitatloctoramovil

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import com.smarttechnologies.automatitzaciocapacitatlocomotoramovil.appBackground

class Balance : appBackground() {
    //variables necessàries per l'acceleròmetre
    private var pos = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        start("BALANCE_START")
        //posem la primera imatge
        boton.visibility = View.GONE
        val nuevaImagen = resources.getDrawable(R.drawable.pos1)
        imageView.setImageDrawable(nuevaImagen)
        textView.text = "Posa els peus en aquesta posició i el móvil a la butxaca esquerra"
    }
    public override fun end() {
        if (pos == 4) {
            boton.visibility = View.VISIBLE
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
        } else if(pos == 0||pos==2){
            if(pos == 0){
                val nuevaImagen = resources.getDrawable(R.drawable.pos2)
                imageView.setImageDrawable(nuevaImagen)
            }else{
                val nuevaImagen = resources.getDrawable(R.drawable.pos3)
                imageView.setImageDrawable(nuevaImagen)
            }
            textView.text = "Part Acabada. Ara col·loca els peus d'aquesta manera i torna a posar el móbil a la butxaca"
            pos++
            vibrarReloj()
            //tornem en 10 segons
            val handler = Handler(Looper.getMainLooper())

            // Post a runnable to the handler to run after 10 seconds
            handler.postDelayed({
                end()
            }, 10000)
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