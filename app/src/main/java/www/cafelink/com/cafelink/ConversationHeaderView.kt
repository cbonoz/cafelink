package www.cafelink.com.cafelink

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import android.view.LayoutInflater
import android.view.Gravity
import android.widget.ImageView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.conversation_header.view.*
import www.cafelink.com.cafelink.models.ConversationCafe


class ConversationHeaderView(context: Context?, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : LinearLayout(context, attrs, defStyleAttr) {

    fun setCafe(cafe: ConversationCafe) {

        val pictureData = cafe.picture.data
        val imageView = findViewById<ImageView>(R.id.cafeImageView)
        imageView.layoutParams.width = pictureData.width * 5
        imageView.layoutParams.height = pictureData.height * 5
        Glide.with(this)
                .load(pictureData.url)
                .into(imageView)

        name.text = cafe.name
        location.text = cafe.location.toString()
    }

    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0) {
        orientation = LinearLayout.HORIZONTAL
        gravity = Gravity.CENTER_VERTICAL

        val ctx: Context
        if (context == null) {
            ctx = CafeApplication.app as Context
        } else {
            ctx = context
        }

        val inflater = ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        inflater.inflate(R.layout.conversation_header, this, true)
    }


}