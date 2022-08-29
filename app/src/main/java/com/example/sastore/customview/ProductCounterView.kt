package com.example.sastore.customview

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.example.sastore.R
import kotlinx.coroutines.runBlocking

private const val TAG = "ProductCounterView"
class ProductCounterView(private val viewContext: Context, private val attributeSet: AttributeSet) :
    LinearLayout(viewContext, attributeSet),
    View.OnClickListener {
    private lateinit var textView: TextView
    private lateinit var addBtn: ImageButton
    private lateinit var removeBtn: ImageButton
    private lateinit var productCountLiveData: MutableLiveData<Int>

    init {
        initializeViews()
    }

    private fun initializeViews() {
        // initialize components
        inflate(viewContext, com.example.sastore.R.layout.counter_good_quantitiy_layout, this)
        addBtn = findViewById(com.example.sastore.R.id.counter_purchases_quantity_button_add)
        removeBtn = findViewById(com.example.sastore.R.id.counter_purchases_quantity_button_remove)
        textView = findViewById(com.example.sastore.R.id.counter_purchases_quantity_textView)
        addBtn.setOnClickListener(this)
        removeBtn.setOnClickListener(this)

        // initialize the gif observer

        // initialize product first count
        productCountLiveData = MutableLiveData<Int>()
        productCountLiveData.value = if (textView.text != null) textView.text.toString().toInt()
        else 1

        productCountLiveData.observe(viewContext as LifecycleOwner, productCountObserver())


    }

    private fun productCountObserver(): Observer<in Int> {
        return Observer<Int> {
                t -> textView.text = t.toString() }
    }

    private fun aButtonClicked(p0: View?) {
        val number = productCountLiveData.value!!
        if (number == 1 && p0 == removeBtn){
            productCountLiveData.value = 0
            return
        }
        val handler = Handler(Looper.getMainLooper())
        val r = Runnable {
            editTheCounterText(p0,number)
        }
        handler.postDelayed(r, 200)
    }
    fun editTheCounterText(p0: View?,number:Int){
        if (p0!! == addBtn) {
            // add button pressed
            if (number == 0) {
                removeBtn.visibility = View.VISIBLE
                textView.visibility = View.VISIBLE
            } else if (number == 1) {
                removeBtn.setImageResource(R.drawable.ic_baseline_remove_circle_24)
            }
            productCountLiveData.value = number + 1

        } else {
            // remove button pressed

            if (number > 1) {
                // removing item
                if (number == 2) {
                    // show delete button instead of remove image
                    removeBtn.setImageResource(R.drawable.ic_baseline_delete_24)
                }
                productCountLiveData.value = number - 1

            } else {
                // deleting item
                productCountLiveData.value = 0
                removeBtn.visibility = View.GONE
                textView.visibility = View.GONE
            }

        }
    }

    fun setProductCountStr(text: String?) {
        Log.i(TAG, "setProductCountStr: model given number of product : $text")
        val newNumber = text?.toInt()
        productCountLiveData.value = newNumber!!
        if (newNumber>1){
            removeBtn.setImageResource(R.drawable.ic_baseline_remove_circle_24)
        }
    }

    override fun onClick(p0: View?) {
        textView.text = "..."
        aButtonClicked(p0)

    }
    fun getProductCountLiveData():MutableLiveData<Int>{
        return productCountLiveData
    }
}