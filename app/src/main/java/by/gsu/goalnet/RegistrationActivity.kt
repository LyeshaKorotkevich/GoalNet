package by.gsu.goalnet

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class RegistrationActivity : AppCompatActivity() {

    private lateinit var usernameEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var confirmPasswordEditText: EditText
    private lateinit var signUpButton: Button
    private lateinit var dbHelper: DBHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        usernameEditText = findViewById(R.id.editTextUsernameReg)
        passwordEditText = findViewById(R.id.editTextPasswordReg)
        confirmPasswordEditText = findViewById(R.id.editTextConfirmPasswordReg)
        signUpButton = findViewById(R.id.buttonReg)
        dbHelper = DBHelper(this)

        signUpButton.setOnClickListener {
            val username = usernameEditText.text.toString()
            val password = passwordEditText.text.toString()
            val confirmPassword = confirmPasswordEditText.text.toString()

            val userExists = dbHelper.checkUserExists(username)
            if (userExists) {
                Toast.makeText(
                    this,
                    "User with this username is already registered",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                if (password.length > 7) {
                    if (password == confirmPassword) {
                        val user = User(
                            username = username,
                            password = password,
                            info = null,
                            birthday = null,
                            country = null,
                            city = null,
                            club = null,
                            player = null,
                            photo = null
                        )
                        val added = dbHelper.addUser(user)
                        if (added) {
                            Toast.makeText(this, "User registered successfully", Toast.LENGTH_SHORT)
                                .show()

                            val currentUser = dbHelper.getUserByUsername(username)

                            val roleAssigned = currentUser?.id?.let { userId ->
                                dbHelper.assignDefaultRoleToUser(userId, 1)
                            }

                            if (roleAssigned == true) {
                                val intent = Intent(this, MainActivity::class.java)
                                intent.putExtra("USER_ID", currentUser?.id)
                                startActivity(intent)
                                finish()
                            } else {
                                Toast.makeText(this, "Assignation role user failed", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            Toast.makeText(this, "Registration failed", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(
                        this,
                        "Password should be at least 8 characters long",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
}
