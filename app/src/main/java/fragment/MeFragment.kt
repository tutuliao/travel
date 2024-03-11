package fragment
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.example.myapplication.R
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.example.myapplication.AccountChangeActivity
import com.example.myapplication.PasswordChangeActivity
import com.example.myapplication.databinding.MePageBinding
import http.RetrofitManager
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import service.SharedPreferencesManager
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MeFragment : Fragment(R.layout.me_page){
    private lateinit var takePictureLauncher: ActivityResultLauncher<Uri>
    private lateinit var pickImageLauncher: ActivityResultLauncher<String>
    private lateinit var binding: MePageBinding
    private lateinit var currentPhotoPath: String
    private val requestCameraPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                // 权限已授予，打开相机
                openCamera()

            } else {
                // 权限被拒绝，显示一个解释为什么需要这个权限的消息
                Toast.makeText(context, "Camera permission is required to take photos", Toast.LENGTH_LONG).show()
            }
        }
    private val apiService = RetrofitManager.getInstance().provideApiService()
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)


        takePictureLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { isSuccess ->
            if (isSuccess) {
                // 图片成功保存到了提供的 Uri，可以进一步处理
                File(currentPhotoPath).also { file ->
                    MediaScannerConnection.scanFile(context, arrayOf(file.toString()), null, null)
                    val requestFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
                    val body = MultipartBody.Part.createFormData("picture", file.name, requestFile)

                    apiService.uploadAvatar(body).enqueue(object : retrofit2.Callback<ResponseBody> {
                        override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                            if (response.isSuccessful) {
                                Glide.with(this@MeFragment)
                                    .load(File(currentPhotoPath))
                                    .into(binding.meCircularImageView)
                                service.Toast.showToast(requireContext(),"头像更新成功")
                            } else {
                                service.Toast.showToast(requireContext(),"替换头像失败")
                            }
                        }

                        override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                            // 处理失败
                        }
                    })

                }
            }

        }

        pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            // 使用 uri
            // 例如：binding.imageView.setImageURI(uri)
            uri?.let {
                uploadImageFromUri(it)
            }
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
          binding = DataBindingUtil.inflate(
            inflater, R.layout.me_page, container, false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.meAccountChange.setOnClickListener{
            val intent = Intent(requireActivity(), AccountChangeActivity::class.java)
            startActivity(intent)
        }

        binding.mePasswordChange.setOnClickListener{
            val intent = Intent(requireActivity(), PasswordChangeActivity::class.java)
            startActivity(intent)
        }

        binding.meCircularImageView.setOnClickListener{
           showPopupMenu(it)
        }

        val sharedPreferencesManager = SharedPreferencesManager.getInstance(requireContext())
        Glide.with(this@MeFragment)
            .load(sharedPreferencesManager.userAvatar)
            .into(binding.meCircularImageView)

    }

    override fun onStart() {
        val sharedPreferencesManager = SharedPreferencesManager.getInstance(requireContext())
        binding.meId.text = sharedPreferencesManager.userId.toString()
        binding.meName.text = sharedPreferencesManager.userName
        super.onStart()
    }

    private fun showPopupMenu(view: View) {
        val popup = PopupMenu(requireContext(), view)
        popup.menuInflater.inflate(R.menu.avatar_menu, popup.menu)
        popup.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_open_camera -> {
                    openCamera()
                    true
                }
                R.id.action_open_gallery -> {
                    openGallery()
                    true
                }
                else -> false
            }
        }
        popup.show()
    }

    //打开摄像头
    private fun openCamera() {
        when (PackageManager.PERMISSION_GRANTED) {
            ContextCompat.checkSelfPermission(
                requireContext(), android.Manifest.permission.CAMERA
            ) -> {
                // 相机权限已经被授予，安全地打开相机
                val photoFile: File = createPublicImageFile()
                val photoURI: Uri = FileProvider.getUriForFile(
                    requireContext(),
                    "${requireContext().packageName}.provider",
                    photoFile
                )
                takePictureLauncher.launch(photoURI)
            }
            else -> {
                // 请求相机权限
                requestCameraPermissionLauncher.launch(android.Manifest.permission.CAMERA)
            }
        }
    }

    //打开手机图库
    private fun openGallery() {
        pickImageLauncher.launch("image/*")
    }

    //使用私有目录保存图片
    private fun createPrivateImageFile(): File {
        // 创建一个时间戳，用于命名图片文件
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val imageFileName: String = "JPEG_" + timeStamp + "_"

        // 获取应用的外部存储私有目录的 Pictures 目录
        val storageDir: File? = requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES)

        // 创建一个空文件来保存图片
        return File.createTempFile(
            imageFileName, /* 文件名 */
            ".jpg", /* 文件后缀 */
            storageDir /* 文件目录 */
        ).apply {
            // 保存文件: 路径用于后续访问
            currentPhotoPath = absolutePath
        }
    }

    //使用公共目录保存图片
    private fun createPublicImageFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val imageFileName = "JPEG_$timeStamp"

        // 使用公共目录保存图片
        val storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)

        val image = File.createTempFile(
            imageFileName, /* 文件名 */
            ".jpg", /* 文件后缀 */
            storageDir /* 文件目录 */
        )

        // 保存文件路径用于后续访问
        currentPhotoPath = image.absolutePath
        return image
    }

    //图片库选取图片上传
    private fun uploadImageFromUri(imageUri: Uri) {
        val inputStream = context?.contentResolver?.openInputStream(imageUri)
        val file = createPrivateImageFile() // 使用前面定义的 createImageFile 方法创建一个临时文件
        inputStream?.let { stream ->
            file.outputStream().use {
                // 将输入流拷贝到文件中
                stream.copyTo(it)
            }
        }

        // 准备文件部分
        val requestFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
        val body = MultipartBody.Part.createFormData("picture", file.name, requestFile)

        // 发起上传请求
        apiService.uploadAvatar(body).enqueue(object : retrofit2.Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    Glide.with(this@MeFragment)
                        .load(file)
                        .into(binding.meCircularImageView)
                    Toast.makeText(requireContext(),"头像更新成功", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(),"替换头像失败", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(requireContext(), "上传失败: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

}
