package com.gps.gyment.di

import ExerciseRepository
import ExerciseViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val appModule = module {
    singleOf(::ExerciseRepository)
    single { FirebaseFirestore.getInstance() }
    single { FirebaseAuth.getInstance() }
    viewModel { ExerciseViewModel(get()) }
}
