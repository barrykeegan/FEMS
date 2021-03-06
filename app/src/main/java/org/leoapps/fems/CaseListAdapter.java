package org.leoapps.fems;

import android.content.DialogInterface;
import android.content.Intent;
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

public class CaseListAdapter extends RecyclerView.Adapter<CaseListAdapter.ViewHolder> {
    private List<Case> caseList;

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView tvCaseListID;
        public TextView caseListReference;
        public TextView caseListOperation;
        public TextView caseListType;
        public TextView caseListDate;
        public TextView caseListLocation;
        public View layout;
        public ImageView imgShareCase;
        public ImageView imgEditCase;
        public ImageView imgDeleteCase;
        public LinearLayout llCaseDetails;

        public ViewHolder(View v)
        {
            super(v);
            layout = v;

            tvCaseListID = v.findViewById(R.id.tv_case_list_case_id);
            caseListReference = v.findViewById(R.id.tv_case_list_reference);
            caseListOperation = v.findViewById(R.id.tv_case_list_operation);
            caseListType = v.findViewById(R.id.tv_case_list_type);
            caseListDate = v.findViewById(R.id.tv_case_list_date);
            caseListLocation = v.findViewById(R.id.tv_case_list_location);

            llCaseDetails = v.findViewById(R.id.ll_case_list_detail);
            imgShareCase = v.findViewById(R.id.img_case_list_share_case);
            imgEditCase = v.findViewById(R.id.img_case_list_edit_case);
            imgDeleteCase = v.findViewById(R.id.img_case_list_delete_case);

            llCaseDetails.setOnClickListener(this);
            imgShareCase.setOnClickListener(this);
            imgEditCase.setOnClickListener(this);
            imgDeleteCase.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if( v == llCaseDetails)
            {
                Intent toCaseDetails = new Intent(v.getContext(), CaseDetails.class);
                toCaseDetails.putExtra("CaseID", tvCaseListID.getText().toString());
                v.getContext().startActivity(toCaseDetails);
            }
            if (v == imgDeleteCase)
            {
                verifyCaseDelete(v);
            }
            if(v == imgEditCase)
            {
                Intent toUpdateCase = new Intent(v.getContext(), UpdateCase.class);
                toUpdateCase.putExtra("CaseID", tvCaseListID.getText().toString());
                toUpdateCase.putExtra("From", "CaseList");
                v.getContext().startActivity(toUpdateCase);
            }
            if(v == imgShareCase)
            {
                shareAllCasePhotos();
                Toast.makeText(v.getContext(), "Downloaded all photos for this case to 'FEMS' directory in external storage of device.", Toast.LENGTH_LONG).show();
            }
        }

        private void verifyCaseDelete(View v)
        {
            //https://stackoverflow.com/questions/5127407/how-to-implement-a-confirmation-yes-no-dialogpreference
            new AlertDialog.Builder(v.getContext())
                    .setTitle("Confirm Delete")
                    .setMessage("Are you sure you want to delete this case? If you press yes all exhibits and photos will also be removed.")
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int whichButton) {
                            Utils.database.caseDAO().getCase(
                                    Integer.parseInt( tvCaseListID.getText().toString())
                                ).deleteCase();
                            //https://stackoverflow.com/questions/37339465/recyclerview-does-not-update-after-deleting-an-item-from-sqlite
                            caseList.remove(getAdapterPosition());
                            notifyItemRemoved(getAdapterPosition());
                        }})
                    .setNegativeButton(android.R.string.no, null).show();
        }

        private void shareAllCasePhotos()
        {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            List<Exhibit> exhibits= Utils.database.exhibitDAO().getExhibitsForCase(
                                    Integer.parseInt(tvCaseListID.getText().toString())
                            );

                            for (Exhibit exhibit: exhibits)
                            {
                                List<Photograph> photos = Utils.database.photographDAO().getPhotographsForExhibit(exhibit.ID);
                                if (photos.size() > 0)
                                {
                                    for (Photograph photo: photos)
                                    {
                                        Utils.copyPhotoToExternal(photo.FileLocation);
                                    }
                                }
                            }
                        }
                    });
                }
            }).start();

        }
    }

    public CaseListAdapter(List<Case> dataset) {caseList = dataset;}

    @NonNull
    @Override
    public CaseListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View caseListView = inflater.inflate(R.layout.case_list_content, parent, false);
        CaseListAdapter.ViewHolder caseListHolder = new CaseListAdapter.ViewHolder(caseListView);
        return caseListHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CaseListAdapter.ViewHolder holder, int position) {
        final Case aCase = caseList.get(position);
        holder.tvCaseListID.setText(Integer.toString(aCase.ID));

        if(aCase.ReferenceID == null || aCase.ReferenceID.isEmpty())
        {
            holder.caseListReference.setText(R.string.value_not_set);
        }
        else
        {
            holder.caseListReference.setText(aCase.ReferenceID);
        }

        if(aCase.OperationName == null || aCase.OperationName.isEmpty())
        {
            holder.caseListOperation.setText(R.string.value_not_set);
        }
        else
        {
            holder.caseListOperation.setText(aCase.OperationName);
        }

        if(aCase.CaseType == null || aCase.CaseType.isEmpty())
        {
            holder.caseListType.setText(R.string.value_not_set);
        }
        else
        {
            holder.caseListType.setText(aCase.CaseType);
        }

        if(aCase.CaseDate == null || aCase.CaseDate.isEmpty())
        {
            holder.caseListDate.setText(R.string.value_not_set);
        }
        else
        {
            holder.caseListDate.setText(aCase.CaseDate);
        }

        if(aCase.Location == null || aCase.Location.isEmpty())
        {
            holder.caseListLocation.setText(R.string.value_not_set);
        }
        else
        {
            holder.caseListLocation.setText(aCase.Location);
        }
    }

    @Override
    public int getItemCount() {
        return caseList.size();
    }
}
