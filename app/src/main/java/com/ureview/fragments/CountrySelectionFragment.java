package com.ureview.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.ureview.R;
import com.ureview.adapters.CountrySelectionAdapter;
import com.ureview.listeners.IClickListener;
import com.ureview.models.CountriesModel;
import com.ureview.utils.Constants;

import java.util.ArrayList;

public class CountrySelectionFragment extends DialogFragment implements View.OnClickListener, IClickListener {

    private View rootView;
    private String countryCode;
    private ImageView imgBack, imgClear;
    private RecyclerView recyclerView;
    private EditText edtSearch;
    private ArrayList<CountriesModel> searchList, completeCountriesList;
    private CountrySelectionAdapter countrySelectionAdapter;
    private String[] arrContryCode;
    private int selectedCountryPosition = 0;

    public CountrySelectionFragment() {

    }

    public static CountrySelectionFragment newInstance(String countryCode) {
        CountrySelectionFragment fragment = new CountrySelectionFragment();
        Bundle args = new Bundle();
        args.putString(Constants.COUNTRY_CODE, countryCode);
        fragment.setArguments(args);
        return fragment;
    }

    public static CountrySelectionFragment newInstance(String countryCode, boolean isFromForgotPassword) {
        CountrySelectionFragment fragment = new CountrySelectionFragment();
        Bundle args = new Bundle();
        args.putString(Constants.COUNTRY_CODE, countryCode);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            if (getArguments().containsKey(Constants.COUNTRY_CODE))
                countryCode = getArguments().getString(Constants.COUNTRY_CODE);
        }
        searchList = new ArrayList<>();
        completeCountriesList = new ArrayList<>();
        arrContryCode = getResources().getStringArray(R.array.DialingCountryCode);
        if (!TextUtils.isEmpty(countryCode)) {
            for (int i = 0; i < arrContryCode.length; i++) {
                String completeText = arrContryCode[i];
                String[] parts = completeText.split(",");
                if (countryCode.equalsIgnoreCase(parts[0])) {
                    selectedCountryPosition = i;
                }
                searchList.add(new CountriesModel(completeText));
            }
            completeCountriesList.addAll(searchList);
        } else {
            for (String completeText : arrContryCode) {
                searchList.add(new CountriesModel(completeText));
            }
            selectedCountryPosition = 0;
            completeCountriesList.addAll(searchList);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_countries_selection, container, false);
        initComponents();
        return rootView;
    }

    private void initComponents() {
        setReferences();
        setListeners();
        setRecyclerViewAdapter();
        if (!TextUtils.isEmpty(countryCode)) {
            getSelectedPosition();
        }
    }

    private void setReferences() {
        edtSearch = rootView.findViewById(R.id.edtSearch);
        imgClear = rootView.findViewById(R.id.imgClear);
        imgBack = rootView.findViewById(R.id.imgBack);
        recyclerView = rootView.findViewById(R.id.recyclerView);
    }

    private void setListeners() {
        addTextWatcher();
        imgClear.setOnClickListener(this);
        imgBack.setOnClickListener(this);
    }

    private void getSelectedPosition() {
        recyclerView.scrollToPosition(selectedCountryPosition);
    }

    private void addTextWatcher() {
        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!TextUtils.isEmpty(charSequence.toString().trim())) {
                    makeSearch(charSequence.toString());
                } else {
                    searchList.clear();
                    searchList.addAll(completeCountriesList);
                    countrySelectionAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void setRecyclerViewAdapter() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        countrySelectionAdapter = new CountrySelectionAdapter(searchList, this);
        recyclerView.setAdapter(countrySelectionAdapter);
    }

    private void makeSearch(String text) {
        searchList.clear();

        for (String d : arrContryCode) {
            if (d.contains(text)) {
                searchList.add(new CountriesModel(d));
            }
        }
        countrySelectionAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imgClear:
                edtSearch.setText("");
                break;
            case R.id.imgBack:
                getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_CANCELED, getActivity().getIntent());
                CountrySelectionFragment.this.dismiss();
                break;
            default:
                break;
        }
    }

    @Override
    public void onClick(View view, int position) {
        Intent intent = getActivity().getIntent();
        CountriesModel countriesModel=searchList.get(position);
        intent.putExtra("countriesModel", countriesModel);
        getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);
        CountrySelectionFragment.this.dismiss();
    }

    @Override
    public void onLongClick(View view, int position) {

    }

}
