package pnpmsjm.com.ourmasjid;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class MembersAdapter extends RecyclerView.Adapter<MembersAdapter.ViewHolder> {

    private Context context;
    private List<Members> membersList;

    public MembersAdapter(Context context, List<Members> memberList) {
        this.context = context;
        this.membersList = memberList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_members, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Members member = membersList.get(position);

        // Name
        holder.tvName.setText(member.getName() != null ? member.getName() : "No Name");

        // Mobile
        holder.tvMobile.setText(member.getMobile_No() != null ? member.getMobile_No() : "No Number");

        // Designation (with visibility control)
        if (member.getDesignation() != null && !member.getDesignation().isEmpty()) {
            holder.tvDesignation.setText(member.getDesignation());
            holder.tvDesignation.setVisibility(View.VISIBLE);
        } else {
            holder.tvDesignation.setVisibility(View.GONE);
        }

        // Photo (with fallback image)
        if (member.getPurl() != null && !member.getPurl().isEmpty()) {
            Picasso.get()
                    .load(member.getPurl())
                    .placeholder(R.drawable.default_img)
                    .error(R.drawable.error_img)
                    .into(holder.imgPhoto);
            holder.imgPhoto.setVisibility(View.VISIBLE);
        } else {
            holder.imgPhoto.setVisibility(View.GONE);
        }

        // Toggle designation on name click
        holder.tvName.setOnClickListener(v -> {
            if (holder.tvDesignation.getVisibility() == View.GONE) {
                holder.tvDesignation.setVisibility(View.VISIBLE);
            } else {
                holder.tvDesignation.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public int getItemCount() {
        return membersList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvDesignation, tvMobile;
        ImageView imgPhoto;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvDesignation = itemView.findViewById(R.id.tvDesignation);
            tvMobile = itemView.findViewById(R.id.tvMobile);
            imgPhoto = itemView.findViewById(R.id.imgPhoto);
        }
    }
}
