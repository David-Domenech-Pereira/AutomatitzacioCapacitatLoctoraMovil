package com.smarttechnologies.automatitzaciocapacitatlocomotoramovil

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Vibrator
import androidx.activity.ComponentActivity
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.Date
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.utils.ColorTemplate
import com.smarttechnologies.automatitzaciocapacitatloctoramovil.R

abstract class appBackground : ComponentActivity() {
    protected var tiempoInicio: Long = 0
    private lateinit var lineChart: LineChart
    private val dataList = ArrayList<Entry>()
    protected lateinit var sensorManager: SensorManager
    protected var accelerometer: Sensor? = null
    protected lateinit var imageView: ImageView
    protected lateinit var textView: TextView
    protected lateinit var boton: Button

    protected lateinit var sensorEventListener: SensorEventListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.cadira);
        boton = findViewById(R.id.end)
        imageView = findViewById(R.id.imageView)
    }
    public fun start(type: String){

        tiempoInicio = System.currentTimeMillis()// contem el temps desde que s'ha iniciat

        //inicialitzem
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        textView = findViewById(R.id.textView)

        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed({
            empieza()
        }, 10000) //Fem la app esperi 10 segons

    }
    public fun empieza(){
        //después dels 10 segons
        textView = findViewById(R.id.textView)
        textView.text = "Iniciem el test"
        sensorEventListener = object : SensorEventListener {
            override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
                // No se utiliza en este caso
            }

            override fun onSensorChanged(event: SensorEvent) {
                // Lee los valores del acelerómetro y muestra las indicaciones según tus necesidades
                val x = event.values[0]
                val y = event.values[1]
                val z = event.values[2]
                val datos = "$x;$y;$z;" //posem les dades separades per ;

                println(datos); //printegem les dades
                guardarDatos(datos,"accelerometre");
            }
        }

        sensorManager.registerListener(sensorEventListener, accelerometer, SensorManager.SENSOR_DELAY_NORMAL)


        boton.setOnClickListener {
            //carguem el end
            end()
        }
        //TODO fer que el botó es vegi quan creiem que ha acabat
        //Necessitem el permís per accedir als sen
        val permission = Manifest.permission.BODY_SENSORS
        val requestCode = 1

        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
            // El permiso no se ha concedido, así que solicitamos permiso
            ActivityCompat.requestPermissions(this, arrayOf(permission), requestCode)
        } else {
            // El permiso ya se ha concedido, puedes continuar con la lógica del sensor de ritmo cardíaco
        }

        vibrarReloj() //cridem l'atenció de l'usuariw
        guardarDatos("Start","BALANCE_START")
        //Hacemos que acabe en 10 segundos
        val handler = Handler(Looper.getMainLooper())

        // Post a runnable to the handler to run after 10 seconds
        handler.postDelayed({
            end()
        }, 10000)
    }
    public abstract fun end()
    private fun setupLineChart() {
        // Configurar algunas propiedades del gráfico
        lineChart.description.isEnabled = false
        lineChart.setTouchEnabled(true)
        lineChart.isDragEnabled = true
        lineChart.setScaleEnabled(true)

        // Configurar el conjunto de datos inicial vacío
        var dataSet = LineDataSet(dataList, "x")
        dataSet.color = ColorTemplate.getHoloBlue()
        dataSet.setCircleColor(ColorTemplate.getHoloBlue())
        dataSet.lineWidth = 2f
        dataSet.circleRadius = 4f
        dataSet.setDrawCircleHole(false)
        dataSet.valueTextSize = 9f

        // Configurar el conjunto de datos en el gráfico
        val lineData = LineData(dataSet)
        lineChart.data = lineData

        // Actualizar el gráfico
        lineChart.invalidate()
        val data = lineChart.data
        //Creamos los 3 datasets
        dataSet = LineDataSet(null, "y")
        dataSet.color = ColorTemplate.rgb("FF0000")
        dataSet.setCircleColor(ColorTemplate.getHoloBlue())
        dataSet.lineWidth = 2f
        dataSet.circleRadius = 4f
        dataSet.setDrawCircleHole(false)
        dataSet.valueTextSize = 9f
        data.addDataSet(dataSet)
        //Creamos los 3 datasets
        dataSet = LineDataSet(null, "z")
        dataSet.color = ColorTemplate.rgb("008F39")
        dataSet.setCircleColor(ColorTemplate.getHoloBlue())
        dataSet.lineWidth = 2f
        dataSet.circleRadius = 4f
        dataSet.setDrawCircleHole(false)
        dataSet.valueTextSize = 9f
        data.addDataSet(dataSet)

    }
    private fun dibujar_datos(datos: String){
        //los separamos por ;
        val parsed_data = datos.split(";")
        val data = lineChart.data
        //el primer valor es la x, el segundo la y y el tercer la z
        var i = 0
        for(valor in parsed_data){
            if (i==3){
                break
            }
            var valor_sin_espacios = valor.replace("\\s+".toRegex(), "")
            println(valor_sin_espacios)
            val valor_float = valor_sin_espacios.toFloat();
            var dataSet = data.getDataSetByIndex(i)
            data.addEntry(Entry(dataSet.entryCount.toFloat(), valor_float), i)

            // Notificar al gráfico que los datos han cambiado
            data.notifyDataChanged()
            lineChart.notifyDataSetChanged()
            lineChart.setVisibleXRangeMaximum(10f)
            lineChart.moveViewToX(data.entryCount.toFloat())
            i++
        }

    }
    protected fun guardarDatos(datos: String, type:String) {
        val tiempoActual = System.currentTimeMillis()
        val formatoFecha = SimpleDateFormat("yyyy-MM-dd")
        val fechaActual = formatoFecha.format(Date())
        val nombreArchivo = "$fechaActual.txt"
        val directorio = filesDir // Obtén el directorio de archivos de la aplicación
        var tipus = ""

        if(type=="hr"){
            tipus = "HR"

        }else if(type=="accelerometre"){
            tipus = "ACC"
           // dibujar_datos(datos)
        }else{
            tipus = type
        }
        try {

            val archivo = File(directorio, nombreArchivo)
            val escritor = FileWriter(archivo, true) // Usar "true" para habilitar el modo de anexar
            escritor.append("$tiempoActual;$tipus;$datos\n")
            escritor.flush()
            escritor.close()
            // Archivo guardado exitosamente
        } catch (e: Exception) {
            e.printStackTrace()
            // Ocurrió un error al guardar el archivo
            showErrorDialog(this)
        }
    }
    protected fun vibrarReloj() {
        val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

        // Verifica si el dispositivo admite la vibración
        if (vibrator.hasVibrator()) {
            // Define un patrón de vibración (por ejemplo, 500 ms vibrando, 200 ms en pausa)
            val pattern = longArrayOf(500, 200)

            // -1 indica repetición de patrón, 0 indica no repetición
            vibrator.vibrate(pattern, -1)
        }
    }
    protected fun showErrorDialog(context: Context) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Error")
        builder.setMessage("No se pudo escribir en el archivo.")
        builder.setPositiveButton(
            "Aceptar"
        ) { dialog, which -> // Aquí puedes añadir alguna acción adicional si el usuario pulsa el botón "Aceptar"
            dialog.dismiss()
        }
        val dialog = builder.create()
        dialog.show()
    }
}