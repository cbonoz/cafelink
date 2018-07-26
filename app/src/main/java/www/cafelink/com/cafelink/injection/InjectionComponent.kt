package www.cafelink.com.cafelink.injection

import javax.inject.Singleton

import dagger.Component
import www.cafelink.com.cafelink.activities.MainActivity
import www.cafelink.com.cafelink.activities.SplashActivity
import www.cafelink.com.cafelink.fragments.*
import www.cafelink.com.cafelink.fragments.conversation.AbstractConversationFragment
import www.cafelink.com.cafelink.fragments.conversation.CafeConversationFragment
import www.cafelink.com.cafelink.fragments.conversation.RecentConversationFragment
import www.cafelink.com.cafelink.fragments.conversation.UserConversationFragment
import www.cafelink.com.cafelink.fragments.message.MessagesFragment

@Singleton
@Component(modules = arrayOf(CafeModule::class))
interface InjectionComponent {

    // Activities
//    fun inject(activity: LoginActivity)
    fun inject(activity: MainActivity)
    fun inject(activity: SplashActivity)
    fun inject(profileFragment: ProfileFragment)

    fun inject(cafeConversationFragment: CafeConversationFragment)
    fun inject(userConversationFragment: UserConversationFragment)
    fun inject(recentConversationFragment: RecentConversationFragment)
    fun inject(messagesFragment: MessagesFragment)
    fun inject(abstractConversationFragment: AbstractConversationFragment)
    fun inject(mapsFragment: MapsFragment)

    // Fragments
//    fun inject(favoritesFragment: FavoritesFragment)
//    fun inject(genomeFragment: CafeFragment)
//    fun inject(recipeFragment: RecipeFragment)
}
