package com.connectus.mobile.ui.siba.onboarding;

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
import com.connectus.mobile.ui.siba.EligibilityResponse;
import com.hbb20.CountryCodePicker;

import java.util.List;

public class SibaOnBoardingInvitesRecyclerAdapter extends RecyclerView.Adapter<SibaOnBoardingInvitesRecyclerAdapter.ViewHolder> {

    private Context context;
    private final List<EligibilityResponse> invites;
    private FragmentManager fragmentManager;

    public SibaOnBoardingInvitesRecyclerAdapter(Context context, List<EligibilityResponse> invites, FragmentManager fragmentManager) {
        this.context = context;
        this.invites = invites;
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
        EligibilityResponse eligibilityResponse = invites.get(position);
        String fullName = eligibilityResponse.getFirstName() + " " + eligibilityResponse.getLastName();
        holder.textViewFullName.setText(fullName);
        String username = eligibilityResponse.getUsername();
        holder.textViewUsername.setText(username);
        holder.ccpCountryCode.setFullNumber(username);
        holder.position = position;
    }

    @Override
    public int getItemCount() {
        return invites.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView textViewFullName, textViewUsername;
        private CountryCodePicker ccpCountryCode;
        private int position;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewFullName = (TextView) itemView.findViewById(R.id.text_view_full_name);
            textViewUsername = (TextView) itemView.findViewById(R.id.text_view_username);
            ccpCountryCode = (CountryCodePicker) itemView.findViewById(R.id.ccp_country_code);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("eMalyami Alert")
                            .setMessage("Are you sure you want to delete invite!")
                            .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    CreateSibaProfileFragment createSibaProfileFragment = (CreateSibaProfileFragment) fragmentManager.findFragmentByTag(CreateSibaProfileFragment.class.getSimpleName());
                                    createSibaProfileFragment.remoteInviteToView(position);
                                }
                            })
                            .show();
                }
            });
        }
    }
}

