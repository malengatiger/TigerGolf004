package com.boha.malengagolf.library.gallery;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;

import com.boha.malengagolf.library.R;
import com.boha.malengagolf.library.data.GolfGroupDTO;
import com.boha.malengagolf.library.data.RequestDTO;
import com.boha.malengagolf.library.data.ResponseDTO;
import com.boha.malengagolf.library.data.TournamentDTO;
import com.boha.malengagolf.library.fragments.StaggeredListener;
import com.boha.malengagolf.library.util.ErrorUtil;
import com.boha.malengagolf.library.util.MGPageFragment;
import com.boha.malengagolf.library.util.SharedUtil;
import com.boha.malengagolf.library.util.Statics;
import com.boha.malengagolf.library.util.WebSocketUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by aubreyM on 2014/05/23.
 */
public class StaggeredTournamentGridFragment extends Fragment implements MGPageFragment {


    StaggeredListener listener;
    @Override
    public void onAttach(Activity a) {
        if (a instanceof StaggeredListener) {
            listener = (StaggeredListener)a;
        } else {
            throw new UnsupportedOperationException("Host " + a.getLocalClassName() + " must implement StaggeredListener");
        }
        Log.i(LOG,
                "onAttach ---- Fragment called and hosted by "
                        + a.getLocalClassName()
        );
        super.onAttach(a);
    }

    FragmentActivity act;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle saved) {
        Log.e(LOG, "@@@@@@@@@@@@@@@@@@@@@@@@@@.......onCreateView.........");
        ctx = getActivity();
        act = getActivity();
        inflater = getActivity().getLayoutInflater();
        view = inflater
                .inflate(R.layout.fragment_tourn_pictures, container, false);
        setFields();
        golfGroup = SharedUtil.getGolfGroup(ctx);
        Log.w(LOG, "#####################golfGroup: " + golfGroup.getGolfGroupID());
        Bundle b = getArguments();
        if (b != null) {
            tournament = (TournamentDTO) b.getSerializable("tournament");
            getTournamentPictures();
        }

        return view;
    }
    public void getTournamentPictures() {
        Log.e(LOG, "getTournamentPictures ##########");
        RequestDTO w = new RequestDTO();
        w.setRequestType(RequestDTO.GET_TOURNAMENT_THUMBNAILS);
        w.setTournamentID(tournament.getTournamentID());
        w.setGolfGroupID(golfGroup.getGolfGroupID());
        listener.setBusy();
        WebSocketUtil.sendRequest(ctx, Statics.ADMIN_ENDPOINT, w, new WebSocketUtil.WebSocketListener() {
            @Override
            public void onMessage(final ResponseDTO r) {
                act.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        listener.setNotBusy();
                        if (ErrorUtil.checkServerError(ctx, r)) {
                            if (r.getImageFileNames().isEmpty()) {
                                listener.onTournamentImagesNotFound();
                                return;
                            }
                            fileNames = r.getImageFileNames();
                            createURLs();
                        }
                    }
                });
            }

            @Override
            public void onClose() {

            }

            @Override
            public void onError(String message) {
                act.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        listener.setNotBusy();
                        ErrorUtil.showServerCommsError(ctx);
                    }
                });
            }
        });
//        BaseVolley.getRemoteData(Statics.SERVLET_PHOTO, w, ctx, new BaseVolley.BohaVolleyListener() {
//            @Override
//            public void onResponseReceived(ResponseDTO r) {
//                listener.setNotBusy();
//                if (ErrorUtil.checkServerError(ctx, r)) {
//                    fileNames = r.getImageFileNames();
//                    createURLs();
//                }
//            }
//
//            @Override
//            public void onVolleyError(VolleyError error) {
//                listener.setNotBusy();
//                ErrorUtil.showServerCommsError(ctx);
//            }
//        });
    }
    private void createURLs() {
        Log.e(LOG, "createURLs ##########");
        imageURLs = new ArrayList<String>();

        Log.w(LOG, "golfGroup: " + golfGroup.getGolfGroupID());
        Log.w(LOG, " tournament" + tournament.getTournamentID());
        if (fileNames != null) {
            for (String fileName : fileNames) {
                StringBuilder sb = new StringBuilder();
                sb.append(Statics.IMAGE_URL).append("golfgroup");
                sb.append(golfGroup.getGolfGroupID()).append("/");
                sb.append("tournament").append(tournament.getTournamentID()).append("/");
                sb.append(fileName);
                imageURLs.add(sb.toString());
            }
        } else {
            Log.e(LOG, "fileNames is NULL");
        }
        Log.e(LOG,"**********number of image items: " + imageURLs.size());

        setList();
    }


    private void setList() {
        Log.e(LOG, "setList ##########");
        imageAdapter = new MGImageAdapter(imageURLs, true, ctx,
                new MGImageAdapter.ImageAdapterListener() {
                    @Override
                    public void onImageClick(String url) {
                        Log.w(LOG, "Image tapped: " + url);
                    }
                }
        );
        gridView.setAdapter(imageAdapter);
        imageAdapter.notifyDataSetChanged();


    }

    boolean mHasRequestedMore;
    private void setFields() {
        gridView = (GridView) view.findViewById(R.id.TPIC_gridView);
        //gridView.setGridPadding(0,0,0,0);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.d(LOG,"onItemClick .... fileName: " + fileNames.get(i));
            }
        });
        gridView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i2, int i3) {
                firstVisibleItem = i;
                visibleItemCount = i2;
                totalItemCount = i3;
//                Log.w(LOG, "--- onScroll firstVisibleItem:" + firstVisibleItem +
//                        " visibleItemCount:" + visibleItemCount +
//                        " totalItemCount:" + totalItemCount);
                // our handling
                //if (!mHasRequestedMore) {
                    int lastInScreen = firstVisibleItem + visibleItemCount;
                    if (lastInScreen >= totalItemCount) {
                        //Log.d(LOG, "onScroll lastInScreen - so load more");
                        mHasRequestedMore = true;
                        onLoadMoreItems();

                    }

                //}
            }
        });
    }
    private void onLoadMoreItems() {
        //Log.e(LOG, "------------onLoadMoreItems ---------");
//        final ArrayList<String> sampleData = SampleData.generateSampleData();
//        for (String data : sampleData) {
//            mAdapter.add(data);
//        }
//        // stash all the data in our backing store
//        mData.addAll(sampleData);
//        // notify the adapter that we can update now
//        mAdapter.notifyDataSetChanged();
//        mHasRequestedMore = false;
    }
    GridView gridView;
    MGImageAdapter imageAdapter;
    Context ctx;
    View view;
    ResponseDTO response;
    TournamentDTO tournament;
    GolfGroupDTO golfGroup;
    List<String> imageURLs, fileNames;
    int firstVisibleItem, visibleItemCount, totalItemCount;

    public void setFileNames(List<String> fileNames) {
        this.fileNames = fileNames;

    }

    static final String LOG = "TournamentPicturesFragment";
}
