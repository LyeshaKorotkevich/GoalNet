package by.gsu.goalnet

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class PostActivity : AppCompatActivity() {
    private lateinit var dbHelper: DBHelper

    private lateinit var textViewUsernamePost: TextView
    private lateinit var imageNavAvatarPost: ImageView

    private lateinit var  title: TextView
    private lateinit var content: TextView
    private lateinit var createdAt: TextView
    private lateinit var username: TextView
    private lateinit var userAvatar: ImageView
    private lateinit var commentCount: TextView
    private lateinit var deleteIcon: ImageView

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post)

        val imageLogout: ImageView = findViewById(R.id.imageLogout)
        imageLogout.setOnClickListener {
            navigateToLogin()
        }

        textViewUsernamePost = findViewById(R.id.textViewUsernamePost)
        imageNavAvatarPost = findViewById(R.id.imageNavAvatarPost)
        dbHelper = DBHelper(this)

        title = findViewById(R.id.postTitle)
        content = findViewById(R.id.postContent)
        createdAt = findViewById(R.id.postDate)
        username = findViewById(R.id.userName)
        userAvatar = findViewById(R.id.userAvatar)
        commentCount = findViewById(R.id.commentCount)
        deleteIcon = findViewById(R.id.deleteIcon)

        val userId = intent.getIntExtra("CURRENT_USER_ID", -1)
        val postId = intent.getIntExtra("SELECTED_POST_ID", -1)
        if (userId != -1) {
            loadUserData(userId)

            val post: Post = dbHelper.getPostById(postId)!!
            val postUser: User = dbHelper.getUserById(post.userId)!!

            deleteIcon.visibility = View.GONE
            title.text = post.title
            content.text = post.content
            createdAt.text = post.createdAt
            username.text = postUser.username
            commentCount.text = dbHelper.getCommentCountForPost(postId).toString()

            val byteArray = postUser.photo
            Glide.with(this)
                .load(byteArray ?: R.drawable.default_avatar)
                .into(userAvatar)

            setupPostButton(userId, postId)

            val recyclerView: RecyclerView = findViewById(R.id.recyclerViewComments)
            val comments: List<Comment> = dbHelper.getCommentsByPostId(postId)
            val sortedComments = comments.sortedByDescending {
                LocalDateTime.parse(it.createdAt, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
            }
            val layoutManager = LinearLayoutManager(this)
            recyclerView.layoutManager = layoutManager
            val adapter = CommentsAdapter(sortedComments, dbHelper)
            recyclerView.adapter = adapter

        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setupPostButton(userId: Int, postId: Int) {
        val buttonComment: Button = findViewById(R.id.buttonComment)
        buttonComment.setOnClickListener {
            val editTextComment: EditText = findViewById(R.id.editTextComment)
            val content = editTextComment.text.toString()

            if (content.isNotEmpty()) {
                val success = createNewComment(userId, postId, content)
                if (success) {
                    editTextComment.setText("")
                    showToast("New comment created successfully")
                } else {
                    showToast("Failed to create comment")
                }
            } else {
                showToast("Please enter Content")
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNewComment(userId: Int, postId: Int, content: String): Boolean {
        val newComment = Comment(
            content = content,
            userId = userId,
            postId = postId,
            createdAt = getTimeStamp(),
        )

        val success = dbHelper.addComment(newComment)

        if (success) {
            commentCount.text = dbHelper.getCommentCountForPost(postId).toString()

            val recyclerView: RecyclerView = findViewById(R.id.recyclerViewComments)
            val adapter = recyclerView.adapter as CommentsAdapter
            val comments: List<Comment> = dbHelper.getCommentsByPostId(postId)
            val sortedComments = comments.sortedByDescending {
                LocalDateTime.parse(it.createdAt, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
            }
            adapter.updateComments(sortedComments)
            adapter.notifyDataSetChanged()
        }

        return success
    }


    private fun loadUserData(userId: Int) {
        val currentUser = dbHelper.getUserById(userId)
        currentUser?.let {
            textViewUsernamePost.text = it.username
            val byteArray = it.photo
            Glide.with(this)
                .load(byteArray ?: R.drawable.default_avatar)
                .into(imageNavAvatarPost)

            textViewUsernamePost.setOnClickListener { navigateToProfileActivity(userId) }
            imageNavAvatarPost.setOnClickListener { navigateToProfileActivity(userId) }
        }
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
}
