package com.musicplayer.notifications

import android.app.Application
import android.app.NotificationManager
import android.content.Context
import com.musicplayer.extensions.systemService
import org.koin.dsl.bind
import org.koin.dsl.module

val notificationModule = module {
    factory<NotificationManager> {
        get<Application>().systemService(Context.NOTIFICATION_SERVICE)
    }

    single {
        NotificationsImplementation(get(), get())
    } bind Notifications::class
}