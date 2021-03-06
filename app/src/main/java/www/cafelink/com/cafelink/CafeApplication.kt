package www.cafelink.com.cafelink

import android.app.Application

import com.facebook.FacebookSdk
import timber.log.Timber
import www.cafelink.com.cafelink.injection.CafeModule
import www.cafelink.com.cafelink.injection.DaggerInjectionComponent
import www.cafelink.com.cafelink.injection.InjectionComponent

class CafeApplication : Application() {
    private var mInjectionComponent: InjectionComponent? = null

    override fun onCreate() {
        super.onCreate()
        FacebookSdk.setClientToken(getString(R.string.facebook_client_token))
        mInjectionComponent = DaggerInjectionComponent.builder()
                .cafeModule(CafeModule(this))
                .build()
        Timber.plant(Timber.DebugTree());
        app = this
    }

    companion object {

        val CAFE_SEARCH_STRING = "cafe"

        val LAST_LOCATION_LOC = "last_location"

        val CAFE_DATA = "cafe_data"
        val CONVERSATION_DATA = "conv_data"

        val MY_PERMISSIONS_ACCESS_FINE_LOCATION = 100
        var app: CafeApplication? = null

        val injectionComponent: InjectionComponent
            get() = app!!.mInjectionComponent!!
    }
}
