package fragment
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.example.myapplication.R
import androidx.databinding.DataBindingUtil
import com.example.myapplication.AccountChangeActivity
import com.example.myapplication.PasswordChangeActivity
import com.example.myapplication.databinding.MePageBinding
import service.SharedPreferencesManager
import java.io.File

class MeFragment : Fragment(R.layout.me_page){
    private lateinit var takePictureLauncher: ActivityResultLauncher<Uri>
    private lateinit var pickImageLauncher: ActivityResultLauncher<String>
    private lateinit var binding: MePageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        takePictureLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { isSuccess ->
            if (isSuccess) {
                // 图片成功保存到了提供的 Uri，可以进一步处理
            }
        }

        pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            // 使用 uri
            // 例如：binding.imageView.setImageURI(uri)
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

    private fun openCamera() {
        val photoUri = createImageUri() // 创建图片Uri
        photoUri?.let {
          takePictureLauncher.launch(photoUri) // 启动相机
        }
    }

    private fun openGallery() {
        pickImageLauncher.launch("image/*")
    }

    private fun createImageUri(): Uri? {
        val context = requireContext() // 获取Fragment的Context
        val fileName = "temp_photo_" + System.currentTimeMillis() + ".jpg"
        val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val tempFile = File(storageDir, fileName)
        return FileProvider.getUriForFile(context, "${context.packageName}.provider", tempFile)
    }
}
