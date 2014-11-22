package com.droidcba.redditget.rest.api;

import com.droidcba.redditget.rest.pojo.Listing;

import retrofit.http.GET;
import retrofit.http.Query;
import rx.Observable;

/**
 * Created by juancho on 11/21/14.
 */
public interface RedditApi {

    //http://www.reddit.com/r/google/top.json?limit=5
    @GET("/r/google/top.json?limit=10")
    Observable<Listing> getTop(@Query("after") String after);
}
