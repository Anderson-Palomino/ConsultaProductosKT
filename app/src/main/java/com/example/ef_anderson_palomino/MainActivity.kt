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
                binding.textResultado.text = "⚠️ Ingrese un ID numérico válido."
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
                    val ratingTexto = if (p.rating != null) {
                        "Rating: ${p.rating.rate} (${p.rating.count} votos)"
                    } else {
                        "Rating: No disponible"
                    }

                    binding.textResultado.text = """
        ID: ${p.id}
        Título: ${p.title}
        Precio: $${p.price}
        Categoría: ${p.category}
        $ratingTexto
        Descripción: ${p.description}
    """.trimIndent()

                    Glide.with(this@MainActivity)
                        .load(p.image)
                        .into(binding.imageProducto)
                } else {
                    binding.textResultado.text =
                        "Producto no encontrado o error: ${response.code()}"
                }

            }

            override fun onFailure(call: Call<Producto>, t: Throwable) {
                binding.textResultado.text = "Error al consultar: ${t.message}"
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
