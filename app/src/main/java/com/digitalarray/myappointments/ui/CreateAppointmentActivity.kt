package com.digitalarray.myappointments.ui

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.digitalarray.myappointments.R
import com.digitalarray.myappointments.io.ApiService
import com.digitalarray.myappointments.model.Doctor
import com.digitalarray.myappointments.model.Specialty
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_create_apointment.*
import kotlinx.android.synthetic.main.card_view_step_one.*
import kotlinx.android.synthetic.main.card_view_step_two.*
import kotlinx.android.synthetic.main.card_view_step_three.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

import java.util.*
import kotlin.collections.ArrayList

class CreateAppointmentActivity : AppCompatActivity() {

    private val apiService : ApiService by lazy{
        ApiService.create()
    }

    private val selectedCalendar = Calendar.getInstance()
    private var selectedTimeRadioBtn: RadioButton? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_apointment)

        btnNext.setOnClickListener {
            if (etDescription.text.toString().length < 7) {
                etDescription.error = getString(R.string.validate_appointment_description)
            } else {
                //Continue to step 2
                cvStep1.visibility = View.GONE
                cvStep2.visibility = View.VISIBLE
            }
        }

        btnNext2.setOnClickListener {
            when {
                etScheduleDate.text.toString().isEmpty() -> {
                    etScheduleDate.error = getString(R.string.validate_appointment_date)
                }
                selectedTimeRadioBtn == null -> {
                    Snackbar.make(
                        createAppointmentlinearLayout,
                        R.string.validate_appointment_time,
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
                else -> {
                    //Continue to step 3
                    showAppointmentDataToConfirm()
                    cvStep2.visibility = View.GONE
                    cvStep3.visibility = View.VISIBLE
                }
            }
        }

        btnConfirmAppointment.setOnClickListener {
            Toast.makeText(this, "Cita registrada correctamente", Toast.LENGTH_LONG).show()
            finish()
        }


        loadSpecialties()
        listenSpecialtyChanges()

        val doctorsOptions = arrayOf("DoctorA", "DoctorB", "DoctorC", "DoctorD")
        spinnerDoctors.adapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, doctorsOptions)
    }

    private fun loadSpecialties(){
        val call = apiService.getSpecialties()
        call.enqueue(object: Callback<ArrayList<Specialty>> {
            override fun onFailure(call: Call<ArrayList<Specialty>>, t: Throwable)
            {
                Toast.makeText(this@CreateAppointmentActivity, getString(R.string.error_loading_specialties), Toast.LENGTH_LONG).show()
                finish()
            }

            override fun onResponse(call: Call<ArrayList<Specialty>>, response: Response<ArrayList<Specialty>>) {
                if(response.isSuccessful){ //(200..300)
                    val specialties = response.body()
                    spinnerSpecialties.adapter = ArrayAdapter<Specialty>(this@CreateAppointmentActivity, android.R.layout.simple_list_item_1,
                        specialties!!)
                    //No devería llevar !!
                }
            }
        })
    }

    private fun listenSpecialtyChanges(){
        spinnerSpecialties.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(adapter: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val specialty = adapter?.getItemAtPosition(position) as Specialty
                loadDoctors(specialty.id)
            }
        }
    }

    private fun loadDoctors(specialtyId: Int){
        val call= apiService.getDoctors(specialtyId)
        call.enqueue(object: Callback<ArrayList<Doctor>>{
            override fun onFailure(call: Call<ArrayList<Doctor>>, t: Throwable) {
                Toast.makeText(this@CreateAppointmentActivity, getString(R.string.error_loading_doctors), Toast.LENGTH_LONG).show()
            }

            override fun onResponse(call: Call<ArrayList<Doctor>>, response: Response<ArrayList<Doctor>>) {
                if(response.isSuccessful) { //(200..300)
                    val doctors = response.body()
                    spinnerDoctors.adapter = ArrayAdapter<Doctor>(
                        this@CreateAppointmentActivity, android.R.layout.simple_list_item_1,
                        doctors!!)
                }
            }
        })
    }

    private fun showAppointmentDataToConfirm() {
        tvConfirmDescription.text = etDescription.text.toString()
        tvConfirmSpecialty.text = spinnerSpecialties.selectedItem.toString()

        val selectedRadioButtonId = radioGroupType.checkedRadioButtonId
        val selectedRadioType = radioGroupType.findViewById<RadioButton>(selectedRadioButtonId)

        tvConfirmType.text = selectedRadioType.text.toString()

        tvConfirmDoctorName.text = spinnerDoctors.selectedItem.toString()
        tvConfirmDate.text = etScheduleDate.text.toString()
        tvConfirmTime.text = selectedTimeRadioBtn?.text.toString()
    }

    fun onClickScheduledDate(v: View) {
        val year = selectedCalendar.get(Calendar.YEAR)
        val month = selectedCalendar.get(Calendar.MONTH)
        val dayOfMonth = selectedCalendar.get(Calendar.DAY_OF_MONTH)

        val listener = DatePickerDialog.OnDateSetListener { datePicker, y, m, d ->
            //Toast.makeText(this, "$y-$m-$d", Toast.LENGTH_SHORT).show()
            selectedCalendar.set(y, m, d)
            etScheduleDate.setText(
                resources.getString(
                    R.string.date_format,
                    y,
                    m.twoDigits(),
                    d.twoDigits()
                )
            )
            etScheduleDate.error = null
            displayRadioButtons()
        }
        //new dialog
        val datePickerDialog = DatePickerDialog(this, listener, year, month, dayOfMonth)
        val datePicker = datePickerDialog.datePicker
        //set limits
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_MONTH, 1)
        datePicker.minDate = selectedCalendar.timeInMillis //+1
        calendar.add(Calendar.DAY_OF_MONTH, 29)
        datePicker.maxDate = calendar.timeInMillis  //+30
        //show dialog
        datePickerDialog.show()

    }

    private fun displayRadioButtons() {

        //  radioGroup.clearCheck()
        selectedTimeRadioBtn = null
        radioGroupLeft.removeAllViews()
        radioGroupRight.removeAllViews()


        val hours = arrayListOf("3:00 PM", "3:30 PM", "4:00 PM", "4:30 PM")
        var goToLeft = true

        hours.forEach {
            val radioButton = RadioButton(this)
            radioButton.id = View.generateViewId()
            radioButton.text = it

            radioButton.setOnClickListener { view ->
                selectedTimeRadioBtn?.isChecked = false

                selectedTimeRadioBtn = view as RadioButton?
                selectedTimeRadioBtn?.isChecked = true
            }

            if (goToLeft)
                radioGroupLeft.addView(radioButton)
            else
                radioGroupRight.addView(radioButton)
            goToLeft = !goToLeft
        }
    }

    private fun Int.twoDigits() = if (this >= 10) this.toString() else "$this"

    override fun onBackPressed() {
        when {
            cvStep3.visibility == View.VISIBLE -> {
                cvStep3.visibility = View.GONE
                cvStep2.visibility = View.VISIBLE
            }
            cvStep2.visibility == View.VISIBLE -> {
                cvStep2.visibility = View.GONE
                cvStep1.visibility = View.VISIBLE

            }
            cvStep1.visibility == View.VISIBLE -> {
                val builder = AlertDialog.Builder(this)
                builder.setTitle(getString(R.string.dialog_create_appointment_exit_title))
                builder.setMessage(getString(R.string.dialog_create_appointment_exit_message))
                builder.setPositiveButton(getString(R.string.dialog_create_appointment_exit_positive_btn)) { _, _ ->
                    finish()
                }
                builder.setNegativeButton(getString(R.string.dialog_create_appointment_exit_negative_bt)) { dialog, _ ->
                    dialog.dismiss()
                }

                val dialog = builder.create()
                dialog.show()
            }
        }
    }
}

