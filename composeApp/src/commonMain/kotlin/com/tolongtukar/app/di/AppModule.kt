package com.tolongtukar.app.di

import com.tolongtukar.app.converter.ConversionEngine
import org.koin.core.module.Module
import org.koin.dsl.module

val appModule: Module = module {
    single { ConversionEngine }
}
