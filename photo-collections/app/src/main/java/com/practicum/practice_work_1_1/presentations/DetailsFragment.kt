package com.practicum.practice_work_1_1.presentations

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import coil.load
import com.practicum.practice_work_1_1.R
import com.practicum.practice_work_1_1.data.db.LoadPhotoResponse
import com.practicum.practice_work_1_1.compound.CompoundDetailsAdapter
import com.practicum.practice_work_1_1.databinding.FragmentDetailsBinding
import com.practicum.practice_work_1_1.models.CollectionsViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class DetailsFragment : Fragment() {

    lateinit var collectionID: String
    lateinit var collectionPoster: String
    private var _binding: FragmentDetailsBinding? = null
    private val binding get() = _binding!!
    private val collectionsDetailsAdapter =
        CompoundDetailsAdapter { LoadPhotoResponse -> onItemClick(LoadPhotoResponse) }
    private val viewModel: CollectionsViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return CollectionsViewModel() as T
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDetailsBinding.inflate(inflater, container, false)
        binding.collectionDetailsRecycler.adapter = collectionsDetailsAdapter
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val errorMessage = getString(R.string.collections_photo_unsplash_plus)
        collectionID = arguments?.getString("CollectionID").toString()
        collectionPoster = arguments?.getString("Collection-poster").toString()

        binding.collectionName.text = arguments?.getString("Collection-title").toString()
        binding.collectionTags.text = arguments?.getString("Collection_TAGs").toString()
        binding.collectionDescription.text =
            arguments?.getString("Collection-description").toString()
        binding.authorNickname.text = arguments?.getString("Collection-nickname").toString()
        binding.collectionTotalPhoto.text =
            arguments?.getString("Collection-Total-Photo").toString()
        binding.backgroundCover.load(collectionPoster) {
            target {
                binding.progress.isVisible = false
                binding.collectionDetailsInterface.isVisible = true
                binding.backgroundCover.setImageDrawable(it)
            }
        }
        val sub = collectionPoster.contains("plus.unsplash")
        if (sub) {
            customToast(errorMessage)
        }
        viewModel.id = collectionID
        viewModel.pagedPhotoCollection.onEach {
            collectionsDetailsAdapter.submitData(it)
        }.launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun onItemClick(item: LoadPhotoResponse) {
        val bundle = Bundle()
        bundle.putString("PhotoId", item.id)
        findNavController().navigate(R.id.feedPhotoDetails, bundle)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}