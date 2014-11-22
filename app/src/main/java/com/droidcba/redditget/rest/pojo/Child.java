package com.droidcba.redditget.rest.pojo;

import com.google.gson.annotations.Expose;

/**
 * Created by juancho on 11/21/14.
 */
public class Child {
    @Expose
    private String kind;
    @Expose
    private Data_ data;

    /**
     *
     * @return
     * The kind
     */
    public String getKind() {
        return kind;
    }

    /**
     *
     * @param kind
     * The kind
     */
    public void setKind(String kind) {
        this.kind = kind;
    }

    /**
     *
     * @return
     * The data
     */
    public Data_ getData() {
        return data;
    }

    /**
     *
     * @param data
     * The data
     */
    public void setData(Data_ data) {
        this.data = data;
    }
}
