package www.cafelink.com.cafelink.activities

import android.content.Context
import android.content.Intent
import android.util.Log

import com.daimajia.androidanimations.library.Techniques
import com.viksaa.sssplash.lib.activity.AwesomeSplash
import com.viksaa.sssplash.lib.cnst.Flags
import com.viksaa.sssplash.lib.model.ConfigSplash
import timber.log.Timber
import www.cafelink.com.cafelink.BuildConfig
import www.cafelink.com.cafelink.CafeApplication
import www.cafelink.com.cafelink.R
import www.cafelink.com.cafelink.util.UserSessionManager

import javax.inject.Inject
import com.afollestad.materialdialogs.MaterialDialog
import android.text.InputType
import android.widget.Toast
import www.cafelink.com.cafelink.models.User
import java.util.*


/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
class SplashActivity : AwesomeSplash() {

    @Inject
    lateinit var userSessionManager: UserSessionManager

    //DO NOT OVERRIDE onCreate()!
    //if you need to start some services do it in initSplash()!
    override fun initSplash(configSplash: ConfigSplash) {
        /* you don't have to override every property */
        CafeApplication.injectionComponent.inject(this)

        val duration: Int
        if (BuildConfig.DEBUG) {
            duration = 500
        } else {
            // Production duration for into splash screen animations.
            duration = 1000
        }
        Timber.d("Splash duration: %s", duration)

        //Customize Circular Reveal
        configSplash.backgroundColor = R.color.md_brown_100
        configSplash.animCircularRevealDuration = duration //int ms
        configSplash.revealFlagX = Flags.REVEAL_LEFT  //or Flags.REVEAL_RIGHT
        configSplash.revealFlagY = Flags.REVEAL_TOP //or Flags.REVEAL_BOTTOM

        //Choose LOGO OR PATH; if you don't provide String value for path it's logo by default

        //Customize Logo
        configSplash.logoSplash = R.drawable.cafe_link_trans_170
        configSplash.animLogoSplashDuration = duration //int ms
        configSplash.animLogoSplashTechnique = Techniques.FadeIn //choose one form Techniques (ref: https://github.com/daimajia/AndroidViewAnimations)

        //Customize Title
        configSplash.titleSplash = getString(R.string.slogan)
        configSplash.titleTextColor = R.color.md_brown_500
        configSplash.titleTextSize = 18f //float value
        configSplash.animTitleDuration = duration
        configSplash.animTitleTechnique = Techniques.FadeInDown
        // configSplash.setTitleFont("fonts/myfont.ttf"); //provide string to your font located in assets/fonts/
    }

    override fun animationsFinished() {
        // Transit to another activity here or perform other actions.
        if (userSessionManager.hasLoggedInUser()) {
            proceed()
            return
        }

        MaterialDialog.Builder(this)
                .title("Enter Username")
                .content("Enter your user name for the CafeLink app - note you can only set this once for the device")
                .autoDismiss(false)
                .inputType(InputType.TYPE_CLASS_TEXT)
                .input(R.string.input_hint, R.string.input_prefill, MaterialDialog.InputCallback { dialog, input ->
                    val userName = input.toString()

                    if (userName.isBlank()) {
                        Toast.makeText(this, "Username must not be empty", Toast.LENGTH_SHORT).show()
                    } else {
                        userSessionManager.setLoggedInUser(this, User(UUID.randomUUID().toString(), userName, ""))
                        dialog.dismiss()
                        proceed()
                    }
                    // Do something
                }).show()
    }

    private fun proceed() {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }

}
