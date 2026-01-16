package com.test.androiddemoosv.ui

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.test.androiddemoosv.databinding.ItemModuleBinding
import com.test.androiddemoosv.model.Module

class ModuleAdapter(
    private var modules: List<Module>,
    private val onClick: (Module) -> Unit
) : RecyclerView.Adapter<ModuleAdapter.VH>() {

    private var coolingActive = false
    private var accessibleIds: List<String> = emptyList()


    fun submitList(newList: List<Module>) {
        modules = newList
        notifyDataSetChanged()
    }

    fun accessableListModules(newList: List<String>) {
        accessibleIds = newList
        notifyDataSetChanged()
    }

    fun updateCoolingState(isCooling: Boolean) {
        coolingActive = isCooling
        notifyDataSetChanged()
    }


    inner class VH(
        private val binding: ItemModuleBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(module: Module, accessibleIds: List<String>, holder: VH) {
            binding.tvModule.text = module.title

            val context = holder.itemView.context
            val isPayment = module.id == "payments"
            val isAccessible = accessibleIds.contains(module.id)

            val state = when {
                isPayment && coolingActive -> CardState.COOLING
                isAccessible -> CardState.ACTIVE
                else -> CardState.DISABLED
            }

            binding.cardRoot.applyCardState(state, context)

            binding.cardRoot.setOnClickListener {
                onClick(module)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ItemModuleBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(modules[position],accessibleIds, holder)

    }

    override fun getItemCount(): Int = modules.size
}
