package com.boha.malengagolf.library.fragments;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.GestureDetectorCompat;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.boha.malengagolf.library.R;
import com.boha.malengagolf.library.data.PhotoUploadDTO;
import com.boha.malengagolf.library.data.ResponseDTO;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static android.view.GestureDetector.SimpleOnGestureListener;

/**
 * Created by aubreymalabie on 4/2/16.
 */
public class PhotoScrollerFragment extends Fragment implements View.OnTouchListener {

    List<PhotoUploadDTO> photoUploads;
    View view;

    public static PhotoScrollerFragment newInstance(List<PhotoUploadDTO> list) {
        PhotoScrollerFragment f = new PhotoScrollerFragment();
        ResponseDTO w = new ResponseDTO();
        w.setPhotoUploads(list);
        Bundle b = new Bundle();
        b.putSerializable("list", w);
        return f;
    }

    @Override
    public void onCreate(Bundle b) {
        if (getArguments() != null) {
            ResponseDTO w = (ResponseDTO) getArguments().getSerializable("list");
            photoUploads = w.getPhotoUploads();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_photo_scroller, container, false);
        gDetect = new GestureDetectorCompat(getActivity(), new GestureListener(new SwipeListener() {
            @Override
            public void onForwardSwipe() {
                if (position == photoUploads.size() - 1) {
                    Snackbar.make(image, "No more photographs this way", Snackbar.LENGTH_SHORT).show();
                    return;
                }
                position++;
                if (position < photoUploads.size()) {
                    setPosition(position);
                    photoAndCaption.startAnimation(AnimationUtils
                            .loadAnimation(getActivity(), R.anim.slide_in_right));
                    return;
                }

            }

            @Override
            public void onBackwardSwipe() {
                if (position == 0) {
                    Snackbar.make(image, "No more photographs this way", Snackbar.LENGTH_SHORT).show();
                    return;
                }
                position--;
                if (position > -1) {
                    setPosition(position);
                    photoAndCaption.startAnimation(AnimationUtils
                            .loadAnimation(getActivity(), R.anim.slide_in_left));
                    return;
                }
            }
        }));

        setFields();
        setPosition(position);
        return view;
    }

    int position;
    PhotoUploadDTO photo;
    TextView txtDate, txtCaption, number;
    ImageView image;
    View photoAndCaption;

    static final SimpleDateFormat df = new SimpleDateFormat("EEEE, dd MMMM yyyy HH:mm");

    public void setPosition(int position) {
        this.position = position;
        if (photoUploads == null) {
            return;
        }

        if (position < photoUploads.size()) {
            photo = photoUploads.get(position);
            if (photo.getFilePath() != null) {
                setLocalImage();
            } else {
                setRemoteImage();
            }
            txtDate.setText(df.format(new Date(photo.getDateTaken())));

            number.setText("" + (photoUploads.size() - position));
        }
    }

    private void setFields() {
        image = (ImageView) view.findViewById(R.id.PHOTO_image);
        photoAndCaption = view.findViewById(R.id.PHOTO_top);
        txtCaption = (TextView) view.findViewById(R.id.PHOTO_caption);
        number = (TextView) view.findViewById(R.id.PHOTO_numberRed);

        image.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                gDetect.onTouchEvent(event);
                return true;
            }
        });
    }

    private void setLocalImage() {
        File file = new File(photo.getFilePath());
        Picasso.with(getContext()).load(file).fit().into(image);
    }

    private void setRemoteImage() {
        Picasso.with(getContext()).load(photo.getUrl()).fit().into(image);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        gDetect.onTouchEvent(event);
        return true;
    }

    public interface PhotoListener {
        void onPhotoClicked(PhotoUploadDTO photo);
    }

    public interface SwipeListener {
        void onForwardSwipe();

        void onBackwardSwipe();
    }

    private GestureDetectorCompat gDetect;

    public class GestureListener extends SimpleOnGestureListener {
        private float flingMin = 100;
        private float velocityMin = 100;
        private SwipeListener listener;

        public GestureListener(SwipeListener listener) {
            this.listener = listener;
        }

        @Override
        public boolean onFling(MotionEvent event1, MotionEvent event2,
                               float velocityX, float velocityY) {
            boolean forward = false;
            boolean backward = false;
            //calculate the change in X position within the fling gesture
            float horizontalDiff = event2.getX() - event1.getX();
            float verticalDiff = event2.getY() - event1.getY();

            float absHDiff = Math.abs(horizontalDiff);
            float absVDiff = Math.abs(verticalDiff);
            float absVelocityX = Math.abs(velocityX);
            float absVelocityY = Math.abs(velocityY);

            if (absHDiff > absVDiff && absHDiff > flingMin && absVelocityX > velocityMin) {
                if (horizontalDiff > 0)
                    backward = true;
                else
                    forward = true;
            } else if (absVDiff > flingMin && absVelocityY > velocityMin) {
                if (verticalDiff > 0)
                    backward = true;
                else
                    forward = true;
            }
            if (forward) {
                listener.onForwardSwipe();
            } else if (backward) {
                listener.onBackwardSwipe();
            }

            return true;
        }

        @Override
        public boolean onDown(MotionEvent event) {
            return true;
        }

    }

}
