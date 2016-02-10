package com.tortelabs.realmrecyclerview.db;

import com.tortelabs.realmrecyclerview.pojo.Movie;

import io.realm.Realm;

/**
 * Created by gowtham on 10/02/16.
 */
public class RealmDbHelper {

    public static void saveMovie(final Movie movie) {
        //save movie object in background thread
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.copyToRealmOrUpdate(movie);
            }
        }, new Realm.Transaction.Callback());
    }

}
