package com.smarttechnologies.automatitzaciocapacitatloctoramovil

import android.app.IntentService
import android.content.Intent
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import org.json.JSONObject
import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStreamReader
import java.text.SimpleDateFormat
import java.util.Date

class PostService : IntentService("PostService") {
    lateinit var token: String
    override fun onHandleIntent(intent: Intent?) {
        println("Mandamos")
        token = obtenerToken("TOKENDEPROVA")
        //Obtenim la informació dels fitxers i la posem en un json
        var info = getInfo()
        for(data in info){
            // Aquí es donde se realizará el POST request al servidor.
            enviarPostRequestAlServidor(generateJson(data),"https://smarttechnologiesurv.000webhostapp.com/api/sensorData.php")
        }

    }
    private fun getInfo() : List<sensorData>{
        val datos = arrayListOf<sensorData>()

        try {
            val tiempoActual = System.currentTimeMillis()
            val formatoFecha = SimpleDateFormat("yyyy-MM-dd")
            val fechaActual = formatoFecha.format(Date())
            val nombreArchivo = "$fechaActual.txt"
            val directorio = filesDir // Obtén el directorio de archivos de la aplicación

            val inputStream = FileInputStream(File(directorio, nombreArchivo))
            val reader = BufferedReader(InputStreamReader(inputStream))
            var line: String? = reader.readLine()

            while (line != null) {
                val fields = line.split(";")
                if (fields.size > 3) {
                    val timestamp = fields[0].toLongOrNull()
                    val type = fields[1]
                    val values = fields.subList(2, 5).map { it.toDoubleOrNull() ?: 0 }

                    var guardar:sensorData? = null
                    for(valor in datos){
                        if(valor.getSensor()==type){
                            guardar = valor
                            break
                        }
                    }
                    if (guardar == null){
                        guardar = sensorData(type)
                        datos.add(guardar)
                    }
                    if (timestamp != null) {
                        val dataEntry = sensorData.DataEntry(timestamp.toDouble(),
                            values as List<Double>
                        )
                        guardar.add(dataEntry)
                    }
                }else{
                    //no nos mandan los datos sino que se ha empezado o acabado un test
                    val timestamp = fields[0].toLongOrNull()
                    val type = fields[1]
                    //separamos el type por el _
                    var type_info = type.split("_")
                    lateinit var  params:String
                    if(type_info[1]=="START"){
                        params = "&start="+timestamp
                    }else{
                        params = "&end="+timestamp
                    }
                    lateinit var tipo_test: String
                    if(type_info[0]=="BALANCE"){
                        params = "?testType=2"+params
                    }else if(type_info[1]=="CHAIR"){
                        params ="?testType=1"+params
                    }
                    //Hacemos un GET request con los params y el header
                    val mediaType = "application/json".toMediaTypeOrNull()

                    val request = Request.Builder()
                        .url("https://smarttechnologiesurv.000webhostapp.com/api/testData.php"+params)
                        .addHeader("Content-Type", "application/json")
                        .addHeader("Authorization", token)
                        .build()

                    val client = OkHttpClient()

                    try {
                        val response = client.newCall(request).execute()
                        val responseData = response.body?.string()
                        println(responseData)
                    } catch (e: IOException) {
                        e.printStackTrace()
                        println("ERROR TOKEN")
                    }
                }

                line = reader.readLine()
            }

            reader.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return datos
    }
    private fun generateJson(dataEntries: sensorData): String {
        val jsonObject = JsonObject()
        val gson = Gson()
        jsonObject.addProperty("sensor", dataEntries.getSensor())

        val dataArray = JsonArray()
        for (dataEntry in dataEntries.getData()) {
            val entryObject = JsonObject()
            entryObject.addProperty("timestamp", dataEntry.timestamp)
            entryObject.addProperty("values", dataEntry.values.toString())
            dataArray.add(entryObject)
        }

        jsonObject.add("data", dataArray)

        return gson.toJson(jsonObject)
    }




    private fun enviarPostRequestAlServidor(text: String, url: String) {
        println(text)
        val client = OkHttpClient()

        val mediaType = "application/json".toMediaTypeOrNull()
        val requestBody = RequestBody.create(mediaType, text)
        val request = Request.Builder()
            .url(url)
            .post(requestBody)

            .addHeader("Content-Type", "application/json")
            .addHeader("Authorization", token)
            .build()
        try {
            val response: Response = client.newCall(request).execute()
            // Manejar la respuesta del servidor si es necesario.
            val responseData = response.body?.string()
            println(responseData)
            response.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
    fun obtenerToken(publicKey: String): String{
        val publicKey = publicKey
        val json = """
        {
            "publicKey": "$publicKey"
        }
    """.trimIndent()

        val mediaType = "application/json".toMediaTypeOrNull()
        val requestBody = RequestBody.create(mediaType, json)
        val request = Request.Builder()
            .url("https://smarttechnologiesurv.000webhostapp.com/api/authorization.php")
            .post(requestBody)
            .addHeader("Content-Type", "application/json")
            .build()

        val client = OkHttpClient()

        try {
            val response = client.newCall(request).execute()
            val responseData = response.body?.string()
            println(responseData)
            response.close()

            val jsonResponse = responseData?.let { json ->
                val jsonObject = JSONObject(json)
                return jsonObject.optString("token")
            }

            return ""
        } catch (e: IOException) {
            e.printStackTrace()
            println("ERROR TOKEN")
        }

        return ""
    }

}

