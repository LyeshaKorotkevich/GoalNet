package by.gsu.goalnet

import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide

class AnotherProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_another_profile)

        val textViewUsername: TextView = findViewById(R.id.textNavUsernameAnotherProfile)
        val imageViewNavAvatar: ImageView = findViewById(R.id.imageNavAvatarAnotherProfile)

        val imageViewAvatar: ImageView = findViewById(R.id.imageViewAvatarAnotherProfile)
        val textViewName: TextView = findViewById(R.id.textViewNameAnotherProfile)
        val textViewInfo: TextView = findViewById(R.id.textViewInfoAnotherProfile)
        val textViewCountry: TextView = findViewById(R.id.textViewCountryAnotherProfile)
        val textViewCity: TextView = findViewById(R.id.textViewCityAnotherProfile)
        val textViewBirthday: TextView = findViewById(R.id.textViewBirthdayAnotherProfile)
        val textViewClub: TextView = findViewById(R.id.textViewClubAnotherProfile)
        val textViewPlayer: TextView = findViewById(R.id.textViewPlayerAnotherProfile)

        val postUserId = intent.getIntExtra("USER_ID", -1)
        val currentUserId = intent.getIntExtra("CURRENT_USER_ID", -1)
        if (postUserId != -1) {
            val dbHelper = DBHelper(this)
            val postUser = dbHelper.getUserById(postUserId)
            val currentUser = dbHelper.getUserById(currentUserId)

            postUser?.let {
                textViewUsername.text = currentUser!!.username

                val byteArrayC = currentUser.photo
                if (byteArrayC != null) {
                    val bitmap = BitmapFactory.decodeByteArray(byteArrayC, 0, byteArrayC.size)

                    Glide.with(this)
                        .load(bitmap)
                        .into(imageViewNavAvatar)
                } else {
                    Glide.with(this)
                        .load(R.drawable.default_avatar)
                        .into(imageViewNavAvatar)
                }

                val byteArray = postUser.photo
                if (byteArray != null) {
                    val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)

                    Glide.with(this)
                        .load(bitmap)
                        .into(imageViewAvatar)
                } else {
                    Glide.with(this)
                        .load(R.drawable.default_avatar)
                        .into(imageViewAvatar)
                }

                textViewName.text = it.username
                textViewInfo.text = if (it.info.isNullOrEmpty()) {
                    "No information"
                } else {
                    it.info
                }
                textViewCountry.text = if (it.country.isNullOrEmpty()) {
                    "Country not available"
                } else {
                    it.country
                }
                textViewCity.text = if (it.city.isNullOrEmpty()) {
                    "City not available"
                } else {
                    it.city
                }
                textViewBirthday.text = if (it.birthday.isNullOrEmpty()) {
                    "Birthday not available"
                } else {
                    it.birthday
                }
                textViewClub.text = if (it.club.isNullOrEmpty()) {
                    "Favorite club not available"
                } else {
                    it.club
                }
                textViewPlayer.text = if (it.player.isNullOrEmpty()) {
                    "Favorite player not available"
                } else {
                    it.player
                }
            }
        }
    }
}
