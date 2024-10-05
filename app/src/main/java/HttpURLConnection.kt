import android.os.AsyncTask
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class FetchDataFromApi : AsyncTask<Void, Void, JSONObject>() {
    override fun doInBackground(vararg params: Void?): JSONObject? {
        val url = URL("http://192.168.1.15:8080/")
        val connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = "GET"
        val responseCode = connection.responseCode
        if (responseCode == HttpURLConnection.HTTP_OK) {
            val reader = BufferedReader(InputStreamReader(connection.inputStream))
            val response = StringBuilder()
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                response.append(line)
            }
            reader.close()
            return JSONObject(response.toString())
        }
        return null
    }

    override fun onPostExecute(result: JSONObject?) {
        super.onPostExecute(result)
        if (result != null) {
            // Aquí puedes procesar los datos recibidos y crear los botones en el GridLayout
            // Por ejemplo, puedes acceder a los valores de los atributos como result.getString("username")
        }
    }
}
