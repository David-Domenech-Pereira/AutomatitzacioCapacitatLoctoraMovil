package com.smarttechnologies.automatitzaciocapacitatloctoramovil

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.smarttechnologies.automatitzaciocapacitatlocomotoramovil.Cadira
import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.Date


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main)
        val button: Button = findViewById(R.id.button);
        button.setOnClickListener{
            val intent = Intent(this, Cadira::class.java)
            startActivity(intent)
        }
        val button2: Button = findViewById(R.id.button2);
        button2.setOnClickListener{
            val intent = Intent(this, Balance::class.java)
            startActivity(intent)
        }
        val but_fit : Button = findViewById(R.id.butt_fit)
        but_fit.setOnClickListener { // Cuando se hace clic en el botón, llamamos al método para mostrar el contenido del archivo
            showFileContent()
        }
        startService(Intent(this, BackgroundSensor::class.java))
        configurarServidor()

    }
    private fun configurarServidor(){
        println("Configurado")
        // Configurar el AlarmManager para que inicie el servicio una vez al día.
        val intent = Intent(this, PostService::class.java)
        val pendingIntent = PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_MUTABLE)

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // Aquí configuramos el intervalo de tiempo para que se ejecute una vez al día.
        //val intervaloDia = 24 * 60 * 60 * 1000L // 24 horas en milisegundos.
        val intervaloDia = 100L; //para probar ponemos cada segundo
        // La primera ejecución se realiza después de un día (intervaloDia).
        val tiempoEjecucion = System.currentTimeMillis() + intervaloDia

        // Configuramos el servicio para que se inicie una vez al día.
        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            tiempoEjecucion,
            intervaloDia,
            pendingIntent
        )
    }
    private fun showFileContent(){
        try {

            val formatoFecha = SimpleDateFormat("yyyy-MM-dd")
            val fechaActual = formatoFecha.format(Date())
            // Nombre del archivo a leer
            val filename = "$fechaActual.txt"

            // Abrir el archivo en modo privado para que solo esta aplicación pueda acceder a él
            val fileInputStream = openFileInput(filename)
            val inputStreamReader = InputStreamReader(fileInputStream)
            val bufferedReader = BufferedReader(inputStreamReader)
            val stringBuilder = StringBuilder()
            var line: String?
            while (bufferedReader.readLine().also { line = it } != null) {
                stringBuilder.append(line).append("\n")
            }

            // Cerrar los flujos
            bufferedReader.close()
            inputStreamReader.close()
            fileInputStream.close()

            // Mostrar el contenido del archivo en un Toast
            val fileContent = stringBuilder.toString()
            if (fileContent.isEmpty()) {
                // Mostrar un mensaje si el archivo está vacío
                Toast.makeText(this, "El archivo está vacío.", Toast.LENGTH_SHORT).show();
            } else {
                // Mostrar el contenido del archivo en un Toast
                Toast.makeText(this, fileContent, Toast.LENGTH_LONG).show();
            }
        } catch (e: Exception) {
            e.printStackTrace()
            // Mostrar un mensaje de error en caso de que ocurra un problema al leer el archivo
            Toast.makeText(this, "Error al leer el archivo.", Toast.LENGTH_SHORT).show()
        }
    }
}

