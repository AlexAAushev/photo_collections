package com.practicum.practice_work_1_1.presentations

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.practicum.practice_work_1_1.R
import com.practicum.practice_work_1_1.compound.PagingAdapter
import com.practicum.practice_work_1_1.models.CollectionsViewModel
import com.practicum.practice_work_1_1.data.db.LoadCollections
import com.practicum.practice_work_1_1.databinding.FragmentCollectionsBinding


import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class CollectionsFragment : Fragment() {

    private var _binding: FragmentCollectionsBinding? = null
    private val binding get() = _binding!!
    private val collectionAdapter =
        PagingAdapter { Collections -> onItemClick(Collections) }
    private val viewModel: CollectionsViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return CollectionsViewModel() as T
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCollectionsBinding.inflate(inflater, container, false)
        val root: View = binding.root
        binding.collectionsRecycler.adapter = collectionAdapter
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        startLoad()
    }

    private fun startLoad() {
        try {
            viewModel.pagedPhoto.onEach {
                collectionAdapter.submitData(it)
            }.launchIn(viewLifecycleOwner.lifecycleScope)
        } catch (e: Exception) {
            println("Error 3: $e")
        }
    }

    private fun onItemClick(item: LoadCollections) {
        val bundle = Bundle()
        val tags = mutableListOf<String>()
        item.tags.onEach {
            tags.add("#${it.title}")
        }
        val totalPhoto = resources.getQuantityString(
            R.plurals.collection_photo_sum,
            item.total_photos, item.total_photos
        )
        bundle.putString("CollectionID", item.id)
        bundle.putString("Collection-title", item.title)
        bundle.putString(
            "Collection-description",
            item.description
        )
        bundle.putString("Collection_TAGs", tags.joinToString(", ").replace(",", ""))
        bundle.putString("Collection-nickname", "@${item.user.username}")
        bundle.putString("Collection-Total-Photo", totalPhoto)
        bundle.putString("Collection-poster", item.cover_photo.urls.regular)
        findNavController().navigate(R.id.collectionsDetails, bundle)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}