package pnpmsjm.com.ourmasjid;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager; // Import for checking package
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button; // Keep if you use general Button somewhere else
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
// import com.squareup.picasso.Picasso; // No longer used, can remove if not used elsewhere

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

        // Designation
        holder.tvDesignation.setText(member.getDesignation() != null ? member.getDesignation() : "No Designation");

        // Load image using Glide
        if (member.getPurl() != null && !member.getPurl().isEmpty()) {
            Glide.with(holder.imgPhoto.getContext())
                    .load(member.getPurl())
                    .placeholder(R.drawable.default_img) // Make sure you have a default_img
                    .error(R.drawable.error_img)         // Make sure you have an error_img
                    .into(holder.imgPhoto);
        } else {
            holder.imgPhoto.setImageResource(R.drawable.default_img);
        }

        // Call button
        holder.btnCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = member.getMobile_No();
                if (phone != null && !phone.isEmpty()) {
                    // Check if the phone number starts with country code (e.g., +880)
                    // If not, you might want to add a default one or ensure it's provided by the user.
                    // For Bangladesh, usually +880.
                    // Example: if phone is "01712345678", convert to "+8801712345678" for international dialing
                    if (!phone.startsWith("+")) {
                        // Assuming Bangladesh numbers start with 0 and need +88
                        // You might need more robust country code handling depending on your user base
                        phone = "+88" + phone.substring(1); // Converts "017..." to "+88017..."
                    }
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:" + phone));
                    context.startActivity(intent);
                } else {
                    Toast.makeText(context, "ফোন নম্বর পাওয়া যায়নি", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // WhatsApp button
        holder.btnWhatsApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = member.getMobile_No();
                if (phone != null && !phone.isEmpty()) {
                    // WhatsApp requires number in international format without '+' or '00'
                    // For example, if number is "+8801712345678", WhatsApp link should be "8801712345678"
                    // If number is "01712345678", convert to "8801712345678"

                    String formattedPhone = phone.replace("+", ""); // Remove '+' if present
                    if (formattedPhone.startsWith("0") && formattedPhone.length() > 1) {
                        // Assuming local numbers start with '0', prepend country code
                        // Adjust "88" if your app supports other countries
                        formattedPhone = "88" + formattedPhone; // Converts "017..." to "88017..."
                    }


                    try {
                        String url = "https://api.whatsapp.com/send?phone=" + formattedPhone;
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(url));
                        context.startActivity(i);
                    } catch (Exception e) {
                        Toast.makeText(context, "হোয়াটসঅ্যাপ ইন্সটল করা নেই অথবা খুলতে সমস্যা হচ্ছে।", Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }

                    // Alternative: Using package name (more direct but can fail if app is not found)
                    // try {
                    //     String url = "whatsapp://send?phone=" + formattedPhone;
                    //     Intent intent = new Intent(Intent.ACTION_VIEW);
                    //     intent.setData(Uri.parse(url));
                    //     context.startActivity(intent);
                    // } catch (Exception e) {
                    //     Toast.makeText(context, "হোয়াটসঅ্যাপ ইন্সটল করা নেই অথবা খুলতে সমস্যা হচ্ছে।", Toast.LENGTH_LONG).show();
                    //     e.printStackTrace();
                    // }

                } else {
                    Toast.makeText(context, "হোয়াটসঅ্যাপ নম্বার পাওয়া যায়নি", Toast.LENGTH_SHORT).show();
                }
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
        ImageButton btnCall, btnWhatsApp; // Declaring btnWhatsApp

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvDesignation = itemView.findViewById(R.id.tvDesignation);
            tvMobile = itemView.findViewById(R.id.tvMobile);
            imgPhoto = itemView.findViewById(R.id.imgPhoto);
            btnCall = itemView.findViewById(R.id.btnCall);
            btnWhatsApp = itemView.findViewById(R.id.btnWhatsApp);
        }
    }
}