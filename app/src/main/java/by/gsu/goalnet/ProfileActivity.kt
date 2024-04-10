package by.gsu.goalnet

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import java.io.InputStream

class ProfileActivity : AppCompatActivity() {

    private lateinit var currentUser: User
    private var dbHelper: DBHelper? = null

    private lateinit var textNavUsername: TextView
    private lateinit var imageNavAvatarProfile: ImageView

    private lateinit var editTextName: EditText
    private lateinit var editTextInfo: EditText
    private lateinit var editTextCountry: EditText
    private lateinit var editTextCity: EditText
    private lateinit var editTextBirthday: EditText
    private lateinit var editTextClub: EditText
    private lateinit var editTextPlayer: EditText
    private lateinit var buttonSave: Button
    private lateinit var imageViewAvatar: ImageView

    companion object {
        private const val GALLERY_REQUEST_CODE = 100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        val imageLogout: ImageView = findViewById(R.id.imageLogoutP)
        imageLogout.setOnClickListener {
            navigateToLogin()
        }

        imageViewAvatar = findViewById(R.id.imageViewAvatarProfile)
        imageViewAvatar.setOnClickListener {
            val galleryIntent =
                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE)
        }

        textNavUsername = findViewById(R.id.textNavUsernameProfile)
        imageNavAvatarProfile = findViewById(R.id.imageNavAvatarProfile)

        editTextName = findViewById(R.id.editTextNameProfile)
        editTextName.isFocusable = false
        editTextName.isClickable = false
        editTextName.isFocusableInTouchMode = false

        editTextInfo = findViewById(R.id.editTextInfoProfile)
        editTextCountry = findViewById(R.id.editTextCountryProfile)
        editTextCity = findViewById(R.id.editTextCityProfile)
        editTextBirthday = findViewById(R.id.editTextBirthdayProfile)
        editTextClub = findViewById(R.id.editTextClubProfile)
        editTextPlayer = findViewById(R.id.editTextPlayerProfile)
        buttonSave = findViewById(R.id.buttonSaveProfile)

        dbHelper = DBHelper(this)

        val userId = intent.getIntExtra("USER_ID", -1)
        if (userId != -1) {
            currentUser = dbHelper?.getUserById(userId)!!
        }

        textNavUsername.text = currentUser.username

        val byteArray = currentUser.photo
        if (byteArray != null) {
            val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)

            Glide.with(this)
                .load(bitmap)
                .into(imageNavAvatarProfile)
            Glide.with(this)
                .load(bitmap)
                .into(imageViewAvatar)
        } else {
            Glide.with(this)
                .load(R.drawable.default_avatar)
                .into(imageNavAvatarProfile)
            Glide.with(this)
                .load(R.drawable.default_avatar)
                .into(imageViewAvatar)
        }

        editTextName.setText(currentUser.username)
        editTextInfo.setText(currentUser.info)
        editTextCountry.setText(currentUser.country)
        editTextCity.setText(currentUser.city)
        editTextBirthday.setText(currentUser.birthday)
        editTextClub.setText(currentUser.club)
        editTextPlayer.setText(currentUser.player)

        buttonSave.setOnClickListener {
            saveUserProfile()
        }
    }

    private fun saveUserProfile() {
        currentUser.info = editTextInfo.text.toString()
        currentUser.country = editTextCountry.text.toString()
        currentUser.city = editTextCity.text.toString()
        currentUser.birthday = editTextBirthday.text.toString()
        currentUser.club = editTextClub.text.toString()
        currentUser.player = editTextPlayer.text.toString()

        dbHelper?.updateUser(currentUser)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GALLERY_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            val selectedImage: Uri? = data.data
            selectedImage?.let {
                val inputStream: InputStream? = contentResolver.openInputStream(selectedImage)
                val bytes: ByteArray? = inputStream?.readBytes()
                inputStream?.close()

                currentUser.photo = bytes

                bytes?.let {
                    val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                    imageViewAvatar.setImageBitmap(bitmap)
                }

                val user = User(
                    id = currentUser.id,
                    username = currentUser.username,
                    password = currentUser.password,
                    info = currentUser.info,
                    birthday = currentUser.birthday,
                    country = currentUser.country,
                    city = currentUser.city,
                    club = currentUser.club,
                    player = currentUser.player,
                    photo = bytes
                )

                dbHelper?.updateUser(user)
            }
        }
    }

    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}
