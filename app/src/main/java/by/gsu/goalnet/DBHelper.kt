package by.gsu.goalnet

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

data class User(
    var id: Int = -1,
    var username: String,
    var password: String,
    var info: String?,
    var birthday: String?,
    var country: String?,
    var city: String?,
    var club: String?,
    var player: String?,
    var photo: ByteArray?
)

data class Post(
    var id: Int = -1,
    var title: String,
    var content: String,
    var userId: Int,
    var createdAt: String,
    var updatedAt: String,
    var commentCount: Int = 0
)

data class Comment(
    var id: Int = -1,
    var content: String,
    var postId: Int,
    var userId: Int,
    var createdAt: String
)

class DBHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "GoalNetDB"

        private const val TABLE_USERS = "users"
        private const val KEY_ID = "id"
        private const val KEY_USERNAME = "username"
        private const val KEY_PASSWORD = "password"
        private const val KEY_INFO = "info"
        private const val KEY_BIRTHDAY = "birthday"
        private const val KEY_COUNTRY = "country"
        private const val KEY_CITY = "city"
        private const val KEY_CLUB = "club"
        private const val KEY_PLAYER = "player"
        private const val KEY_PHOTO = "photo"

        private const val TABLE_ROLES = "roles"
        private const val KEY_ROLE_ID = "id"
        private const val KEY_ROLE_NAME = "name"

        private const val TABLE_USER_ROLES = "user_roles"
        private const val KEY_USER_ROLES_USER_ID = "user_id"
        private const val KEY_USER_ROLES_ROLE_ID = "role_id"

        private const val TABLE_POSTS = "posts"
        private const val KEY_POST_ID = "id"
        private const val KEY_POST_TITLE = "title"
        private const val KEY_POST_CONTENT = "content"
        private const val KEY_POST_USER_ID = "user_id"
        private const val KEY_POST_CREATED_AT = "created_at"
        private const val KEY_POST_UPDATED_AT = "updated_at"

        private const val TABLE_COMMENTS = "comments"
        private const val KEY_COMMENT_ID = "id"
        private const val KEY_COMMENT_CONTENT = "content"
        private const val KEY_COMMENT_POST_ID = "post_id"
        private const val KEY_COMMENT_USER_ID = "user_id"
        private const val KEY_COMMENT_CREATED_AT = "createdAt"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val CREATE_USERS_TABLE = ("CREATE TABLE $TABLE_USERS ($KEY_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$KEY_USERNAME TEXT NOT NULL, $KEY_PASSWORD TEXT NOT NULL, $KEY_INFO TEXT, " +
                "$KEY_BIRTHDAY TEXT, $KEY_COUNTRY TEXT, $KEY_CITY TEXT, $KEY_CLUB TEXT, " +
                "$KEY_PLAYER TEXT, $KEY_PHOTO BLOB)")

        db?.execSQL(CREATE_USERS_TABLE)

        val CREATE_POSTS_TABLE = ("CREATE TABLE $TABLE_POSTS " +
                "($KEY_POST_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$KEY_POST_TITLE TEXT NOT NULL, " +
                "$KEY_POST_CONTENT TEXT NOT NULL, " +
                "$KEY_POST_USER_ID INTEGER NOT NULL, " +
                "$KEY_POST_CREATED_AT TEXT NOT NULL, " +
                "$KEY_POST_UPDATED_AT TEXT NOT NULL, " +
                "FOREIGN KEY ($KEY_POST_USER_ID) REFERENCES $TABLE_USERS($KEY_ID))")

        db?.execSQL(CREATE_POSTS_TABLE)

        val CREATE_COMMENTS_TABLE = ("CREATE TABLE $TABLE_COMMENTS " +
                "($KEY_COMMENT_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$KEY_COMMENT_CONTENT TEXT NOT NULL, " +
                "$KEY_COMMENT_POST_ID INTEGER NOT NULL, " +
                "$KEY_COMMENT_USER_ID INTEGER NOT NULL, " +
                "$KEY_COMMENT_CREATED_AT TEXT DEFAULT CURRENT_TIMESTAMP, " +
                "FOREIGN KEY ($KEY_COMMENT_POST_ID) REFERENCES $TABLE_POSTS($KEY_POST_ID), " +
                "FOREIGN KEY ($KEY_COMMENT_USER_ID) REFERENCES $TABLE_USERS($KEY_ID))")

        db?.execSQL(CREATE_COMMENTS_TABLE)

        val CREATE_ROLES_TABLE = ("CREATE TABLE $TABLE_ROLES " +
                "($KEY_ROLE_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$KEY_ROLE_NAME TEXT NOT NULL)")

        db?.execSQL(CREATE_ROLES_TABLE)

        val CREATE_USER_ROLES_TABLE = ("CREATE TABLE $TABLE_USER_ROLES " +
                "($KEY_USER_ROLES_USER_ID INT NOT NULL, " +
                "$KEY_USER_ROLES_ROLE_ID INT NOT NULL, " +
                "FOREIGN KEY ($KEY_USER_ROLES_USER_ID) REFERENCES $TABLE_USERS($KEY_ID), " +
                "FOREIGN KEY ($KEY_USER_ROLES_ROLE_ID) REFERENCES $TABLE_ROLES($KEY_ROLE_ID), " +
                "UNIQUE ($KEY_USER_ROLES_USER_ID, $KEY_USER_ROLES_ROLE_ID))")

        db?.execSQL(CREATE_USER_ROLES_TABLE)

        val userRoleValues = ContentValues().apply {
            put(KEY_ROLE_NAME, "USER")
        }
        db?.insert(TABLE_ROLES, null, userRoleValues)

        val adminRoleValues = ContentValues().apply {
            put(KEY_ROLE_NAME, "ADMIN")
        }
        db?.insert(TABLE_ROLES, null, adminRoleValues)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_POSTS")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_COMMENTS")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_ROLES")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_USER_ROLES")

        onCreate(db)
    }

    fun addUser(user: User): Boolean {
        val db = this.writableDatabase
        val values = ContentValues()
        values.apply {
            put(KEY_USERNAME, user.username)
            put(KEY_PASSWORD, user.password)
            put(KEY_INFO, user.info)
            put(KEY_BIRTHDAY, user.birthday)
            put(KEY_COUNTRY, user.country)
            put(KEY_CITY, user.city)
            put(KEY_CLUB, user.club)
            put(KEY_PLAYER, user.player)
            put(KEY_PHOTO, user.photo)
        }

        val success = db.insert(TABLE_USERS, null, values) != -1L
        db.close()
        return success
    }

    fun addPost(post: Post): Boolean {
        val db = this.writableDatabase
        val values = ContentValues()
        values.apply {
            put(KEY_POST_TITLE, post.title)
            put(KEY_POST_CONTENT, post.content)
            put(KEY_POST_USER_ID, post.userId)
            put(KEY_POST_CREATED_AT, post.createdAt)
            put(KEY_POST_UPDATED_AT, post.updatedAt)
        }

        val success = db.insert(TABLE_POSTS, null, values) != -1L
        db.close()
        return success
    }

    fun addComment(comment: Comment): Boolean {
        val db = this.writableDatabase
        val values = ContentValues()
        values.apply {
            put(KEY_COMMENT_CONTENT, comment.content)
            put(KEY_COMMENT_POST_ID, comment.postId)
            put(KEY_COMMENT_USER_ID, comment.userId)
            put(KEY_COMMENT_CREATED_AT, comment.createdAt)
        }

        val success = db.insert(TABLE_COMMENTS, null, values) != -1L
        db.close()
        return success
    }


    @SuppressLint("Range")
    fun getUserById(userId: Int): User? {
        val db = this.readableDatabase
        val cursor: Cursor = db.query(
            TABLE_USERS,
            arrayOf(
                KEY_ID,
                KEY_USERNAME,
                KEY_PASSWORD,
                KEY_INFO,
                KEY_BIRTHDAY,
                KEY_COUNTRY,
                KEY_CITY,
                KEY_CLUB,
                KEY_PLAYER,
                KEY_PHOTO
            ),
            "$KEY_ID=?",
            arrayOf(userId.toString()),
            null,
            null,
            null,
            null
        )
        return if (cursor.moveToFirst()) {
            val user = User(
                cursor.getInt(cursor.getColumnIndex(KEY_ID)),
                cursor.getString(cursor.getColumnIndex(KEY_USERNAME)),
                cursor.getString(cursor.getColumnIndex(KEY_PASSWORD)),
                cursor.getString(cursor.getColumnIndex(KEY_INFO)),
                cursor.getString(cursor.getColumnIndex(KEY_BIRTHDAY)),
                cursor.getString(cursor.getColumnIndex(KEY_COUNTRY)),
                cursor.getString(cursor.getColumnIndex(KEY_CITY)),
                cursor.getString(cursor.getColumnIndex(KEY_CLUB)),
                cursor.getString(cursor.getColumnIndex(KEY_PLAYER)),
                cursor.getBlob(cursor.getColumnIndex(KEY_PHOTO))
            )
            cursor.close()
            user
        } else {
            null
        }
    }

    @SuppressLint("Range")
    fun getPostById(postId: Int): Post? {
        val db = this.readableDatabase
        val cursor: Cursor = db.query(
            TABLE_POSTS,
            arrayOf(
                KEY_POST_ID,
                KEY_POST_TITLE,
                KEY_POST_CONTENT,
                KEY_POST_USER_ID,
                KEY_POST_CREATED_AT,
                KEY_POST_UPDATED_AT,
            ),
            "$KEY_POST_ID=?",
            arrayOf(postId.toString()),
            null,
            null,
            null,
            null
        )
        return if (cursor.moveToFirst()) {
            val post = Post(
                cursor.getInt(cursor.getColumnIndex(KEY_POST_ID)),
                cursor.getString(cursor.getColumnIndex(KEY_POST_TITLE)),
                cursor.getString(cursor.getColumnIndex(KEY_POST_CONTENT)),
                cursor.getInt(cursor.getColumnIndex(KEY_POST_USER_ID)),
                cursor.getString(cursor.getColumnIndex(KEY_POST_CREATED_AT)),
                cursor.getString(cursor.getColumnIndex(KEY_POST_UPDATED_AT))
            )
            cursor.close()
            post
        } else {
            null
        }
    }


    @SuppressLint("Range")
    fun getUserByUsername(username: String): User? {
        val db = this.readableDatabase
        val cursor: Cursor = db.query(
            TABLE_USERS,
            arrayOf(
                KEY_ID,
                KEY_USERNAME,
                KEY_PASSWORD,
                KEY_INFO,
                KEY_BIRTHDAY,
                KEY_COUNTRY,
                KEY_CITY,
                KEY_CLUB,
                KEY_PLAYER,
                KEY_PHOTO
            ),
            "$KEY_USERNAME=?",
            arrayOf(username),
            null,
            null,
            null,
            null
        )
        return if (cursor.moveToFirst()) {
            val user = User(
                cursor.getInt(cursor.getColumnIndex(KEY_ID)),
                cursor.getString(cursor.getColumnIndex(KEY_USERNAME)),
                cursor.getString(cursor.getColumnIndex(KEY_PASSWORD)),
                cursor.getString(cursor.getColumnIndex(KEY_INFO)),
                cursor.getString(cursor.getColumnIndex(KEY_BIRTHDAY)),
                cursor.getString(cursor.getColumnIndex(KEY_COUNTRY)),
                cursor.getString(cursor.getColumnIndex(KEY_CITY)),
                cursor.getString(cursor.getColumnIndex(KEY_CLUB)),
                cursor.getString(cursor.getColumnIndex(KEY_PLAYER)),
                cursor.getBlob(cursor.getColumnIndex(KEY_PHOTO))
            )
            cursor.close()
            user
        } else {
            null
        }
    }

    @SuppressLint("Range")
    fun getPosts(): List<Post> {
        val postsList = mutableListOf<Post>()
        val db = this.readableDatabase
        val cursor: Cursor = db.rawQuery("SELECT * FROM $TABLE_POSTS", null)

        if (cursor.moveToFirst()) {
            do {
                val post = Post(
                    cursor.getInt(cursor.getColumnIndex(KEY_POST_ID)),
                    cursor.getString(cursor.getColumnIndex(KEY_POST_TITLE)),
                    cursor.getString(cursor.getColumnIndex(KEY_POST_CONTENT)),
                    cursor.getInt(cursor.getColumnIndex(KEY_POST_USER_ID)),
                    cursor.getString(cursor.getColumnIndex(KEY_POST_CREATED_AT)),
                    cursor.getString(cursor.getColumnIndex(KEY_POST_UPDATED_AT))
                )
                postsList.add(post)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return postsList
    }

    @SuppressLint("Range")
    fun getCommentsByPostId(postId: Int): List<Comment> {
        val commentsList = mutableListOf<Comment>()
        val db = this.readableDatabase
        val cursor: Cursor = db.query(
            TABLE_COMMENTS,
            arrayOf(
                KEY_COMMENT_ID,
                KEY_COMMENT_CONTENT,
                KEY_COMMENT_POST_ID,
                KEY_COMMENT_USER_ID,
                KEY_COMMENT_CREATED_AT
            ),
            "$KEY_COMMENT_POST_ID=?",
            arrayOf(postId.toString()),
            null,
            null,
            null,
            null
        )

        if (cursor.moveToFirst()) {
            do {
                val comment = Comment(
                    cursor.getInt(cursor.getColumnIndex(KEY_COMMENT_ID)),
                    cursor.getString(cursor.getColumnIndex(KEY_COMMENT_CONTENT)),
                    cursor.getInt(cursor.getColumnIndex(KEY_COMMENT_POST_ID)),
                    cursor.getInt(cursor.getColumnIndex(KEY_COMMENT_USER_ID)),
                    cursor.getString(cursor.getColumnIndex(KEY_COMMENT_CREATED_AT))
                )
                commentsList.add(comment)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return commentsList
    }

    fun updateUser(user: User): Boolean {
        val db = this.writableDatabase
        val values = ContentValues()
        values.apply {
            put(KEY_USERNAME, user.username)
            put(KEY_PASSWORD, user.password)
            put(KEY_INFO, user.info)
            put(KEY_BIRTHDAY, user.birthday)
            put(KEY_COUNTRY, user.country)
            put(KEY_CITY, user.city)
            put(KEY_CLUB, user.club)
            put(KEY_PLAYER, user.player)
            put(KEY_PHOTO, user.photo)
        }

        val success = db.update(
            TABLE_USERS,
            values,
            "$KEY_ID = ?",
            arrayOf(user.id.toString())
        ) > 0
        db.close()
        return success
    }

    fun updatePost(post: Post): Boolean {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(KEY_POST_TITLE, post.title)
            put(KEY_POST_CONTENT, post.content)
            put(KEY_POST_USER_ID, post.userId)
            put(KEY_POST_CREATED_AT, post.createdAt)
            put(KEY_POST_UPDATED_AT, post.updatedAt)
        }

        val success = db.update(
            TABLE_POSTS,
            values,
            "$KEY_POST_ID = ?",
            arrayOf(post.id.toString())
        ) > 0
        db.close()
        return success
    }


    fun checkUser(username: String, password: String): Boolean {
        val db = this.readableDatabase
        val selection = "$KEY_USERNAME = ? AND $KEY_PASSWORD = ?"
        val selectionArgs = arrayOf(username, password)

        val cursor = db.query(
            TABLE_USERS,
            arrayOf(KEY_ID),
            selection,
            selectionArgs,
            null,
            null,
            null
        )

        val userExists = cursor.count > 0
        cursor.close()
        return userExists
    }

    fun checkUserExists(username: String): Boolean {
        val db = this.readableDatabase
        val selection = "$KEY_USERNAME = ?"
        val selectionArgs = arrayOf(username)

        val cursor = db.query(
            TABLE_USERS,
            arrayOf(KEY_ID),
            selection,
            selectionArgs,
            null,
            null,
            null
        )

        val userExists = cursor.count > 0
        cursor.close()
        return userExists
    }

    fun deletePost(postId: Int): Boolean {
        val db = this.writableDatabase
        val success = db.delete(TABLE_POSTS, "$KEY_POST_ID=?", arrayOf(postId.toString())) > 0
        db.close()
        return success
    }

    fun getCommentCountForPost(postId: Int): Int {
        val db = this.readableDatabase
        val selection = "$KEY_COMMENT_POST_ID = ?"
        val selectionArgs = arrayOf(postId.toString())

        val cursor = db.query(
            TABLE_COMMENTS,
            arrayOf(KEY_COMMENT_ID),
            selection,
            selectionArgs,
            null,
            null,
            null
        )

        val commentCount = cursor.count
        cursor.close()
        return commentCount
    }


    fun assignDefaultRoleToUser(userId: Int, roleId: Int): Boolean {
        val db = this.writableDatabase
        val values = ContentValues()
        values.apply {
            put(KEY_USER_ROLES_USER_ID, userId)
            put(KEY_USER_ROLES_ROLE_ID, roleId)
        }

        val success = db.insert(TABLE_USER_ROLES, null, values) != -1L
        db.close()
        return success
    }
}
