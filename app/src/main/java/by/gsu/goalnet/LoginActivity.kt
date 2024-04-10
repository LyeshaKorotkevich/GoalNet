package by.gsu.goalnet

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity


class LoginActivity : AppCompatActivity() {
    private var dbHelper: DBHelper? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        dbHelper = DBHelper(this)
    }

    fun loginUser(view: View) {
        val usernameEditText: EditText = findViewById(R.id.editTextUsernameL)
        val passwordEditText: EditText = findViewById(R.id.editTextPasswordL)

        val username = usernameEditText.text.toString()
        val password = passwordEditText.text.toString()

        val userExists: Boolean = dbHelper!!.checkUser(username, password)
        if (userExists) {
            val currentUser = dbHelper!!.getUserByUsername(username)

            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("USER_ID", currentUser?.id)
            startActivity(intent)
            finish()
        } else {
            Toast.makeText(this, "Неверный логин или пароль", Toast.LENGTH_SHORT).show()
        }
    }

    fun openRegistrationActivity(view: View) {
        val intent = Intent(this, RegistrationActivity::class.java)
        startActivity(intent)
    }
}
