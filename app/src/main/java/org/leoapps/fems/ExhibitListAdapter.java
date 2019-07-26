package org.leoapps.fems;

import android.content.DialogInterface;
import android.content.Intent;
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

public class ExhibitListAdapter extends RecyclerView.Adapter<ExhibitListAdapter.ViewHolder> {
    private List<Exhibit> exhibitList;

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public View layout;

        public LinearLayout llExhibitDetails;

        public TextView tvCaseListID;
        public TextView tvExhibitListID;
        public TextView tvExhibitLocalID;
        public TextView tvExhibitExternalID;
        public TextView tvExhibitDescription;

        public ImageView ivExhibitThumbnail;
        public ImageView ivShareExhibit;
        public ImageView ivEditExhibit;
        public ImageView ivDeleteExhibit;

        public ViewHolder(View v)
        {
            super(v);
            layout = v;

            llExhibitDetails = v.findViewById(R.id.ll_exhibit_list_details);

            tvCaseListID = v.findViewById(R.id.tv_exhibit_list_case_id);
            tvExhibitListID = v.findViewById(R.id.tv_exhibit_list_id);
            tvExhibitLocalID = v.findViewById(R.id.tv_exhibit_list_local_id);
            tvExhibitExternalID = v.findViewById(R.id.tv_exhibit_list_external_id);
            tvExhibitDescription = v.findViewById(R.id.tv_exhibit_list_description);

            ivExhibitThumbnail = v.findViewById(R.id.iv_exhibit_list_thumbnail);
            ivShareExhibit = v.findViewById(R.id.iv_exhibit_list_share_exhibit);
            ivEditExhibit = v.findViewById(R.id.iv_exhibit_list_edit_exhibit);
            ivDeleteExhibit = v.findViewById(R.id.iv_exhibit_list_delete_exhibit);

            llExhibitDetails.setOnClickListener(this);
            ivShareExhibit.setOnClickListener(this);
            ivEditExhibit.setOnClickListener(this);
            ivDeleteExhibit.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            String whichExhibit = "Exhibit " + tvExhibitListID.getText().toString();
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
            }
        }

        private void verifyExhibitDelete(View v)
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
        }
    }

    public ExhibitListAdapter(List<Exhibit> dataset) {exhibitList = dataset;}

    @NonNull
    @Override
    public ExhibitListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View exhibitListView = inflater.inflate(R.layout.exhibit_list_content, parent, false);
        ExhibitListAdapter.ViewHolder exhibitListHolder = new ExhibitListAdapter.ViewHolder(exhibitListView);
        return exhibitListHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ExhibitListAdapter.ViewHolder holder, int position) {
        final Exhibit exhibit = exhibitList.get(position);
        holder.tvCaseListID.setText(Integer.toString(exhibit.CaseID));
        holder.tvExhibitListID.setText(Integer.toString(exhibit.ID));

        holder.tvExhibitLocalID.setText(exhibit.LocalExhibitID);
        holder.tvExhibitExternalID.setText(exhibit.ExternalExhibitID);
        holder.tvExhibitDescription.setText(exhibit.Description);

        /*if(aCase.ReferenceID == null || aCase.ReferenceID.isEmpty())
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
        }*/
    }

    @Override
    public int getItemCount() {
        return exhibitList.size();
    }
}
