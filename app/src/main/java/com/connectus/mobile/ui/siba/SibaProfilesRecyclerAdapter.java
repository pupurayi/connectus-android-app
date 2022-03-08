package com.connectus.mobile.ui.siba;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.connectus.mobile.R;
import com.connectus.mobile.api.dto.siba.SibaProfile;

import java.util.List;

public class SibaProfilesRecyclerAdapter extends RecyclerView.Adapter<SibaProfilesRecyclerAdapter.ViewHolder> {

    private Context context;
    private final List<SibaProfile> sibaProfiles;
    private FragmentManager fragmentManager;

    public SibaProfilesRecyclerAdapter(Context context, List<SibaProfile> sibaProfiles, FragmentManager fragmentManager) {
        this.context = context;
        this.sibaProfiles = sibaProfiles;
        this.fragmentManager = fragmentManager;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_siba_profile_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SibaProfile sibaProfile = sibaProfiles.get(position);
        holder.textViewProfileSubject.setText(sibaProfile.getSubject());

        String summary = String.format("%s members with %s %.2f balance", sibaProfile.getMembers().size(), sibaProfile.getCurrency(), sibaProfile.getBalance());
        holder.textViewProfileSummary.setText(summary);
        holder.position = position;
    }

    @Override
    public int getItemCount() {
        return sibaProfiles.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView textViewProfileSubject, textViewProfileSummary;
        private int position = 0;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewProfileSubject = (TextView) itemView.findViewById(R.id.text_view_profile_subject);
            textViewProfileSummary = (TextView) itemView.findViewById(R.id.text_view_profile_summary);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SibaProfilesFragment sibaProfilesFragment = (SibaProfilesFragment) fragmentManager.findFragmentByTag(SibaProfilesFragment.class.getSimpleName());
                    sibaProfilesFragment.openProfileFragment(position);
                }
            });
        }
    }
}


