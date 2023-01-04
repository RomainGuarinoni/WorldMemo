package com.example.worldmemo.ui.add

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.example.worldmemo.databinding.FragmentAddBinding
import com.example.worldmemo.utils.PermissionUtils

class AddFragment : Fragment() {

    private var _binding: FragmentAddBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var permissionUtils: PermissionUtils

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {


        _binding = FragmentAddBinding.inflate(inflater, container, false)
        val root: View = binding.root




        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val createAudioButton = binding.addAudio
        val createPhotoButton = binding.addPhoto

        permissionUtils = PermissionUtils(requireActivity())

        createAudioButton.setOnClickListener {

            val audioPermission =
                permissionUtils.isPermissionAllowed(permissionUtils.PERMISSION_AUDIO)
            val writePermission =
                permissionUtils.isPermissionAllowed(permissionUtils.PERMISSION_WRITE_STORAGE)
            val readPermission =
                permissionUtils.isPermissionAllowed(permissionUtils.PERMISSION_READ_STORAGE)

            if (audioPermission && writePermission && readPermission) {
                val action = AddFragmentDirections.actionNavigationAddToAddAudioFragment(null)

                Navigation.findNavController(view).navigate(action)
            } else {
                Toast.makeText(
                    requireActivity(),
                    "The permission to record audio is not allowed...",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        createPhotoButton.setOnClickListener {

            val writePermission =
                permissionUtils.isPermissionAllowed(permissionUtils.PERMISSION_WRITE_STORAGE)
            val readPermission =
                permissionUtils.isPermissionAllowed(permissionUtils.PERMISSION_READ_STORAGE)

            if (writePermission && readPermission) {
                val action = AddFragmentDirections.actionNavigationAddToAddPhotoFragment(null)

                Navigation.findNavController(view).navigate(action)
            } else {
                Toast.makeText(
                    requireActivity(),
                    "The permission to record photo is not allowed...",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}