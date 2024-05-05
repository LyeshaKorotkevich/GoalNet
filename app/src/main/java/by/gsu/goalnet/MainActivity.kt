package by.gsu.goalnet

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class MainActivity : AppCompatActivity() {
    private lateinit var textViewUsernameM: TextView
    private lateinit var imageNavAvatarM: ImageView
    private lateinit var dbHelper: DBHelper

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val imageLogout: ImageView = findViewById(R.id.imageLogout)
        imageLogout.setOnClickListener {
            navigateToLogin()
        }

        textViewUsernameM = findViewById(R.id.textViewUsernameM)
        imageNavAvatarM = findViewById(R.id.imageNavAvatarM)
        dbHelper = DBHelper(this)

        val userId = intent.getIntExtra("USER_ID", -1)
        if (userId != -1) {
            loadUserData(userId)
            setupPostButton(userId)
            setupTagsSpinner()
            setupTagsSpinner2()
            setupSearchField()

            val recyclerView: RecyclerView = findViewById(R.id.recyclerViewPosts)
            val posts: List<Post> = dbHelper.getPosts()
            val sortedPosts = posts.sortedByDescending {
                LocalDateTime.parse(it.createdAt, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
            }
            val layoutManager = LinearLayoutManager(this)
            recyclerView.layoutManager = layoutManager
            val adapter = ForumAdapter(sortedPosts, dbHelper, userId)
            recyclerView.adapter = adapter

        }

        setupSearchField()
    }

    private fun setupTagsSpinner() {
        val spinnerTags: Spinner = findViewById(R.id.spinnerTags)
        val tagsList = dbHelper.getTags() // Получаем список тегов из базы данных
        // Получение списка имен тегов из списка тегов типа Tag
        val tagNamesList = tagsList.map { it.name }
        val adapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, tagNamesList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerTags.adapter = adapter
    }

    private fun setupTagsSpinner2() {
        val spinnerTagsSearch: Spinner = findViewById(R.id.spinnerTagsSearch)
        val tagsList = dbHelper.getTags() // Получаем список тегов из базы данных
        // Получение списка имен тегов из списка тегов типа Tag
        val tagNamesList = tagsList.map { it.name }
        val adapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, tagNamesList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerTagsSearch.adapter = adapter
    }

    private fun loadUserData(userId: Int) {
        val currentUser = dbHelper.getUserById(userId)
        currentUser?.let {
            textViewUsernameM.text = it.username
            val byteArray = it.photo
            Glide.with(this)
                .load(byteArray ?: R.drawable.ic_launcher_background)
                .into(imageNavAvatarM)

            textViewUsernameM.setOnClickListener { navigateToProfileActivity(userId) }
            imageNavAvatarM.setOnClickListener { navigateToProfileActivity(userId) }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setupPostButton(userId: Int) {
        val buttonPost: Button = findViewById(R.id.buttonPost)
        buttonPost.setOnClickListener {
            val editTextTitle: EditText = findViewById(R.id.editTextTitle)
            val editTextContent: EditText = findViewById(R.id.editTextContent)
            val title = editTextTitle.text.toString()
            val content = editTextContent.text.toString()

            val spinnerTags: Spinner = findViewById(R.id.spinnerTags)
            val selectedTagName = spinnerTags.selectedItem as String // Получаем выбранное имя тега

            val selectedTag = dbHelper.getTags().find { it.name == selectedTagName }

            if (title.isNotEmpty() && content.isNotEmpty() && selectedTag != null) {
                val success = createNewPost(userId, title, content, selectedTag.id)
                if (success) {
                    editTextTitle.setText("")
                    editTextContent.setText("")
                    showToast("New post created successfully")
                } else {
                    showToast("Failed to create post")
                }
            } else {
                showToast("Please enter Title and Content")
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNewPost(userId: Int, title: String, content: String, TagID: Int): Boolean {
        val newPost = Post(
            title = title,
            content = content,
            userId = userId,
            createdAt = getTimeStamp(),
            updatedAt = getTimeStamp(),
            tagId = TagID
        )
        val success = dbHelper.addPost(newPost)
        if (success) {
            val recyclerView: RecyclerView = findViewById(R.id.recyclerViewPosts)
            val adapter = recyclerView.adapter as ForumAdapter
            adapter.updatePosts(dbHelper.getPosts().sortedByDescending {
                LocalDateTime.parse(it.createdAt, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
            })
            adapter.notifyDataSetChanged()
        }
        return success
    }

    private fun navigateToProfileActivity(userId: Int) {
        val intent = Intent(this, ProfileActivity::class.java)
        intent.putExtra("USER_ID", userId)
        startActivity(intent)
    }

    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getTimeStamp(): String {
        val currentDateTime = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        return currentDateTime.format(formatter)
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

/*    private fun setupSearchField() {
        val editTextSearch: EditText = findViewById(R.id.editTextSearch)
        editTextSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val searchText = s.toString()
                if (searchText.isNotEmpty()) {
                    val posts = dbHelper.getPostsByTitle(searchText)
                    updateRecyclerView(posts)
                } else {

                    val allPosts = dbHelper.getPosts()
                    updateRecyclerView(allPosts)
                }
            }
        })
    }*/

    private fun setupSearchField() {
        val editTextSearch: EditText = findViewById(R.id.editTextSearch)
        val spinnerTagsSearch: Spinner = findViewById(R.id.spinnerTagsSearch)

        editTextSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val searchText = s.toString()
                val selectedTag = spinnerTagsSearch.selectedItem as String // Получаем выбранный тег

                val posts = if (searchText.isNotEmpty() && selectedTag.isNotEmpty()) {
                    dbHelper.getPostsByTagAndTitle(selectedTag, searchText) // Запрос по тегу и заголовку
                } else if (searchText.isNotEmpty()) {
                    dbHelper.getPostsByTitle(searchText) // Запрос только по заголовку
                } else if (selectedTag.isNotEmpty()) {
                    //dbHelper.getTagIdByName(selectedTag)?.let { dbHelper.getPostsByTag(it) } // Запрос только по тегу
                    dbHelper.getPostsByTag(selectedTag) // Запрос только по тегу
                } else {
                    dbHelper.getPosts() // Запрос без фильтрации
                }

                if (posts != null) {
                    updateRecyclerView(posts)
                }
            }
        })
    }

    private fun updateRecyclerView(posts: List<Post>) {
        val recyclerView: RecyclerView = findViewById(R.id.recyclerViewPosts)
        val adapter = recyclerView.adapter as ForumAdapter
        adapter.updatePosts(posts.sortedByDescending {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                LocalDateTime.parse(it.createdAt, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
            } else {
                TODO("VERSION.SDK_INT < O")
            }
        })
        adapter.notifyDataSetChanged()
    }

}
