package amal.com.maddiscovery.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.List;

import amal.com.maddiscovery.R;
import amal.com.maddiscovery.model.MadEvent;
import amal.com.maddiscovery.utill.EventUtil;

/**
 * Created by Shazeen-PC on 3/5/2016.
 */
public class EventListAdapter extends RecyclerView.Adapter<EventListAdapter.EventViewHolder> {

    private List<MadEvent> eventList;
    private Context listContext;

    public EventListAdapter(Context context, List<MadEvent> eventList) {
        this.eventList = eventList;
        this.listContext = context;
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    @Override
    public void onBindViewHolder(EventViewHolder eventViewHolder, int i) {
        MadEvent ci = eventList.get(i);
        eventViewHolder.eId.setText(String.valueOf(ci.getId()));
        eventViewHolder.eName.setText(ci.getEventName());
        eventViewHolder.eDateTime.setText(ci.getEventDate() + " @ " + ci.getEventTime());
        eventViewHolder.eOrganizer.setText("By, "+ci.getEventOrganizer());
        eventViewHolder.eLocation.setText("In - "+ci.getEventLocation());
        setPic(ci.getEventPicturePath(), eventViewHolder.eImage);
    }

    @Override
    public EventViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.event_list_item, viewGroup, false);

        return new EventViewHolder(itemView);
    }



    public static class EventViewHolder extends RecyclerView.ViewHolder {
        protected TextView eId;
        protected TextView eName;
        protected TextView eDateTime;
        protected TextView eOrganizer;
        protected TextView eLocation;
        protected ImageView eImage;

        public EventViewHolder(View v) {
            super(v);
            eId = (TextView) v.findViewById(R.id.evtlstId);
            eName = (TextView) v.findViewById(R.id.evtLstTitle);
            eDateTime = (TextView) v.findViewById(R.id.evtLstDateTime);
            eOrganizer = (TextView) v.findViewById(R.id.evtlstorganizer);
            eLocation = (TextView) v.findViewById(R.id.evtlstLocation);
            eImage = (ImageView) v.findViewById(R.id.evtLstIv);
        }
    }



    private void setPic(String eventPicturePath, ImageView eImage) {

        if (!eventPicturePath.equals("NONE")) {
            File imgFile = new File(eventPicturePath);

            try {
                if (imgFile.exists()) {
                    Bitmap bm;
                    bm = Bitmap.createScaledBitmap(BitmapFactory.decodeFile(eventPicturePath), 100, 100, true);
                    eImage.setImageBitmap(bm);
                }
            } catch (OutOfMemoryError error) {
                new EventUtil().showToast(listContext,"Can't load image. Low mamorry");
            }

        }
    }

}
