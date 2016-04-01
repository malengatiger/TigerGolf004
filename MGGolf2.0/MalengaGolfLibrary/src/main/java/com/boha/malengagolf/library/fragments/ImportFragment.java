package com.boha.malengagolf.library.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.boha.malengagolf.library.R;
import com.boha.malengagolf.library.adapters.ImportPlayerAdapter;
import com.boha.malengagolf.library.data.GolfGroupDTO;
import com.boha.malengagolf.library.data.ImportPlayerDTO;
import com.boha.malengagolf.library.data.RequestDTO;
import com.boha.malengagolf.library.data.ResponseDTO;
import com.boha.malengagolf.library.util.ErrorUtil;
import com.boha.malengagolf.library.util.ImportUtil;
import com.boha.malengagolf.library.util.SharedUtil;
import com.boha.malengagolf.library.util.Statics;
import com.boha.malengagolf.library.util.ToastUtil;
import com.boha.malengagolf.library.util.WebSocketUtil;
import com.boha.malengagolf.library.util.bean.ImportException;
import com.boha.malengagolf.library.volley.toolbox.BaseVolley;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Created by aubreyM on 2014/06/13.
 */
public class ImportFragment extends Fragment {
    public interface ImportFragmentListener {
        public void onPlayersImported();

        public void setBusy();

        public void setNotBusy();
    }

    ImportFragmentListener listener;
    FragmentActivity act;
    @Override
    public void onAttach(Activity a) {
        if (a instanceof ImportFragmentListener) {
            listener = (ImportFragmentListener) a;
        } else {
            throw new UnsupportedOperationException("Host " + a.getLocalClassName() +
                    " must implement ImportFragmentListener");
        }
        Log.e(LOG, "##### Fragment hosted by " + a.getLocalClassName());
        super.onAttach(a);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle saved) {
        ctx = getActivity();
        act = getActivity();
        inflater = getActivity().getLayoutInflater();
        view = inflater
                .inflate(R.layout.import_players, container, false);
        setFields();
        golfGroup = SharedUtil.getGolfGroup(ctx);
        setFields();
        return view;

    }

    private void setFields() {
        fileSpinner = (Spinner) view.findViewById(R.id.IMP_fileSpinner);
        btnImport = (Button) view.findViewById(R.id.IMP_btnImport);
        txtTitle = (TextView) view.findViewById(R.id.IMP_title);
        txtCount = (TextView) view.findViewById(R.id.IMP_count);
        image = (ImageView) view.findViewById(R.id.IMP_image);
        image.setVisibility(View.GONE);
        list = (ListView) view.findViewById(R.id.IMP_list);
        checkBox = (CheckBox) view.findViewById(R.id.IMP_allCheck);

        txtTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                image.setVisibility(View.VISIBLE);
            }
        });
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                image.setVisibility(View.GONE);
            }
        });

        btnImport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int count = 0;
                for (ImportPlayerDTO p : playerList) {
                    if (p.isSelected()) {
                        count++;
                    }
                }
                if (count == 0) {
                    ToastUtil.toast(ctx, ctx.getResources().getString(R.string.select_players_imp));
                    return;
                }
                totalPages = playerList.size() / PAGE_SIZE;
                int rem = playerList.size() % PAGE_SIZE;
                if (rem > 0) {
                    totalPages++;
                }
                controlImport();
            }
        });

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    for (ImportPlayerDTO p : playerList) {
                        p.setSelected(true);
                    }
                } else {
                    for (ImportPlayerDTO p : playerList) {
                        p.setSelected(false);
                    }
                }
                adapter.notifyDataSetChanged();
            }
        });
        files = ImportUtil.getImportFilesOnSD();

        setSpinner();
        Log.w(LOG, "Import files found: " + files.size());
    }

    private void parseFile(File file) throws IOException {
        playerList = new ArrayList<ImportPlayerDTO>();

        BufferedReader brReadMe = new BufferedReader(new InputStreamReader(
                new FileInputStream(file), "UTF-8"));
        String strLine = brReadMe.readLine();
        while (strLine != null) {
            ImportPlayerDTO dto = null;
            try {
                dto = parseLine(strLine);
                playerList.add(dto);
                Log.e(LOG, "####### Player added to list from import_pic file");
                strLine = brReadMe.readLine();
            } catch (ImportException e) {
                e.printStackTrace();
            }

        }

        brReadMe.close();
        setList();
    }

    public static ImportPlayerDTO parseLine(String line) throws ImportException {
        Pattern patt = Pattern.compile(";");

        if (line.indexOf(",") > -1) {
            patt = Pattern.compile(",");
        }
        String[] result = patt.split(line);
        ImportPlayerDTO dto = new ImportPlayerDTO();
        try {
            if (result[0] != null) {
                dto.setFirstName(result[0]);
            }

            if (result[1] != null) {
                dto.setLastName(result[1]);
            }
            try {
                if (result[2] != null) {
                    dto.setEmail(result[2]);
                }
                if (result[3] != null) {
                    dto.setCellphone(result[3]);
                }
                if (result[4] != null) {
                    //TODO - parse string date of birth - dd-mm-yyyy
                    Pattern p = Pattern.compile("-");
                    String[] strings = p.split(result[4]);
                    String dd = strings[0];
                    String mm = strings[1];
                    String yyyy = strings[2];
                    Calendar cal = Calendar.getInstance();
                    cal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(dd));
                    cal.set(Calendar.MONTH, Integer.parseInt(mm) - 1);
                    cal.set(Calendar.YEAR, Integer.parseInt(yyyy));
                    dto.setDateOfBirth(cal.getTimeInMillis());
                }
                if (result[5] != null) {
                    if (result[5].equalsIgnoreCase("M")) {
                        dto.setGender(1);
                    }
                    if (result[5].equalsIgnoreCase("F")) {
                        dto.setGender(2);
                    }
                }

            } catch (Exception e) {
                // ignore
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            //ignore
        }
        // validate
        if (dto.getFirstName() == null || dto.getLastName() == null) {
            throw new ImportException();
        }
        Log.e(LOG,
                "Found player: " + dto.getFirstName() + " "
                        + dto.getLastName() + " to import_pic into mgGolf");
        return dto;
    }

    List<File> files = new ArrayList<File>();

    int index = 0, pageCnt = 0, totalPages = 0;
    static final int PAGE_SIZE = 30;
    boolean isDone;

    private void controlImport() {

        if (pageCnt < totalPages) {
            List<ImportPlayerDTO> list = new ArrayList<ImportPlayerDTO>();
            for (int i = 0; i < PAGE_SIZE; i++) {
                try {
                    list.add(playerList.get(index));
                    index++;
                } catch (Exception e) {
                }
            }
            importPlayers(list);

        } else {
            listener.onPlayersImported();
            ToastUtil.toast(ctx, ctx.getResources().getString(R.string.import_ok));
        }


    }

    private void importPlayers(List<ImportPlayerDTO> list) {

        Log.e(LOG, "### totalPages: " + totalPages + " pageCnt: " + pageCnt + " list: " + list.size());
        RequestDTO w = new RequestDTO();
        w.setRequestType(RequestDTO.IMPORT_PLAYERS);
        w.setImportPlayers(list);
        w.setGolfGroupID(golfGroup.getGolfGroupID());

        if (!BaseVolley.checkNetworkOnDevice(ctx)) {
            return;
        }
        listener.setBusy();
        WebSocketUtil.sendRequest(ctx,Statics.ADMIN_ENDPOINT,w,new WebSocketUtil.WebSocketListener() {
            @Override
            public void onMessage(final ResponseDTO response) {
                act.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        listener.setNotBusy();
                        if (!ErrorUtil.checkServerError(ctx, response)) {
                            return;
                        }
                        Log.w(LOG, "#####  ########## players imported OK ....pageCnt: " + pageCnt + response.getMessage());
                        pageCnt++;
                        controlImport();
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

                    }
                });
            }
        });
