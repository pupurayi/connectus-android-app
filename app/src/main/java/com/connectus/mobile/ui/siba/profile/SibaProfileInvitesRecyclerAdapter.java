package com.connectus.mobile.ui.siba.profile;

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
import com.connectus.mobile.api.dto.siba.SibaInvite;
import com.hbb20.CountryCodePicker;

import java.util.List;

public class SibaProfileInvitesRecyclerAdapter extends RecyclerView.Adapter<SibaProfileInvitesRecyclerAdapter.ViewHolder> {

    private Context context;
    private final List<SibaInvite> sibaInvites;
    private FragmentManager fragmentManager;

    public SibaProfileInvitesRecyclerAdapter(Context context, List<SibaInvite> sibaInvites, FragmentManager fragmentManager) {
        this.context = context;
        this.sibaInvites = sibaInvites;
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
        SibaInvite sibaInvite = sibaInvites.get(position);
        String fullName = sibaInvite.getFirstName() + " " + sibaInvite.getLastName();
        holder.textViewFullName.setText(fullName);
        holder.textViewMsisdn.setText(sibaInvite.getMsisdn());
        holder.ccpCountryCode.setFullNumber(sibaInvite.getMsisdn());
        holder.position = position;
    }

    @Override
    public int getItemCount() {
        return sibaInvites.size();
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
                    final CharSequence[] options = new CharSequence[]{"Delete Invite"};
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
                            SibaProfileInvitesFragment sibaProfileInvitesFragment = (SibaProfileInvitesFragment) fragmentManager.findFragmentByTag(SibaProfileInvitesFragment.class.getSimpleName());
                            if (options[item].equals("Delete Invite")) {
//                                sibaProfileInvitesFragment.declineInvite(position);
                            }
                        }
                    });
                    builder.show();
                }
            });
        }
    }
}


