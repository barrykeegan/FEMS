package org.leoapps.fems;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class CaseListAdapter extends RecyclerView.Adapter<CaseListAdapter.ViewHolder> {
    private List<Case> caseList;

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView caseListReference;
        public TextView caseListOperation;
        public TextView caseListType;
        public TextView caseListDate;
        public TextView caseListLocation;
        public View layout;

        public ViewHolder(View v)
        {
            super(v);
            layout = v;

            caseListReference = v.findViewById(R.id.tv_case_list_reference);
            caseListOperation = v.findViewById(R.id.tv_case_list_operation);
            caseListType = v.findViewById(R.id.tv_case_list_type);
            caseListDate = v.findViewById(R.id.tv_case_list_date);
            caseListLocation = v.findViewById(R.id.tv_case_list_location);
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
