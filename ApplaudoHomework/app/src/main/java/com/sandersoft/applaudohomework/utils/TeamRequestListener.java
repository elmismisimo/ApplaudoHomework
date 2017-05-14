package com.sandersoft.applaudohomework.utils;

import android.graphics.Bitmap;
import android.widget.ImageView;

import com.sandersoft.applaudohomework.models.Team;

/**
 * Created by Sander on 11/05/2017.
 */

public interface TeamRequestListener {
    public void receiveTeams(Team[] teams);
    public void receiveImage(Bitmap image, Team team, ImageView imageHolder);
    public void receiveError(String error);
}
