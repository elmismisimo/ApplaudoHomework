package com.sandersoft.applaudohomework;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.ImageView;

import com.sandersoft.applaudohomework.models.Team;
import com.sandersoft.applaudohomework.utils.Globals;
import com.sandersoft.applaudohomework.utils.TeamRequestListener;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.ResourceHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.Callable;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Sander on 11/05/2017.
 */

public class WebManager {

    public WebManager(){}
    //instance of the mTeam fecth observable, so this can be unsubscribed anytime
    Observable<ResponseEntity> teamsObservable;

    /**
     * Verify if there is an internet connection
     * @return <b>true</b> if there is a connection, <b>false</b> if there isnt
     */
    public boolean verifyConn()
    {
        //check if the instance of the app is null
        if (null == ApplicationMain.getContext())return false;
        //check if there is an internet connection
        ConnectivityManager cm = (ConnectivityManager) ApplicationMain.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (null != netInfo && netInfo.isConnectedOrConnecting())
            return true;
        return false;
    }

    /**
     * Prepare and execute a mTeam fetch request
     */
    public void doTeamsRequest(TeamRequestListener listener){
        getTeamsReactive(listener);
    }
    /**
     * Function that fetches the teams, using reactive
     */
    private void getTeamsReactive(final TeamRequestListener listener){
        //we check if the observable exists, else we unsubscribe so the last call is not called
        if (teamsObservable != null)
            teamsObservable.unsubscribeOn(Schedulers.io());
        //we create the observable (this is in charge of retrieving the mTeam list)
        teamsObservable = Observable.fromCallable(new Callable<ResponseEntity>() {
            @Override
            public ResponseEntity call() throws Exception {
                String url = Globals.TEAMS_URL;
                return getMoviesProcess(url);
            }
        });
        //we create the observer (this is in charge of observing the task and receiving the response of the teams)
        Observer<ResponseEntity> teamsObserver = new Observer<ResponseEntity>() {
            @Override
            public void onSubscribe(Disposable d) { }

            @Override
            public void onNext(ResponseEntity response) {
                //verify if there is a weblistener and if the response is diferent to null
                if (null != listener && null != response) {
                    //verify if the request was successfull
                    if (response.getStatusCode().equals(HttpStatus.OK)) {
                        //return the teams
                        listener.receiveTeams((Team[]) response.getBody());
                    } else {
                        //return the error
                        listener.receiveError(response.getStatusCode() + ": " + response.getBody().toString());
                    }
                }
            }

            @Override
            public void onError(Throwable e) {
                Log.e("WebRequest", e.getMessage(), e);

            }

            @Override
            public void onComplete() { }
        };
        //we create the subscrition (this is in charge of creating the task in background and starts it
        //at .subscribe(observer))
        teamsObservable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(teamsObserver);
    }
    /**
     * The process that does the fetch of the teams form applaudo json file
     * @param url The url to fetch
     * @return returns a list of Team instnaces returned form applaudo
     */
    private ResponseEntity getMoviesProcess(String url) throws HttpClientErrorException {
        //define the spring request
        RestTemplate restTemplate = new RestTemplate();
        //define the headers
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.add("Content-Type", "application/json");
        HttpEntity<?> requestEntity = new HttpEntity<Object>(requestHeaders);
        //we add the json converter
        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
        //do the request (depending if its multiple or not, the receiving class changes)
        ResponseEntity response = restTemplate.exchange(url, HttpMethod.GET, requestEntity, Team[].class);
        return response;
    }

    /**
     * Prepares and execute a bitmap image request
     * @param listener
     * @param imageHolder
     * @param team
     * @param image_url
     */
    public void doImageRequest(TeamRequestListener listener, ImageView imageHolder, Team team
            , String image_url){
        getImageReactive(listener, imageHolder, team, image_url);
    }
    /**
     * Function that fetches a bitmap image from the web, using reactive
     * @param listener
     * @param imageHolder
     * @param team
     * @param image_url
     */
    public void getImageReactive(final TeamRequestListener listener, final ImageView imageHolder
            , final Team team, final String image_url){
        //we create the observable (this is in charge of retrieving the movie list)
        Observable<Bitmap> imageObservable = Observable.fromCallable(new Callable<Bitmap>() {
            @Override
            public Bitmap call() throws Exception {
                //generate the url
                final String url = image_url;
                //do the request process
                ResponseEntity<Resource> response = getImageProcess(url);
                //convert the response to bitmap
                if (null != response.getBody()) {
                    Bitmap b = BitmapFactory.decodeStream(response.getBody().getInputStream());
                    return b;
                }
                return null;
            }
        });
        //we create the observer (this is in charge of observing the task and receiving the response of the movies)
        Observer<Bitmap> imageObserver = new Observer<Bitmap>() {
            @Override
            public void onSubscribe(Disposable d) {}

            @Override
            public void onNext(Bitmap image) {
                //verify if there is a weblistener
                if (null != listener && null != image) {
                    //return the bitmap image
                    listener.receiveImage(image, team, imageHolder);
                }
            }

            @Override
            public void onError(Throwable e) {
                //Log.e("ImageFetch", e.getMessage() + " " + image_url, e);
                Bitmap image = BitmapFactory.decodeResource(ApplicationMain.getContext().getResources(),
                        R.mipmap.image_not_found);
                //return the bitmap image
                listener.receiveImage(image, team, imageHolder);
            }

            @Override
            public void onComplete() { }
        };
        //we create the subscrition (this is in charge of creating the task in background and starts it
        //at .subscribe(observer))
        imageObservable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(imageObserver);
    }
    /**
     * Process that fetches an image bitmap from TMDB
     * @param url the url of the image
     * @return ResponseEntity with the type Resource, and the image must be in the body
     */
    public ResponseEntity<Resource> getImageProcess(String url){
        //define the spring request
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
        restTemplate.getMessageConverters().add(new ByteArrayHttpMessageConverter());
        restTemplate.getMessageConverters().add(new ResourceHttpMessageConverter());
        //define the headders
        HttpHeaders requestHeaders = new HttpHeaders();
        HttpEntity<?> requestEntity = new HttpEntity<Object>(requestHeaders);
        //do the request
        ResponseEntity<Resource> response = restTemplate.exchange(url, HttpMethod.GET, requestEntity, Resource.class);
        return response;
    }

}
