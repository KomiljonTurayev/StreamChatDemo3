package com.example.streamchatdemo3.adapter

import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.streamchatdemo3.databinding.UserRowLayoutBinding
import com.example.streamchatdemo3.ui.users.UsersFragment
import com.example.streamchatdemo3.ui.users.UsersFragmentDirections
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.client.models.User


class UsersAdapter : RecyclerView.Adapter<UsersAdapter.MyViewHolder>() {

    private val client = ChatClient.instance()
    private var userList = emptyList<User>()

    class MyViewHolder(val binding: UserRowLayoutBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            UserRowLayoutBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentUser = userList[position]
        holder.binding.avatarImageView.setUserData(currentUser)
        holder.binding.usernameTextView.text = currentUser.id
        holder.binding.lastActiveTextView.text = convertDate(currentUser.lastActive!!.time)
        holder.binding.rootLayout.setOnClickListener {
            createNewChannel(currentUser.id, holder)
        }
    }

    private fun createNewChannel(selectedUser: String, holder: MyViewHolder) {
        client.createChannel(
            channelType = "messaging",
            members = listOf(client.getCurrentUser()!!.id, selectedUser)
        ).enqueue { result ->
            if (result.isSuccess) {
                navigateToChatFragment(holder, result.data().cid)
            }
        }
    }

    private fun navigateToChatFragment(holder: UsersAdapter.MyViewHolder, cid: String) {
        val action = UsersFragmentDirections.actionUsersFragmentToChatFragment(cid)
        holder.itemView.findNavController().navigate(action)
    }


    private fun convertDate(milliseconds: Long): String {
        return DateFormat.format("dd/MM/yyyy hh:mm a", milliseconds).toString()
    }

    override fun getItemCount() = userList.size
}