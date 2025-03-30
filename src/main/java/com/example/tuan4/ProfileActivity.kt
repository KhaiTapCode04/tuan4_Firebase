package com.example.tuan4

import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.storage.FirebaseStorage
import com.example.tuan4.databinding.ActivityProfileBinding
import java.text.SimpleDateFormat
import java.util.*

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient

    // Khởi tạo ActivityResultLauncher cho việc chọn ảnh
    private val imagePicker = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            uploadImageToFirebase(uri)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Khởi tạo Firebase Auth
        auth = FirebaseAuth.getInstance()

        // Cấu hình Google Sign-In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        // Hiển thị thông tin người dùng
        setupUserInfo()

        // Nút quay lại
        binding.btnBack.setOnClickListener {
            finish()
        }

        // Nút chọn ngày sinh
        binding.etDateOfBirth.setOnClickListener {
            showDatePickerDialog()
        }

        // Nút đăng xuất
        binding.btnLogout.setOnClickListener {
            logoutUser()
        }

        // Nút cập nhật thông tin
        binding.btnUpdate.setOnClickListener {
            updateUserProfile()
        }

        // Nút thay đổi ảnh đại diện
        binding.ivProfilePicture.setOnClickListener {
            imagePicker.launch("image/*") // Chọn ảnh từ bộ sưu tập
        }
    }

    private fun setupUserInfo() {
        val user = auth.currentUser
        if (user == null) {
            Toast.makeText(this, "Bạn chưa đăng nhập!", Toast.LENGTH_SHORT).show()
            Log.e("ProfileActivity", "Người dùng chưa đăng nhập!")
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        // Hiển thị thông tin người dùng
        binding.etName.setText(user.displayName ?: "Chưa có tên")
        binding.etEmail.setText(user.email ?: "Không có email")

        Glide.with(this)
            .load(user.photoUrl)
            .placeholder(R.drawable.default_profile)
            .error(R.drawable.default_profile)
            .into(binding.ivProfilePicture)  // Hiển thị ảnh đại diện
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDay ->
                val selectedDate = Calendar.getInstance()
                selectedDate.set(selectedYear, selectedMonth, selectedDay)
                val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                binding.etDateOfBirth.setText(dateFormat.format(selectedDate.time))
            },
            year,
            month,
            day
        )

        datePickerDialog.show()
    }

    private fun updateUserProfile() {
        val newName = binding.etName.text.toString().trim()
        if (newName.isEmpty()) {
            Toast.makeText(this, "Tên không được để trống!", Toast.LENGTH_SHORT).show()
            return
        }

        val user = auth.currentUser
        val profileUpdates = userProfileChangeRequest {
            displayName = newName
        }

        user?.updateProfile(profileUpdates)?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(this, "Cập nhật thành công!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Cập nhật thất bại: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun logoutUser() {
        auth.signOut()  // Đăng xuất Firebase
        googleSignInClient.signOut().addOnCompleteListener {
            Toast.makeText(this, "Đã đăng xuất!", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    // Hàm tải ảnh lên Firebase Storage
    private fun uploadImageToFirebase(imageUri: Uri) {
        val storageRef = FirebaseStorage.getInstance().reference
        val imageRef = storageRef.child("profile_pictures/${auth.currentUser?.uid}.jpg")

        imageRef.putFile(imageUri)
            .addOnSuccessListener {
                imageRef.downloadUrl.addOnSuccessListener { uri ->
                    updateUserProfileWithImage(uri.toString()) // Cập nhật URL ảnh vào Firebase Auth
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Tải ảnh thất bại!", Toast.LENGTH_SHORT).show()
            }
    }

    // Hàm cập nhật ảnh vào Firebase Auth
    private fun updateUserProfileWithImage(photoUrl: String) {
        val user = auth.currentUser
        val profileUpdates = userProfileChangeRequest {
            photoUri = Uri.parse(photoUrl)
        }

        user?.updateProfile(profileUpdates)?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(this, "Cập nhật ảnh thành công!", Toast.LENGTH_SHORT).show()
                Glide.with(this).load(photoUrl).into(binding.ivProfilePicture)  // Hiển thị ảnh mới
            } else {
                Toast.makeText(this, "Cập nhật thất bại!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}