package com.ureview.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LegendEntry;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.ureview.BaseApplication;
import com.ureview.R;
import com.ureview.activities.MainActivity;
import com.ureview.adapters.VideoViewRankingAdapter;
import com.ureview.listeners.IClickListener;
import com.ureview.listeners.IParserListener;
import com.ureview.models.UserStatsModel;
import com.ureview.models.VideoViewsModel;
import com.ureview.utils.LocalStorage;
import com.ureview.utils.StaticUtils;
import com.ureview.utils.views.CustomTextView;
import com.ureview.wsutils.WSCallBacksListener;
import com.ureview.wsutils.WSUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;

public class StatsFragment extends BaseFragment implements IParserListener<JsonElement>, IClickListener {
    private View rootView;
    private RecyclerView rvRankings;
    private MainActivity mainActivity;
    private LineChart chart;
    private CustomTextView txtYourRank;
    private ArrayList<UserStatsModel> userStatsModelArrayList;
    private ArrayList<VideoViewsModel> videoViewsModelArrayList;
    private VideoViewRankingAdapter videoViewRankingAdapter;
    private String userId;
    private RelativeLayout relRanking;
    private String rank;

    public static StatsFragment newInstance(String userId) {
        StatsFragment followersFragment = new StatsFragment();
        Bundle bundle = new Bundle();
        bundle.putString("userId", userId);
        followersFragment.setArguments(bundle);
        return followersFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = (MainActivity) getActivity();
        userStatsModelArrayList = new ArrayList<>();
        videoViewsModelArrayList = new ArrayList<>();

        if (getArguments() != null) userId = getArguments().getString("userId");
        else
            userId = LocalStorage.getInstance(mainActivity).getString(LocalStorage.PREF_USER_ID, "");
    }

