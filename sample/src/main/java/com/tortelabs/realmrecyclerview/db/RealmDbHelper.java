package com.tortelabs.realmrecyclerview.db;

import android.content.Context;

import com.tortelabs.realmrecyclerview.pojo.Movie;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by gowtham on 10/02/16.
 */
public class RealmDbHelper {

    public static void saveMovie(final Movie movie, Context context) {
        //save movie object in background thread
        Realm realm = Realm.getInstance(context);
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.copyToRealmOrUpdate(movie);
            }
        });
    }

    public static void clearDb(Context context) {
        Realm realm = Realm.getInstance(context);
        realm.beginTransaction();
        realm.where(Movie.class).findAll().clear();
        realm.commitTransaction();
    }

    public static void saveMovies(final ArrayList<Movie> data,Context context) {
        Realm realm = Realm.getInstance(context);
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.copyToRealmOrUpdate(data);
            }
        });
    }

    public static RealmResults<Movie> getAllMovies(Context context) {
        return Realm.getInstance(context).where(Movie.class).findAll();
    }
}
