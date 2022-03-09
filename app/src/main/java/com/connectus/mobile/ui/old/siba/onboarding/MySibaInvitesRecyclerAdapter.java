package com.connectus.mobile.ui.old.siba.onboarding;

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
import com.connectus.mobile.api.dto.siba.MySibaInvite;

import java.util.List;

public class MySibaInvitesRecyclerAdapter extends RecyclerView.Adapter<MySibaInvitesRecyclerAdapter.ViewHolder> {

    private Context context;
    private final List<MySibaInvite> invites;
    private FragmentManager fragmentManager;

    public MySibaInvitesRecyclerAdapter(Context context, List<MySibaInvite> invites, FragmentManager fragmentManager) {
        this.context = context;
        this.invites = invites;
        this.fragmentManager = fragmentManager;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_my_siba_invites_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MySibaInvite mySibaInvite = invites.get(position);
        String profileSubject = mySibaInvite.getSubject();
        holder.textViewProfileSubject.setText(profileSubject);
        String createdBy = "by " + mySibaInvite.getCreatedBy();
        holder.textViewCreatedBy.setText(createdBy);
        holder.position = position;
    }

    @Override
    public int getItemCount() {
        return invites.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView textViewProfileSubject, textViewCreatedBy;
        private int position = 0;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewProfileSubject = (TextView) itemView.findViewById(R.id.text_view_siba_profile_subject);
            textViewCreatedBy = (TextView) itemView.findViewById(R.id.text_view_created_by);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final CharSequence[] options = {"Join Profile", "Decline Invite"};
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
                            MySibaInvitesFragment mySibaInvitesFragment = (MySibaInvitesFragment) fragmentManager.findFragmentByTag(MySibaInvitesFragment.class.getSimpleName());
                            if (options[item].equals("Join Profile")) {
                                mySibaInvitesFragment.joinProfile(position);
                            } else if (options[item].equals("Decline Invite")) {
                                mySibaInvitesFragment.declineInvite(position);
                            }
                        }
                    });
                    builder.show();
                }
            });
        }
    }
}