    public static StatsFragment newInstance() {
        return new StatsFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_stats, container, false);
        initComponents();
        return rootView;
    }

    private void initComponents() {
        chart = rootView.findViewById(R.id.chart);
        rvRankings = rootView.findViewById(R.id.rvRankings);
        relRanking = rootView.findViewById(R.id.relRanking);
        txtYourRank = rootView.findViewById(R.id.txtYourRank);
        requestForGetRankingsWS();

        ViewCompat.setNestedScrollingEnabled(rootView.findViewById(R.id.nestedScrollView), true);
        ViewCompat.setNestedScrollingEnabled(rvRankings, false);
        setGraphData();
        requestForGetStatisticsWS();
    }

    private void requestForGetStatisticsWS() {
        Call<JsonElement> call = BaseApplication.getInstance().getWsClientListener().getUserStatistics(userId, Calendar.getInstance().get(Calendar.YEAR) + "");
        new WSCallBacksListener().requestForJsonObject(mainActivity, WSUtils.REQ_USER_STATISTICS, call, this);
    }

    private void requestForGetRankingsWS() {
        Call<JsonElement> call = BaseApplication.getInstance().getWsClientListener().getRankings(userId);
        new WSCallBacksListener().requestForJsonObject(mainActivity, WSUtils.REQ_FOR_RANKINGS, call, this);
    }

    private void setGraphData() {
        if (userStatsModelArrayList.size() > 0) {
            final HashMap<Integer, String> numMap = new HashMap<>();
            List<Entry> entries = new ArrayList<>();
            for (int i = 0; i < userStatsModelArrayList.size(); i++) {
                numMap.put(i, userStatsModelArrayList.get(i).month);
                entries.add(new Entry(i, Float.parseFloat(userStatsModelArrayList.get(i).count)));
            }
            LineDataSet dataSet = new LineDataSet(entries, /*Video View Stats*/"");
            dataSet.setColor(getResources().getColor(R.color.app_color_medium));
            dataSet.setCircleColor(getResources().getColor(R.color.app_color_dark));
            dataSet.setCircleColorHole(getResources().getColor(R.color.app_color_dark));
            dataSet.setCircleRadius(3f);
            dataSet.setCircleHoleRadius(3f);
            dataSet.setLineWidth(2f);
            dataSet.setDrawValues(true);

            LineData lineData = new LineData(dataSet);
            lineData.setValueTextColor(getResources().getColor(R.color.app_color_dark));
            lineData.setValueTextSize(13);
            XAxis xAxis = chart.getXAxis();
            xAxis.setValueFormatter((value, axis) -> numMap.get((int) value));

            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.setTextSize(13f);
            xAxis.setTextColor(Color.BLACK);
            xAxis.setDrawAxisLine(true);
            xAxis.setAxisLineColor(ContextCompat.getColor(mainActivity, R.color.app_color_medium));
            xAxis.setGridColor(ContextCompat.getColor(mainActivity, R.color.app_color_medium));
            xAxis.setDrawGridLines(false);
            xAxis.setAvoidFirstLastClipping(true);
            xAxis.setDrawLabels(true);
            xAxis.setLabelCount(userStatsModelArrayList.size(), true);
            xAxis.setGranularityEnabled(true);

            YAxis leftAxis = chart.getAxisLeft();
            chart.getAxisRight().setEnabled(false);
            leftAxis.setEnabled(false);
            leftAxis.setDrawAxisLine(false);
            leftAxis.setDrawGridLines(false);

            Legend legend = chart.getLegend();
            legend.mNeededHeight = 2f;
            legend.setYEntrySpace(2f);
            ArrayList<LegendEntry> list = new ArrayList<>();
            LegendEntry legendEntry = new LegendEntry();
            legendEntry.formColor = ContextCompat.getColor(mainActivity, R.color.color_white);
            list.add(legendEntry);
            legend.setCustom(list);
            legend.setEnabled(true);

            chart.getDescription().setEnabled(false);

            chart.setData(lineData);
            chart.invalidate();
        } else {
            chart.setData(null);
            chart.setNoDataText("No chart data available.");
            chart.setNoDataTextColor(getResources().getColor(R.color.colorBlack));
            chart.notifyDataSetChanged();
            chart.invalidate();
        }
    }

    @Override
    public void successResponse(int requestCode, JsonElement response) {
        switch (requestCode) {
            case WSUtils.REQ_USER_STATISTICS:
                parseGetUserStatsResponse((JsonObject) response);
                break;
            case WSUtils.REQ_FOR_RANKINGS:
                parseGetRankingsResponse((JsonObject) response);
                break;
            default:
                break;
        }
    }

    private void parseGetRankingsResponse(JsonObject response) {
        try {
            if (response.has("status")) {
                if (response.get("status").getAsString().equalsIgnoreCase("success")) {
                    if (response.has("video_views")) {
                        try {
                            videoViewsModelArrayList.clear();
                            JsonArray videoViewsArray = response.get("video_views").getAsJsonArray();
                            if (videoViewsArray.size() > 0) {
                                for (int i = 0; i < videoViewsArray.size(); i++) {
                                    VideoViewsModel vidModel = new VideoViewsModel(videoViewsArray.get(i).getAsJsonObject());
                                    if (vidModel.user_id.equalsIgnoreCase(LocalStorage
                                            .getInstance(mainActivity).getString(LocalStorage.PREF_USER_ID, "")))
                                        rank = vidModel.rank;
                                    videoViewsModelArrayList.add(vidModel);
                                }
                                relRanking.setVisibility(View.VISIBLE);
                            } else relRanking.setVisibility(View.GONE);
                            rvRankings.setLayoutManager(new LinearLayoutManager(mainActivity));
                            videoViewRankingAdapter = new VideoViewRankingAdapter(mainActivity, videoViewsModelArrayList, this);
                            rvRankings.setAdapter(videoViewRankingAdapter);
                            if (!TextUtils.isEmpty(rank))
                                txtYourRank.setText("Your Rank - ".concat(rank));
                            txtYourRank.setVisibility(TextUtils.isEmpty(rank) ? View.GONE : View.VISIBLE);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } else if (response.get("status").getAsString().equalsIgnoreCase("fail")) {
//                    StaticUtils.showToast(mainActivity, response.get("message").getAsString());
                    relRanking.setVisibility(View.GONE);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void parseGetUserStatsResponse(JsonObject response) {
        try {
            if (response.has("status")) {
                if (response.get("status").getAsString().equalsIgnoreCase("success")) {
                    if (response.has("video_views")) {
                        try {
                            userStatsModelArrayList.clear();
                            JsonArray videoViewsArray = response.get("video_views").getAsJsonArray();
                            if (videoViewsArray.size() > 0) {
                                for (int i = 0; i < videoViewsArray.size(); i++) {
                                    userStatsModelArrayList.add(new UserStatsModel(videoViewsArray.get(i).getAsJsonObject()));
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        setGraphData();
                    }
                } else if (response.get("status").getAsString().equalsIgnoreCase("fail")) {
                    StaticUtils.showToast(mainActivity, response.get("message").getAsString());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void errorResponse(int requestCode, String error) {

    }

    @Override
    public void noInternetConnection(int requestCode) {

    }

    @Override
    public void onClick(View view, int position) {
        switch (view.getId()) {
            case R.id.relBody:
//                mainActivity.replaceFragment(ProfileFragment.newInstance(videoViewsModelArrayList.get(position).user_id,
//                        videoViewsModelArrayList.get(position).name), true, R.id.mainContainer);
                break;
            default:
                break;
        }
    }

    @Override
    public void onLongClick(View view, int position) {

    }
}