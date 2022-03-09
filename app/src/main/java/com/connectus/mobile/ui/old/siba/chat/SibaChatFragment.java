package com.connectus.mobile.ui.old.siba.chat;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.connectus.mobile.R;
import com.connectus.mobile.api.dto.ProfileDto;
import com.connectus.mobile.api.dto.siba.SibaProfile;
import com.connectus.mobile.api.dto.siba.SibaProfileMember;
import com.connectus.mobile.api.dto.siba.chat.ChatMessage;
import com.connectus.mobile.common.Common;
import com.connectus.mobile.common.DetailedChatMessage;
import com.connectus.mobile.common.Sender;
import com.connectus.mobile.database.DbHandler;
import com.connectus.mobile.database.SharedPreferencesManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class SibaChatFragment extends Fragment {
    private static final String TAG = SibaChatFragment.class.getSimpleName();

    ProgressDialog pd;
    TextView textViewProfileSubject;
    EditText editTextNewMessage;
    ImageView imageViewBack, imageViewSend;

    SharedPreferencesManager sharedPreferencesManager;
    FragmentManager fragmentManager;
    SibaMessagesRecyclerAdapter sibaMessagesRecyclerAdapter;
    RecyclerView recyclerViewProfiles;
    String sibaProfileId;
    SibaProfile sibaProfile;
    Map<UUID, SibaProfileMember> sibaProfileMembers;
    List<DetailedChatMessage> detailedChatMessages;
    FirebaseFirestore db;

    String authentication;
    ProfileDto profileDTO;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle arguments = getArguments();
        if (arguments != null) {
            sibaProfileId = arguments.getString("sibaProfileId");
        }
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_siba_chat, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (sibaProfileId == null) {
            getActivity().onBackPressed();
        }
        fragmentManager = getActivity().getSupportFragmentManager();
        pd = new ProgressDialog(getActivity());
        sharedPreferencesManager = new SharedPreferencesManager(getContext());
        authentication = sharedPreferencesManager.getAuthenticationToken();
        profileDTO = sharedPreferencesManager.getProfile();
        db = FirebaseFirestore.getInstance();

        imageViewBack = view.findViewById(R.id.image_view_back);
        imageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        fetchProfileChats();
        listenForChats();
        fetchSibaProfile();
        populateSibaProfileMembersMap();

        textViewProfileSubject = view.findViewById(R.id.text_view_profile_subject);
        textViewProfileSubject.setText(sibaProfile.getSubject());

        detailedChatMessages = fetchDetailedMessages();
        recyclerViewProfiles = view.findViewById(R.id.recycler_view_siba_messages);
        sibaMessagesRecyclerAdapter = new SibaMessagesRecyclerAdapter(getContext(), detailedChatMessages, profileDTO);
        recyclerViewProfiles.setAdapter(sibaMessagesRecyclerAdapter);
        recyclerViewProfiles.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewProfiles.scrollToPosition(sibaMessagesRecyclerAdapter.getItemCount() - 1);

        editTextNewMessage = view.findViewById(R.id.edit_text_new_message);
        imageViewSend = view.findViewById(R.id.image_view_send);
        imageViewSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = editTextNewMessage.getText().toString().trim();
                if (!message.isEmpty()) {
                    ChatMessage chatMessage = new ChatMessage(UUID.randomUUID().toString(), sibaProfile.getId(), profileDTO.getProfileId(), message, new Date().getTime());
                    sendSibaMessage(chatMessage);
                } else {
                    Toast.makeText(getContext(), "Type message to send!", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        fetchSibaProfile();
        populateSibaProfileMembersMap();
        refreshChats();
    }

    public void sendSibaMessage(ChatMessage chatMessage) {
        db.collection("siba_profiles").document(chatMessage.getSiba_profile_id().toString())
                .collection("messages").document(chatMessage.getMessageId())
                .set(chatMessage)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        DbHandler dbHandler = new DbHandler(getContext());
                        dbHandler.insertChatMessage(chatMessage);
                        editTextNewMessage.getText().clear();
                        refreshChats();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error sending message", e);
                    }
                });
    }

    public void fetchProfileChats() {
        db.collection("siba_profiles").document(sibaProfileId)
                .collection("messages")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            Activity activity = getActivity();
                            if (activity != null) {
                                DbHandler dbHandler = new DbHandler(activity);
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Map<String, Object> data = document.getData();
                                    ChatMessage chatMessage = new ChatMessage(data.get("messageId").toString(), UUID.fromString(data.get("siba_profile_id").toString()), UUID.fromString(data.get("sender_profile_id").toString()), data.get("message").toString(), Long.parseLong(data.get("createdAt").toString()));
                                    dbHandler.insertChatMessage(chatMessage);
                                }
                                refreshChats();
                            }
                        } else {
                            Log.d(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
    }

    public void listenForChats() {
        db.collection("siba_profiles").document(sibaProfileId)
                .collection("messages")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            Log.w(TAG, "Listen for Chats Failed.", error);
                            return;
                        }
                        Activity activity = getActivity();
                        if (activity != null) {
                            DbHandler dbHandler = new DbHandler(activity);
                            for (DocumentSnapshot document : value.getDocuments()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                Map<String, Object> data = document.getData();
                                ChatMessage chatMessage = new ChatMessage(data.get("messageId").toString(), UUID.fromString(data.get("siba_profile_id").toString()), UUID.fromString(data.get("sender_profile_id").toString()), data.get("message").toString(), Long.parseLong(data.get("createdAt").toString()));
                                dbHandler.insertChatMessage(chatMessage);
                            }
                            refreshChats();
                        }
                    }
                });
    }

    public void refreshChats() {
        detailedChatMessages.clear();
        detailedChatMessages.addAll(fetchDetailedMessages());
        sibaMessagesRecyclerAdapter.notifyDataSetChanged();
        recyclerViewProfiles.scrollToPosition(sibaMessagesRecyclerAdapter.getItemCount() - 1);
    }

    public void fetchSibaProfile() {
        sibaProfile = Common.getSibaProfileById(getContext(), sibaProfileId);
        if (sibaProfile == null) {
            getActivity().onBackPressed();
        }
    }

    public void populateSibaProfileMembersMap() {
        sibaProfileMembers = new HashMap<>();
        for (SibaProfileMember sibaProfileMember : sibaProfile.getMembers()) {
            sibaProfileMembers.put(sibaProfileMember.getMemberProfileId(), sibaProfileMember);
        }
    }

    public List<DetailedChatMessage> fetchDetailedMessages() {
        DbHandler dbHandler = new DbHandler(getContext());
        List<ChatMessage> chatMessages = dbHandler.getChatMessages();
        List<DetailedChatMessage> detailedChatMessages = new LinkedList<>();
        for (ChatMessage chatMessage : chatMessages) {
            SibaProfileMember sibaProfileMember = sibaProfileMembers.get(chatMessage.getSender_profile_id());
            Sender sender = new Sender(sibaProfileMember.getMemberProfileId(), sibaProfileMember.getId(), sibaProfileMember.getFirstName());
            DetailedChatMessage detailedChatMessage = new DetailedChatMessage(chatMessage.getMessageId(), sender, chatMessage.getMessage(), chatMessage.getCreatedAt());
            detailedChatMessages.add(detailedChatMessage);
        }
        return detailedChatMessages;
    }

    public DetailedChatMessage fetchDetailedMessage(ChatMessage chatMessage) {
        SibaProfileMember sibaProfileMember = sibaProfileMembers.get(chatMessage.getSender_profile_id());
        Sender sender = new Sender(sibaProfileMember.getMemberProfileId(), sibaProfileMember.getId(), sibaProfileMember.getFirstName());
        return new DetailedChatMessage(chatMessage.getMessageId(), sender, chatMessage.getMessage(), chatMessage.getCreatedAt());
    }
}