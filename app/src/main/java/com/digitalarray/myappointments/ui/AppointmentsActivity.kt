package com.digitalarray.myappointments.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.digitalarray.myappointments.R
import com.digitalarray.myappointments.model.Appointment
import kotlinx.android.synthetic.main.activity_appointments.*

class AppointmentsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_appointments)

        val appointments = ArrayList<Appointment>()
        appointments.add(
            Appointment(
                1,
                "Médico Test",
                "17/12/2019",
                "4:00 PM"
            )
        )
        appointments.add(
            Appointment(
                2,
                "Médico Test 2",
                "17/12/2020",
                "4:30 PM"
            )
        )
        appointments.add(
            Appointment(
                3,
                "Médico Test 3",
                "15/12/2019",
                "5:00 PM"
            )
        )

        rvAppointments.layoutManager = LinearLayoutManager(this)
        rvAppointments.adapter =
            AppointmentAdapter(appointments)
    }
}
