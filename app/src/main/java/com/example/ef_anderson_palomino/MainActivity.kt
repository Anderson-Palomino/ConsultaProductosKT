package com.example.ef_anderson_palomino

import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.ef_anderson_palomino.databinding.ActivityMainBinding
import com.example.ef_anderson_palomino.model.Producto
import com.example.ef_anderson_palomino.network.ProductoAPI
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val handler = Handler()
    private lateinit var api: ProductoAPI

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val retrofit = Retrofit.Builder()
            .baseUrl("https://fakestoreapi.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        api = retrofit.create(ProductoAPI::class.java)

        binding.btnConsultar.setOnClickListener {
            val id = binding.editTextId.text.toString().toIntOrNull()
            if (id == null) {
                binding.textId.text = ""
                binding.textTituloProducto.text = ""
                binding.textPrecio.text = ""
                binding.textDescripcion.text = "⚠️ Ingrese un ID numérico válido."
                return@setOnClickListener
            }
            consultarProducto(id)
        }

        actualizarHora()
    }

    private fun consultarProducto(id: Int) {
        api.getProducto(id).enqueue(object : Callback<Producto> {
            override fun onResponse(call: Call<Producto>, response: Response<Producto>) {
                if (response.isSuccessful && response.body() != null) {
                    val p = response.body()!!
                    binding.textId.text = p.id.toString()
                    binding.textTituloProducto.text = p.title
                    binding.textPrecio.text = "$${p.price}"
                    binding.textDescripcion.text = p.description
                    binding.textCategoria.text = p.category
                    binding.textRating.text = p.rating.rate.toString()
                    binding.textCount.text = p.rating.count.toString()

                    Glide.with(this@MainActivity)
                        .load(p.image)
                        .into(binding.imageProducto)

                    binding.textAlumno.text = "Alumno: Anderson Palomino"
                } else {
                    binding.textDescripcion.text = "❌ Producto no encontrado o error: ${response.code()}"
                }
            }

            override fun onFailure(call: Call<Producto>, t: Throwable) {
                binding.textDescripcion.text = "❌ Error al consultar: ${t.message}"
            }
        })
    }

    private fun actualizarHora() {
        handler.post(object : Runnable {
            override fun run() {
                val formato = SimpleDateFormat("hh:mm:ss a", Locale.getDefault())
                binding.textHora.text = formato.format(Date())
                handler.postDelayed(this, 1000)
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }
}
