package com.example.weatherapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.SearchView
import com.example.weatherapp.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {
    private lateinit var binding:ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        fetchWeatherData("Jaipur")
        SearchCity()
    }

    private fun SearchCity() {
        val searchView=binding.searchView

        searchView.setOnQueryTextListener(object:SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    fetchWeatherData(query)
                }
                return true
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })
    }

    private fun fetchWeatherData(cityName: String) {
        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .build().create((ApiInterface::class.java))
        val response = retrofit.getweatherdata(cityName,"7feb9955e6f440942bd27ad32f6c2848","metric")
        response.enqueue(object :Callback<WeatherApp>{
            override fun onResponse(call: Call<WeatherApp>, response: Response<WeatherApp>) {
                val responseBody=response.body()
                if(response.isSuccessful && responseBody!=null){
                    val temperature =responseBody.main.temp.toString()
                    val humidity=responseBody.main.humidity
                    val windspeed=responseBody.wind.speed
                    val sunRise=responseBody.sys.sunrise.toLong()
                    val sunSet=responseBody.sys.sunset.toLong()
                    val seaLevel=responseBody.main.pressure
                    val condition=responseBody.weather.firstOrNull()?.main?:"unknown"
                    val maxTemp=responseBody.main.temp_max
                    val minTemp=responseBody.main.temp_min
                    //Log.d("TAG", "onResponse: $temperature")
                    binding.temp.text="$temperature °C"
                    binding.weather.text=condition
                    binding.maxtemp.text="Max Temp: $maxTemp °C"
                    binding.mintemp.text="Min Temp: $minTemp °C"
                    binding.humiditypercentage.text="$humidity %"
                    binding.windspeed.text="$windspeed m/s"
                    binding.sunrisetime.text="${time(sunRise)}"
                    binding.sunsettime.text="${time(sunSet)}"
                    binding.seapressure.text="$seaLevel hPa"
                    binding.condition.text=condition
                    binding.day.text=dayName(System.currentTimeMillis())
                    binding.date.text=date()
                    binding.cityname.text="$cityName"
                    changeImageAccordingToWeatherCondition(condition)
                }
            }
            override fun onFailure(call: Call<WeatherApp>, t: Throwable) {
//                Log.d("WeatherApp", "Failed to fetch weather data", t)

            }
        })
        }

    private fun changeImageAccordingToWeatherCondition(conditions:String) {
        when(conditions){
            "Clear Sky", "Sunny", "Clear"->{
                binding.root.setBackgroundResource(R.drawable.sunny_background)
                binding.lottieAnimationView.setAnimation(R.raw.sun)
            }
            "Partly Clouds","Clouds", "Overcast", "Mist", "Foggy"->{
                binding.root.setBackgroundResource(R.drawable.colud_background)
                binding.lottieAnimationView.setAnimation(R.raw.cloud)
            }
            "Light Rain", "Drizzle", "Moderate Rain", "Showers", "Heavy Rain"->{
                binding.root.setBackgroundResource(R.drawable.rain_background)
                binding.lottieAnimationView.setAnimation(R.raw.rain)
            }
            "Light Snow", "Moderate Snow", "Heavy Snow", "Blizzard"->{
                binding.root.setBackgroundResource(R.drawable.snow_background)
                binding.lottieAnimationView.setAnimation(R.raw.snow)
            }
            else ->{
                binding.root.setBackgroundResource(R.drawable.sunny_background )
                binding.lottieAnimationView.setAnimation(R.raw.sun)
            }
        }
        binding.lottieAnimationView.playAnimation()
    }

    private fun time(timestamp: Long): String {
        val sdf=SimpleDateFormat("HH:mm",Locale.getDefault())
        return sdf.format((Date(timestamp*1000)))
    }
    private fun date(): String {
        val sdf=SimpleDateFormat("dd MMMM yyyy",Locale.getDefault())
        return sdf.format((Date()))

    }
    fun dayName(timestamp: Long):String{
        val sdf=SimpleDateFormat("EEEE", Locale.getDefault())
        return sdf.format((Date()))
    }
}