package by.gsu.goalnet

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class CommentsAdapter(private var comments: List<Comment>, private val dbHelper: DBHelper) :
    RecyclerView.Adapter<CommentsAdapter.CommentViewHolder>() {

    inner class CommentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val commentAvatar: ImageView = itemView.findViewById(R.id.commentUserAvatar)
        val commenterName: TextView = itemView.findViewById(R.id.commentUserName)
        val commentText: TextView = itemView.findViewById(R.id.commentText)
        val commentDate: TextView = itemView.findViewById(R.id.commentDate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_comment, parent, false)
        return CommentViewHolder(view)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        val currentComment = comments[position]

        val commentUser = dbHelper.getUserById(currentComment.userId)
        holder.commenterName.text = commentUser!!.username
        holder.commentText.text = currentComment.content
        holder.commentDate.text = currentComment.createdAt

        val byteArray = commentUser.photo
        Glide.with(holder.itemView.context)
            .load(byteArray ?: R.drawable.default_avatar)
            .into(holder.commentAvatar)
    }

    override fun getItemCount(): Int {
        return comments.size
    }

    fun updateComments(updatedComments: List<Comment>) {
        comments = updatedComments
    }
}
