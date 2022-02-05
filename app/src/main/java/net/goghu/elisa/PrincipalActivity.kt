package net.goghu.elisa

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.Menu
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import net.goghu.elisa.MyFirebaseMessagingService
import com.google.android.gms.location.*
import net.goghu.elisa.databinding.ActivityPrincipalBinding
import org.json.JSONObject
import org.json.JSONTokener

class PrincipalActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityPrincipalBinding

    public lateinit var latitud: String
    public lateinit var longitud: String

    val PERMISSION_ID = 42
    lateinit var mFusedLocationClient: FusedLocationProviderClient


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPrincipalBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarPrincipal.toolbar)

        binding.appBarPrincipal.fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_principal)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        //Toast.makeText(this, "holas desde la actividad", Toast.LENGTH_LONG).show()

        // llamamos a la ubicacion y todas las funciones que necesita
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        getLastLocation()


    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.principal, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_principal)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    // localizacion
    @SuppressLint("MissingPermission")
    private fun getLastLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {

                mFusedLocationClient.lastLocation.addOnCompleteListener(this) { task ->
                    var location: Location? = task.result
                    if (location == null) {
                        requestNewLocationData()
                    } else {
                        Log.i("ubicacion latitud ", location.latitude.toString())
                        Log.i("ubicacion longitud ", location.longitude.toString())

                        findViewById<TextView>(R.id.txt_latitud).text = location.latitude.toString()
                        findViewById<TextView>(R.id.txt_longitud).text = location.longitude.toString()

                        latitud = location.latitude.toString()
                        longitud = location.longitude.toString()
                    }
                }
            } else {
                Toast.makeText(this, "Turn on location", Toast.LENGTH_LONG).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        } else {
            requestPermissions()
        }
    }

    @SuppressLint("MissingPermission")
    private fun requestNewLocationData() {
        var mLocationRequest = LocationRequest.create()
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest.interval = 0
        mLocationRequest.fastestInterval = 0
        mLocationRequest.numUpdates = 1

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        mFusedLocationClient!!.requestLocationUpdates(
            mLocationRequest, mLocationCallback,
            Looper.myLooper()
        )
    }

    // cuando cambiamos de ubicacion llamamos al evento de la nueva ubicacion
    private val mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            var mLastLocation: Location = locationResult.lastLocation

            findViewById<TextView>(R.id.text_home).text = "Cambio la posicion"
            findViewById<TextView>(R.id.txt_latitud).text = mLastLocation.latitude.toString()
            findViewById<TextView>(R.id.txt_longitud).text = mLastLocation.longitude.toString()

            Log.i("ubicacion latitud ", mLastLocation.latitude.toString())
            Log.i("ubicacion longitud ", mLastLocation.longitude.toString())

            Toast.makeText(this@PrincipalActivity, "Se cambio de ubicacion", Toast.LENGTH_LONG).show()

            enviaLocalizacion("1", mLastLocation.latitude.toString(), mLastLocation.longitude.toString())
        }
    }

    private fun isLocationEnabled(): Boolean {
        var locationManager: LocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    // verficamos que los permisos esten habilitados
    private fun checkPermissions(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION),
            PERMISSION_ID
        )
    }

    // verificacion si no nos concedio el permiso
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_ID) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                getLastLocation()
            }
        }
    }

    fun panico(view: android.view.View) {
        getLastLocation();
        findViewById<TextView>(R.id.text_home).text = "Cambio la posicion"
        enviaLocalizacion("1", latitud, longitud)
        Toast.makeText(this, "holas desde el boton", Toast.LENGTH_LONG).show()
    }

    // notificaciones push

    fun salirTema(){
        MyFirebaseMessagingService.unsubscribeTopic(this, "Encargados")
    }

    fun suscribirTema(view: android.view.View) {
        MyFirebaseMessagingService.subscribeTopic(this@PrincipalActivity,"Encargados")
        Toast.makeText(this, "Suscrito a Encargados", Toast.LENGTH_LONG).show()
    }

    fun enviarMensaje(view: android.view.View) {
        MyFirebaseMessagingService.sendMessage("Elisa: Alerta", "Cristiam en problemas", "Encargados")
    }

    // fin notificaciones push

    private fun enviaLocalizacion(usuario: String, latitud :String, longitud: String){
//        Toast.makeText(this, nombre, Toast.LENGTH_SHORT).show()
        val url = Constants.BASE_URL + Constants.API_PATH + Constants.LOCALIZACION_PATH

        val jsonParams = JSONObject()

        jsonParams.put(Constants.ID_PARAM, usuario)
        jsonParams.put(Constants.LATITUD_PARAM, latitud)
        jsonParams.put(Constants.LONGITUD_PARAM, longitud)

        val jsonObjectRequest = object : JsonObjectRequest(Request.Method.POST, url, jsonParams, { response ->
//            me retorna los datos de guardado
            Log.i("respuesta", response.toString())

            // manejamos la respuesta
            val jsonObject = JSONTokener(response.toString()).nextValue() as JSONObject
            // capturamos el id que nos devolvio el registro
            val usuario = jsonObject.getString("usuario")
//            Log.i("Usuario: ", usuario)




        },{
            if (it.networkResponse.statusCode == 400){
                Log.i("Error", "error en el envio")
            }
        }){
            override fun getHeaders(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                params["Content-Type"] = "application/json"
                return params
            }
        }

        LoginApplication.reqResApi.addToRequestQueue(jsonObjectRequest)

    }
}