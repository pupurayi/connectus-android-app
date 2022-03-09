package com.connectus.mobile.ui.old.siba.profile;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.connectus.mobile.R;
import com.connectus.mobile.api.dto.siba.SibaProfileMember;
import com.hbb20.CountryCodePicker;

import java.util.List;
import java.util.UUID;

public class SibaProfileMembersRecyclerAdapter extends RecyclerView.Adapter<SibaProfileMembersRecyclerAdapter.ViewHolder> {

    private Context context;
    private final List<SibaProfileMember> profileMembers;
    private UUID adminProfileId;
    private FragmentManager fragmentManager;

    public SibaProfileMembersRecyclerAdapter(Context context, List<SibaProfileMember> profileMembers, UUID adminProfileId, FragmentManager fragmentManager) {
        this.context = context;
        this.profileMembers = profileMembers;
        this.adminProfileId = adminProfileId;
        this.fragmentManager = fragmentManager;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_siba_invites_and_members, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SibaProfileMember sibaProfileMember = profileMembers.get(position);
        String fullName = sibaProfileMember.getFirstName() + " " + sibaProfileMember.getLastName();
        holder.textViewFullName.setText(fullName);
        holder.textViewMsisdn.setText(sibaProfileMember.getMsisdn());
        holder.ccpCountryCode.setFullNumber(sibaProfileMember.getMsisdn());
        holder.position = position;
    }

    @Override
    public int getItemCount() {
        return profileMembers.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView textViewFullName, textViewMsisdn;
        private CountryCodePicker ccpCountryCode;
        private int position = 0;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewFullName = (TextView) itemView.findViewById(R.id.text_view_full_name);
            textViewMsisdn = (TextView) itemView.findViewById(R.id.text_view_msisdn);
            ccpCountryCode = (CountryCodePicker) itemView.findViewById(R.id.ccp_country_code);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final CharSequence[] options;
                    if (adminProfileId != null) {
                        options = new CharSequence[]{"Remove Member", "Member Contributions"};
                    } else {
                        options = new CharSequence[]{"Member Contributions"};
                    }

                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Choose Action");
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.setItems(options, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int item) {
                            SibaProfileMembersFragment sibaProfileMembersFragment = (SibaProfileMembersFragment) fragmentManager.findFragmentByTag(SibaProfileMembersFragment.class.getSimpleName());
                            if (options[item].equals("Remove Member")) {
//                                sibaProfileMembersFragment.joinProfile(position);
                            } else if (options[item].equals("Member Contributions")) {
//                                sibaProfileMembersFragment.declineInvite(position);
                            }
                        }
                    });
                    builder.show();
                }
            });
        }
    }
}