//        BaseVolley.getRemoteData(Statics.SERVLET_ADMIN, w, ctx, new BaseVolley.BohaVolleyListener() {
//            @Override
//            public void onResponseReceived(ResponseDTO response) {
//                listener.setNotBusy();
//                if (!ErrorUtil.checkServerError(ctx, response)) {
//                    return;
//                }
//                Log.w(LOG, "#####  ########## players imported OK ....pageCnt: " + pageCnt + response.getMessage());
//                pageCnt++;
//                controlImport();
//
//            }
//
//            @Override
//            public void onVolleyError(VolleyError error) {
//                listener.setNotBusy();
//                ErrorUtil.showServerCommsError(ctx);
//            }
//        });
    }

    private void setList() {
        adapter = new ImportPlayerAdapter(ctx, R.layout.import_item, playerList);
        list.setAdapter(adapter);
        txtCount.setText("" + playerList.size());
    }

    static final Locale loc = Locale.getDefault();
    static final SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm", loc);

    private void setSpinner() {

        List<String> list = new ArrayList<String>();
        for (File p : files) {
            list.add(p.getName() + " - " + sdf.format(new Date(p.lastModified())));
        }
        ArrayAdapter a = new ArrayAdapter(ctx, R.layout.xxsimple_spinner_item, list);
        a.setDropDownViewResource(R.layout.xxsimple_spinner_dropdown_item);
        fileSpinner.setAdapter(a);
        fileSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                try {
                    checkBox.setChecked(false);
                    parseFile(files.get(i));
                } catch (IOException e) {
                    ToastUtil.errorToast(ctx, ctx.getResources().getString(R.string.failed_import));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    View view;
    Context ctx;
    GolfGroupDTO golfGroup;
    TextView txtTitle, txtCount;
    Spinner fileSpinner;
    Button btnImport;
    ListView list;
    List<ImportPlayerDTO> playerList;
    ImportPlayerAdapter adapter;
    ImageView image;
    CheckBox checkBox;
    static final String LOG = "ImportFragment";
}
//http://192.168.1.111:8055/golf/admin?JSON={"playerList":[{"cellphone":"082 999 8077","email":"anderson@gmail.com","firstName":"Anderson","lastName":"Cooper","gender":1,"dateOfBirth":976448985250,"dateRegistered":0,"parentID":0,"numberOfTournaments":0,"playerID":0,"age":0,"selected":true,"sortType":0,"yearJoined":0}],"latitude":0.0,"longitude":0.0,"page":0,"provinceID":0,"radius":0,"radiusType":0,"requestType":85,"personalPlayerID":0,"leaderBoardID":0,"playerID":0,"tournamentID":0,"tournamentType":0,"golfGroupID":44,"countryID":0,"clubCourseID":0,"type":0,"appUserID":0,"winnerFlag":0,"zippedResponse":true}