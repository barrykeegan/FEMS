package org.leoapps.fems;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class NoContentAdapter extends RecyclerView.Adapter<NoContentAdapter.ViewHolder> {
    private List<NoContent> noContentList;

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView no_content_title;
        public TextView no_content_message;
        public View layout;

        public ViewHolder(View v)
        {
            super(v);
            layout = v;
            no_content_title = v.findViewById(R.id.tv_no_content_title);
            no_content_message = v.findViewById(R.id.tv_no_content_message);
        }
    }

    public NoContentAdapter(List<NoContent> dataset) {noContentList = dataset;}

    @NonNull
    @Override
    public NoContentAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View moduleView = inflater.inflate(R.layout.no_content, parent, false);
        ViewHolder noContent = new ViewHolder(moduleView);
        return noContent;
    }

    @Override
    public void onBindViewHolder(@NonNull NoContentAdapter.ViewHolder holder, int position) {
        final NoContent noContent = noContentList.get(position);
        holder.no_content_title.setText(noContent.title);
        holder.no_content_message.setText(noContent.message);
    }

    @Override
    public int getItemCount() {
        return noContentList.size();
    }
}
