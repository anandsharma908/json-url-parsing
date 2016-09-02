package com.example.anand.jsontask;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static com.example.anand.jsontask.R.id.imageView;

public class MainActivity extends AppCompatActivity {
    //static TextView textView;
     ListView listView;
    Button button;
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Create default options which will be used for every
//  displayImage(...) call if no options will be passed to this method
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true).cacheOnDisk(true).build();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
                .defaultDisplayImageOptions(defaultOptions).build();
        ImageLoader.getInstance().init(config); // Do it on Application start

        listView = (ListView) findViewById(R.id.listview);
        dialog=new ProgressDialog(this);
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        dialog.setMessage("Loading please wait....");
        /*button = (Button) findViewById(R.id.btnhit);
        textView = (TextView) findViewById(R.id.json_tv);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new JSONTask().execute("http://jsonparsing.parseapp.com/jsonData/moviesDemoList.txt");
            }
        });*/
    }
    public class JSONTask extends AsyncTask<String,String,List<MovieModel>>
    {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.show();
        }

        @Override
        protected List<MovieModel> doInBackground(String... params) {
            HttpURLConnection connection=null;
            BufferedReader reader=null;
            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                InputStream stream = connection.getInputStream();
                connection.connect();
                reader = new BufferedReader(new InputStreamReader(stream));
                StringBuffer buffer = new StringBuffer();
                String line = "";
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }
       /* it will handle only one line
       String finalJson=buffer.toString();
        JSONObject parentObject=new JSONObject(finalJson);
        JSONArray parentArray=parentObject.getJSONArray("movies");
        JSONObject finalObject=parentArray.getJSONObject(0);
        String movie=finalObject.getString("movie");
        int year=finalObject.getInt("year");
        return movie+" "+year;*/
                String finalJson = buffer.toString();
                JSONObject parentObject = new JSONObject(finalJson);
                JSONArray parentArray = parentObject.getJSONArray("movies");
                //StringBuffer finalString=new StringBuffer();
                List<MovieModel> movieModelList = new ArrayList<>();
                MovieModel movieModel ;
                Gson gson=new Gson();
                for (int i = 0; i < parentArray.length(); i++) {
                    JSONObject finalObject = parentArray.getJSONObject(i);
                    movieModel=gson.fromJson(finalObject.toString(),MovieModel.class);
                    //String movie=finalObject.getString("movie");
                    //int year=finalObject.getInt("year");
                    //finalString.append(movie+" "+year+"\n");
                  /*  movieModel = new MovieModel();
                    movieModel.setMovie(finalObject.getString("movie"));
                    movieModel.setRating((float) finalObject.getDouble("rating"));
                    movieModel.setDuration(finalObject.getString("duration"));
                    movieModel.setDirector(finalObject.getString("director"));
                    movieModel.setTagline(finalObject.getString("tagline"));
                    movieModel.setYear(finalObject.getInt("year"));
                    movieModel.setStory(finalObject.getString("story"));
                    movieModel.setImage(finalObject.getString("image"));

                    List<MovieModel.Cast> castList = new ArrayList<>();

                    for (int j = 0; j < finalObject.getJSONArray("cast").length(); j++) {
                        MovieModel.Cast cast1 = new MovieModel.Cast();
                        cast1.setName(finalObject.getJSONArray("cast").getJSONObject(j).getString("name"));
                        castList.add(cast1);
                    }
                    movieModel.setCastList(castList);*/
                    movieModelList.add(movieModel);
                }
                return movieModelList;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                if(connection!=null)
                    connection.disconnect();
                if(reader!=null)
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<MovieModel> result) {
            super.onPostExecute(result);
            dialog.dismiss();
            MovieAdapter adapter=new MovieAdapter(getApplicationContext(),R.layout.row,result);
            listView.setAdapter(adapter);
            //MainActivity.textView.setText(result);
            //Toast.makeText(this, ""+ result, Toast.LENGTH_SHORT).show();
            //Toast.makeText(this,result,2000).show();
        }
    }


    public class MovieAdapter extends ArrayAdapter implements View.OnClickListener {
        private int resourse;
        private LayoutInflater inflater1;
        public List<MovieModel> movieModelList;
        public MovieAdapter(Context context, int resource, List<MovieModel> objects) {
            super(context, resource, objects);
            movieModelList=objects;
            this.resourse=resource;
            inflater1= (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder=null;
            if(convertView==null)
            {
                holder=new ViewHolder();
                convertView=inflater1.inflate(resourse,null);
                holder.ivImageIcon=(ImageView)convertView.findViewById(imageView);
                holder.tvMovie=(TextView)convertView.findViewById(R.id.tv_movie);
                holder.tvTagline=(TextView)convertView.findViewById(R.id.tv_tagline);
                holder.tvDirector=(TextView)convertView.findViewById(R.id.tv_director);
                holder.tvDuration=(TextView)convertView.findViewById(R.id.tv_duration);
                holder.tvStory=(TextView)convertView.findViewById(R.id.tv_story);
                holder.tvYear=(TextView)convertView.findViewById(R.id.tv_year);
                holder.tvCast=(TextView)convertView.findViewById(R.id.tv_cast);
                holder.ratingBar=(RatingBar)convertView.findViewById(R.id.ratingBar);
                convertView.setTag(holder);
            }else
            {
               holder=(ViewHolder) convertView.getTag();
            }
            final ProgressBar progressBar=(ProgressBar)convertView.findViewById(R.id.progressBar);;
            holder.tvMovie.setText(movieModelList.get(position).getMovie());
            holder.tvTagline.setText(movieModelList.get(position).getTagline());
            holder.tvDirector.setText(movieModelList.get(position).getDirector());
            holder.tvDuration.setText("Length: " + movieModelList.get(position).getDuration());
            holder.tvStory.setText(movieModelList.get(position).getStory());
            holder.tvYear.setText("Year " + movieModelList.get(position).getYear());

            //if(holder.tvMovie.getText().)
            // Then later, when you want to display image
            ImageLoader.getInstance().displayImage(movieModelList.get(position).getImage(), holder.ivImageIcon, new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String imageUri, View view) {
                    progressBar.setVisibility(View.VISIBLE);
                }

                @Override
                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                    progressBar.setVisibility(View.GONE);
                }

                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    progressBar.setVisibility(View.GONE);
                }

                @Override
                public void onLoadingCancelled(String imageUri, View view) {
                    progressBar.setVisibility(View.GONE);
                }
            }); // Default// options will be used
            String str= (String) holder.tvMovie.getText();
            holder.tvMovie.setOnClickListener(this);


            holder.ratingBar.setRating(movieModelList.get(position).getRating() / 2);
            StringBuffer stringBuffer=new StringBuffer();
            for(MovieModel.Cast cast:movieModelList.get(position).getCastList())
            {
                stringBuffer.append(cast.getName()+" ");
            }
            holder.tvCast.setText(stringBuffer);
            return convertView;
        }

        @Override
        public void onClick(View view) {
            int id=view.getId();


            Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("https://retail.onlinesbi.com/retail/login.htm"));
            startActivity(i);
                   }

        public class ViewHolder
        {
            ImageView ivImageIcon;
            TextView tvMovie;
            TextView tvTagline;
            TextView tvDirector;
            TextView tvDuration;
            TextView tvStory;
            TextView tvYear;
            TextView tvCast;
            RatingBar ratingBar;
        }
    }
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id=item.getItemId();
        if(id==R.id.list_menu)
        {
            new JSONTask().execute("http://jsonparsing.parseapp.com/jsonData/moviesData.txt");
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

