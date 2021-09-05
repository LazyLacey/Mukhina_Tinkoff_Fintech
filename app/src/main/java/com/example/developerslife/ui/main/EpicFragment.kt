package com.example.developerslife.ui.main

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.developerslife.R
import com.example.developerslife.models.DevResponse
import com.example.developerslife.models.DevResponseInfo
import com.example.developerslife.services.ApiService
import com.example.developerslife.services.CheckConnect
import com.example.developerslife.services.Constants
import com.google.android.material.button.MaterialButton
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import okhttp3.*
import java.io.IOException
import java.lang.Exception
import java.lang.NullPointerException


class EpicFragment(val position: Int) : Fragment() {

    private var wasLoaded = false;
    private val model = Model()
    private val apiService = ApiService()
    private val checkConnect = CheckConnect()

    private var pageCounter = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val thiscontext = requireContext()
        val view = inflater.inflate(R.layout.item_page, container, false)

        val noInternetView = view.findViewById<RelativeLayout>(R.id.no_internet)
        val noInternetBtn = view.findViewById<MaterialButton>(R.id.no_internet_button)
        val showableView = view.findViewById<RelativeLayout>(R.id.showable)
        val showableBtnsView = view.findViewById<LinearLayout>(R.id.showable_btns)

        var connect = checkConnect.isOnline(thiscontext)
        if (!connect) {
            noInternetView.visibility = View.VISIBLE
            showableView.visibility = View.INVISIBLE
            showableBtnsView.visibility = View.INVISIBLE
        }

        noInternetBtn.setOnClickListener {
            if (checkConnect.isOnline(thiscontext)) {
                noInternetView.visibility = View.INVISIBLE
                showableView.visibility = View.VISIBLE
                showableBtnsView.visibility = View.VISIBLE
            }
        }


        val imageShow = view.findViewById<ImageView>(R.id.image_show);
        val textShow = view.findViewById<TextView>(R.id.text_show);

        val buttonForward = view.findViewById<MaterialButton>(R.id.do_forward)
        val buttonBack = view.findViewById<MaterialButton>(R.id.do_back)

        val circularProgressDrawable = CircularProgressDrawable(thiscontext)
        circularProgressDrawable.strokeWidth = 5f
        circularProgressDrawable.centerRadius = 30f
        circularProgressDrawable.start()

        buttonBack.setOnClickListener {
            DoBack()
        }

        buttonForward.setOnClickListener {
            DoForward()
        }


        Glide.with(view)
            .load(R.drawable.ic_outline_not_started_24)
            .dontTransform()
            .into(imageShow)

        model.param.observe(viewLifecycleOwner, Observer {
            if (wasLoaded && it.size == 0) {
                Glide.with(view)
                    .load(R.drawable.ic_baseline_sentiment_very_dissatisfied_24)
                    .dontTransform()
                    .into(imageShow)
                textShow.text = "Нет постов :("

                buttonForward.isCheckable = false
                buttonForward.isEnabled = false

            }
        })


        model.counter.observe(viewLifecycleOwner, Observer {
            if (it > 0 ) {
                if (!buttonBack.isEnabled) {
                    buttonBack.isCheckable = true
                    buttonBack.isEnabled = true
                }
                try {
                    loadImage(model.param.value!![it].gifURL, view, imageShow, circularProgressDrawable)
                    textShow.text = model.param.value!![it].description
                } catch (e: NullPointerException) {
                    println("nope")
                }
            } else {
                if (buttonBack.isEnabled) {
                    buttonBack.isCheckable = false
                    buttonBack.isEnabled = false
                }
                try {
                    loadImage(model.param.value!![it].gifURL, view, imageShow, circularProgressDrawable)
                    textShow.text = model.param.value!![it].description
                } catch (e: Exception) {
                    println("nope")
                }
            }
        })

        return view
    }

    private fun loadImage(link: String?, view: View, imageShow: ImageView, placeholder: Drawable ) {
        Glide.with(view)
            .asGif()
            .load(link)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .placeholder(placeholder)
            .centerCrop()
            .into(imageShow)
        println("loaded img")
    }

    private fun loadPage() {
        val category = Constants.TAB_PARAMS[position];
        val request = apiService.getCategoryRequest(category, pageCounter);
        request.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!response.isSuccessful)
                        throw IOException("Unexpected code $response")
                    val obj = Json.decodeFromString<DevResponse>(response.body!!.string());
                    val objResults = obj.result
                    val res = model.param.value
                    res?.addAll(objResults!!)
                    model.param.postValue(res)
                    model.counter.postValue(model.counter.value?.plus(1))
                    wasLoaded = true
                }
            }})
    }

    fun DoForward() {
        if (model.param.value!!.size < model.counter.value!! + 1) {
            println("here")
            model.counter.value = model.counter.value?.plus(1)
        } else {
            pageCounter += 1
            loadPage()
        }
    }

    fun DoBack() {
        model.counter.value = model.counter.value?.minus(1)
    }

}

class Model {
    val param = MutableLiveData(mutableListOf<DevResponseInfo>());

    val counter = MutableLiveData(-1)

}