package com.example.realgamerhours;

import android.app.ListActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class EventActivity extends ListActivity {

    private ProgressBar progressBar;
    private static String TAG_TITLE ="title";
    private static String TAG_LINK="link";
    private static String TAG_DATE="date";
    private static String TAG_ADDRESS="address";
    private static String TAG_IMAGE="image";

    public static final String TAG = "EventActivity";
    List<HashMap<String , String>> eventList;
    ListView listView;
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        eventList = new ArrayList<>();
        imageView = findViewById(R.id.image);

        new getEventInfo().execute();
        listView = getListView();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                TextView eventLink = (TextView) view.findViewById(R.id.linkList);
                String eventLinkText = eventLink.getText().toString();

                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(eventLinkText));
                startActivity(i);
            }
        });
    }

    private class getEventInfo extends AsyncTask<Void, Void, Boolean>{

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            progressBar = new ProgressBar(EventActivity.this, null, android.R.attr.progressBarStyleLarge);

            RelativeLayout relativeLayout = findViewById(R.id.relativeLayout);
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT
            );
            lp.addRule(RelativeLayout.CENTER_IN_PARENT);
            progressBar.setLayoutParams(lp);
            progressBar.setVisibility(View.VISIBLE);
            relativeLayout.addView(progressBar);
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                String url = "https://dublin.ie/whats-on/";
                Document page = Jsoup.connect(url).userAgent("Jsoup Scraper").get();

                String eventName = ".cards article.event .text header";
                String eventAddress = ".cards article.event .text .location";
                String eventDate = ".cards article.event .text time";
                String eventLink = ".cards article.event .text a";
                String eventImage = ".cards article.event .img";

                Elements eventNameElements = page.select(eventName);
                Elements eventAddressElements = page.select(eventAddress);
                Elements eventDateElements = page.select(eventDate);
                Elements eventLinkElements = page.select(eventLink);
                Elements eventImageElements = page.select(eventImage);

                for(int i=0; i < eventAddressElements.size(); i++){
                    HashMap<String , String> map = new HashMap<String, String>();

                    Element e = eventNameElements.get(i);
                    Element e2 = eventAddressElements.get(i);
                    Element e3 = eventDateElements.get(i);
                    Element e4 = eventLinkElements.get(i);
                    Element e5 = eventImageElements.get(i);

                    String[] imgS = e5.attributes().get("style").split("'");

                    map.put(TAG_TITLE, e.text());
                    map.put(TAG_ADDRESS, e2.text());
                    map.put(TAG_DATE, e3.text());
                    map.put(TAG_LINK, e4.attributes().get("href"));
                    map.put(TAG_IMAGE, imgS[1]);
                    eventList.add(map);
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
//                        Uri myURI = Uri.parse(TAG_IMAGE);
//                        Bitmap bmp = null;
//                        try {
//                            bmp = BitmapFactory.decodeStream(getContentResolver().openInputStream(myURI));
//                        } catch (FileNotFoundException e) {
//                            e.printStackTrace();
//                        }
//                        imageView.setImageBitmap(bmp);
                        ListAdapter adapter = new SimpleAdapter(EventActivity.this, eventList, R.layout.item_list,
                                new String[]{TAG_LINK, TAG_TITLE, TAG_ADDRESS, TAG_DATE},
                                new int[]{R.id.linkList, R.id.nameList, R.id.addressList, R.id.dateList });

                        setListAdapter(adapter);
                    }
                });
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean event) {
            progressBar.setVisibility(View.GONE);
            for(HashMap<String, String> s: eventList){
                Log.d(TAG, TAG_TITLE); // ask debby y output total line instead of all of it
            }
        }
    }
}
