package org.leoapps.fems;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.logging.LogRecord;

public class PhotographListAdapter extends RecyclerView.Adapter<PhotographListAdapter.ViewHolder> {
    private List<Photograph> photoList;

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public View layout;
        public LinearLayout llPhoto;
        public ImageView ivExhibitPhoto;
        public TextView tvPhotoID;

        public Photograph p;

        public ViewHolder(View v)
        {
            super(v);
            layout = v;

            llPhoto = v.findViewById(R.id.ll_photograph_grid_content);

            ivExhibitPhoto = v.findViewById(R.id.iv_exhibit_photo);

            tvPhotoID = v.findViewById(R.id.tv_photograph_id);

            layout.setOnClickListener(this);
            /*llExhibitDetails.setOnClickListener(this);
            ivShareExhibit.setOnClickListener(this);
            ivEditExhibit.setOnClickListener(this);
            ivDeleteExhibit.setOnClickListener(this);*/
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(v.getContext(), PhotoDetails.class);
            intent.putExtra("ID", p.ID);
            intent.putExtra("Timestamp", p.DateTimeTaken);
            intent.putExtra("Location", p.FileLocation);
            v.getContext().startActivity(intent);
            /*String whichExhibit = "Exhibit " + tvExhibitListID.getText().toString();
            if( v == llExhibitDetails)
            {
                Toast.makeText(v.getContext(), "Details for... " + whichExhibit, Toast.LENGTH_LONG).show();
                Intent toExhibitDetails = new Intent(v.getContext(), ExhibitDetails.class);
                toExhibitDetails.putExtra("ExhibitID", tvExhibitListID.getText().toString());
                v.getContext().startActivity(toExhibitDetails);
            }
            if (v == ivDeleteExhibit)
            {
                verifyExhibitDelete(v);
            }

            if(v == ivEditExhibit)
            {
                Intent toUpdateExhibit = new Intent(v.getContext(), UpdateExhibit.class);
                toUpdateExhibit.putExtra("From", "CaseDetails");
                toUpdateExhibit.putExtra("ExhibitID", tvExhibitListID.getText().toString());
                v.getContext().startActivity(toUpdateExhibit);
            }
            if(v == ivShareExhibit)
            {
                Toast.makeText(v.getContext(), "Sharing... " + whichExhibit, Toast.LENGTH_LONG).show();
            }*/
        }

        /*private void verifyExhibitDelete(View v)
        {
            //https://stackoverflow.com/questions/5127407/how-to-implement-a-confirmation-yes-no-dialogpreference
            new AlertDialog.Builder(v.getContext())
                    .setTitle("Confirm Delete")
                    .setMessage("Are you sure you want to delete this exhibit? If you press yes this exhibit and all associated photos will also be removed.")
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int whichButton) {
                            Utils.database.exhibitDAO().deleteExhibit(
                                    Integer.parseInt(tvExhibitListID.getText().toString())
                            );
                            //https://stackoverflow.com/questions/37339465/recyclerview-does-not-update-after-deleting-an-item-from-sqlite
                            exhibitList.remove(getAdapterPosition());
                            notifyItemRemoved(getAdapterPosition());
                        }})
                    .setNegativeButton(android.R.string.no, null).show();
        }*/
    }

    public PhotographListAdapter(List<Photograph> dataset) {photoList = dataset;}

    @NonNull
    @Override
    public PhotographListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View photoGridView = inflater.inflate(R.layout.photo_grid_content, parent, false);
        PhotographListAdapter.ViewHolder photoGridHolder = new PhotographListAdapter.ViewHolder(photoGridView);
        return photoGridHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final PhotographListAdapter.ViewHolder holder, final int position) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable() {
                    @Override
                    public void run() {

                        final Photograph photo = photoList.get(position);
                        holder.p = photo;
                        holder.tvPhotoID.setText(Integer.toString(photo.ID));
                        holder.ivExhibitPhoto.setImageBitmap(ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(photo.FileLocation), 90, 90));
                    }
                });
            }
        }).start();

    }

    @Override
    public int getItemCount() {
        return photoList.size();
    }
}
