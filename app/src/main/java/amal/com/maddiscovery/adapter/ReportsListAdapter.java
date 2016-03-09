package amal.com.maddiscovery.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;

import amal.com.maddiscovery.R;
import amal.com.maddiscovery.model.EventReport;
import amal.com.maddiscovery.utill.EventUtil;

/**
 * Created by Shazeen-PC on 3/5/2016.
 */
public class ReportsListAdapter extends RecyclerView.Adapter<ReportsListAdapter.EventViewHolder> {

    private List<EventReport> eventList;
    private Activity evetAct;

    public ReportsListAdapter(Activity activity, List<EventReport> eventList) {
        this.eventList = eventList;
        this.evetAct = activity;
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    @Override
    public void onBindViewHolder(EventViewHolder eventViewHolder, int i) {
        final EventReport ci = eventList.get(i);
        eventViewHolder.rName.setText(ci.getMessage());
        eventViewHolder.rDateTime.setText(ci.getReportDateTime());
        eventViewHolder.rShareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = EventUtil.selectedEventName+" - "+ci.getMessage();
                new EventUtil().shareNow(evetAct,msg, EventUtil.selectedPicPath);
            }
        });
    }

    @Override
    public EventViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.report_list_item, viewGroup, false);

        return new EventViewHolder(itemView);
    }

    public static class EventViewHolder extends RecyclerView.ViewHolder {
        protected TextView rName;
        protected TextView rDateTime;
        protected ImageButton rShareBtn;

        public EventViewHolder(View v) {
            super(v);
            rName = (TextView) v.findViewById(R.id.reportTv);
            rDateTime = (TextView) v.findViewById(R.id.reportDtTImTv);
            rShareBtn = (ImageButton) v.findViewById(R.id.shareBtn);
        }
    }


}
