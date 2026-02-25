package com.example.primerparcial

import android.app.Application
import com.example.primerparcial.data.AppDatabase

class PrimerParcialApplication : Application() {
    val database: AppDatabase by lazy {
        AppDatabase.getDatabase(this)
    }
}
