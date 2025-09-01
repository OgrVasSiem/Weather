package com.kamaz.weather.data.dataStore

import android.content.Context
import androidx.datastore.preferences.core.stringPreferencesKey
import com.kamaz.weather.data.dataStore.core.ApplicationDataStore
import com.kamaz.weather.utils.PairSerializer
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.serialization.builtins.ListSerializer
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthorizationDataStore @Inject constructor(
    @ApplicationContext context: Context
) : ApplicationDataStore<Pair<String, String>>(
    context = context,
    serializer = PairSerializer,
    key = stringPreferencesKey("authorization_store_key"),
    defaultValue = "" to "" ,
    fileName = "authorization_store"
)
