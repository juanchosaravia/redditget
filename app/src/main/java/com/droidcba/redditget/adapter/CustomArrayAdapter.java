package com.droidcba.redditget.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.droidcba.redditget.R;
import com.droidcba.redditget.rest.pojo.Child;
import com.droidcba.redditget.utils.Utilities;
import com.koushikdutta.ion.Ion;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;

import rx.Observable;
import rx.Subscriber;

/**
 * Created by juancho on 11/21/14.
 */
public class CustomArrayAdapter extends ArrayAdapter<Child> {

    private static final String TAG = "CustomArrayAdapter";
    private static HashMap<Integer, Bitmap> imageMap = new HashMap<Integer, Bitmap>();
    private Context context;
    private Drawable drawable;

    public CustomArrayAdapter(Context context, int textViewResourceId, List<Child> objects) {
        super(context, textViewResourceId, objects);
        this.context = context;
        drawable = context.getResources().getDrawable(R.drawable.world_icon);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        final Child rowItem = getItem(position);

        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_row, null);
            holder = new ViewHolder();
            holder.txtDescription = (TextView) convertView.findViewById(R.id.description);
            holder.txtAuthor = (TextView) convertView.findViewById(R.id.author);
            holder.txtComments = (TextView) convertView.findViewById(R.id.comments);
            holder.ivThumbnail = (ImageView) convertView.findViewById(R.id.list_image);
            holder.txtTime = (TextView) convertView.findViewById(R.id.time);
            convertView.setTag(holder);
        } else
            holder = (ViewHolder) convertView.getTag();

        holder.txtAuthor.setText(
                getContext().getResources().getString(R.string.adapter_writted_by)
                        + " "
                        + rowItem.getData().getAuthor());
        holder.txtDescription.setText(rowItem.getData().getTitle());
        holder.txtComments.setText(String.valueOf(
                rowItem.getData().getNumComments())
                + " "
                + getContext().getResources().getString(R.string.adapter_comments));
        holder.txtTime.setText(String.valueOf(
                Utilities.getFriendlyTime(
                        rowItem.getData().getCreatedUtc())));

        String picUrl = rowItem.getData().getThumbnail();
        if (!Utilities.isValidUrl(picUrl)) {
            holder.ivThumbnail.setImageDrawable(drawable);
        } else {
            Ion.with(holder.ivThumbnail).load(picUrl);
        }
        /*
        new ThumbnailTask(position, holder).executeOnExecutor(
                AsyncTask.THREAD_POOL_EXECUTOR,
                rowItem.getData().getThumbnail());*/

        /*
        setImageObservable(rowItem.getData().getThumbnail(), position, holder.ivThumbnail)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .cache()
                .subscribe();*/

        return convertView;
    }

    public Observable<Bitmap> setImageObservable(final String url, final int position,
                                                 final ImageView imageView) {
        return rx.Observable.create(new rx.Observable.OnSubscribe<Bitmap>() {
            @Override
            public void call(Subscriber<? super Bitmap> subscriber) {
                try {
                    if (imageMap.containsKey(position)) {
                        Log.d(TAG, "IMG IN MAP: " + url);
                        imageView.setImageBitmap(imageMap.get(position));
                    } else {
                        if (Utilities.isValidUrl(url)) {
                            InputStream in = new java.net.URL(url).openStream();
                            Bitmap bitmap = BitmapFactory.decodeStream(in);
                            imageMap.put(position, bitmap);
                            imageView.setImageBitmap(bitmap);
                            Log.d(TAG, "IMG DOWNLOADED: " + url);
                        } else {
                            imageView.setImageResource(0);
                            Log.d(TAG, "IMG INVALID URL: " + url);
                        }
                    }
                    subscriber.onCompleted();
                } catch (Exception e) {
                    Log.d(TAG, "There was an error: " + e.getMessage());
                }
            }
        });
    }

    private static class ThumbnailTask extends AsyncTask<String, Void, Bitmap> {
        private int mPosition;
        private ViewHolder mHolder;

        public ThumbnailTask(int position, ViewHolder holder) {
            mPosition = position;
            mHolder = holder;
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            Bitmap bitmap = null;
            String url = params[0];
            try {
                InputStream in = new java.net.URL(url).openStream();
                bitmap = BitmapFactory.decodeStream(in);
            } catch (IOException e) {
                Log.d(TAG, "Invalid URL: " + url);
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (bitmap != null && mHolder.position == mPosition) {
                mHolder.ivThumbnail.setImageBitmap(bitmap);
            }
        }
    }

    /*private view holder class*/
    private class ViewHolder {
        ImageView ivThumbnail;
        TextView txtDescription;
        TextView txtAuthor;
        TextView txtComments;
        TextView txtTime;
        int position;
    }
}