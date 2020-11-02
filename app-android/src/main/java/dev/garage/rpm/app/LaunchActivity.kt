package dev.garage.rpm.app

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import dev.garage.rpm.app.counter.CounterActivity
import dev.garage.rpm.app.main.MainActivity
import dev.garage.rpm.app.permissions.PermissionsActivity
import dev.garage.rpm.app.socketIO.SocketActivity
import dev.garage.rpm.app.validation.FormValidationActivity

class LaunchActivity : AppCompatActivity() {

    private lateinit var counterSample: View
    private lateinit var mainSample: View
    private lateinit var formValidationSample: View
    private lateinit var permissionsSample: View
    private lateinit var socketIOSample: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_launch)

        counterSample = findViewById(R.id.counterSample)
        mainSample = findViewById(R.id.mainSample)
        formValidationSample = findViewById(R.id.formValidationSample)
        permissionsSample = findViewById(R.id.permissionsSample)
        socketIOSample = findViewById(R.id.socketIO)

        counterSample.setOnClickListener {
            launchActivity(CounterActivity::class.java)
        }

        mainSample.setOnClickListener {
            launchActivity(MainActivity::class.java)
        }

        formValidationSample.setOnClickListener {
            launchActivity(FormValidationActivity::class.java)
        }

        permissionsSample.setOnClickListener {
            launchActivity(PermissionsActivity::class.java)
        }

        socketIOSample.setOnClickListener {
            launchActivity(SocketActivity::class.java)
        }
    }

    private fun launchActivity(clazz: Class<out Activity>) {
        startActivity(Intent(this, clazz))
    }
}