package com.gps.gyment.di

import ExerciseRepository
import HistoryRepository
import HomeRepository
import AuthRepository
import UserRepository

import ExerciseViewModel
import HistoryViewModel
import HomeViewModel
import LoginViewModel
import ProfileViewModel

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.gps.gyment.ui.viewmodels.ExerciseDetailViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val appModule = module {
    singleOf(::ExerciseRepository)
    singleOf(::HistoryRepository)
    singleOf(::HomeRepository)
    singleOf(::AuthRepository)
    singleOf(::UserRepository)

    single { FirebaseFirestore.getInstance() }
    single { FirebaseAuth.getInstance() }

    viewModel { ExerciseViewModel(get()) }
    viewModel { ExerciseDetailViewModel(get()) }
    viewModel { HistoryViewModel() }
    viewModel { HomeViewModel(get()) }
    viewModel { LoginViewModel(get()) }
    viewModel { ProfileViewModel(get()) }
}
