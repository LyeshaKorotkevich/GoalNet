package by.gsu.goalnet

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class ForumAdapter(private var posts: List<Post>, private val dbHelper: DBHelper, private val currentUserId: Int) :
    RecyclerView.Adapter<ForumAdapter.ForumViewHolder>() {

    inner class ForumViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.postTitle)
        val content: TextView = itemView.findViewById(R.id.postContent)
        val createdAt: TextView = itemView.findViewById(R.id.postDate)
        val username: TextView = itemView.findViewById(R.id.userName)
        val userAvatar: ImageView = itemView.findViewById(R.id.userAvatar)
        val commentCount: TextView = itemView.findViewById(R.id.commentCount)
        val deleteIcon: ImageView = itemView.findViewById(R.id.deleteIcon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ForumViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_forum_post, parent, false)
        return ForumViewHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ForumViewHolder, position: Int) {
        val currentPost = posts[position]
        holder.title.text = currentPost.title
        holder.content.text = currentPost.content
        holder.createdAt.text = currentPost.createdAt
        holder.commentCount.text = dbHelper.getCommentCountForPost(currentPost.id).toString()
        holder.itemView.findViewById<TextView>(R.id.postTag).text = dbHelper.getTagById(currentPost.tagId)!!.name

        val userId = currentPost.userId
        val user = dbHelper.getUserById(userId)

        user?.let {
            holder.username.text = it.username

            val byteArray = it.photo
            Glide.with(holder.itemView.context)
                .load(byteArray ?: R.drawable.default_avatar)
                .into(holder.userAvatar)

            if (currentUserId == userId) {
                holder.deleteIcon.visibility = View.VISIBLE
            } else {
                holder.deleteIcon.visibility = View.GONE
            }
        }

        holder.username.setOnClickListener { navigateToProfileActivity(holder.itemView, userId, currentUserId) }
        holder.userAvatar.setOnClickListener { navigateToProfileActivity(holder.itemView, userId, currentUserId) }

        holder.deleteIcon.setOnClickListener {
            showDeleteConfirmationDialog(holder.itemView.context, currentPost.id, position)
        }

        holder.itemView.setOnClickListener {
            val selectedPost = posts[position]

            val intent = Intent(holder.itemView.context, PostActivity::class.java)

            intent.putExtra("SELECTED_POST_ID", selectedPost.id)
            intent.putExtra("CURRENT_USER_ID", currentUserId)

            holder.itemView.context.startActivity(intent)
        }
    }

    fun updatePosts(updatedPosts: List<Post>) {
        posts = updatedPosts
    }

    override fun getItemCount(): Int {
        return posts.size
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun showDeleteConfirmationDialog(context: Context, postId: Int, position: Int) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Delete Post")
            .setMessage("Are you sure you want to delete this post?")
            .setPositiveButton("Yes") { _, _ ->
                dbHelper.deletePost(postId)

                updatePosts(dbHelper.getPosts().sortedByDescending {
                    LocalDateTime.parse(it.createdAt, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                })
                notifyDataSetChanged()

                Toast.makeText(context, "Post deleted successfully", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }

    private fun navigateToProfileActivity(view: View, userId: Int, currentUserId: Int) {
        val intent = Intent(view.context, AnotherProfileActivity::class.java)
        intent.putExtra("USER_ID", userId)
        intent.putExtra("CURRENT_USER_ID", currentUserId)
        view.context.startActivity(intent)
    }
}
