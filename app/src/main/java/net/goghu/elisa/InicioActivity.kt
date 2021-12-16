package net.goghu.elisa

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import net.goghu.elisa.databinding.ActivityInicioBinding

class InicioActivity : AppCompatActivity() {

    private lateinit var binding: ActivityInicioBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_inicio)
        binding = ActivityInicioBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.btnIngresar.setOnClickListener{
//            Toast.makeText(this, "Entro", Toast.LENGTH_LONG).show()

            val intent = Intent(this@InicioActivity,LoginActivity::class.java);
            startActivity(intent);
        }

        binding.btnRegistrar.setOnClickListener{
//            Toast.makeText(this, "Entro", Toast.LENGTH_LONG).show()

            val intent = Intent(this@InicioActivity,RegistroActivity::class.java);
            startActivity(intent);
        }
    }
}